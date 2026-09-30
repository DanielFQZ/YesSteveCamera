/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.geckolib3.core.builder.Animation
 *  com.elfmcys.ysm.geckolib3.core.keyframe.BoneAnimation
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 */
package io.github.tt432.yessteveskill.yss.attack;

import com.elfmcys.ysm.geckolib3.core.builder.Animation;
import com.elfmcys.ysm.geckolib3.core.keyframe.BoneAnimation;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel;
import io.github.tt432.yessteveskill.yss.YssKeyFrameSampler;
import io.github.tt432.yessteveskill.yss.attack.YssAttackProjectLoader;
import java.util.ArrayList;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

final class YssRootMotion {
    static final String ROOT_BONE_NAME = "Root";
    private static final float MODEL_UNIT = 0.0625f;

    private YssRootMotion() {
    }

    @Nullable
    static Vec3 computeModelOffset(AnimatedGeoModel animatedModel, YssAttackProjectLoader.ModelGeometry geometry) {
        YssAttackProjectLoader.BoneGeometry rootGeometry = geometry.bone(ROOT_BONE_NAME);
        if (rootGeometry == null) {
            return null;
        }
        AnimatedGeoBone rootBone = (AnimatedGeoBone)animatedModel.getBoneMap().get(rootGeometry.pooledName());
        if (!(rootBone != null && YssRootMotion.isFinite(rootBone.getPositionX()) && YssRootMotion.isFinite(rootBone.getPositionY()) && YssRootMotion.isFinite(rootBone.getPositionZ()))) {
            return null;
        }
        return YssRootMotion.toModelOffset(new Vector3f(rootBone.getPositionX(), rootBone.getPositionY(), rootBone.getPositionZ()), animatedModel, geometry, rootGeometry);
    }

    @Nullable
    static Vec3 computeCurveOffset(Animation animation, float tick, AnimatedGeoModel animatedModel, YssAttackProjectLoader.ModelGeometry geometry) {
        YssAttackProjectLoader.BoneGeometry rootGeometry = geometry.bone(ROOT_BONE_NAME);
        if (rootGeometry == null) {
            return null;
        }
        for (BoneAnimation boneAnimation : animation.boneAnimations) {
            if (boneAnimation.bonePooledName != rootGeometry.pooledName()) continue;
            Vector3f channel = YssKeyFrameSampler.sample(boneAnimation.positionKeyFrames, tick, null);
            return channel == null ? null : YssRootMotion.toModelOffset(channel, animatedModel, geometry, rootGeometry);
        }
        return null;
    }

    private static Vec3 toModelOffset(Vector3f rootChannel, AnimatedGeoModel animatedModel, YssAttackProjectLoader.ModelGeometry geometry, YssAttackProjectLoader.BoneGeometry rootGeometry) {
        YssAttackProjectLoader.BoneGeometry parent;
        Vector3f offset = new Vector3f(-rootChannel.x, rootChannel.y, rootChannel.z).mul(0.0625f);
        ArrayList<AnimatedGeoBone> ancestors = new ArrayList<AnimatedGeoBone>();
        YssAttackProjectLoader.BoneGeometry current = rootGeometry;
        for (int depth = 0; current.parent() != null && depth <= geometry.bones().size() && (parent = geometry.bone(current.parent())) != null; ++depth) {
            AnimatedGeoBone animatedParent = (AnimatedGeoBone)animatedModel.getBoneMap().get(parent.pooledName());
            if (animatedParent != null) {
                ancestors.add(animatedParent);
            }
            current = parent;
        }
        Matrix4f parentLinear = new Matrix4f();
        for (int i = ancestors.size() - 1; i >= 0; --i) {
            AnimatedGeoBone ancestor = (AnimatedGeoBone)ancestors.get(i);
            parentLinear.rotateZYX(YssRootMotion.sanitizeRotation(ancestor.getRotationZ()), YssRootMotion.sanitizeRotation(ancestor.getRotationY()), YssRootMotion.sanitizeRotation(ancestor.getRotationX())).scale(YssRootMotion.sanitizeScale(ancestor.getScaleX()), YssRootMotion.sanitizeScale(ancestor.getScaleY()), YssRootMotion.sanitizeScale(ancestor.getScaleZ()));
        }
        offset.mulDirection((Matrix4fc)parentLinear);
        return new Vec3((double)offset.x, (double)offset.y, (double)offset.z);
    }

    private static boolean isFinite(float value) {
        return Float.isFinite(value);
    }

    private static float sanitizeRotation(float value) {
        return Float.isFinite(value) ? value : 0.0f;
    }

    private static float sanitizeScale(float value) {
        return Float.isFinite(value) && value != 0.0f ? value : 1.0f;
    }
}

