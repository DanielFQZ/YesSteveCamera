/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.client.gui.layout;

import io.github.tt432.yessteveskill.client.gui.layout.Layout;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public interface Length {
    public int resolveX(Layout var1);

    public int resolveY(Layout var1);

    public static Length px(int v) {
        return new px(v);
    }

    public static Length em(double v) {
        return new em(v);
    }

    public static Length pct(double v) {
        return new pct(v);
    }

    public static Length clamp(Length min, Length val, Length max) {
        return new clamp(min, val, max);
    }

    public record px(int v) implements Length
    {
        @Override
        public int resolveX(Layout ctx) {
            return this.v;
        }

        @Override
        public int resolveY(Layout ctx) {
            return this.v;
        }
    }

    public record em(double v) implements Length
    {
        @Override
        public int resolveX(Layout ctx) {
            return (int)Math.round(this.v * (double)ctx.fontHeight());
        }

        @Override
        public int resolveY(Layout ctx) {
            return (int)Math.round(this.v * (double)ctx.fontHeight());
        }
    }

    public record pct(double v) implements Length
    {
        @Override
        public int resolveX(Layout ctx) {
            return (int)Math.round((double)ctx.screenWidth() * this.v / 100.0);
        }

        @Override
        public int resolveY(Layout ctx) {
            return (int)Math.round((double)ctx.screenHeight() * this.v / 100.0);
        }
    }

    public record clamp(Length min, Length val, Length max) implements Length
    {
        @Override
        public int resolveX(Layout ctx) {
            int lo = this.min.resolveX(ctx);
            int v = this.val.resolveX(ctx);
            int hi = this.max.resolveX(ctx);
            return Math.max(lo, Math.min(v, hi));
        }

        @Override
        public int resolveY(Layout ctx) {
            int lo = this.min.resolveY(ctx);
            int v = this.val.resolveY(ctx);
            int hi = this.max.resolveY(ctx);
            return Math.max(lo, Math.min(v, hi));
        }
    }
}

