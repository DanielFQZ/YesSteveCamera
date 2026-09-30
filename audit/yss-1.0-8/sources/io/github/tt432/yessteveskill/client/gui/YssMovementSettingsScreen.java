/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package io.github.tt432.yessteveskill.client.gui;

import io.github.tt432.yessteveskill.client.gui.YssHitEditorScreen;
import io.github.tt432.yessteveskill.client.gui.layout.HRow;
import io.github.tt432.yessteveskill.client.gui.layout.Layout;
import io.github.tt432.yessteveskill.client.gui.layout.Length;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import io.github.tt432.yessteveskill.client.gui.layout.VCol;
import io.github.tt432.yessteveskill.combat.config.MovementConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class YssMovementSettingsScreen
extends Screen {
    private final YssHitEditorScreen parent;
    private final List<RowWidget> rows = new ArrayList<RowWidget>();
    private Layout layout;
    private Rect titleRect;
    private Rect hintRect;
    private Rect backBtnRect;

    public YssMovementSettingsScreen(YssHitEditorScreen parent) {
        super((Component)Component.m_237113_((String)"\u5168\u5c40\u79fb\u52a8\u8bbe\u7f6e"));
        this.parent = parent;
    }

    protected void m_7856_() {
        this.rows.clear();
        this.layout = Layout.of(this, this.f_96547_);
        Length margin = Length.em(1.5);
        Length formRowH = Length.em(2.5);
        Length labelW = Length.em(12.0);
        Length fieldW = Length.clamp(Length.px(80), Length.em(12.0), Length.px(140));
        Length btnW = Length.clamp(Length.px(50), Length.em(6.5), Length.px(80));
        Length btnH = Length.em(2.25);
        int formWidth = this.layout.resolveX(labelW) + this.layout.resolveX(fieldW);
        int formLeft = (this.f_96543_ - formWidth) / 2;
        this.titleRect = this.layout.vcol(margin, Length.em(2.0), Length.px(this.f_96543_ - 2 * this.layout.resolveX(margin)), Length.px(0)).next(Length.em(1.0));
        VCol formCol = this.layout.vcol(Length.px(formLeft), Length.em(4.0), Length.px(formWidth), Length.px(0));
        MovementConfig movement = this.parent.editingState().getMovement();
        this.addRow(formCol, formRowH, labelW, fieldW, "\u884c\u8d70\u901f\u5ea6", "\u884c\u8d70\u901f\u5ea6\u500d\u7387\uff08\u57fa\u51c6 1.0\uff09", movement.walk(), v -> this.update(m -> YssMovementSettingsScreen.withWalk(m, v.floatValue())));
        this.addRow(formCol, formRowH, labelW, fieldW, "\u5954\u8dd1\u901f\u5ea6", "\u75be\u8dd1\u901f\u5ea6\u500d\u7387\uff08\u53e0\u5728\u539f\u7248 1.3 \u75be\u8dd1\u52a0\u6210\u4e0a\uff09", movement.run(), v -> this.update(m -> YssMovementSettingsScreen.withRun(m, v.floatValue())));
        this.addRow(formCol, formRowH, labelW, fieldW, "\u6f5c\u884c\u901f\u5ea6", "\u6f5c\u884c\u901f\u5ea6\u500d\u7387\uff08\u53e0\u5728\u539f\u7248 0.3 \u6f5c\u884c\u7cfb\u6570\u4e0a\uff09", movement.sneak(), v -> this.update(m -> YssMovementSettingsScreen.withSneak(m, v.floatValue())));
        this.addRow(formCol, formRowH, labelW, fieldW, "\u6e38\u6cf3\u901f\u5ea6", "\u6c34\u4e2d\u79fb\u52a8\u901f\u5ea6\u500d\u7387", movement.swim(), v -> this.update(m -> YssMovementSettingsScreen.withSwim(m, v.floatValue())));
        this.addRow(formCol, formRowH, labelW, fieldW, "\u722c\u68af\u901f\u5ea6", "\u68af\u5b50/\u85e4\u8513\u6500\u722c\u901f\u5ea6\u500d\u7387", movement.climb(), v -> this.update(m -> YssMovementSettingsScreen.withClimb(m, v.floatValue())));
        this.addRow(formCol, formRowH, labelW, fieldW, "\u722c\u884c\u901f\u5ea6", "\u5730\u9762\u530d\u5310\uff08\u6e38\u6cf3\u59ff\u6001\uff09\u901f\u5ea6\u500d\u7387", movement.crawl(), v -> this.update(m -> YssMovementSettingsScreen.withCrawl(m, v.floatValue())));
        this.addRow(formCol, formRowH, labelW, fieldW, "\u8df3\u8dc3\u9ad8\u5ea6", "\u8df3\u8dc3\u9ad8\u5ea6\u500d\u7387\uff082.0=\u8df3\u4e24\u500d\u9ad8\uff09", movement.jumpHeight(), v -> this.update(m -> YssMovementSettingsScreen.withJumpHeight(m, v.floatValue())));
        this.hintRect = formCol.next(Length.em(2.0));
        int backX = this.layout.fromRight(margin) - this.layout.resolveX(btnW);
        int backY = this.layout.fromBottom(Length.em(1.0)) - this.layout.resolveY(btnH);
        this.backBtnRect = new Rect(backX, backY, this.layout.resolveX(btnW), this.layout.resolveY(btnH));
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u8fd4\u56de"), b -> this.back()).m_252987_(this.backBtnRect.x(), this.backBtnRect.y(), this.backBtnRect.width(), this.backBtnRect.height()).m_253136_());
    }

    private void addRow(VCol formCol, Length rowH, Length labelW, Length fieldW, String label, String tooltip, float current, Consumer<Float> setter) {
        Rect row = formCol.next(rowH);
        HRow fields = this.layout.hrow(row, Length.px(0));
        Rect labelRect = fields.next(labelW);
        Rect fieldRect = fields.next(fieldW);
        EditBox box = new EditBox(this.f_96547_, fieldRect.x(), fieldRect.y(), fieldRect.width(), fieldRect.height(), (Component)Component.m_237119_());
        box.m_94199_(16);
        box.m_94144_(String.valueOf(current));
        box.m_94153_(s -> s.isEmpty() || s.equals(".") || YssMovementSettingsScreen.isNonNegativeFloat(s));
        box.m_94151_(s -> {
            try {
                setter.accept(Float.valueOf(Float.parseFloat(s)));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        });
        this.m_142416_((GuiEventListener)box);
        this.rows.add(new RowWidget(labelRect, label, tooltip));
    }

    private void update(Function<MovementConfig, MovementConfig> updater) {
        this.parent.editingState().setMovement(updater.apply(this.parent.editingState().getMovement()));
        this.parent.markDirty();
    }

    private void back() {
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)this.parent);
        }
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
        graphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, this.titleRect.y(), -1);
        for (RowWidget row : this.rows) {
            String string = row.label;
            int n = row.rect.x();
            int n2 = row.rect.y();
            int n3 = row.rect.height();
            Objects.requireNonNull(this.f_96547_);
            graphics.m_280056_(this.f_96547_, string, n, n2 + (n3 - 9) / 2 + 1, -2039584, false);
            if (mouseX < row.rect.x() || mouseX > row.rect.right() || mouseY < row.rect.y() || mouseY > row.rect.bottom()) continue;
            graphics.m_280557_(this.f_96547_, (Component)Component.m_237113_((String)row.tooltip), mouseX, mouseY);
        }
        graphics.m_280056_(this.f_96547_, "\u5168\u90e8\u7f3a\u7701 1.0 = \u539f\u7248\u624b\u611f\uff1b\u7acb\u5373\u751f\u6548\uff0c\u968f\u4e3b\u754c\u9762\u300c\u4fdd\u5b58\u300d\u5199\u5165 hit.json", this.hintRect.x(), this.hintRect.y(), -7431768, false);
    }

    private static boolean isNonNegativeFloat(String s) {
        try {
            return Float.parseFloat(s) >= 0.0f;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    private static MovementConfig withWalk(MovementConfig m, float v) {
        return new MovementConfig(v, m.run(), m.sneak(), m.swim(), m.climb(), m.crawl(), m.jumpHeight());
    }

    private static MovementConfig withRun(MovementConfig m, float v) {
        return new MovementConfig(m.walk(), v, m.sneak(), m.swim(), m.climb(), m.crawl(), m.jumpHeight());
    }

    private static MovementConfig withSneak(MovementConfig m, float v) {
        return new MovementConfig(m.walk(), m.run(), v, m.swim(), m.climb(), m.crawl(), m.jumpHeight());
    }

    private static MovementConfig withSwim(MovementConfig m, float v) {
        return new MovementConfig(m.walk(), m.run(), m.sneak(), v, m.climb(), m.crawl(), m.jumpHeight());
    }

    private static MovementConfig withClimb(MovementConfig m, float v) {
        return new MovementConfig(m.walk(), m.run(), m.sneak(), m.swim(), v, m.crawl(), m.jumpHeight());
    }

    private static MovementConfig withCrawl(MovementConfig m, float v) {
        return new MovementConfig(m.walk(), m.run(), m.sneak(), m.swim(), m.climb(), v, m.jumpHeight());
    }

    private static MovementConfig withJumpHeight(MovementConfig m, float v) {
        return new MovementConfig(m.walk(), m.run(), m.sneak(), m.swim(), m.climb(), m.crawl(), v);
    }

    private record RowWidget(Rect rect, String label, String tooltip) {
    }
}

