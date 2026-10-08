package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.fov.MovementFovConfig;
import com.github.exopandora.shouldersurfing.camera.fov.MovementFovState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class MovementFovTest
{
    @TempDir Path directory;
    private static final MovementFovConfig DEFAULT = MovementFovConfig.DEFAULT;

    @Test void walkingAndSprintingIgnoreArbitraryModelAndPotionSpeedMultipliers()
    {
        for (double speed : new double[] {0, 0.001, 0.1, 0.13, 0.6, 10})
        {
            var state = new MovementFovState();
            assertEquals(1, vanillaFactor(state.fovSpeed(speed, 0.1F, true, false, DEFAULT, 0)), 1E-6);
            state.fovSpeed(speed, 0.1F, true, true, DEFAULT, 0);
            assertEquals(1.08, vanillaFactor(state.fovSpeed(speed, 0.1F, true, true, DEFAULT, 0.2)), 1E-6);
        }
    }

    @Test void stableModeRemovesSprintExpansionAndVanillaModePreservesExactAttributeRead()
    {
        var stable = new MovementFovConfig(MovementFovConfig.Mode.STABLE, 1.04, 0, 0, true);
        var vanilla = new MovementFovConfig(MovementFovConfig.Mode.VANILLA, 1.04, 0.2, 0.25, true);
        var state = new MovementFovState();
        assertEquals(1, vanillaFactor(state.fovSpeed(0.6, 0.1F, true, true, stable, 0)), 1E-6);
        state.reset();
        assertEquals(0.6, state.fovSpeed(0.6, 0.1F, true, true, vanilla, 0));
        assertEquals(0.02, state.fovSpeed(0.02, 0.1F, true, false, vanilla, 0.05));
    }

    @Test void transitionIsBoundedAndHasIndependentEnterAndExitDurations()
    {
        var state = new MovementFovState();
        state.update(4, true, false, DEFAULT, 0);
        assertEquals(1, state.update(4, true, true, DEFAULT, 0));
        assertEquals(1.04, state.update(4, true, true, DEFAULT, 0.1), 1E-9);
        assertEquals(1.08, state.update(4, true, true, DEFAULT, 0.2), 1E-9);
        state.update(4, true, false, DEFAULT, 0.2);
        assertEquals(1.04, state.update(4, true, false, DEFAULT, 0.325), 1E-9);
        assertEquals(1, state.update(4, true, false, DEFAULT, 0.45), 1E-9);
    }

    @Test void duplicateEvaluationAndPauseDoNotAdvanceTransition()
    {
        var state = new MovementFovState();
        state.update(1, true, false, DEFAULT, 0);
        state.update(1, true, true, DEFAULT, 0);
        double previous = state.update(1, true, true, DEFAULT, 0.05);
        for (int i = 0; i < 100; i++) assertEquals(previous, state.update(1, true, true, DEFAULT, 0.05));
    }

    @Test void interpolationIsIndependentOfEvaluationFrequency()
    {
        var slow = new MovementFovState();
        var fast = new MovementFovState();
        slow.update(1, true, false, DEFAULT, 0); fast.update(1, true, false, DEFAULT, 0);
        slow.update(1, true, true, DEFAULT, 0); fast.update(1, true, true, DEFAULT, 0);
        for (int i = 1; i <= 3; i++) slow.update(1, true, true, DEFAULT, i / 30.0);
        for (int i = 1; i <= 12; i++) fast.update(1, true, true, DEFAULT, i / 120.0);
        assertEquals(slow.update(1, true, true, DEFAULT, 0.1), fast.update(1, true, true, DEFAULT, 0.1), 1E-9);
    }

    @Test void scopeExitSmoothlyHandsBackToLiveVanillaWithoutPerpetuallyRestarting()
    {
        var state = new MovementFovState();
        state.update(4, true, false, DEFAULT, 0);
        assertEquals(1, state.update(4, false, false, DEFAULT, 0));
        assertEquals(2.5, state.update(4, false, false, DEFAULT, 0.125), 1E-9);
        assertEquals(3, state.update(3, false, false, DEFAULT, 0.25));
        assertEquals(2, state.update(2, false, false, DEFAULT, 0.30));
        assertEquals(2, state.update(2, true, false, DEFAULT, 0.30));
        assertEquals(1, state.update(50, true, false, DEFAULT, 0.55));
    }

    @Test void RapidSprintReversalRemainsContinuousAndResetDropsPreviousSession()
    {
        var state = new MovementFovState();
        state.update(1, true, false, DEFAULT, 0);
        state.update(1, true, true, DEFAULT, 0);
        double previous = state.update(1, true, true, DEFAULT, 0.05);
        assertEquals(previous, state.update(1, true, false, DEFAULT, 0.05));
        assertEquals(1, state.update(1, true, false, DEFAULT, 0.30));
        state.reset();
        assertEquals(4, state.update(4, false, false, DEFAULT, 0));
        assertEquals(1, state.update(4, true, false, DEFAULT, -1));
    }

    @Test void zeroWalkingSpeedRetainsVanillaFallbackAndOtherMultipliersRemainIndependent()
    {
        var state = new MovementFovState();
        assertEquals(0.4, state.fovSpeed(0.4, 0, true, true, DEFAULT, 0));
        double controlled = vanillaFactor(state.fovSpeed(0.4, 0.1F, true, true, DEFAULT, 1));
        // Vanilla applies flying before and bow after the movement factor; these are untouched.
        assertEquals(1.1 * 1.08 * 0.85, 1.1 * controlled * 0.85, 1E-6);
    }

    @Test void configCreatesDefaultsSupportsPartialFilesAndRejectsInvalidInput() throws Exception
    {
        var path = directory.resolve("fov.json");
        assertEquals(DEFAULT, MovementFovConfig.load(path));
        Files.writeString(path, "{\"mode\":\"STABLE\"}");
        assertEquals(MovementFovConfig.Mode.STABLE, MovementFovConfig.load(path).mode());
        assertEquals(DEFAULT.enterSeconds(), MovementFovConfig.load(path).enterSeconds());
        for (String json : new String[] {"null", "[]", "{\"mode\":\"BAD\"}", "{\"enterSeconds\":-1}",
                "{\"sprintMultiplier\":100}", "{\"exitSeconds\":\"NaN\"}"})
        {
            Files.writeString(path, json);
            assertThrows(java.io.IOException.class, () -> MovementFovConfig.load(path), json);
        }
    }

    private static double vanillaFactor(double speed) { return ((float) speed / 0.1F + 1.0F) / 2.0F; }
}
