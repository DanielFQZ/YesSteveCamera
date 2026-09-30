package com.github.exopandora.shouldersurfing.camera.shake;

/** Bounded easing functions used by keyframes and envelopes. */
public enum Easing
{
	LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT, SMOOTHSTEP;

	public double apply(double value)
	{
		double t = Math.max(0.0D, Math.min(1.0D, value));
		return switch (this)
		{
			case LINEAR -> t;
			case EASE_IN -> t * t;
			case EASE_OUT -> 1.0D - (1.0D - t) * (1.0D - t);
			case EASE_IN_OUT -> t < 0.5D ? 2.0D * t * t : 1.0D - Math.pow(-2.0D * t + 2.0D, 2.0D) / 2.0D;
			case SMOOTHSTEP -> t * t * (3.0D - 2.0D * t);
		};
	}
}
