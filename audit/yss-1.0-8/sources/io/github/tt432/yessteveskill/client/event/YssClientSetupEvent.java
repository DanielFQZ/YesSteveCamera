/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package io.github.tt432.yessteveskill.client.event;

import io.github.tt432.yessteveskill.client.dnd.DragDropManager;
import io.github.tt432.yessteveskill.client.input.YssCustomKeys;
import io.github.tt432.yessteveskill.client.input.YssProjectScreenKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="yessteveskill", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class YssClientSetupEvent {
    private YssClientSetupEvent() {
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(YssProjectScreenKey.OPEN_PROJECT_EXPORT_SCREEN_KEY);
        YssCustomKeys.CUSTOM_KEYS.forEach(arg_0 -> ((RegisterKeyMappingsEvent)event).register(arg_0));
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> DragDropManager.INSTANCE.install());
    }
}

