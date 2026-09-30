/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.api.annotation.Side
 *  com.elfmcys.ysm.api.annotation.YsmEventHandler
 *  com.elfmcys.ysm.api.rendering.v0.event.RenderModelEvent
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 */
package io.github.tt432.yessteveskill.yss.attack;

import com.elfmcys.ysm.api.annotation.Side;
import com.elfmcys.ysm.api.annotation.YsmEventHandler;
import com.elfmcys.ysm.api.rendering.v0.event.RenderModelEvent;
import io.github.tt432.yessteveskill.yss.attack.YssAttackService;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@YsmEventHandler(side=Side.CLIENT)
public final class YssRootMotionRenderHandler {
    @SubscribeEvent
    public void onRenderModel(RenderModelEvent event) {
        Object object = event.target();
        if (!(object instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer)object;
        YssAttackService service = YssAttackService.activeInstance();
        if (service == null) {
            return;
        }
        Vec3 offset = service.currentRootMotionOffset(player);
        if (offset == null || offset.m_82556_() < 1.0E-12) {
            return;
        }
        event.pose().m_85837_(-offset.f_82479_, -offset.f_82480_, -offset.f_82481_);
    }
}

