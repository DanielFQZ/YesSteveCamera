package com.github.exopandora.shouldersurfing.camera.assist;

/** One sprint has either target-relative approach or free escape steering. */
public final class SprintSteering
{
	private boolean sprinting, released;
	private double mouseTurn;
	public void reset() { sprinting = false; released = false; mouseTurn = 0; }
	public void update(boolean activeSprint, float forward, float left, double viewAngleToTarget)
	{
		if (!activeSprint) { reset(); return; }
		if (!sprinting && Math.abs(viewAngleToTarget) > 75) released = true;
		sprinting = true;
		// Forward diagonals still approach; backwards or purely sideways input escapes.
		if (forward <= 0 && (forward != 0 || left != 0)) released = true;
	}
	public boolean mouse(double degrees)
	{
		if (!sprinting || released || !Double.isFinite(degrees)) return false;
		mouseTurn += degrees;
		if (Math.abs(mouseTurn) < 55) return false;
		released = true;
		return true;
	}
	public boolean released() { return released; }
	public double mouseTurn() { return mouseTurn; }
}
