/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Event
 */
package io.github.tt432.yessteveskill.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class YSMAnimationPlayEvent
extends Event {
    private final Player player;
    private final String controllerName;
    private final int controllerSlot;
    private final String previousStateName;
    private final String stateName;
    private final String animationName;
    private final boolean replay;

    public YSMAnimationPlayEvent(Player player, String controllerName, int controllerSlot, String previousStateName, String stateName, String animationName) {
        this(player, controllerName, controllerSlot, previousStateName, stateName, animationName, false);
    }

    public YSMAnimationPlayEvent(Player player, String controllerName, int controllerSlot, String previousStateName, String stateName, String animationName, boolean replay) {
        this.player = player;
        this.controllerName = controllerName;
        this.controllerSlot = controllerSlot;
        this.previousStateName = previousStateName;
        this.stateName = stateName;
        this.animationName = animationName;
        this.replay = replay;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getControllerName() {
        return this.controllerName;
    }

    public int getControllerSlot() {
        return this.controllerSlot;
    }

    public String getPreviousStateName() {
        return this.previousStateName;
    }

    public String getStateName() {
        return this.stateName;
    }

    public String getAnimationName() {
        return this.animationName;
    }

    public boolean isReplay() {
        return this.replay;
    }
}

