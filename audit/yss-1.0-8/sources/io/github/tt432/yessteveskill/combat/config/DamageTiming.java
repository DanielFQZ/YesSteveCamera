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

public enum DamageTiming {
    INSTANT("instant"),
    DELAYED("delayed");

    private final String key;
    public static final Codec<DamageTiming> CODEC;

    private DamageTiming(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

    static {
        CODEC = Codec.STRING.comapFlatMap(name -> {
            for (DamageTiming value : DamageTiming.values()) {
                if (!value.key.equals(name)) continue;
                return DataResult.success((Object)((Object)value));
            }
            return DataResult.error(() -> "Unknown damage_timing: " + name);
        }, DamageTiming::key);
    }
}

