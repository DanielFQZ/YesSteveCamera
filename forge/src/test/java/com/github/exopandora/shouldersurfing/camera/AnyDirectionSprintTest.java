package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.assist.SprintSteering;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnyDirectionSprintTest
{
	@Test void everyDirectionIsEligibleForTheCameraSprintRule()
	{
		for (float forward : new float[]{-1, 0, 1}) for (float left : new float[]{-1, 0, 1})
			if (forward != 0 || left != 0)
			{
				var state = new SprintSteering();
				state.update(true, forward, left, 0);
				if (forward > 0) assertFalse(state.released());
				else assertTrue(state.released());
			}
	}
	@Test void cameraRuleDoesNotApplyToNoInputOrFirstPersonByConstruction()
	{
		var state = new SprintSteering();
		state.update(true, 0, 0, 0);
		assertFalse(state.released());
		state.update(false, 1, 0, 0);
		assertFalse(state.released());
	}
}
