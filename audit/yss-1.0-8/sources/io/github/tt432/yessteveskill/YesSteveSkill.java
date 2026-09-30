/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 */
package io.github.tt432.yessteveskill;

import io.github.tt432.yessteveskill.YssCompatCheck;
import io.github.tt432.yessteveskill.combat.CombatEventHandler;
import io.github.tt432.yessteveskill.network.YssNetwork;
import io.github.tt432.yessteveskill.ysm.YSMAnimationTickListener;
import io.github.tt432.yessteveskill.ysm.YSMClientAccess;
import io.github.tt432.yessteveskill.yss.YssCameraService;
import io.github.tt432.yessteveskill.yss.attack.YssAttackModeEventHandler;
import io.github.tt432.yessteveskill.yss.attack.YssAttackService;
import io.github.tt432.yessteveskill.yss.attack.YssMovementClientService;
import io.github.tt432.yessteveskill.yss.molang.YssMolangRegister;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value="yessteveskill")
public class YesSteveSkill {
    public static final String MOD_ID = "yessteveskill";
    private static final YSMClientAccess ysmAccess = new YSMClientAccess();
    private static final YssAttackModeEventHandler attackModeEventHandler = new YssAttackModeEventHandler();
    private static final CombatEventHandler combatEventHandler = new CombatEventHandler();

    public YesSteveSkill() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        YssNetwork.initialize();
        MinecraftForge.EVENT_BUS.register((Object)attackModeEventHandler);
        MinecraftForge.EVENT_BUS.register((Object)combatEventHandler);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        YssCompatCheck.verifyClient();
        event.enqueueWork(YssMolangRegister::register);
        YssCameraService.initialize(ysmAccess);
        MinecraftForge.EVENT_BUS.register((Object)new YSMAnimationTickListener(ysmAccess));
        MinecraftForge.EVENT_BUS.register((Object)new YssAttackService(ysmAccess));
        MinecraftForge.EVENT_BUS.register((Object)new YssMovementClientService(ysmAccess));
    }

    public static YSMClientAccess getYsmAccess() {
        return ysmAccess;
    }
}

