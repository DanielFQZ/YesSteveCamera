package com.github.exopandora.shouldersurfing.camera.fov;

public final class MovementFovService
{
    private static MovementFovConfig config = MovementFovConfig.DEFAULT;
    private MovementFovService() {}
    public static MovementFovConfig config() { return config; }
    public static void configure(MovementFovConfig value) { config = value; }
    public static String status() { return config.mode() + " / sprint x" + config.sprintMultiplier(); }
}
