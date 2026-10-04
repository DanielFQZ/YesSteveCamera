package com.github.exopandora.shouldersurfing.camera.assist;

import com.github.exopandora.shouldersurfing.api.target.TargetClassification;
import com.github.exopandora.shouldersurfing.camera.target.TargetService;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import java.util.Comparator;

/** Runs synchronously inside YSS's pre-judgement window, never from a render/animation worker. */
public final class CombatAssistService
{
	private static AssistConfig config = AssistConfig.DEFAULT;
	private static final AssistWindow WINDOW = new AssistWindow();
	private static LivingEntity target;
	private static int lastTick = Integer.MIN_VALUE;
	private static int lastEventTick = Integer.MIN_VALUE;
	private static long currentAction;
	private static boolean freeFacing, manuallySuppressed;
	private static String status = "idle";
	private static AssistDiagnostics diagnostics = new AssistDiagnostics();
	private CombatAssistService() {}
	public static void configure(AssistConfig value) { config = value; cancel("configuration reloaded"); }
	public static void reset() { diagnostics = new AssistDiagnostics(); WINDOW.reset(); target = null; lastTick = Integer.MIN_VALUE; lastEventTick = Integer.MIN_VALUE; freeFacing = false; manuallySuppressed = false; status = "idle"; }
	public static void cancel(String reason)
	{
		if (diagnostics.finish(reason))
			com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.LOGGER.info("Camera assist: {}", diagnostics.summary());
		WINDOW.stop(); freeFacing = false; target = null; status = reason;
		if (reason.equals("manual cancel") || reason.equals("lock cancelled")) manuallySuppressed = true;
	}
	public static String status() { return (armed() ? "sword ready: " : "no sword: ") + (hasAssistTarget() ? (sprintReleasesFacing() ? "sprinting: facing released" : status) : "idle") + " | " + diagnostics.summary(); }
	public static LivingEntity target() { return target; }
	private static boolean hasAssistTarget() { return (WINDOW.active() || freeFacing) && target != null && armed() && TargetService.enabled(); }
	public static boolean ownsFacing() { return hasAssistTarget() && !sprintReleasesFacing(); }
	public static boolean sprintReleasesFacing()
	{
		return LockMovementService.releasesFacing();
	}

	public static LivingEntity chooseTarget()
	{
		var mc = Minecraft.getInstance();
		if (!config.enabled() || !armed() || !TargetService.enabled()) return null;
		LivingEntity locked = TargetService.target();
		if (locked != null) return legal(locked) ? locked : null;
		if (!config.autoHostiles()) return null;
		Vec3 forward = Vec3.directionFromRotation(0, mc.player.getYRot());
		// Automatic attacks use the player's forward hemisphere, independently of the camera.
		return mc.level.getEntitiesOfClass(LivingEntity.class, mc.player.getBoundingBox().inflate(config.range()),
				entity -> legal(entity) && TargetService.classify(entity) == TargetClassification.HOSTILE)
				.stream().filter(entity -> withinAcquisitionRange(entity))
				.filter(entity -> direction(entity).dot(forward) >= 0)
				.min(Comparator.<LivingEntity>comparingDouble(entity -> -direction(entity).dot(forward))
						.thenComparingDouble(mc.player::distanceToSqr).thenComparing(LivingEntity::getUUID)).orElse(null);
	}
	private static boolean withinAcquisitionRange(LivingEntity entity)
	{
		return Minecraft.getInstance().player.distanceToSqr(entity) <= config.range() * config.range();
	}

	private static Vec3 direction(LivingEntity entity)
	{
		Vec3 delta = entity.position().subtract(Minecraft.getInstance().player.position());
		return new Vec3(delta.x, 0, delta.z).normalize();
	}
	private static boolean legal(LivingEntity entity)
	{
		var mc = Minecraft.getInstance();
		return mc.player != null && entity != mc.player && entity.level() == mc.level && entity.isAlive()
				&& !entity.isRemoved() && !entity.isSpectator() && TargetService.classify(entity) != TargetClassification.PROTECTED
				&& TargetService.hasUnobstructedView(entity);
	}

	private static boolean armed()
	{
		var player = Minecraft.getInstance().player;
		return player != null && (player.getMainHandItem().getItem() instanceof SwordItem
				|| player.getMainHandItem().is(ItemTags.SWORDS));
	}

