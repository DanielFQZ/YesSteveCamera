/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.api.annotation.Side
 *  com.elfmcys.ysm.api.annotation.YsmEventHandler
 *  com.elfmcys.ysm.api.rendering.v0.event.RenderModelEvent
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapability
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package io.github.tt432.yessteveskill.yss.attack;

import com.elfmcys.ysm.api.annotation.Side;
import com.elfmcys.ysm.api.annotation.YsmEventHandler;
import com.elfmcys.ysm.api.rendering.v0.event.RenderModelEvent;
import com.elfmcys.ysm.capability.PlayerAnimatableCapability;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel;
import io.github.tt432.yessteveskill.YesSteveSkill;
import io.github.tt432.yessteveskill.yss.attack.YssAttackService;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@YsmEventHandler(side=Side.CLIENT)
public final class YssBoneHighlightHandler {
    private static final int HIGHLIGHT_GLOW = 15;
    private static final int DEFAULT_GLOW = -1;
    private final WeakHashMap<AnimatedGeoModel, Map<Integer, int[]>> appliedByModel = new WeakHashMap();

    @SubscribeEvent
    public void onRenderModel(RenderModelEvent event) {
        AnimatedGeoBone bone;
        PlayerAnimatableCapability capability;
        AnimatedGeoModel model;
        Map<Integer, int[]> highlights;
        Object object = event.target();
        if (!(object instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer)object;
        Map<Integer, int[]> desired = Map.of();
        YssAttackService service = YssAttackService.activeInstance();
        if (service != null && service.hasActiveRuntime() && Minecraft.m_91087_().m_91290_().m_114377_() && (highlights = service.currentBoneHighlights(player)) != null) {
            desired = highlights;
        }
        AnimatedGeoModel animatedGeoModel = model = (capability = YesSteveSkill.getYsmAccess().getCapability(player)) == null ? null : capability.getLoadedGeoModel();
        if (model == null) {
            return;
        }
        Map applied = this.appliedByModel.get(model);
        if (applied == null) {
            applied = Map.of();
        }
        for (Map.Entry entry : applied.entrySet()) {
            int[] newRgb = desired.get(entry.getKey());
            if (newRgb != null && Arrays.equals(newRgb, (int[])entry.getValue()) || (bone = (AnimatedGeoBone)model.getBoneMap().get(entry.getKey())) == null) continue;
            bone.setColor(255, 255, 255);
            bone.setGlow(-1);
        }
        for (Map.Entry<Object, Object> entry : desired.entrySet()) {
            int[] oldRgb = (int[])applied.get(entry.getKey());
            if (oldRgb != null && Arrays.equals(oldRgb, (int[])entry.getValue()) || (bone = (AnimatedGeoBone)model.getBoneMap().get(entry.getKey())) == null) continue;
            int[] rgb = (int[])entry.getValue();
            bone.setColor(rgb[0], rgb[1], rgb[2]);
            bone.setGlow(15);
        }
        if (desired.isEmpty()) {
            this.appliedByModel.remove(model);
        } else {
            this.appliedByModel.put(model, desired);
        }
    }
}

