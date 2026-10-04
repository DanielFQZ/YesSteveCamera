package com.github.exopandora.shouldersurfing.forge.event;

import com.github.exopandora.shouldersurfing.camera.target.TargetService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Vector4f;

/** Screen-space marker avoids shader-specific entity render types. */
public final class TargetOverlay
{
	private static float x, y;
	private static boolean visible;
	private TargetOverlay() {}
	private static net.minecraft.world.entity.LivingEntity target()
	{
		return TargetService.target() != null ? TargetService.target() : com.github.exopandora.shouldersurfing.camera.assist.CombatAssistService.target();
	}

	public static void project(RenderLevelStageEvent event)
	{
		visible = false;
		var target = target();
		if (target == null || !TargetService.enabled()) return;
		Vec3 anchor = target.getPosition(event.getPartialTick()).add(0, target.getBbHeight() * 0.6, 0);
		Vec3 delta = anchor.subtract(event.getCamera().getPosition());
		Vector4f clip = new Vector4f((float) delta.x, (float) delta.y, (float) delta.z, 1);
		clip.mul(event.getPoseStack().last().pose()).mul(event.getProjectionMatrix());
		if (clip.w <= 0 || !Float.isFinite(clip.w)) return;
		x = clip.x / clip.w;
		y = clip.y / clip.w;
		visible = Float.isFinite(x) && Float.isFinite(y) && Math.abs(x) <= 1 && Math.abs(y) <= 1
				&& Math.abs(clip.z / clip.w) <= 1;
	}

	public static void render(GuiGraphics graphics, int width, int height)
	{
		Minecraft mc = Minecraft.getInstance();
		if (!TargetService.enabled() || target() == null || mc.options.hideGui) return;
		String label = (TargetService.target() != null ? "锁定：" : "攻击辅助：") + target().getDisplayName().getString() + (visible ? "" : "（屏幕外）");
		graphics.drawCenteredString(mc.font, label, width / 2, height / 2 + 38, 0xFFFFD56A);
		if (!visible) return;
		int px = Math.round((x + 1) * width / 2), py = Math.round((1 - y) * height / 2);
		int color = 0xFFFFD56A;
		for (int sx : new int[]{-1, 1}) for (int sy : new int[]{-1, 1})
		{
			int left = px + sx * 10, top = py + sy * 10;
			graphics.fill(Math.min(left, left - sx * 5), top, Math.max(left, left - sx * 5) + 1, top + 1, color);
			graphics.fill(left, Math.min(top, top - sy * 5), left + 1, Math.max(top, top - sy * 5) + 1, color);
		}
	}
}
