package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.duel.DuelConfig;
import com.github.exopandora.shouldersurfing.camera.duel.DuelFraming;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class DuelFramingTest
{
	private final AABB player = new AABB(-0.3, 0, -0.3, 0.3, 1.8, 0.3);
	private final Vec3 eye = new Vec3(0, 1.62, 0);

	private void assertFits(AABB target, double aspect)
	{
		double yaw = -35, pitch = 12;
		var frame = DuelFraming.fit(eye, player, target, yaw, pitch, 60, aspect, DuelConfig.DEFAULT);
		assertFalse(frame.limited());
		Vec3 camera = eye.add(frame.offset()), forward = DuelFraming.forward(yaw, pitch);
		Vec3 right = new Vec3(-Math.cos(Math.toRadians(yaw)), 0, -Math.sin(Math.toRadians(yaw)));
		Vec3 up = right.cross(forward);
		double tanY = Math.tan(Math.toRadians(30));
		for (AABB box : new AABB[]{player, target}) for (int i = 0; i < 8; i++)
		{
			Vec3 delta = new Vec3((i & 1) == 0 ? box.minX : box.maxX, (i & 2) == 0 ? box.minY : box.maxY,
					(i & 4) == 0 ? box.minZ : box.maxZ).subtract(camera);
			assertTrue(delta.dot(forward) > 0);
			assertTrue(Math.abs(delta.dot(right)) / delta.dot(forward) < tanY * aspect);
			assertTrue(Math.abs(delta.dot(up)) / delta.dot(forward) < tanY);
		}
		assertTrue(player.getCenter().subtract(camera).dot(right) < 0, "player on left");
		assertTrue(target.getCenter().subtract(camera).dot(right) > 0, "target on right");
	}
	@Test void fitsTwoHumanoidsWithPlayerLeftAndTargetRight() { assertFits(player.move(0, 0, 4), 16.0 / 9); }
	@Test void fitsTallEnemyAndNarrowWindow() { assertFits(new AABB(-0.6, 0, 3.4, 0.6, 3.6, 4.6), 0.75); }
	@Test void remoteTargetsDoNotPullCameraOutOfItsDistanceBudget()
	{
		var frame = DuelFraming.fit(eye, player, player.move(0, 0, 200), -35, 12, 60, 16.0 / 9, DuelConfig.DEFAULT);
		assertTrue(frame.limited()); assertEquals(12, frame.offset().length(), 1e-8);
		Vec3 delta = player.getCenter().subtract(eye.add(frame.offset()));
		Vec3 forward = DuelFraming.forward(-35, 12);
		Vec3 right = new Vec3(-Math.cos(Math.toRadians(-35)), 0, -Math.sin(Math.toRadians(-35)));
		assertTrue(delta.dot(forward) > 0);
		assertTrue(delta.dot(right) < 0);
		assertTrue(Math.abs(delta.dot(right) / delta.dot(forward)) < Math.tan(Math.toRadians(30)), "player remains on screen at range cap");
	}
	@Test void overlappingSubjectsStillProduceFiniteCamera()
	{
		var frame = DuelFraming.fit(eye, player, player, 0, 12, 60, 1, DuelConfig.DEFAULT);
		assertTrue(Double.isFinite(frame.offset().length())); assertTrue(frame.offset().length() > 1);
	}
	@Test void smoothingIsIndependentOfFrameCount()
	{
		double a = 0, b = 0;
		for (int i = 0; i < 30; i++) a += (1 - a) * DuelFraming.blend(1.0 / 30, 0.3);
		for (int i = 0; i < 144; i++) b += (1 - b) * DuelFraming.blend(1.0 / 144, 0.3);
		assertEquals(a, b, 1e-12); assertEquals(0, DuelFraming.blend(0, 0.3));
	}
	@Test void angleSmoothingCrossesWrapBoundaryAlongShortArc()
	{
		assertEquals(180, DuelFraming.turn(179, -179, 0.5), 1e-9);
		assertEquals(-180, DuelFraming.turn(-179, 179, 0.5), 1e-9);
	}
	@Test void invalidReloadIsRejectedAndDefaultsRoundTrip(@TempDir Path directory) throws Exception
	{
		Path path = directory.resolve("duel.json");
		assertEquals(DuelConfig.DEFAULT, DuelConfig.load(path));
		Files.writeString(path, "{\"enabled\":true,\"maxDistance\":1000000}");
		assertThrows(java.io.IOException.class, () -> DuelConfig.load(path));
	}
}
