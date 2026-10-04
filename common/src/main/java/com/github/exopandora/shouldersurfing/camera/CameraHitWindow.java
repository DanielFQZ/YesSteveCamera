package com.github.exopandora.shouldersurfing.camera;

import java.util.HashSet;
import java.util.Set;

/** Client-side armed hit window for one local animation source and slot. */
final class CameraHitWindow {
    private final String preset;
    private final float scale;
    private final Set<Long> sequences = new HashSet<>();

    CameraHitWindow(String preset, float scale) {
        this.preset = preset;
        this.scale = scale;
    }

    String preset() { return preset; }
    float scale() { return scale; }

    boolean accept(long sequence) {
        if (sequences.size() >= 256) sequences.clear();
        return sequences.add(sequence);
    }
}
