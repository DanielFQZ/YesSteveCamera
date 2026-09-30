/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.KnockupConfig;

public class MutableKnockupConfig {
    private float power;

    public MutableKnockupConfig() {
        this(0.0f);
    }

    public MutableKnockupConfig(float power) {
        this.power = power;
    }

    public static MutableKnockupConfig fromRecord(KnockupConfig knockup) {
        if (knockup == null) {
            return null;
        }
        return new MutableKnockupConfig(knockup.power());
    }

    public KnockupConfig toRecord() {
        return new KnockupConfig(this.power);
    }

    public float getPower() {
        return this.power;
    }

    public void setPower(float power) {
        this.power = power;
    }
}

