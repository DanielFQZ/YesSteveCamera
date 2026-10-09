package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.assist.ActionAssistPolicy;
import com.github.exopandora.shouldersurfing.camera.assist.AssistWindow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionAssistPolicyTest
{
	@Test void startingOffAttackKeepsVfxTargetWithoutAcquiringOrSteering()
	{
		var window = new AssistWindow();
		Object target = new Object();
		assertTrue(window.observe(1));
		assertSame(target, ActionAssistPolicy.targetForAction("off", target, value -> true,
				() -> fail("Off must not acquire another target")));
		assertTrue(ActionAssistPolicy.suppressesSteering(window.active(), "off"));
		window.stop();
		assertFalse(ActionAssistPolicy.suppressesSteering(window.active(), "off"));
	}

	@Test void offWithoutExistingTargetMustNotAcquireOne()
	{
		assertNull(ActionAssistPolicy.targetForAction("off", null,
				value -> fail("Null target must not be validated"),
				() -> fail("Off must not acquire a target")));
	}

	@Test void offDiscardsInvalidTargetWithoutReplacingIt()
	{
		assertNull(ActionAssistPolicy.targetForAction("off", new Object(), value -> false,
				() -> fail("Invalid targets must not be replaced during off")));
	}

	@Test void subsequentInheritAndStartOnlyActionsResumeSelection()
	{
		var window = new AssistWindow();
		window.observe(1);
		assertTrue(ActionAssistPolicy.suppressesSteering(window.active(), "off"));
		long actionId = 2;
		for (String mode : new String[] {"inherit", "start_only"})
		{
			assertTrue(window.observe(actionId++));
			Object selected = new Object();
			assertSame(selected, ActionAssistPolicy.targetForAction(mode, null, value -> false, () -> selected));
			assertFalse(ActionAssistPolicy.suppressesSteering(window.active(), mode));
		}
	}
}
