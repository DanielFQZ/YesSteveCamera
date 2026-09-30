/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package io.github.tt432.yessteveskill.yss.runtime;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class YssCameraCoordinateConverter {
    private YssCameraCoordinateConverter() {
    }

    @Nullable
    public static Vector3f position(@Nullable Vector3f sampled) {
        if (sampled == null) {
            return null;
        }
        return new Vector3f((Vector3fc)sampled).div(16.0f).mul(-1.0f, 1.0f, 1.0f);
    }

    @Nullable
    public static Vector3f rotation(@Nullable Vector3f sampled) {
        if (sampled == null) {
            return null;
        }
        return new Vector3f((Vector3fc)sampled).mul(-1.0f, -1.0f, 1.0f);
    }

    @Nullable
    public static Float fov(@Nullable Float sampled) {
        return sampled;
    }
}

