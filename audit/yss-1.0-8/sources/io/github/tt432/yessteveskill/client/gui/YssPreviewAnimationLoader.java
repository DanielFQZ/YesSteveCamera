/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.format.parser.AnimationBuilder
 *  com.elfmcys.ysm.format.parser.pojo.animation.AnimationFile
 *  com.elfmcys.ysm.geckolib3.core.builder.Animation
 *  com.elfmcys.ysm.model.resource.client.render.AnimationProtoMapper
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.Animation
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.AnimationFile
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package io.github.tt432.yessteveskill.client.gui;

import com.elfmcys.ysm.format.parser.AnimationBuilder;
import com.elfmcys.ysm.format.parser.pojo.animation.AnimationFile;
import com.elfmcys.ysm.model.resource.client.render.AnimationProtoMapper;
import com.elfmcys.ysm.proto.mixel.asset.model.data.Animation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;

public final class YssPreviewAnimationLoader {
    private YssPreviewAnimationLoader() {
    }

    public static Map<String, com.elfmcys.ysm.geckolib3.core.builder.Animation> load(JsonObject root) {
        AnimationFile parsed = (AnimationFile)AnimationFile.createGson((boolean)false).fromJson((JsonElement)root, AnimationFile.class);
        if (parsed == null) {
            return Map.of();
        }
        com.elfmcys.ysm.proto.mixel.asset.model.data.AnimationFile proto = AnimationBuilder.build((AnimationFile)parsed);
        LinkedHashMap<String, com.elfmcys.ysm.geckolib3.core.builder.Animation> result = new LinkedHashMap<String, com.elfmcys.ysm.geckolib3.core.builder.Animation>();
        if (!proto.animations().isEmpty()) {
            for (Animation animation : proto.animations()) {
                if (animation.nameNullOrBlank()) continue;
                result.put(animation.name(), AnimationProtoMapper.animation((Animation)animation));
            }
        }
        return Map.copyOf(result);
    }
}

