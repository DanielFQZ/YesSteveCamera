/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.network.chat.Component
 */
package io.github.tt432.yessteveskill.client.gui;

import io.github.tt432.yessteveskill.client.gui.EnumDisplay;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

class YssEnumButtonGroup<T extends Enum<T>>
extends AbstractWidget {
    private static final int ITEM_GAP = 2;
    private final T[] values;
    private final Consumer<T> setter;
    private T current;

    YssEnumButtonGroup(int x, int y, int w, int h, T[] values, T current, Consumer<T> setter) {
        super(x, y, w, h, (Component)Component.m_237119_());
        this.values = values;
        this.current = current;
        this.setter = setter;
    }

    protected void m_87963_(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        Minecraft mc = Minecraft.m_91087_();
        int[] itemWidths = this.computeItemWidths(mc);
        int x = this.m_252754_();
        int y = this.m_252907_();
        int h = this.m_93694_();
        int offset = 0;
        for (int i = 0; i < this.values.length; ++i) {
            int bg;
            boolean hover;
            int ix = x + offset;
            int iw = itemWidths[i];
            offset += iw + 2;
            boolean selected = this.values[i] == this.current;
            boolean bl = hover = mouseX >= ix && mouseX <= ix + iw && mouseY >= y && mouseY <= y + h;
            int n = !this.f_93623_ ? -15329770 : (selected ? -14008486 : (bg = hover ? -12566464 : -14671840));
            int border = !this.f_93623_ ? -12961222 : (selected ? -10048769 : -10461088);
            graphics.m_280509_(ix, y, ix + iw, y + h, bg);
            graphics.m_280509_(ix, y, ix + iw, y + 1, border);
            graphics.m_280509_(ix, y + h - 1, ix + iw, y + h, border);
            Font font = mc.f_91062_;
            String string = EnumDisplay.displayName(this.values[i]);
            Objects.requireNonNull(mc.f_91062_);
            graphics.m_280056_(font, string, ix + 3, y + (h - 9) / 2 + 1, !this.f_93623_ ? -9408400 : (selected ? -2232577 : -1), false);
        }
    }

    private int[] computeItemWidths(Minecraft mc) {
        int n = this.values.length;
        int[] widths = new int[n];
        int total = 0;
        for (int i = 0; i < n; ++i) {
            widths[i] = mc.f_91062_.m_92895_(EnumDisplay.displayName(this.values[i])) + 8;
            total += widths[i];
        }
        if (total + 2 * (n - 1) > this.m_5711_()) {
            int itemW = Math.max(16, (this.m_5711_() - 2 * (n - 1)) / n);
            for (int i = 0; i < n; ++i) {
                widths[i] = itemW;
            }
        }
        return widths;
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (button != 0 || !this.f_93623_ || !this.f_93624_) {
            return false;
        }
        if (mouseX < (double)this.m_252754_() || mouseX > (double)(this.m_252754_() + this.m_5711_()) || mouseY < (double)this.m_252907_() || mouseY > (double)(this.m_252907_() + this.m_93694_())) {
            return false;
        }
        int[] itemWidths = this.computeItemWidths(Minecraft.m_91087_());
        int offset = 0;
        for (int i = 0; i < this.values.length; ++i) {
            int ix = this.m_252754_() + offset;
            offset += itemWidths[i] + 2;
            if (!(mouseX >= (double)ix) || !(mouseX <= (double)(ix + itemWidths[i]))) continue;
            if (this.values[i] != this.current) {
                this.current = this.values[i];
                this.setter.accept(this.current);
            }
            this.m_7435_(Minecraft.m_91087_().m_91106_());
            return true;
        }
        return false;
    }

    protected void m_168797_(NarrationElementOutput narration) {
    }
}

