package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.assist.LockedMovement;
import com.github.exopandora.shouldersurfing.camera.assist.SprintSteering;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LockMovementTest
{
	@Test void forwardMovesTowardTargetEvenWhileBodyIsStillTurning()
	{
		for (float body : new float[]{-179, -90, 0, 65, 179})
		{
			var input = LockedMovement.relative(0, 1, 110, body);
			double radians = Math.toRadians(body);
			double worldX = input.x() * Math.cos(radians) - input.y() * Math.sin(radians);
			double worldZ = input.x() * Math.sin(radians) + input.y() * Math.cos(radians);
			assertEquals(-Math.sin(Math.toRadians(110)), worldX, 0.001);
			assertEquals(Math.cos(Math.toRadians(110)), worldZ, 0.001);
		}
	}
	@Test void strafingAndBackwardsPreserveDirectionAndInputMagnitude()
	{
		var left = LockedMovement.relative(1, 0, 0, 90);
		assertEquals(0, left.x(), 0.001); assertEquals(-1, left.y(), 0.001);
		var back = LockedMovement.relative(0, -0.3F, 90, 0);
		assertEquals(0.3, back.x(), 0.001); assertEquals(0, back.y(), 0.001);
		assertEquals(0.18, LockedMovement.relative(0.3F, 0.3F, 73, -112).lengthSquared(), 0.001);
	}
	@Test void forwardSprintKeepsLockDespiteAutomaticCompositionChanges()
	{
		var state = new SprintSteering();
		state.update(true, 1, 0, -35);
		for (int i = 0; i < 100; i++) state.update(true, 1, 0, -120);
		assertFalse(state.released(), "automatic camera changes are not mouse intent");
		assertFalse(state.mouse(20)); assertFalse(state.mouse(-20));
	}
	@Test void deliberateMouseTurnReleasesUntilSprintEnds()
	{
		var state = new SprintSteering(); state.update(true, 1, 0, -35);
		assertFalse(state.mouse(30)); assertTrue(state.mouse(25)); assertTrue(state.released());
		state.update(true, 1, 0, 0); assertTrue(state.released());
		state.update(false, 1, 0, 0); assertFalse(state.released());
		state.update(true, 1, 0, -35); assertFalse(state.released());
		assertTrue(state.mouse(-55));
	}
	@Test void backwardsSidewaysAndAlreadyLookingAwaySprintRelease()
	{
		var state = new SprintSteering(); state.update(true, 1, 1, -35); assertFalse(state.released());
		state.update(true, 0, 1, -35); assertTrue(state.released());
		state.reset(); state.update(true, -1, 0, 0); assertTrue(state.released());
		state.reset(); state.update(true, 1, 0, 100); assertTrue(state.released());
	}
	@Test void walkingMouseAndInvalidInputCannotCarryEscapeIntoNextSprint()
	{
		var state = new SprintSteering(); assertFalse(state.mouse(200));
		state.update(true, 1, 0, 0); assertFalse(state.mouse(Double.NaN));
		assertFalse(state.released()); assertEquals(0, state.mouseTurn());
		state.mouse(60); state.reset(); state.update(true, 1, 0, 0); assertFalse(state.released());
	}
}
