/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.common.capabilities.AutoRegisterCapability
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.CapabilityManager
 *  net.minecraftforge.common.capabilities.CapabilityToken
 *  net.minecraftforge.common.capabilities.ICapabilityProvider
 *  net.minecraftforge.common.capabilities.ICapabilitySerializable
 *  net.minecraftforge.common.util.INBTSerializable
 *  net.minecraftforge.common.util.LazyOptional
 *  net.minecraftforge.event.AttachCapabilitiesEvent
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.combat;

import io.github.tt432.yessteveskill.combat.config.EntityCombatStats;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCapability
public class CombatStatsCapability
implements INBTSerializable<CompoundTag> {
    public static final Capability<CombatStatsCapability> CAPABILITY = CapabilityManager.get((CapabilityToken)new CapabilityToken<CombatStatsCapability>(){});
    private static final ResourceLocation KEY = new ResourceLocation("yessteveskill", "combat_stats");
    @Nullable
    private EntityCombatStats custom;

    @Nullable
    public EntityCombatStats getCustom() {
        return this.custom;
    }

    public void setCustom(@Nullable EntityCombatStats custom) {
        this.custom = custom;
    }

    public boolean hasCustom() {
        return this.custom != null;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.m_128379_("has_custom", this.hasCustom());
        if (this.custom != null) {
            tag.m_128350_("anti_interrupt", this.custom.antiInterrupt());
            tag.m_128350_("weight", this.custom.weight());
            tag.m_128350_("hardness", this.custom.hardness());
        }
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.custom = nbt.m_128471_("has_custom") ? new EntityCombatStats(nbt.m_128457_("anti_interrupt"), nbt.m_128457_("weight"), nbt.m_128457_("hardness")) : null;
    }

    @Nullable
    public static CombatStatsCapability get(LivingEntity entity) {
        return (CombatStatsCapability)entity.getCapability(CAPABILITY).orElse(null);
    }

    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity) {
            Provider provider = new Provider();
            event.addCapability(KEY, (ICapabilityProvider)provider);
            event.addListener(() -> provider.optional.invalidate());
        }
    }

    private static final class Provider
    implements ICapabilitySerializable<CompoundTag> {
        private final CombatStatsCapability backend = new CombatStatsCapability();
        private final LazyOptional<CombatStatsCapability> optional = LazyOptional.of(() -> this.backend);

        private Provider() {
        }

        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return CAPABILITY.orEmpty(cap, this.optional);
        }

        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }
}

