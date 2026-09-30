/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.LocalPlayer
 */
package io.github.tt432.yessteveskill.yss;

import io.github.tt432.yessteveskill.yss.YssAnimationKey;
import net.minecraft.client.player.LocalPlayer;

public record YssAnimationFrame(LocalPlayer player, YssAnimationKey key, String controllerName, float animationTicks, float animationSeconds, float animationLength, float partialTick) {
}

