# 极鲜达 Fresh-Link

极鲜达（Fresh-Link）是一个面向“多仓即时零售”场景的多模块项目，包含用户端小程序、管理端前端和 Java 后端服务。项目重点展示从商品浏览、购物车、下单、库存处理到支付与超时取消的一条完整业务链路。

## 1. 项目简介

本仓库围绕“多仓即时零售系统”展开，包含：

- 用户端小程序
- 管理端前端
- Java 多模块后端服务
- SQL 初始化脚本
- 本地依赖编排配置

项目目标是在尽量保持业务链路完整的前提下，呈现一个结构清晰、便于阅读和快速上手的工程化示例。

## 2. 系统架构说明

系统可以理解为三层结构：

- 用户端小程序：面向终端用户，负责最近仓选择、商品浏览、购物车、下单与支付流程。
- 管理端前端：面向运营和仓库管理人员，负责商品、订单、仓库和基础数据管理。
- Java 后端服务：负责网关接入、用户能力、商品与订单主链路、库存、支付 mock、延迟取消等核心逻辑。

基础依赖包括 MySQL、Redis、RabbitMQ、Nacos、Seata、Elasticsearch 等。

## 3. 仓库目录结构

```text
.
├── jixianda-common
├── jixianda-gateway
├── jixianda-pojo
├── jixianda-server
├── jixianda-user
├── project-rjwm-weixin-uniapp-develop-wsy
├── project-sky-admin-vue-ts
├── sql
├── docker-compose.yml
└── pom.xml
```

## 4. 模块说明

### 后端模块

- `jixianda-common`
  公共工具、常量、通用配置与基础能力封装。
- `jixianda-pojo`
  DTO、VO、Entity 以及跨模块共享的数据模型。
- `jixianda-gateway`
  系统统一入口，负责路由、跨域、鉴权和服务转发。
- `jixianda-user`
  用户相关服务，负责用户登录、地址簿、仓库选择等能力。
- `jixianda-server`
  核心业务服务，承载商品、购物车、订单、库存、支付 mock 等主链路。

### 前端模块

- `project-sky-admin-vue-ts`
  管理端前端工程。
- `project-rjwm-weixin-uniapp-develop-wsy`
  用户端小程序工程。

## 5. 技术栈

### 后端

- Java
- Spring Boot
- Spring Cloud Gateway
- Spring Cloud Alibaba Nacos / Sentinel / Seata
- MyBatis
- Redis
- RabbitMQ
- Elasticsearch
- Maven 多模块

### 前端

- Vue
- TypeScript
- Element UI
- uni-app

### 基础设施

- MySQL
- Docker Compose

## 6. 核心功能

- 多仓商品展示
- 按仓购物车聚合
- 用户下单与订单流转
- 库存预占与释放
- 支付模拟闭环
- 后台商品、仓库、订单管理

## 7. 核心亮点

- 多仓商品展示与按仓购物车聚合
- 下单链路完整可演示
- 库存预占
- Redis + Lua 原子扣减
- RabbitMQ 延迟取消未支付订单
- mock 支付闭环

## 8. 典型业务链路

### 最近仓 -> 商品展示

用户先选择或命中最近仓，再按仓库维度展示可售商品与库存信息。

### 购物车按仓聚合

购物车以仓库维度组织，避免不同仓商品直接混单，贴近即时零售履约场景。

### 下单 -> 库存预占 -> 订单创建

下单时先校验购物车和库存，再执行库存预占与订单落库，保证主链路一致性。

### mock 支付

本项目保留 mock 支付闭环，便于本地联调和演示订单状态流转。

### 超时取消 -> 库存释放

对于超时未支付订单，系统通过延迟消息触发取消，并释放已预占库存。

## 9. 快速启动

### 环境准备

- JDK 8+
- Maven 3.8+
- Node.js
- Docker / Docker Compose
- 微信开发者工具或 HBuilderX

### 启动顺序建议

1. 启动 MySQL、Redis、RabbitMQ、Nacos、Seata、Elasticsearch 等依赖
2. 启动 `jixianda-user`
3. 启动 `jixianda-server`
4. 启动 `jixianda-gateway`
5. 启动管理端前端
6. 启动用户端小程序

## 10. 本地开发说明

- 后端采用 Maven 多模块结构，建议从父工程统一管理依赖和构建。
- 管理端与小程序端分别独立运行，按各自工程安装依赖并启动。
- 如需本地联调，请先准备数据库、中间件与必要配置。

## 11. 数据库初始化说明

- `sql/` 目录下存放数据库初始化脚本。
- 初始化数据库前，请先确认本地数据库版本、字符集与端口配置。
- 若接入 Nacos / Seata 等组件，请同时准备对应依赖服务和配置数据。


