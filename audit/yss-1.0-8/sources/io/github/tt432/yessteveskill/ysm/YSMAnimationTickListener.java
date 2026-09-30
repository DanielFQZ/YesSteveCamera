/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapability
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.ysm;

import com.elfmcys.ysm.capability.PlayerAnimatableCapability;
import io.github.tt432.yessteveskill.event.YSMAnimationPlayEvent;
import io.github.tt432.yessteveskill.ysm.YSMClientAccess;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YSMAnimationTickListener {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/AnimTick");
    private final YSMClientAccess access;
    private final Map<Integer, Map<String, CachedState>> stateCache = new HashMap<Integer, Map<String, CachedState>>();
    private boolean firstTick = true;

    public YSMAnimationTickListener(YSMClientAccess access) {
        this.access = access;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91073_ == null || mc.f_91074_ == null) {
            if (!this.stateCache.isEmpty()) {
                this.stateCache.clear();
            }
            return;
        }
        if (this.firstTick) {
            this.firstTick = false;
            LOGGER.info("YSM animation tick listener active, monitoring {} players", (Object)mc.f_91073_.m_6907_().size());
        }
        HashSet<Integer> currentPlayerIds = new HashSet<Integer>();
        for (AbstractClientPlayer player : mc.f_91073_.m_6907_()) {
            currentPlayerIds.add(player.m_19879_());
            this.checkPlayer(player);
        }
        this.stateCache.keySet().removeIf(id -> !currentPlayerIds.contains(id));
    }

    private void checkPlayer(AbstractClientPlayer player) {
        try {
            PlayerAnimatableCapability capability = this.access.getCapability(player);
            if (capability == null) {
                LOGGER.trace("[{}] capInstance is null", (Object)player.m_7755_().getString());
                return;
            }
            Minecraft minecraft = Minecraft.m_91087_();
            float renderTicks = (float)player.f_19797_ + minecraft.m_91296_();
            List<YSMClientAccess.ControllerSlotInfo> infos = this.access.getControllerSlotInfos(capability, renderTicks);
            if (infos == null) {
                LOGGER.trace("[{}] controllerInfos is null", (Object)player.m_7755_().getString());
                return;
            }
            int entityId = player.m_19879_();
            Map playerCache = this.stateCache.computeIfAbsent(entityId, k -> new HashMap());
            HashSet<String> seenKeys = new HashSet<String>();
            for (YSMClientAccess.ControllerSlotInfo info : infos) {
                String controllerName = info.name();
                int controllerSlot = info.slotIndex();
                String stateName = info.stateName();
                String animName = info.animationName();
                int animState = info.animState();
                boolean hasAnim = animName != null && !animName.isEmpty();
                String cacheKey = this.controllerCacheKey(controllerName, controllerSlot);
                seenKeys.add(cacheKey);
                CachedState cached = (CachedState)playerCache.get(cacheKey);
                float animTicks = info.animationTicks();
                if (cached == null) {
                    playerCache.put(cacheKey, new CachedState(stateName, animName, animState, animTicks));
                    if (hasAnim && info.isPlaying()) {
                        LOGGER.debug("[{}] FIRST_SEEN ctrl={}#{} state={} anim={} animState={}  -> FIRE", new Object[]{player.m_7755_().getString(), controllerName, controllerSlot, stateName, animName, animState});
                        this.fireEvent((Player)player, controllerName, controllerSlot, null, stateName, animName, false);
                        continue;
                    }
                    LOGGER.debug("[{}] FIRST_SEEN ctrl={}#{} state={} anim={} animState={}  -> SKIP", new Object[]{player.m_7755_().getString(), controllerName, controllerSlot, stateName, animName, animState});
                    continue;
                }
                if (!(!hasAnim || Objects.equals(cached.stateName, stateName) && Objects.equals(cached.animationName, animName))) {
                    LOGGER.debug("[{}] CHANGED ctrl={}#{} state: {} -> {} anim: {} -> {} animState={}  -> FIRE", new Object[]{player.m_7755_().getString(), controllerName, controllerSlot, cached.stateName, stateName, cached.animationName, animName, animState});
                    String prevState = cached.stateName;
                    cached.stateName = stateName;
                    cached.animationName = animName;
                    cached.lastAnimState = animState;
                    cached.lastAnimationTicks = animTicks;
                    this.fireEvent((Player)player, controllerName, controllerSlot, prevState, stateName, animName, false);
                    continue;
                }
                if (hasAnim && animState == YSMClientAccess.ControllerInfo.STATE_BEGINNING_TRANSITION && cached.lastAnimState != YSMClientAccess.ControllerInfo.STATE_BEGINNING_TRANSITION) {
                    LOGGER.debug("[{}] RETRIGGER ctrl={}#{} state={} anim={} animState: {} -> {}  -> FIRE", new Object[]{player.m_7755_().getString(), controllerName, controllerSlot, stateName, animName, cached.lastAnimState, animState});
                    cached.lastAnimState = animState;
                    cached.lastAnimationTicks = animTicks;
                    this.fireEvent((Player)player, controllerName, controllerSlot, cached.stateName, stateName, animName, true);
                    continue;
                }
                if (hasAnim && info.isPlaying() && cached.lastAnimState == YSMClientAccess.ControllerInfo.STATE_IDLE) {
                    LOGGER.debug("[{}] REPLAY ctrl={}#{} state={} anim={} animState: {} -> {}  -> FIRE", new Object[]{player.m_7755_().getString(), controllerName, controllerSlot, stateName, animName, cached.lastAnimState, animState});
                    cached.lastAnimState = animState;
                    cached.lastAnimationTicks = animTicks;
                    this.fireEvent((Player)player, controllerName, controllerSlot, cached.stateName, stateName, animName, true);
                    continue;
                }
                if (hasAnim && animState == YSMClientAccess.ControllerInfo.STATE_RUNNING && cached.lastAnimationTicks > animTicks + 0.001f) {
                    LOGGER.debug("[{}] TICK_RESET ctrl={}#{} state={} anim={} ticks: {} -> {}  -> FIRE", new Object[]{player.m_7755_().getString(), controllerName, controllerSlot, stateName, animName, Float.valueOf(cached.lastAnimationTicks), Float.valueOf(animTicks)});
                    cached.lastAnimState = animState;
                    cached.lastAnimationTicks = animTicks;
                    this.fireEvent((Player)player, controllerName, controllerSlot, cached.stateName, stateName, animName, true);
                    continue;
                }
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("[{}] NO_CHANGE ctrl={}#{} state={} anim={} animState={} animTicks={} cached=[state={} anim={} lastAnimState={} lastAnimTicks={}]", new Object[]{player.m_7755_().getString(), controllerName, controllerSlot, stateName, animName, animState, Float.valueOf(animTicks), cached.stateName, cached.animationName, cached.lastAnimState, Float.valueOf(cached.lastAnimationTicks)});
                }
                cached.stateName = stateName;
                cached.animationName = animName;
                cached.lastAnimState = animState;
                cached.lastAnimationTicks = animTicks;
            }
            playerCache.keySet().removeIf(key -> !seenKeys.contains(key));
        }
        catch (Exception e) {
            LOGGER.warn("Error checking player animation: {}", (Object)player.m_7755_().getString(), (Object)e);
        }
    }

    private void fireEvent(Player player, String controllerName, int controllerSlot, String prevState, String stateName, String animName, boolean replay) {
        LOGGER.debug("FIRE EVENT player={} ctrl={}#{} prevState={} state={} anim={} replay={}", new Object[]{player.m_7755_().getString(), controllerName, controllerSlot, prevState != null ? prevState : "(null)", stateName != null ? stateName : "(null)", animName, replay});
        MinecraftForge.EVENT_BUS.post((Event)new YSMAnimationPlayEvent(player, controllerName, controllerSlot, prevState != null ? prevState : "", stateName != null ? stateName : "", animName != null ? animName : "", replay));
    }

    private String controllerCacheKey(String controllerName, int controllerSlot) {
        return controllerName + "#" + controllerSlot;
    }

    private static class CachedState {
        String stateName;
        String animationName;
        int lastAnimState;
        float lastAnimationTicks;

        CachedState(String stateName, String animationName, int animState, float animationTicks) {
            this.stateName = stateName;
            this.animationName = animationName;
            this.lastAnimState = animState;
            this.lastAnimationTicks = animationTicks;
        }
    }
}

