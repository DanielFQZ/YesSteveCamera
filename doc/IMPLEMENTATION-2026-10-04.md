# Camera 开发续作与部署计划（2026-10-04）

## 当前事实

基线为 dev/1.20.1 的 d101ccfe。已有基础锁定、不可变目标快照、震动预设与调试命令；尚无 YSM 指令帧桥、锁定标记、YSS 位移仲裁。客户端仍使用原版 Shoulder Surfing 4.21.0。

## 本轮交付

1. 独立可重绑定的锁定按键、目标屏幕标记和名称提示；清理退出世界、切维度、切视角后的目标状态。
2. 捕获震动前的实际 Camera 位姿用于选敌，避免显示震动反向干扰选敌。
3. 震动重载报告具体文件错误，失败保留旧预设；补齐目录创建、世界切换清理、距离轨道和数量约束。
4. 可选 YSM CtrlBinding：`ctrl.camera_shake('yesstevecamera:light_hit', 'attack', 1.0);`、`ctrl.camera_shake_stop('attack');`。仅本地真实玩家；allowEmitting 检查；有界主线程队列；会话失效。震动自然结束，不随动画切换中断。
5. 构建 Forge 1.20.1 测试包，验证纯逻辑与构建产物。备份到仓库 build/client-backups，禁用客户端原版 Shoulder Surfing，再放入 Camera JAR。保留 shouldersurfing modId 和 API 兼容；不能同时启用二者。
6. 更新使用说明与实际验证记录，游戏内视觉/手感由用户重启客户端后验收。

## 后续顺序

- P2：YSS 攻击目标入口和动作/位移仲裁；自动选敌、近距离接近、停止滞后和障碍/悬崖防护。保留重力、击退、Root Motion、Caster Move。
- 普通移动解除权限与强制闪避打断分开。`assist_cancelable(1)` 只授予解除权限，不立即终止；具体函数待 P2 实现。
- P3：玩家与敌人双目标构图、平滑、碰撞；统一 YSS 镜头轨道和 Camera 震动的叠加阶段。
- 命中震动接收服务端确认的 YSS 命中消息；单机事件不等同于专用服务器客户端桥。
- VFX 先实现目标坐标/挂载，再方向发射/追踪。owner 始终是施法者；释放时固定目标，不随切换锁定改目标；视觉碰撞不直接伤害。
- 发布前处理原版 CurseForge/Modrinth 发布目的地、产品信息与验证矩阵；本轮只本地构建部署，不发布正式版。

## 边界与验收

YSM 管动画指令，Camera 管镜头/选敌，YSS 管攻击和技能位移，VFX 管表现。不能为了让第一轮可运行而直接接管 YSS 私有状态。按键不复用原版选取方块输入。需要检查锁定切换/长按取消、死亡/遮挡、GUI/视角/世界切换、同 slot 替换/不同 slot 叠加、坏配置保留、YSM 本地/远端/预览边界。

构建成功、自动测试通过和游戏实测分别记录，不混称为已完成。

## 本轮实际交付记录

- Forge 构建、reobfJar 和10项回归测试通过（0失败、0跳过）；日志 `build/camera-test-20261004.log`。
- 已检查产物包含 YSM bridge/checker/canary/兼容清单、中文按键资源；兼容清单编译分析无缺口，Minecraft 调用已重映射。运行时 YSM 检查与视觉仍待游戏验证。
- 原版已有的 RenderType/Cobblemon Mixin 编译警告仍存在，本轮未新增针对这些兼容路径的功能；未把这些路径称为实测通过。
- 已部署 `YesSteveCamera-Forge-1.20.1-4.21.0-ysc.0.1.0-test.1.jar`，SHA256 `9B0957127D00ABA69B6817A8D9E9DE3A88A5CA2290B90BED37298F1F171229D5`。
- 客户端原越肩 JAR 已改为 `.jar.disabled`；额外完整备份位于 `E:\JavaProject\YesSteveCamera\build\client-backups\camera-20261004-test1`。逐个解析 active JAR 的 mods 声明确认只有一份 shouldersurfing，依赖它的其他模组保持正常。
- 示例 `heavy_hit.json` 已复制到客户端 config/yesstevecamera/shakes。
- 默认锁定键 R（可改绑）；face_target 默认 false，先避免与 YSS 抢身体朝向。YSS 仲裁/吸附/决斗构图继续排期。
- 游戏实测：待用户完整重启后验收，本轮没有宣称已运行完整客户端场景。

## test.2：命令解析与按键界面修复

