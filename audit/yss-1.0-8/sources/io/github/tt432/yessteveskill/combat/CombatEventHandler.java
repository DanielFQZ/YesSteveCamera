/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.AttachCapabilitiesEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingJumpEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package io.github.tt432.yessteveskill.combat;

import io.github.tt432.yessteveskill.combat.CombatStateCapability;
import io.github.tt432.yessteveskill.combat.CombatStatsCapability;
import io.github.tt432.yessteveskill.combat.YssMovementAttributeService;
import io.github.tt432.yessteveskill.yss.attack.YssAttackServerExecutor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CombatEventHandler {
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        CombatStatsCapability.attach(event);
        CombatStateCapability.attach(event);
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        boolean interrupted;
        LivingEntity entity = event.getEntity();
        CombatStateCapability cap = CombatStateCapability.get(entity);
        if (entity.m_9236_().m_5776_()) {
            if (cap != null) {
                cap.getState().tick();
            }
            return;
        }
        boolean hitstopped = cap != null && cap.getState().isHitstopped();
        boolean bl = interrupted = cap != null && cap.getState().isInterrupted();
        if (hitstopped || interrupted) {
            entity.m_20334_(0.0, 0.0, 0.0);
            entity.f_19864_ = true;
        }
        if (!hitstopped) {
            YssAttackServerExecutor.tryFirePending(entity);
        }
        if (entity instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)entity;
            YssMovementAttributeService.tick(serverPlayer);
        }
        if (cap != null) {
            cap.getState().tick();
        }
        YssAttackServerExecutor.applyKnockupGravityReduction(entity);
        YssAttackServerExecutor.applyHoverGravityReduction(entity);
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity livingEntity;
        if (event.getEntity().m_9236_().m_5776_() || !((livingEntity = event.getEntity()) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)livingEntity;
        float multiplier = YssMovementAttributeService.jumpVelocityMultiplier(YssMovementAttributeService.resolveMovement(player));
        if (multiplier == 1.0f) {
            return;
        }
        Vec3 delta = player.m_20184_();
        player.m_20334_(delta.f_82479_, delta.f_82480_ * (double)multiplier, delta.f_82481_);
    }
}

