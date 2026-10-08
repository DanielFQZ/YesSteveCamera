package com.github.exopandora.shouldersurfing.camera.motion;

import com.github.exopandora.shouldersurfing.camera.duel.DuelCameraService;
import com.github.exopandora.shouldersurfing.camera.duel.DuelConfig;
import com.github.exopandora.shouldersurfing.camera.duel.DuelFraming;
import com.github.exopandora.shouldersurfing.camera.target.TargetService;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import java.util.Map;
import java.util.Set;

/** World-space follow origin and a fixed action overview. Never moves the player. */
public final class CameraMotionService
{
	private static MotionConfig config = MotionConfig.DEFAULT;
	private static volatile Map<String, OverviewPreset> presets = Map.of(OverviewPreset.DEFAULT.id(), OverviewPreset.DEFAULT);
	private static final PositionSpring follow = new PositionSpring();
	private static OverviewWindow window;
	private static Vec3 overviewFocus, overviewStartEye, releasePosition, previousEye;
	private static double overviewDistance;
	private static double previousTime = Double.NaN, dt, collisionRecovery;
	private CameraMotionService() {}
	public static void configure(MotionConfig value, Map<String, OverviewPreset> values) { config = value; presets = values; reset(); }
	public static Set<String> presetIds() { return presets.keySet(); }
	public static boolean exclusive() { return window != null; }
	public static boolean buffering() { return config.enabled() || exclusive(); }
	public static String status() { return window != null ? (window.releasing() ? "overview returning" : "action overview") : config.enabled() ? "buffered follow" : "direct follow"; }
	public static void reset()
	{
		window = null; overviewFocus = null; overviewStartEye = null; releasePosition = null; previousEye = null;
		overviewDistance = 0;
		previousTime = Double.NaN; dt = 0; collisionRecovery = 0; follow.reset();
	}
	public static boolean begin(String id, double duration)
	{
		var mc = Minecraft.getInstance(); var player = mc.player; var preset = presets.get(id);
		if (preset == null || player == null || !player.isAlive() || mc.getCameraEntity() != player
				|| !ShoulderSurfingImpl.getInstance().isShoulderSurfing() || player.isSleeping()
				|| !Double.isFinite(duration) || duration <= 0 || duration > 60) return false;
		var camera = mc.gameRenderer.getMainCamera();
		Vec3 eye = player.getEyePosition();
		var playerBox = player.getBoundingBox().inflate(preset.horizontalMargin(), preset.verticalMargin(), preset.horizontalMargin());
		var target = TargetService.target();
		var targetBox = target == null ? playerBox : target.getBoundingBox().inflate(preset.horizontalMargin(), preset.verticalMargin(), preset.horizontalMargin());
		// Keep the action area stable while deriving the camera position from the
		// current view each frame. This lets the player look around during the shot.
		overviewFocus = playerBox.getCenter().lerp(targetBox.getCenter(), 0.5);
		overviewStartEye = eye;
		float yaw = camera.getYRot(), pitch = camera.getXRot();
		var fitting = new DuelConfig(true, 35, preset.minDistance(), preset.maxDistance(), 0.3, 0, 0);
		var frame = DuelFraming.fit(eye, playerBox, targetBox, yaw, pitch,
				Math.min(60, mc.options.fov().get()), (double) mc.getWindow().getWidth() / Math.max(1, mc.getWindow().getHeight()), fitting);
		overviewDistance = preset.distance() > 0 ? preset.distance() : frame.offset().length();
		releasePosition = null;
		window = new OverviewWindow(); window.begin(duration, preset.enterSeconds(), preset.exitSeconds());
		DuelCameraService.reset();
		return true;
	}
	public static void stop() { if (window != null) window.stop(); }
	public static Vec2f prepare(Entity entity, float partial, Vec2f normal)
	{
		var mc = Minecraft.getInstance();
		if (mc.level == null || entity != mc.player || !entity.isAlive() || !ShoulderSurfingImpl.getInstance().isShoulderSurfing())
		{ reset(); return normal; }
		double time = mc.level.getGameTime() + partial;
		dt = Double.isNaN(previousTime) || mc.isPaused() ? 0 : Mth.clamp((time - previousTime) / 20, 0, 0.25);
		previousTime = Double.isNaN(previousTime) ? time : Math.max(previousTime, time);
		Vec3 eye = entity.getEyePosition(partial);
		if (previousEye != null && eye.distanceTo(previousEye) > config.teleportDistance())
		{ reset(); previousTime = time; }
		if (window != null)
		{
			window.advance(dt);
			if (window.finished()) window = null;
			else return normal;
		}
		return normal;
	}
	public static Vec3 offset(Camera camera, Entity entity, float partial, Vec3 normal)
	{
		if (entity != Minecraft.getInstance().player) return normal;
		Vec3 eye = entity.getEyePosition(partial);
		boolean moving = previousEye != null && eye.distanceToSqr(previousEye) > 1.0E-7;
		// An active overview is an action/root-motion shot even when the player
		// happens to remain on the ground, so it keeps the softer action response.
		boolean duel = DuelCameraService.blending();
		boolean grounded = entity.onGround() && window == null && !duel;
		double startResponse = grounded ? config.groundStartSeconds() : config.startSeconds();
		double stopResponse = grounded ? config.groundStopSeconds() : config.stopSeconds();
		double maxLag = grounded ? config.groundMaxLag() : config.maxLag();
		Vec3 origin;
		if (config.enabled() && !duel)
		{
			origin = follow.step(eye, dt, moving ? startResponse : stopResponse);
			Vec3 lag = origin.subtract(eye);
			if (lag.length() > maxLag) { origin = eye.add(lag.normalize().scale(maxLag)); follow.snap(origin); }
		}
		else { follow.snap(eye); origin = eye; }
		previousEye = eye;
		Vec3 left = new Vec3(camera.getLeftVector()), up = new Vec3(camera.getUpVector()), look = new Vec3(camera.getLookVector());
		Vec3 world = origin.add(left.scale(normal.x)).add(up.scale(normal.y)).subtract(look.scale(normal.z));
		if (window != null && overviewFocus != null)
		{
			// Move the captured action area by the same smoothed player displacement
			// as the normal camera, keeping the overview composed during root motion.
			Vec3 actionFocus = overviewFocus;
			if (overviewStartEye != null) actionFocus = actionFocus.add(origin.subtract(overviewStartEye));
			Vec3 actionWorld = actionFocus.subtract(look.scale(overviewDistance));
			if (window.releasing())
			{
				if (releasePosition == null) releasePosition = actionWorld;
				world = releasePosition.lerp(world, window.releaseProgress());
			}
			else world = world.lerp(actionWorld, window.weight());
		}
		Vec3 relative = world.subtract(eye);
		return new Vec3(relative.dot(left), relative.dot(up), -relative.dot(look));
	}
	public static boolean scroll(double amount)
	{
		var mc = Minecraft.getInstance();
		if (!config.wheelZoom() || !Double.isFinite(amount) || amount == 0 || mc.player == null
				|| mc.screen != null || !ShoulderSurfingImpl.getInstance().isShoulderSurfing()) return false;
		double previous = Config.CLIENT.getOffsetZ();
		Config.CLIENT.scrollDistance(-amount * config.wheelStep());
		DuelCameraService.zoom(Config.CLIENT.getOffsetZ() - previous);
		return true;
	}
	public static double collisionDistance(double desired, double allowed)
	{
		// Walls retract immediately; recovering clearance eases outward without tracking the player.
		collisionRecovery = Math.max(Math.max(0, desired - allowed), collisionRecovery * Math.exp(-dt / config.stopSeconds()));
		return Math.min(allowed, Math.max(0, desired - collisionRecovery));
	}
}
