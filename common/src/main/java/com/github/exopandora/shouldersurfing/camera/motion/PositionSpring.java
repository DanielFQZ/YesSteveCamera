package com.github.exopandora.shouldersurfing.camera.motion;

import net.minecraft.world.phys.Vec3;

/** Exact critically damped step for a fixed target, independent of render frame rate. */
public final class PositionSpring
{
	private Vec3 position, velocity = Vec3.ZERO;
	public void reset() { position = null; velocity = Vec3.ZERO; }
	public void snap(Vec3 value) { position = value; velocity = Vec3.ZERO; }
	public Vec3 step(Vec3 target, double seconds, double response)
	{
		if (position == null) snap(target);
		if (seconds <= 0) return position;
		double omega = 2 / response, decay = Math.exp(-omega * seconds);
		Vec3 error = position.subtract(target);
		Vec3 change = velocity.add(error.scale(omega));
		position = target.add(error.add(change.scale(seconds)).scale(decay));
		velocity = velocity.subtract(change.scale(omega * seconds)).scale(decay);
		return position;
	}
}
