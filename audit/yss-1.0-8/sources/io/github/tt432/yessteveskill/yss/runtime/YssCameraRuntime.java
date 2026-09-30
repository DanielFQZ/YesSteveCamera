/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.geckolib3.core.builder.LoopType
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.yss.runtime;

import com.elfmcys.ysm.geckolib3.core.builder.LoopType;
import io.github.tt432.yessteveskill.yss.YssAnimationKey;
import io.github.tt432.yessteveskill.yss.camera.YssCameraExtensionType;
import io.github.tt432.yessteveskill.yss.runtime.YssCameraCoordinateConverter;
import io.github.tt432.yessteveskill.yss.runtime.YssCameraRuntimeSample;
import org.jetbrains.annotations.Nullable;

public class YssCameraRuntime {
    private final YssAnimationKey key;
    private final String controllerName;
    private final LoopType loopType;
    private final float animationLength;
    private final YssCameraExtensionType.CameraTrack track;
    private float elapsedSeconds;

    public YssCameraRuntime(YssAnimationKey key, String controllerName, LoopType loopType, float animationLength, YssCameraExtensionType.CameraTrack track) {
        this.key = key;
        this.controllerName = controllerName;
        this.loopType = loopType;
        this.animationLength = animationLength;
        this.track = track;
    }

    public YssAnimationKey key() {
        return this.key;
    }

    public void tick() {
        this.elapsedSeconds += 0.05f;
    }

    public YssCameraRuntimeSample sample(float partialTick) {
        float sampleTime = this.playbackTime(partialTick);
        return new YssCameraRuntimeSample(YssCameraCoordinateConverter.position(this.track.position(sampleTime)), YssCameraCoordinateConverter.rotation(this.track.rotation(sampleTime)), YssCameraCoordinateConverter.fov(this.track.fov(sampleTime)));
    }

    private float playbackTime(float partialTick) {
        float raw = this.elapsedSeconds + partialTick / 20.0f;
        if (this.animationLength <= 0.0f) {
            return raw;
        }
        return switch (this.loopType) {
            default -> throw new IncompatibleClassChangeError();
            case LoopType.LOOP -> raw % this.animationLength;
            case LoopType.HOLD_ON_LAST_FRAME -> Math.min(raw, this.animationLength);
            case LoopType.PLAY_ONCE -> raw;
        };
    }

    public boolean matches(String controllerName, @Nullable String animationName) {
        return this.controllerName.equals(controllerName) && this.key.animationName().equals(animationName);
    }
}

