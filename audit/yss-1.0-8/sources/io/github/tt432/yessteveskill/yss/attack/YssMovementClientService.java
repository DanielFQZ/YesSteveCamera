/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapability
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingJumpEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.yss.attack;

import com.elfmcys.ysm.capability.PlayerAnimatableCapability;
import io.github.tt432.yessteveskill.combat.YssCombatConfigLoader;
import io.github.tt432.yessteveskill.combat.YssMovementAttributeService;
import io.github.tt432.yessteveskill.combat.config.HitProjectConfig;
import io.github.tt432.yessteveskill.combat.config.MovementConfig;
import io.github.tt432.yessteveskill.network.YssNetwork;
import io.github.tt432.yessteveskill.ysm.YSMClientAccess;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

public class YssMovementClientService {
    private final YSMClientAccess access;
    @Nullable
    private String lastSyncedModelId;

    public YssMovementClientService(YSMClientAccess access) {
        this.access = access;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        String modelId;
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        LocalPlayer player = minecraft.f_91074_;
        if (player == null || minecraft.f_91073_ == null) {
            this.lastSyncedModelId = null;
            return;
        }
        PlayerAnimatableCapability capability = this.access.getCapability((AbstractClientPlayer)player);
        String string = modelId = capability == null ? null : this.access.getModelId(capability);
        if (modelId != null && !Objects.equals(modelId, this.lastSyncedModelId)) {
            YssNetwork.sendModelIdSync(modelId);
        }
        this.lastSyncedModelId = modelId;
        this.applyClimbCompensation(player, modelId);
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LocalPlayer player;
        Minecraft minecraft = Minecraft.m_91087_();
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof LocalPlayer) || (player = (LocalPlayer)livingEntity) != minecraft.f_91074_) {
            return;
        }
        PlayerAnimatableCapability capability = this.access.getCapability((AbstractClientPlayer)player);
        String modelId = capability == null ? null : this.access.getModelId(capability);
        float multiplier = YssMovementAttributeService.jumpVelocityMultiplier(YssMovementClientService.resolveMovement(modelId));
        if (multiplier == 1.0f) {
            return;
        }
        Vec3 delta = player.m_20184_();
        player.m_20334_(delta.f_82479_, delta.f_82480_ * (double)multiplier, delta.f_82481_);
    }

    private void applyClimbCompensation(LocalPlayer player, @Nullable String modelId) {
        if (!player.m_6147_()) {
            return;
        }
        double y = player.m_20184_().f_82480_;
        if (Math.abs(y) < 0.01) {
            return;
        }
        MovementConfig movement = YssMovementClientService.resolveMovement(modelId);
        if (movement == null || movement.climb() == 1.0f) {
            return;
        }
        double extra = y * ((double)movement.climb() - 1.0);
        if (extra != 0.0) {
            player.m_6478_(MoverType.SELF, new Vec3(0.0, extra, 0.0));
        }
    }

    @Nullable
    private static MovementConfig resolveMovement(@Nullable String modelId) {
        if (modelId == null) {
            return null;
        }
        return YssCombatConfigLoader.resolveProject(modelId).map(HitProjectConfig::movement).orElse(null);
    }
}

