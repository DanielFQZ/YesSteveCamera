/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.yss;

import io.github.tt432.yessteveskill.yss.YssPathHelper;
import io.github.tt432.yessteveskill.yss.project.YssBinaryProjectIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;

public final class YssRuntimeProjectResolver {
    public static final String MAIN_FILE_NAME = "main.json";
    public static final String HIT_FILE_NAME = "hit.json";
    public static final String ANIMATIONS_FILE_NAME = "animations.json";

    private YssRuntimeProjectResolver() {
    }

    public static Path resolvePreferredSourcePath(String modelId) {
        Path directory = YssPathHelper.resolveProjectDirectory(modelId);
        if (Files.isDirectory(directory, new LinkOption[0])) {
            return directory;
        }
        return YssPathHelper.resolveProjectBinaryFile(modelId);
    }

    public static boolean hasHitConfig(String modelId) {
        Path directory = YssPathHelper.resolveProjectDirectory(modelId);
        if (Files.isDirectory(directory, new LinkOption[0])) {
            return Files.isRegularFile(directory.resolve(HIT_FILE_NAME), new LinkOption[0]);
        }
        return Files.isRegularFile(YssPathHelper.resolveProjectBinaryFile(modelId), new LinkOption[0]);
    }

    @Nullable
    public static ResolvedProject resolve(String modelId) throws IOException {
        String normalizedModelId = YssPathHelper.normalizeModelId(modelId);
        Path directory = YssPathHelper.resolveProjectDirectory(modelId);
        if (Files.isDirectory(directory, new LinkOption[0])) {
            return YssRuntimeProjectResolver.resolveDirectory(directory, normalizedModelId);
        }
        Path binaryFile = YssPathHelper.resolveProjectBinaryFile(modelId);
        if (!Files.isRegularFile(binaryFile, new LinkOption[0])) {
            return null;
        }
        YssBinaryProjectIO.LoadedBinaryProject binaryProject = YssBinaryProjectIO.read(binaryFile);
        byte[] mainBytes = binaryProject.file(MAIN_FILE_NAME);
        byte[] hitBytes = YssRuntimeProjectResolver.required(binaryProject, HIT_FILE_NAME);
        byte[] animationsBytes = binaryProject.file(ANIMATIONS_FILE_NAME);
        FileFingerprint binaryFingerprint = YssRuntimeProjectResolver.fingerprint(binaryFile);
        return new ResolvedProject(binaryFile, binaryProject.modelId(), null, mainBytes, hitBytes, animationsBytes, new Stamp(mainBytes == null ? null : binaryFingerprint, binaryFingerprint, animationsBytes == null ? null : binaryFingerprint));
    }

    private static ResolvedProject resolveDirectory(Path directory, String modelId) throws IOException {
        Path mainPath = directory.resolve(MAIN_FILE_NAME);
        Path hitPath = directory.resolve(HIT_FILE_NAME);
        Path animationsPath = directory.resolve(ANIMATIONS_FILE_NAME);
        if (!Files.isRegularFile(hitPath, new LinkOption[0])) {
            return null;
        }
        boolean hasMain = Files.isRegularFile(mainPath, new LinkOption[0]);
        boolean hasAnimations = Files.isRegularFile(animationsPath, new LinkOption[0]);
        return new ResolvedProject(directory, modelId, hasMain ? mainPath : null, hasMain ? Files.readAllBytes(mainPath) : null, Files.readAllBytes(hitPath), hasAnimations ? Files.readAllBytes(animationsPath) : null, new Stamp(hasMain ? YssRuntimeProjectResolver.fingerprint(mainPath) : null, YssRuntimeProjectResolver.fingerprint(hitPath), hasAnimations ? YssRuntimeProjectResolver.fingerprint(animationsPath) : null));
    }

    private static byte[] required(YssBinaryProjectIO.LoadedBinaryProject binaryProject, String fileName) throws IOException {
        byte[] bytes = binaryProject.file(fileName);
        if (bytes == null) {
            throw new IOException("Missing " + fileName + " in " + binaryProject.path().getFileName());
        }
        return bytes;
    }

    private static FileFingerprint fingerprint(Path path) throws IOException {
        return new FileFingerprint(Files.getLastModifiedTime(path, new LinkOption[0]).toMillis(), Files.size(path));
    }

    public record ResolvedProject(Path sourcePath, String modelId, @Nullable Path mainFilePath, @Nullable byte[] mainBytes, byte[] hitBytes, @Nullable byte[] animationsBytes, Stamp stamp) {
    }

    public record FileFingerprint(long modifiedTime, long size) {
    }

    public record Stamp(@Nullable FileFingerprint main, FileFingerprint hit, @Nullable FileFingerprint animations) {
    }
}

