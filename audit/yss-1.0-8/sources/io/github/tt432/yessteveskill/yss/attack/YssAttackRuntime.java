/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapability
 *  com.elfmcys.ysm.geckolib3.core.builder.Animation
 *  com.elfmcys.ysm.geckolib3.core.builder.LoopType
 *  com.elfmcys.ysm.geckolib3.core.keyframe.BoneAnimation
 *  com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrame
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel
 *  com.elfmcys.ysm.model.resource.client.ModelRenderTarget
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.yss.attack;

import com.elfmcys.ysm.capability.PlayerAnimatableCapability;
import com.elfmcys.ysm.geckolib3.core.builder.Animation;
import com.elfmcys.ysm.geckolib3.core.builder.LoopType;
import com.elfmcys.ysm.geckolib3.core.keyframe.BoneAnimation;
import com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrame;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel;
import com.elfmcys.ysm.model.resource.client.ModelRenderTarget;
import io.github.tt432.yessteveskill.combat.config.AnimationHitConfig;
import io.github.tt432.yessteveskill.combat.config.CasterMoveConfig;
import io.github.tt432.yessteveskill.combat.config.CasterMoveType;
import io.github.tt432.yessteveskill.combat.config.HitSegment;
import io.github.tt432.yessteveskill.combat.config.HoverConfig;
import io.github.tt432.yessteveskill.ysm.YSMClientAccess;
import io.github.tt432.yessteveskill.yss.attack.SegmentHitTracker;
import io.github.tt432.yessteveskill.yss.attack.YssAttackModelSampler;
import io.github.tt432.yessteveskill.yss.attack.YssAttackProjectLoader;
import io.github.tt432.yessteveskill.yss.attack.YssRootMotion;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class YssAttackRuntime {
    private final String modelId;
    private final String controllerName;
    private final int controllerSlot;
    private final String animationName;
    private final YssAttackProjectLoader.LoadedProject project;
    private final List<SegmentState> segments;
    private final List<CasterMoveConfig> casterMoves;
    private final HoverConfig hover;
    private final boolean rootMotion;
    private final int rootBonePooledName;
    @Nullable
    private final List<BoneKeyFrame> rootPositionFrames;
    private final float animationLengthTicks;
    private final Set<Integer> firedImpulseMoves;
    private final boolean looping;
    @Nullable
    private Vec3 prevRootMotionOffset;
    private boolean tailMode;
    @Nullable
    private Vec3 tailReferenceOffset;
    private float animSeconds;
    private float lastRenderTicks = -1.0f;

    public YssAttackRuntime(String modelId, String controllerName, int controllerSlot, String animationName, YssAttackProjectLoader.LoadedProject project, PlayerAnimatableCapability capability) {
        this.modelId = modelId;
        this.controllerName = controllerName;
        this.controllerSlot = controllerSlot;
        this.animationName = animationName;
        this.project = project;
        this.segments = YssAttackRuntime.buildSegments(project, animationName);
        this.casterMoves = YssAttackRuntime.buildCasterMoves(project, animationName);
        this.hover = YssAttackRuntime.buildHover(project, animationName);
        this.rootMotion = YssAttackRuntime.buildRootMotion(project, animationName);
        this.firedImpulseMoves = new LinkedHashSet<Integer>();
        Animation animation = capability == null ? null : capability.getAnimation(animationName);
        this.looping = animation != null && animation.loop == LoopType.LOOP;
        this.animationLengthTicks = animation == null ? 0.0f : animation.animationLength;
        YssAttackProjectLoader.BoneGeometry rootBone = project.geometry().bone("Root");
        this.rootBonePooledName = rootBone == null ? -1 : rootBone.pooledName();
        this.rootPositionFrames = YssAttackRuntime.findRootPositionFrames(animation, this.rootBonePooledName);
    }

    @Nullable
    private static List<BoneKeyFrame> findRootPositionFrames(@Nullable Animation animation, int rootBonePooledName) {
        if (animation == null || rootBonePooledName < 0) {
            return null;
        }
        for (BoneAnimation boneAnimation : animation.boneAnimations) {
            if (boneAnimation.bonePooledName != rootBonePooledName || boneAnimation.positionKeyFrames.isEmpty()) continue;
            return boneAnimation.positionKeyFrames;
        }
        return null;
    }

    public boolean rootMotionEnabled() {
        return this.rootMotion;
    }

    @Nullable
    public Vec3 tickRootMotion(PlayerAnimatableCapability capability, LocalPlayer player, float partialTick, float animationTicks) {
        Vec3 scaled = this.sampleScaledCurveOffset(capability, animationTicks);
        if (scaled == null) {
            return null;
        }
        Vec3 previous = this.prevRootMotionOffset == null ? scaled : this.prevRootMotionOffset;
        this.prevRootMotionOffset = scaled;
        Vec3 delta = scaled.m_82546_(previous);
        if (delta.m_82556_() < 1.0E-12) {
            return Vec3.f_82478_;
        }
        return YssAttackRuntime.rotateAroundY(delta, 180.0f - this.resolveRenderBodyYaw((LivingEntity)player, partialTick));
    }

    public boolean enterTail(PlayerAnimatableCapability capability) {
        if (!this.rootMotion) {
            return false;
        }
        Vec3 residual = this.currentRootMotionOffset(capability);
        if (residual == null || residual.m_82556_() < 1.0E-4) {
            return false;
        }
        this.tailMode = true;
        this.tailReferenceOffset = residual;
        return true;
    }

    public boolean tailMode() {
        return this.tailMode;
    }

    public boolean tailReleased(@Nullable Vec3 currentOffset) {
        if (currentOffset == null || currentOffset.m_82556_() < 1.0E-4) {
            return true;
        }
        Vec3 reference = this.tailReferenceOffset;
        if (reference == null) {
            return true;
        }
        double threshold = 0.75 * Math.max(reference.m_82553_(), 0.1);
        return currentOffset.m_82546_(reference).m_82553_() > threshold;
    }

    @Nullable
    public Vec3 currentRootMotionOffset(PlayerAnimatableCapability capability) {
        if (!this.rootMotion) {
            return null;
        }
        AnimatedGeoModel animatedModel = capability.getLoadedGeoModel();
        return animatedModel == null ? null : YssRootMotion.computeModelOffset(animatedModel, this.project.geometry());
    }

    @Nullable
    private Vec3 sampleScaledCurveOffset(PlayerAnimatableCapability capability, float animationTicks) {
        if (!this.rootMotion || this.rootPositionFrames == null) {
            return null;
        }
        AnimatedGeoModel animatedModel = capability.getLoadedGeoModel();
        if (animatedModel == null) {
            return null;
        }
        Animation animation = capability.getAnimation(this.animationName);
        if (animation == null) {
            return null;
        }
        float tick = this.animationLengthTicks > 0.0f ? Mth.m_14036_((float)animationTicks, (float)0.0f, (float)this.animationLengthTicks) : Math.max(animationTicks, 0.0f);
        Vec3 modelOffset = YssRootMotion.computeCurveOffset(animation, tick, animatedModel, this.project.geometry());
        if (modelOffset == null) {
            return null;
        }
        float[] scales = YssAttackRuntime.modelScales(capability);
        return new Vec3(modelOffset.f_82479_ * (double)scales[0], modelOffset.f_82480_ * (double)scales[1], modelOffset.f_82481_ * (double)scales[0]);
    }

    private static Vec3 rotateAroundY(Vec3 vector, float degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Vec3(vector.f_82479_ * cos + vector.f_82481_ * sin, vector.f_82480_, -vector.f_82479_ * sin + vector.f_82481_ * cos);
    }

    public boolean looping() {
        return this.looping;
    }

    private static List<SegmentState> buildSegments(YssAttackProjectLoader.LoadedProject project, String animationName) {
        AnimationHitConfig animConfig = project.hitConfig().animations().get(animationName);
        if (animConfig == null) {
            return List.of();
        }
        ArrayList<SegmentState> result = new ArrayList<SegmentState>(animConfig.segments().size());
        for (HitSegment config : animConfig.segments()) {
            List<YssAttackProjectLoader.BoneGeometry> bones = project.geometry().selectBones(Set.copyOf(config.bones()));
            result.add(new SegmentState(config, bones, new SegmentHitTracker(config.hitReset())));
        }
        return List.copyOf(result);
    }

    private static List<CasterMoveConfig> buildCasterMoves(YssAttackProjectLoader.LoadedProject project, String animationName) {
        AnimationHitConfig animConfig = project.hitConfig().animations().get(animationName);
        return animConfig == null ? List.of() : List.copyOf(animConfig.casterMoves());
    }

    private static HoverConfig buildHover(YssAttackProjectLoader.LoadedProject project, String animationName) {
        AnimationHitConfig animConfig = project.hitConfig().animations().get(animationName);
        return animConfig == null ? null : animConfig.hover();
    }

    private static boolean buildRootMotion(YssAttackProjectLoader.LoadedProject project, String animationName) {
        AnimationHitConfig animConfig = project.hitConfig().animations().get(animationName);
        return animConfig != null && animConfig.rootMotion();
    }

    public boolean matches(YSMClientAccess.ControllerSlotInfo info) {
        return this.controllerSlot == info.slotIndex() && this.controllerName.equals(info.name()) && this.animationName.equals(info.animationName());
    }

    public boolean matches(String controller, int slot, String animation) {
        return this.controllerSlot == slot && this.controllerName.equals(controller) && this.animationName.equals(animation);
    }

    public List<SegmentHit> tickJudge(PlayerAnimatableCapability capability, LocalPlayer player, float renderTicks, float partialTick) {
        this.advanceAnimSeconds(renderTicks);
        if (capability.update(partialTick) == null) {
            return List.of();
        }
        AnimatedGeoModel animatedModel = capability.getLoadedGeoModel();
        if (animatedModel == null) {
            return List.of();
        }
        float animationSeconds = this.animSeconds;
        Vec3 origin = this.resolveRenderOrigin(player, partialTick);
        float bodyYaw = this.resolveRenderBodyYaw((LivingEntity)player, partialTick);
        ArrayList<SegmentHit> result = new ArrayList<SegmentHit>();
        for (int index = 0; index < this.segments.size(); ++index) {
            SegmentState segment = this.segments.get(index);
            if (!SegmentHitTracker.isActive(segment.config().time(), animationSeconds)) continue;
            YssAttackModelSampler sampler = this.sampleSegment(segment, capability, animatedModel, origin, bodyYaw);
            Set<Integer> insideIds = this.selectInsideTargetIds(player, sampler.hitBoxes());
            Set<Integer> newHits = segment.tracker().resolveHits(animationSeconds, insideIds);
            if (newHits.isEmpty()) continue;
            result.add(new SegmentHit(index, newHits));
        }
        return result;
    }

    public List<CasterMoveRequest> tickCasterMoves() {
        if (this.casterMoves.isEmpty()) {
            return List.of();
        }
        ArrayList<CasterMoveRequest> result = new ArrayList<CasterMoveRequest>();
        for (int index = 0; index < this.casterMoves.size(); ++index) {
            CasterMoveConfig move = this.casterMoves.get(index);
            if (!SegmentHitTracker.isActive(move.time(), this.animSeconds) || move.type() == CasterMoveType.IMPULSE && !this.firedImpulseMoves.add(index)) continue;
            result.add(new CasterMoveRequest(index, this.animSeconds));
        }
        return result;
    }

    public SegmentDebugGeometry sampleDebugGeometry(PlayerAnimatableCapability capability, LocalPlayer player, float renderTicks, float partialTick) {
        this.advanceAnimSeconds(renderTicks);
        Vec3 origin = this.resolveRenderOrigin(player, partialTick);
        if (capability.update(partialTick) == null) {
            return new SegmentDebugGeometry(origin, List.of());
        }
        AnimatedGeoModel animatedModel = capability.getLoadedGeoModel();
        if (animatedModel == null) {
            return new SegmentDebugGeometry(origin, List.of());
        }
        float bodyYaw = this.resolveRenderBodyYaw((LivingEntity)player, partialTick);
        ArrayList<SegmentDebugBox> boxes = new ArrayList<SegmentDebugBox>();
        for (int index = 0; index < this.segments.size(); ++index) {
            SegmentState segment = this.segments.get(index);
            if (!SegmentHitTracker.isActive(segment.config().time(), this.animSeconds)) continue;
            YssAttackModelSampler sampler = this.sampleSegment(segment, capability, animatedModel, origin, bodyYaw);
            boxes.add(new SegmentDebugBox(index, sampler.hitBoxes(), sampler.hitLines()));
        }
        return new SegmentDebugGeometry(origin, boxes);
    }

    private YssAttackModelSampler sampleSegment(SegmentState segment, PlayerAnimatableCapability capability, AnimatedGeoModel animatedModel, Vec3 origin, float bodyYaw) {
        float[] scales = YssAttackRuntime.modelScales(capability);
        Set zeroPositionBones = this.rootMotion && this.rootBonePooledName >= 0 ? Set.of((Object)this.rootBonePooledName) : Set.of();
        YssAttackModelSampler sampler = new YssAttackModelSampler(this.project.geometry(), segment.bones(), animatedModel, origin, bodyYaw, scales[0], scales[1], zeroPositionBones);
        sampler.sample();
        return sampler;
    }

    private static float[] modelScales(PlayerAnimatableCapability capability) {
        float widthScale = 1.0f;
        float heightScale = 1.0f;
        ModelRenderTarget renderTarget = capability.getModelRenderTarget();
        if (renderTarget != null && renderTarget.info() != null && renderTarget.info().getPlayerSettings() != null) {
            widthScale = renderTarget.info().getPlayerSettings().widthScale();
            heightScale = renderTarget.info().getPlayerSettings().heightScale();
        }
        return new float[]{widthScale, heightScale};
    }

    private void advanceAnimSeconds(float renderTicks) {
        if (this.lastRenderTicks < 0.0f) {
            this.lastRenderTicks = renderTicks;
            return;
        }
        float delta = renderTicks - this.lastRenderTicks;
        if (delta > 0.0f) {
            this.animSeconds += delta / 20.0f;
        }
        this.lastRenderTicks = renderTicks;
    }

    public float animSeconds() {
        return this.animSeconds;
    }

    public Map<Integer, Integer> activeSegmentBones() {
        LinkedHashMap<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();
        for (int index = 0; index < this.segments.size(); ++index) {
            SegmentState segment = this.segments.get(index);
            if (!SegmentHitTracker.isActive(segment.config().time(), this.animSeconds)) continue;
            for (YssAttackProjectLoader.BoneGeometry bone : segment.bones()) {
                result.putIfAbsent(bone.pooledName(), index);
            }
        }
        return result;
    }

    private Set<Integer> selectInsideTargetIds(LocalPlayer player, List<AABB> hitBoxes) {
        if (hitBoxes.isEmpty()) {
            return Set.of();
        }
        double reach = player.m_7500_() ? 6.0 : 3.0;
        double reachSqr = reach * reach;
        LinkedHashMap<Integer, LivingEntity> targets = new LinkedHashMap<Integer, LivingEntity>();
        for (AABB hitBox : hitBoxes) {
            for (Entity entity2 : player.m_9236_().m_6249_((Entity)player, hitBox.m_82400_(0.05), entity -> this.canAttack(player, (Entity)entity, reachSqr))) {
                if (!(entity2 instanceof LivingEntity)) continue;
                LivingEntity livingEntity = (LivingEntity)entity2;
                targets.putIfAbsent(livingEntity.m_19879_(), livingEntity);
            }
        }
        return new LinkedHashSet<Integer>(targets.keySet());
    }

    private boolean canAttack(LocalPlayer player, Entity entity, double reachSqr) {
        return entity instanceof LivingEntity && entity.m_6084_() && !entity.m_213877_() && entity != player && player.m_20280_(entity) <= reachSqr && player.m_142582_(entity);
    }

    private float resolveRenderBodyYaw(LivingEntity livingEntity, float partialTick) {
        Entity entity;
        boolean shouldSit;
        float yaw = Mth.m_14189_((float)partialTick, (float)livingEntity.f_20884_, (float)livingEntity.f_20883_);
        float headYaw = Mth.m_14189_((float)partialTick, (float)livingEntity.f_20886_, (float)livingEntity.f_20885_);
        boolean bl = shouldSit = livingEntity.m_20159_() && livingEntity.m_20202_() != null && livingEntity.m_20202_().shouldRiderSit();
        if (shouldSit && (entity = livingEntity.m_20202_()) instanceof LivingEntity) {
            LivingEntity vehicle = (LivingEntity)entity;
            yaw = Mth.m_14189_((float)partialTick, (float)vehicle.f_20884_, (float)vehicle.f_20883_);
            float wrappedYawDiff = Mth.m_14036_((float)Mth.m_14177_((float)(headYaw - yaw)), (float)-85.0f, (float)85.0f);
            yaw = headYaw - wrappedYawDiff;
            if (wrappedYawDiff * wrappedYawDiff > 2500.0f) {
                yaw += wrappedYawDiff * 0.2f;
            }
        }
        return yaw;
    }

    private Vec3 resolveRenderOrigin(LocalPlayer player, float partialTick) {
        double originX = Mth.m_14139_((double)partialTick, (double)player.f_19790_, (double)player.m_20185_());
        double originY = Mth.m_14139_((double)partialTick, (double)player.f_19791_, (double)player.m_20186_());
        double originZ = Mth.m_14139_((double)partialTick, (double)player.f_19792_, (double)player.m_20189_());
        Vec3 renderOffset = Minecraft.m_91087_().m_91290_().m_114382_((Entity)player).m_7860_((Entity)player, partialTick);
        return new Vec3(originX, originY, originZ).m_82549_(renderOffset);
    }

    public String modelId() {
        return this.modelId;
    }

    public String controllerName() {
        return this.controllerName;
    }

    public int controllerSlot() {
        return this.controllerSlot;
    }

    public String animationName() {
        return this.animationName;
    }

    public HoverConfig hover() {
        return this.hover;
    }

    private record SegmentState(HitSegment config, List<YssAttackProjectLoader.BoneGeometry> bones, SegmentHitTracker tracker) {
    }

    public record SegmentHit(int segmentIndex, Set<Integer> targetIds) {
    }

    public record CasterMoveRequest(int moveIndex, float clientAnimTime) {
    }

    public record SegmentDebugGeometry(Vec3 origin, List<SegmentDebugBox> activeSegments) {
    }

    public record SegmentDebugBox(int segmentIndex, List<AABB> boxes, List<YssAttackModelSampler.LineSegment> lines) {
    }
}

