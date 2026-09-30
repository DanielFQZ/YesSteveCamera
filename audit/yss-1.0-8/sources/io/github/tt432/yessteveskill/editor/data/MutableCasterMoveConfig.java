/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.editor.data;

import io.github.tt432.yessteveskill.combat.config.CasterMoveConfig;
import io.github.tt432.yessteveskill.combat.config.CasterMoveDirection;
import io.github.tt432.yessteveskill.combat.config.CasterMoveType;
import io.github.tt432.yessteveskill.editor.data.MutableTimeWindow;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class MutableCasterMoveConfig {
    @Nullable
    private MutableTimeWindow time;
    private CasterMoveDirection direction;
    private float angleOffset;
    private float power;
    private CasterMoveType type;

    public MutableCasterMoveConfig() {
        this(null, CasterMoveDirection.FACING, 0.0f, 0.0f, CasterMoveType.IMPULSE);
    }

    public MutableCasterMoveConfig(@Nullable MutableTimeWindow time, CasterMoveDirection direction, float angleOffset, float power, CasterMoveType type) {
        this.time = time;
        this.direction = direction;
        this.angleOffset = angleOffset;
        this.power = power;
        this.type = type;
    }

    public static MutableCasterMoveConfig fromRecord(CasterMoveConfig move) {
        return new MutableCasterMoveConfig(MutableTimeWindow.fromRecord(move.time()), move.direction(), move.angleOffset(), move.power(), move.type());
    }

    public CasterMoveConfig toRecord() {
        return new CasterMoveConfig(this.time == null ? Optional.empty() : Optional.of(this.time.toRecord()), this.direction != null ? this.direction : CasterMoveDirection.FACING, this.angleOffset, this.power, this.type != null ? this.type : CasterMoveType.IMPULSE);
    }

    @Nullable
    public MutableTimeWindow getTime() {
        return this.time;
    }

    public void setTime(@Nullable MutableTimeWindow time) {
        this.time = time;
    }

    public CasterMoveDirection getDirection() {
        return this.direction;
    }

    public void setDirection(CasterMoveDirection direction) {
        this.direction = direction;
    }

    public float getAngleOffset() {
        return this.angleOffset;
    }

    public void setAngleOffset(float angleOffset) {
        this.angleOffset = angleOffset;
    }

    public float getPower() {
        return this.power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public CasterMoveType getType() {
        return this.type;
    }

    public void setType(CasterMoveType type) {
        this.type = type;
    }
}

