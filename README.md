# N0BAD 外卖（苍穹外卖学习项目）

> 基于黑马程序员"苍穹外卖"课程实现的外卖点餐系统（Spring Boot + 微信小程序）。

## 项目简介

一个完整的外卖点餐平台，包含**管理端**（Web）和**用户端**（微信小程序）：

- 管理端：员工登录、分类/菜品/套餐管理、订单管理、数据统计（营业额/用户/订单/销量）、工作台、报表导出
- 用户端：微信登录、浏览菜品、购物车、下单、历史订单、地址簿、支付（开发模式绕过）

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Java 8、Spring Boot、MyBatis、Spring Cache、JWT、Knife4j/Swagger |
| 中间件 | MySQL、Redis |
| 前端 | 管理端 Vue（编译产物）、用户端 uni-app 微信小程序（编译产物） |
| 部署 | nginx 反向代理 |

## 快速启动

1. 启动 MySQL（导入 `sky_take_out.sql`）和 Redis
2. 配置 `sky-server/src/main/resources/application-dev.yml`（数据库、Redis、微信、OSS）
3. 启动 `SkyApplication`
4. 管理端访问 `http://localhost/`，默认账号 `admin / 123456`
5. 小程序用微信开发者工具打开 `mp-weixin` 工程，勾选"不校验合法域名"

## 项目结构

```text
sky-common  # 公共模块：工具类、常量、异常、属性类
sky-pojo   # 实体、DTO、VO
sky-server # 后端服务：controller / service / mapper / config
```

## 待办 / 上线前清单

- [ ] 还原微信登录（真实 appid/secret，当前为 mock openid）
- [ ] 还原微信支付（当前开发模式直接标记已支付）
- [ ] 阿里云 OSS 换成自己的桶
- [ ] 数据库、Redis 生产配置与密码
- [ ] HTTPS 域名 + 小程序合法域名/IP 白名单
- [ ] 参数校验已加（@Valid），建议继续补全

## 说明

- 本项目为学习用途，前后端均使用课程编译产物，品牌已替换为 N0BAD
- 关键修改（绕过登录/支付）在代码中以 TODO 注释标记

