/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.event.AttachCapabilitiesEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package io.github.tt432.yessteveskill.yss.attack;

import io.github.tt432.yessteveskill.yss.attack.YssAttackModeCapability;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class YssAttackModeEventHandler {
    private static final ThreadLocal<Boolean> CUSTOM_ATTACK_CONTEXT = ThreadLocal.withInitial(() -> false);

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        YssAttackModeCapability.attach(event);
    }

    public static void withCustomAttackContext(Runnable action) {
        boolean previous = CUSTOM_ATTACK_CONTEXT.get();
        CUSTOM_ATTACK_CONTEXT.set(true);
        try {
            action.run();
        }
        finally {
            CUSTOM_ATTACK_CONTEXT.set(previous);
        }
    }
}

