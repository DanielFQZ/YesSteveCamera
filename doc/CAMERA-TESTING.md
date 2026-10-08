> test.10新增第三人称任意方向疾跑：A/S/D+疾跑可直接起跑；第一人称仍使用原版规则。

> test.9新增锁定相对移动与意图式疾跑：W/S/A/D以目标为参照；朝目标疾跑保持锁定构图，后退/转身疾跑释放。详细步骤见P3-DUEL-CAMERA.md。

> 当前test.8开始测试P3a双目标构图，详细步骤与临时开关见 [P3-DUEL-CAMERA.md](P3-DUEL-CAMERA.md)。test.7朝向/疾跑行为用户已确认通过；YSS保持camera-assist-test.2。

> test.7疾跑复测：持剑R锁定，行走时保持面朝目标；进入疾跑，转动镜头向目标相反方向跑，确认角色能转身且锁定标记保留；松开/结束疾跑恢复行走，应自动转回同一目标，无需重新按R。分别测试按键疾跑和双击前进，攻击中疾跑后恢复，及Root接触停步回归。

> test.6距离复测：拿剑R锁定目标后分别退到6格外、24格外，保持视线，播放Root攻击，确认仍纠正朝向；目标卸载/死亡应清理。接触后停步仍需回归。配套YSS保持camera-assist-test.2。

> 当前攻击辅助已更新为test.5持剑朝向纠正；Root接触保护需配套YSS test.2，验收见 [P2-ATTACK-ASSIST.md](P2-ATTACK-ASSIST.md)。旧“自动接近”步骤已废止。

# YesSteveCamera 首轮客户端测试

## 安装与范围

用 YesSteveCamera 替换 Shoulder Surfing，不能同时启用。测试版本 `4.21.0-ysc.0.1.0-test.5` 保留 `shouldersurfing` modId 与原有越肩设置。客户端必须完整重启。

当前实现：手动选敌/切换/取消、屏幕标记、镜头震动、YSM 动画指令帧震动，以及 [P2a YSS攻击朝向和短距离接近](P2-ATTACK-ASSIST.md)。test.3 需要配套更新YSS才能启用攻击辅助；不直接替换YSS伤害目标。双目标构图、权限MoLang与YSS真实命中震动仍未实现。

## 锁定

1. 切到原有越肩视角，面向附近生物。
2. 默认短按 **R** 选择/切换目标，长按约0.45秒取消。按键可以在“控制 → YesSteveCamera”重绑定；若 R 已被技能使用，请换键。本版独立按键不复用原版中键选取方块。原越肩视角按键与锁定按键统一列在 YesSteveCamera 分类，既有按键绑定保持不变。
3. 目标身上显示金色括号，屏幕中心下方显示目标名称。屏幕外显示文字提示。标记使用 HUD，遮挡宽限期间仍会显示。
4. 死亡、超距、持续遮挡、切换视角或打开其他 GUI 会取消；聊天框保留锁定，暂停操作。退出世界/切维度清理目标。

`config/yesstevecamera/targeting.json` 可配置范围（默认24）、锥体半角（70度）、长按 tick（9）、遮挡宽限（20），以及实体 ID 分类覆盖。队友、自身、自己的宠物始终受保护；其他玩家默认保护，未知生物允许手动锁定。

`face_target` 默认 false：首轮不抢夺 YSS 或原越肩视角的角色朝向。设置为 true 可试验身体朝向目标，但尚未与 YSS 技能朝向完成仲裁，不建议开启。

## 命令与预设

```text
/yesstevecamera status
/yesstevecamera reload
/yesstevecamera shake play yesstevecamera:light_hit
/yesstevecamera shake play yesstevecamera:light_hit 0.5
/yesstevecamera shake stop
/yesstevecamera shake stop_all
/yesstevecamera target cancel
```

`status` 显示目标、预设数量和 YSM 桥接状态。play 的预设名支持补全。stop 只停止命令使用的 `command` 槽；stop_all 清除正在播放的所有震动。强度范围0–10，建议先用0.5–1。

