package com.github.exopandora.shouldersurfing.camera.shake;

/** Optional oscillator applied inside a track envelope. */
public record ShakeWave(Type type, double frequency, double damping)
{
	public enum Type { NONE, SINE, TRIANGLE, DAMPED }

	public ShakeWave
	{
		if (type == null) throw new IllegalArgumentException("type cannot be null");
		if (!Double.isFinite(frequency) || frequency < 0.0D) throw new IllegalArgumentException("Invalid frequency");
		if (!Double.isFinite(damping) || damping < 0.0D) throw new IllegalArgumentException("Invalid damping");
	}

	public static ShakeWave none() { return new ShakeWave(Type.NONE, 0.0D, 0.0D); }

	public double sample(double seconds)
	{
		double phase = seconds * frequency * Math.PI * 2.0D;
		return switch (type)
		{
			case NONE -> 1.0D;
			case SINE -> Math.sin(phase);
			case TRIANGLE -> 2.0D / Math.PI * Math.asin(Math.sin(phase));
			case DAMPED -> Math.sin(phase) * Math.exp(-damping * Math.max(0.0D, seconds));
		};
	}
}
