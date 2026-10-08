package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.client.CrosshairRenderer;
import com.github.exopandora.shouldersurfing.camera.shake.CameraShakeService;
import com.github.exopandora.shouldersurfing.camera.target.TargetService;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.mixinducks.CameraDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler
{
	public static void blockHighlight(net.minecraftforge.client.event.RenderHighlightEvent.Block event)
	{
		if (com.github.exopandora.shouldersurfing.camera.CombatInputState.active()) event.setCanceled(true);
	}

	@SubscribeEvent
	public static void mouseScroll(net.minecraftforge.client.event.InputEvent.MouseScrollingEvent event)
	{
		if (com.github.exopandora.shouldersurfing.camera.motion.CameraMotionService.scroll(event.getScrollDelta()))
			event.setCanceled(true);
	}

	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if (event.phase != Phase.START) return;
		Minecraft mc = Minecraft.getInstance();
		com.github.exopandora.shouldersurfing.camera.CameraRuntime.tick();
		if (mc.level != null && !mc.isPaused()) ShoulderSurfingImpl.getInstance().tick();
		TargetService.tick(CameraKeys.LOCK.isDown());
		if (!TargetService.enabled()) com.github.exopandora.shouldersurfing.camera.assist.CombatAssistService.cancel(mc.screen != null ? "UI opened" : "camera unavailable");
		while (CameraKeys.LOCK.consumeClick()) { /* Poll held state, not accumulated clicks. */ }

	}
	
	public static void assistEndTickEvent(ClientTickEvent event)
	{
		if (event.phase == Phase.END) com.github.exopandora.shouldersurfing.camera.assist.CombatAssistService.endTick();
	}

	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGuiOverlayEvent.Pre event)
	{
		if(VanillaGuiOverlay.CROSSHAIR.id().equals(event.getOverlay().id()) && !ShoulderSurfingImpl.getInstance().getCrosshairRenderer().doRenderCrosshair())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void registerGuiOverlaysEvent(RegisterGuiOverlaysEvent event)
	{
		event.registerAboveAll("target_lock", (gui, graphics, partial, width, height) -> TargetOverlay.render(graphics, width, height));
		event.registerBelow(VanillaGuiOverlay.CROSSHAIR.id(), "pre_crosshair", (gui, guiGraphics, partialTick, screenWith, screenHeight) ->
		{
			CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
			
			if(crosshairRenderer.doRenderCrosshair())
			{
				crosshairRenderer.preRenderCrosshair(guiGraphics);
			}
		});
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "post_crosshair", (gui, guiGraphics, partialTick, screenWith, screenHeight) ->
		{
			CrosshairRenderer crosshairRenderer = ShoulderSurfingImpl.getInstance().getCrosshairRenderer();
			
			if(crosshairRenderer.doRenderCrosshair())
			{
				crosshairRenderer.postRenderCrosshair(guiGraphics);
			}
		});
	}
	
	@SubscribeEvent
	public static void renderLevelStageEvent(RenderLevelStageEvent event)
	{
		if(RenderLevelStageEvent.Stage.AFTER_SKY.equals(event.getStage()))
		{
			TargetOverlay.project(event);
			ShoulderSurfingImpl.getInstance().getCrosshairRenderer().updateDynamicRaytrace(event.getCamera(), event.getPoseStack().last().pose(), event.getProjectionMatrix(), event.getPartialTick());
		}
	}
	
	@SubscribeEvent
	public static void movementInputUpdateEvent(MovementInputUpdateEvent event)
	{
		ShoulderSurfingImpl.getInstance().getInputHandler().updateMovementInput(event.getInput());
		ShoulderSurfingImpl.getInstance().updatePlayerRotations();
		TargetService.applyPlayerFacing();
	}
	
	@SubscribeEvent
	public static void computeCameraAnglesEvent(ViewportEvent.ComputeCameraAngles event)
	{
		var sample = CameraShakeService.sample((float) event.getPartialTick());
		CameraShakeService.apply(event.getCamera(), (float) event.getPartialTick());
		((CameraDuck) event.getCamera()).shouldersurfing$constrainPosition((float) event.getPartialTick());
		// Forge writes the event angles back after dispatch; camera-only rotation is overwritten.
		event.setPitch(event.getPitch() + sample.rotateX());
		event.setYaw(event.getYaw() + sample.rotateY());
		event.setRoll(event.getRoll() + ((CameraDuck) event.getCamera()).shouldersurfing$getZRot());
	}

	public static void computeFovEvent(ViewportEvent.ComputeFov event)
	{
		event.setFOV(CameraShakeService.applyFov((float) event.getFOV(), (float) event.getPartialTick()));
	}
}
