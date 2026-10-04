# YesSteveCamera 开发文档

- [P3a 双目标构图](P3-DUEL-CAMERA.md)：test.8的新功能、配置与验收步骤。
- [P2 朝向与Root接触保护](P2-ATTACK-ASSIST.md)：test.5–7已由用户验证的行为。
- [当前开发记录](IMPLEMENTATION-2026-10-04.md)：版本交付与验证记录。
- [客户端测试与 MoLang 用法](CAMERA-TESTING.md)。
- [总体开发方案](DEVELOPMENT-PLAN.md)：P0–P6路线；历史吸附设计以最新P2行为为准。
- [震动 JSON 示例](CAMERA-SHAKE-EXAMPLE.json)。
- [YSS 历史审计](YSS-AUDIT-2026-09-29.md)：当前公开事件以YesSteveSkill-Daniel源码为准。

工作分支dev/1.20.1，产品为YesSteveCamera，保留shouldersurfing modId及原版API/配置。客户端不能同时启用原版Shoulder Surfing。

当前交付Forge 1.20.1。Camera不附加吸附位移，YSS负责Root，Camera做朝向纠正及接触裁剪；疾跑释放朝向，恢复行走后继续纠正。test.8增加双目标基础构图，test.9增加锁定相对移动及按意图释放疾跑，test.10补齐第三人称A/S/D任意方向起跑；真实画面仍待用户验收。权限MoLang、Caster Move进一步仲裁、命中震动、目标VFX联动仍待后续；YSS公开事件尚未提交上游PR。
