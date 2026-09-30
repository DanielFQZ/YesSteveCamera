/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrame
 *  com.elfmcys.ysm.molang.runtime.ExpressionEvaluator
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 */
package io.github.tt432.yessteveskill.yss;

import com.elfmcys.ysm.geckolib3.core.keyframe.bone.BoneKeyFrame;
import com.elfmcys.ysm.molang.runtime.ExpressionEvaluator;
import java.util.List;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public final class YssKeyFrameSampler {
    private YssKeyFrameSampler() {
    }

    @Nullable
    public static Vector3f sample(List<BoneKeyFrame> frames, float tick, @Nullable ExpressionEvaluator<?> evaluator) {
        if (frames.isEmpty()) {
            return null;
        }
        BoneKeyFrame selected = frames.get(frames.size() - 1);
        float percent = 1.0f;
        for (int i = 0; i < frames.size(); ++i) {
            BoneKeyFrame frame = frames.get(i);
            if (i != frames.size() - 1 && !(tick < frame.getEndTick())) continue;
            selected = frame;
            float total = frame.getTotalTick();
            percent = total <= 0.0f ? 1.0f : Mth.m_14036_((float)((tick - frame.getStartTick()) / total), (float)0.0f, (float)1.0f);
            break;
        }
        try {
            return selected.getLerpPoint(evaluator, percent);
        }
        catch (RuntimeException e) {
            return null;
        }
    }
}

