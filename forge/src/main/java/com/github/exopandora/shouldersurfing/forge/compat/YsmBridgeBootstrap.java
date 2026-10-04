package com.github.exopandora.shouldersurfing.forge.compat;

import com.github.exopandora.shouldersurfing.ShoulderSurfingCommon;
import net.minecraftforge.fml.ModList;

public final class YsmBridgeBootstrap
{
	private static String status = "not installed";
	private YsmBridgeBootstrap() {}
	public static String status() { return status; }
	public static void tryInstall()
	{
		if (!ModList.get().isLoaded("ysm")) { status = "YSM absent"; return; }
		try
		{
			ClassLoader loader = YsmBridgeBootstrap.class.getClassLoader();
			String name = "com.github.exopandora.shouldersurfing.forge.compat.ysm.YsmCameraIntegration";
			Class<?> checker = Class.forName(name + "CompatibilityChecker", false, loader);
			Object result = checker.getMethod("checkInstall").invoke(null);
			if (!(Boolean) result.getClass().getMethod("isCompatible").invoke(result)
					|| !(Boolean) result.getClass().getMethod("coverageComplete").invoke(result))
				throw new IllegalStateException("YSM API compatibility check failed: " + result);
			Class.forName(name, true, loader).getMethod("install").invoke(null);
			status = "ctrl.camera_shake / ctrl.camera_shake_stop ready";
			ShoulderSurfingCommon.LOGGER.info("YesSteveCamera YSM: {}", status);
		}
		catch (ReflectiveOperationException | LinkageError | RuntimeException exception)
		{
			status = "unavailable (see latest.log)";
			ShoulderSurfingCommon.LOGGER.warn("YesSteveCamera YSM bridge unavailable", exception);
		}
	}
}
