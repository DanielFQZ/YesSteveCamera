package com.github.exopandora.shouldersurfing.camera;

import com.github.exopandora.shouldersurfing.camera.assist.RootMotionClip;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RootMotionClipTest
{
	private final AABB player = new AABB(-0.3, 0, -0.3, 0.3, 1.8, 0.3);
	private final AABB enemy = new AABB(-0.3, 0, 1.7, 0.3, 1.8, 2.3);

	@Test void shortStepsRemainUntouchedButFastStepsCannotTunnel()
	{
		assertEquals(1, RootMotionClip.horizontalScale(player, enemy, new Vec3(0, 0, 0.1)));
		assertEquals(1.38 / 4, RootMotionClip.horizontalScale(player, enemy, new Vec3(0, 0, 4)), 1e-9);
	}

	@Test void touchingStopsForwardButAllowsRetreatAndSideways()
	{
		AABB touching = player.move(0, 0, 1.38);
		assertEquals(0, RootMotionClip.horizontalScale(touching, enemy, new Vec3(0, 0, 0.2)), 1e-9);
		assertEquals(1, RootMotionClip.horizontalScale(touching, enemy, new Vec3(0, 0, -0.2)));
		assertEquals(1, RootMotionClip.horizontalScale(touching, enemy, new Vec3(0.2, 0, 0)));
	}

	@Test void overlapCanEscapeWithoutTeleporting()
	{
		AABB overlap = player.move(0, 0, 1.7);
		assertEquals(0, RootMotionClip.horizontalScale(overlap, enemy, new Vec3(0, 0, 0.2)));
		assertEquals(1, RootMotionClip.horizontalScale(overlap, enemy, new Vec3(0, 0, -0.2)));
	}

	@Test void diagonalMissAndVerticalClearanceDoNotCreateInvisibleWalls()
	{
		assertEquals(1, RootMotionClip.horizontalScale(player, enemy, new Vec3(4, 0, 4)));
		assertEquals(1, RootMotionClip.horizontalScale(player.move(0, 3, 0), enemy, new Vec3(0, 0, 4)));
		assertEquals(1, RootMotionClip.horizontalScale(player, enemy, new Vec3(0, 2, 0)));
	}

	@Test void diagonalApproachStopsAtFirstContactAndNegativeAxesAreSymmetric()
	{
		AABB diagonal = enemy.move(2, 0, 0);
		assertEquals(1.38 / 4, RootMotionClip.horizontalScale(player, diagonal, new Vec3(4, 0, 4)), 1e-9);
		assertEquals(1.38 / 4, RootMotionClip.horizontalScale(player, enemy.move(0, 0, -4), new Vec3(0, 0, -4)), 1e-9);
	}
}
