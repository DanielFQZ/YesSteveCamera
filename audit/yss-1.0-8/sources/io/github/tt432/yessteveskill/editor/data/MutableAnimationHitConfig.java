/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.AnimationHitConfig;
import io.github.tt432.yessteveskill.combat.config.CasterMoveConfig;
import io.github.tt432.yessteveskill.combat.config.HitSegment;
import io.github.tt432.yessteveskill.editor.data.MutableCasterMoveConfig;
import io.github.tt432.yessteveskill.editor.data.MutableHitSegment;
import io.github.tt432.yessteveskill.editor.data.MutableHoverConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class MutableAnimationHitConfig {
    private List<MutableHitSegment> segments;
    private List<MutableCasterMoveConfig> casterMoves;
    @Nullable
    private MutableHoverConfig hover;
    private boolean rootMotion;

    public MutableAnimationHitConfig() {
        this(new ArrayList<MutableHitSegment>(), new ArrayList<MutableCasterMoveConfig>(), null, false);
    }

    public MutableAnimationHitConfig(List<MutableHitSegment> segments, List<MutableCasterMoveConfig> casterMoves, @Nullable MutableHoverConfig hover, boolean rootMotion) {
        this.segments = segments;
        this.casterMoves = casterMoves;
        this.hover = hover;
        this.rootMotion = rootMotion;
    }

    public static MutableAnimationHitConfig fromRecord(AnimationHitConfig config) {
        ArrayList<MutableHitSegment> segments = new ArrayList<MutableHitSegment>();
        for (HitSegment seg : config.segments()) {
            segments.add(MutableHitSegment.fromRecord(seg));
        }
        ArrayList<MutableCasterMoveConfig> moves = new ArrayList<MutableCasterMoveConfig>();
        for (CasterMoveConfig move : config.casterMoves()) {
            moves.add(MutableCasterMoveConfig.fromRecord(move));
        }
        return new MutableAnimationHitConfig(segments, moves, MutableHoverConfig.fromRecord(config.hover()), config.rootMotion());
    }

    public AnimationHitConfig toRecord() {
        ArrayList<HitSegment> segs = new ArrayList<HitSegment>();
        if (this.segments != null) {
            for (MutableHitSegment seg : this.segments) {
                segs.add(seg.toRecord());
            }
        }
        ArrayList<CasterMoveConfig> moves = new ArrayList<CasterMoveConfig>();
        if (this.casterMoves != null) {
            for (MutableCasterMoveConfig move : this.casterMoves) {
                moves.add(move.toRecord());
            }
        }
        return new AnimationHitConfig(List.copyOf(segs), List.copyOf(moves), this.hover == null ? Optional.empty() : Optional.of(this.hover.toRecord()), this.rootMotion);
    }

    public List<MutableHitSegment> getSegments() {
        return this.segments;
    }

    public void setSegments(List<MutableHitSegment> segments) {
        this.segments = segments;
    }

    public List<MutableCasterMoveConfig> getCasterMoves() {
        return this.casterMoves;
    }

    public void setCasterMoves(List<MutableCasterMoveConfig> casterMoves) {
        this.casterMoves = casterMoves;
    }

    @Nullable
    public MutableHoverConfig getHover() {
        return this.hover;
    }

    public void setHover(@Nullable MutableHoverConfig hover) {
        this.hover = hover;
    }

    public boolean isRootMotion() {
        return this.rootMotion;
    }

    public void setRootMotion(boolean rootMotion) {
        this.rootMotion = rootMotion;
    }
}

