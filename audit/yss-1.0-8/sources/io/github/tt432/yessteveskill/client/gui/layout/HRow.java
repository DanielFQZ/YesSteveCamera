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

public final class HRow {
    private int x;
    private final int y;
    private final int height;
    private final int spacing;
    @Nullable
    private final Layout ctx;

    public HRow(int x, int y, int height, int spacing) {
        this(null, x, y, height, spacing);
    }

    public HRow(Rect origin, int spacing) {
        this(null, origin.x(), origin.y(), origin.height(), spacing);
    }

    HRow(Layout ctx, Length x, Length y, Length height, Length spacing) {
        this(ctx, x.resolveX(ctx), y.resolveY(ctx), height.resolveY(ctx), spacing.resolveX(ctx));
    }

    HRow(Layout ctx, Rect origin, Length spacing) {
        this(ctx, origin.x(), origin.y(), origin.height(), spacing.resolveX(ctx));
    }

    private HRow(@Nullable Layout ctx, int x, int y, int height, int spacing) {
        this.ctx = ctx;
        this.x = x;
        this.y = y;
        this.height = height;
        this.spacing = spacing;
    }

    public Rect next(int width) {
        Rect rect = new Rect(this.x, this.y, width, this.height);
        this.x += width + this.spacing;
        return rect;
    }

    public Rect next(Length width) {
        this.requireCtx();
        return this.next(width.resolveX(this.ctx));
    }

    public Rect remaining(int rightX) {
        return new Rect(this.x, this.y, Math.max(0, rightX - this.x), this.height);
    }

    public Rect remaining(Length rightX) {
        this.requireCtx();
        return this.remaining(rightX.resolveX(this.ctx));
    }

    public Rect remainingToRight(Length fromRight) {
        this.requireCtx();
        return this.remaining(this.ctx.screenWidth() - fromRight.resolveX(this.ctx));
    }

    public int cursorX() {
        return this.x;
    }

    private void requireCtx() {
        if (this.ctx == null) {
            throw new IllegalStateException("Length API requires a Layout context; use Layout.hrow() to construct");
        }
    }
}

