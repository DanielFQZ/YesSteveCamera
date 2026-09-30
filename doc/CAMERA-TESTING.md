# YesSteveCamera 首个测试切片

## 配置

客户端启动后会创建：

```text
config/yesstevecamera/targeting.json
config/yesstevecamera/shakes/
```

将 `CAMERA-SHAKE-EXAMPLE.json` 复制到 `shakes/` 后，使用客户端命令测试：

```text
/yesstevecamera shake play yesstevecamera:heavy_hit
/yesstevecamera shake stop
```

内置 `yesstevecamera:light_hit` 不需要文件。`targeting.json` 中未知实体默认只能主动锁定；`PROTECTED` 优先于其他分类规则。

## 中键锁定

越肩视角下短按中键选择准心锥体内的下一个合法生物，长按到 `hold_ticks` 后取消。目标死亡、离开加载范围、超出距离或持续遮挡超过 `occlusion_grace_ticks` 会自动取消。锁定后玩家身体逐 tick 朝向目标，但相机观察方向保持独立。

当前切片只完成目标快照和朝向，不会替换 YSS 的攻击输入，也不会强行把玩家位置写到目标身上。吸附位移和 YSS Root Motion/Caster Move 的仲裁在下一阶段接入。
