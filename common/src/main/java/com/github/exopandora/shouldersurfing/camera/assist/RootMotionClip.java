package com.github.exopandora.shouldersurfing.camera.assist;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Sweeps the player's box against one target; only shortens horizontal root motion. */
public final class RootMotionClip
{
	private static final double EPSILON = 1.0E-9;
	private static final double GAP = 0.02;
	private RootMotionClip() {}

	public static double horizontalScale(AABB self, AABB target, Vec3 delta)
	{
		if (!Double.isFinite(delta.x) || !Double.isFinite(delta.y) || !Double.isFinite(delta.z)) return 1;
		if (delta.horizontalDistanceSqr() < EPSILON) return 1;
		// Preserve vertical animation. No horizontal barrier when the swept heights are separate.
		if (self.maxY + Math.max(0, delta.y) <= target.minY
				|| self.minY + Math.min(0, delta.y) >= target.maxY) return 1;
		double minX = target.minX - self.maxX - GAP, maxX = target.maxX - self.minX + GAP;
		double minZ = target.minZ - self.maxZ - GAP, maxZ = target.maxZ - self.minZ + GAP;
		if (minX < 0 && maxX > 0 && minZ < 0 && maxZ > 0)
		{
			// Already overlapping (e.g. a mob walked into us): permit escape/tangential motion,
			// but do not push or teleport either entity to resolve the overlap.
			double toward = delta.x * (target.getCenter().x - self.getCenter().x)
					+ delta.z * (target.getCenter().z - self.getCenter().z);
			return toward > EPSILON ? 0 : 1;
		}
		double enter = 0, exit = 1;
		double[] minima = {minX, minZ}, maxima = {maxX, maxZ}, steps = {delta.x, delta.z};
		for (int axis = 0; axis < 2; axis++)
		{
			if (Math.abs(steps[axis]) < EPSILON)
			{
				if (minima[axis] >= 0 || maxima[axis] <= 0) return 1;
				continue;
			}
			double a = minima[axis] / steps[axis], b = maxima[axis] / steps[axis];
			enter = Math.max(enter, Math.min(a, b));
			exit = Math.min(exit, Math.max(a, b));
			if (enter >= exit) return 1;
		}
		return Math.max(0, Math.min(1, enter));
	}
}
