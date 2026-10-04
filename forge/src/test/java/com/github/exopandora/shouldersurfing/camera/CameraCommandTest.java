package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.forge.event.CameraClientCommands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CameraCommandTest
{
	@Test void duelTogglesParseInActualTree()
	{
		var dispatcher = new CommandDispatcher<CommandSourceStack>();
		CameraClientCommands.register(dispatcher);
		for (String toggle : new String[]{"on", "off"})
		{
			var parsed = dispatcher.parse("yesstevecamera duel " + toggle, null);
			assertFalse(parsed.getReader().canRead());
			assertNotNull(parsed.getContext().build(parsed.getReader().getString()).getCommand());
		}
	}

	@Test void namespacedPresetParsesInActualCommandTree()
	{
		var dispatcher = new CommandDispatcher<CommandSourceStack>();
		CameraClientCommands.register(dispatcher);
		for (String preset : new String[]{"yesstevecamera:light_hit", "yesstevecamera:heavy_hit", "custom:combat/heavy_hit"})
		{
			var parsed = dispatcher.parse("yesstevecamera shake play " + preset, null);
			assertFalse(parsed.getReader().canRead(), parsed.getExceptions().toString());
			var context = parsed.getContext().build(parsed.getReader().getString());
			assertNotNull(context.getCommand());
			assertEquals(preset, ResourceLocationArgument.getId(context, "preset").toString());
		}
	}

	@Test void optionalScaleStillParsesAfterNamespacedPreset()
	{
		var dispatcher = new CommandDispatcher<CommandSourceStack>();
		CameraClientCommands.register(dispatcher);
		var parsed = dispatcher.parse("yesstevecamera shake play yesstevecamera:light_hit 0.5", null);
		assertFalse(parsed.getReader().canRead());
		var context = parsed.getContext().build(parsed.getReader().getString());
		assertNotNull(context.getCommand());
		assertEquals(0.5F, FloatArgumentType.getFloat(context, "scale"));
	}
}
