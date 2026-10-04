package com.github.exopandora.shouldersurfing.camera.assist;

import com.github.exopandora.shouldersurfing.math.Vec2f;

/** Convert target-relative input to the player's current yaw without changing speed. */
public final class LockedMovement
{
	private LockedMovement() {}
	public static Vec2f relative(float left, float forward, float targetYaw, float playerYaw)
	{
		return new Vec2f(left, forward).rotateDegrees(targetYaw - playerYaw);
	}
}
