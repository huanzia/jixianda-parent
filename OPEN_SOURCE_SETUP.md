# 开源前配置说明

本仓库已将敏感配置拆分为“可提交示例文件 + 本地私有文件”模式。

后端配置：
- 根目录使用 `.env.example` 作为 `docker-compose.yml` 的环境变量示例。
- 网关私有配置示例见 `jixianda-gateway/src/main/resources/application-private.example.yml`。
- 核心服务私有配置示例见 `jixianda-server/src/main/resources/application-dev-private.example.yml`。
- 用户服务配置示例见 `jixianda-user/src/main/resources/application.yml.example` 和 `jixianda-user/src/main/resources/application-private.example.yml`。

前端与小程序：
- 管理端使用 `project-sky-admin-vue-ts/.env.example`，小程序使用 `project-rjwm-weixin-uniapp-develop-wsy/.env.example`。
- 管理端、小程序的 `.env` 文件只保留非敏感本地地址，私有变量不要提交。
- 微信小程序 `appid` 已改为占位符。公开仓库请保留占位符版本，本地联调时自行改回并保持私有。

本地运行建议：
1. 复制示例文件并填入你自己的数据库、JWT、微信、对象存储、支付等私有配置。
2. 私有配置文件如 `.env`、`application-private.yml`、`application-dev-private.yml` 不要提交。
3. 证书、私钥、支付密钥请通过本地文件路径或环境变量注入，不要放入仓库。
