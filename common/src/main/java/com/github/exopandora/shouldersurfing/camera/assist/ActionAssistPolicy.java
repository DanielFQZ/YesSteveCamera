package com.github.exopandora.shouldersurfing.camera.assist;

import java.util.function.Predicate;
import java.util.function.Supplier;

/** Disabling action steering does not discard the target consumed by target VFX. */
public final class ActionAssistPolicy
{
	private ActionAssistPolicy() {}

	public static boolean suppressesSteering(boolean active, String mode)
	{
		return active && "off".equals(mode);
	}

	public static <T> T targetForAction(String mode, T retained, Predicate<T> valid, Supplier<T> acquire)
	{
		if (!"off".equals(mode)) return acquire.get();
		return retained != null && valid.test(retained) ? retained : null;
	}
}
