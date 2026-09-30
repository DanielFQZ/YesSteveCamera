/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
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
 */
package io.github.tt432.yessteveskill.yss.attack;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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

@AutoRegisterCapability
public class YssAttackModeCapability
implements INBTSerializable<CompoundTag> {
    public static final Capability<YssAttackModeCapability> CAPABILITY = CapabilityManager.get((CapabilityToken)new CapabilityToken<YssAttackModeCapability>(){});
    private static final ResourceLocation KEY = new ResourceLocation("yessteveskill", "attack_mode");
    private boolean enabled;
    @Nullable
    private String modelId;

    public boolean enabled() {
        return this.enabled;
    }

    public boolean setEnabled(boolean enabled) {
        boolean changed = this.enabled != enabled;
        this.enabled = enabled;
        return changed;
    }

    @Nullable
    public String modelId() {
        return this.modelId;
    }

    public void setModelId(@Nullable String modelId) {
        this.modelId = modelId;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.m_128379_("enabled", this.enabled);
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.enabled = nbt.m_128471_("enabled");
    }

    @Nullable
    public static YssAttackModeCapability get(Player player) {
        return (YssAttackModeCapability)player.getCapability(CAPABILITY).orElse(null);
    }

    public static boolean isEnabled(Player player) {
        YssAttackModeCapability capability = YssAttackModeCapability.get(player);
        return capability != null && capability.enabled();
    }

    public static boolean setEnabled(Player player, boolean enabled) {
        YssAttackModeCapability capability = YssAttackModeCapability.get(player);
        return capability != null && capability.setEnabled(enabled);
    }

    @Nullable
    public static String getCachedModelId(Player player) {
        YssAttackModeCapability capability = YssAttackModeCapability.get(player);
        return capability != null ? capability.modelId() : null;
    }

    public static void setCachedModelId(Player player, @Nullable String modelId) {
        YssAttackModeCapability capability = YssAttackModeCapability.get(player);
        if (capability != null) {
            capability.setModelId(modelId);
        }
    }

    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            Provider provider = new Provider();
            event.addCapability(KEY, (ICapabilityProvider)provider);
            event.addListener(() -> provider.optional.invalidate());
        }
    }

    private static final class Provider
    implements ICapabilitySerializable<CompoundTag> {
        private final YssAttackModeCapability backend = new YssAttackModeCapability();
        private final LazyOptional<YssAttackModeCapability> optional = LazyOptional.of(() -> this.backend);

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

