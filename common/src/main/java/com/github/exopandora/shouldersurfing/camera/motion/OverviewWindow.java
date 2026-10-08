package com.github.exopandora.shouldersurfing.camera.motion;

/** Finite animation camera lease with smooth entry and explicit/automatic release. */
public final class OverviewWindow
{
	private double elapsed, duration, enter, exit, releaseElapsed = -1, releaseWeight;
	public void begin(double duration, double enter, double exit)
	{
		this.duration = duration; this.enter = enter; this.exit = exit;
		elapsed = 0; releaseElapsed = -1;
	}
	public void stop() { if (releaseElapsed < 0) { releaseWeight = weight(); releaseElapsed = 0; } }
	public void advance(double seconds)
	{
		if (seconds <= 0) return;
		if (releaseElapsed >= 0) { releaseElapsed += seconds; return; }
		double consumed = Math.min(seconds, Math.max(0, duration - elapsed));
		elapsed += consumed;
		if (elapsed >= duration) { stop(); releaseElapsed += seconds - consumed; }
	}
	public boolean finished() { return releaseElapsed >= exit; }
	public boolean releasing() { return releaseElapsed >= 0; }
	public double releaseProgress() { return smooth(releaseElapsed / exit); }
	public double weight() { return releaseElapsed < 0 ? smooth(elapsed / enter) : releaseWeight * (1 - smooth(releaseElapsed / exit)); }
	private static double smooth(double value) { double t = Math.max(0, Math.min(1, value)); return t * t * (3 - 2 * t); }
}
