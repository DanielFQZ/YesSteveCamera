/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package io.github.tt432.yessteveskill.yss.attack;

import com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.tt432.yessteveskill.yss.attack.YssAttackProjectLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class YssAttackModelSampler {
    private static final float MODEL_UNIT = 0.0625f;
    private static final int[][] BOX_EDGES = new int[][]{{0, 1}, {0, 2}, {0, 4}, {1, 3}, {1, 5}, {2, 3}, {2, 6}, {3, 7}, {4, 5}, {4, 6}, {5, 7}, {6, 7}};
    private final YssAttackProjectLoader.ModelGeometry geometry;
    private final List<YssAttackProjectLoader.BoneGeometry> bones;
    private final AnimatedGeoModel animatedModel;
    private final Vec3 origin;
    private final float bodyYaw;
    private final float widthScale;
    private final float heightScale;
    private final Set<Integer> zeroPositionBones;
    private final Map<Integer, Matrix4f> bonePoses = new HashMap<Integer, Matrix4f>();
    private final Set<Integer> invalidBones = new HashSet<Integer>();
    private final Set<Integer> resolvingBones = new HashSet<Integer>();
    private final List<AABB> hitBoxes = new ArrayList<AABB>();
    private final List<LineSegment> hitLines = new ArrayList<LineSegment>();

    public YssAttackModelSampler(YssAttackProjectLoader.ModelGeometry geometry, List<YssAttackProjectLoader.BoneGeometry> bones, AnimatedGeoModel animatedModel, Vec3 origin, float bodyYaw, float widthScale, float heightScale, Set<Integer> zeroPositionBones) {
        this.geometry = geometry;
        this.bones = bones;
        this.animatedModel = animatedModel;
        this.origin = origin;
        this.bodyYaw = bodyYaw;
        this.widthScale = this.sanitizeScale(widthScale);
        this.heightScale = this.sanitizeScale(heightScale);
        this.zeroPositionBones = zeroPositionBones;
    }

    public void sample() {
        PoseStack poseStack = new PoseStack();
        poseStack.m_85837_(this.origin.f_82479_, this.origin.f_82480_, this.origin.f_82481_);
        poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0f - this.bodyYaw));
        poseStack.m_252880_(0.0f, 0.01f, 0.0f);
        poseStack.m_85841_(this.widthScale, this.heightScale, this.widthScale);
        Matrix4f modelPose = poseStack.m_85850_().m_252922_();
        Matrix4f worldPose = new Matrix4f();
        for (YssAttackProjectLoader.BoneGeometry bone : this.bones) {
            Matrix4f bonePose;
            AnimatedGeoBone animatedBone = (AnimatedGeoBone)this.animatedModel.getBoneMap().get(bone.pooledName());
            if (animatedBone == null || animatedBone.areCubesHidden() || (bonePose = this.resolveBonePose(bone)) == null || animatedBone.getScaleX() == 0.0f || animatedBone.getScaleY() == 0.0f || animatedBone.getScaleZ() == 0.0f) continue;
            modelPose.mulAffine((Matrix4fc)bonePose, worldPose);
            for (YssAttackProjectLoader.CubeGeometry cube : bone.cubes()) {
                this.sampleCube(cube.vertices(), worldPose);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private Matrix4f resolveBonePose(YssAttackProjectLoader.BoneGeometry bone) {
        Matrix4f cached = this.bonePoses.get(bone.pooledName());
        if (cached != null) {
            return cached;
        }
        if (this.invalidBones.contains(bone.pooledName()) || !this.resolvingBones.add(bone.pooledName())) {
            this.invalidBones.add(bone.pooledName());
            return null;
        }
        try {
            Matrix4f pose;
            AnimatedGeoBone animatedBone = (AnimatedGeoBone)this.animatedModel.getBoneMap().get(bone.pooledName());
            if (animatedBone == null || !this.hasValidTransform(animatedBone)) {
                this.invalidBones.add(bone.pooledName());
                Matrix4f matrix4f = null;
                return matrix4f;
            }
            if (bone.parent() == null) {
                pose = new Matrix4f();
            } else {
                YssAttackProjectLoader.BoneGeometry parent = this.geometry.bone(bone.parent());
                if (parent == null) {
                    this.invalidBones.add(bone.pooledName());
                    Matrix4f matrix4f = null;
                    return matrix4f;
                }
                Matrix4f parentPose = this.resolveBonePose(parent);
                AnimatedGeoBone animatedParent = (AnimatedGeoBone)this.animatedModel.getBoneMap().get(parent.pooledName());
                if (parentPose == null || animatedParent == null || animatedParent.areChildrenHidden()) {
                    this.invalidBones.add(bone.pooledName());
                    Matrix4f matrix4f = null;
                    return matrix4f;
                }
                pose = new Matrix4f((Matrix4fc)parentPose);
            }
            this.applyLocalPose(pose, animatedBone);
            this.bonePoses.put(bone.pooledName(), pose);
            Matrix4f matrix4f = pose;
            return matrix4f;
        }
        finally {
            this.resolvingBones.remove(bone.pooledName());
        }
    }

    private void applyLocalPose(Matrix4f pose, AnimatedGeoBone bone) {
        Vector3f pivot = bone.getPivot();
        boolean zeroPosition = this.zeroPositionBones.contains(bone.getPooledName());
        float positionX = zeroPosition ? 0.0f : bone.getPositionX();
        float positionY = zeroPosition ? 0.0f : bone.getPositionY();
        float positionZ = zeroPosition ? 0.0f : bone.getPositionZ();
        pose.translate((pivot.x - positionX) * 0.0625f, (pivot.y + positionY) * 0.0625f, (pivot.z + positionZ) * 0.0625f).rotateZYX(bone.getRotationZ(), bone.getRotationY(), bone.getRotationX()).scale(YssAttackModelSampler.sanitizeAxisScale(bone.getScaleX()), YssAttackModelSampler.sanitizeAxisScale(bone.getScaleY()), YssAttackModelSampler.sanitizeAxisScale(bone.getScaleZ())).translate(-pivot.x * 0.0625f, -pivot.y * 0.0625f, -pivot.z * 0.0625f);
    }

    private static float sanitizeAxisScale(float value) {
        return value == 0.0f ? 1.0f : value;
    }

    private boolean hasValidTransform(AnimatedGeoBone bone) {
        Vector3f pivot = bone.getPivot();
        return Float.isFinite(pivot.x) && Float.isFinite(pivot.y) && Float.isFinite(pivot.z) && Float.isFinite(bone.getPositionX()) && Float.isFinite(bone.getPositionY()) && Float.isFinite(bone.getPositionZ()) && Float.isFinite(bone.getRotationX()) && Float.isFinite(bone.getRotationY()) && Float.isFinite(bone.getRotationZ()) && Float.isFinite(bone.getScaleX()) && Float.isFinite(bone.getScaleY()) && Float.isFinite(bone.getScaleZ());
    }

    private void sampleCube(float[] vertices, Matrix4f pose) {
        Vec3[] transformedVertices = new Vec3[8];
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        Vector4f transformed = new Vector4f();
        for (int vertexIndex = 0; vertexIndex < transformedVertices.length; ++vertexIndex) {
            Vec3 position;
            int offset = vertexIndex * 3;
            transformed.set(vertices[offset], vertices[offset + 1], vertices[offset + 2], 1.0f).mul((Matrix4fc)pose);
            transformedVertices[vertexIndex] = position = new Vec3((double)transformed.x, (double)transformed.y, (double)transformed.z);
            minX = Math.min(minX, position.f_82479_);
            minY = Math.min(minY, position.f_82480_);
            minZ = Math.min(minZ, position.f_82481_);
            maxX = Math.max(maxX, position.f_82479_);
            maxY = Math.max(maxY, position.f_82480_);
            maxZ = Math.max(maxZ, position.f_82481_);
        }
        this.hitBoxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
        for (int[] edge : BOX_EDGES) {
            Vec3 from = transformedVertices[edge[0]];
            Vec3 to = transformedVertices[edge[1]];
            if (from.equals((Object)to)) continue;
            this.hitLines.add(new LineSegment(from, to));
        }
    }

    private float sanitizeScale(float value) {
        return Float.isFinite(value) && value > 0.0f ? value : 1.0f;
    }

    public List<AABB> hitBoxes() {
        return this.hitBoxes;
    }

    public List<LineSegment> hitLines() {
        return this.hitLines;
    }

    public record LineSegment(Vec3 from, Vec3 to) {
    }
}

