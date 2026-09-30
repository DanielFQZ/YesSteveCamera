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

public record EntityCombatStats(float antiInterrupt, float weight, float hardness) {
    public static final Codec<EntityCombatStats> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.optionalFieldOf("anti_interrupt", (Object)Float.valueOf(0.0f)).forGetter(EntityCombatStats::antiInterrupt), (App)Codec.FLOAT.optionalFieldOf("weight", (Object)Float.valueOf(0.0f)).forGetter(EntityCombatStats::weight), (App)Codec.FLOAT.optionalFieldOf("hardness", (Object)Float.valueOf(0.0f)).forGetter(EntityCombatStats::hardness)).apply((Applicative)instance, EntityCombatStats::new));

    public EntityCombatStats {
        if (antiInterrupt < 0.0f) {
            antiInterrupt = 0.0f;
        } else if (antiInterrupt > 1.0f) {
            antiInterrupt = 1.0f;
        }
        if (weight < 0.0f) {
            weight = 0.0f;
        }
        if (hardness < 0.0f) {
            hardness = 0.0f;
        }
    }
}

