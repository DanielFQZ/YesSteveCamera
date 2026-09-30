/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.common.ForgeMod
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.combat;

import io.github.tt432.yessteveskill.combat.YssCombatConfigLoader;
import io.github.tt432.yessteveskill.combat.config.HitProjectConfig;
import io.github.tt432.yessteveskill.combat.config.MovementConfig;
import io.github.tt432.yessteveskill.yss.attack.YssAttackModeCapability;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

public final class YssMovementAttributeService {
    private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString("7c9e2b3a-5f8d-4a1b-9e6c-2d7f0a3b5c81");
    private static final UUID SWIM_MODIFIER_ID = UUID.fromString("3a1f7d92-4b6e-4c58-8f0a-1e9b3d6c2475");
    private static final double EPSILON = 1.0E-4;

    private YssMovementAttributeService() {
    }

    public static void tick(ServerPlayer player) {
        MovementConfig movement = YssMovementAttributeService.resolveMovement(player);
        if (movement == null || movement.isDefault()) {
            YssMovementAttributeService.clearModifier(player, Attributes.f_22279_, MOVEMENT_MODIFIER_ID);
            YssMovementAttributeService.clearModifier(player, (Attribute)ForgeMod.SWIM_SPEED.get(), SWIM_MODIFIER_ID);
            return;
        }
        YssMovementAttributeService.applyMultiplier(player, Attributes.f_22279_, MOVEMENT_MODIFIER_ID, "yss_land_speed", YssMovementAttributeService.landMultiplier(player, movement));
        YssMovementAttributeService.applyMultiplier(player, (Attribute)ForgeMod.SWIM_SPEED.get(), SWIM_MODIFIER_ID, "yss_swim_speed", player.m_20069_() ? movement.swim() : 1.0f);
    }

    public static float jumpVelocityMultiplier(@Nullable MovementConfig movement) {
        if (movement == null || movement.jumpHeight() == 1.0f) {
            return 1.0f;
        }
        return (float)Math.sqrt(movement.jumpHeight());
    }

    @Nullable
    public static MovementConfig resolveMovement(ServerPlayer player) {
        String modelId = YssAttackModeCapability.getCachedModelId((Player)player);
        if (modelId == null) {
            return null;
        }
        return YssCombatConfigLoader.resolveProject(modelId).map(HitProjectConfig::movement).orElse(null);
    }

    private static float landMultiplier(ServerPlayer player, MovementConfig movement) {
        if (player.m_20069_() || player.m_6147_()) {
            return 1.0f;
        }
        if (player.m_20089_() == Pose.SWIMMING) {
            return movement.crawl();
        }
        if (player.m_6047_()) {
            return movement.sneak();
        }
        if (player.m_20142_()) {
            return movement.run();
        }
        return movement.walk();
    }

    private static void applyMultiplier(ServerPlayer player, Attribute attribute, UUID modifierId, String name, float multiplier) {
        AttributeInstance instance = player.m_21051_(attribute);
        if (instance == null) {
            return;
        }
        double amount = (double)multiplier - 1.0;
        if (Math.abs(amount) < 1.0E-4) {
            YssMovementAttributeService.clearModifier(player, attribute, modifierId);
            return;
        }
        AttributeModifier existing = instance.m_22111_(modifierId);
        if (existing != null && existing.m_22218_() == amount) {
            return;
        }
        instance.m_22120_(modifierId);
        instance.m_22118_(new AttributeModifier(modifierId, name, amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    private static void clearModifier(ServerPlayer player, Attribute attribute, UUID modifierId) {
        AttributeInstance instance = player.m_21051_(attribute);
        if (instance != null && instance.m_22111_(modifierId) != null) {
            instance.m_22120_(modifierId);
        }
    }
}

