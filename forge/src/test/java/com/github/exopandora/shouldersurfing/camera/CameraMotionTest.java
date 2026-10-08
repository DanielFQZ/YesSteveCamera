package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.motion.*;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class CameraMotionTest
{
	@TempDir Path directory;
	@Test void suddenMotionAcceleratesAndSettlesWithoutOvershoot()
	{
		var spring = new PositionSpring(); spring.snap(Vec3.ZERO);
		var target = new Vec3(10, 5, -2);
		Vec3 first = spring.step(target, 1.0 / 60, 0.22);
		assertTrue(first.x > 0 && first.x < 0.2);
		double previous = first.x;
		for (int i = 0; i < 120; i++)
		{
			Vec3 current = spring.step(target, 1.0 / 60, 0.22);
			assertTrue(current.x >= previous && current.x <= 10);
			previous = current.x;
		}
		assertEquals(10, previous, 0.001);
	}
	@Test void springIsFrameRateIndependentForStationaryTarget()
	{
		var slow = new PositionSpring(); var fast = new PositionSpring();
		slow.snap(Vec3.ZERO); fast.snap(Vec3.ZERO);
		Vec3 target = new Vec3(7, -3, 4), a = null, b = null;
		for (int i = 0; i < 30; i++) a = slow.step(target, 1.0 / 30, 0.35);
		for (int i = 0; i < 144; i++) b = fast.step(target, 1.0 / 144, 0.35);
		assertEquals(0, a.distanceTo(b), 1.0E-10);
	}
	@Test void pausedSpringDoesNotAdvanceAndResetDoesNotFlyFromOldWorld()
	{
		var spring = new PositionSpring(); spring.snap(Vec3.ZERO);
		assertEquals(Vec3.ZERO, spring.step(new Vec3(10, 0, 0), 0, 0.2));
		spring.reset(); Vec3 spawn = new Vec3(10000, 50, -5000);
		assertEquals(spawn, spring.step(spawn, 0.01, 0.2));
	}
	@Test void overviewAutomaticallyReleasesAndPauseDoesNotExpireIt()
	{
		var window = new OverviewWindow(); window.begin(5, 0.4, 0.6);
		assertEquals(0, window.weight()); window.advance(0.4); assertEquals(1, window.weight());
		window.advance(0); assertFalse(window.releasing());
		window.advance(4.6); assertTrue(window.releasing()); assertEquals(1, window.weight());
		window.advance(0.6); assertTrue(window.finished()); assertEquals(0, window.weight());
	}
	@Test void earlyStopHasContinuousWeightAndRepeatedStopDoesNotRestartRelease()
	{
		var window = new OverviewWindow(); window.begin(5, 0.4, 0.6); window.advance(0.2);
		double previous = window.weight(); window.stop(); assertEquals(previous, window.weight());
		window.advance(0.3); window.stop(); window.advance(0.3); assertTrue(window.finished());
		window.begin(2, 0.4, 0.6); assertFalse(window.releasing()); assertEquals(0, window.weight());
	}
	@Test void configAndPresetLoadAndRejectInvalidValues() throws Exception
	{
		assertEquals(MotionConfig.DEFAULT, MotionConfig.load(directory.resolve("motion.json")));
		assertTrue(MotionConfig.DEFAULT.groundStartSeconds() < MotionConfig.DEFAULT.startSeconds());
		assertTrue(MotionConfig.DEFAULT.groundMaxLag() < MotionConfig.DEFAULT.maxLag());
		Files.writeString(directory.resolve("legacy-motion.json"),
				"{\"enabled\":true,\"startSeconds\":0.22,\"stopSeconds\":0.35,\"maxLag\":8,\"teleportDistance\":64,\"wheelZoom\":true,\"wheelStep\":0.5}");
		MotionConfig legacy = MotionConfig.load(directory.resolve("legacy-motion.json"));
		assertEquals(MotionConfig.DEFAULT.groundStartSeconds(), legacy.groundStartSeconds());
		assertEquals(MotionConfig.DEFAULT.groundMaxLag(), legacy.groundMaxLag());
		assertEquals(OverviewPreset.DEFAULT, OverviewPreset.load(directory.resolve("overviews")).get(OverviewPreset.DEFAULT.id()));
		assertThrows(IllegalArgumentException.class, () -> new MotionConfig(true, 0, 0.3, 8, 64, true, 0.5));
		assertThrows(IllegalArgumentException.class, () -> new OverviewPreset("bad", 8, 24, 3, 6, 0.4, 0.6));
		Files.writeString(directory.resolve("overviews/broken.json"), "{}");
		assertThrows(java.io.IOException.class, () -> OverviewPreset.load(directory.resolve("overviews")));
	}
}
