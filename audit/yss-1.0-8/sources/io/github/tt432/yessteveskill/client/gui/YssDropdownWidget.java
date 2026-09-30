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

class YssDropdownWidget<T extends Enum<T>>
extends AbstractWidget {
    private static final int ITEM_H = 12;
    private final T[] values;
    private final Consumer<T> setter;
    private T current;
    private boolean expanded = false;

    YssDropdownWidget(int x, int y, int w, int h, T[] values, T current, Consumer<T> setter) {
        super(x, y, w, h, (Component)Component.m_237113_((String)EnumDisplay.displayName(current)));
        this.values = values;
        this.current = current;
        this.setter = setter;
    }

    boolean isExpanded() {
        return this.expanded;
    }

    protected void m_87963_(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        Minecraft mc = Minecraft.m_91087_();
        int x = this.m_252754_();
        int y = this.m_252907_();
        int w = this.m_5711_();
        int h = this.m_93694_();
        int bg = !this.f_93623_ ? -15329770 : (this.m_274382_() ? -12566464 : -14671840);
        graphics.m_280509_(x, y, x + w, y + h, bg);
        graphics.m_280509_(x, y, x + w, y + 1, !this.f_93623_ ? -12961222 : (this.expanded ? -5592321 : -10461088));
        graphics.m_280509_(x, y + h - 1, x + w, y + h, -10461088);
        Font font = mc.f_91062_;
        String string = EnumDisplay.displayName(this.current);
        Objects.requireNonNull(mc.f_91062_);
        graphics.m_280056_(font, string, x + 4, y + (h - 9) / 2 + 1, !this.f_93623_ ? -9408400 : -1, false);
        Font font2 = mc.f_91062_;
        Objects.requireNonNull(mc.f_91062_);
        graphics.m_280056_(font2, "V", x + w - 10, y + (h - 9) / 2 + 1, !this.f_93623_ ? -11513776 : -5592406, false);
    }

    void renderOverlay(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!this.expanded) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        int total = this.values.length * 12;
        int top = this.getOverlayTop(total);
        int left = this.m_252754_();
        int w = this.m_5711_();
        graphics.m_280509_(left, top, left + w, top + total, -13619152);
        graphics.m_280509_(left, top, left + w, top + 1, -8355712);
        graphics.m_280509_(left, top + total - 1, left + w, top + total, -8355712);
        for (int i = 0; i < this.values.length; ++i) {
            boolean hover;
            int iy = top + i * 12;
            boolean bl = hover = mouseX >= left && mouseX <= left + w && mouseY >= iy && mouseY <= iy + 12;
            if (hover) {
                graphics.m_280509_(left, iy, left + w, iy + 12, -10461088);
            }
            int color = this.values[i] == this.current ? -86 : -1;
            graphics.m_280056_(mc.f_91062_, EnumDisplay.displayName(this.values[i]), left + 4, iy + 1, color, false);
        }
    }

    private int getOverlayTop(int total) {
        int h;
        Minecraft mc = Minecraft.m_91087_();
        int screenH = mc.f_91080_ != null ? mc.f_91080_.f_96544_ : mc.m_91268_().m_85444_();
        int y = this.m_252907_();
        if (y + (h = this.m_93694_()) + total <= screenH) {
            return y + h;
        }
        return y - total;
    }

    boolean handleOverlayClick(double mouseX, double mouseY) {
        int idx;
        if (!this.expanded) {
            return false;
        }
        this.expanded = false;
        int total = this.values.length * 12;
        int top = this.getOverlayTop(total);
        int left = this.m_252754_();
        int w = this.m_5711_();
        if (mouseX >= (double)left && mouseX <= (double)(left + w) && mouseY >= (double)top && mouseY <= (double)(top + total) && (idx = (int)((mouseY - (double)top) / 12.0)) >= 0 && idx < this.values.length) {
            this.current = this.values[idx];
            this.setter.accept(this.current);
        }
        return true;
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (button != 0 || !this.f_93623_ || !this.f_93624_) {
            return false;
        }
        int x = this.m_252754_();
        int y = this.m_252907_();
        int w = this.m_5711_();
        int h = this.m_93694_();
        if (mouseX >= (double)x && mouseX <= (double)(x + w) && mouseY >= (double)y && mouseY <= (double)(y + h)) {
            this.expanded = !this.expanded;
            this.m_7435_(Minecraft.m_91087_().m_91106_());
            return true;
        }
        return false;
    }

    protected void m_168797_(NarrationElementOutput narration) {
    }
}

