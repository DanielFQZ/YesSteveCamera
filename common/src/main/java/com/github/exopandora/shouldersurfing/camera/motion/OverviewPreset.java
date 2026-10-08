package com.github.exopandora.shouldersurfing.camera.motion;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public record OverviewPreset(String id, double minDistance, double maxDistance, double distance,
		double horizontalMargin, double verticalMargin, double enterSeconds, double exitSeconds)
{
	/** A fixed distance keeps action shots readable; set distance to 0 for automatic framing. */
	public static final OverviewPreset DEFAULT = new OverviewPreset("yesstevecamera:action_overview", 8, 24, 14, 3, 6, 0.45, 0.65);
	/** Compatibility constructor for callers using the pre-distance format. */
	public OverviewPreset(String id, double minDistance, double maxDistance, double horizontalMargin,
			double verticalMargin, double enterSeconds, double exitSeconds)
	{
		this(id, minDistance, maxDistance, 0, horizontalMargin, verticalMargin, enterSeconds, exitSeconds);
	}
	public OverviewPreset
	{
		if (id == null || !id.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")
				|| !MotionConfig.valid(minDistance, 1, 12) || !MotionConfig.valid(maxDistance, minDistance, 24)
				|| !MotionConfig.valid(distance, 0, 64)
				|| !MotionConfig.valid(horizontalMargin, 0, 24) || !MotionConfig.valid(verticalMargin, 0, 32)
				|| !MotionConfig.valid(enterSeconds, 0.05, 5) || !MotionConfig.valid(exitSeconds, 0.05, 5))
			throw new IllegalArgumentException("Invalid overview preset id, framing or transition");
	}
	public static Map<String, OverviewPreset> load(Path directory) throws IOException
	{
		Files.createDirectories(directory);
		var gson = new GsonBuilder().setPrettyPrinting().create();
		Path builtin = directory.resolve("action_overview.json");
		if (!Files.exists(builtin)) Files.writeString(builtin, gson.toJson(DEFAULT));
		Map<String, OverviewPreset> result = new TreeMap<>();
		try (var files = Files.list(directory))
		{
			for (Path path : files.filter(p -> p.getFileName().toString().endsWith(".json")).sorted().toList())
				try (var reader = Files.newBufferedReader(path))
				{
					OverviewPreset preset = gson.fromJson(reader, OverviewPreset.class);
					if (preset == null || result.putIfAbsent(preset.id(), preset) != null)
						throw new IllegalArgumentException("Empty or duplicate preset");
				}
				catch (RuntimeException e) { throw new IOException(path + ": " + e.getMessage(), e); }
		}
		return Map.copyOf(result);
	}
}
