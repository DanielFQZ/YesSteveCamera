/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.yss.project;

import java.nio.file.Path;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public record YssProject(Path directory, String name, String modelId, List<String> bones, List<String> animations, @Nullable String errorMessage) {
    public YssProject(Path directory, String name, String modelId, List<String> bones, List<String> animations, @Nullable String errorMessage) {
        bones = List.copyOf(bones);
        animations = List.copyOf(animations);
    }

    public boolean canExport() {
        return this.errorMessage == null;
    }
}

