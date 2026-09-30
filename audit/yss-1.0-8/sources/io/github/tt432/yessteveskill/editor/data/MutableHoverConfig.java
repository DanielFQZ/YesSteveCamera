/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.HoverConfig;

public class MutableHoverConfig {
    private float duration;
    private float gravityScale;
    private boolean blockInput;

    public MutableHoverConfig() {
        this(1.0f, 1.0f, true);
    }

    public MutableHoverConfig(float duration, float gravityScale, boolean blockInput) {
        this.duration = duration;
        this.gravityScale = gravityScale;
        this.blockInput = blockInput;
    }

    public static MutableHoverConfig fromRecord(HoverConfig hover) {
        if (hover == null) {
            return null;
        }
        return new MutableHoverConfig(hover.duration(), hover.gravityScale(), hover.blockInput());
    }

    public HoverConfig toRecord() {
        return new HoverConfig(this.duration, this.gravityScale, this.blockInput);
    }

    public float getDuration() {
        return this.duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getGravityScale() {
        return this.gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
    }

    public boolean isBlockInput() {
        return this.blockInput;
    }

    public void setBlockInput(boolean blockInput) {
        this.blockInput = blockInput;
    }
}

