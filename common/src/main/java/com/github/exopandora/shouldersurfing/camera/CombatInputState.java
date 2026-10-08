package com.github.exopandora.shouldersurfing.camera;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/** The HUD consumes YSS's live input policy instead of independently classifying weapons. */
public final class CombatInputState
{
	private static BooleanSupplier state = () -> false;
	private static boolean available;
	private CombatInputState() {}
	public static void install(BooleanSupplier value) { state = Objects.requireNonNull(value); available = true; }
	public static boolean active() { return state.getAsBoolean(); }
	public static String status() { return available ? (active() ? "attack only" : "normal") : "YSS API unavailable"; }
}
