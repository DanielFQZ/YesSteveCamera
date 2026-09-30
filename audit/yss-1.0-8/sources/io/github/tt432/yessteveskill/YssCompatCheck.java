/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill;

import io.github.tt432.yessteveskill.client.gui.YssHitEditorScreenCompatibilityChecker;
import io.github.tt432.yessteveskill.client.gui.YssModelPreviewRendererCompatibilityChecker;
import io.github.tt432.yessteveskill.client.gui.YssPreviewAnimationLoaderCompatibilityChecker;
import io.github.tt432.yessteveskill.editor.data.AnimationTimelineInfoCompatibilityChecker;
import io.github.tt432.yessteveskill.ysm.YSMClientAccessCompatibilityChecker;
import io.github.tt432.yessteveskill.ysm.YSMModelAssetsCompatibilityChecker;
import io.github.tt432.yessteveskill.yss.attack.YssAttackModelSamplerCompatibilityChecker;
import io.github.tt432.yessteveskill.yss.attack.YssAttackProjectLoaderCompatibilityChecker;
import io.github.tt432.yessteveskill.yss.attack.YssAttackRuntimeCompatibilityChecker;
import io.github.tt432.yessteveskill.yss.attack.YssAttackServiceCompatibilityChecker;
import io.github.tt432.yessteveskill.yss.camera.YssCameraExtensionTypeCompatibilityChecker;
import io.github.tt432.yessteveskill.yss.extension.YssExtensionFileLoaderCompatibilityChecker;
import io.github.tt432.yessteveskill.yss.molang.YssMolangBindingCompatibilityChecker;
import io.github.tt432.yessteveskill.yss.molang.YssMolangRegisterCompatibilityChecker;
import io.github.tt432.yessteveskill.yss.runtime.YssCameraRuntimeCompatibilityChecker;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class YssCompatCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/Compat");
    private static volatile boolean verified;
    private static volatile String lastSummary;

    private YssCompatCheck() {
    }

    static void verifyClient() {
        ArrayList<String> failures = new ArrayList<String>();
        YSMClientAccessCompatibilityChecker.Result r1 = YSMClientAccessCompatibilityChecker.check();
        YssCompatCheck.report("ysm.YSMClientAccess", r1.status().name(), r1.isCompatible(), r1.coverageComplete(), r1.issues(), failures);
        YSMModelAssetsCompatibilityChecker.Result r2 = YSMModelAssetsCompatibilityChecker.check();
        YssCompatCheck.report("ysm.YSMModelAssets", r2.status().name(), r2.isCompatible(), r2.coverageComplete(), r2.issues(), failures);
        YssAttackServiceCompatibilityChecker.Result r3 = YssAttackServiceCompatibilityChecker.check();
        YssCompatCheck.report("attack.YssAttackService", r3.status().name(), r3.isCompatible(), r3.coverageComplete(), r3.issues(), failures);
        YssAttackRuntimeCompatibilityChecker.Result r4 = YssAttackRuntimeCompatibilityChecker.check();
        YssCompatCheck.report("attack.YssAttackRuntime", r4.status().name(), r4.isCompatible(), r4.coverageComplete(), r4.issues(), failures);
        YssAttackModelSamplerCompatibilityChecker.Result r5 = YssAttackModelSamplerCompatibilityChecker.check();
        YssCompatCheck.report("attack.YssAttackModelSampler", r5.status().name(), r5.isCompatible(), r5.coverageComplete(), r5.issues(), failures);
        YssAttackProjectLoaderCompatibilityChecker.Result r6 = YssAttackProjectLoaderCompatibilityChecker.check();
        YssCompatCheck.report("attack.YssAttackProjectLoader", r6.status().name(), r6.isCompatible(), r6.coverageComplete(), r6.issues(), failures);
        YssHitEditorScreenCompatibilityChecker.Result r7 = YssHitEditorScreenCompatibilityChecker.check();
        YssCompatCheck.report("gui.YssHitEditorScreen", r7.status().name(), r7.isCompatible(), r7.coverageComplete(), r7.issues(), failures);
        YssModelPreviewRendererCompatibilityChecker.Result r8 = YssModelPreviewRendererCompatibilityChecker.check();
        YssCompatCheck.report("gui.YssModelPreviewRenderer", r8.status().name(), r8.isCompatible(), r8.coverageComplete(), r8.issues(), failures);
        YssPreviewAnimationLoaderCompatibilityChecker.Result r9 = YssPreviewAnimationLoaderCompatibilityChecker.check();
        YssCompatCheck.report("gui.YssPreviewAnimationLoader", r9.status().name(), r9.isCompatible(), r9.coverageComplete(), r9.issues(), failures);
        AnimationTimelineInfoCompatibilityChecker.Result r10 = AnimationTimelineInfoCompatibilityChecker.check();
        YssCompatCheck.report("data.AnimationTimelineInfo", r10.status().name(), r10.isCompatible(), r10.coverageComplete(), r10.issues(), failures);
        YssCameraExtensionTypeCompatibilityChecker.Result r11 = YssCameraExtensionTypeCompatibilityChecker.check();
        YssCompatCheck.report("camera.YssCameraExtensionType", r11.status().name(), r11.isCompatible(), r11.coverageComplete(), r11.issues(), failures);
        YssExtensionFileLoaderCompatibilityChecker.Result r12 = YssExtensionFileLoaderCompatibilityChecker.check();
        YssCompatCheck.report("extension.YssExtensionFileLoader", r12.status().name(), r12.isCompatible(), r12.coverageComplete(), r12.issues(), failures);
        YssCameraRuntimeCompatibilityChecker.Result r13 = YssCameraRuntimeCompatibilityChecker.check();
        YssCompatCheck.report("camera.YssCameraRuntime", r13.status().name(), r13.isCompatible(), r13.coverageComplete(), r13.issues(), failures);
        YssMolangBindingCompatibilityChecker.Result r14 = YssMolangBindingCompatibilityChecker.check();
        YssCompatCheck.report("molang.YssMolangBinding", r14.status().name(), r14.isCompatible(), r14.coverageComplete(), r14.issues(), failures);
        YssMolangRegisterCompatibilityChecker.Result r15 = YssMolangRegisterCompatibilityChecker.check();
        YssCompatCheck.report("molang.YssMolangRegister", r15.status().name(), r15.isCompatible(), r15.coverageComplete(), r15.issues(), failures);
        verified = failures.isEmpty();
        Object object = lastSummary = verified ? "COMPATIBLE (15 checkers)" : "FAILED: " + failures;
        if (!verified) {
            throw new IllegalStateException("yessteveskill \u4e0e\u5f53\u524d YSM \u7248\u672c\u4e0d\u517c\u5bb9\uff0c\u8bf7\u66f4\u65b0 YSM \u6216\u91cd\u65b0\u6784\u5efa\u672c\u6a21\u7ec4: " + failures);
        }
        LOGGER.info("YSM \u517c\u5bb9\u6027\u68c0\u67e5\u5168\u90e8\u901a\u8fc7\uff0815 \u4e2a checker\uff09");
    }

    public static boolean verified() {
        return verified;
    }

    public static String lastSummary() {
        return lastSummary;
    }

    private static void report(String name, String status, boolean compatible, boolean coverageComplete, List<?> issues, List<String> failures) {
        if ("NOT_APPLICABLE".equals(status)) {
            LOGGER.info("YSM \u517c\u5bb9\u6027\u68c0\u67e5\u8df3\u8fc7 {} -> {}", (Object)name, (Object)status);
            return;
        }
        if (!compatible) {
            failures.add(name + " -> " + status + " " + issues);
            LOGGER.error("YSM \u517c\u5bb9\u6027\u68c0\u67e5\u5931\u8d25 {} -> {} issues={}", new Object[]{name, status, issues});
            return;
        }
        if (!coverageComplete || !issues.isEmpty()) {
            LOGGER.warn("YSM \u517c\u5bb9\u6027\u68c0\u67e5\u901a\u8fc7\u4f46\u5b58\u5728\u8986\u76d6\u7f3a\u53e3 {} -> {} issues={}", new Object[]{name, status, issues});
            return;
        }
        LOGGER.info("YSM \u517c\u5bb9\u6027\u68c0\u67e5\u901a\u8fc7 {} -> {}", (Object)name, (Object)status);
    }

    static {
        lastSummary = "not run";
    }
}

