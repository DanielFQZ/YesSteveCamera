package com.github.exopandora.shouldersurfing.camera.duel;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Pure perspective fitting; no entity, world, or mutable render state. */
public final class DuelFraming
{
	public record Frame(Vec3 offset, boolean limited) {}
	private DuelFraming() {}
	public static double blend(double seconds, double smoothing) { return 1 - Math.exp(-Math.max(0, seconds) / smoothing); }
	public static double turn(double current, double desired, double amount)
	{
		double difference = ((desired - current) % 360 + 540) % 360 - 180;
		return current + difference * amount;
	}
	public static Vec3 forward(double yaw, double pitch)
	{
		double y = Math.toRadians(yaw), p = Math.toRadians(pitch);
		return new Vec3(-Math.sin(y) * Math.cos(p), -Math.sin(p), Math.cos(y) * Math.cos(p));
	}
	public static Frame fit(Vec3 eye, AABB player, AABB target, double yaw, double pitch,
			double verticalFov, double aspect, DuelConfig config)
	{
		Vec3 focus = player.getCenter().lerp(target.getCenter(), 0.5);
		Vec3 forward = forward(yaw, pitch);
		Vec3 right = new Vec3(-Math.cos(Math.toRadians(yaw)), 0, -Math.sin(Math.toRadians(yaw)));
		Vec3 up = right.cross(forward);
		double tanY = Math.tan(Math.toRadians(Math.max(20, Math.min(100, verticalFov))) / 2) * 0.78;
		double tanX = tanY * Math.max(0.3, Math.min(4, aspect));
		double distance = config.minDistance();
		for (AABB box : new AABB[]{player, target})
			for (int corner = 0; corner < 8; corner++)
			{
				Vec3 point = new Vec3((corner & 1) == 0 ? box.minX : box.maxX,
						(corner & 2) == 0 ? box.minY : box.maxY, (corner & 4) == 0 ? box.minZ : box.maxZ).subtract(focus);
				double depth = point.dot(forward);
				distance = Math.max(distance, Math.max(Math.abs(point.dot(right)) / tanX, Math.abs(point.dot(up)) / tanY) - depth);
			}
		Vec3 offset = focus.subtract(eye).subtract(forward.scale(distance));
		boolean limited = offset.length() > config.maxDistance();
		if (limited)
		{
			// When both subjects cannot fit, keep the player visible on the left rather
			// than clamping a remote midpoint and potentially looking past the player.
			offset = player.getCenter().subtract(eye).add(right.scale(config.maxDistance() * 0.18))
					.subtract(forward.scale(config.maxDistance()));
			if (offset.length() > config.maxDistance()) offset = offset.normalize().scale(config.maxDistance());
		}
		return new Frame(offset, limited);
	}
}
