/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Camera
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.yss;

import java.lang.reflect.Method;
import net.minecraft.client.Camera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class YssCameraAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/CameraAccess");
    private static final Method MOVE_METHOD = YssCameraAccess.resolveMoveMethod();

    private YssCameraAccess() {
    }

    public static void move(Camera camera, double x, double y, double z) {
        if (MOVE_METHOD == null) {
            return;
        }
        try {
            MOVE_METHOD.invoke(camera, x, y, z);
        }
        catch (Exception e) {
            LOGGER.warn("Failed to move camera", (Throwable)e);
        }
    }

    private static Method resolveMoveMethod() {
        try {
            return YssCameraAccess.prepareMethod(Camera.class.getDeclaredMethod("move", Double.TYPE, Double.TYPE, Double.TYPE));
        }
        catch (Exception e) {
            for (Method method : Camera.class.getDeclaredMethods()) {
                Class<?>[] parameterTypes;
                if (method.getReturnType() != Void.TYPE || method.getParameterCount() != 3 || (parameterTypes = method.getParameterTypes())[0] != Double.TYPE || parameterTypes[1] != Double.TYPE || parameterTypes[2] != Double.TYPE) continue;
                LOGGER.info("Resolved Camera move method via signature fallback: {}", (Object)method.getName());
                return YssCameraAccess.prepareMethod(method);
            }
            LOGGER.warn("Cannot access Camera.move(double,double,double)", (Throwable)e);
            return null;
        }
    }

    private static Method prepareMethod(Method method) {
        method.setAccessible(true);
        return method;
    }
}

