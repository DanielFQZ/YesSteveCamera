package com.github.exopandora.shouldersurfing.camera.target;

import com.github.exopandora.shouldersurfing.api.target.TargetClassification;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Client rules only; they never grant server-side attack permissions. */
public record TargetingConfig(double range, double coneDegrees, int holdTicks, int occlusionGraceTicks,
		Map<String, TargetClassification> entities, boolean faceTarget)
{
	public static final TargetingConfig DEFAULT = new TargetingConfig(24, 70, 9, 20, Map.of(), false);
	public TargetingConfig
	{
		if(!Double.isFinite(range) || range < 1 || range > 64) throw new IllegalArgumentException("range: 1..64");
		if(!Double.isFinite(coneDegrees) || coneDegrees < 1 || coneDegrees > 89) throw new IllegalArgumentException("cone_degrees: 1..89");
		if(holdTicks < 2 || holdTicks > 40 || occlusionGraceTicks < 0 || occlusionGraceTicks > 100) throw new IllegalArgumentException("Invalid hold/grace ticks");
		entities = Map.copyOf(entities);
	}

	public static TargetingConfig load(Path path) throws IOException
	{
		if(!Files.exists(path))
		{
			Files.createDirectories(path.getParent());
			Files.writeString(path, """
					{
					  "schema_version": 1,
					  "range": 24,
					  "cone_degrees": 70,
					  "hold_ticks": 9,
					  "occlusion_grace_ticks": 20,
					  "entities": {},
					  "face_target": false
					}
					""");
		}
		try(Reader reader = Files.newBufferedReader(path))
		{
			JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
			if(number(root, "schema_version", 1) != 1) throw new IllegalArgumentException("Unsupported targeting schema");
			Map<String, TargetClassification> rules = new HashMap<>();
			if(root.has("entities")) root.getAsJsonObject("entities").entrySet().forEach(entry ->
			{
				if(!entry.getKey().matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) throw new IllegalArgumentException("Invalid entity ID: " + entry.getKey());
				rules.put(entry.getKey(), TargetClassification.valueOf(entry.getValue().getAsString()));
			});
			return new TargetingConfig(number(root, "range", 24), number(root, "cone_degrees", 70),
					integer(root, "hold_ticks", 9), integer(root, "occlusion_grace_ticks", 20), rules, root.has("face_target") && root.get("face_target").getAsBoolean());
		}
		catch(RuntimeException exception)
		{
			throw new IOException(path + ": " + exception.getMessage(), exception);
		}
	}

	private static int integer(JsonObject root, String key, int fallback)
	{
		double value = number(root, key, fallback);
		if(value != Math.rint(value)) throw new IllegalArgumentException(key + " must be an integer");
		return (int) value;
	}

	private static double number(JsonObject root, String key, double fallback)
	{
		if(!root.has(key)) return fallback;
		if(!root.get(key).isJsonPrimitive() || !root.getAsJsonPrimitive(key).isNumber()) throw new IllegalArgumentException(key + " must be numeric");
		return root.get(key).getAsDouble();
	}
}