- 用户确认锁定正常；截图中的震动失败属于 Brigadier 参数解析错误，尚未执行震动渲染。将预设参数从 StringArgumentType.word 改为 ResourceLocationArgument.id，支持无需引号的 namespace:path，并保留强度参数与预设补全。
- 添加真实 Camera 命令树解析回归测试，覆盖内置 light_hit/heavy_hit、带目录预设与可选强度。
- 原越肩按键的分类原先硬编码为 Shoulder Surfing，现统一为 key.categories.yesstevecamera；保留原按键标识，不重置用户键位。
- 中文源码正常，但 test.1 的语言资源字节不满足 UTF-8。所有 ProcessResources 的 filteringCharset 设为 UTF-8，补齐4条进入视角的中文翻译。
- 用户客户端日志已确认 test.1 的 YSM bridge 安装成功（ctrl.camera_shake / ctrl.camera_shake_stop ready）。此信息不等于震动视觉测试通过。
- 同时保留 JSON 转义字符，修正构建展开导致英/土语言文件出现裸换行的问题。最终JAR的9个语言JSON均通过严格UTF-8和JSON解析验证。
- test.2 构建完成，12项测试全部通过；已部署客户端，旧test.1备份到 `build/client-backups/camera-20261004-test2` 并禁用。SHA256 `1941512D08BB320682C8D67DBFD677BC2C74FC43C7E1AEF813AD18D2CEA2C799`。待用户重启实测震动与按键界面。

## test.3：P2a 攻击辅助

- 用户确认test.2锁定与震动正常，继续开发P2a。实现与边界见 [P2-ATTACK-ASSIST.md](P2-ATTACK-ASSIST.md)。
- YSS fork新增目标提示查询和判定前动作窗口事件，Camera可选订阅。原版挖掘、骨骼判定、服务端伤害规则保持各自入口。
- 先完成固定动作目标、朝向、短距离有碰撞接近、停止滞后和主动取消；技能自有位移/控制状态保守让出。
- 本轮不提前声称完整P2完成；权限MoLang、细粒度技能位移协调和P3构图继续后续实现。
- Camera test.3构建、重映射及19项测试通过；YSS build通过（无JUnit套件）。已同时部署，并备份/禁用旧Camera与YSS到 `build/client-backups/camera-20261004-test3`。产物哈希及活动模组检查见该目录deployment.json。
- 已确认客户端实际启用的Camera/YSS各一份，原越肩仍禁用。两边公开事件/反射桥的游戏联动与移动手感待用户完整重启实测；尚未发送上游PR。

## test.4：辅助误取消与诊断丢失

用户反馈桥接ready但无朝向/位移效果。确认配置无Root Motion/Caster Move/Hover；源码存在BLOCK射线误取消（客户端射线400格）与聊天界面清空锁定/覆盖status的问题。已改为实际挖掘检查、聊天保留锁定但取消当前辅助，并保存动作事件计数、实际位移/转向和最后失败原因。新增3项诊断回归测试；仅更新Camera，不修改YSS或用户动作资产。
- test.4 构建、重映射与22项测试通过；已部署并核对SHA256：`6E3656FE0487631518BFFA501E271C93D419EC3619D34A59A98393626BE130D9`。旧test.3已备份至 `build/client-backups/camera-20261004-test4` 并禁用；逐个检查活动JAR声明，确认仅一份 shouldersurfing。YSS保持test.1；游戏内朝向与接近效果待用户重启复测。

## test.5：持剑朝向纠正与Root接触裁剪

用户截图确认此前最后原因为YSS root_motion。按新需求移除Camera附加接近位移，主手SwordItem或minecraft:swords默认启用朝向纠正；Root动作也可转向。YSS新增YssRootMotionEvent公开水平缩放接口，由Camera对当前目标扫掠AABB并裁剪，保留垂直运动和后退，不积累被挡住的Root距离。详见P2-ATTACK-ASSIST.md。

Camera构建/重映射和27项测试通过（含5项新增接触扫描测试）；YSS build通过（无测试套件）。Camera test.5及YSS camera-assist-test.2已部署；校验与活动mod唯一性见build/client-backups/camera-20261004-test5/deployment.json。旧JAR在该目录备份且已禁用。真实游戏朝向、碰撞手感与服务器同步待用户重启测试；未发上游PR。

## test.6：已锁定目标不按距离丢失

用户确认test.5转向与接触停步正常。将6格辅助范围和24格锁定范围限制移至初次选敌；已手动锁定目标及当前动作固定目标不再按距离清理。保留死亡/卸载/同世界/保护分类/遮挡检查；以原生ClipContext方块检测替代hasLineOfSight，避免其128格隐含上限。空闲自动选敌仍限定附近。YSS命中校验与接触裁剪未改。

