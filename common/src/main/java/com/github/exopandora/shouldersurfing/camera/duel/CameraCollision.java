package com.github.exopandora.shouldersurfing.camera.duel;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Final visibility volume after skill camera translation and shake have been composed. */
public final class CameraCollision
{
	private CameraCollision() {}
	public static Vec3 constrain(Entity viewer, Vec3 eye, Vec3 desired)
	{
		double length = eye.distanceTo(desired);
		if (length < 1.0E-8) return desired;
		double allowed = length;
		for (int i = 0; i < 8; i++)
		{
			Vec3 padding = new Vec3((i & 1) == 0 ? -0.12 : 0.12,
					(i & 2) == 0 ? -0.12 : 0.12, (i & 4) == 0 ? -0.12 : 0.12);
			Vec3 start = eye.add(padding), end = desired.add(padding);
			var hit = viewer.level().clip(new ClipContext(start, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, viewer));
			if (hit.getType() != HitResult.Type.MISS) allowed = Math.min(allowed, Math.max(0, start.distanceTo(hit.getLocation()) - 0.05));
		}
		return eye.lerp(desired, allowed / length);
	}
}
