package com.github.exopandora.shouldersurfing.camera.assist;

/** The authored heading survives temporary targeting suspension, never a new action. */
public final class AttackHeading
{
	private float yaw = Float.NaN;
	public void clear() { yaw = Float.NaN; }
	public boolean captured() { return Float.isFinite(yaw); }
	public float yaw() { return yaw; }
	public void capture(float value) { if (!captured() && Float.isFinite(value)) yaw = value; }
	public static boolean temporaryBlock(String reason)
	{
		return reason.equals("hover") || reason.equals("caster_move") || reason.equals("hitstop");
	}
}
