package com.github.exopandora.shouldersurfing.camera.shake;

/** Attack/hold/release envelope, all values in seconds. */
public record ShakeEnvelope(double attack, double hold, double release, Easing easing)
{
	public ShakeEnvelope
	{
		if (!Double.isFinite(attack) || attack < 0.0D) throw new IllegalArgumentException("Invalid attack");
		if (!Double.isFinite(hold) || hold < 0.0D) throw new IllegalArgumentException("Invalid hold");
		if (!Double.isFinite(release) || release < 0.0D) throw new IllegalArgumentException("Invalid release");
		if (easing == null) throw new IllegalArgumentException("easing cannot be null");
		if (attack + hold + release <= 0.0D) throw new IllegalArgumentException("Envelope duration must be positive");
	}

	public double duration() { return attack + hold + release; }

	public double sample(double seconds)
	{
		if (seconds <= 0.0D) return attack == 0.0D ? 1.0D : 0.0D;
		if (seconds < attack && attack > 0.0D) return easing.apply(seconds / attack);
		double releaseStart = attack + hold;
		if (seconds < releaseStart || release == 0.0D) return 1.0D;
		if (seconds >= duration()) return 0.0D;
		return 1.0D - easing.apply((seconds - releaseStart) / release);
	}
}
