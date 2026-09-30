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
import io.github.tt432.yessteveskill.combat.config.CasterMoveConfig;
import io.github.tt432.yessteveskill.combat.config.HitSegment;
import io.github.tt432.yessteveskill.combat.config.HoverConfig;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public record AnimationHitConfig(List<HitSegment> segments, List<CasterMoveConfig> casterMoves, Optional<HoverConfig> hoverOpt, boolean rootMotion) {
    public static final Codec<AnimationHitConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)HitSegment.CODEC.listOf().optionalFieldOf("segments", (Object)List.of()).forGetter(AnimationHitConfig::segments), (App)CasterMoveConfig.CODEC.listOf().optionalFieldOf("caster_moves", (Object)List.of()).forGetter(AnimationHitConfig::casterMoves), (App)HoverConfig.CODEC.optionalFieldOf("hover").forGetter(AnimationHitConfig::hoverOpt), (App)Codec.BOOL.optionalFieldOf("root_motion", (Object)false).forGetter(AnimationHitConfig::rootMotion)).apply((Applicative)instance, AnimationHitConfig::new));

    public AnimationHitConfig {
        segments = segments == null ? List.of() : List.copyOf(segments);
        casterMoves = casterMoves == null ? List.of() : List.copyOf(casterMoves);
        hoverOpt = hoverOpt == null ? Optional.empty() : hoverOpt;
    }

    @Nullable
    public HoverConfig hover() {
        return this.hoverOpt.orElse(null);
    }
}

