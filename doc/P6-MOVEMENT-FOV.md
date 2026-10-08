# P6 移动视场控制（test.17）

YSS 按行走/疾跑状态修改 MOVEMENT_SPEED；原版用 `(当前速度属性 / abilities.walkingSpeed + 1) / 2` 计算移速视场倍率。速度配置差距大时，视场会剧烈变化。

Camera 仅替换 AbstractClientPlayer.getFieldOfViewModifier 中用于视场计算的速度读取，不修改玩家属性，不依赖 YSS。行走保持正常视场，疾跑可固定小幅扩张。药水、装备及其他模组增加的移速同样不再影响这一倍率。

## 配置

启动后自动创建 `config/yesstevecamera/fov.json`：

```json
{
  "mode": "SPRINT",
  "sprintMultiplier": 1.08,
  "enterSeconds": 0.2,
  "exitSeconds": 0.25,
  "shoulderOnly": true
}
```

- `mode`：`SPRINT` 固定疾跑扩张；`STABLE` 移动视场恒定；`VANILLA` 恢复原版移速视场。
- `sprintMultiplier`：1～1.3，默认 1.08；设为 1 就不扩张。与模型的速度配置无关。
- `enterSeconds` / `exitSeconds`：移动倍率增加/减小时的过渡时间，0～2 秒；0 表示立即改变目标值。原版 GameRenderer 的常规视场插值仍保留，实际画面会稍晚收敛，不影响 Camera 震动的响应。
- `shoulderOnly`：默认只作用于 Camera 肩部第三人称；false 同时作用于本地玩家的其他视角。

修改后执行 `/yesstevecamera reload`。`/yesstevecamera status` 显示当前 FOV 模式。配置错误会报错并保留上次成功加载的配置。

原版“视场效果”强度继续生效。默认配置下，基础 FOV 70、效果强度最大、没有其他效果时，疾跑 FOV 约 75.6。Camera 原有基础 FOV 覆盖仍可使用。拉弓、望远镜、飞行的独立倍率及后续水下、死亡和震动处理保持原路径，不把整个最终 FOV 写死。其他模组若在后续主动覆盖 FOV，仍可影响最终画面。

## 状态和兼容边界

过渡状态归属于每个玩家实例；复活/重连使用新状态，时间回退会重置。重复求值或游戏暂停不推进过渡。退出作用范围/切回 VANILLA 时，在退出时间内交回实时原版倍率；之后不干预原版变化。切回第一人称后，原版的大幅移速视场仍可能恢复，这是默认作用范围的含义；若需全视角稳定，设置 shoulderOnly=false。

不改变相机位置缓冲、滚轮距离、决斗和动作全景算法；默认只小幅扩大视场，不收窄其取景范围。最小化注入范围，不增加整段 FOV 方法覆盖。速度读取使用反算值传给原版公式，真实移动属性及服务端同步保持不变。

## 验证

自动测试覆盖极端移速、三种模式、进入/退出时间、暂停/重复求值、更新频率差异、疾跑反复切换、作用范围交接、状态重置、零基础速度和配置校验。

实机需验证：悬殊行走/疾跑倍率的模型、任意方向疾跑、速度/缓慢药水、第一/第三人称切换、拉弓/望远镜、命中震动、决斗和动作全景。构建及自动测试不能替代手感验收。

## 构建与部署记录

2026-10-08：Java 17 离线执行 `:forge:test :forge:build`，传入测试客户端 YSM JAR 编译可选桥接，59 项测试全部通过（本次新增 9 项）。构建保留原有 RenderType/Cobblemon 的 3 条 Mixin 编译警告；本次注入无新增警告。已核对发布 JAR 的 FOV 类、YSM 桥接、版本、JSON 和速度读取的 SRG refmap。

已部署 `YesSteveCamera-Forge-1.20.1-4.21.0-ysc.0.1.0-test.16.jar`，SHA256 为 `03A9D2BBACBFF656097DD0CB5626053DD175D30FD05F70E1CA7A7A3150E5FF49`。测试客户端只有此 Camera JAR 启用，test.15 已备份至 `build/client-backups/camera-test16-20261008-025041` 并禁用。客户端 fov.json 已创建默认配置。需完整重启 Minecraft，游戏内验收待用户完成。

## test.17 调整

疾跑默认倍率从 1.04 提高至 1.08；测试客户端现有 fov.json 同步为 1.08，其他配置保持原值。原版无额外效果、视场效果强度为最大时，疾跑速度乘 1.3，对应视场倍率 `(1.3 + 1) / 2 = 1.15`。8% 小于原版的 15%。

Camera 视角下任意一只手持剑、斧、三叉戟、弓或弩时隐藏准心（瞄准时也隐藏）；空手、普通物品或退出 Camera 视角恢复原有准心规则。锁定目标标记仍显示。隐藏的是原版整个准心绘制，包括设在准心位置的攻击冷却指示；快捷栏攻击指示不受此规则影响。

在 `config/shouldersurfing-client.toml` 的 `[crosshair]` 下设置 `hide_with_weapon = false` 可关闭此功能。武器识别支持上述原版物品类型的子类、minecraft:swords / minecraft:axes 标签，以及自定义 `yesstevecamera:weapons` 物品标签。可通过数据包 `data/yesstevecamera/tags/items/weapons.json` 扩充其他模组武器；这不会更改攻击辅助的识别规则。

2026-10-08：test.17 构建及 59 项回归测试通过，已核对打包语言 JSON、版本及 YSM 桥接。部署 SHA256：`F3402312E5142B7086D81B44A46036B6B7E759EFD0E9AF04A949C8985A65022D`；旧 JAR 和配置备份在 `build/client-backups/camera-test17-20261008-031957`。需完整重启，实机重点检查双手武器/空手切换、第一人称、瞄准和锁定标记。
