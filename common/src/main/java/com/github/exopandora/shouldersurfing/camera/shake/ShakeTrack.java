package com.github.exopandora.shouldersurfing.camera.shake;

import java.util.Arrays;
import java.util.Comparator;

/** One scalar camera track. A track may use keyframes or an oscillator. */
public final class ShakeTrack
{
	private final ShakeChannel channel;
	private final double axisX, axisY, axisZ, amplitude;
	private final ShakeEnvelope envelope;
	private final ShakeWave wave;
	private final ShakeKeyframe[] keyframes;

	public ShakeTrack(ShakeChannel channel, double axisX, double axisY, double axisZ, double amplitude,
			ShakeEnvelope envelope, ShakeWave wave, ShakeKeyframe[] keyframes)
	{
		if (channel == null || envelope == null || wave == null || keyframes == null) throw new IllegalArgumentException("Track fields cannot be null");
		if (!Double.isFinite(amplitude) || amplitude < 0.0D) throw new IllegalArgumentException("Invalid amplitude");
		if (!Double.isFinite(axisX) || !Double.isFinite(axisY) || !Double.isFinite(axisZ)) throw new IllegalArgumentException("Invalid axis");
		this.channel = channel; this.axisX = axisX; this.axisY = axisY; this.axisZ = axisZ; this.amplitude = amplitude;
		this.envelope = envelope; this.wave = wave; this.keyframes = keyframes.clone();
		Arrays.sort(this.keyframes, Comparator.comparingDouble(ShakeKeyframe::time));
	}

	public ShakeChannel channel() { return channel; }
	public double axisX() { return axisX; }
	public double axisY() { return axisY; }
	public double axisZ() { return axisZ; }

	public double sample(double seconds)
	{
		if (seconds < 0.0D || seconds >= envelope.duration()) return 0.0D;
		double value = keyframes.length == 0 ? wave.sample(seconds) : sampleKeyframes(seconds);
		return amplitude * envelope.sample(seconds) * value;
	}

	private double sampleKeyframes(double seconds)
	{
		if (seconds <= keyframes[0].time()) return keyframes[0].value();
		for (int i = 1; i < keyframes.length; i++)
		{
			ShakeKeyframe right = keyframes[i];
			if (seconds <= right.time())
			{
				ShakeKeyframe left = keyframes[i - 1];
				double duration = right.time() - left.time();
				double progress = duration <= 0.0D ? 1.0D : (seconds - left.time()) / duration;
				return left.value() + (right.value() - left.value()) * right.easing().apply(progress);
			}
		}
		return keyframes[keyframes.length - 1].value();
	}
}
