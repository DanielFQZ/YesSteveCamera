/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package io.github.tt432.yessteveskill.yss.extension;

import com.google.gson.JsonElement;
import io.github.tt432.yessteveskill.yss.YssAnimationFrame;
import io.github.tt432.yessteveskill.yss.YssViewModifier;

public interface YssAnimationExtensionType<T> {
    public String key();

    public T parse(JsonElement var1);

    public void apply(T var1, YssAnimationFrame var2, YssViewModifier.Builder var3);
}

