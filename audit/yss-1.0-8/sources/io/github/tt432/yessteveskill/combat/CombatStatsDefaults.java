/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.combat;

import io.github.tt432.yessteveskill.combat.config.EntityCombatStats;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public final class CombatStatsDefaults {
    private static final Map<ResourceLocation, EntityCombatStats> TABLE = new ConcurrentHashMap<ResourceLocation, EntityCombatStats>();

    private CombatStatsDefaults() {
    }

    public static void register(ResourceLocation typeId, EntityCombatStats stats) {
        TABLE.put(typeId, stats);
    }

    @Nullable
    public static EntityCombatStats get(EntityType<?> type) {
        return CombatStatsDefaults.get(BuiltInRegistries.f_256780_.m_7981_(type));
    }

    @Nullable
    public static EntityCombatStats get(ResourceLocation typeId) {
        return TABLE.get(typeId);
    }
}

