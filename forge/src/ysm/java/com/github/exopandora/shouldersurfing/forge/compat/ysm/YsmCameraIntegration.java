package com.github.exopandora.shouldersurfing.forge.compat.ysm;

import com.elfmcys.ysm.api.annotation.Side;
import com.elfmcys.ysm.api.annotation.YsmExtension;
import com.elfmcys.ysm.client.animation.molang.CtrlBinding;
import com.elfmcys.ysm.client.entity.IPreviewEntity;
import com.elfmcys.ysm.geckolib3.core.molang.context.IContext;
import com.elfmcys.ysm.molang.runtime.ExecutionContext;
import com.elfmcys.ysm.molang.runtime.Function;
import com.github.exopandora.shouldersurfing.camera.CameraRuntime;
import net.minecraft.world.entity.Entity;

/** Optional YSM bridge. Animation evaluations enqueue immutable values, never camera writes. */
public final class YsmCameraIntegration
{
	private static boolean installed;
	private YsmCameraIntegration() {}

	@YsmExtension(side = Side.CLIENT)
	public static synchronized void install() throws org.apache.commons.lang3.concurrent.ConcurrentException
	{
		if (installed) return;
		var binding = CtrlBinding.INSTANCE.get();
		for (String name : new String[]{"camera_shake", "camera_shake_stop", "camera_hit_begin", "camera_hit_end"})
			if (binding.getProperty(name) != null) throw new IllegalStateException("Already registered: ctrl." + name);
		binding.function("camera_shake", function(false));
		binding.function("camera_shake_stop", function(true));
		binding.function("camera_hit_begin", hitFunction(false));
		binding.function("camera_hit_end", hitFunction(true));
		installed = true;
	}

	private static Function function(boolean stop)
	{
		return new Function()
		{
			@Override public boolean validateArgumentSize(int size) { return size == (stop ? 1 : 3); }
			@Override public Object evaluate(ExecutionContext<?> execution, Function.ArgumentCollection arguments)
			{
				if (!validateArgumentSize(arguments.size()) || !(execution.entity() instanceof IContext<?> context)
						|| !context.allowEmitting() || !(context.entity() instanceof Entity entity)
						|| entity.isRemoved() || !entity.level().isClientSide) return 0F;
				var animatable = context.animatableEntity();
				if (animatable == null || animatable.isFakePlayer() || animatable instanceof IPreviewEntity) return 0F;
				String preset = stop ? null : arguments.getAsString(execution, 0);
				String slot = arguments.getAsString(execution, stop ? 0 : 1);
				float scale = stop ? 0 : (float) arguments.getAsDouble(execution, 2);
				return CameraRuntime.enqueue(entity.level(), entity.getUUID(), preset, slot, scale, stop) ? 1F : 0F;
			}
		};
	}

	private static Function hitFunction(boolean stop)
	{
		return new Function()
		{
			@Override public boolean validateArgumentSize(int size) { return size == (stop ? 1 : 3); }
			@Override public Object evaluate(ExecutionContext<?> execution, Function.ArgumentCollection arguments)
			{
				if (!validateArgumentSize(arguments.size()) || !(execution.entity() instanceof IContext<?> context)
						|| !context.allowEmitting() || !(context.entity() instanceof Entity entity)
						|| entity.isRemoved() || !entity.level().isClientSide) return 0F;
				var animatable = context.animatableEntity();
				if (animatable == null || animatable.isFakePlayer() || animatable instanceof IPreviewEntity) return 0F;
				String slot = arguments.getAsString(execution, stop ? 0 : 1);
				String preset = stop ? null : arguments.getAsString(execution, 0);
				float scale = stop ? 0 : (float) arguments.getAsDouble(execution, 2);
				return CameraRuntime.enqueueHitWindow(entity.level(), entity.getUUID(), preset, slot, scale, stop) ? 1F : 0F;
			}
		};
	}
}
