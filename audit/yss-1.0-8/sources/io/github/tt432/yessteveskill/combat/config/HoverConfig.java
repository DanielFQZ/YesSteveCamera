/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package io.github.tt432.yessteveskill.combat.config;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HoverConfig(float duration, float gravityScale, boolean blockInput) {
    private static final Codec<Float> DURATION_CODEC = Codec.FLOAT.flatXmap(f -> f.floatValue() > 0.0f ? DataResult.success((Object)f) : DataResult.error(() -> "hover duration must be > 0, got " + f), DataResult::success);
    public static final Codec<HoverConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)DURATION_CODEC.fieldOf("duration").forGetter(HoverConfig::duration), (App)Codec.FLOAT.optionalFieldOf("gravity_scale", (Object)Float.valueOf(1.0f)).forGetter(HoverConfig::gravityScale), (App)Codec.BOOL.optionalFieldOf("block_input", (Object)true).forGetter(HoverConfig::blockInput)).apply((Applicative)instance, HoverConfig::new));

    public HoverConfig {
        if (duration <= 0.0f) {
            throw new IllegalArgumentException("HoverConfig duration must be > 0, got " + duration);
        }
        gravityScale = Math.max(0.0f, Math.min(1.0f, gravityScale));
    }
}

