/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.TimeWindow;

public class MutableTimeWindow {
    private float start;
    private float end;

    public MutableTimeWindow() {
        this(0.0f, 0.0f);
    }

    public MutableTimeWindow(float start, float end) {
        this.start = start;
        this.end = end;
    }

    public static MutableTimeWindow fromRecord(TimeWindow time) {
        if (time == null) {
            return null;
        }
        return new MutableTimeWindow(time.start(), time.end());
    }

    public TimeWindow toRecord() {
        return new TimeWindow(this.start, this.end);
    }

    public float getStart() {
        return this.start;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public float getEnd() {
        return this.end;
    }

    public void setEnd(float end) {
        this.end = end;
    }
}

