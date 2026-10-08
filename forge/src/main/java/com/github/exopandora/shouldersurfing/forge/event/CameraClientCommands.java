package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.camera.shake.CameraShakeService;
import com.github.exopandora.shouldersurfing.camera.target.TargetService;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
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
		register(event.getDispatcher());
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("yesstevecamera")
				.then(Commands.literal("reload").executes(context -> {
					String error = com.github.exopandora.shouldersurfing.camera.CameraRuntime.reload();
					if (error != null) { context.getSource().sendFailure(Component.literal("Camera reload failed (old config retained): " + error)); return 0; }
					context.getSource().sendSuccess(() -> Component.literal("Camera reloaded: " + CameraShakeService.presetCount() + " shake presets"), false);
					return 1;
				}))
				.then(Commands.literal("status").executes(context -> {
					context.getSource().sendSuccess(() -> Component.literal("Target: " + TargetService.targetName()
							+ " | Presets: " + CameraShakeService.presetCount() + " | YSM: "
							+ com.github.exopandora.shouldersurfing.forge.compat.YsmBridgeBootstrap.status()
							+ " | YSS: " + com.github.exopandora.shouldersurfing.forge.compat.YssCombatBridge.status()
							+ " | Duel: " + com.github.exopandora.shouldersurfing.camera.duel.DuelCameraService.status()
							+ " | Assist: " + com.github.exopandora.shouldersurfing.camera.assist.CombatAssistService.status()
							+ " | Motion: " + com.github.exopandora.shouldersurfing.camera.motion.CameraMotionService.status()
							+ " | FOV: " + com.github.exopandora.shouldersurfing.camera.fov.MovementFovService.status()
							+ " | Input: " + com.github.exopandora.shouldersurfing.camera.CombatInputState.status()), false);
					return 1;
				}))
				.then(Commands.literal("duel")
						.then(Commands.literal("on").executes(context -> {
							com.github.exopandora.shouldersurfing.camera.duel.DuelCameraService.setEnabled(true);
							context.getSource().sendSuccess(() -> Component.literal("Duel camera enabled (subject to duel.json)"), false); return 1;
						}))
						.then(Commands.literal("off").executes(context -> {
							com.github.exopandora.shouldersurfing.camera.duel.DuelCameraService.setEnabled(false);
							context.getSource().sendSuccess(() -> Component.literal("Duel camera disabled; target lock retained"), false); return 1;
						})))
				.then(Commands.literal("assist_cancel").executes(context -> {
					com.github.exopandora.shouldersurfing.camera.assist.CombatAssistService.cancel("manual cancel"); return 1;
				}))
				.then(Commands.literal("preset")
						.then(Commands.literal("play").then(Commands.argument("preset", ResourceLocationArgument.id())
								.suggests((context, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(
										com.github.exopandora.shouldersurfing.camera.motion.CameraMotionService.presetIds(), builder))
								.then(Commands.argument("seconds", FloatArgumentType.floatArg(0.05F, 60))
										.executes(context -> {
											boolean started = com.github.exopandora.shouldersurfing.camera.motion.CameraMotionService.begin(
													ResourceLocationArgument.getId(context, "preset").toString(), FloatArgumentType.getFloat(context, "seconds"));
											if (!started) context.getSource().sendFailure(Component.literal("Preset unavailable: select a loaded preset and enter shoulder third person"));
											else context.getSource().sendSuccess(() -> Component.literal("Action overview started"), false);
											return started ? 1 : 0;
										}))))
						.then(Commands.literal("stop").executes(context -> { com.github.exopandora.shouldersurfing.camera.motion.CameraMotionService.stop(); return 1; })))
				.then(Commands.literal("shake")
						.then(Commands.literal("play").then(Commands.argument("preset", ResourceLocationArgument.id())
								.suggests((context, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(CameraShakeService.presetIds(), builder))
								.executes(context -> play(context.getSource(), ResourceLocationArgument.getId(context, "preset").toString(), "command", 1.0F))
								.then(Commands.argument("scale", FloatArgumentType.floatArg(0, 10))
										.executes(context -> play(context.getSource(), ResourceLocationArgument.getId(context, "preset").toString(), "command", FloatArgumentType.getFloat(context, "scale"))))))
						.then(Commands.literal("stop_all").executes(context -> { CameraShakeService.reset(); return 1; }))
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
