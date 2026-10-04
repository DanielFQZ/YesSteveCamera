package com.github.exopandora.shouldersurfing.camera.shake;

import java.util.List;

/** Validated, immutable shake definition loaded from a config resource. */
public record ShakePreset(String id, double duration, List<ShakeTrack> tracks)
{
	public ShakePreset
	{
		if (id == null || !id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) throw new IllegalArgumentException("id must be namespace:path");
		if (!Double.isFinite(duration) || duration <= 0.0D || duration > 60) throw new IllegalArgumentException("duration must be within (0, 60] seconds");
		if (tracks == null || tracks.size() > 64) throw new IllegalArgumentException("tracks cannot be null");
		tracks = List.copyOf(tracks);
	}
}
