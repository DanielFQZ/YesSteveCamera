/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.screens.Screen
 */
package io.github.tt432.yessteveskill.client.gui.layout;

import io.github.tt432.yessteveskill.client.gui.layout.HRow;
import io.github.tt432.yessteveskill.client.gui.layout.Length;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import io.github.tt432.yessteveskill.client.gui.layout.VCol;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

public final class Layout {
    private final int screenWidth;
    private final int screenHeight;
    private final int fontHeight;

    public Layout(int screenWidth, int screenHeight, int fontHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.fontHeight = fontHeight;
    }

    public static Layout of(Screen screen, Font font) {
        int n = screen.f_96543_;
        int n2 = screen.f_96544_;
        Objects.requireNonNull(font);
        return new Layout(n, n2, 9);
    }

    public int screenWidth() {
        return this.screenWidth;
    }

    public int screenHeight() {
        return this.screenHeight;
    }

    public int fontHeight() {
        return this.fontHeight;
    }

    public int resolveX(Length l) {
        return l.resolveX(this);
    }

    public int resolveY(Length l) {
        return l.resolveY(this);
    }

    public static int center(int total, int child) {
        return (total - child) / 2;
    }

    public int centerX(int child) {
        return (this.screenWidth - child) / 2;
    }

    public int centerY(int child) {
        return (this.screenHeight - child) / 2;
    }

    public int fromBottom(Length fromBottom) {
        return this.screenHeight - fromBottom.resolveY(this);
    }

    public int fromRight(Length fromRight) {
        return this.screenWidth - fromRight.resolveX(this);
    }

    public VCol vcol(Length x, Length y, Length width, Length spacing) {
        return new VCol(this, x, y, width, spacing);
    }

    public HRow hrow(Length x, Length y, Length height, Length spacing) {
        return new HRow(this, x, y, height, spacing);
    }

    public VCol vcol(Rect origin, Length spacing) {
        return new VCol(this, origin, spacing);
    }

    public HRow hrow(Rect origin, Length spacing) {
        return new HRow(this, origin, spacing);
    }
}

