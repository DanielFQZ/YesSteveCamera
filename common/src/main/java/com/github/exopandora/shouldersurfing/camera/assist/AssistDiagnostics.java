package com.github.exopandora.shouldersurfing.camera.assist;

/** Retains the last attack result when chat/menu lifecycle cleanup runs. */
public final class AssistDiagnostics
{
	private long events, actions;
	private String result = "no YSS attack event received";
	private String target = "";
	private double moved, turned, rootClipped;
	private boolean finished;

	public void received() { events++; }
	public void begin(String name)
	{
		actions++; target = name; moved = 0; turned = 0; rootClipped = 0; finished = false; result = "started";
	}
	public void progress(String state, double distance, double degrees)
	{
		result = state; moved += distance; turned += Math.abs(degrees);
	}
	public void clipped(double distance) { rootClipped += distance; result = "root motion contact"; }
	public boolean finish(String reason)
	{
		if (actions == 0 || finished) return false;
		finished = true; result = reason; return true;
	}
	public String summary()
	{
		return String.format(java.util.Locale.ROOT, "events=%d, actions=%d, last=%s, target=%s, moved=%.3f, turned=%.1f, rootClipped=%.3f",
				events, actions, result, target, moved, turned, rootClipped);
	}
}
