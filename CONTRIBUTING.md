# Contributing

感谢你关注本项目。

## 提交 Issue

- 先确认是否已有相同问题
- 提供清晰的复现步骤、预期结果和实际结果
- 如涉及接口问题，尽量附带请求参数、返回结果和关键日志

## 提交 PR

- 一个 PR 尽量只解决一类问题
- 不要顺手改动无关模块
- 涉及配置、接口或启动流程变化时，请同步更新文档
- 提交前请确保基本构建和启动流程可验证

## 分支命名建议

- `feat/xxx`
- `fix/xxx`
- `docs/xxx`
- `refactor/xxx`
- `chore/xxx`

## Commit Message 建议

推荐使用简洁一致的提交信息：

- `feat: add warehouse order flow documentation`
- `docs: improve open source onboarding`
- `chore: update repository gitignore`

## 代码协作约定

- 不随意重命名核心模块
- 不破坏 Maven 多模块结构
- 不修改与当前目标无关的业务逻辑
- 文档优先使用中文，必要时可补充英文标题
