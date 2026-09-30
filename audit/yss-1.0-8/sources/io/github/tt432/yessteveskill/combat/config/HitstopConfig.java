/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.combat.config;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public record HitstopConfig(Optional<Self> selfOpt, Optional<Target> targetOpt, boolean forced) {
    public static final Codec<HitstopConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Self.CODEC.optionalFieldOf("self").forGetter(HitstopConfig::selfOpt), (App)Target.CODEC.optionalFieldOf("target").forGetter(HitstopConfig::targetOpt), (App)Codec.BOOL.optionalFieldOf("forced", (Object)false).forGetter(HitstopConfig::forced)).apply((Applicative)instance, HitstopConfig::new));

    public HitstopConfig {
        selfOpt = selfOpt == null ? Optional.empty() : selfOpt;
        targetOpt = targetOpt == null ? Optional.empty() : targetOpt;
    }

    @Nullable
    public Self self() {
        return this.selfOpt.orElse(null);
    }

    @Nullable
    public Target target() {
        return this.targetOpt.orElse(null);
    }

    public record Self(int ticks, boolean scaleByTargetHardness, float multiTargetDecay) {
        public static final Codec<Self> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("ticks").forGetter(Self::ticks), (App)Codec.BOOL.optionalFieldOf("scale_by_target_hardness", (Object)false).forGetter(Self::scaleByTargetHardness), (App)Codec.FLOAT.optionalFieldOf("multi_target_decay", (Object)Float.valueOf(1.0f)).forGetter(Self::multiTargetDecay)).apply((Applicative)instance, Self::new));
    }

    public record Target(int ticks, boolean scaleByHardness) {
        public static final Codec<Target> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("ticks").forGetter(Target::ticks), (App)Codec.BOOL.optionalFieldOf("scale_by_hardness", (Object)true).forGetter(Target::scaleByHardness)).apply((Applicative)instance, Target::new));
    }
}

