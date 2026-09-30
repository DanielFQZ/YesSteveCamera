/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.format.parser.pojo.animation.keyframe.BoneKeyFrame
 *  com.elfmcys.ysm.format.parser.pojo.animation.keyframe.BoneKeyFrameList
 *  com.elfmcys.ysm.format.parser.pojo.animation.value.UnionValue
 *  com.elfmcys.ysm.format.parser.pojo.animation.value.UnionValue$ValueType
 *  com.elfmcys.ysm.format.parser.pojo.animation.value.Vector3v
 *  com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrame
 *  com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrameProcessor
 *  com.elfmcys.ysm.geckolib3.core.keyframe.bone.RawBoneKeyFrame
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package io.github.tt432.yessteveskill.yss.camera;

import com.elfmcys.ysm.format.parser.pojo.animation.keyframe.BoneKeyFrameList;
import com.elfmcys.ysm.format.parser.pojo.animation.value.UnionValue;
import com.elfmcys.ysm.format.parser.pojo.animation.value.Vector3v;
import com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrame;
import com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrameProcessor;
import com.elfmcys.ysm.geckolib3.core.keyframe.bone.RawBoneKeyFrame;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.tt432.yessteveskill.yss.YssAnimationFrame;
import io.github.tt432.yessteveskill.yss.YssViewModifier;
import io.github.tt432.yessteveskill.yss.extension.YssAnimationExtensionType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class YssCameraExtensionType
implements YssAnimationExtensionType<CameraExtension> {
    private static final Gson GSON = new Gson();

    @Override
    public String key() {
        return "camera";
    }

    @Override
    public CameraExtension parse(JsonElement jsonElement) {
        JsonObject object = jsonElement.getAsJsonObject();
        LinkedHashMap<String, CameraTrack> tracks = new LinkedHashMap<String, CameraTrack>();
        for (Map.Entry entry : object.entrySet()) {
            tracks.put((String)entry.getKey(), YssCameraExtensionType.parseTrack(((JsonElement)entry.getValue()).getAsJsonObject()));
        }
        return new CameraExtension(tracks);
    }

    @Override
    public void apply(CameraExtension extension, YssAnimationFrame frame, YssViewModifier.Builder builder) {
        Float fov;
        Vector3f rotation;
        CameraTrack track = extension.selectTrack();
        if (track == null) {
            return;
        }
        float timeSeconds = frame.animationSeconds();
        Vector3f position = track.position(timeSeconds);
        if (position != null) {
            builder.position(position);
        }
        if ((rotation = track.rotation(timeSeconds)) != null) {
            builder.rotation(rotation);
        }
        if ((fov = track.fov(timeSeconds)) != null) {
            builder.fov(fov);
        }
    }

    private static CameraTrack parseTrack(JsonObject object) {
        return new CameraTrack(object.has("position") ? YssCameraExtensionType.parseChannel(object.get("position")) : null, object.has("rotation") ? YssCameraExtensionType.parseChannel(object.get("rotation")) : null, object.has("fov") ? YssCameraExtensionType.parseChannel(object.get("fov")) : null);
    }

    private static KeyframeChannel parseChannel(JsonElement element) {
        BoneKeyFrameList source = (BoneKeyFrameList)GSON.fromJson(element, BoneKeyFrameList.class);
        RawBoneKeyFrame[] rawFrames = new RawBoneKeyFrame[source.keyFrames.size()];
        for (int index = 0; index < source.keyFrames.size(); ++index) {
            com.elfmcys.ysm.format.parser.pojo.animation.keyframe.BoneKeyFrame sourceFrame = (com.elfmcys.ysm.format.parser.pojo.animation.keyframe.BoneKeyFrame)source.keyFrames.get(index);
            RawBoneKeyFrame rawFrame = new RawBoneKeyFrame();
            rawFrame.startTick = sourceFrame.startTick;
            rawFrame.easingType = sourceFrame.easingType;
            YssCameraExtensionType.assignVector(rawFrame, sourceFrame.preValue, true);
            if (sourceFrame.postValue == null) {
                rawFrame.contiguous = true;
            } else {
                YssCameraExtensionType.assignVector(rawFrame, sourceFrame.postValue, false);
            }
            rawFrames[index] = rawFrame;
        }
        return new KeyframeChannel(BoneKeyFrameProcessor.process((RawBoneKeyFrame[])rawFrames, (boolean)false));
    }

    private static void assignVector(RawBoneKeyFrame target, Vector3v value, boolean pre) {
        float x = YssCameraExtensionType.component(value, 0);
        float y = YssCameraExtensionType.component(value, 1);
        float z = YssCameraExtensionType.component(value, 2);
        if (pre) {
            target.preX = x;
            target.preY = y;
            target.preZ = z;
        } else {
            target.postX = x;
            target.postY = y;
            target.postZ = z;
        }
    }

    private static float component(Vector3v vector, int index) {
        UnionValue value;
        if (vector == null || vector.components == null || vector.components.isEmpty()) {
            throw new IllegalArgumentException("camera keyframe vector is empty");
        }
        UnionValue unionValue = value = vector.components.size() == 1 ? (UnionValue)vector.components.get(0) : (UnionValue)vector.components.get(index);
        if (value.type != UnionValue.ValueType.FLOAT) {
            throw new IllegalArgumentException("camera keyframes must contain numeric values");
        }
        return value.floatValue;
    }

    public record CameraTrack(@Nullable KeyframeChannel position, @Nullable KeyframeChannel rotation, @Nullable KeyframeChannel fov) {
        @Nullable
        public Vector3f position(float timeSeconds) {
            return this.position == null ? null : this.position.sample(timeSeconds);
        }

        @Nullable
        public Vector3f rotation(float timeSeconds) {
            return this.rotation == null ? null : this.rotation.sample(timeSeconds);
        }

        @Nullable
        public Float fov(float timeSeconds) {
            Vector3f value = this.fov == null ? null : this.fov.sample(timeSeconds);
            return value == null ? null : Float.valueOf(value.x);
        }
    }

    public record CameraExtension(LinkedHashMap<String, CameraTrack> tracks) {
        public CameraExtension {
            tracks = new LinkedHashMap<String, CameraTrack>(tracks);
        }

        @Nullable
        public CameraTrack selectTrack() {
            if (this.tracks.isEmpty()) {
                return null;
            }
            CameraTrack preferred = this.tracks.get("main");
            if (preferred != null) {
                return preferred;
            }
            preferred = this.tracks.get("default");
            if (preferred != null) {
                return preferred;
            }
            preferred = this.tracks.get("camera");
            if (preferred != null) {
                return preferred;
            }
            return this.tracks.values().iterator().next();
        }
    }

    public record KeyframeChannel(List<BoneKeyFrame> frames) {
        public KeyframeChannel(List<BoneKeyFrame> frames) {
            this.frames = frames = List.copyOf(frames);
        }

        @Nullable
        public Vector3f sample(float timeSeconds) {
            float duration;
            if (this.frames.isEmpty()) {
                return null;
            }
            float tick = Math.max(0.0f, timeSeconds * 20.0f);
            BoneKeyFrame selected = this.frames.get(this.frames.size() - 1);
            for (int index = 0; index < this.frames.size() - 1; ++index) {
                BoneKeyFrame candidate = this.frames.get(index);
                if (!(tick < candidate.getEndTick())) continue;
                selected = candidate;
                break;
            }
            float progress = (duration = selected.getTotalTick()) <= 0.0f ? 1.0f : Math.max(0.0f, Math.min(1.0f, (tick - selected.getStartTick()) / duration));
            return selected.getLerpPoint(null, progress);
        }
    }
}

