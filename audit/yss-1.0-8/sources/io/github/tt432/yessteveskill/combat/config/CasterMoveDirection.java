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

public enum CasterMoveDirection {
    FACING("facing"),
    WORLD("world");

    private final String key;
    public static final Codec<CasterMoveDirection> CODEC;

    private CasterMoveDirection(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

    static {
        CODEC = Codec.STRING.comapFlatMap(name -> {
            for (CasterMoveDirection value : CasterMoveDirection.values()) {
                if (!value.key.equals(name)) continue;
                return DataResult.success((Object)((Object)value));
            }
            return DataResult.error(() -> "Unknown caster_move direction: " + name);
        }, CasterMoveDirection::key);
    }
}

