package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.camera.shake.CameraShakeService;
import com.github.exopandora.shouldersurfing.camera.target.TargetService;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.MOD_ID,
		value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CameraClientCommands
{
	private CameraClientCommands() {}

	@SubscribeEvent
	public static void register(RegisterClientCommandsEvent event)
	{
		event.getDispatcher().register(Commands.literal("yesstevecamera")
				.then(Commands.literal("shake")
						.then(Commands.literal("play").then(Commands.argument("preset", StringArgumentType.word())
								.executes(context -> play(context.getSource(), StringArgumentType.getString(context, "preset"), "command", 1.0F))
								.then(Commands.argument("scale", FloatArgumentType.floatArg(0, 10))
										.executes(context -> play(context.getSource(), StringArgumentType.getString(context, "preset"), "command", FloatArgumentType.getFloat(context, "scale"))))))
						.then(Commands.literal("stop").executes(context -> { CameraShakeService.stop("command"); context.getSource().sendSuccess(() -> Component.literal("Camera shake stopped"), false); return 1; })))
				.then(Commands.literal("target").then(Commands.literal("cancel").executes(context -> { TargetService.cancel(); context.getSource().sendSuccess(() -> Component.literal("Target lock cancelled"), false); return 1; }))));
	}

	private static int play(net.minecraft.commands.CommandSourceStack source, String id, String slot, float scale)
	{
		if(!CameraShakeService.hasPreset(id))
		{
			source.sendFailure(Component.literal("Unknown camera shake preset: " + id));
			return 0;
		}
		CameraShakeService.trigger(id, slot, scale);
		source.sendSuccess(() -> Component.literal("Camera shake queued: " + id), false);
		return 1;
	}
}
