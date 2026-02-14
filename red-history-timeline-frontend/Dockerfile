# ======== 构建阶段 ========
FROM node:20-alpine AS build
WORKDIR /app

COPY red-history-timeline-frontend/package*.json ./
RUN npm install

COPY red-history-timeline-frontend/ .
RUN npm run build

# ======== 部署阶段（Nginx 托管静态文件） ========
FROM nginx:1.27-alpine
WORKDIR /usr/share/nginx/html

# 拷贝前端构建结果
COPY --from=build /app/dist /usr/share/nginx/html

# 使用自定义 Nginx 配置，将 /api 代理到后端
COPY red-history-timeline-frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

