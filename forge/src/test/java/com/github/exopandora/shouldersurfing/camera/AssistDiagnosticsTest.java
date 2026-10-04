package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.assist.AssistDiagnostics;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AssistDiagnosticsTest
{
	@Test void openingChatDoesNotReplaceTheAttackFailure()
	{
		var diagnostics = new AssistDiagnostics();
		diagnostics.received(); diagnostics.begin("Zombie");
		assertTrue(diagnostics.finish("obstacle"));
		for (int i = 0; i < 20; i++) assertFalse(diagnostics.finish("camera unavailable"));
		assertTrue(diagnostics.summary().contains("last=obstacle"));
		assertTrue(diagnostics.summary().contains("events=1, actions=1"));
	}

	@Test void noEventIsDistinguishedFromAnAttackThatDidNotMove()
	{
		var diagnostics = new AssistDiagnostics();
		assertFalse(diagnostics.finish("UI opened"));
		assertTrue(diagnostics.summary().contains("no YSS attack event received"));
		diagnostics.received(); diagnostics.begin("none"); diagnostics.finish("target unavailable");
		assertTrue(diagnostics.summary().contains("last=target unavailable"));
	}

	@Test void completedMovementIsRetainedUntilNextAction()
	{
		var diagnostics = new AssistDiagnostics();
		diagnostics.received(); diagnostics.begin("Zombie");
		diagnostics.progress("approaching", 0.03, 25);
		diagnostics.received(); diagnostics.progress("approaching", 0.06, -10);
		diagnostics.finish("animation ended"); diagnostics.finish("UI opened");
		assertTrue(diagnostics.summary().contains("moved=0.090, turned=35.0"));
		diagnostics.received(); diagnostics.begin("Cow");
		assertTrue(diagnostics.summary().contains("events=3, actions=2"));
		assertTrue(diagnostics.summary().contains("target=Cow, moved=0.000, turned=0.0"));
	}
}