启动或 reload 会创建 `config/yesstevecamera/shakes/`。可把文档中的 `CAMERA-SHAKE-EXAMPLE.json` 复制到此目录，再 reload。`yesstevecamera:light_hit` 为内置的极小命中预设，`yesstevecamera:heavy_hit` 保留较明显的重击幅度。duration 为秒，最多60秒，每预设最多64轨；相同文件夹中的重复 ID 拒绝加载。某个文件错误会报告路径，保留上次有效配置；成功 reload 清除旧震动与待执行动画请求。

轨道支持 translation、rotation、fov、distance。translation 的 axis 对应镜头侧向/上向/后退，distance 正值向后退。平移执行基本射线避墙，仍需实测墙角和光影下的表现。最多32个并行槽；合成位移/旋转/FOV 有上限。每个轨道的 envelope 决定轨道寿命，预设 duration 是总寿命上限。

## YSM 动画指令帧

Blockbench 动画的“动画效果 → 指令”填入：

```text
ctrl.camera_shake('yesstevecamera:light_hit', 'attack', 1.0);
```

在另一个时机主动停止同一槽：

```text
ctrl.camera_shake_stop('attack');
```

这是3参数与1参数函数。第二个参数是播放槽：同名替换，不同名叠加，例如连段使用 `attack_1`、`attack_2`、`attack_3` 可并行。动画结束/切换不会自动终止已经播放的震动，预设自然结束；切世界、死亡和成功 reload 会清理。

只接收本地真实玩家允许产生副作用的动画求值，远端玩家和模型预览不控制本地镜头；异步动画请求进入有界队列，客户端 tick 执行。返回1表示已进入队列，不代表预设存在或确认命中。命中震动仍需后续 YSS 确认事件桥。

先执行 status，确认 `ctrl.camera_shake / ctrl.camera_shake_stop ready`。若 unavailable，请查看 latest.log 中 `YesSteveCamera YSM` / `YSM bridge unavailable`，不要把链接失败误当作动画帧未触发。

## 验收顺序

- 完整重启，确认模组列表仅一份 Camera/Shoulder Surfing，越肩视角和移动保持正常。
- 用两个生物测试短按选择/切换、长按取消、遮挡/死亡与跨世界清理。
- 执行 light_hit 命令，检查旋转和平移；再测试 YSM 本地动画指令帧。
- 在30/60/高帧率以及光影开关下比较；目标标记应跟随插值位置，震动不应改变候选排序。
- 放入格式错误预设后 reload，确认报错并且原有 light_hit 仍可播放；修正后重载。

## 本地构建

Java17 / Gradle8.7，示例（PowerShell）：

```powershell
.\gradlew.bat :forge:test :forge:jar '-PysmJar=绝对路径\ysm-dev.jar'
```

不传 ysmJar 只构建核心，没有动画指令帧桥；发布测试包必须传入与客户端对应的 YSM 开发 JAR。产物在 `forge/build/libs`，使用不带 sources/api 分类的完整 Forge JAR。
## P5 动作全景、镜头缓冲与滚轮调距

YSM 动画指令帧可调用：

```text
ctrl.camera_preset('yesstevecamera:action_overview', 2.8);
ctrl.camera_preset_stop();
```

`camera_preset` 的第二个参数是最长持续时间（秒）。预设默认创建在
`config/yesstevecamera/overviews/action_overview.json`，动作开始时记录当前玩家和锁定目标的取景区域，期间镜头保持该区域，超时或调用 stop 后平滑恢复。动作预设不持续追踪高速移动的玩家，适合飞向目标再返回的技能。

`config/yesstevecamera/motion.json` 控制普通镜头的启动/停止响应和滚轮调距：

- `startSeconds` / `stopSeconds` / `maxLag`：空中、root 位移使用的跟随响应和最大滞后；
- `groundStartSeconds` / `groundStopSeconds` / `groundMaxLag`：地面行走和奔跑使用的独立参数，默认更紧跟；
- `wheelZoom` / `wheelStep`：是否允许第三人称滚轮调距及每格步长。

第三人称且没有打开界面时，滚轮向上拉近、向下拉远；距离上限为 `max_offset_z`，测试版默认扩大到 20 格。墙体碰撞仍会立即把镜头收回，离开墙体时再缓慢恢复。
