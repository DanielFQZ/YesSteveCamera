/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Options
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.client.settings.KeyModifier
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.Nullable;

public final class YssCustomKeys {
    public static final int CUSTOM_KEY_COUNT = 10;
    public static final int TOTAL_KEY_COUNT = 12;
    private static final String CATEGORY = "key.category.yessteveskill";
    public static final List<KeyMapping> CUSTOM_KEYS = YssCustomKeys.createCustomKeys();

    private YssCustomKeys() {
    }

    private static List<KeyMapping> createCustomKeys() {
        Object[] keys = new KeyMapping[10];
        for (int i = 0; i < 10; ++i) {
            keys[i] = new KeyMapping("key.yessteveskill.custom_key_" + (i + 1), (IKeyConflictContext)KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.f_84822_, CATEGORY);
        }
        return List.of((Object[])keys);
    }

    @Nullable
    public static KeyMapping getByIndex(int index) {
        if (index < 0 || index >= 12) {
            return null;
        }
        Options options = Minecraft.m_91087_().f_91066_;
        return switch (index) {
            case 0 -> options.f_92096_;
            case 1 -> options.f_92095_;
            default -> CUSTOM_KEYS.get(index - 2);
        };
    }
}

