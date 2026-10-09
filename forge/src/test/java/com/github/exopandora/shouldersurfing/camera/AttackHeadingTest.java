package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.assist.AttackHeading;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttackHeadingTest
{
	@Test void returningPastTargetMustNotReverseHeading()
	{
		var heading = new AttackHeading();
		heading.capture(40);
		heading.capture(220);
		assertEquals(40, heading.yaw());
		heading.clear();
		heading.capture(220);
		assertEquals(220, heading.yaw());
	}

	@Test void hoverAndHitstopAreSuspensionsNotActionCancellation()
	{
		assertTrue(AttackHeading.temporaryBlock("hover"));
		assertTrue(AttackHeading.temporaryBlock("hitstop"));
		assertTrue(AttackHeading.temporaryBlock("caster_move"));
		assertFalse(AttackHeading.temporaryBlock("interrupted"));
		assertFalse(AttackHeading.temporaryBlock("knockup"));
	}

	@Test void missingOldYssHeadingAllowsOneFallbackCapture()
	{
		var heading = new AttackHeading();
		heading.capture(Float.NaN);
		assertFalse(heading.captured());
		heading.capture(-70);
		heading.capture(Float.POSITIVE_INFINITY);
		assertEquals(-70, heading.yaw());
	}
}
