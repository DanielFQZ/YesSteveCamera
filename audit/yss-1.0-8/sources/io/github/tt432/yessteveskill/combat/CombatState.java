/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.combat;

public class CombatState {
    private int hitstopTicks;
    private int knockupTicks;
    private int hoverTicks;
    private float hoverGravityScale = 1.0f;
    private int interruptedTicks;
    private float overrideAntiInterrupt = -1.0f;
    private int overrideAntiInterruptTicks;

    public boolean isHitstopped() {
        return this.hitstopTicks > 0;
    }

    public boolean isKnockup() {
        return this.knockupTicks > 0;
    }

    public boolean isHover() {
        return this.hoverTicks > 0;
    }

    public boolean isInterrupted() {
        return this.interruptedTicks > 0;
    }

    public boolean hasAntiInterruptOverride() {
        return this.overrideAntiInterrupt >= 0.0f;
    }

    public int getHitstopTicks() {
        return this.hitstopTicks;
    }

    public int getKnockupTicks() {
        return this.knockupTicks;
    }

    public int getHoverTicks() {
        return this.hoverTicks;
    }

    public float getHoverGravityScale() {
        return this.hoverGravityScale;
    }

    public int getInterruptedTicks() {
        return this.interruptedTicks;
    }

    public float getOverrideAntiInterrupt() {
        return this.overrideAntiInterrupt;
    }

    public int getOverrideAntiInterruptTicks() {
        return this.overrideAntiInterruptTicks;
    }

    public void setHitstop(int ticks) {
        this.hitstopTicks = Math.max(0, ticks);
    }

    public void setKnockup(int ticks) {
        this.knockupTicks = Math.max(0, ticks);
    }

    public void setHover(int ticks) {
        this.hoverTicks = Math.max(0, ticks);
    }

    public void setHoverGravityScale(float scale) {
        this.hoverGravityScale = Math.max(0.0f, Math.min(1.0f, scale));
    }

    public void setInterrupted(int ticks) {
        this.interruptedTicks = Math.max(0, ticks);
    }

    public void setOverrideAntiInterrupt(float value, int ticks) {
        if (ticks <= 0) {
            this.clearOverrideAntiInterrupt();
            return;
        }
        this.overrideAntiInterrupt = Math.max(0.0f, Math.min(1.0f, value));
        this.overrideAntiInterruptTicks = ticks;
    }

    public void clearOverrideAntiInterrupt() {
        this.overrideAntiInterrupt = -1.0f;
        this.overrideAntiInterruptTicks = 0;
    }

    public void syncFrom(int hitstopTicks, int knockupTicks, int interruptedTicks, float overrideAntiInterrupt, int overrideAntiInterruptTicks, int hoverTicks) {
        this.hitstopTicks = hitstopTicks;
        this.knockupTicks = knockupTicks;
        this.interruptedTicks = interruptedTicks;
        this.overrideAntiInterrupt = overrideAntiInterrupt;
        this.overrideAntiInterruptTicks = overrideAntiInterruptTicks;
        this.hoverTicks = hoverTicks;
    }

    public void tick() {
        if (this.hitstopTicks > 0) {
            --this.hitstopTicks;
        }
        if (this.knockupTicks > 0) {
            --this.knockupTicks;
        }
        if (this.hoverTicks > 0) {
            --this.hoverTicks;
            if (this.hoverTicks == 0) {
                this.hoverGravityScale = 1.0f;
            }
        }
        if (this.interruptedTicks > 0) {
            --this.interruptedTicks;
        }
        if (this.overrideAntiInterruptTicks > 0) {
            --this.overrideAntiInterruptTicks;
            if (this.overrideAntiInterruptTicks == 0) {
                this.overrideAntiInterrupt = -1.0f;
            }
        }
    }
}

