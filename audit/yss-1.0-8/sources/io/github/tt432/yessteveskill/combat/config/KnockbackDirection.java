/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package io.github.tt432.yessteveskill.combat.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum KnockbackDirection {
    SKILL("skill"),
    RADIAL("radial");

    private final String key;
    public static final Codec<KnockbackDirection> CODEC;

    private KnockbackDirection(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

    static {
        CODEC = Codec.STRING.comapFlatMap(name -> {
            for (KnockbackDirection value : KnockbackDirection.values()) {
                if (!value.key.equals(name)) continue;
                return DataResult.success((Object)((Object)value));
            }
            return DataResult.error(() -> "Unknown knockback dir: " + name);
        }, KnockbackDirection::key);
    }
}

