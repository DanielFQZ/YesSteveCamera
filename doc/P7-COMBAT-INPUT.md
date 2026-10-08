# P7 战斗输入与方块选中框（test.18）

配套 YSS：`yessteveskill-1.0-combat-input-test.3.jar`。两个仓库暂不合并，不发上游 PR，先实机验收。

Camera 启动时向 YSS 注册当前 Camera 视角条件，保存 YSS 返回的实时战斗状态。武器分类和挖掘限制已移到 YSS；Camera 的 CrosshairRenderer 与 Forge RenderHighlightEvent.Block 均读取这个状态，避免显示与输入各自判断。

Camera 视角 + 任意一只手持武器时，方块选中框隐藏，左键只能攻击/空挥，不能挖方块。YSS 在单次点击事件里保留挥手，在持续挖掘入口提前终止以避免重复挥手。进入模式时终止已经开始的挖掘。退出视角或收起武器恢复。右键交互和锁定目标标记保留，不清空 hitResult。

`config/shouldersurfing-client.toml` 的 `crosshair.hide_with_weapon` 仍只控制准心显示，设为 false 不会重新允许战斗模式下挖掘。方块框按实际输入限制隐藏。

武器规则：剑、斧、三叉戟、弓/弩（包括相应类型子类），minecraft:swords / minecraft:axes，以及 yessteveskill:weapons / yesstevecamera:weapons 标签。不改变原有仅持剑的朝向辅助规则。

`/yesstevecamera status` 新增 Input：`attack only` 为战斗输入模式；`normal` 为当前不符合启用条件；`YSS API unavailable` 表示缺少配套 API。无新版 API 时准心和方块框走原有配置，不再独立假定武器状态。其他 YSS 命中/位移接口不受新接口缺失影响。

构建及 59 项既有 Camera 测试通过；YSS 构建、发布映射和制品检查通过，没有现成单元测试套件。待用户完整重启后验证地面长按、点击连招、命中反馈、手持切换、第一人称、右键交互。详细设计和验证边界见 YSS 仓库 `docs/COMBAT-INPUT-CAMERA.md`。
