/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapability
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 */
package io.github.tt432.yessteveskill.yss.attack;

import com.elfmcys.ysm.capability.PlayerAnimatableCapability;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.tt432.yessteveskill.combat.CombatStateCapability;
import io.github.tt432.yessteveskill.combat.config.HoverConfig;
import io.github.tt432.yessteveskill.event.YSMAnimationPlayEvent;
import io.github.tt432.yessteveskill.network.YssNetwork;
import io.github.tt432.yessteveskill.ysm.YSMClientAccess;
import io.github.tt432.yessteveskill.yss.attack.YssAttackModeCapability;
import io.github.tt432.yessteveskill.yss.attack.YssAttackModelSampler;
import io.github.tt432.yessteveskill.yss.attack.YssAttackProjectLoader;
import io.github.tt432.yessteveskill.yss.attack.YssAttackRuntime;
import io.github.tt432.yessteveskill.yss.attack.YssEdgeGuard;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class YssAttackService {
    private static final int[][] SEGMENT_COLORS = new int[][]{{255, 0, 0}, {0, 0, 255}, {255, 255, 0}, {0, 255, 255}, {255, 0, 255}, {255, 128, 0}};
    @Nullable
    private static YssAttackService ACTIVE_INSTANCE;
    private final YSMClientAccess access;
    private final YssAttackProjectLoader projectLoader = new YssAttackProjectLoader();
    @Nullable
    private YssAttackRuntime runtime;

    public YssAttackService(YSMClientAccess access) {
        this.access = access;
        ACTIVE_INSTANCE = this;
    }

    @Nullable
    public static YssAttackService activeInstance() {
        return ACTIVE_INSTANCE;
    }

    public boolean hasActiveRuntime() {
        return this.runtime != null;
    }

    @Nullable
    public Map<Integer, int[]> currentBoneHighlights(AbstractClientPlayer player) {
        LocalPlayer localPlayer;
        Minecraft minecraft = Minecraft.m_91087_();
        if (!(player instanceof LocalPlayer) || minecraft.f_91074_ != (localPlayer = (LocalPlayer)player)) {
            return null;
        }
        YssAttackRuntime currentRuntime = this.runtime;
        if (currentRuntime == null) {
            return null;
        }
        PlayerAnimatableCapability capability = this.access.getCapability((AbstractClientPlayer)localPlayer);
        if (capability == null) {
            return null;
        }
        float renderTicks = (float)localPlayer.f_19797_ + minecraft.m_91296_();
        YSMClientAccess.ControllerSlotInfo slotInfo = this.findSlot(capability, currentRuntime, renderTicks);
        if (slotInfo == null || !this.isRuntimePlaying(currentRuntime, slotInfo)) {
            return null;
        }
        Map<Integer, Integer> activeBones = currentRuntime.activeSegmentBones();
        if (activeBones.isEmpty()) {
            return null;
        }
        HashMap<Integer, int[]> result = new HashMap<Integer, int[]>();
        activeBones.forEach((pooledName, segmentIndex) -> result.put((Integer)pooledName, YssAttackService.segmentColor(segmentIndex)));
        return result;
    }

    @Nullable
    public Vec3 currentRootMotionOffset(AbstractClientPlayer player) {
        LocalPlayer localPlayer;
        Minecraft minecraft = Minecraft.m_91087_();
        if (!(player instanceof LocalPlayer) || minecraft.f_91074_ != (localPlayer = (LocalPlayer)player)) {
            return null;
        }
        YssAttackRuntime currentRuntime = this.runtime;
        if (currentRuntime == null || !currentRuntime.rootMotionEnabled()) {
            return null;
        }
        PlayerAnimatableCapability capability = this.access.getCapability((AbstractClientPlayer)localPlayer);
        if (capability == null) {
            return null;
        }
        if (currentRuntime.tailMode()) {
            return currentRuntime.currentRootMotionOffset(capability);
        }
        float renderTicks = (float)localPlayer.f_19797_ + minecraft.m_91296_();
        YSMClientAccess.ControllerSlotInfo slotInfo = this.findSlot(capability, currentRuntime, renderTicks);
        if (slotInfo == null || !this.isRuntimePlaying(currentRuntime, slotInfo)) {
            if (currentRuntime.enterTail(capability)) {
                return currentRuntime.currentRootMotionOffset(capability);
            }
            return null;
        }
        return currentRuntime.currentRootMotionOffset(capability);
    }

    public static boolean shouldCancelVanillaAttack(Minecraft minecraft) {
        YssAttackService service = ACTIVE_INSTANCE;
        if (service == null) {
            return false;
        }
        LocalPlayer player = minecraft.f_91074_;
        if (player == null || minecraft.f_91073_ == null) {
            return false;
        }
        return service.isYssAttackActive(player, minecraft.m_91296_());
    }

    @SubscribeEvent
    public void onAnimationPlay(YSMAnimationPlayEvent event) {
        String modelId;
        LocalPlayer player;
        Minecraft minecraft = Minecraft.m_91087_();
        Player player2 = event.getPlayer();
        if (!(player2 instanceof LocalPlayer) || minecraft.f_91074_ != (player = (LocalPlayer)player2)) {
            return;
        }
        PlayerAnimatableCapability capability = this.access.getCapability((AbstractClientPlayer)player);
        if (!this.refreshAttackModeCapability(player, capability)) {
            this.clearRuntime();
            return;
        }
        String string = modelId = capability == null ? null : this.access.getModelId(capability);
        if (modelId == null || event.getAnimationName().isBlank()) {
            this.clearRuntime();
            return;
        }
        YssAttackProjectLoader.LoadedProject project = this.projectLoader.load(modelId, this.access.getModelAssetsNow(capability));
        if (project == null || !project.attackAnimations().contains(event.getAnimationName())) {
            return;
        }
        if (this.runtime != null && this.runtime.matches(event.getControllerName(), event.getControllerSlot(), event.getAnimationName()) && !event.isReplay()) {
            return;
        }
        this.runtime = new YssAttackRuntime(modelId, event.getControllerName(), event.getControllerSlot(), event.getAnimationName(), project, capability);
        YssNetwork.sendModelIdSync(modelId);
        if (this.runtime.hover() != null) {
            YssNetwork.sendHover(event.getAnimationName());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Vec3 rootMotionDelta;
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        LocalPlayer player = minecraft.f_91074_;
        if (player == null || minecraft.f_91073_ == null) {
            this.clearRuntime();
            return;
        }
        PlayerAnimatableCapability capability = this.access.getCapability((AbstractClientPlayer)player);
        if (!this.refreshAttackModeCapability(player, capability)) {
            this.clearRuntime();
            return;
        }
        YssAttackRuntime currentRuntime = this.runtime;
        if (currentRuntime == null) {
            return;
        }
        if (currentRuntime.tailMode()) {
            if (currentRuntime.tailReleased(currentRuntime.currentRootMotionOffset(capability))) {
                this.clearRuntime();
            }
            return;
        }
        float partialTick = minecraft.m_91296_();
        float renderTicks = (float)player.f_19797_ + partialTick;
        YSMClientAccess.ControllerSlotInfo slotInfo = this.findSlot(capability, currentRuntime, renderTicks);
        if (slotInfo == null || !this.isRuntimePlaying(currentRuntime, slotInfo)) {
            if (!currentRuntime.enterTail(capability)) {
                this.clearRuntime();
            }
            return;
        }
        CombatStateCapability selfCap = CombatStateCapability.get((LivingEntity)player);
        if (selfCap != null && selfCap.getState().isHitstopped()) {
            return;
        }
        HoverConfig hover = currentRuntime.hover();
        if (selfCap != null && selfCap.getState().isHover() && hover != null && hover.blockInput()) {
            player.f_108618_.f_108567_ = 0.0f;
            player.f_108618_.f_108566_ = 0.0f;
        }
        String animName = currentRuntime.animationName();
        float clientAnimTime = currentRuntime.animSeconds();
        List<YssAttackRuntime.SegmentHit> hits = currentRuntime.tickJudge(capability, player, renderTicks, partialTick);
        for (YssAttackRuntime.SegmentHit segmentHit : hits) {
            int[] targetIds = segmentHit.targetIds().stream().mapToInt(Integer::intValue).toArray();
            YssNetwork.sendHitEvent(segmentHit.segmentIndex(), targetIds, animName, clientAnimTime);
        }
        List<YssAttackRuntime.CasterMoveRequest> moves = currentRuntime.tickCasterMoves();
        for (YssAttackRuntime.CasterMoveRequest m : moves) {
            YssNetwork.sendCasterMove(m.moveIndex(), animName, m.clientAnimTime());
        }
        Vec3 vec3 = player.m_20184_();
        Vec3 trimmedCombatDelta = YssEdgeGuard.trimHorizontal((Entity)player, vec3);
        if (trimmedCombatDelta != vec3) {
            player.m_20256_(trimmedCombatDelta);
        }
        if ((rootMotionDelta = currentRuntime.tickRootMotion(capability, player, partialTick, slotInfo.animationTicks())) != null && rootMotionDelta.m_82556_() > 1.0E-12) {
            player.m_6478_(MoverType.SELF, YssEdgeGuard.trimHorizontal((Entity)player, rootMotionDelta));
        }
    }

    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        LocalPlayer player = minecraft.f_91074_;
        if (player == null || minecraft.f_91073_ == null || minecraft.f_91063_.m_109153_() == null) {
            return;
        }
        PlayerAnimatableCapability capability = this.access.getCapability((AbstractClientPlayer)player);
        if (!this.refreshAttackModeCapability(player, capability) || this.runtime == null) {
            return;
        }
        YssAttackRuntime currentRuntime = this.runtime;
        float partialTick = event.getPartialTick();
        float renderTicks = (float)player.f_19797_ + partialTick;
        YSMClientAccess.ControllerSlotInfo slotInfo = this.findSlot(capability, currentRuntime, renderTicks);
        if (slotInfo == null || !this.isRuntimePlaying(currentRuntime, slotInfo)) {
            return;
        }
        YssAttackRuntime.SegmentDebugGeometry geo = currentRuntime.sampleDebugGeometry(capability, player, renderTicks, partialTick);
        if (geo.activeSegments().isEmpty()) {
            return;
        }
        if (!minecraft.m_91290_().m_114377_()) {
            return;
        }
        Vec3 camera = minecraft.f_91063_.m_109153_().m_90583_();
        PoseStack poseStack = event.getPoseStack();
        poseStack.m_85836_();
        poseStack.m_85837_(-camera.f_82479_, -camera.f_82480_, -camera.f_82481_);
        MultiBufferSource.BufferSource bufferSource = minecraft.m_91269_().m_110104_();
        VertexConsumer consumer = bufferSource.m_6299_(RenderType.m_110504_());
        this.renderDebugLine(poseStack, consumer, geo.origin(), geo.origin().m_82520_(0.0, 3.0, 0.0), 0, 255, 0);
        for (YssAttackRuntime.SegmentDebugBox segBox : geo.activeSegments()) {
            int[] rgb = YssAttackService.segmentColor(segBox.segmentIndex());
            for (YssAttackModelSampler.LineSegment line : segBox.lines()) {
                this.renderDebugLine(poseStack, consumer, line.from(), line.to(), rgb[0], rgb[1], rgb[2]);
            }
        }
        bufferSource.m_109912_(RenderType.m_110504_());
        poseStack.m_85849_();
    }

    private void renderDebugLine(PoseStack poseStack, VertexConsumer consumer, Vec3 from, Vec3 to, int red, int green, int blue) {
        double endX = to.f_82479_;
        double startX = from.f_82479_;
        double dx = endX - startX;
        double endY = to.f_82480_;
        double startY = from.f_82480_;
        double dy = endY - startY;
        double endZ = to.f_82481_;
        double startZ = from.f_82481_;
        double dz = endZ - startZ;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length <= 1.0E-6) {
            return;
        }
        float nx = (float)(dx / length);
        float ny = (float)(dy / length);
        float nz = (float)(dz / length);
        Matrix4f pose = poseStack.m_85850_().m_252922_();
        consumer.m_252986_(pose, (float)startX, (float)startY, (float)startZ).m_6122_(red, green, blue, 255).m_5601_(nx, ny, nz).m_5752_();
        consumer.m_252986_(pose, (float)endX, (float)endY, (float)endZ).m_6122_(red, green, blue, 255).m_5601_(nx, ny, nz).m_5752_();
    }

    private static int[] segmentColor(int index) {
        return SEGMENT_COLORS[index % SEGMENT_COLORS.length];
    }

    private boolean isYssAttackActive(LocalPlayer player, float partialTick) {
        YssAttackRuntime currentRuntime = this.runtime;
        if (currentRuntime == null) {
            return false;
        }
        PlayerAnimatableCapability capability = this.access.getCapability((AbstractClientPlayer)player);
        if (!this.refreshAttackModeCapability(player, capability)) {
            return false;
        }
        float renderTicks = (float)player.f_19797_ + partialTick;
        YSMClientAccess.ControllerSlotInfo slotInfo = this.findSlot(capability, currentRuntime, renderTicks);
        return slotInfo != null && this.isRuntimePlaying(currentRuntime, slotInfo);
    }

    private boolean isRuntimePlaying(YssAttackRuntime runtime, YSMClientAccess.ControllerSlotInfo slotInfo) {
        if (!slotInfo.isPlaying()) {
            return false;
        }
        return runtime.looping() || slotInfo.animationLength() <= 0.0f || slotInfo.animationTicks() < slotInfo.animationLength();
    }

    @Nullable
    private YSMClientAccess.ControllerSlotInfo findSlot(PlayerAnimatableCapability capability, YssAttackRuntime currentRuntime, float renderTicks) {
        for (YSMClientAccess.ControllerSlotInfo info : this.access.getControllerSlotInfos(capability, renderTicks)) {
            if (!currentRuntime.matches(info)) continue;
            return info;
        }
        return null;
    }

    private boolean refreshAttackModeCapability(@Nullable LocalPlayer player, @Nullable PlayerAnimatableCapability capability) {
        if (player == null) {
            return false;
        }
        boolean enabled = this.resolveAttackModeEnabled(capability);
        boolean changed = YssAttackModeCapability.setEnabled((Player)player, enabled);
        if (changed) {
            YssNetwork.sendAttackModeSync(enabled);
        }
        return enabled;
    }

    private boolean resolveAttackModeEnabled(@Nullable PlayerAnimatableCapability capability) {
        if (capability == null) {
            return false;
        }
        this.access.requestModelAssets(capability);
        String modelId = this.access.getModelId(capability);
        return modelId != null && this.projectLoader.hasHitConfig(modelId);
    }

    private void clearRuntime() {
        this.runtime = null;
    }
}

