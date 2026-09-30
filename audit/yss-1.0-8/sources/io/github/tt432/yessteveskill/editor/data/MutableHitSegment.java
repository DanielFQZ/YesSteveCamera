/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.DamageTiming;
import io.github.tt432.yessteveskill.combat.config.HitReset;
import io.github.tt432.yessteveskill.combat.config.HitSegment;
import io.github.tt432.yessteveskill.editor.data.MutableHitReset;
import io.github.tt432.yessteveskill.editor.data.MutableHitstopConfig;
import io.github.tt432.yessteveskill.editor.data.MutableInterruptConfig;
import io.github.tt432.yessteveskill.editor.data.MutableKnockbackConfig;
import io.github.tt432.yessteveskill.editor.data.MutableKnockupConfig;
import io.github.tt432.yessteveskill.editor.data.MutableTimeWindow;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class MutableHitSegment {
    private String id;
    @Nullable
    private MutableTimeWindow time;
    private List<String> bones;
    private MutableHitReset hitReset;
    private float damage;
    @Nullable
    private MutableKnockbackConfig knockback;
    @Nullable
    private MutableKnockupConfig knockup;
    @Nullable
    private MutableHitstopConfig hitstop;
    @Nullable
    private MutableInterruptConfig interrupt;
    private DamageTiming damageTiming;

    public MutableHitSegment() {
        this("", null, new ArrayList<String>(), new MutableHitReset(), 0.0f, null, null, null, null, DamageTiming.INSTANT);
    }

    public MutableHitSegment(String id, @Nullable MutableTimeWindow time, List<String> bones, MutableHitReset hitReset, float damage, @Nullable MutableKnockbackConfig knockback, @Nullable MutableKnockupConfig knockup, @Nullable MutableHitstopConfig hitstop, @Nullable MutableInterruptConfig interrupt, DamageTiming damageTiming) {
        this.id = id;
        this.time = time;
        this.bones = bones;
        this.hitReset = hitReset;
        this.damage = damage;
        this.knockback = knockback;
        this.knockup = knockup;
        this.hitstop = hitstop;
        this.interrupt = interrupt;
        this.damageTiming = damageTiming;
    }

    public static MutableHitSegment fromRecord(HitSegment segment) {
        return new MutableHitSegment(segment.id(), MutableTimeWindow.fromRecord(segment.time()), new ArrayList<String>(segment.bones()), MutableHitReset.fromRecord(segment.hitReset()), segment.damage(), MutableKnockbackConfig.fromRecord(segment.knockback()), MutableKnockupConfig.fromRecord(segment.knockup()), MutableHitstopConfig.fromRecord(segment.hitstop()), MutableInterruptConfig.fromRecord(segment.interrupt()), segment.damageTiming());
    }

    public HitSegment toRecord() {
        return new HitSegment(this.id != null ? this.id : "", this.time == null ? Optional.empty() : Optional.of(this.time.toRecord()), List.copyOf(this.bones != null ? this.bones : List.of()), this.hitReset != null ? this.hitReset.toRecord() : HitReset.ONCE, this.damage, this.knockback == null ? Optional.empty() : Optional.of(this.knockback.toRecord()), this.knockup == null ? Optional.empty() : Optional.of(this.knockup.toRecord()), this.hitstop == null ? Optional.empty() : Optional.of(this.hitstop.toRecord()), this.interrupt == null ? Optional.empty() : Optional.of(this.interrupt.toRecord()), this.damageTiming != null ? this.damageTiming : DamageTiming.INSTANT);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public MutableTimeWindow getTime() {
        return this.time;
    }

    public void setTime(@Nullable MutableTimeWindow time) {
        this.time = time;
    }

    public List<String> getBones() {
        return this.bones;
    }

    public void setBones(List<String> bones) {
        this.bones = bones;
    }

    public MutableHitReset getHitReset() {
        return this.hitReset;
    }

    public void setHitReset(MutableHitReset hitReset) {
        this.hitReset = hitReset;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Nullable
    public MutableKnockbackConfig getKnockback() {
        return this.knockback;
    }

    public void setKnockback(@Nullable MutableKnockbackConfig knockback) {
        this.knockback = knockback;
    }

    @Nullable
    public MutableKnockupConfig getKnockup() {
        return this.knockup;
    }

    public void setKnockup(@Nullable MutableKnockupConfig knockup) {
        this.knockup = knockup;
    }

    @Nullable
    public MutableHitstopConfig getHitstop() {
        return this.hitstop;
    }

    public void setHitstop(@Nullable MutableHitstopConfig hitstop) {
        this.hitstop = hitstop;
    }

    @Nullable
    public MutableInterruptConfig getInterrupt() {
        return this.interrupt;
    }

    public void setInterrupt(@Nullable MutableInterruptConfig interrupt) {
        this.interrupt = interrupt;
    }

    public DamageTiming getDamageTiming() {
        return this.damageTiming;
    }

    public void setDamageTiming(DamageTiming damageTiming) {
        this.damageTiming = damageTiming;
    }
}

