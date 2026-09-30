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

import io.github.tt432.yessteveskill.combat.CombatState;
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
public class CombatStateCapability
implements INBTSerializable<CompoundTag> {
    public static final Capability<CombatStateCapability> CAPABILITY = CapabilityManager.get((CapabilityToken)new CapabilityToken<CombatStateCapability>(){});
    private static final ResourceLocation KEY = new ResourceLocation("yessteveskill", "combat_state");
    private final CombatState state = new CombatState();

    public CombatState getState() {
        return this.state;
    }

    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    public void deserializeNBT(CompoundTag nbt) {
    }

    @Nullable
    public static CombatStateCapability get(LivingEntity entity) {
        return (CombatStateCapability)entity.getCapability(CAPABILITY).orElse(null);
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
        private final CombatStateCapability backend = new CombatStateCapability();
        private final LazyOptional<CombatStateCapability> optional = LazyOptional.of(() -> this.backend);

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

