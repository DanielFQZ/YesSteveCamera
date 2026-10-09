package com.github.exopandora.shouldersurfing.camera.assist;

import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

/** Distances are blocks, speed is additional blocks per client tick. */
public record AssistConfig(boolean enabled, boolean autoHostiles, double range, double stopGap,
		double resumeGap, double speed, double acceleration, int maxTicks, double turnDegrees,
		double attackRange)
{
	public static final AssistConfig DEFAULT = new AssistConfig(true, true, 6, 1.25, 1.6, 0.12, 0.03, 12, 25, 64);
	/** Compatibility constructor for integrations compiled against the previous config shape. */
	public AssistConfig(boolean enabled, boolean autoHostiles, double range, double stopGap,
			double resumeGap, double speed, double acceleration, int maxTicks, double turnDegrees)
	{
		this(enabled, autoHostiles, range, stopGap, resumeGap, speed, acceleration, maxTicks, turnDegrees, 64);
	}
	public AssistConfig
	{
		// Old assist.json files have no attackRange field; use the new default when Gson supplies 0.
		if (attackRange <= 0) attackRange = 64;
		if (!finite(range, 1, 8) || !finite(attackRange, 8, 256)
				|| !finite(stopGap, 0.3, 3) || !finite(resumeGap, stopGap + 0.1, 4)
				|| !finite(speed, 0.01, 0.2) || !finite(acceleration, 0.005, 0.1)
				|| maxTicks < 1 || maxTicks > 30 || !finite(turnDegrees, 1, 45))
			throw new IllegalArgumentException("Invalid assist ranges, speed, duration or turn limit");
	}
	private static boolean finite(double v, double min, double max) { return Double.isFinite(v) && v >= min && v <= max; }
	public static AssistConfig load(Path path) throws IOException
	{
		var gson = new GsonBuilder().setPrettyPrinting().create();
		if (!Files.exists(path)) { Files.createDirectories(path.getParent()); Files.writeString(path, gson.toJson(DEFAULT)); }
		try (var reader = Files.newBufferedReader(path))
		{
			AssistConfig value = gson.fromJson(reader, AssistConfig.class);
			if (value == null) throw new IllegalArgumentException("Empty configuration");
			return value;
		}
		catch (RuntimeException exception) { throw new IOException(path + ": " + exception.getMessage(), exception); }
	}
}
