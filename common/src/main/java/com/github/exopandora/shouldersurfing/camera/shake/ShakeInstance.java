package com.github.exopandora.shouldersurfing.camera.shake;

/** One triggered preset instance. Time advances only from client ticks. */
public final class ShakeInstance
{
	private final ShakePreset preset;
	private final String slot;
	private final float scale;
	private double elapsed;

	public ShakeInstance(ShakePreset preset, String slot, float scale)
	{
		if (preset == null || slot == null || slot.isBlank()) throw new IllegalArgumentException("Invalid shake instance");
		if (!Float.isFinite(scale) || scale < 0.0F) throw new IllegalArgumentException("Invalid shake scale");
		this.preset = preset; this.slot = slot; this.scale = scale;
	}

	public String slot() { return slot; }
	public boolean finished() { return elapsed >= preset.duration(); }
	public void tick(double seconds) { if (Double.isFinite(seconds) && seconds > 0.0D) elapsed += seconds; }

	public ShakeSample sample(double partialSeconds)
	{
		double time = elapsed + Math.max(0.0D, partialSeconds);
		ShakeSample sample = ShakeSample.zero();
		for (ShakeTrack track : preset.tracks())
		{
			double value = track.sample(time) * scale;
			switch (track.channel())
			{
				case TRANSLATION -> sample = sample.add(new ShakeSample(value * track.axisX(), value * track.axisY(), value * track.axisZ(), 0, 0, 0, 0, 0));
				case ROTATION -> sample = sample.add(new ShakeSample(0, 0, 0, (float) (value * track.axisX()), (float) (value * track.axisY()), (float) (value * track.axisZ()), 0, 0));
				case FOV -> sample = sample.add(new ShakeSample(0, 0, 0, 0, 0, 0, (float) value, 0));
				case DISTANCE -> sample = sample.add(new ShakeSample(0, 0, 0, 0, 0, 0, 0, value));
			}
		}
		return sample;
	}
}
