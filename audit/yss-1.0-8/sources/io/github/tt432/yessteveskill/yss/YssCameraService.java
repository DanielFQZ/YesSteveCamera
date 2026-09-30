/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapability
 *  com.google.gson.JsonObject
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.ViewportEvent$ComputeCameraAngles
 *  net.minecraftforge.client.event.ViewportEvent$ComputeFov
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.yss;

import com.elfmcys.ysm.capability.PlayerAnimatableCapability;
import com.google.gson.JsonObject;
import io.github.tt432.yessteveskill.event.YSMAnimationPlayEvent;
import io.github.tt432.yessteveskill.ysm.YSMClientAccess;
import io.github.tt432.yessteveskill.yss.YssAnimationKey;
import io.github.tt432.yessteveskill.yss.YssCameraAccess;
import io.github.tt432.yessteveskill.yss.YssViewModifier;
import io.github.tt432.yessteveskill.yss.camera.YssCameraExtensionType;
import io.github.tt432.yessteveskill.yss.extension.YssExtensionFileLoader;
import io.github.tt432.yessteveskill.yss.runtime.YssCameraRuntime;
import io.github.tt432.yessteveskill.yss.runtime.YssCameraRuntimeSample;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid="yessteveskill", value={Dist.CLIENT})
public class YssCameraService {
    private static final YssCameraExtensionType CAMERA_EXTENSION = new YssCameraExtensionType();
    private static final YssExtensionFileLoader LOADER = new YssExtensionFileLoader();
    private static YSMClientAccess access;
    private static int lastPlayerId;
    private static int lastTick;
    private static int lastPartialBits;
    private static YssViewModifier lastModifier;
    private static YssCameraRuntime runtime;

    public static void initialize(YSMClientAccess clientAccess) {
        access = clientAccess;
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        YssViewModifier modifier = YssCameraService.resolveModifier(event.getCamera(), (float)event.getPartialTick());
        if (modifier == null) {
            return;
        }
        if (modifier.position() != null) {
            YssCameraAccess.move(event.getCamera(), modifier.position().x, modifier.position().y, modifier.position().z);
        }
        if (modifier.rotation() != null) {
            event.setPitch(event.getPitch() + modifier.rotation().x);
            event.setYaw(event.getYaw() + modifier.rotation().y);
            event.setRoll(event.getRoll() + modifier.rotation().z);
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        YssViewModifier modifier = YssCameraService.resolveModifier(event.getCamera(), (float)event.getPartialTick());
        if (modifier != null && modifier.fov() != null) {
            event.setFOV((double)modifier.fov().floatValue());
        }
    }

    @SubscribeEvent
    public static void onAnimationPlay(YSMAnimationPlayEvent event) {
        YssCameraRuntime newRuntime;
        LocalPlayer player;
        Minecraft minecraft = Minecraft.m_91087_();
        Player player2 = event.getPlayer();
        if (!(player2 instanceof LocalPlayer) || minecraft.f_91074_ != (player = (LocalPlayer)player2)) {
            return;
        }
        if (access == null) {
            return;
        }
        PlayerAnimatableCapability capability = access.getCapability((AbstractClientPlayer)player);
        if (capability == null) {
            YssCameraService.clearRuntime();
            return;
        }
        String modelId = access.getModelId(capability);
        if (modelId == null || event.getAnimationName().isBlank()) {
            YssCameraService.clearRuntime();
            return;
        }
        YssAnimationKey key = new YssAnimationKey(modelId, event.getAnimationName());
        YssExtensionFileLoader.LoadedAnimation loadedAnimation = LOADER.loadAnimation(key);
        runtime = newRuntime = YssCameraService.createRuntime(key, event.getControllerName(), loadedAnimation);
        YssCameraService.resetFrameCache();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || runtime == null) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ == null || minecraft.f_91073_ == null) {
            YssCameraService.clearRuntime();
            return;
        }
        runtime.tick();
        YssCameraService.resetFrameCache();
    }

    @Nullable
    private static YssViewModifier resolveModifier(Camera camera, float partialTick) {
        Minecraft minecraft = Minecraft.m_91087_();
        LocalPlayer player = minecraft.f_91074_;
        if (player == null) {
            YssCameraService.clearCache();
            return null;
        }
        if (camera.m_90592_() != player) {
            return null;
        }
        if (access == null) {
            return null;
        }
        int partialBits = Float.floatToIntBits(partialTick);
        if (player.m_19879_() == lastPlayerId && player.f_19797_ == lastTick && partialBits == lastPartialBits) {
            return lastModifier;
        }
        lastPlayerId = player.m_19879_();
        lastTick = player.f_19797_;
        lastPartialBits = partialBits;
        lastModifier = YssCameraService.computeModifier(player, partialTick);
        return lastModifier;
    }

    @Nullable
    private static YssViewModifier computeModifier(LocalPlayer player, float partialTick) {
        if (runtime == null) {
            return null;
        }
        PlayerAnimatableCapability capability = access.getCapability((AbstractClientPlayer)player);
        if (capability == null) {
            YssCameraService.clearRuntime();
            return null;
        }
        float renderTicks = (float)player.f_19797_ + partialTick;
        List<YSMClientAccess.ControllerInfo> infos = access.getControllerInfos(capability, renderTicks);
        if (infos == null || infos.isEmpty()) {
            return null;
        }
        ArrayList<YSMClientAccess.ControllerInfo> sortedInfos = new ArrayList<YSMClientAccess.ControllerInfo>(infos);
        sortedInfos.sort(Comparator.comparingInt(YssCameraService::controllerPriority));
        for (YSMClientAccess.ControllerInfo info : sortedInfos) {
            if (!info.isPlaying() || info.animationName() == null || info.animationName().isBlank() || !runtime.matches(info.name(), info.animationName())) continue;
            YssCameraRuntimeSample sample = runtime.sample(partialTick);
            return new YssViewModifier(sample.position(), sample.rotation(), sample.fov());
        }
        YssCameraService.clearRuntime();
        return null;
    }

    @Nullable
    private static YssCameraRuntime createRuntime(YssAnimationKey key, String controllerName, @Nullable YssExtensionFileLoader.LoadedAnimation loadedAnimation) {
        if (loadedAnimation == null) {
            return null;
        }
        JsonObject extensions = loadedAnimation.extensions();
        if (!extensions.has("camera") || !extensions.get("camera").isJsonObject()) {
            return null;
        }
        YssCameraExtensionType.CameraExtension cameraExtension = CAMERA_EXTENSION.parse(extensions.get("camera"));
        YssCameraExtensionType.CameraTrack track = cameraExtension.selectTrack();
        if (track == null) {
            return null;
        }
        return new YssCameraRuntime(key, controllerName, loadedAnimation.entry().loop, loadedAnimation.entry().animationLength / 20.0f, track);
    }

    private static int controllerPriority(YSMClientAccess.ControllerInfo info) {
        if ("player.main".equals(info.name())) {
            return 0;
        }
        return 1;
    }

    private static void clearCache() {
        YssCameraService.clearRuntime();
    }

    private static void clearRuntime() {
        runtime = null;
        YssCameraService.resetFrameCache();
    }

    private static void resetFrameCache() {
        lastPlayerId = Integer.MIN_VALUE;
        lastTick = Integer.MIN_VALUE;
        lastPartialBits = Integer.MIN_VALUE;
        lastModifier = null;
    }

    static {
        lastPlayerId = Integer.MIN_VALUE;
        lastTick = Integer.MIN_VALUE;
        lastPartialBits = Integer.MIN_VALUE;
    }
}

