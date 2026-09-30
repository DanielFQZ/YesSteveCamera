/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.EntityCombatStats;

public class MutableEntityCombatStats {
    private float antiInterrupt;
    private float weight;
    private float hardness;

    public MutableEntityCombatStats() {
        this(0.0f, 0.0f, 0.0f);
    }

    public MutableEntityCombatStats(float antiInterrupt, float weight, float hardness) {
        this.antiInterrupt = antiInterrupt;
        this.weight = weight;
        this.hardness = hardness;
    }

    public static MutableEntityCombatStats fromRecord(EntityCombatStats stats) {
        if (stats == null) {
            return null;
        }
        return new MutableEntityCombatStats(stats.antiInterrupt(), stats.weight(), stats.hardness());
    }

    public EntityCombatStats toRecord() {
        return new EntityCombatStats(this.antiInterrupt, this.weight, this.hardness);
    }

    public float getAntiInterrupt() {
        return this.antiInterrupt;
    }

    public void setAntiInterrupt(float antiInterrupt) {
        this.antiInterrupt = antiInterrupt;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHardness() {
        return this.hardness;
    }

    public void setHardness(float hardness) {
        this.hardness = hardness;
    }
}

