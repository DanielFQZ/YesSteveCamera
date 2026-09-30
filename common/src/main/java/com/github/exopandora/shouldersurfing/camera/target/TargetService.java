package com.github.exopandora.shouldersurfing.camera.target;

import com.github.exopandora.shouldersurfing.api.target.TargetClassification;
import com.github.exopandora.shouldersurfing.api.target.TargetSnapshot;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** Main-thread lock lifecycle; only the published immutable snapshot is shared. */
public final class TargetService
{
	private static final LockButton BUTTON = new LockButton();
	private static TargetingConfig config = TargetingConfig.DEFAULT;
	private static ClientLevel level;
	private static UUID owner;
	private static UUID session = UUID.randomUUID();
	private static LivingEntity target;
	private static int hiddenTicks;
	private static long selectionVersion;
	private static volatile TargetSnapshot snapshot;

	private TargetService() {}

	public static TargetSnapshot snapshot() { return snapshot; }
	public static void configure(TargetingConfig value) { config = value; cancel(); }
	public static TargetingConfig config() { return config; }

	public static boolean enabled()
	{
		Minecraft mc = Minecraft.getInstance();
		return mc.level != null && mc.player != null && mc.player.isAlive() && !mc.player.isSpectator()
				&& mc.screen == null && mc.getCameraEntity() == mc.player && ShoulderSurfingImpl.getInstance().isShoulderSurfing();
	}

	public static void tick(boolean buttonDown)
	{
		Minecraft mc = Minecraft.getInstance();
		UUID currentOwner = mc.player == null ? null : mc.player.getUUID();
		if(level != mc.level || !java.util.Objects.equals(owner, currentOwner))
		{
			level = mc.level;
			owner = currentOwner;
			session = UUID.randomUUID();
			cancel();
			BUTTON.suppressUntilRelease();
		}
		if(!enabled())
		{
			cancel();
			BUTTON.suppressUntilRelease();
			return;
		}
		if(mc.isPaused()) return;
		switch(BUTTON.tick(buttonDown, config.holdTicks()))
		{
			case SELECT -> selectNext();
			case CANCEL -> cancel();
			default -> { }
		}
		if(target == null) return;
		if(!legal(target) || mc.player.distanceToSqr(target) > config.range() * config.range())
		{
			cancel();
			return;
		}
		hiddenTicks = mc.player.hasLineOfSight(target) ? 0 : hiddenTicks + 1;
		if(hiddenTicks > config.occlusionGraceTicks()) { cancel(); return; }
		publish();
	}

	public static void selectNext()
	{
		if(!enabled()) return;
		Minecraft mc = Minecraft.getInstance();
		var camera = ShoulderSurfingImpl.getInstance().getCamera();
		Vec3 forward = Vec3.directionFromRotation(camera.getXRot(), camera.getYRot());
		Vec3 origin = mc.player.getEyePosition();
		double minDot = Math.cos(Math.toRadians(config.coneDegrees()));
		List<LivingEntity> candidates = mc.level.getEntitiesOfClass(LivingEntity.class, mc.player.getBoundingBox().inflate(config.range()), TargetService::legal)
				.stream().filter(entity -> mc.player.distanceToSqr(entity) <= config.range() * config.range())
				.filter(entity -> direction(origin, entity).dot(forward) >= minDot)
				.sorted(Comparator.<LivingEntity>comparingDouble(entity -> -direction(origin, entity).dot(forward))
						.thenComparingDouble(mc.player::distanceToSqr).thenComparing(LivingEntity::getUUID))
				.limit(128).filter(mc.player::hasLineOfSight).toList();
		if(candidates.isEmpty()) return;
		int current = candidates.indexOf(target);
		target = candidates.get((current + 1) % candidates.size());
		hiddenTicks = 0;
		selectionVersion++;
		publish();
	}

	private static Vec3 direction(Vec3 origin, LivingEntity entity)
	{
		return entity.getBoundingBox().getCenter().subtract(origin).normalize();
	}

	private static boolean legal(LivingEntity entity)
	{
		Minecraft mc = Minecraft.getInstance();
		return entity != mc.player && entity.isAlive() && !entity.isRemoved() && !entity.isSpectator()
				&& entity.level() == mc.level && classify(entity) != TargetClassification.PROTECTED;
	}

	public static TargetClassification classify(LivingEntity entity)
	{
		Player player = Minecraft.getInstance().player;
		if(entity == player || (player != null && entity.isAlliedTo(player))
				|| (entity instanceof TamableAnimal pet && player != null && pet.isOwnedBy(player))) return TargetClassification.PROTECTED;
		TargetClassification explicit = config.entities().get(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
		if(explicit != null) return explicit;
		if(entity instanceof Player) return TargetClassification.PROTECTED;
		return entity instanceof Enemy ? TargetClassification.HOSTILE : TargetClassification.MANUAL_ONLY;
	}

	private static void publish()
	{
		Minecraft mc = Minecraft.getInstance();
		snapshot = new TargetSnapshot(session, mc.player.getUUID(), target.getUUID(), mc.level.dimension(),
				target.getBoundingBox().getCenter(), mc.level.getGameTime(), selectionVersion, classify(target));
	}

	public static String targetName() { return target == null ? "" : target.getDisplayName().getString(); }

	/** Applies only the combat body yaw; camera look direction remains independent. */
	public static void applyPlayerFacing()
	{
		if(target == null || !enabled()) return;
		Minecraft mc = Minecraft.getInstance();
		Vec3 delta = target.getBoundingBox().getCenter().subtract(mc.player.getEyePosition());
		if(delta.horizontalDistanceSqr() < 1.0E-8D) return;
		float desired = (float) (Mth.atan2(-delta.x, delta.z) * Mth.RAD_TO_DEG);
		float current = mc.player.getYRot();
		float difference = Mth.wrapDegrees(desired - current);
		float step = Mth.clamp(difference, -30.0F, 30.0F);
		mc.player.setYRot(current + step);
		mc.player.yBodyRot = mc.player.getYRot();
		mc.player.yHeadRot = mc.player.getYRot();
	}

	public static void cancel()
	{
		if(target != null) selectionVersion++;
		target = null;
		snapshot = null;
		hiddenTicks = 0;
	}
}
