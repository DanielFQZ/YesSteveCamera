/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.combat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.github.tt432.yessteveskill.combat.config.AnimationHitConfig;
import io.github.tt432.yessteveskill.combat.config.CasterMoveConfig;
import io.github.tt432.yessteveskill.combat.config.HitProjectConfig;
import io.github.tt432.yessteveskill.combat.config.HitSegment;
import io.github.tt432.yessteveskill.yss.YssPathHelper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class YssCombatConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/CombatConfig");
    private static final ConcurrentHashMap<String, CacheEntry> CACHE = new ConcurrentHashMap();

    private YssCombatConfigLoader() {
    }

    public static Optional<HitProjectConfig> resolveProject(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return Optional.empty();
        }
        Path file = YssPathHelper.resolveProjectDirectory(modelId).resolve("hit.json");
        return YssCombatConfigLoader.load(modelId, file);
    }

    public static Optional<HitSegment> resolveSegment(String modelId, String animName, int segmentId) {
        Optional<HitProjectConfig> project = YssCombatConfigLoader.resolveProject(modelId);
        if (project.isEmpty() || animName == null) {
            return Optional.empty();
        }
        AnimationHitConfig anim = project.get().animations().get(animName);
        if (anim == null) {
            return Optional.empty();
        }
        List<HitSegment> segments = anim.segments();
        if (segmentId < 0 || segmentId >= segments.size()) {
            return Optional.empty();
        }
        return Optional.of(segments.get(segmentId));
    }

    public static Optional<CasterMoveConfig> resolveCasterMove(String modelId, String animName, int moveIndex) {
        Optional<HitProjectConfig> project = YssCombatConfigLoader.resolveProject(modelId);
        if (project.isEmpty() || animName == null) {
            return Optional.empty();
        }
        AnimationHitConfig anim = project.get().animations().get(animName);
        if (anim == null) {
            return Optional.empty();
        }
        List<CasterMoveConfig> moves = anim.casterMoves();
        if (moveIndex < 0 || moveIndex >= moves.size()) {
            return Optional.empty();
        }
        return Optional.of(moves.get(moveIndex));
    }

    public static void invalidate(String modelId) {
        if (modelId != null) {
            CACHE.remove(modelId);
        }
    }

    public static void invalidateAll() {
        CACHE.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Optional<HitProjectConfig> load(String modelId, Path file) {
        long size;
        long mtime;
        try {
            mtime = Files.getLastModifiedTime(file, new LinkOption[0]).toMillis();
            size = Files.size(file);
        }
        catch (IOException e) {
            LOGGER.debug("hit.json missing for {} at {}", (Object)modelId, (Object)file);
            return Optional.empty();
        }
        CacheEntry cached = CACHE.get(modelId);
        if (cached != null && cached.mtime() == mtime && cached.size() == size) {
            return Optional.of(cached.config());
        }
        ConcurrentHashMap<String, CacheEntry> concurrentHashMap = CACHE;
        synchronized (concurrentHashMap) {
            CacheEntry again = CACHE.get(modelId);
            if (again != null && again.mtime() == mtime && again.size() == size) {
                return Optional.of(again.config());
            }
            Optional<HitProjectConfig> parsed = YssCombatConfigLoader.parseFile(modelId, file);
            if (parsed.isPresent()) {
                CACHE.put(modelId, new CacheEntry(parsed.get(), mtime, size));
            }
            return parsed;
        }
    }

    private static Optional<HitProjectConfig> parseFile(String modelId, Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            JsonElement element = JsonParser.parseString((String)new String(bytes, StandardCharsets.UTF_8));
            if (!(element instanceof JsonObject)) {
                LOGGER.warn("hit.json for {} is not a JSON object: {}", (Object)modelId, (Object)file);
                return Optional.empty();
            }
            JsonObject obj = (JsonObject)element;
            return Optional.of(HitProjectConfig.parse(obj));
        }
        catch (IOException e) {
            LOGGER.warn("Failed to read hit.json for {} at {}: {}", new Object[]{modelId, file, e.getMessage()});
            return Optional.empty();
        }
        catch (JsonSyntaxException e) {
            LOGGER.warn("Malformed hit.json for {} at {}: {}", new Object[]{modelId, file, e.getMessage()});
            return Optional.empty();
        }
        catch (RuntimeException e) {
            LOGGER.warn("Failed to parse hit.json for {} at {}: {}", new Object[]{modelId, file, e.getMessage(), e});
            return Optional.empty();
        }
    }

    private record CacheEntry(HitProjectConfig config, long mtime, long size) {
    }
}

