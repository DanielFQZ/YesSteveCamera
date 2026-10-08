package com.github.exopandora.shouldersurfing.camera.fov;

/** Per-viewer movement multiplier. Other FOV effects never enter this transition. */
public final class MovementFovState
{
    private double value = Double.NaN;
    private double previousTime = Double.NaN;
    private double previousTarget;
    private boolean wasControlled;
    private double startValue, elapsed, duration;

    public void reset()
    {
        value = Double.NaN;
        previousTime = Double.NaN;
        wasControlled = false;
        elapsed = duration = 0;
    }

    /** Time is game time in seconds: duplicate evaluations and pauses cannot advance it. */
    public double update(double vanilla, boolean inScope, boolean sprinting,
            MovementFovConfig config, double time)
    {
        boolean controlled = inScope && config.mode() != MovementFovConfig.Mode.VANILLA;
        double target = controlled
                ? (config.mode() == MovementFovConfig.Mode.SPRINT && sprinting ? config.sprintMultiplier() : 1)
                : vanilla;
        if (Double.isNaN(value) || time < previousTime)
        {
            value = startValue = previousTarget = target;
            previousTime = time;
            wasControlled = controlled;
            elapsed = duration = 0;
            return value;
        }

        double dt = Math.max(0, Math.min(0.25, time - previousTime));
        previousTime = time;
        if (controlled != wasControlled || (controlled && target != previousTarget))
        {
            startValue = value;
            elapsed = 0;
            duration = controlled && target > value ? config.enterSeconds() : config.exitSeconds();
        }
        wasControlled = controlled;
        previousTarget = target;
        elapsed = Math.min(duration, elapsed + dt);
        double progress = duration == 0 ? 1 : elapsed / duration;
        double weight = progress * progress * (3 - 2 * progress);
        // During scope release, follow the live vanilla target without restarting the transition.
        value = progress >= 1 ? target : startValue + (target - startValue) * weight;
        return value;
    }

    /** Invert vanilla's (speed / walkingSpeed + 1) / 2 only for its FOV calculation. */
    public double fovSpeed(double speed, float walkingSpeed, boolean inScope, boolean sprinting,
            MovementFovConfig config, double time)
    {
        double vanilla = ((float) speed / walkingSpeed + 1.0F) / 2.0F;
        if (walkingSpeed <= 0 || !Float.isFinite(walkingSpeed) || !Double.isFinite(vanilla))
        {
            reset();
            return speed; // Keep vanilla's invalid/zero walking-speed fallback.
        }
        double factor = update(vanilla, inScope, sprinting, config, time);
        return factor == vanilla ? speed : walkingSpeed * (2 * factor - 1);
    }
}
