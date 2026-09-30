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

public record MovementConfig(float walk, float run, float sneak, float swim, float climb, float crawl, float jumpHeight) {
    public static final MovementConfig DEFAULT = new MovementConfig(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
    public static final Codec<MovementConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.optionalFieldOf("walk", (Object)Float.valueOf(1.0f)).forGetter(MovementConfig::walk), (App)Codec.FLOAT.optionalFieldOf("run", (Object)Float.valueOf(1.0f)).forGetter(MovementConfig::run), (App)Codec.FLOAT.optionalFieldOf("sneak", (Object)Float.valueOf(1.0f)).forGetter(MovementConfig::sneak), (App)Codec.FLOAT.optionalFieldOf("swim", (Object)Float.valueOf(1.0f)).forGetter(MovementConfig::swim), (App)Codec.FLOAT.optionalFieldOf("climb", (Object)Float.valueOf(1.0f)).forGetter(MovementConfig::climb), (App)Codec.FLOAT.optionalFieldOf("crawl", (Object)Float.valueOf(1.0f)).forGetter(MovementConfig::crawl), (App)Codec.FLOAT.optionalFieldOf("jump_height", (Object)Float.valueOf(1.0f)).forGetter(MovementConfig::jumpHeight)).apply((Applicative)instance, MovementConfig::new));

    public MovementConfig {
        walk = MovementConfig.sanitize(walk);
        run = MovementConfig.sanitize(run);
        sneak = MovementConfig.sanitize(sneak);
        swim = MovementConfig.sanitize(swim);
        climb = MovementConfig.sanitize(climb);
        crawl = MovementConfig.sanitize(crawl);
        jumpHeight = MovementConfig.sanitize(jumpHeight);
    }

    private static float sanitize(float value) {
        return Float.isFinite(value) && value >= 0.0f ? value : 1.0f;
    }

    public boolean isDefault() {
        return this.walk == 1.0f && this.run == 1.0f && this.sneak == 1.0f && this.swim == 1.0f && this.climb == 1.0f && this.crawl == 1.0f && this.jumpHeight == 1.0f;
    }
}

