/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.client.settings.KeyModifier
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package io.github.tt432.yessteveskill.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.tt432.yessteveskill.client.gui.YssProjectExportScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="yessteveskill", value={Dist.CLIENT})
public class YssProjectScreenKey {
    public static final KeyMapping OPEN_PROJECT_EXPORT_SCREEN_KEY = new KeyMapping("key.yessteveskill.open_project_export.desc", (IKeyConflictContext)KeyConflictContext.IN_GAME, KeyModifier.ALT, InputConstants.Type.KEYSYM, 77, "key.category.yessteveskill");

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (!YssProjectScreenKey.isInGame(minecraft)) {
            return;
        }
        if (event.getAction() != 1) {
            return;
        }
        if (!OPEN_PROJECT_EXPORT_SCREEN_KEY.m_90832_(event.getKey(), event.getScanCode())) {
            return;
        }
        if (!OPEN_PROJECT_EXPORT_SCREEN_KEY.getKeyModifier().equals((Object)KeyModifier.getActiveModifier())) {
            return;
        }
        Minecraft.m_91087_().m_91152_((Screen)new YssProjectExportScreen());
    }

    private static boolean isInGame(Minecraft minecraft) {
        if (minecraft.m_91265_() != null) {
            return false;
        }
        if (minecraft.f_91080_ != null) {
            return false;
        }
        if (!minecraft.f_91067_.m_91600_()) {
            return false;
        }
        if (!minecraft.m_91302_()) {
            return false;
        }
        return minecraft.f_91074_ != null && minecraft.f_91073_ != null;
    }
}

