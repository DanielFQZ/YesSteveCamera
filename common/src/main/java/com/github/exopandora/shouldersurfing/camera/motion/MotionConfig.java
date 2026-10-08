package com.github.exopandora.shouldersurfing.camera.motion;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record MotionConfig(boolean enabled, double startSeconds, double stopSeconds,
		double maxLag, double teleportDistance, boolean wheelZoom, double wheelStep,
		double groundStartSeconds, double groundStopSeconds, double groundMaxLag)
{
	public static final MotionConfig DEFAULT = new MotionConfig(true, 0.22, 0.35, 8, 64, true, 0.5, 0.06, 0.10, 1.25);
	/** Compatibility constructor for the original motion configuration format. */
	public MotionConfig(boolean enabled, double startSeconds, double stopSeconds,
			double maxLag, double teleportDistance, boolean wheelZoom, double wheelStep)
	{
		this(enabled, startSeconds, stopSeconds, maxLag, teleportDistance, wheelZoom, wheelStep,
				0.06, 0.10, 1.25);
	}
	public MotionConfig
	{
		if (!valid(startSeconds, 0.02, 2) || !valid(stopSeconds, 0.02, 2)
				|| !valid(maxLag, 0.1, 24) || !valid(teleportDistance, 32, 256) || !valid(wheelStep, 0.05, 4))
			throw new IllegalArgumentException("Invalid camera motion response, lag or wheel step");
		if (!valid(groundStartSeconds, 0.02, 2) || !valid(groundStopSeconds, 0.02, 2)
				|| !valid(groundMaxLag, 0.1, 24))
			throw new IllegalArgumentException("Invalid grounded motion response or lag");
	}
	static boolean valid(double value, double min, double max) { return Double.isFinite(value) && value >= min && value <= max; }
	public static MotionConfig load(Path path) throws IOException
	{
		var gson = new GsonBuilder().setPrettyPrinting().create();
		if (!Files.exists(path)) { Files.createDirectories(path.getParent()); Files.writeString(path, gson.toJson(DEFAULT)); }
		try (var reader = Files.newBufferedReader(path))
		{
			var json = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
			// Fill fields introduced after the original config format.
			if (!json.has("groundStartSeconds")) json.addProperty("groundStartSeconds", DEFAULT.groundStartSeconds());
			if (!json.has("groundStopSeconds")) json.addProperty("groundStopSeconds", DEFAULT.groundStopSeconds());
			if (!json.has("groundMaxLag")) json.addProperty("groundMaxLag", DEFAULT.groundMaxLag());
			MotionConfig value = gson.fromJson(json, MotionConfig.class);
			if (value == null) throw new IllegalArgumentException("Empty motion configuration");
			return value;
		}
		catch (RuntimeException e) { throw new IOException(path + ": " + e.getMessage(), e); }
	}
}
