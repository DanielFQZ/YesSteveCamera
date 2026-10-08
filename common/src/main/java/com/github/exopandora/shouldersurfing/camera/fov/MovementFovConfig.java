package com.github.exopandora.shouldersurfing.camera.fov;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Independent of movement attributes and any combat mod. */
public record MovementFovConfig(Mode mode, double sprintMultiplier, double enterSeconds,
        double exitSeconds, boolean shoulderOnly)
{
    public enum Mode { VANILLA, STABLE, SPRINT }
    public static final MovementFovConfig DEFAULT = new MovementFovConfig(Mode.SPRINT, 1.08, 0.20, 0.25, true);

    public MovementFovConfig
    {
        if (mode == null || !valid(sprintMultiplier, 1, 1.3)
                || !valid(enterSeconds, 0, 2) || !valid(exitSeconds, 0, 2))
            throw new IllegalArgumentException("Invalid FOV mode, multiplier (1..1.3) or transition seconds (0..2)");
    }

    private static boolean valid(double value, double min, double max)
    {
        return Double.isFinite(value) && value >= min && value <= max;
    }

    public static MovementFovConfig load(Path path) throws IOException
    {
        var gson = new GsonBuilder().setPrettyPrinting().create();
        if (!Files.exists(path))
        {
            Files.createDirectories(path.getParent());
            Files.writeString(path, gson.toJson(DEFAULT));
        }
        try (var reader = Files.newBufferedReader(path))
        {
            var json = JsonParser.parseReader(reader).getAsJsonObject();
            // Allow small hand-written configs without silently zeroing omitted fields.
            var defaults = gson.toJsonTree(DEFAULT).getAsJsonObject();
            for (var entry : defaults.entrySet())
                if (!json.has(entry.getKey())) json.add(entry.getKey(), entry.getValue());
            return gson.fromJson(json, MovementFovConfig.class);
        }
        catch (RuntimeException exception)
        {
            throw new IOException(path + ": " + exception.getMessage(), exception);
        }
    }
}