构建重映射成功，27项测试通过；已备份并禁用test.5、部署test.6，校验一致且只有一份活动Camera。配套YSS保持test.2。部署SHA256：`85D818DC2F7CFEC40ACE4B8CBDB707A6016FE3C306E61F415DD9BB1BF85BA487`。远距离转向与真实遮挡行为待用户重启实测。

## test.7：疾跑临时释放朝向

用户确认test.6正常，要求锁定行走朝向敌人、疾跑可以转身逃跑。新增实际isSprinting状态检查，同时释放Camera朝向所有权和旧face_target分支。暂停不调用cancel，不清除目标或当前动作，结束疾跑自动恢复；Root接触裁剪与朝向所有权分开，仍保持原有接触保护。

构建重映射成功，27项既有回归测试通过（不等于疾跑游戏实测）；test.6已备份并禁用、test.7部署校验一致且唯一活动。配套YSS保持test.2。SHA256：`4F13E37940C6FF0821986C86E36B6E2A7FD8C715B49F8B6B8A82AB08D849BC8E`。待用户重启验收行走/疾跑切换。

## test.8：P3a双目标构图

用户已验收test.7。按P3-DUEL-CAMERA.md实现主动锁定双目标AABB透视拟合、有限鼠标微调与时间平滑、疾跑/瞄准让出、距离上限及玩家优先回退。复用越肩体积碰撞，YSS技能轨道和震动叠加后再做最终碰撞；选敌使用技能轨道与震动之前的基础镜头。新增duel.json及duel on/off命令，配置加载失败保留旧状态。YSS/YSM本轮无修改。

构建重映射通过，35项测试通过（新增7项构图/边界测试和1项命令解析测试）。产物语言JSON校验正常，客户端部署SHA256：`E43CC356240D04371A107CFAED74363D2E9D2057B4C85265ABC642C0D04F709C`。test.7已备份并禁用；只有一个活动shouldersurfing。实际构图、碰撞、鼠标/疾跑手感与光影仍待用户完整重启测试，未宣称实机验收。

## test.9：锁定相对移动与意图式疾跑

用户确认test.8构图、平滑、碰撞和疾跑释放无问题。新增LockMovementService：锁定时W/S/A/D转为目标相对输入，保持速度和方向语义；新增SprintSteering，朝目标W疾跑保持锁定，后退/纯横移或鼠标主动转离超过阈值才释放本次疾跑的朝向和构图，结束疾跑重置。自动构图改变镜头不会计为鼠标意图；锁定、目标生命周期和Root接触裁剪保持不变。

Camera构建和41项测试通过：原有35项加6项移动/疾跑测试；客户端已部署test.9，test.8完整备份并禁用，唯一活动shouldersurfing。YSS保持test.2。真实W/S/A/D、朝目标疾跑和转身逃跑待用户重启测试。

## test.10：第三人称任意方向起跑

用户确认test.9方向和意图式疾跑正常，但原版A/S/D不能从静止直接起跑。新增AnyDirectionSprintService，在Camera肩部第三人称收集方向+疾跑输入，于LocalPlayer.aiStep末尾设置疾跑，避免原版只允许W的条件取消；第一人称/界面/游泳/飞行/骑乘等保持原版。

Camera构建和43项测试通过：原有41项加2项输入状态测试；客户端已部署test.10，test.9完整备份并禁用，唯一活动shouldersurfing。YSS保持test.2。真实A/S/D直起疾跑待用户重启测试。

## P4：YSS 服务端确认命中震动

YSS 新增 `YssClientHitResolvedEvent` 和 P2C 命中通知包。通知只在服务端验证通过并且 `hurt(...)` 成功后发送给攻击者，携带攻击者/目标 UUID、模型、动画、段索引、目标位置和单调序列号；不改变伤害、停帧、击退或现有 C2S 协议。Camera 通过可选反射桥接该公共事件，仅响应本地玩家并按序列号去重，在 `yss_hit` 槽触发内置 `yesstevecamera:light_hit`。旧版 YSS 缺少新事件时只跳过命中震动，攻击辅助和原有镜头功能保持可用。详细接口见 [P4-HIT-SHAKE.md](P4-HIT-SHAKE.md)。

## P4：命中检测区间实现

Camera 新增 `ctrl.camera_hit_begin(preset, slot, scale)` / `ctrl.camera_hit_end(slot)`；VFX 新增 `ctrl.vfx_hit_begin(slot, sound_id, effect_id, effect_slot)` / `ctrl.vfx_hit_end(slot)`。窗口由动画指令帧显式开启和关闭，收到 YSS 的服务端确认命中后按 sequence 去重并触发表现；没有窗口时保留 Camera 默认 `light_hit` 和 VFX 原有 `audio.json` 命中绑定。两边均在换世界、死亡、reload 时清理窗口。Camera 和 VFX 构建成功，游戏内命中窗口仍待客户端实测。
