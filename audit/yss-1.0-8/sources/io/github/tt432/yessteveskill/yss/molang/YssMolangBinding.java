/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.geckolib3.core.molang.binding.ContextBinding
 *  com.elfmcys.ysm.molang.runtime.ExecutionContext
 *  com.elfmcys.ysm.molang.runtime.Function
 *  com.elfmcys.ysm.molang.runtime.Function$ArgumentCollection
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.yss.molang;

import com.elfmcys.ysm.geckolib3.core.molang.binding.ContextBinding;
import com.elfmcys.ysm.molang.runtime.ExecutionContext;
import com.elfmcys.ysm.molang.runtime.Function;
import io.github.tt432.yessteveskill.client.input.YssKeyStateTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class YssMolangBinding
extends ContextBinding {
    public YssMolangBinding() {
        this.function("get_key", new KeyFunction(KeyQuery.DURATION));
        this.function("is_key_down", new KeyFunction(KeyQuery.DOWN_EDGE));
        this.function("is_key_up", new KeyFunction(KeyQuery.UP_EDGE));
    }

    private static final class KeyFunction
    implements Function {
        private final KeyQuery query;

        private KeyFunction(KeyQuery query) {
            this.query = query;
        }

        public boolean validateArgumentSize(int size) {
            return size == 1;
        }

        @Nullable
        public Object evaluate(@NotNull ExecutionContext<?> context, @NotNull Function.ArgumentCollection arguments) {
            int key = arguments.getAsInt(context, 0);
            return switch (this.query) {
                default -> throw new IncompatibleClassChangeError();
                case KeyQuery.DURATION -> Float.valueOf(YssKeyStateTracker.getKeyDuration(key));
                case KeyQuery.DOWN_EDGE -> Float.valueOf(YssKeyStateTracker.isKeyDownEdge(key) ? 1.0f : 0.0f);
                case KeyQuery.UP_EDGE -> Float.valueOf(YssKeyStateTracker.isKeyUpEdge(key) ? 1.0f : 0.0f);
            };
        }
    }

    private static enum KeyQuery {
        DURATION,
        DOWN_EDGE,
        UP_EDGE;

    }
}

