/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.HitReset;

public class MutableHitReset {
    private HitReset.Type type;
    private float interval;

    public MutableHitReset() {
        this(HitReset.Type.ONCE, 0.0f);
    }

    public MutableHitReset(HitReset.Type type, float interval) {
        this.type = type;
        this.interval = interval;
    }

    public static MutableHitReset fromRecord(HitReset hitReset) {
        if (hitReset == null) {
            return new MutableHitReset();
        }
        return new MutableHitReset(hitReset.type(), hitReset.interval());
    }

    public HitReset toRecord() {
        return new HitReset(this.type != null ? this.type : HitReset.Type.ONCE, this.interval);
    }

    public HitReset.Type getType() {
        return this.type;
    }

    public void setType(HitReset.Type type) {
        this.type = type;
    }

    public float getInterval() {
        return this.interval;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }
}

