/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.InterruptConfig;

public class MutableInterruptConfig {
    private float power;

    public MutableInterruptConfig() {
        this(0.0f);
    }

    public MutableInterruptConfig(float power) {
        this.power = power;
    }

    public static MutableInterruptConfig fromRecord(InterruptConfig interrupt) {
        if (interrupt == null) {
            return null;
        }
        return new MutableInterruptConfig(interrupt.power());
    }

    public InterruptConfig toRecord() {
        return new InterruptConfig(this.power);
    }

    public float getPower() {
        return this.power;
    }

    public void setPower(float power) {
        this.power = power;
    }
}

