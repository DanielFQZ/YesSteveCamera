# P5 动作全景与运动缓冲（2026-10-07）

目标：高速往返技能不再让镜头追着玩家冲刺；普通跟随有启动/停止缓冲；滚轮调距，普通最大后撤扩至 20 格。

实现计划：

- 增加 `ctrl.camera_preset(id, duration_seconds)` / `ctrl.camera_preset_stop()`，有界、会话校验的本地玩家请求队列；预设为独占镜头，新请求替换旧请求，超时自动恢复，不依赖 YSM 停止事件。
- `yesstevecamera:action_overview` 记录动作开始区域，镜头位置围绕该区域按当前鼠标视角更新；不持续追逐玩家或目标。镜头距离/空中余量/进入退出时间可配置。
- 新增基于时间的临界阻尼位置跟随，分别配置移动与停止时的响应时间；暂停不推进，死亡、换世界、视角切换、瞬移清理。碰撞立即收紧，不受缓冲延迟影响。
- 动作全景期间暂停决斗自动构图，结束后平滑恢复；震动及原有 YSS 显式相机轨道仍按既有顺序叠加。
- Forge 滚轮在肩部第三人称、无界面时调整普通相机后撤，消费事件避免同时切快捷栏；提供配置开关和步长。在决斗锁定中保留用户调距偏好。
- 不修改 YSS、YSM 和用户动画文件。先通过数学/命令/配置测试和构建，再部署客户端供用户实际测试。

本轮不保证任意动画全程可见：动画未提供轨迹边界，依靠起始位置、目标位置和预设余量估计；墙体、巨大目标、超出距离上限的动作仍可能遮挡或出画。

## test.13 使用方法

在起飞前的动画指令帧添加（例如持续 2.8 秒）：

```text
ctrl.camera_preset('yesstevecamera:action_overview', 2.8);
```

持续时间包含拉远过程，到时开始平滑恢复。可以在角色回归的指令帧提前结束：

```text
ctrl.camera_preset_stop();
```

不需要 slot。镜头预设同一时刻只有一个，新调用从当前镜头位置切换；动画中断后即使没执行结束帧，也会按时恢复。只响应本地玩家，第一人称/预览模型不触发。动作取景期间鼠标仍可转动，镜头位置围绕动作开始区域跟随视角；切第一人称直接解除。建议在位移前约 0.3～0.5 秒调用，让拉远发生在冲刺之前。

独立验证命令：

```text
/yesstevecamera preset play yesstevecamera:action_overview 3
/yesstevecamera preset stop
/yesstevecamera status
/yesstevecamera reload
```

客户端 `config/yesstevecamera/overviews/action_overview.json`：`minDistance=8`、`maxDistance=24`、`distance=14`、`horizontalMargin=3`、`verticalMargin=6`、`enterSeconds=0.45`、`exitSeconds=0.65`。距离单位为格，过渡时间单位为秒。`distance` 大于 0 时使用手动全景距离，设为 0 才使用自动构图；手动距离允许到 64 格，仍会受到墙体碰撞收回。复制 JSON、修改 id 即可增加不同技能预设，reload 后可补全。动作全景期间鼠标仍由普通镜头处理，镜头位置围绕动作开始时的区域更新，因此可以转动观察而不会锁死视角。

`config/yesstevecamera/motion.json`：`enabled=true`，空中/root 位移使用 `startSeconds=0.22`、`stopSeconds=0.35`、`maxLag=8`；地面行走和奔跑单独使用 `groundStartSeconds=0.06`、`groundStopSeconds=0.10`、`groundMaxLag=1.25`。`teleportDistance=64`，滚轮开关 `wheelZoom=true`，步长 `wheelStep=0.5`。响应时间是阻尼参数，不是等候这么久才开始移动；调大更柔和，调小更紧跟。地面参数调小可以避免普通移动时镜头明显落后，空中参数则保留动作/root 位移的缓冲。

滚轮向上拉近、向下拉远，第三人称下消费滚轮，不同时切快捷栏；关闭 `wheelZoom` 可恢复快捷栏用途。普通镜头距离使用 `offset_z`，默认最大后撤距离为 20 格；已有客户端的 `shouldersurfing-client.toml` 不会自动覆盖，请将 `max_offset_z` 手动改为 `20.0`。动作全景不是固定在世界坐标：它会把动作开始时的取景区域随同一位置弹簧平滑移动，同时保留视角旋转和全景距离。跟随参数均在 `config/yesstevecamera/motion.json` 中调整。

待实机验收：平地高速往返/停步、起飞前触发全景、自然超时/提前结束/连续触发、墙边出入、切第一人称/换世界、锁定构图恢复、普通与锁定视角滚轮调距。构建和数学测试不能替代游戏内手感验收。

## 构建与部署

2026-10-07：`:forge:test :forge:build`（Java 17，包含客户端 YSM JAR 的可选桥接编译）通过，全部 50 项测试通过，其中覆盖缓冲、帧率差异、暂停/重置、地面参数、旧 motion 配置兼容、预设生命周期、配置校验和命令解析。JAR 内已核对预设桥接、motion 类、refmap 和语言 JSON。保留了原有 Mixin 兼容性编译警告。

test.14 已替换客户端旧 Camera JAR，备份位于 `build/client-backups/camera-test14-20261007-192245`；YSS、YSM、VFX JAR 及模型动画不改。替换后需要完整重启 Minecraft 才能加载新 Camera。
