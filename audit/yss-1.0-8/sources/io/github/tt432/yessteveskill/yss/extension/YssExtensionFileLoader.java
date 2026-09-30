/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.format.parser.pojo.animation.Animation
 *  com.elfmcys.ysm.format.parser.pojo.animation.AnimationFile
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.yss.extension;

import com.elfmcys.ysm.format.parser.pojo.animation.Animation;
import com.elfmcys.ysm.format.parser.pojo.animation.AnimationFile;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.tt432.yessteveskill.yss.YssAnimationKey;
import io.github.tt432.yessteveskill.yss.YssRuntimeProjectResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YssExtensionFileLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/YssLoader");
    private final Map<Path, CacheEntry> cache = new HashMap<Path, CacheEntry>();

    @Nullable
    public LoadedAnimation loadAnimation(YssAnimationKey key) {
        LoadedAnimation loadedAnimation;
        YssRuntimeProjectResolver.ResolvedProject resolvedProject = YssRuntimeProjectResolver.resolve(key.modelId());
        if (resolvedProject == null) {
            this.cache.remove(YssRuntimeProjectResolver.resolvePreferredSourcePath(key.modelId()));
            return null;
        }
        Path sourcePath = resolvedProject.sourcePath();
        byte[] animationsBytes = resolvedProject.animationsBytes();
        if (animationsBytes == null) {
            this.cache.remove(sourcePath);
            return null;
        }
        CacheEntry cached = this.cache.get(sourcePath);
        if (cached != null && cached.stamp.equals((Object)resolvedProject.stamp())) {
            return this.findAnimation(cached.root, cached.animationFile, key.animationName());
        }
        InputStreamReader reader = new InputStreamReader((InputStream)new ByteArrayInputStream(animationsBytes), StandardCharsets.UTF_8);
        try {
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            AnimationFile animationFile = (AnimationFile)AnimationFile.createGson((boolean)false).fromJson((JsonElement)root, AnimationFile.class);
            this.cache.put(sourcePath, new CacheEntry(resolvedProject.stamp(), root, animationFile));
            LOGGER.debug("Loaded yss animation file {}", (Object)sourcePath);
            loadedAnimation = this.findAnimation(root, animationFile, key.animationName());
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((Reader)reader).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                sourcePath = YssRuntimeProjectResolver.resolvePreferredSourcePath(key.modelId());
                LOGGER.warn("Failed to read yss extension file {}", (Object)sourcePath, (Object)e);
                this.cache.remove(sourcePath);
                return null;
            }
            catch (Exception e) {
                sourcePath = YssRuntimeProjectResolver.resolvePreferredSourcePath(key.modelId());
                LOGGER.warn("Invalid yss extension file {}", (Object)sourcePath, (Object)e);
                this.cache.remove(sourcePath);
                return null;
            }
        }
        ((Reader)reader).close();
        return loadedAnimation;
    }

    @Nullable
    private LoadedAnimation findAnimation(JsonObject root, AnimationFile animationFile, String animationName) {
        Animation entry = null;
        for (Animation candidate : animationFile.animations) {
            if (!animationName.equals(candidate.animationName)) continue;
            entry = candidate;
            break;
        }
        if (entry == null) {
            return null;
        }
        if (!root.has("animations") || !root.get("animations").isJsonObject()) {
            return null;
        }
        JsonObject animations = root.getAsJsonObject("animations");
        if (!animations.has(animationName) || !animations.get(animationName).isJsonObject()) {
            return null;
        }
        JsonObject animationObject = animations.getAsJsonObject(animationName);
        if (!animationObject.has("extension") || !animationObject.get("extension").isJsonObject()) {
            return null;
        }
        return new LoadedAnimation(entry, animationObject.getAsJsonObject("extension"));
    }

    private record CacheEntry(YssRuntimeProjectResolver.Stamp stamp, JsonObject root, AnimationFile animationFile) {
    }

    public record LoadedAnimation(Animation entry, JsonObject extensions) {
    }
}

