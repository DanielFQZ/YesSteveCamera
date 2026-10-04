package com.github.exopandora.shouldersurfing.camera.shake;

/** Camera-local additive result for one render sample. */
public record ShakeSample(double translateX, double translateY, double translateZ,
		float rotateX, float rotateY, float rotateZ, float fov, double distance)
{
	public static ShakeSample zero() { return new ShakeSample(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F, 0.0F, 0.0D); }

	public ShakeSample add(ShakeSample other)
	{
		return new ShakeSample(translateX + other.translateX, translateY + other.translateY, translateZ + other.translateZ,
				rotateX + other.rotateX, rotateY + other.rotateY, rotateZ + other.rotateZ, fov + other.fov, distance + other.distance);
	}
	/** Hard bounds keep overlapping or malformed presets from destabilizing the camera. */
	public ShakeSample limited()
	{
		return new ShakeSample(limit(translateX, 1), limit(translateY, 1), limit(translateZ, 1),
				(float) limit(rotateX, 30), (float) limit(rotateY, 30), (float) limit(rotateZ, 30),
				(float) limit(fov, 40), limit(distance, 2));
	}

	private static double limit(double value, double bound)
	{
		return Double.isNaN(value) ? 0 : Math.max(-bound, Math.min(bound, value));
	}
}
