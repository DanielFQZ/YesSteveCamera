# 命中检测区间与镜头震动

## Molang

在 YSM 动画中放置两条指令帧：

```molang
ctrl.camera_hit_begin('yesstevecamera:heavy_hit', 'attack_hit', 1.0);
```

在攻击判定结束的位置：

```molang
ctrl.camera_hit_end('attack_hit');
```

第一条指令只武装当前实体的一个有限命中窗口，第二条指令关闭同名窗口。
收到 YSS 服务端确认的命中包时，Camera 只有在窗口仍打开的情况下才会触发
指定预设。窗口关闭、换世界、玩家死亡或客户端会话重置后，迟到的命中包会
被丢弃。

`camera_hit_begin` 的预设、slot 和强度均由动画选择。slot 只用于区分同一
实体的多个并行攻击窗口，不是 YSS 模型 ID。命中包的 sequence 用于去重。

没有任何活动窗口时，保留兼容行为：真实命中触发内置的极小
`yesstevecamera:light_hit`。窗口活动时，指定预设优先，并抑制这次命中的
默认震动，避免一次命中震两次。

## 与 VFX/音效的关系

VFX 可以注册同一类窗口，例如：

```molang
ctrl.vfx_hit_begin('attack_hit', 'yesstevevfx:new_test_pack/attack1', 'new_test_pack:test1', 'hit_fx');
```

结束时：

```molang
ctrl.vfx_hit_end('attack_hit');
```

窗口内的 YSS 确认命中会选择指定音效和命中特效。示例中的 ID 必须替换为已经在客户端加载的资源。一次函数调用必须在一个 YSM 指令帧字符串中保持为一整行。音效 ID 可以来自任意已
加载的 VFX 包或 YSM 容器；特效 ID 使用正常的 VFX effect ID。命中音效和
特效仍由各自客户端根据本地资源解析，客户端不向服务端提交任意资源路径。

没有活动窗口时，现有 `audio.json` 的 `hit_bindings` 仍可作为兼容回退；有
活动窗口时，窗口配置优先，避免默认绑定重复播放。命中特效应在目标坐标
生成，使用独立的命中 slot，不覆盖施法者身上的普通技能特效。

## 事件边界

YSS 仍是唯一的命中权威：空挥、距离/视线校验失败、频率拒绝和
`hurt(...) == false` 都不会触发。Camera 只消费发给攻击者的确认包；VFX
命中包可以继续发给附近客户端，各客户端是否播放由自身是否武装窗口决定。

第一版不要求 YSM 提供动画停止帧或 AnimationPlayer token。窗口由指令帧显式
开启和关闭，并在客户端会话切换时清理。若未来需要区分同名动画的快速重播，
再由 YSM/YSS 提供可选的播放代次。
