package com.github.exopandora.shouldersurfing.camera.shake;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Deterministic mixer. Slots replace their previous instance; different slots add. */
public final class ShakeMixer
{
	private final Map<String, ShakeInstance> instances = new LinkedHashMap<>();

	public void trigger(ShakePreset preset, String slot, float scale) { instances.put(slot, new ShakeInstance(preset, slot, scale)); }
	public void stop(String slot) { instances.remove(slot); }
	public void clear() { instances.clear(); }

	public void tick(double seconds)
	{
		for (Iterator<ShakeInstance> it = instances.values().iterator(); it.hasNext();)
		{
			ShakeInstance instance = it.next(); instance.tick(seconds); if (instance.finished()) it.remove();
		}
	}

	public ShakeSample sample(double partialSeconds)
	{
		ShakeSample result = ShakeSample.zero();
		for (ShakeInstance instance : instances.values()) result = result.add(instance.sample(partialSeconds));
		return result;
	}
}
