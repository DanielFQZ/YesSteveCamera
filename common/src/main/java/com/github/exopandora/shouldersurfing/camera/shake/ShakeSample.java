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
}
