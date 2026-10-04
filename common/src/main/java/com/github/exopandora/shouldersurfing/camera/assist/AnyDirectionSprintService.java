package com.github.exopandora.shouldersurfing.camera.assist;

import com.github.exopandora.shouldersurfing.api.model.Perspective;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;

/** Allows the third-person Camera view to start sprinting from any directional input. */
public final class AnyDirectionSprintService
{
	private static boolean requested;
	private AnyDirectionSprintService() {}
	public static void reset() { requested = false; }
	public static void request(Input input)
	{
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		requested = player != null && mc.screen == null
				&& Perspective.SHOULDER_SURFING == Perspective.current()
				&& ShoulderSurfingImpl.getInstance().isShoulderSurfing()
				&& mc.options.keySprint.isDown() && input != null
				&& (Math.abs(input.forwardImpulse) > 1.0E-4F || Math.abs(input.leftImpulse) > 1.0E-4F);
	}
	/** Called at LocalPlayer.aiStep tail so vanilla's forward-only sprint check cannot undo it. */
	public static void applyAfterAiStep(LocalPlayer player)
	{
		if (requested && player != null && player.isAlive() && player.onGround()
				&& !player.isPassenger() && !player.isFallFlying() && !player.isSwimming()
				&& !player.getAbilities().flying && Minecraft.getInstance().options.keySprint.isDown())
			player.setSprinting(true);
		requested = false;
	}
}
