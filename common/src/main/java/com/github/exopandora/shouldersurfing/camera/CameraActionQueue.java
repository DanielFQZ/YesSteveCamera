package com.github.exopandora.shouldersurfing.camera;

import java.util.ArrayDeque;
import java.util.UUID;
import java.util.function.Consumer;

/** Bounded handoff; session identity invalidates delayed producer work after resets. */
public final class CameraActionQueue
{
	public record Action(UUID session, String preset, String slot, float scale, boolean stop) {}
	private final ArrayDeque<Action> actions = new ArrayDeque<>();
	private UUID session = UUID.randomUUID();
	public synchronized UUID session() { return session; }
	public synchronized void reset() { actions.clear(); session = UUID.randomUUID(); }
	public synchronized boolean offer(Action action)
	{
		if (!session.equals(action.session()) || actions.size() >= 128) return false;
		if (action.slot() == null || action.slot().isBlank() || action.slot().length() > 128) return false;
		if (!Float.isFinite(action.scale()) || action.scale() < 0 || action.scale() > 10) return false;
		if (!action.stop() && (action.preset() == null || !action.preset().matches("[a-z0-9_.-]+:[a-z0-9_./-]+"))) return false;
		actions.add(action);
		return true;
	}
	public synchronized void drain(Consumer<Action> consumer)
	{
		Action action;
		while ((action = actions.poll()) != null) consumer.accept(action);
	}
}
