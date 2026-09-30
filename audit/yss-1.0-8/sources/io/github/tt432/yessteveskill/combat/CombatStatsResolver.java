/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.combat;

import io.github.tt432.yessteveskill.combat.CombatStatsCapability;
import io.github.tt432.yessteveskill.combat.CombatStatsDefaults;
import io.github.tt432.yessteveskill.combat.config.EntityCombatStats;
import io.github.tt432.yessteveskill.combat.config.HitProjectConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public final class CombatStatsResolver {
    private CombatStatsResolver() {
    }

    @Nullable
    public static EntityCombatStats resolveStats(LivingEntity entity, @Nullable HitProjectConfig project) {
        CombatStatsCapability cap = CombatStatsCapability.get(entity);
        if (cap != null && cap.hasCustom()) {
            return cap.getCustom();
        }
        if (project != null) {
            String typeId = CombatStatsResolver.entityTypeKey(entity.m_6095_());
            EntityCombatStats override = project.entityOverrides().get(typeId);
            if (override != null) {
                return override;
            }
        }
        return CombatStatsDefaults.get(entity.m_6095_());
    }

    public static String entityTypeKey(EntityType<?> type) {
        ResourceLocation rl = BuiltInRegistries.f_256780_.m_7981_(type);
        return rl == null ? "" : rl.toString();
    }
}

