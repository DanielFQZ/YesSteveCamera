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
import io.github.tt432.yessteveskill.combat.config.DamageTiming;
import io.github.tt432.yessteveskill.combat.config.HitReset;
import io.github.tt432.yessteveskill.combat.config.HitstopConfig;
import io.github.tt432.yessteveskill.combat.config.InterruptConfig;
import io.github.tt432.yessteveskill.combat.config.KnockbackConfig;
import io.github.tt432.yessteveskill.combat.config.KnockupConfig;
import io.github.tt432.yessteveskill.combat.config.TimeWindow;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public record HitSegment(String id, Optional<TimeWindow> timeOpt, List<String> bones, HitReset hitReset, float damage, Optional<KnockbackConfig> knockbackOpt, Optional<KnockupConfig> knockupOpt, Optional<HitstopConfig> hitstopOpt, Optional<InterruptConfig> interruptOpt, DamageTiming damageTiming) {
    public static final Codec<HitSegment> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.optionalFieldOf("id", (Object)"").forGetter(HitSegment::id), (App)TimeWindow.CODEC.optionalFieldOf("time").forGetter(HitSegment::timeOpt), (App)Codec.STRING.listOf().optionalFieldOf("bones", (Object)List.of()).forGetter(HitSegment::bones), (App)HitReset.CODEC.optionalFieldOf("hit_reset", (Object)HitReset.ONCE).forGetter(HitSegment::hitReset), (App)Codec.FLOAT.optionalFieldOf("damage", (Object)Float.valueOf(0.0f)).forGetter(HitSegment::damage), (App)KnockbackConfig.CODEC.optionalFieldOf("knockback").forGetter(HitSegment::knockbackOpt), (App)KnockupConfig.CODEC.optionalFieldOf("knockup").forGetter(HitSegment::knockupOpt), (App)HitstopConfig.CODEC.optionalFieldOf("hitstop").forGetter(HitSegment::hitstopOpt), (App)InterruptConfig.CODEC.optionalFieldOf("interrupt").forGetter(HitSegment::interruptOpt), (App)DamageTiming.CODEC.optionalFieldOf("damage_timing", (Object)DamageTiming.INSTANT).forGetter(HitSegment::damageTiming)).apply((Applicative)instance, HitSegment::new));

    public HitSegment {
        if (id == null) {
            id = "";
        }
        if (bones == null) {
            bones = List.of();
        }
        if (hitReset == null) {
            hitReset = HitReset.ONCE;
        }
    }

    @Nullable
    public TimeWindow time() {
        return this.timeOpt.orElse(null);
    }

    @Nullable
    public KnockbackConfig knockback() {
        return this.knockbackOpt.orElse(null);
    }

    @Nullable
    public KnockupConfig knockup() {
        return this.knockupOpt.orElse(null);
    }

    @Nullable
    public HitstopConfig hitstop() {
        return this.hitstopOpt.orElse(null);
    }

    @Nullable
    public InterruptConfig interrupt() {
        return this.interruptOpt.orElse(null);
    }
}

