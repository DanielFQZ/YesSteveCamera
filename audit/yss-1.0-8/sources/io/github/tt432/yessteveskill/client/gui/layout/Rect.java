/*
 * Decompiled with CFR 0.152.
 */
package io.github.tt432.yessteveskill.client.gui.layout;

public record Rect(int x, int y, int width, int height) {
    public int right() {
        return this.x + this.width;
    }

    public int bottom() {
        return this.y + this.height;
    }
}

