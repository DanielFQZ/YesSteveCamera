/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package io.github.tt432.yessteveskill.combat.config;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HitReset(Type type, float interval) {
    public static final HitReset ONCE = new HitReset(Type.ONCE, 0.0f);
    private static final Codec<HitReset> OBJECT_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Type.CODEC.fieldOf("type").forGetter(HitReset::type), (App)Codec.FLOAT.optionalFieldOf("interval", (Object)Float.valueOf(0.0f)).forGetter(HitReset::interval)).apply((Applicative)instance, HitReset::new));
    public static final Codec<HitReset> CODEC = Codec.either((Codec)Codec.STRING, OBJECT_CODEC).flatXmap(either -> (DataResult)either.map(HitReset::fromStringDataResult, DataResult::success), hr -> hr.type == Type.INTERVAL ? DataResult.success((Object)Either.right((Object)hr)) : DataResult.success((Object)Either.left((Object)hr.type.key)));

    public HitReset(Type type) {
        this(type, 0.0f);
    }

    private static DataResult<HitReset> fromStringDataResult(String name) {
        for (Type value : Type.values()) {
            if (!value.key.equals(name)) continue;
            return DataResult.success((Object)((Object)new HitReset(value, 0.0f)));
        }
        return DataResult.error(() -> "Unknown hit_reset: " + name);
    }

    public static enum Type {
        ON_EXIT("on_exit"),
        ONCE("once"),
        INTERVAL("interval");

        private final String key;
        public static final Codec<Type> CODEC;

        private Type(String key) {
            this.key = key;
        }

        public String key() {
            return this.key;
        }

        static {
            CODEC = Codec.STRING.comapFlatMap(name -> {
                for (Type value : Type.values()) {
                    if (!value.key.equals(name)) continue;
                    return DataResult.success((Object)((Object)value));
                }
                return DataResult.error(() -> "Unknown hit_reset type: " + name);
            }, Type::key);
        }
    }
}

