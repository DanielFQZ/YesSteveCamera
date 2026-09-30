/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.client.gui;

import io.github.tt432.yessteveskill.combat.config.CasterMoveDirection;
import io.github.tt432.yessteveskill.combat.config.CasterMoveType;
import io.github.tt432.yessteveskill.combat.config.DamageTiming;
import io.github.tt432.yessteveskill.combat.config.HitReset;
import io.github.tt432.yessteveskill.combat.config.KnockbackDirection;
import java.util.Map;

final class EnumDisplay {
    private static final Map<Object, String> NAMES = Map.ofEntries((Map.Entry[])new Map.Entry[]{Map.entry((Object)((Object)DamageTiming.INSTANT), (Object)"\u5373\u65f6"), Map.entry((Object)((Object)DamageTiming.DELAYED), (Object)"\u5ef6\u8fdf"), Map.entry((Object)((Object)HitReset.Type.ON_EXIT), (Object)"\u79bb\u5f00\u91cd\u7f6e"), Map.entry((Object)((Object)HitReset.Type.ONCE), (Object)"\u4ec5\u4e00\u6b21"), Map.entry((Object)((Object)HitReset.Type.INTERVAL), (Object)"\u95f4\u9694"), Map.entry((Object)((Object)KnockbackDirection.SKILL), (Object)"\u6280\u80fd\u65b9\u5411"), Map.entry((Object)((Object)KnockbackDirection.RADIAL), (Object)"\u5f84\u5411"), Map.entry((Object)((Object)CasterMoveDirection.FACING), (Object)"\u671d\u5411"), Map.entry((Object)((Object)CasterMoveDirection.WORLD), (Object)"\u4e16\u754c"), Map.entry((Object)((Object)CasterMoveType.IMPULSE), (Object)"\u51b2\u91cf"), Map.entry((Object)((Object)CasterMoveType.SUSTAINED), (Object)"\u6301\u7eed")});

    static String displayName(Enum<?> e) {
        return NAMES.getOrDefault(e, e.name());
    }

    private EnumDisplay() {
    }
}

