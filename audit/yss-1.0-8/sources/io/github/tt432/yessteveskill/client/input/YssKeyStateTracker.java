/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.client.KeyMapping
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package io.github.tt432.yessteveskill.client.input;

import io.github.tt432.yessteveskill.client.input.YssCustomKeys;
import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="yessteveskill", value={Dist.CLIENT})
public final class YssKeyStateTracker {
    private static final int SLOT_COUNT = 12;
    private static final boolean[] down = new boolean[12];
    private static final long[] pressStartMs = new long[12];
    private static final float[] lastHoldSeconds = new float[12];
    private static final long[] downEdgeTick = new long[12];
    private static final long[] upEdgeTick = new long[12];
    private static long tickCounter;

    private YssKeyStateTracker() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        YssKeyStateTracker.tick();
    }

    private static void tick() {
        ++tickCounter;
        long now = Util.m_137550_();
        for (int i = 0; i < 12; ++i) {
            KeyMapping key = YssCustomKeys.getByIndex(i);
            if (key == null) continue;
            boolean pressed = key.m_90857_();
            if (pressed && !down[i]) {
                YssKeyStateTracker.down[i] = true;
                YssKeyStateTracker.pressStartMs[i] = now;
                YssKeyStateTracker.downEdgeTick[i] = tickCounter;
                continue;
            }
            if (pressed || !down[i]) continue;
            YssKeyStateTracker.down[i] = false;
            YssKeyStateTracker.lastHoldSeconds[i] = (float)(now - pressStartMs[i]) / 1000.0f;
            YssKeyStateTracker.upEdgeTick[i] = tickCounter;
        }
    }

    public static float getKeyDuration(int key) {
        if (!YssKeyStateTracker.isValidKey(key)) {
            return 0.0f;
        }
        if (down[key]) {
            return (float)(Util.m_137550_() - pressStartMs[key]) / 1000.0f;
        }
        return lastHoldSeconds[key];
    }

    public static boolean isKeyDownEdge(int key) {
        return YssKeyStateTracker.isValidKey(key) && downEdgeTick[key] == tickCounter;
    }

    public static boolean isKeyUpEdge(int key) {
        return YssKeyStateTracker.isValidKey(key) && upEdgeTick[key] == tickCounter;
    }

    private static boolean isValidKey(int key) {
        return key >= 0 && key < 12;
    }

    static {
        Arrays.fill(downEdgeTick, -1L);
        Arrays.fill(upEdgeTick, -1L);
    }
}