	public static void onAttack(long actionId, String blocked)
	{
		var mc = Minecraft.getInstance();
		if (mc.player == null || mc.isPaused()) return;
		lastEventTick = mc.player.tickCount;
		currentAction = actionId;
		diagnostics.received();
		if (WINDOW.observe(actionId))
		{
			freeFacing = false; manuallySuppressed = false;
			target = chooseTarget();
			diagnostics.begin(target == null ? "none" : target.getDisplayName().getString());
		}
		// Root motion owns translation, not facing. Older bridge versions report it as a block.
		if (!blocked.isEmpty() && !blocked.equals("root_motion")) { cancel("YSS " + blocked); return; }
		if (WINDOW.active()) correctFacing();
	}

	private static void correctFacing()
	{
		var mc = Minecraft.getInstance(); var player = mc.player;
		if (lastTick == player.tickCount) return;
		lastTick = player.tickCount;
		if (!armed()) { cancel("no sword"); return; }
		if (!ShoulderSurfingImpl.getInstance().isCameraDecoupled()) { cancel("coupled camera"); return; }
		if (mc.gameMode != null && mc.gameMode.isDestroying()) { cancel("mining block"); return; }
		if (!config.enabled() || !TargetService.enabled() || target == null || !legal(target)) { cancel("target unavailable"); return; }
		if (TargetService.target() != null && TargetService.target() != target) { cancel("lock changed"); return; }
		if (player.isPassenger() || player.isFallFlying() || player.getAbilities().flying) { cancel("riding or flying"); return; }
		if (player.hurtTime > 0) { cancel("hurt"); return; }
		if (player.isUsingItem() || ShoulderSurfingImpl.getInstance().isAiming()) { cancel("using item or aiming"); return; }
		// Yield rotation without ending the action or losing its target. Walking resumes it.
		if (sprintReleasesFacing())
		{
			status = "sprinting: facing released";
			if (WINDOW.active()) diagnostics.progress(status, 0, 0);
			return;
		}
		Vec3 heading = direction(target);
		float previousYaw = player.getYRot();
		float yaw = (float) (Mth.atan2(-heading.x, heading.z) * Mth.RAD_TO_DEG);
		if (heading.lengthSqr() > 1.0E-8)
		{
			float turn = Mth.clamp(Mth.wrapDegrees(yaw - player.getYRot()), (float) -config.turnDegrees(), (float) config.turnDegrees());
			player.setYRot(player.getYRot() + turn);
			player.yBodyRot = player.getYRot(); player.yHeadRot = player.getYRot();
		}
		status = "correcting facing";
		if (WINDOW.active()) diagnostics.progress(status, 0, Mth.wrapDegrees(player.getYRot() - previousYaw));
		// Only facing changes here. Translation belongs entirely to YSS / vanilla movement.
		if (player.getYRot() != previousYaw)
			player.connection.send(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot(
					player.getYRot(), player.getXRot(), player.onGround()));
	}

	/** Called synchronously before YSS applies this action's root-motion delta. */
	public static double rootMotionScale(long actionId, Vec3 delta)
	{
		var mc = Minecraft.getInstance();
		if (!WINDOW.active() || actionId != currentAction || !hasAssistTarget() || !legal(target)
				|| lastEventTick != mc.player.tickCount) return 1;
		double scale = RootMotionClip.horizontalScale(mc.player.getBoundingBox(), target.getBoundingBox(), delta);
		if (scale < 1)
		{
			status = "root motion contact";
			diagnostics.clipped(delta.horizontalDistance() * (1 - scale));
		}
		return scale;
	}

	public static void endTick()
	{
		var mc = Minecraft.getInstance();
		if (mc.isPaused()) return;
		if (mc.player == null) { reset(); return; }
		if (!armed()) { manuallySuppressed = false; cancel("no sword"); return; }
		if (lastEventTick == mc.player.tickCount) return;
		if (WINDOW.active()) cancel("animation ended");
		// Holding a sword arms correction even without a YSS animation. Do not erase the
		// last action's diagnostics while idle or when opening chat to inspect them.
		if (manuallySuppressed || !config.enabled() || !TargetService.enabled()) return;
		// Idle automatic assistance stays local; a manual lock or active attack has no distance cap.
		if (target == null || !legal(target)
				|| (TargetService.target() == null && !withinAcquisitionRange(target))
				|| (TargetService.target() != null && TargetService.target() != target))
			target = chooseTarget();
		freeFacing = target != null;
		if (freeFacing) correctFacing();
	}
}
