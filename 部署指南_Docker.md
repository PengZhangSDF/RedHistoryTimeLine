## 使用 Docker 部署“抗日战争时间轴”项目（Windows / Linux 通用）

本文档说明如何把本项目整体（前端 + 后端 + MySQL + 初始数据）搬到另一台电脑上运行，无论是 **Windows** 还是 **Linux**。

> 前提：你已经在当前机器上把项目跑通，且项目目录结构保持为：
> `red-history-timeline-frontend/`、`red-history-timeline-backend/`、`docker-compose.yml` 等都在同一根目录下。

---

## 一、打包与拷贝项目代码

1. 在原电脑上找到项目根目录，例如：
   - Windows：`E:\Red-History-TimeLine`
   - Linux：`/home/xxx/Red-History-TimeLine`

2. 将整个目录打包为压缩包（推荐 zip 或 tar.gz）：

   - **Windows（PowerShell）示例**：

     ```powershell
     cd E:\Red-History-TimeLine
     Compress-Archive -Path . -DestinationPath ..\Red-History-TimeLine.zip
     ```

   - **Linux 示例**：

     ```bash
     cd /home/xxx/Red-History-TimeLine
     tar czf ../Red-History-TimeLine.tar.gz .
     ```

3. 将压缩包拷贝到目标电脑（U 盘 / 网盘 / SCP / Git 等均可）。

4. 在目标电脑上解压到一个目录，例如：
   - Windows：`D:\Red-History-TimeLine`
   - Linux：`/opt/Red-History-TimeLine`

> 注意：整个项目目录结构必须保持不变，`docker-compose.yml` 一定要在根目录。

---

## 二、目标电脑上的准备工作

### 1. 安装 Docker（Windows）

1. 打开浏览器访问 Docker 官网，下载安装 **Docker Desktop for Windows**：
   - https://www.docker.com/products/docker-desktop/
2. 安装完成后重启电脑（如提示）。
3. 启动 Docker Desktop，等左下角显示 “Docker Engine running”。

### 2. 安装 Docker（Linux）

以 Ubuntu 为例（其他发行版命令略有不同，参考官方文档）：

```bash
sudo apt update
sudo apt install -y docker.io

# 启动并设置开机自启
sudo systemctl enable --now docker

#（可选）把当前用户加入 docker 组，避免每次都 sudo
sudo usermod -aG docker $USER
# 重新登录终端后生效
```

### 3. 安装 Docker Compose（如未内置）

新版本的 Docker Desktop / Docker Engine 一般已经内置 **docker compose** 子命令，可以直接用：

```bash
docker compose version
```

若命令存在并输出版本号，则无需额外安装。

---

## 三、在目标电脑上启动整个项目

以下操作在 **目标电脑** 的项目根目录执行（即 `docker-compose.yml` 所在目录）。

### 1. 进入项目目录

- Windows（PowerShell）：

  ```powershell
  cd D:\Red-History-TimeLine
  ```

- Linux：

  ```bash
  cd /opt/Red-History-TimeLine
  ```

### 2. 首次启动（会自动构建镜像并初始化数据库）

执行：

```bash
docker compose build
docker compose up -d
```

说明：

- `build`：构建前端镜像（Vue3 + Vite + Nginx）和后端镜像（Spring Boot）。
- `up -d`：后台启动三个容器：
  - `red_history_db`：MySQL 8.0，库名 `red_history`；
  - `red_history_backend`：后端 API，端口 3001；
  - `red_history_frontend`：前端 Web，端口 3000。

> 数据库容器第一次启动时，会自动执行以下脚本：
>
> - `scripts/init-db.sql`：创建数据库和所有表（默认字符集 utf8mb4）；
> - `scripts/import-data-from-json.sql`：导入事件 / 人物 / 地点 / 事件-人物关联（使用 `SET NAMES utf8mb4`，防止中文乱码）；
> - `scripts/import-media-bilibili-9.sql`：从 `data/vedio.txt` 映射的 19 条 B 站视频，写入 `media` 表；
> - `scripts/import-materials-from-md.sql`：从 `data/相关资料.md` 映射的权威网址，写入 `materials` 表。

### 3. 访问前端与后端

- **前端页面（时间轴网站入口）**：

  在浏览器输入：

  ```text
  http://localhost:3000/
  ```

- **后端接口（可选验证）**：

  ```text
  http://localhost:3001/api/events
  ```

  若能看到 JSON，其中 `code: 200`，表示后端 + 数据库连接正常。

---

## 四、端口占用与调整

默认端口映射在 `docker-compose.yml` 中如下：

- 前端：`3000:80`（宿主机 3000 → 容器 Nginx 80）
- 后端：`3001:3001`（宿主机 3001 → 容器 Spring Boot 3001）
- 数据库：`3307:3306`（宿主机 3307 → 容器 MySQL 3306）

如果目标电脑某个端口已被占用，可以修改左侧宿主机端口，例如将前端改为 8080：

```yaml
frontend:
  ports:
    - "8080:80"
```

修改完后需要重新：

```bash
docker compose down
docker compose up -d
```

---

## 五、如何停止 / 重启 / 清空数据

在项目根目录执行：

### 1. 停止所有容器（保留数据）

```bash
docker compose down
```

之后可以再次启动：

```bash
docker compose up -d
```

### 2. 停止并删除容器 + 删除数据库数据

如果你希望在目标电脑上 **重置数据库**（重新用脚本导入所有数据）：

```bash
docker compose down -v
docker compose up -d
```

`-v` 会删除 `db_data` 卷，下次启动时 MySQL 会重新执行 `init-db.sql` 与导入脚本，保证表结构与中文数据都用 utf8mb4 正确写入。

---

## 六、Linux 环境注意事项

1. 若 `docker compose` 命令提示权限不足，请在前面加 `sudo`：

   ```bash
   sudo docker compose up -d
   ```

2. 若你想非 root 用户使用 docker，请将该用户加入 docker 组后重新登录：

   ```bash
   sudo usermod -aG docker $USER
   ```

3. 某些发行版可能使用 `docker-compose`（有连字符）命令而非子命令形式，具体以系统安装提示为准，但本项目推荐使用新版本 `docker compose`。

---

## 七、常见问题简要说明

- **网页能打开但“获取事件列表失败”**  
  多数是后端连不上数据库或数据库为空，检查：
  - `docker compose ps` 看 `red_history_backend` 和 `red_history_db` 是否都是 `Up`；
  - 用 `docker logs red_history_backend` 查看错误是否为数据库连接；
  - 若需要重置数据库，参考上文 “停止并删除容器 + 删除数据库数据” 然后重新 `up -d`。

- **中文乱码**  
  只要使用当前版本的 SQL 脚本（内部已加 `SET NAMES utf8mb4`），并按本文重新初始化（`docker compose down -v` 后 `up -d`），导入的数据将使用 utf8mb4，不会再乱码。若仍有乱码，多半是目标电脑旧数据残留没有清除干净。

---

通过以上步骤，你可以将当前完整的“抗日战争时间轴”项目连同数据库一同打包，并在任何安装了 Docker 的 Windows 或 Linux 机器上快速运行。+
