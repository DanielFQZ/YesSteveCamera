package com.github.exopandora.shouldersurfing.camera.assist;

/** A bounded window for one action. Stopped actions cannot reacquire control until a new action. */
public final class AssistWindow
{
	private long action = Long.MIN_VALUE;
	private boolean active, arrived;
	private int ticks;
	private double speed;
	public boolean observe(long id)
	{
		if (action == id) return false;
		action = id; active = true; arrived = false; ticks = 0; speed = 0;
		return true;
	}
	public boolean active() { return active; }
	public boolean arrived() { return arrived; }
	public void stop() { active = false; speed = 0; }
	public void reset() { stop(); action = Long.MIN_VALUE; }
	public double step(double gap, AssistConfig config)
	{
		if (!active || !Double.isFinite(gap) || ++ticks > config.maxTicks()) { stop(); return 0; }
		if (gap <= config.stopGap()) arrived = true;
		else if (gap >= config.resumeGap()) arrived = false;
		if (arrived) { speed = 0; return 0; }
		speed = Math.min(config.speed(), speed + config.acceleration());
		return Math.min(speed, Math.max(0, gap - config.stopGap()));
	}
}
