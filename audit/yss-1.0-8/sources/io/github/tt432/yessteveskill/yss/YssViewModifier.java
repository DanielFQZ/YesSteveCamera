/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package io.github.tt432.yessteveskill.yss;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record YssViewModifier(@Nullable Vector3f position, @Nullable Vector3f rotation, @Nullable Float fov) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Vector3f position;
        private Vector3f rotation;
        private Float fov;

        public void position(@Nullable Vector3f value) {
            this.position = value == null ? null : new Vector3f((Vector3fc)value);
        }

        public void rotation(@Nullable Vector3f value) {
            this.rotation = value == null ? null : new Vector3f((Vector3fc)value);
        }

        public void fov(@Nullable Float value) {
            this.fov = value;
        }

        public boolean isEmpty() {
            return this.position == null && this.rotation == null && this.fov == null;
        }

        public YssViewModifier build() {
            return new YssViewModifier(this.position, this.rotation, this.fov);
        }
    }
}

