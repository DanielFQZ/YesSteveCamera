/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package io.github.tt432.yessteveskill.combat.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tt432.yessteveskill.combat.config.AnimationHitConfig;
import io.github.tt432.yessteveskill.combat.config.DamageTiming;
import io.github.tt432.yessteveskill.combat.config.EntityCombatStats;
import io.github.tt432.yessteveskill.combat.config.HitReset;
import io.github.tt432.yessteveskill.combat.config.HitSegment;
import io.github.tt432.yessteveskill.combat.config.MovementConfig;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record HitProjectConfig(float widthScale, float heightScale, Map<String, AnimationHitConfig> animations, Map<String, EntityCombatStats> entityOverrides, MovementConfig movement) {
    public static final HitProjectConfig EMPTY = new HitProjectConfig(1.0f, 1.0f, Map.of(), Map.of(), MovementConfig.DEFAULT);
    private static final Codec<HitProjectConfig> RECORD_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.optionalFieldOf("width_scale", (Object)Float.valueOf(1.0f)).forGetter(HitProjectConfig::widthScale), (App)Codec.FLOAT.optionalFieldOf("height_scale", (Object)Float.valueOf(1.0f)).forGetter(HitProjectConfig::heightScale), (App)Codec.unboundedMap((Codec)Codec.STRING, AnimationHitConfig.CODEC).fieldOf("animation").forGetter(HitProjectConfig::animations), (App)Codec.unboundedMap((Codec)Codec.STRING, EntityCombatStats.CODEC).optionalFieldOf("entity_overrides", (Object)Map.of()).forGetter(HitProjectConfig::entityOverrides), (App)MovementConfig.CODEC.optionalFieldOf("movement", (Object)MovementConfig.DEFAULT).forGetter(HitProjectConfig::movement)).apply((Applicative)instance, HitProjectConfig::new));

    public HitProjectConfig {
        if (!Float.isFinite(widthScale) || widthScale <= 0.0f) {
            widthScale = 1.0f;
        }
        if (!Float.isFinite(heightScale) || heightScale <= 0.0f) {
            heightScale = 1.0f;
        }
        animations = animations == null ? Map.of() : Map.copyOf(animations);
        entityOverrides = entityOverrides == null ? Map.of() : Map.copyOf(entityOverrides);
        movement = movement == null ? MovementConfig.DEFAULT : movement;
    }

    public JsonObject toJsonObject() {
        return ((JsonElement)RECORD_CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)this).result().orElseThrow(() -> new IllegalStateException("Failed to encode HitProjectConfig"))).getAsJsonObject();
    }

    public static HitProjectConfig parse(JsonObject root) {
        if (root == null) {
            return EMPTY;
        }
        JsonElement animationElement = root.get("animation");
        if (animationElement instanceof JsonArray) {
            JsonArray array = (JsonArray)animationElement;
            return HitProjectConfig.parseLegacy(root, array);
        }
        return RECORD_CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)root).result().orElseGet(() -> {
            float widthScale = HitProjectConfig.readPositiveFloat(root, "width_scale", 1.0f);
            float heightScale = HitProjectConfig.readPositiveFloat(root, "height_scale", 1.0f);
            return new HitProjectConfig(widthScale, heightScale, Map.of(), Map.of(), MovementConfig.DEFAULT);
        });
    }

    private static HitProjectConfig parseLegacy(JsonObject root, JsonArray animationArray) {
        float widthScale = HitProjectConfig.readPositiveFloat(root, "width_scale", 1.0f);
        float heightScale = HitProjectConfig.readPositiveFloat(root, "height_scale", 1.0f);
        LinkedHashSet<String> bones = HitProjectConfig.readStringSet(root.get("bone"));
        HitSegment legacySegment = new HitSegment("", Optional.empty(), List.copyOf(bones), HitReset.ONCE, 0.0f, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), DamageTiming.INSTANT);
        AnimationHitConfig legacyAnim = new AnimationHitConfig(List.of((Object)((Object)legacySegment)), List.of(), Optional.empty(), false);
        LinkedHashMap<String, AnimationHitConfig> animations = new LinkedHashMap<String, AnimationHitConfig>();
        for (JsonElement entry : animationArray) {
            String name;
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString() || (name = entry.getAsString().trim()).isBlank()) continue;
            animations.put(name, legacyAnim);
        }
        return new HitProjectConfig(widthScale, heightScale, animations, Map.of(), MovementConfig.DEFAULT);
    }

    private static float readPositiveFloat(JsonObject root, String key, float defaultValue) {
        JsonElement element = root.get(key);
        if (element == null || !element.isJsonPrimitive()) {
            return defaultValue;
        }
        try {
            float value = element.getAsFloat();
            return Float.isFinite(value) && value > 0.0f ? value : defaultValue;
        }
        catch (RuntimeException ignored) {
            return defaultValue;
        }
    }

    private static LinkedHashSet<String> readStringSet(JsonElement element) {
        LinkedHashSet<String> values = new LinkedHashSet<String>();
        if (!(element instanceof JsonArray)) {
            return values;
        }
        JsonArray array = (JsonArray)element;
        for (JsonElement entry : array) {
            String value;
            if (!entry.isJsonPrimitive() || !entry.getAsJsonPrimitive().isString() || (value = entry.getAsString().trim()).isBlank()) continue;
            values.add(value);
        }
        return values;
    }
}

