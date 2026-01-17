# API接口参考文档

## 基础信息
- 基础URL: `http://localhost:3001/api`
- 数据格式: JSON
- 字符编码: UTF-8

## 统一响应格式
所有API响应统一使用以下格式：
```json
{
  "code": 200,
  "msg": "success",
  "data": {},
  "total": 0
}
```

## 接口列表

### 事件相关接口

#### GET /api/events
获取事件列表（支持筛选）

**参数：**
- startDate (可选): 开始日期 (YYYY-MM-DD)
- endDate (可选): 结束日期 (YYYY-MM-DD)
- locationId (可选): 地点ID

**响应：**
```json
{
  "code": 200,
  "msg": "success",
  "data": [Event, ...],
  "total": 11
}
```

#### GET /api/events/{id}
获取事件详情

**响应：**
```json
{
  "code": 200,
  "msg": "success",
  "data": Event
}
```

### 人物相关接口

#### GET /api/persons/{id}
获取人物详情

#### GET /api/persons/event/{eventId}
根据事件ID获取关联人物

### 地点相关接口

#### GET /api/locations
获取所有地点列表（包含坐标）

#### GET /api/locations/{id}
获取地点详情（包含高德坐标）

### 媒体相关接口

#### GET /api/media/event/{eventId}
根据事件ID获取关联媒体资源

**参数：**
- type (可选): 媒体类型 (image/video/audio)

