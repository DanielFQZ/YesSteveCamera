package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.assist.AssistConfig;
import com.github.exopandora.shouldersurfing.camera.assist.AssistWindow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class AssistWindowTest
{
	@Test void stoppedActionCannotReacquireUntilNewAction()
	{
		var window = new AssistWindow();
		assertTrue(window.observe(1)); window.stop();
		assertFalse(window.observe(1)); assertFalse(window.active());
		assertEquals(0, window.step(3, AssistConfig.DEFAULT));
		assertTrue(window.observe(2)); assertTrue(window.active());
	}
	@Test void arrivalHysteresisPreventsOscillation()
	{
		var window = new AssistWindow(); window.observe(1);
		assertEquals(0, window.step(1.2, AssistConfig.DEFAULT)); assertTrue(window.arrived());
		assertEquals(0, window.step(1.4, AssistConfig.DEFAULT)); assertTrue(window.arrived());
		assertTrue(window.step(1.7, AssistConfig.DEFAULT) > 0); assertFalse(window.arrived());
	}
	@Test void accelerationAndStopDistanceAreBounded()
	{
		var window = new AssistWindow(); window.observe(1);
		assertEquals(0.03, window.step(4, AssistConfig.DEFAULT), 1e-8);
		assertEquals(0.06, window.step(4, AssistConfig.DEFAULT), 1e-8);
		assertEquals(0.01, window.step(1.26, AssistConfig.DEFAULT), 1e-8);
	}
	@Test void hardTimeoutEvenWhenStandingInRange()
	{
		var window = new AssistWindow(); window.observe(1);
		for (int i = 0; i < 12; i++) { window.step(0, AssistConfig.DEFAULT); assertTrue(window.active()); }
		window.step(0, AssistConfig.DEFAULT); assertFalse(window.active());
		assertFalse(window.observe(1));
	}
	@Test void sceneResetAllowsSameNumericIdFromNewSession()
	{
		var window = new AssistWindow(); window.observe(1); window.stop(); window.reset();
		assertTrue(window.observe(1));
	}
	@Test void invalidDistanceStopsControl()
	{
		var window = new AssistWindow(); window.observe(1);
		assertEquals(0, window.step(Double.NaN, AssistConfig.DEFAULT)); assertFalse(window.active());
	}
	@Test void configRoundTripAndInvalidConfigAreRejected(@TempDir Path directory) throws Exception
	{
		Path path = directory.resolve("assist.json");
		assertEquals(AssistConfig.DEFAULT, AssistConfig.load(path));
		Files.writeString(path, Files.readString(path).replace("0.12", "99"));
		assertThrows(java.io.IOException.class, () -> AssistConfig.load(path));
	}
}
