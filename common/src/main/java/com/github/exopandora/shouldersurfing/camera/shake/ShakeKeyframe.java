package com.github.exopandora.shouldersurfing.camera.shake;

/** A scalar keyframe. Time is in seconds and value is channel-local. */
public record ShakeKeyframe(double time, double value, Easing easing)
{
	public ShakeKeyframe
	{
		if (!Double.isFinite(time) || time < 0.0D) throw new IllegalArgumentException("Invalid keyframe time");
		if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid keyframe value");
		if (easing == null) throw new IllegalArgumentException("easing cannot be null");
	}
}
