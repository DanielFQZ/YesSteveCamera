package com.github.exopandora.shouldersurfing.camera.shake;

import java.util.List;

/** Validated, immutable shake definition loaded from a config resource. */
public record ShakePreset(String id, double duration, List<ShakeTrack> tracks)
{
	public ShakePreset
	{
		if (id == null || id.isBlank()) throw new IllegalArgumentException("id cannot be blank");
		if (!Double.isFinite(duration) || duration <= 0.0D) throw new IllegalArgumentException("duration must be positive");
		if (tracks == null) throw new IllegalArgumentException("tracks cannot be null");
		tracks = List.copyOf(tracks);
	}
}
