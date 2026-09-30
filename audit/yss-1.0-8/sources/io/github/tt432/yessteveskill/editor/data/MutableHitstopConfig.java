/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.HitstopConfig;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class MutableHitstopConfig {
    @Nullable
    private MutableSelf self;
    @Nullable
    private MutableTarget target;
    private boolean forced;

    public MutableHitstopConfig() {
        this(null, null, false);
    }

    public MutableHitstopConfig(@Nullable MutableSelf self, @Nullable MutableTarget target, boolean forced) {
        this.self = self;
        this.target = target;
        this.forced = forced;
    }

    public static MutableHitstopConfig fromRecord(HitstopConfig hitstop) {
        if (hitstop == null) {
            return null;
        }
        return new MutableHitstopConfig(MutableSelf.fromRecord(hitstop.self()), MutableTarget.fromRecord(hitstop.target()), hitstop.forced());
    }

    public HitstopConfig toRecord() {
        return new HitstopConfig(this.self != null ? Optional.of(this.self.toRecord()) : Optional.empty(), this.target != null ? Optional.of(this.target.toRecord()) : Optional.empty(), this.forced);
    }

    @Nullable
    public MutableSelf getSelf() {
        return this.self;
    }

    public void setSelf(@Nullable MutableSelf self) {
        this.self = self;
    }

    @Nullable
    public MutableTarget getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable MutableTarget target) {
        this.target = target;
    }

    public boolean isForced() {
        return this.forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public static class MutableSelf {
        private int ticks;
        private boolean scaleByTargetHardness;
        private float multiTargetDecay;

        public MutableSelf() {
            this(0, false, 1.0f);
        }

        public MutableSelf(int ticks, boolean scaleByTargetHardness, float multiTargetDecay) {
            this.ticks = ticks;
            this.scaleByTargetHardness = scaleByTargetHardness;
            this.multiTargetDecay = multiTargetDecay;
        }

        public static MutableSelf fromRecord(HitstopConfig.Self self) {
            if (self == null) {
                return null;
            }
            return new MutableSelf(self.ticks(), self.scaleByTargetHardness(), self.multiTargetDecay());
        }

        public HitstopConfig.Self toRecord() {
            return new HitstopConfig.Self(this.ticks, this.scaleByTargetHardness, this.multiTargetDecay);
        }

        public int getTicks() {
            return this.ticks;
        }

        public void setTicks(int ticks) {
            this.ticks = ticks;
        }

        public boolean isScaleByTargetHardness() {
            return this.scaleByTargetHardness;
        }

        public void setScaleByTargetHardness(boolean scaleByTargetHardness) {
            this.scaleByTargetHardness = scaleByTargetHardness;
        }

        public float getMultiTargetDecay() {
            return this.multiTargetDecay;
        }

        public void setMultiTargetDecay(float multiTargetDecay) {
            this.multiTargetDecay = multiTargetDecay;
        }
    }

    public static class MutableTarget {
        private int ticks;
        private boolean scaleByHardness;

        public MutableTarget() {
            this(0, true);
        }

        public MutableTarget(int ticks, boolean scaleByHardness) {
            this.ticks = ticks;
            this.scaleByHardness = scaleByHardness;
        }

        public static MutableTarget fromRecord(HitstopConfig.Target target) {
            if (target == null) {
                return null;
            }
            return new MutableTarget(target.ticks(), target.scaleByHardness());
        }

        public HitstopConfig.Target toRecord() {
            return new HitstopConfig.Target(this.ticks, this.scaleByHardness);
        }

        public int getTicks() {
            return this.ticks;
        }

        public void setTicks(int ticks) {
            this.ticks = ticks;
        }

        public boolean isScaleByHardness() {
            return this.scaleByHardness;
        }

        public void setScaleByHardness(boolean scaleByHardness) {
            this.scaleByHardness = scaleByHardness;
        }
    }
}

