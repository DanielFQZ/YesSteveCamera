package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import com.github.exopandora.shouldersurfing.camera.assist.AssistConfig;
import com.github.exopandora.shouldersurfing.camera.assist.CombatAssistService;
import com.github.exopandora.shouldersurfing.camera.shake.CameraShakeService;
import com.github.exopandora.shouldersurfing.camera.target.TargetService;
import com.github.exopandora.shouldersurfing.camera.target.TargetingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import com.github.exopandora.shouldersurfing.camera.motion.CameraMotionService;
import com.github.exopandora.shouldersurfing.camera.motion.MotionConfig;
import com.github.exopandora.shouldersurfing.camera.motion.OverviewPreset;

public final class CameraRuntime
{
	private record Session(Level level, UUID owner, UUID token) {}
	private static final CameraActionQueue QUEUE = new CameraActionQueue();
	private record HitWindowAction(UUID session, UUID source, String preset, String slot, float scale, boolean stop) {}
	private static final ArrayDeque<HitWindowAction> HIT_ACTIONS = new ArrayDeque<>();
	private static final Map<String, CameraHitWindow> HIT_WINDOWS = new HashMap<>();
	private record OverviewAction(UUID session, UUID source, String preset, double duration, boolean stop) {}
	private static final ArrayDeque<OverviewAction> OVERVIEW_ACTIONS = new ArrayDeque<>();
	private static volatile Session session;
	private CameraRuntime() {}

	/** Called on every client START tick, including disconnect and paused frames. */
	public static void tick()
	{
		Minecraft mc = Minecraft.getInstance();
		UUID owner = mc.player == null ? null : mc.player.getUUID();
		Session current = session;
		if (current == null || current.level() != mc.level || !java.util.Objects.equals(current.owner(), owner))
		{
			session = null;
			QUEUE.reset();
			resetHitWindows();
			synchronized (OVERVIEW_ACTIONS) { OVERVIEW_ACTIONS.clear(); }
			CameraMotionService.reset();
			com.github.exopandora.shouldersurfing.camera.assist.LockMovementService.reset();
			com.github.exopandora.shouldersurfing.camera.assist.AnyDirectionSprintService.reset();
			CameraShakeService.reset(); CombatAssistService.reset(); com.github.exopandora.shouldersurfing.camera.duel.DuelCameraService.reset();
			if (mc.level != null && owner != null) session = new Session(mc.level, owner, QUEUE.session());
		}
		if (mc.player == null || !mc.player.isAlive()) { QUEUE.reset(); resetHitWindows(); synchronized (OVERVIEW_ACTIONS) { OVERVIEW_ACTIONS.clear(); } session = null; CameraShakeService.reset(); CombatAssistService.reset(); com.github.exopandora.shouldersurfing.camera.assist.LockMovementService.reset(); com.github.exopandora.shouldersurfing.camera.assist.AnyDirectionSprintService.reset(); com.github.exopandora.shouldersurfing.camera.duel.DuelCameraService.reset(); CameraMotionService.reset(); return; }
		if (!com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl.getInstance().isShoulderSurfing() || mc.player.isSleeping())
		{
			com.github.exopandora.shouldersurfing.camera.duel.DuelCameraService.reset();
			CameraMotionService.reset();
		}
		if (mc.isPaused()) return;
		CameraShakeService.tick();
		QUEUE.drain(action -> {
			if (action.stop()) CameraShakeService.stop(action.slot());
			else if (CameraShakeService.hasPreset(action.preset())) CameraShakeService.trigger(action.preset(), action.slot(), action.scale());
			else ShoulderSurfingCommon.LOGGER.warn("Unknown YesSteveCamera shake preset: {}", action.preset());
		});
		synchronized (OVERVIEW_ACTIONS)
		{
			OverviewAction action;
			while ((action = OVERVIEW_ACTIONS.poll()) != null && session != null && session.token().equals(action.session()))
				if (action.stop()) CameraMotionService.stop();
				else CameraMotionService.begin(action.preset(), action.duration());
		}
		synchronized (HIT_ACTIONS)
		{
			HitWindowAction action;
			while ((action = HIT_ACTIONS.poll()) != null && session != null && session.token().equals(action.session()))
			{
				if (action.stop()) HIT_WINDOWS.remove(action.slot());
				else if (CameraShakeService.hasPreset(action.preset())) HIT_WINDOWS.put(action.slot(), new CameraHitWindow(action.preset(), action.scale()));
				else ShoulderSurfingCommon.LOGGER.warn("Unknown YesSteveCamera hit-window preset: {}", action.preset());
			}
		}
	}

