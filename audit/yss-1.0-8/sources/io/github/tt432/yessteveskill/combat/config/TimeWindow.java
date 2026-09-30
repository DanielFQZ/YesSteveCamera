/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package io.github.tt432.yessteveskill.combat.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;

public record TimeWindow(float start, float end) {
    public static final Codec<TimeWindow> CODEC = Codec.FLOAT.listOf().comapFlatMap(list -> {
        if (list.size() != 2) {
            return DataResult.error(() -> "time must be a 2-element array, got size " + list.size());
        }
        float s = ((Float)list.get(0)).floatValue();
        float e = ((Float)list.get(1)).floatValue();
        if (e < s) {
            return DataResult.error(() -> "time end < start: " + s + " > " + e);
        }
        return DataResult.success((Object)((Object)new TimeWindow(s, e)));
    }, tw -> List.of((Object)Float.valueOf(tw.start), (Object)Float.valueOf(tw.end)));

    public TimeWindow {
        if (!(end >= start)) {
            throw new IllegalArgumentException("TimeWindow end < start: " + start + " > " + end);
        }
    }
}

