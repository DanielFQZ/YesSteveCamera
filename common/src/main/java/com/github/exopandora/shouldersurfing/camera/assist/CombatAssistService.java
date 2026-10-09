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
	private static final AttackHeading HEADING = new AttackHeading();
	private static LivingEntity target;
	private static int lastTick = Integer.MIN_VALUE;
	private static int lastEventTick = Integer.MIN_VALUE;
	private static long currentAction;
	private static boolean freeFacing, manuallySuppressed;
	private static String actionAssistMode = "inherit";
	private static String status = "idle";
	private static AssistDiagnostics diagnostics = new AssistDiagnostics();
	private CombatAssistService() {}
	public static void configure(AssistConfig value) { config = value; cancel("configuration reloaded"); }
	public static void reset() { diagnostics = new AssistDiagnostics(); WINDOW.reset(); HEADING.clear(); target = null; lastTick = Integer.MIN_VALUE; lastEventTick = Integer.MIN_VALUE; freeFacing = false; manuallySuppressed = false; actionAssistMode = "inherit"; status = "idle"; }
	public static void cancel(String reason)
	{
		if (diagnostics.finish(reason))
			com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.LOGGER.info("Camera assist: {}", diagnostics.summary());
		WINDOW.stop(); HEADING.clear(); freeFacing = false; target = null; status = reason;
		// Cancelling an explicit lock must return to automatic acquisition, not disable it.
		manuallySuppressed = reason.equals("manual cancel");
	}
	public static String status() { return (armed() ? "sword ready: " : "no sword: ") + (hasAssistTarget() ? (sprintReleasesFacing() ? "sprinting: facing released" : status) : "idle") + " | " + diagnostics.summary(); }
	public static LivingEntity target() { return target; }
	/** Makes the target chosen by YSS's pre-animation attack event visible to optional integrations. */
	public static void rememberTarget(LivingEntity value)
	{
		if (value != null && legal(value)) target = value;
	}
	public static void onLockCleared()
	{
		target = null;
		freeFacing = false;
		manuallySuppressed = false;
		// Keep the launched action's frame even if its target dies or the user unlocks it.
	}
	private static boolean hasAssistTarget() {
		if (!(WINDOW.active() || freeFacing) || target == null || !armed() || !TargetService.enabled()) return false;
		return WINDOW.active() ? withinAttackRange(target)
				: (TargetService.target() != null || withinAssistLockRange(target));
	}
	public static boolean steeringSuppressed() { return ActionAssistPolicy.suppressesSteering(WINDOW.active(), actionAssistMode); }
	public static boolean ownsFacing() { return !steeringSuppressed() && (preservesHeading() || (hasAssistTarget() && !sprintReleasesFacing())); }
	public static boolean preservesHeading()
	{
		var mc = Minecraft.getInstance();
		return WINDOW.active() && HEADING.captured() && mc.player != null && mc.player.isAlive()
				&& ShoulderSurfingImpl.getInstance().isCameraDecoupled()
				&& !"off".equals(actionAssistMode);
	}
	/** Also called after vanilla's body-turn calculation, which otherwise turns during return travel. */
	public static void applyAttackHeading()
	{
		if (!preservesHeading()) return;
		var player = Minecraft.getInstance().player;
		player.setYRot(HEADING.yaw());
		player.yBodyRot = HEADING.yaw(); player.yHeadRot = HEADING.yaw();
		player.yRotO = HEADING.yaw(); player.yBodyRotO = HEADING.yaw(); player.yHeadRotO = HEADING.yaw();
	}
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
		// An attack-assist target is a soft lock, not a one-tick candidate. Prefer it for
		// subsequent actions while it remains alive and inside the enlarged keep range;
		// otherwise a root-motion action that leaves the target behind would immediately
		// lose the target when the next animation starts.
		if (target != null && legal(target) && withinAssistLockRange(target)) return target;
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
	/**
	 * A locked action may travel far beyond the acquisition radius. This gate only applies while
	 * the YSS attack window is active, so ordinary target selection remains local and predictable.
	 */
	private static boolean withinAttackRange(LivingEntity entity)
	{
		var player = Minecraft.getInstance().player;
		return player != null && player.distanceToSqr(entity) <= config.attackRange() * config.attackRange();
	}

	/**
	 * Once automatic attack assist has selected a target, keep that target beyond the
	 * initial acquisition cone. This is deliberately separate from {@link #withinAcquisitionRange}
	 * so walking away does not immediately erase the target used by target VFX.
	 */
	private static boolean withinAssistLockRange(LivingEntity entity)
	{
		var player = Minecraft.getInstance().player;
		return player != null && player.distanceToSqr(entity) <= config.attackRange() * config.attackRange();
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
		onAttack(actionId, blocked, Float.NaN);
	}

	public static void onAttack(long actionId, String blocked, float rootMotionYaw)
	{
		onAttack(actionId, blocked, rootMotionYaw, "inherit");
	}

	public static void onAttack(long actionId, String blocked, float rootMotionYaw, String assistMode)
	{
		var mc = Minecraft.getInstance();
		if (mc.player == null || mc.isPaused()) return;
		lastEventTick = mc.player.tickCount;
		currentAction = actionId;
		actionAssistMode = assistMode == null ? "inherit" : assistMode;
		diagnostics.received();
		boolean actionStarted = false;
		if (WINDOW.observe(actionId))
		{
			HEADING.clear();
			actionStarted = true;
			freeFacing = false; manuallySuppressed = false;
			target = ActionAssistPolicy.targetForAction(actionAssistMode, target,
					entity -> legal(entity) && withinAssistLockRange(entity), CombatAssistService::chooseTarget);
			diagnostics.begin(target == null ? "none" : target.getDisplayName().getString());
			com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.LOGGER.debug(
					"Camera assist action start: id={}, mode={}, target={}", actionId, actionAssistMode,
					target == null ? "none" : target.getUUID());
		}
		if (!WINDOW.active()) return;
		if (steeringSuppressed())
		{
			// Keep identity available to VFX, but never acquire or steer during an off action.
			if (target != null && (!legal(target) || !withinAssistLockRange(target))) target = null;
			status = "camera steering disabled for animation";
			return;
		}
		if (!blocked.isEmpty() && !blocked.equals("root_motion") && !AttackHeading.temporaryBlock(blocked))
		{ cancel("YSS " + blocked); return; }
		if (!config.enabled() || !armed() || !TargetService.available()
				|| !ShoulderSurfingImpl.getInstance().isCameraDecoupled()) { cancel("camera unavailable"); return; }
		// Losing a target does not change the coordinate frame of an already launched attack.
		if (!"start_only".equals(actionAssistMode) && target != null && (!legal(target) || !withinAttackRange(target))) target = null;
		// Set the body's launch heading once. Re-facing every tick would rotate later root-motion
		// deltas while fixed-origin VFX continue along the original animation trajectory.
		if (actionStarted && target != null && !AttackHeading.temporaryBlock(blocked)
				&& TargetService.enabled()) correctFacing(true);
		HEADING.capture(rootMotionYaw);
		if (!HEADING.captured()) HEADING.capture(mc.player.getYRot());
		if (WINDOW.active())
		{
			HEADING.capture(mc.player.getYRot());
			applyAttackHeading();
			status = "preserving attack heading";
		}
	}

	private static void correctFacing()
	{
		correctFacing(false);
	}

	private static void correctFacing(boolean force)
	{
		var mc = Minecraft.getInstance(); var player = mc.player;
		if (!force && lastTick == player.tickCount) return;
		lastTick = player.tickCount;
		if (!armed()) { cancel("no sword"); return; }
		if (!ShoulderSurfingImpl.getInstance().isCameraDecoupled()) { cancel("coupled camera"); return; }
		if (mc.gameMode != null && mc.gameMode.isDestroying()) { cancel("mining block"); return; }
		if (!config.enabled() || !TargetService.enabled() || target == null || !legal(target)
				|| (WINDOW.active() && !"start_only".equals(actionAssistMode) && !withinAttackRange(target))) { cancel("target unavailable"); return; }
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
		// Root motion is authored together with fixed-origin VFX. Preserve its exact trajectory;
		// hit detection belongs to YSS hitboxes and must not move/clip the caster.
		return 1;
	}

	public static void endTick()
	{
		var mc = Minecraft.getInstance();
		if (mc.isPaused()) return;
		if (mc.player == null) { reset(); return; }
		if (!armed()) { manuallySuppressed = false; finishAction("no sword"); return; }
		if (lastEventTick == mc.player.tickCount) return;
		if (WINDOW.active()) finishAction("animation ended");
		// Holding a sword arms correction even without a YSS animation. Do not erase the
		// last action's diagnostics while idle or when opening chat to inspect them.
		if (manuallySuppressed || !config.enabled() || !TargetService.enabled()) return;
		// Idle automatic assistance stays local; a manual lock or active attack has no distance cap.
		if (target == null || !legal(target)
				|| (TargetService.target() == null && !withinAssistLockRange(target))
				|| (TargetService.target() != null && TargetService.target() != target))
			target = chooseTarget();
		freeFacing = target != null;
		if (freeFacing) correctFacing();
	}

	/** Ends the current attack control window while retaining a valid soft-lock target. */
	private static void finishAction(String reason)
	{
		if (diagnostics.finish(reason))
			com.github.exopandora.shouldersurfing.ShoulderSurfingCommon.LOGGER.info("Camera assist: {}", diagnostics.summary());
		WINDOW.stop();
		HEADING.clear();
		freeFacing = target != null && legal(target) && withinAssistLockRange(target);
		status = reason;
	}
}
