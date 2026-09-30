/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package io.github.tt432.yessteveskill.editor.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.tt432.yessteveskill.combat.config.AnimationHitConfig;
import io.github.tt432.yessteveskill.combat.config.EntityCombatStats;
import io.github.tt432.yessteveskill.combat.config.HitProjectConfig;
import io.github.tt432.yessteveskill.combat.config.MovementConfig;
import io.github.tt432.yessteveskill.editor.data.MutableAnimationHitConfig;
import io.github.tt432.yessteveskill.editor.data.MutableEntityCombatStats;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class HitProjectEditingState {
    private static final String HIT_FILE_NAME = "hit.json";
    private float widthScale;
    private float heightScale;
    private Map<String, MutableAnimationHitConfig> animations;
    private Map<String, MutableEntityCombatStats> entityOverrides;
    private MovementConfig movement = MovementConfig.DEFAULT;

    public HitProjectEditingState() {
        this(1.0f, 1.0f, new LinkedHashMap<String, MutableAnimationHitConfig>(), new LinkedHashMap<String, MutableEntityCombatStats>());
    }

    public HitProjectEditingState(float widthScale, float heightScale, Map<String, MutableAnimationHitConfig> animations, Map<String, MutableEntityCombatStats> entityOverrides) {
        this.widthScale = widthScale;
        this.heightScale = heightScale;
        this.animations = animations;
        this.entityOverrides = entityOverrides;
    }

    public static HitProjectEditingState from(HitProjectConfig config) {
        LinkedHashMap<String, MutableAnimationHitConfig> animations = new LinkedHashMap<String, MutableAnimationHitConfig>();
        for (Map.Entry<String, AnimationHitConfig> entry : config.animations().entrySet()) {
            animations.put(entry.getKey(), MutableAnimationHitConfig.fromRecord(entry.getValue()));
        }
        LinkedHashMap<String, MutableEntityCombatStats> overrides = new LinkedHashMap<String, MutableEntityCombatStats>();
        for (Map.Entry<String, EntityCombatStats> entry : config.entityOverrides().entrySet()) {
            overrides.put(entry.getKey(), MutableEntityCombatStats.fromRecord(entry.getValue()));
        }
        HitProjectEditingState hitProjectEditingState = new HitProjectEditingState(config.widthScale(), config.heightScale(), animations, overrides);
        hitProjectEditingState.movement = config.movement();
        return hitProjectEditingState;
    }

    public HitProjectConfig toConfig() {
        LinkedHashMap<String, AnimationHitConfig> animations = new LinkedHashMap<String, AnimationHitConfig>();
        for (Map.Entry<String, MutableAnimationHitConfig> entry : this.animations.entrySet()) {
            animations.put(entry.getKey(), entry.getValue().toRecord());
        }
        LinkedHashMap<String, EntityCombatStats> overrides = new LinkedHashMap<String, EntityCombatStats>();
        for (Map.Entry<String, MutableEntityCombatStats> entry : this.entityOverrides.entrySet()) {
            overrides.put(entry.getKey(), entry.getValue().toRecord());
        }
        return new HitProjectConfig(this.widthScale, this.heightScale, animations, overrides, this.movement == null ? MovementConfig.DEFAULT : this.movement);
    }

    public static HitProjectEditingState load(Path projectDir) {
        Path hitPath = projectDir.resolve(HIT_FILE_NAME);
        if (!Files.exists(hitPath, new LinkOption[0])) {
            return new HitProjectEditingState();
        }
        try {
            String content = Files.readString((Path)hitPath, (Charset)StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString((String)content).getAsJsonObject();
            return HitProjectEditingState.from(HitProjectConfig.parse(root));
        }
        catch (Exception e) {
            return new HitProjectEditingState();
        }
    }

    public void save(Path projectDir) throws IOException {
        HitProjectConfig config = this.toConfig();
        JsonObject json = config.toJsonObject();
        Files.writeString((Path)projectDir.resolve(HIT_FILE_NAME), (CharSequence)json.toString(), (Charset)StandardCharsets.UTF_8, (OpenOption[])new OpenOption[0]);
    }

    public float getWidthScale() {
        return this.widthScale;
    }

    public void setWidthScale(float widthScale) {
        this.widthScale = widthScale;
    }

    public float getHeightScale() {
        return this.heightScale;
    }

    public void setHeightScale(float heightScale) {
        this.heightScale = heightScale;
    }

    public Map<String, MutableAnimationHitConfig> getAnimations() {
        return this.animations;
    }

    public void setAnimations(Map<String, MutableAnimationHitConfig> animations) {
        this.animations = animations;
    }

    public Map<String, MutableEntityCombatStats> getEntityOverrides() {
        return this.entityOverrides;
    }

    public void setEntityOverrides(Map<String, MutableEntityCombatStats> entityOverrides) {
        this.entityOverrides = entityOverrides;
    }

    public MovementConfig getMovement() {
        return this.movement == null ? MovementConfig.DEFAULT : this.movement;
    }

    public void setMovement(MovementConfig movement) {
        this.movement = movement == null ? MovementConfig.DEFAULT : movement;
    }
}

