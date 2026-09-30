/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.yss.attack;

import io.github.tt432.yessteveskill.combat.config.HitReset;
import io.github.tt432.yessteveskill.combat.config.TimeWindow;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public final class SegmentHitTracker {
    private final HitReset resetPolicy;
    private final HitReset.Type type;
    private final Set<Integer> hitOnce = new LinkedHashSet<Integer>();
    private final Set<Integer> prevInside = new LinkedHashSet<Integer>();
    private final Set<Integer> hitInCurrentStay = new LinkedHashSet<Integer>();
    private final Map<Integer, Float> nextAvailableAt = new LinkedHashMap<Integer, Float>();

    public SegmentHitTracker(HitReset resetPolicy) {
        if (resetPolicy == null) {
            throw new NullPointerException("resetPolicy");
        }
        this.resetPolicy = resetPolicy;
        this.type = resetPolicy.type();
    }

    public Set<Integer> resolveHits(float animSeconds, Set<Integer> currentInsideTargets) {
        switch (this.type) {
            case ONCE: {
                return this.resolveOnce(currentInsideTargets);
            }
            case ON_EXIT: {
                return this.resolveOnExit(currentInsideTargets);
            }
            case INTERVAL: {
                return this.resolveInterval(animSeconds, currentInsideTargets);
            }
        }
        return new LinkedHashSet<Integer>();
    }

    private Set<Integer> resolveOnce(Set<Integer> currentInside) {
        LinkedHashSet<Integer> newHits = new LinkedHashSet<Integer>();
        for (int target : currentInside) {
            if (!this.hitOnce.add(target)) continue;
            newHits.add(target);
        }
        return newHits;
    }

    private Set<Integer> resolveOnExit(Set<Integer> currentInside) {
        for (int prev : this.prevInside) {
            if (currentInside.contains(prev)) continue;
            this.hitInCurrentStay.remove(prev);
        }
        LinkedHashSet<Integer> newHits = new LinkedHashSet<Integer>();
        for (int target : currentInside) {
            if (!this.hitInCurrentStay.add(target)) continue;
            newHits.add(target);
        }
        this.prevInside.clear();
        this.prevInside.addAll(currentInside);
        return newHits;
    }

    private Set<Integer> resolveInterval(float animSeconds, Set<Integer> currentInside) {
        float interval = this.resetPolicy.interval();
        LinkedHashSet<Integer> newHits = new LinkedHashSet<Integer>();
        for (int target : currentInside) {
            Float next = this.nextAvailableAt.get(target);
            if (next != null && !(animSeconds >= next.floatValue())) continue;
            newHits.add(target);
            this.nextAvailableAt.put(target, Float.valueOf(animSeconds + interval));
        }
        return newHits;
    }

    public static boolean isActive(@Nullable TimeWindow time, float animSeconds) {
        if (time == null) {
            return true;
        }
        return animSeconds >= time.start() && animSeconds <= time.end();
    }
}

