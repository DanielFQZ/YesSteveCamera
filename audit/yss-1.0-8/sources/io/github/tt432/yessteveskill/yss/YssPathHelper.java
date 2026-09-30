/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package io.github.tt432.yessteveskill.yss;

import io.github.tt432.yessteveskill.yss.YssAnimationKey;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import net.minecraftforge.fml.loading.FMLPaths;

public final class YssPathHelper {
    public static final String ROOT_DIRECTORY_NAME = "yes_steve_skill";
    private static final Set<String> KNOWN_EXT = Set.of((Object)".zip", (Object)".7z", (Object)".ysm");

    private YssPathHelper() {
    }

    public static Path resolveRootDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve(ROOT_DIRECTORY_NAME);
    }

    public static Path resolveProjectDirectory(String modelId) {
        return YssPathHelper.resolveRootDirectory().resolve(YssPathHelper.sanitizeSegment(YssPathHelper.normalizeModelId(modelId)));
    }

    public static Path resolveProjectBinaryFile(String modelId) {
        return YssPathHelper.resolveRootDirectory().resolve(YssPathHelper.sanitizeSegment(YssPathHelper.normalizeModelId(modelId)) + ".yss");
    }

    public static Path resolveAnimations(YssAnimationKey key) {
        return YssPathHelper.resolveProjectDirectory(key.modelId()).resolve("animations.json");
    }

    public static String normalizeModelId(String raw) {
        String trimmed = raw.trim();
        int lastSlash = trimmed.lastIndexOf(47);
        String modelName = lastSlash == -1 ? trimmed : trimmed.substring(lastSlash + 1);
        int lastDot = modelName.lastIndexOf(46);
        if (lastDot < 1) {
            return modelName;
        }
        String ext = modelName.substring(lastDot).toLowerCase(Locale.ROOT);
        if (!KNOWN_EXT.contains(ext)) {
            return modelName;
        }
        return modelName.substring(0, lastDot);
    }

    static String sanitizeSegment(String raw) {
        StringBuilder builder = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if (c < ' ' || c == '<' || c == '>' || c == ':' || c == '\"' || c == '/' || c == '\\' || c == '|' || c == '?' || c == '*') {
                builder.append('_');
                continue;
            }
            builder.append(c);
        }
        return builder.toString();
    }
}

