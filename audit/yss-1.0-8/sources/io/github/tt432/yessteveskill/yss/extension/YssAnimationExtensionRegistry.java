/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.yss.extension;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.tt432.yessteveskill.yss.YssAnimationFrame;
import io.github.tt432.yessteveskill.yss.YssViewModifier;
import io.github.tt432.yessteveskill.yss.extension.YssAnimationExtensionType;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YssAnimationExtensionRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/YssExtensionRegistry");
    private final Map<String, YssAnimationExtensionType<?>> types = new LinkedHashMap();

    public YssAnimationExtensionRegistry register(YssAnimationExtensionType<?> type) {
        this.types.put(type.key(), type);
        return this;
    }

    public YssViewModifier apply(JsonObject extensions, YssAnimationFrame frame) {
        YssViewModifier.Builder builder = YssViewModifier.builder();
        for (Map.Entry<String, YssAnimationExtensionType<?>> entry : this.types.entrySet()) {
            String key = entry.getKey();
            if (!extensions.has(key)) continue;
            this.applyTyped(entry.getValue(), extensions.get(key), frame, builder);
        }
        return builder.build();
    }

    private <T> void applyTyped(YssAnimationExtensionType<T> type, JsonElement element, YssAnimationFrame frame, YssViewModifier.Builder builder) {
        try {
            T parsed = type.parse(element);
            type.apply(parsed, frame, builder);
        }
        catch (Exception e) {
            LOGGER.warn("Failed to apply yss extension '{}' for {}", new Object[]{type.key(), frame.key(), e});
        }
    }
}

