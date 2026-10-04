package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.shake.*;
import com.github.exopandora.shouldersurfing.camera.target.LockButton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class CameraLogicTest
{
	@TempDir Path directory;

	@Test void lockShortPressSelectsOnceAndLongPressCancelsOnce()
	{
		LockButton button = new LockButton();
		assertEquals(LockButton.Action.NONE, button.tick(true, 3));
		assertEquals(LockButton.Action.SELECT, button.tick(false, 3));
		assertEquals(LockButton.Action.NONE, button.tick(false, 3));
		button.tick(true, 3); button.tick(true, 3);
		assertEquals(LockButton.Action.CANCEL, button.tick(true, 3));
		assertEquals(LockButton.Action.NONE, button.tick(true, 3));
		assertEquals(LockButton.Action.NONE, button.tick(false, 3));
	}

	@Test void heldKeyAcrossGuiDoesNotSelect()
	{
		LockButton button = new LockButton();
		button.tick(true, 3); button.suppressUntilRelease();
		assertEquals(LockButton.Action.NONE, button.tick(true, 3));
		assertEquals(LockButton.Action.NONE, button.tick(false, 3));
		button.tick(true, 3);
		assertEquals(LockButton.Action.SELECT, button.tick(false, 3));
	}

	@Test void sessionResetRejectsLateWorkAndClearsPendingActions()
	{
		CameraActionQueue queue = new CameraActionQueue();
		var old = new CameraActionQueue.Action(queue.session(), "test:hit", "attack", 1, false);
		assertTrue(queue.offer(old)); queue.reset(); assertFalse(queue.offer(old));
		List<CameraActionQueue.Action> drained = new ArrayList<>();
		queue.drain(drained::add); assertTrue(drained.isEmpty());
		assertTrue(queue.offer(new CameraActionQueue.Action(queue.session(), "test:hit", "attack", 1, false)));
		queue.drain(drained::add); assertEquals(1, drained.size());
	}

	@Test void queueBoundAndInvalidScale()
	{
		CameraActionQueue queue = new CameraActionQueue();
		assertFalse(queue.offer(new CameraActionQueue.Action(queue.session(), "test:hit", "a", Float.NaN, false)));
		assertFalse(queue.offer(new CameraActionQueue.Action(queue.session(), "bad id", "a", 1, false)));
		for (int i = 0; i < 128; i++) assertTrue(queue.offer(new CameraActionQueue.Action(queue.session(), "test:hit", "a", 1, false)));
		assertFalse(queue.offer(new CameraActionQueue.Action(queue.session(), "test:hit", "a", 1, false)));
	}

	private ShakePreset constant(String id, double duration)
	{
		return new ShakePreset(id, duration, List.of(new ShakeTrack(ShakeChannel.DISTANCE, 0, 0, 0, 1,
				new ShakeEnvelope(0, 2, 0, Easing.LINEAR), ShakeWave.none(), new ShakeKeyframe[0])));
	}

	@Test void sameSlotReplacesOtherSlotsOverlapAndExpire()
	{
		ShakeMixer mixer = new ShakeMixer();
		var preset = constant("test:hit", 1);
		mixer.trigger(preset, "a", 1); mixer.trigger(preset, "a", 2);
		assertEquals(2, mixer.sample(0).distance());
		mixer.trigger(preset, "b", 1); assertEquals(3, mixer.sample(0).distance());
		mixer.stop("a"); assertEquals(1, mixer.sample(0).distance());
		mixer.tick(1); assertEquals(0, mixer.sample(0).distance());
	}

	@Test void partialFrameCannotOutlivePreset()
	{
		ShakeInstance instance = new ShakeInstance(constant("test:hit", 0.01), "a", 1);
		assertEquals(0, instance.sample(0.02).distance());
	}

	@Test void mixerCapsConcurrentSlotsButStillAllowsReplacement()
	{
		ShakeMixer mixer = new ShakeMixer(); var preset = constant("test:hit", 1);
		for (int i = 0; i < 40; i++) mixer.trigger(preset, "slot" + i, 1);
		assertEquals(32, mixer.sample(0).distance());
		mixer.trigger(preset, "slot0", 2); assertEquals(33, mixer.sample(0).distance());
		assertEquals(2, mixer.sample(0).limited().distance());
	}

	private String json(String id) { return "{\"id\":\"" + id + "\",\"duration\":1,\"tracks\":[]}"; }

	@Test void loaderCreatesDirectoryAndReportsBadFileInsteadOfPartialResult() throws Exception
	{
		Path nested = directory.resolve("shakes");
		assertTrue(ShakePresetJsonLoader.loadDirectory(nested).isEmpty());
		Files.writeString(nested.resolve("ok.json"), json("test:ok"));
		assertEquals(1, ShakePresetJsonLoader.loadDirectory(nested).size());
		Files.writeString(nested.resolve("bad.json"), "broken");
		var error = assertThrows(java.io.IOException.class, () -> ShakePresetJsonLoader.loadDirectory(nested));
		assertTrue(error.getMessage().contains("bad.json"));
	}

	@Test void duplicatePresetIdsAreRejected() throws Exception
	{
		Files.writeString(directory.resolve("one.json"), json("test:hit"));
		Files.writeString(directory.resolve("two.json"), json("test:hit"));
		assertTrue(assertThrows(java.io.IOException.class, () -> ShakePresetJsonLoader.loadDirectory(directory)).getMessage().contains("Duplicate"));
	}

	@Test void invalidNumericDefinitionsAreRejected()
	{
		assertThrows(IllegalArgumentException.class, () -> constant("test:hit", Double.NaN));
		assertThrows(IllegalArgumentException.class, () -> constant("test:hit", 61));
		assertThrows(IllegalArgumentException.class, () -> new ShakeTrack(ShakeChannel.ROTATION, Double.NaN, 0, 0, 1,
				new ShakeEnvelope(0, 1, 0, Easing.LINEAR), ShakeWave.none(), new ShakeKeyframe[0]));
	}
}
