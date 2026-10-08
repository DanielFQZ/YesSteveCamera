package com.github.exopandora.shouldersurfing.camera.duel;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record DuelConfig(boolean enabled, double sideAngle, double minDistance, double maxDistance,
		double smoothSeconds, double positionSmoothSeconds, double mouseYawLimit, double mousePitchLimit)
{
	public static final DuelConfig DEFAULT = new DuelConfig(true, 35, 3.5, 12, 0.3, 0.12, 20, 12);
	/** Compatibility constructor for the original duel configuration format. */
	public DuelConfig(boolean enabled, double sideAngle, double minDistance, double maxDistance,
			double smoothSeconds, double mouseYawLimit, double mousePitchLimit)
	{
		this(enabled, sideAngle, minDistance, maxDistance, smoothSeconds, 0.12, mouseYawLimit, mousePitchLimit);
	}
	public DuelConfig
	{
		if (!valid(sideAngle, 10, 70) || !valid(minDistance, 1, 12) || !valid(maxDistance, minDistance, 24)
				|| !valid(smoothSeconds, 0.05, 2) || !valid(positionSmoothSeconds, 0.03, 1)
				|| !valid(mouseYawLimit, 0, 45) || !valid(mousePitchLimit, 0, 30))
			throw new IllegalArgumentException("Invalid duel camera angle, distance or smoothing");
	}
	private static boolean valid(double value, double min, double max) { return Double.isFinite(value) && value >= min && value <= max; }
	public static DuelConfig load(Path path) throws IOException
	{
		var gson = new GsonBuilder().setPrettyPrinting().create();
		if (!Files.exists(path)) { Files.createDirectories(path.getParent()); Files.writeString(path, gson.toJson(DEFAULT)); }
		try (var reader = Files.newBufferedReader(path))
		{
			var json = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
			if (!json.has("positionSmoothSeconds")) json.addProperty("positionSmoothSeconds", DEFAULT.positionSmoothSeconds());
			DuelConfig config = gson.fromJson(json, DuelConfig.class);
			if (config == null) throw new IllegalArgumentException("Empty duel configuration");
			return config;
		}
		catch (RuntimeException exception) { throw new IOException(path + ": " + exception.getMessage(), exception); }
	}
}
