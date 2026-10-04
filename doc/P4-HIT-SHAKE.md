# P4: YSS confirmed hit camera shake

Camera consumes the public `YssClientHitResolvedEvent` exposed by YSS. The
event is delivered only after the server accepts a hit and `hurt(...)` returns
successfully, so empty swings, rejected range/line-of-sight checks, and attack
cooldown failures do not trigger a shake.

The first integration uses the built-in `yesstevecamera:light_hit` preset in
the `yss_hit` mixer slot. It is intentionally a very small impulse. The
previous light-hit strength is available as `yesstevecamera:heavy_hit` for
strong attacks. The slot is independent from animation and command shakes,
so a hit can overlap an existing camera effect without replacing it.
Each packet carries a monotonically increasing sequence; Camera ignores a
duplicate or out-of-order sequence and only the attacking client reacts.

The bridge is optional. If YSS is absent or does not provide the public event,
Camera keeps its target lock, attack assist, and manual/animation shake paths
available and reports the compatibility state in `latest.log`.
