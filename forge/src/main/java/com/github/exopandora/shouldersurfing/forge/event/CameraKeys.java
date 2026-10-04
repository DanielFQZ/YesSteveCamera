package com.github.exopandora.shouldersurfing.forge.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class CameraKeys
{
	private CameraKeys() {}
	public static final KeyMapping LOCK = new KeyMapping("key.yesstevecamera.lock", KeyConflictContext.IN_GAME,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories.yesstevecamera");
}
