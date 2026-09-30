/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package io.github.tt432.yessteveskill.yss.attack;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class YssEdgeGuard {
    private static final double BACKOFF_STEP = 0.05;

    private YssEdgeGuard() {
    }

    public static Vec3 trimHorizontal(Entity entity, Vec3 delta) {
        if (delta.f_82479_ == 0.0 && delta.f_82481_ == 0.0) {
            return delta;
        }
        if (!entity.m_20096_()) {
            return delta;
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (player.m_150110_().f_35935_) {
                return delta;
            }
        }
        double x = delta.f_82479_;
        double z = delta.f_82481_;
        double step = entity.m_274421_();
        AABB bounds = entity.m_20191_();
        while (x != 0.0 && entity.m_9236_().m_45756_(entity, bounds.m_82386_(x, -step, 0.0))) {
            x = YssEdgeGuard.backOff(x);
        }
        while (z != 0.0 && entity.m_9236_().m_45756_(entity, bounds.m_82386_(0.0, -step, z))) {
            z = YssEdgeGuard.backOff(z);
        }
        while (x != 0.0 && z != 0.0 && entity.m_9236_().m_45756_(entity, bounds.m_82386_(x, -step, z))) {
            x = YssEdgeGuard.backOff(x);
            z = YssEdgeGuard.backOff(z);
        }
        return x == delta.f_82479_ && z == delta.f_82481_ ? delta : new Vec3(x, delta.f_82480_, z);
    }

    private static double backOff(double value) {
        if (value < 0.05 && value >= -0.05) {
            return 0.0;
        }
        return value > 0.0 ? value - 0.05 : value + 0.05;
    }
}