	/** No entity/context/arguments are retained. The source level only verifies the published session. */
	public static boolean enqueue(Level sourceLevel, UUID source, String preset, String slot, float scale, boolean stop)
	{
		Session current = session;
		return current != null && current.level() == sourceLevel && current.owner().equals(source)
				&& QUEUE.offer(new CameraActionQueue.Action(current.token(), preset, slot, scale, stop));
	}

	public static boolean enqueueOverview(Level sourceLevel, UUID source, String preset, double duration, boolean stop)
	{
		Session current = session;
		if (current == null || current.level() != sourceLevel || !current.owner().equals(source)
				|| (!stop && (preset == null || !CameraMotionService.presetIds().contains(preset)))
				|| (!stop && (!Double.isFinite(duration) || duration <= 0 || duration > 60))) return false;
		synchronized (OVERVIEW_ACTIONS)
		{
			if (OVERVIEW_ACTIONS.size() >= 64) return false;
			OVERVIEW_ACTIONS.add(new OverviewAction(current.token(), source, preset, duration, stop));
			return true;
		}
	}

	/** Queues an animation-defined confirmed-hit window for the local player. */
	public static boolean enqueueHitWindow(Level sourceLevel, UUID source, String preset, String slot, float scale, boolean stop)
	{
		Session current = session;
		if (current == null || current.level() != sourceLevel || !current.owner().equals(source)
				|| slot == null || slot.isBlank() || slot.length() > 128 || !Float.isFinite(scale) || scale < 0 || scale > 10
				|| (!stop && (preset == null || !preset.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")))) return false;
		synchronized (HIT_ACTIONS)
		{
			if (HIT_ACTIONS.size() >= 128) return false;
			HIT_ACTIONS.add(new HitWindowAction(current.token(), source, preset, slot, scale, stop));
			return true;
		}
	}

	/** Applies one server-confirmed hit to the current local hit windows. */
	public static boolean onConfirmedHit(UUID attacker, long sequence)
	{
		Session current = session;
		if (current == null || !current.owner().equals(attacker)) return false;
		boolean armed = false;
		for (CameraHitWindow window : HIT_WINDOWS.values())
		{
			armed = true;
			if (window.accept(sequence)) CameraShakeService.trigger(window.preset(), "hit:" + window.preset(), window.scale());
		}
		return armed;
	}

	private static void resetHitWindows()
	{
		synchronized (HIT_ACTIONS) { HIT_ACTIONS.clear(); }
		HIT_WINDOWS.clear();
	}

	public static String reload()
	{
		var directory = Minecraft.getInstance().gameDirectory.toPath();
		try
		{
			TargetingConfig targetConfig = TargetingConfig.load(directory.resolve("config/yesstevecamera/targeting.json"));
			AssistConfig assistConfig = AssistConfig.load(directory.resolve("config/yesstevecamera/assist.json"));
			var duelConfig = com.github.exopandora.shouldersurfing.camera.duel.DuelConfig.load(directory.resolve("config/yesstevecamera/duel.json"));
			var motionConfig = MotionConfig.load(directory.resolve("config/yesstevecamera/motion.json"));
			var fovConfig = com.github.exopandora.shouldersurfing.camera.fov.MovementFovConfig.load(directory.resolve("config/yesstevecamera/fov.json"));
			var overviewPresets = OverviewPreset.load(directory.resolve("config/yesstevecamera/overviews"));
			CameraShakeService.reload(directory);
			CombatAssistService.configure(assistConfig);
			TargetService.configure(targetConfig);
			com.github.exopandora.shouldersurfing.camera.duel.DuelCameraService.configure(duelConfig);
			CameraMotionService.configure(motionConfig, overviewPresets);
			com.github.exopandora.shouldersurfing.camera.fov.MovementFovService.configure(fovConfig);
			synchronized (OVERVIEW_ACTIONS) { OVERVIEW_ACTIONS.clear(); }
			QUEUE.reset();
			resetHitWindows();
			Session current = session;
			if (current != null) session = new Session(current.level(), current.owner(), QUEUE.session());
			return null;
		}
		catch (Exception exception)
		{
			ShoulderSurfingCommon.LOGGER.warn("YesSteveCamera reload failed; previous configuration retained", exception);
			return exception.getMessage();
		}
	}
}
