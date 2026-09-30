/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.KnockbackConfig;
import io.github.tt432.yessteveskill.combat.config.KnockbackDirection;

public class MutableKnockbackConfig {
    private float power;
    private KnockbackDirection direction;
    private float angleOffset;

    public MutableKnockbackConfig() {
        this(0.0f, KnockbackDirection.SKILL, 0.0f);
    }

    public MutableKnockbackConfig(float power, KnockbackDirection direction, float angleOffset) {
        this.power = power;
        this.direction = direction;
        this.angleOffset = angleOffset;
    }

    public static MutableKnockbackConfig fromRecord(KnockbackConfig knockback) {
        if (knockback == null) {
            return null;
        }
        return new MutableKnockbackConfig(knockback.power(), knockback.direction(), knockback.angleOffset());
    }

    public KnockbackConfig toRecord() {
        return new KnockbackConfig(this.power, this.direction != null ? this.direction : KnockbackDirection.SKILL, this.angleOffset);
    }

    public float getPower() {
        return this.power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public KnockbackDirection getDirection() {
        return this.direction;
    }

    public void setDirection(KnockbackDirection direction) {
        this.direction = direction;
    }

    public float getAngleOffset() {
        return this.angleOffset;
    }

    public void setAngleOffset(float angleOffset) {
        this.angleOffset = angleOffset;
    }
}

