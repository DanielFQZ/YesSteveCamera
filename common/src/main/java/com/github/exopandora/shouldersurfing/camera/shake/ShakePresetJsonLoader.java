package com.github.exopandora.shouldersurfing.camera.shake;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Strict, fail-soft JSON loader for client shake presets. */
public final class ShakePresetJsonLoader
{
	private ShakePresetJsonLoader() {}

	public static List<ShakePreset> loadDirectory(Path directory)
	{
		if (!Files.isDirectory(directory)) return List.of();
		List<ShakePreset> result = new ArrayList<>();
		try (var paths = Files.list(directory))
		{
			paths.filter(path -> path.getFileName().toString().endsWith(".json")).sorted().forEach(path ->
			{
				try (Reader reader = Files.newBufferedReader(path)) { result.add(parse(JsonParser.parseReader(reader).getAsJsonObject())); }
				catch (Exception ignored) { }
			});
		}
		catch (Exception ignored) { return List.of(); }
		return List.copyOf(result);
	}

	private static ShakePreset parse(JsonObject root)
	{
		JsonArray tracksJson = requiredArray(root, "tracks");
		List<ShakeTrack> tracks = new ArrayList<>();
		for (JsonElement element : tracksJson) tracks.add(parseTrack(element.getAsJsonObject()));
		return new ShakePreset(requiredString(root, "id"), requiredNumber(root, "duration"), tracks);
	}

	private static ShakeTrack parseTrack(JsonObject object)
	{
		ShakeChannel channel = ShakeChannel.valueOf(requiredString(object, "channel").toUpperCase(Locale.ROOT));
		JsonArray axis = object.has("axis") ? object.getAsJsonArray("axis") : array(0, 0, 0);
		if (axis.size() != 3) throw new IllegalArgumentException("axis must have three values");
		ShakeEnvelope envelope = parseEnvelope(object.has("envelope") ? object.getAsJsonObject("envelope") : null);
		ShakeWave wave = parseWave(object.has("wave") ? object.getAsJsonObject("wave") : null);
		ShakeKeyframe[] keyframes = parseKeyframes(object.has("keyframes") ? object.getAsJsonArray("keyframes") : null);
		return new ShakeTrack(channel, axis.get(0).getAsDouble(), axis.get(1).getAsDouble(), axis.get(2).getAsDouble(),
				object.has("amplitude") ? object.get("amplitude").getAsDouble() : 1.0D, envelope, wave, keyframes);
	}

	private static ShakeEnvelope parseEnvelope(JsonObject object)
	{
		if (object == null) return new ShakeEnvelope(0, 0, 1, Easing.EASE_OUT);
		return new ShakeEnvelope(number(object, "attack", 0), number(object, "hold", 0), number(object, "release", 1), easing(object, "easing", Easing.EASE_OUT));
	}

	private static ShakeWave parseWave(JsonObject object)
	{
		if (object == null) return ShakeWave.none();
		return new ShakeWave(ShakeWave.Type.valueOf(requiredString(object, "type").toUpperCase(Locale.ROOT)), number(object, "frequency", 0), number(object, "damping", 0));
	}

	private static ShakeKeyframe[] parseKeyframes(JsonArray array)
	{
		if (array == null) return new ShakeKeyframe[0];
		ShakeKeyframe[] result = new ShakeKeyframe[array.size()];
		for (int i = 0; i < result.length; i++)
		{
			JsonObject frame = array.get(i).getAsJsonObject();
			result[i] = new ShakeKeyframe(requiredNumber(frame, "time"), requiredNumber(frame, "value"), easing(frame, "curve", Easing.LINEAR));
		}
		return result;
	}

	private static String requiredString(JsonObject object, String name) { if (!object.has(name)) throw new IllegalArgumentException("Missing " + name); return object.get(name).getAsString(); }
	private static double requiredNumber(JsonObject object, String name) { if (!object.has(name)) throw new IllegalArgumentException("Missing " + name); return object.get(name).getAsDouble(); }
	private static double number(JsonObject object, String name, double fallback) { return object.has(name) ? object.get(name).getAsDouble() : fallback; }
	private static Easing easing(JsonObject object, String name, Easing fallback) { return object.has(name) ? Easing.valueOf(object.get(name).getAsString().toUpperCase(Locale.ROOT)) : fallback; }
	private static JsonArray requiredArray(JsonObject object, String name) { if (!object.has(name) || !object.get(name).isJsonArray()) throw new IllegalArgumentException("Missing " + name); return object.getAsJsonArray(name); }
	private static JsonArray array(double x, double y, double z) { JsonArray result = new JsonArray(); result.add(x); result.add(y); result.add(z); return result; }
}
