/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.client.gui.layout;

import io.github.tt432.yessteveskill.client.gui.layout.Layout;
import io.github.tt432.yessteveskill.client.gui.layout.Length;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import org.jetbrains.annotations.Nullable;

public final class VCol {
    private int x;
    private int y;
    private final int width;
    private final int spacing;
    @Nullable
    private final Layout ctx;

    public VCol(int x, int y, int width, int spacing) {
        this(null, x, y, width, spacing);
    }

    public VCol(Rect origin, int spacing) {
        this(null, origin.x(), origin.y(), origin.width(), spacing);
    }

    VCol(Layout ctx, Length x, Length y, Length width, Length spacing) {
        this(ctx, x.resolveX(ctx), y.resolveY(ctx), width.resolveX(ctx), spacing.resolveY(ctx));
    }

    VCol(Layout ctx, Rect origin, Length spacing) {
        this(ctx, origin.x(), origin.y(), origin.width(), spacing.resolveY(ctx));
    }

    private VCol(@Nullable Layout ctx, int x, int y, int width, int spacing) {
        this.ctx = ctx;
        this.x = x;
        this.y = y;
        this.width = width;
        this.spacing = spacing;
    }

    public Rect next(int height) {
        Rect rect = new Rect(this.x, this.y, this.width, height);
        this.y += height + this.spacing;
        return rect;
    }

    public Rect next(Length height) {
        this.requireCtx();
        return this.next(height.resolveY(this.ctx));
    }

    public Rect remaining(int bottomY) {
        return new Rect(this.x, this.y, this.width, Math.max(0, bottomY - this.y));
    }

    public Rect remaining(Length bottomY) {
        this.requireCtx();
        return this.remaining(bottomY.resolveY(this.ctx));
    }

    public Rect remainingToBottom(Length fromBottom) {
        this.requireCtx();
        return this.remaining(this.ctx.screenHeight() - fromBottom.resolveY(this.ctx));
    }

    public int cursorY() {
        return this.y;
    }

    private void requireCtx() {
        if (this.ctx == null) {
            throw new IllegalStateException("Length API requires a Layout context; use Layout.vcol() to construct");
        }
    }
}

