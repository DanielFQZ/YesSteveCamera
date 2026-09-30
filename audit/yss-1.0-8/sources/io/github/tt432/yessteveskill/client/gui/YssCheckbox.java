/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.network.chat.Component
 */
package io.github.tt432.yessteveskill.client.gui;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

class YssCheckbox
extends AbstractWidget {
    private final Consumer<Boolean> onChanged;
    private boolean selected;

    YssCheckbox(int x, int y, int w, int h, boolean selected, Consumer<Boolean> onChanged) {
        super(x, y, w, h, (Component)Component.m_237119_());
        this.selected = selected;
        this.onChanged = onChanged;
    }

    protected void m_87963_(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        int box = Math.max(6, Math.min(this.m_93694_() - 4, 12));
        int bx = this.m_252754_();
        int by = this.m_252907_() + (this.m_93694_() - box) / 2;
        int bg = !this.f_93623_ ? -15329770 : (this.m_274382_() ? -12566464 : -14671840);
        int border = !this.f_93623_ ? -12961222 : -10048769;
        graphics.m_280509_(bx, by, bx + box, by + box, bg);
        graphics.m_280509_(bx, by, bx + box, by + 1, border);
        graphics.m_280509_(bx, by + box - 1, bx + box, by + box, border);
        graphics.m_280509_(bx, by, bx + 1, by + box, border);
        graphics.m_280509_(bx + box - 1, by, bx + box, by + box, border);
        if (this.selected) {
            graphics.m_280509_(bx + 3, by + 3, bx + box - 3, by + box - 3, this.f_93623_ ? -10048769 : -11513776);
        }
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (button != 0 || !this.f_93623_ || !this.f_93624_) {
            return false;
        }
        if (mouseX >= (double)this.m_252754_() && mouseX <= (double)(this.m_252754_() + this.m_5711_()) && mouseY >= (double)this.m_252907_() && mouseY <= (double)(this.m_252907_() + this.m_93694_())) {
            this.selected = !this.selected;
            this.onChanged.accept(this.selected);
            this.m_7435_(Minecraft.m_91087_().m_91106_());
            return true;
        }
        return false;
    }

    protected void m_168797_(NarrationElementOutput narration) {
    }
}

