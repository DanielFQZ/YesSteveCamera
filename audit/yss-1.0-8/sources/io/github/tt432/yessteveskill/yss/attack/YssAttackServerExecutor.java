/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.ForgeMod
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.yss.attack;

import io.github.tt432.yessteveskill.combat.CombatState;
import io.github.tt432.yessteveskill.combat.CombatStateCapability;
import io.github.tt432.yessteveskill.combat.CombatStatsResolver;
import io.github.tt432.yessteveskill.combat.YssCombatConfigLoader;
import io.github.tt432.yessteveskill.combat.config.AnimationHitConfig;
import io.github.tt432.yessteveskill.combat.config.CasterMoveConfig;
import io.github.tt432.yessteveskill.combat.config.CasterMoveDirection;
import io.github.tt432.yessteveskill.combat.config.CasterMoveType;
import io.github.tt432.yessteveskill.combat.config.DamageTiming;
import io.github.tt432.yessteveskill.combat.config.EntityCombatStats;
import io.github.tt432.yessteveskill.combat.config.HitProjectConfig;
import io.github.tt432.yessteveskill.combat.config.HitSegment;
import io.github.tt432.yessteveskill.combat.config.HitstopConfig;
import io.github.tt432.yessteveskill.combat.config.HoverConfig;
import io.github.tt432.yessteveskill.combat.config.InterruptConfig;
import io.github.tt432.yessteveskill.combat.config.KnockbackConfig;
import io.github.tt432.yessteveskill.combat.config.KnockbackDirection;
import io.github.tt432.yessteveskill.combat.config.KnockupConfig;
import io.github.tt432.yessteveskill.combat.config.TimeWindow;
import io.github.tt432.yessteveskill.network.YssNetwork;
import io.github.tt432.yessteveskill.yss.attack.YssAttackModeCapability;
import io.github.tt432.yessteveskill.yss.attack.YssAttackModeEventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class YssAttackServerExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/AttackServer");
    private static final float KNOCKUP_GRAVITY_SCALE = 0.5f;
    private static final float WEIGHT_DECAY_PER_POINT = 0.1f;
    private static final float KNOCKUP_TICKS_PER_POWER = 20.0f;
    private static final int KNOCKUP_MIN_TICKS = 10;
    private static final int KNOCKUP_MAX_TICKS = 60;
    private static final int INTERRUPTED_TICKS = 20;
    private static final long RATE_MIN_INTERVAL = 1L;
    private static final long RATE_CLEANUP_AGE = 200L;
    private static final Map<UUID, Map<String, Map<UUID, Long>>> RATE = new ConcurrentHashMap<UUID, Map<String, Map<UUID, Long>>>();
    private static final Map<UUID, PendingHit> PENDING = new ConcurrentHashMap<UUID, PendingHit>();

    private YssAttackServerExecutor() {
    }

    public static void executeHitEvent(ServerPlayer player, int segmentId, int[] targetIds, String animName, float clientAnimTime) {
        if (!YssAttackModeCapability.isEnabled((Player)player) || targetIds.length == 0) {
            return;
        }
        String modelId = YssAttackModeCapability.getCachedModelId((Player)player);
        if (modelId == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("HitEvent rejected: no modelId, player={}", (Object)player.m_7755_().getString());
            }
            return;
        }
        HitProjectConfig projectConfig = YssCombatConfigLoader.resolveProject(modelId).orElse(null);
        Optional<HitSegment> segmentOpt = YssCombatConfigLoader.resolveSegment(modelId, animName, segmentId);
        if (segmentOpt.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("HitEvent rejected: no segment model={} anim={} seg={}", new Object[]{modelId, animName, segmentId});
            }
            return;
        }
        HitSegment segment = segmentOpt.get();
        TimeWindow tw = segment.time();
        if (tw != null && (clientAnimTime < tw.start() || clientAnimTime > tw.end())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("HitEvent rejected: out of time window [{} {}] got {}", new Object[]{Float.valueOf(tw.start()), Float.valueOf(tw.end()), Float.valueOf(clientAnimTime)});
            }
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("HitEvent: player={} segment={} anim={} time={} targets={}", new Object[]{player.m_7755_().getString(), segmentId, animName, Float.valueOf(clientAnimTime), targetIds.length});
        }
        long gameTick = player.m_284548_().m_46467_();
        String segKey = animName + ":" + segmentId;
        boolean delayed = segment.damageTiming() == DamageTiming.DELAYED && YssAttackServerExecutor.hasAnyEffect(segment);
        ArrayList<HitTarget> hitTargets = new ArrayList<HitTarget>();
        YssAttackModeEventHandler.withCustomAttackContext(() -> {
            for (int targetId : targetIds) {
                LivingEntity target;
                Entity entity = player.m_9236_().m_6815_(targetId);
                if (!(entity instanceof LivingEntity) || !YssAttackServerExecutor.canAttack(player, target = (LivingEntity)entity) || !YssAttackServerExecutor.checkAndMark(player.m_20148_(), segKey, target.m_20148_(), gameTick)) continue;
                EntityCombatStats stats = CombatStatsResolver.resolveStats(target, projectConfig);
                YssAttackServerExecutor.setTargetHitstop(target, segment.hitstop(), stats);
                YssAttackServerExecutor.tryInterrupt(target, segment.interrupt(), stats);
                if (delayed) {
                    PENDING.put(target.m_20148_(), new PendingHit(player.m_20148_(), modelId, animName, segmentId));
                } else {
                    YssAttackServerExecutor.applyHit(player, target, segment, projectConfig, stats);
                }
                hitTargets.add(new HitTarget(target, stats));
                YssAttackServerExecutor.broadcastState(target);
            }
        });
        YssAttackServerExecutor.setSelfHitstop(player, segment.hitstop(), hitTargets);
        YssAttackServerExecutor.broadcastState((LivingEntity)player);
        if (!hitTargets.isEmpty()) {
            player.m_6674_(InteractionHand.MAIN_HAND);
        }
    }

    public static void executeCasterMove(ServerPlayer player, int moveIndex, String animName, float clientAnimTime) {
        double vz;
        double vx;
        if (!YssAttackModeCapability.isEnabled((Player)player)) {
            return;
        }
        CombatStateCapability selfCap = CombatStateCapability.get((LivingEntity)player);
        if (selfCap != null && selfCap.getState().isHitstopped()) {
            return;
        }
        String modelId = YssAttackModeCapability.getCachedModelId((Player)player);
        if (modelId == null) {
            return;
        }
        Optional<CasterMoveConfig> moveOpt = YssCombatConfigLoader.resolveCasterMove(modelId, animName, moveIndex);
        if (moveOpt.isEmpty()) {
            return;
        }
        CasterMoveConfig move = moveOpt.get();
        TimeWindow tw = move.time();
        if (tw != null && (clientAnimTime < tw.start() || clientAnimTime > tw.end())) {
            return;
        }
        if (move.power() == 0.0f) {
            return;
        }
        if (move.type() == CasterMoveType.SUSTAINED) {
            String moveKey = animName + ":M:" + moveIndex;
            long gameTick = player.m_284548_().m_46467_();
            if (!YssAttackServerExecutor.checkAndMark(player.m_20148_(), moveKey, player.m_20148_(), gameTick)) {
                return;
            }
        }
        double[] dir = YssAttackServerExecutor.casterMoveDir(player, move);
        double px2 = dir[0] * (double)move.power();
        double pz = dir[1] * (double)move.power();
        Vec3 delta = player.m_20184_();
        if (move.type() == CasterMoveType.IMPULSE) {
            vx = px2;
            vz = pz;
        } else {
            vx = delta.f_82479_ + px2;
            vz = delta.f_82481_ + pz;
        }
        player.m_20334_(vx, delta.f_82480_, vz);
        player.f_19864_ = true;
    }

    public static void executeHover(ServerPlayer player, String animName) {
        if (!YssAttackModeCapability.isEnabled((Player)player)) {
            return;
        }
        if (animName == null || animName.isBlank()) {
            return;
        }
        String modelId = YssAttackModeCapability.getCachedModelId((Player)player);
        if (modelId == null) {
            return;
        }
        HitProjectConfig projectConfig = YssCombatConfigLoader.resolveProject(modelId).orElse(null);
        if (projectConfig == null) {
            return;
        }
        AnimationHitConfig animConfig = projectConfig.animations().get(animName);
        if (animConfig == null) {
            return;
        }
        HoverConfig hover = animConfig.hover();
        if (hover == null) {
            return;
        }
        CombatStateCapability sc = CombatStateCapability.get((LivingEntity)player);
        if (sc == null) {
            return;
        }
        int ticks = Math.max(1, Math.round(hover.duration() * 20.0f));
        sc.getState().setHover(ticks);
        sc.getState().setHoverGravityScale(hover.gravityScale());
        YssAttackServerExecutor.broadcastState((LivingEntity)player);
    }

    private static void applyHit(ServerPlayer player, LivingEntity target, HitSegment segment, @Nullable HitProjectConfig projectConfig, @Nullable EntityCombatStats stats) {
        double eff;
        float weight = stats != null ? stats.weight() : 0.0f;
        boolean damageDealt = true;
        if (segment.damage() > 0.0f) {
            damageDealt = target.m_6469_(target.m_9236_().m_269111_().m_269075_((Player)player), segment.damage());
        }
        if (!damageDealt) {
            return;
        }
        KnockbackConfig kb = segment.knockback();
        KnockupConfig ku = segment.knockup();
        if (kb == null && ku == null) {
            return;
        }
        Vec3 delta = target.m_20184_();
        double vx = delta.f_82479_;
        double vy = delta.f_82480_;
        double vz = delta.f_82481_;
        boolean moved = false;
        if (kb != null && kb.power() != 0.0f && (eff = (double)YssAttackServerExecutor.effectiveKnockback(kb.power(), weight)) > 0.0) {
            double[] dir = YssAttackServerExecutor.knockbackDir(player, target, kb);
            vx = dir[0] * eff;
            vz = dir[1] * eff;
            moved = true;
        }
        if (ku != null && ku.power() != 0.0f) {
            vy = ku.power();
            int ticks = YssAttackServerExecutor.knockupTicks(ku.power());
            CombatStateCapability sc = CombatStateCapability.get(target);
            if (sc != null) {
                sc.getState().setKnockup(ticks);
            }
            moved = true;
        }
        if (moved) {
            target.m_20334_(vx, vy, vz);
            target.f_19864_ = true;
        }
    }

    static float effectiveKnockback(float power, float weight) {
        float factor = Math.max(0.0f, Math.min(1.0f, 1.0f - weight * 0.1f));
        return power * factor;
    }

    static double[] knockbackDir(ServerPlayer player, LivingEntity target, KnockbackConfig kb) {
        double baseZ;
        double baseX;
        double yawRad = Math.toRadians(player.m_146908_());
        double offsetRad = Math.toRadians(kb.angleOffset());
        if (kb.direction() == KnockbackDirection.RADIAL) {
            double dz;
            double dx = target.m_20185_() - player.m_20185_();
            double len = Math.sqrt(dx * dx + (dz = target.m_20189_() - player.m_20189_()) * dz);
            if (len < 1.0E-6) {
                baseX = -Math.sin(yawRad);
                baseZ = Math.cos(yawRad);
            } else {
                baseX = dx / len;
                baseZ = dz / len;
            }
        } else {
            baseX = -Math.sin(yawRad);
            baseZ = Math.cos(yawRad);
        }
        double cosO = Math.cos(offsetRad);
        double sinO = Math.sin(offsetRad);
        return new double[]{baseX * cosO - baseZ * sinO, baseX * sinO + baseZ * cosO};
    }

    static double[] casterMoveDir(ServerPlayer player, CasterMoveConfig move) {
        double baseZ;
        double baseX;
        double offsetRad = Math.toRadians(move.angleOffset());
        if (move.direction() == CasterMoveDirection.WORLD) {
            baseX = -Math.sin(offsetRad);
            baseZ = Math.cos(offsetRad);
        } else {
            double yawRad = Math.toRadians(player.m_146908_());
            baseX = -Math.sin(yawRad + offsetRad);
            baseZ = Math.cos(yawRad + offsetRad);
        }
        return new double[]{baseX, baseZ};
    }

    static int knockupTicks(float power) {
        int t = (int)Math.ceil(Math.abs(power) * 20.0f);
        return Math.max(10, Math.min(60, t));
    }

    private static boolean checkAndMark(UUID playerUUID, String segKey, UUID targetUUID, long gameTick) {
        Map playerBuckets = RATE.computeIfAbsent(playerUUID, k -> new HashMap());
        Map bucket = playerBuckets.computeIfAbsent(segKey, k -> new HashMap());
        Long last = (Long)bucket.get(targetUUID);
        if (last != null && gameTick - last < 1L) {
            return false;
        }
        bucket.put(targetUUID, gameTick);
        playerBuckets.entrySet().removeIf(e -> {
            Map b = (Map)e.getValue();
            if (b.isEmpty()) {
                return true;
            }
            long newest = 0L;
            for (Long v : b.values()) {
                if (v <= newest) continue;
                newest = v;
            }
            return gameTick - newest > 200L;
        });
        return true;
    }

    private static boolean canAttack(ServerPlayer player, LivingEntity target) {
        if (!target.m_6084_() || target.m_213877_() || target == player) {
            return false;
        }
        double reach = player.m_21133_((Attribute)ForgeMod.ENTITY_REACH.get());
        if (reach <= 0.0) {
            reach = player.m_7500_() ? 6.0 : 3.0;
        }
        return player.m_20280_((Entity)target) <= reach * reach && player.m_142582_((Entity)target);
    }

    private static void setTargetHitstop(LivingEntity target, @Nullable HitstopConfig cfg, @Nullable EntityCombatStats stats) {
        CombatStateCapability sc;
        if (cfg == null || cfg.target() == null || stats == null) {
            return;
        }
        int ticks = cfg.target().ticks();
        if (!cfg.forced() && cfg.target().scaleByHardness()) {
            ticks = Math.max(1, Math.round((float)ticks / (1.0f + stats.hardness())));
        }
        if ((sc = CombatStateCapability.get(target)) != null) {
            sc.getState().setHitstop(ticks);
        }
    }

    private static void tryInterrupt(LivingEntity target, @Nullable InterruptConfig cfg, @Nullable EntityCombatStats stats) {
        float eff;
        if (cfg == null || stats == null) {
            return;
        }
        CombatStateCapability sc = CombatStateCapability.get(target);
        if (sc == null) {
            return;
        }
        CombatState state = sc.getState();
        float f = eff = state.hasAntiInterruptOverride() ? state.getOverrideAntiInterrupt() : stats.antiInterrupt();
        if (cfg.power() > eff) {
            state.setInterrupted(20);
        }
    }

    private static void setSelfHitstop(ServerPlayer player, @Nullable HitstopConfig cfg, List<HitTarget> hitTargets) {
        int n;
        if (cfg == null || cfg.self() == null || hitTargets.isEmpty()) {
            return;
        }
        int ticks = cfg.self().ticks();
        if (!cfg.forced() && cfg.self().scaleByTargetHardness()) {
            EntityCombatStats first = hitTargets.get(0).stats();
            float h = first != null ? first.hardness() : 0.0f;
            ticks = Math.max(1, Math.round((float)ticks / (1.0f + h)));
        }
        if ((n = hitTargets.size()) > 1) {
            float factor = (float)Math.pow(cfg.self().multiTargetDecay(), n - 1);
            ticks = Math.round((float)ticks * factor);
        }
        ticks = Math.max(0, ticks);
        CombatStateCapability sc = CombatStateCapability.get((LivingEntity)player);
        if (sc != null) {
            sc.getState().setHitstop(ticks);
        }
    }

    public static void tryFirePending(LivingEntity target) {
        PendingHit p = PENDING.remove(target.m_20148_());
        if (p == null) {
            return;
        }
        Level level = target.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        Entity owner = serverLevel.m_8791_(p.playerUUID());
        if (!(owner instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)owner;
        Optional<HitSegment> segmentOpt = YssCombatConfigLoader.resolveSegment(p.modelId(), p.animName(), p.segmentId());
        if (segmentOpt.isEmpty()) {
            return;
        }
        HitProjectConfig projectConfig = YssCombatConfigLoader.resolveProject(p.modelId()).orElse(null);
        EntityCombatStats stats = CombatStatsResolver.resolveStats(target, projectConfig);
        HitSegment segment = segmentOpt.get();
        YssAttackModeEventHandler.withCustomAttackContext(() -> YssAttackServerExecutor.applyHit(player, target, segment, projectConfig, stats));
        YssAttackServerExecutor.broadcastState(target);
        player.m_6674_(InteractionHand.MAIN_HAND);
    }

    private static boolean hasAnyEffect(HitSegment s) {
        return s.damage() > 0.0f || s.knockback() != null || s.knockup() != null;
    }

    private static void broadcastState(LivingEntity entity) {
        CombatStateCapability cap = CombatStateCapability.get(entity);
        if (cap == null) {
            return;
        }
        CombatState s = cap.getState();
        YssNetwork.sendCombatStateSync(entity, s.getHitstopTicks(), s.getInterruptedTicks(), s.getKnockupTicks(), s.getOverrideAntiInterrupt(), s.getOverrideAntiInterruptTicks(), s.getHoverTicks());
    }

    public static void applyKnockupGravityReduction(LivingEntity entity) {
        CombatStateCapability cap = CombatStateCapability.get(entity);
        if (cap == null || !cap.getState().isKnockup()) {
            return;
        }
        Vec3 d = entity.m_20184_();
        if (d.f_82480_ < 0.0) {
            entity.m_20334_(d.f_82479_, d.f_82480_ * 0.5, d.f_82481_);
            entity.f_19864_ = true;
        }
    }

    public static void applyHoverGravityReduction(LivingEntity entity) {
        CombatStateCapability cap = CombatStateCapability.get(entity);
        if (cap == null || !cap.getState().isHover()) {
            return;
        }
        Vec3 d = entity.m_20184_();
        if (d.f_82480_ < 0.0) {
            float scale = cap.getState().getHoverGravityScale();
            entity.m_20334_(d.f_82479_, d.f_82480_ * (double)scale, d.f_82481_);
            entity.f_19864_ = true;
        }
    }

    private record HitTarget(LivingEntity target, @Nullable EntityCombatStats stats) {
    }

    private record PendingHit(UUID playerUUID, String modelId, String animName, int segmentId) {
    }
}

