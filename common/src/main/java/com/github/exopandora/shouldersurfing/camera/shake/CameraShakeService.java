package com.github.exopandora.shouldersurfing.camera.shake;

import com.github.exopandora.shouldersurfing.mixinducks.CameraDuck;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Client-side shake registry and deterministic runtime. */
public final class CameraShakeService
{
	private static final Map<String, ShakePreset> PRESETS = new HashMap<>();
	private static final ShakeMixer MIXER = new ShakeMixer();
	private static long sampleTick = Long.MIN_VALUE;
	private static int samplePartial;
	private static ShakeSample cachedSample = ShakeSample.zero();

	private CameraShakeService() {}

	public static void reload(Path gameDirectory)
	{
		Map<String, ShakePreset> loaded = new HashMap<>(defaults());
		for (ShakePreset preset : ShakePresetJsonLoader.loadDirectory(gameDirectory.resolve("config/yesstevecamera/shakes"))) loaded.put(preset.id(), preset);
		PRESETS.clear(); PRESETS.putAll(loaded); invalidateSample();
		MIXER.clear();
	}

	public static void trigger(String presetId, String slot, float scale)
	{
		ShakePreset preset = PRESETS.get(presetId);
		if (preset != null) MIXER.trigger(preset, slot, scale);
		invalidateSample();
	}

	public static void stop(String slot) { MIXER.stop(slot); invalidateSample(); }

	public static void tick()
	{
		if (Minecraft.getInstance().isPaused()) return;
		MIXER.tick(1.0D / 20.0D); invalidateSample();
	}

	public static ShakeSample sample(float partialTick)
	{
		Minecraft minecraft = Minecraft.getInstance();
		long tick = minecraft.player == null ? Long.MIN_VALUE : minecraft.player.tickCount;
		int partialBits = Float.floatToIntBits(partialTick);
		if (sampleTick != tick || samplePartial != partialBits)
		{
			sampleTick = tick; samplePartial = partialBits; cachedSample = MIXER.sample(partialTick / 20.0D);
		}
		return cachedSample;
	}

	public static void apply(Camera camera, float partialTick)
	{
		if (camera instanceof CameraDuck shakeCamera)
		{
			ShakeSample sample = sample(partialTick);
			shakeCamera.shouldersurfing$applyShake(sample.translateX(), sample.translateY(), sample.translateZ(), sample.rotateX(), sample.rotateY(), sample.rotateZ());
		}
	}

	public static float applyFov(float fov, float partialTick) { return fov + sample(partialTick).fov(); }
	public static boolean hasPreset(String id) { return PRESETS.containsKey(id); }
	public static int presetCount() { return PRESETS.size(); }

	private static Map<String, ShakePreset> defaults()
	{
		Map<String, ShakePreset> defaults = new HashMap<>();
		ShakeEnvelope envelope = new ShakeEnvelope(0.015D, 0.02D, 0.24D, Easing.EASE_OUT);
		ShakeTrack rotation = new ShakeTrack(ShakeChannel.ROTATION, 1, 0.12D, 0, 6, envelope,
				new ShakeWave(ShakeWave.Type.DAMPED, 18, 9), new ShakeKeyframe[0]);
		ShakeTrack translation = new ShakeTrack(ShakeChannel.TRANSLATION, 0, 0, -1, 0.06D, envelope,
				ShakeWave.none(), new ShakeKeyframe[]{new ShakeKeyframe(0, 0, Easing.LINEAR), new ShakeKeyframe(0.04D, 1, Easing.EASE_OUT), new ShakeKeyframe(0.24D, 0, Easing.EASE_OUT)});
		defaults.put("yesstevecamera:light_hit", new ShakePreset("yesstevecamera:light_hit", 0.275D, java.util.List.of(rotation, translation)));
		return defaults;
	}
	private static void invalidateSample() { sampleTick = Long.MIN_VALUE; }
}
