package com.github.exopandora.shouldersurfing.camera.target;

/** Tick-based press/release state, independent of render frame rate. */
public final class LockButton
{
	public enum Action { NONE, SELECT, CANCEL }
	private int heldTicks;
	private boolean held;
	private boolean consumed;

	public Action tick(boolean down, int threshold)
	{
		if(down)
		{
			held = true;
			if(!consumed && ++heldTicks >= threshold)
			{
				consumed = true;
				return Action.CANCEL;
			}
			return Action.NONE;
		}
		boolean select = held && !consumed;
		held = false;
		consumed = false;
		heldTicks = 0;
		return select ? Action.SELECT : Action.NONE;
	}

	/** A held button across a GUI/world transition must not create a fresh click. */
	public void suppressUntilRelease()
	{
		held = false;
		consumed = true;
		heldTicks = 0;
	}
}
