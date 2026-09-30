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
import io.github.tt432.yessteveskill.combat.config.CasterMoveDirection;
import io.github.tt432.yessteveskill.combat.config.CasterMoveType;
import io.github.tt432.yessteveskill.combat.config.TimeWindow;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public record CasterMoveConfig(Optional<TimeWindow> timeOpt, CasterMoveDirection direction, float angleOffset, float power, CasterMoveType type) {
    public static final Codec<CasterMoveConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)TimeWindow.CODEC.optionalFieldOf("time").forGetter(CasterMoveConfig::timeOpt), (App)CasterMoveDirection.CODEC.optionalFieldOf("direction", (Object)CasterMoveDirection.FACING).forGetter(CasterMoveConfig::direction), (App)Codec.FLOAT.optionalFieldOf("angle_offset", (Object)Float.valueOf(0.0f)).forGetter(CasterMoveConfig::angleOffset), (App)Codec.FLOAT.optionalFieldOf("power", (Object)Float.valueOf(0.0f)).forGetter(CasterMoveConfig::power), (App)CasterMoveType.CODEC.optionalFieldOf("type", (Object)CasterMoveType.IMPULSE).forGetter(CasterMoveConfig::type)).apply((Applicative)instance, CasterMoveConfig::new));

    public CasterMoveConfig {
        if (direction == null) {
            direction = CasterMoveDirection.FACING;
        }
        if (type == null) {
            type = CasterMoveType.IMPULSE;
        }
    }

    @Nullable
    public TimeWindow time() {
        return this.timeOpt.orElse(null);
    }
}

