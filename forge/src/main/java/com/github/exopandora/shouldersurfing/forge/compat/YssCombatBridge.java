package com.github.exopandora.shouldersurfing.forge.compat;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.camera.assist.CombatAssistService;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
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
		try
		{
			var loader = YssCombatBridge.class.getClassLoader();
			Class<? extends Event> targetEvent = Class.forName("io.github.tt432.yessteveskill.event.YssAttackTargetEvent", false, loader).asSubclass(Event.class);
			Class<? extends Event> assistEvent = Class.forName("io.github.tt432.yessteveskill.event.YssAttackAssistEvent", false, loader).asSubclass(Event.class);
			Method targetPlayer = targetEvent.getMethod("getPlayer"), setTarget = targetEvent.getMethod("setTarget", LivingEntity.class);
			Method assistPlayer = assistEvent.getMethod("getPlayer"), actionId = assistEvent.getMethod("getActionId"), reason = assistEvent.getMethod("getBlockReason");
			listen(targetEvent, event -> {
				if (!ready) return;
				try {
					if (targetPlayer.invoke(event) != Minecraft.getInstance().player) return;
					LivingEntity target = CombatAssistService.chooseTarget();
					if (target != null) setTarget.invoke(event, target);
				} catch (ReflectiveOperationException | RuntimeException exception) { fail(exception); }
			});
			listen(assistEvent, event -> {
				if (!ready) return;
				try {
					if (assistPlayer.invoke(event) != Minecraft.getInstance().player) return;
					CombatAssistService.onAttack((Long) actionId.invoke(event), (String) reason.invoke(event));
				} catch (ReflectiveOperationException | RuntimeException exception) { fail(exception); }
			});
			Class<? extends Event> rootEvent = Class.forName("io.github.tt432.yessteveskill.event.YssRootMotionEvent", false, loader).asSubclass(Event.class);
			Method rootPlayer = rootEvent.getMethod("getPlayer"), rootAction = rootEvent.getMethod("getActionId");
			Method movement = rootEvent.getMethod("getMovement"), limit = rootEvent.getMethod("limitHorizontalScale", double.class);
			listen(rootEvent, event -> {
				if (!ready) return;
				try {
					if (rootPlayer.invoke(event) != Minecraft.getInstance().player) return;
					limit.invoke(event, CombatAssistService.rootMotionScale((Long) rootAction.invoke(event), (Vec3) movement.invoke(event)));
				} catch (ReflectiveOperationException | RuntimeException exception) { fail(exception); }
			});
			ready = true; status = "facing + root contact ready";
			installConfirmedHitListener(loader);
			ShoulderSurfingCommon.LOGGER.info("YesSteveCamera YSS: {}", status);
		}
		catch (ReflectiveOperationException | LinkageError | RuntimeException exception) { fail(exception); }
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
			status = "facing + root contact + confirmed hit ready";
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
