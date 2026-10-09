package com.github.exopandora.shouldersurfing.forge.compat;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.camera.assist.CombatAssistService;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;
import java.lang.reflect.Method;
import java.util.UUID;

/** Optional public-event adapter. No reflection into YSS private fields or damage logic. */
public final class YssCombatBridge
{
	private static boolean ready;
	private static String status = "not installed";
	private static long lastHitSequence = Long.MIN_VALUE;
	private YssCombatBridge() {}
	public static String status() { return status; }

	public static void install()
	{
		if (!ModList.get().isLoaded("yessteveskill")) { status = "YSS absent"; return; }
		installCombatInput();
		try
		{
			var loader = YssCombatBridge.class.getClassLoader();
			Class<? extends Event> targetEvent = Class.forName("io.github.tt432.yessteveskill.event.YssAttackTargetEvent", false, loader).asSubclass(Event.class);
			Class<? extends Event> assistEvent = Class.forName("io.github.tt432.yessteveskill.event.YssAttackAssistEvent", false, loader).asSubclass(Event.class);
			Method targetPlayer = targetEvent.getMethod("getPlayer"), setTarget = targetEvent.getMethod("setTarget", LivingEntity.class);
			Method assistPlayer = assistEvent.getMethod("getPlayer"), actionId = assistEvent.getMethod("getActionId"), reason = assistEvent.getMethod("getBlockReason");
			Method rootYaw = optionalRootYaw(assistEvent);
			Method assistMode = optionalAssistMode(assistEvent);
			listen(targetEvent, event -> {
				if (!ready) return;
				try {
					if (targetPlayer.invoke(event) != Minecraft.getInstance().player) return;
					LivingEntity target = CombatAssistService.chooseTarget();
					if (target != null) {
						CombatAssistService.rememberTarget(target);
						setTarget.invoke(event, target);
					}
				} catch (ReflectiveOperationException | RuntimeException exception) { fail(exception); }
			});
			listen(assistEvent, event -> {
				if (!ready) return;
				try {
					if (assistPlayer.invoke(event) != Minecraft.getInstance().player) return;
					CombatAssistService.onAttack((Long) actionId.invoke(event), (String) reason.invoke(event),
							rootYaw == null ? Float.NaN : (Float) rootYaw.invoke(event),
							assistMode == null ? "inherit" : (String) assistMode.invoke(event));
				} catch (ReflectiveOperationException | RuntimeException exception) { fail(exception); }
			});
			ready = true; status = "attack facing ready";
			installConfirmedHitListener(loader);
			ShoulderSurfingCommon.LOGGER.info("YesSteveCamera YSS: {}", status);
		}
		catch (ReflectiveOperationException | LinkageError | RuntimeException exception) { fail(exception); }
	}

	private static void installCombatInput()
	{
		try
		{
			// Resolve this optional public API once. Frame/input queries use the returned supplier directly.
			Class<?> api = Class.forName("io.github.tt432.yessteveskill.client.input.YssCombatInput", true,
					YssCombatBridge.class.getClassLoader());
			java.util.function.BooleanSupplier perspective = () ->
					com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl.getInstance().isShoulderSurfing();
			var state = (java.util.function.BooleanSupplier) api
					.getMethod("registerCameraPerspective", java.util.function.BooleanSupplier.class).invoke(null, perspective);
			com.github.exopandora.shouldersurfing.camera.CombatInputState.install(state);
			ShoulderSurfingCommon.LOGGER.info("YesSteveCamera YSS combat input policy connected");
		}
		catch (ReflectiveOperationException | LinkageError | RuntimeException exception)
		{
			ShoulderSurfingCommon.LOGGER.warn("YesSteveCamera combat input API unavailable; mining and block highlights remain unchanged", exception);
		}
	}

	private static void installConfirmedHitListener(ClassLoader loader)
	{
		try
		{
			Class<? extends Event> hitEvent = Class.forName("io.github.tt432.yessteveskill.event.YssClientHitResolvedEvent", false, loader).asSubclass(Event.class);
			Method hitAttacker = hitEvent.getMethod("attacker"), hitSequence = hitEvent.getMethod("sequence");
			listen(hitEvent, event -> {
				if (!ready) return;
				try
				{
					Minecraft minecraft = Minecraft.getInstance();
					if (minecraft.player == null || !minecraft.player.getUUID().equals((UUID) hitAttacker.invoke(event))) return;
					long sequence = (Long) hitSequence.invoke(event);
					if (com.github.exopandora.shouldersurfing.camera.CameraRuntime.onConfirmedHit((UUID) hitAttacker.invoke(event), sequence)) return;
					if (sequence <= lastHitSequence) return;
					lastHitSequence = sequence;
					if (com.github.exopandora.shouldersurfing.camera.shake.CameraShakeService.hasPreset("yesstevecamera:light_hit"))
						com.github.exopandora.shouldersurfing.camera.shake.CameraShakeService.trigger("yesstevecamera:light_hit", "yss_hit", 1.0F);
				}
				catch (ReflectiveOperationException | RuntimeException exception) { logHitFailure(exception); }
			});
			status = "attack heading + confirmed hit ready";
		}
		catch (ReflectiveOperationException | LinkageError | RuntimeException exception)
		{
			ShoulderSurfingCommon.LOGGER.info("YesSteveCamera YSS confirmed-hit shake unavailable; attack assist remains active");
		}
	}

	private static void logHitFailure(Throwable exception)
	{
		ShoulderSurfingCommon.LOGGER.warn("YesSteveCamera YSS confirmed-hit shake listener failed", exception);
	}

	private static Method optionalRootYaw(Class<?> eventType)
	{
		try { return eventType.getMethod("getRootMotionYaw"); }
		catch (NoSuchMethodException exception)
		{
			ShoulderSurfingCommon.LOGGER.info("YSS has no fixed root heading API; using Camera's action-entry heading");
			return null;
		}
	}

	private static Method optionalAssistMode(Class<?> eventType)
	{
		try { return eventType.getMethod("getCameraAssistMode"); }
		catch (NoSuchMethodException exception)
		{
			ShoulderSurfingCommon.LOGGER.info("YSS has no animation-level Camera assist API; using inherit");
			return null;
		}
	}

	private static <T extends Event> void listen(Class<T> type, java.util.function.Consumer<T> listener)
	{
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, type, listener);
	}
	private static void fail(Throwable exception)
	{
		ready = false; status = "assist unavailable (YSS API missing or failed)";
		CombatAssistService.cancel("YSS bridge unavailable");
		ShoulderSurfingCommon.LOGGER.warn("YesSteveCamera YSS assist disabled; lock and shake remain available", exception);
	}
}
