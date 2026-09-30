/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.geckolib3.core.builder.Animation
 *  com.elfmcys.ysm.geckolib3.core.keyframe.BoneAnimation
 *  com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrame
 *  com.elfmcys.ysm.geckolib3.core.keyframe.event.EventKeyFrame
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.editor.data;

import com.elfmcys.ysm.geckolib3.core.builder.Animation;
import com.elfmcys.ysm.geckolib3.core.keyframe.BoneAnimation;
import com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrame;
import com.elfmcys.ysm.geckolib3.core.keyframe.event.EventKeyFrame;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.jetbrains.annotations.Nullable;

public record AnimationTimelineInfo(float length, List<Float> keyframeTimes) {
    public static AnimationTimelineInfo from(JsonObject animationsRoot, String animName) {
        JsonElement animationsElement = animationsRoot.get("animations");
        if (!(animationsElement instanceof JsonObject)) {
            return new AnimationTimelineInfo(0.0f, List.of());
        }
        JsonObject animations = (JsonObject)animationsElement;
        JsonElement animElement = animations.get(animName);
        if (!(animElement instanceof JsonObject)) {
            return new AnimationTimelineInfo(0.0f, List.of());
        }
        JsonObject anim = (JsonObject)animElement;
        float length = 0.0f;
        JsonElement lengthElement = anim.get("animation_length");
        if (lengthElement != null && lengthElement.isJsonPrimitive() && lengthElement.getAsJsonPrimitive().isNumber()) {
            length = lengthElement.getAsFloat();
        }
        TreeSet<Float> times = new TreeSet<Float>();
        JsonElement bonesElement = anim.get("bones");
        if (bonesElement instanceof JsonObject) {
            JsonObject bones = (JsonObject)bonesElement;
            for (Map.Entry boneEntry : bones.entrySet()) {
                Object v = boneEntry.getValue();
                if (!(v instanceof JsonObject)) continue;
                JsonObject bone = (JsonObject)v;
                AnimationTimelineInfo.collectTimes(bone.get("position"), times);
                AnimationTimelineInfo.collectTimes(bone.get("rotation"), times);
                AnimationTimelineInfo.collectTimes(bone.get("scale"), times);
            }
        }
        AnimationTimelineInfo.collectTimes(anim.get("timeline"), times);
        AnimationTimelineInfo.collectTimes(anim.get("sound_effects"), times);
        AnimationTimelineInfo.collectTimes(anim.get("particle_effects"), times);
        return new AnimationTimelineInfo(length, new ArrayList<Float>(times));
    }

    public static AnimationTimelineInfo from(Animation animation) {
        float animationTicks = animation.animationLength;
        float length = Float.isFinite(animationTicks) && animationTicks > 0.0f && animationTicks < Float.MAX_VALUE ? animationTicks / 20.0f : 0.0f;
        TreeSet<Float> times = new TreeSet<Float>();
        for (BoneAnimation bone : animation.boneAnimations) {
            AnimationTimelineInfo.collectTicks(bone.rotationKeyFrames, times);
            AnimationTimelineInfo.collectTicks(bone.positionKeyFrames, times);
            AnimationTimelineInfo.collectTicks(bone.scaleKeyFrames, times);
        }
        AnimationTimelineInfo.collectEventTicks(animation.soundKeyFrames, times);
        AnimationTimelineInfo.collectEventTicks(animation.particleKeyFrames, times);
        AnimationTimelineInfo.collectEventTicks(animation.customInstructionKeyframes, times);
        return new AnimationTimelineInfo(length, new ArrayList<Float>(times));
    }

    private static void collectTicks(List<? extends BoneKeyFrame> frames, TreeSet<Float> times) {
        for (BoneKeyFrame boneKeyFrame : frames) {
            AnimationTimelineInfo.addTick(boneKeyFrame.getStartTick(), times);
        }
    }

    private static void collectEventTicks(List<? extends EventKeyFrame<?>> frames, TreeSet<Float> times) {
        for (EventKeyFrame<?> frame : frames) {
            AnimationTimelineInfo.addTick(frame.getStartTick(), times);
        }
    }

    private static void addTick(float tick, TreeSet<Float> times) {
        if (Float.isFinite(tick) && tick >= 0.0f) {
            times.add(Float.valueOf(tick / 20.0f));
        }
    }

    private static void collectTimes(@Nullable JsonElement channel, TreeSet<Float> times) {
        if (!(channel instanceof JsonObject)) {
            return;
        }
        JsonObject obj = (JsonObject)channel;
        for (String key : obj.keySet()) {
            try {
                times.add(Float.valueOf(Float.parseFloat(key)));
            }
            catch (NumberFormatException numberFormatException) {}
        }
    }
}

