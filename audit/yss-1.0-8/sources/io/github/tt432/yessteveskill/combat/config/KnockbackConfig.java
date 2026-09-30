/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package io.github.tt432.yessteveskill.combat.config;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tt432.yessteveskill.combat.config.KnockbackDirection;

public record KnockbackConfig(float power, KnockbackDirection direction, float angleOffset) {
    public static final Codec<KnockbackConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("power").forGetter(KnockbackConfig::power), (App)KnockbackDirection.CODEC.optionalFieldOf("dir", (Object)KnockbackDirection.SKILL).forGetter(KnockbackConfig::direction), (App)Codec.FLOAT.optionalFieldOf("angle_offset", (Object)Float.valueOf(0.0f)).forGetter(KnockbackConfig::angleOffset)).apply((Applicative)instance, KnockbackConfig::new));
}

