/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.ObjectSelectionList
 *  net.minecraft.client.gui.components.ObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.client.gui;

import io.github.tt432.yessteveskill.client.gui.YssHitEditorScreen;
import io.github.tt432.yessteveskill.client.gui.layout.HRow;
import io.github.tt432.yessteveskill.client.gui.layout.Layout;
import io.github.tt432.yessteveskill.client.gui.layout.Length;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import io.github.tt432.yessteveskill.client.gui.layout.VCol;
import io.github.tt432.yessteveskill.editor.data.MutableEntityCombatStats;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

public class YssEntityOverridesScreen
extends Screen {
    private final YssHitEditorScreen parent;
    private OverrideList overrideList;
    @Nullable
    private String selectedEntityId;
    private EditBox newIdBox;
    private Button deleteButton;
    private boolean pendingDeleteOverride = false;
    @Nullable
    private Component statusMessage;
    private int statusColor = -1;
    private final List<AbstractWidget> formWidgets = new ArrayList<AbstractWidget>();
    private Layout layout;
    private Rect listRect;
    private Rect newIdBoxRect;
    private Rect addBtnRect;
    private Rect deleteBtnRect;
    private Rect backBtnRect;
    private Rect titleRect;
    private Rect statusRect;
    private Rect entityRow;
    private Rect antiInterruptRow;
    private Rect weightRow;
    private Rect hardnessRow;
    private Rect entityLabelRect;
    private Rect entityValueRect;
    private Rect antiInterruptFieldRect;
    private Rect weightFieldRect;
    private Rect hardnessFieldRect;
    private int detailLeft;
    private int formWidth;

    public YssEntityOverridesScreen(YssHitEditorScreen parent) {
        super((Component)Component.m_237113_((String)"Entity \u8986\u76d6"));
        this.parent = parent;
    }

    protected void m_7856_() {
        this.layout = Layout.of(this, this.f_96547_);
        Length margin = Length.em(1.5);
        Length listWidth = Length.clamp(Length.px(160), Length.pct(25.0), Length.px(280));
        Length listTop = Length.em(4.0);
        Length listBottomGap = Length.em(7.0);
        Length itemH = Length.em(3.5);
        Length detailGap = Length.em(1.5);
        Length formRowH = Length.em(2.5);
        Length formFieldXOffset = Length.em(10.0);
        Length formFieldW = Length.clamp(Length.px(100), Length.em(15.0), Length.px(160));
        Length btnW = Length.clamp(Length.px(50), Length.em(6.5), Length.px(80));
        Length btnH = Length.em(2.25);
        Length btnGap = Length.em(0.5);
        Length btnBottomGap = Length.em(1.0);
        Length newIdBoxH = Length.em(2.25);
        this.listRect = this.layout.vcol(margin, listTop, listWidth, Length.px(0)).remainingToBottom(listBottomGap);
        this.overrideList = (OverrideList)this.m_142416_((GuiEventListener)new OverrideList(this.f_96541_, this.listRect.width(), this.listRect.height(), this.listRect.y(), this.listRect.bottom(), this.layout.resolveY(itemH)));
        this.overrideList.m_93507_(this.listRect.x());
        this.refreshList();
        this.detailLeft = this.layout.resolveX(margin) + this.layout.resolveX(listWidth) + this.layout.resolveX(detailGap);
        this.formWidth = this.layout.resolveX(formFieldXOffset) + this.layout.resolveX(formFieldW);
        int btnY = this.layout.fromBottom(btnBottomGap) - this.layout.resolveY(btnH);
        int newIdBoxY = btnY - this.layout.resolveY(btnGap) - this.layout.resolveY(newIdBoxH);
        this.newIdBoxRect = this.layout.hrow(margin, Length.px(newIdBoxY), newIdBoxH, Length.px(0)).next(listWidth);
        this.newIdBox = (EditBox)this.m_142416_((GuiEventListener)new EditBox(this.f_96547_, this.newIdBoxRect.x(), this.newIdBoxRect.y(), this.newIdBoxRect.width(), this.newIdBoxRect.height(), (Component)Component.m_237119_()));
        this.newIdBox.m_94199_(128);
        HRow leftButtons = this.layout.hrow(margin, Length.px(btnY), btnH, btnGap);
        this.addBtnRect = leftButtons.next(btnW);
        this.deleteBtnRect = leftButtons.next(btnW);
        int backX = this.layout.fromRight(margin) - this.layout.resolveX(btnW);
        this.backBtnRect = this.layout.hrow(Length.px(backX), Length.px(btnY), btnH, Length.px(0)).next(btnW);
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"+\u8986\u76d6"), b -> this.addOverride()).m_252987_(this.addBtnRect.x(), this.addBtnRect.y(), this.addBtnRect.width(), this.addBtnRect.height()).m_253136_());
        this.deleteButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"-\u8986\u76d6"), b -> this.deleteOverride()).m_252987_(this.deleteBtnRect.x(), this.deleteBtnRect.y(), this.deleteBtnRect.width(), this.deleteBtnRect.height()).m_253136_());
        this.deleteButton.f_93623_ = this.selectedEntityId != null;
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u8fd4\u56de"), b -> this.back()).m_252987_(this.backBtnRect.x(), this.backBtnRect.y(), this.backBtnRect.width(), this.backBtnRect.height()).m_253136_());
        this.titleRect = this.layout.vcol(margin, Length.em(2.0), Length.px(this.f_96543_ - 2 * this.layout.resolveX(margin)), Length.px(0)).next(Length.em(1.0));
        VCol formCol = this.layout.vcol(Length.px(this.detailLeft), listTop, Length.px(this.formWidth), Length.px(0));
        this.entityRow = formCol.next(formRowH);
        this.antiInterruptRow = formCol.next(formRowH);
        this.weightRow = formCol.next(formRowH);
        this.hardnessRow = formCol.next(formRowH);
        HRow entityFields = this.layout.hrow(this.entityRow, Length.px(0));
        this.entityLabelRect = entityFields.next(Length.px(40));
        this.entityValueRect = entityFields.next(Length.px(this.entityRow.width() - 40));
        HRow antiInterruptFields = this.layout.hrow(this.antiInterruptRow, Length.px(0));
        antiInterruptFields.next(formFieldXOffset);
        this.antiInterruptFieldRect = antiInterruptFields.next(formFieldW);
        HRow weightFields = this.layout.hrow(this.weightRow, Length.px(0));
        weightFields.next(formFieldXOffset);
        this.weightFieldRect = weightFields.next(formFieldW);
        HRow hardnessFields = this.layout.hrow(this.hardnessRow, Length.px(0));
        hardnessFields.next(formFieldXOffset);
        this.hardnessFieldRect = hardnessFields.next(formFieldW);
        this.statusRect = this.layout.vcol(Length.px(this.detailLeft), Length.px(this.layout.fromBottom(Length.em(7.0))), Length.px(this.f_96543_ - this.detailLeft - this.layout.resolveX(margin)), Length.px(0)).remainingToBottom(Length.px(0));
        this.rebuildForm();
    }

    private Map<String, MutableEntityCombatStats> overrides() {
        return this.parent.editingState().getEntityOverrides();
    }

    private void refreshList() {
        this.overrideList.replaceOverrides(new ArrayList<String>(this.overrides().keySet()));
        if (this.selectedEntityId != null) {
            OverrideEntry existing = this.overrideList.findEntry(this.selectedEntityId);
            if (existing != null) {
                this.overrideList.m_6987_((AbstractSelectionList.Entry)existing);
            } else {
                this.selectedEntityId = null;
            }
        }
        if (this.deleteButton != null) {
            this.deleteButton.f_93623_ = this.selectedEntityId != null;
        }
    }

    private void selectOverride(String entityId) {
        this.selectedEntityId = entityId;
        this.clearPendingDeleteOverride();
        this.rebuildForm();
    }

    private void addOverride() {
        String id = this.newIdBox.m_94155_().trim();
        if (id.isBlank()) {
            this.statusMessage = Component.m_237113_((String)"\u5b9e\u4f53ID \u4e0d\u80fd\u4e3a\u7a7a");
            this.statusColor = -43691;
            return;
        }
        if (this.overrides().containsKey(id)) {
            this.statusMessage = Component.m_237113_((String)("\u5df2\u5b58\u5728: " + id));
            this.statusColor = -43691;
            return;
        }
        this.overrides().put(id, new MutableEntityCombatStats());
        this.newIdBox.m_94144_("");
        this.selectedEntityId = id;
        this.refreshList();
        this.statusMessage = Component.m_237113_((String)("\u5df2\u6dfb\u52a0: " + id));
        this.statusColor = -11141291;
    }

    private void deleteOverride() {
        if (this.selectedEntityId == null) {
            return;
        }
        if (!this.pendingDeleteOverride) {
            this.pendingDeleteOverride = true;
            if (this.deleteButton != null) {
                this.deleteButton.m_93666_((Component)Component.m_237113_((String)"\u786e\u8ba4\u5220\u9664?"));
            }
            this.statusMessage = Component.m_237113_((String)("\u518d\u6b21\u70b9\u51fb\u786e\u8ba4\u5220\u9664 " + this.selectedEntityId));
            this.statusColor = -171;
            return;
        }
        this.overrides().remove(this.selectedEntityId);
        this.statusMessage = Component.m_237113_((String)("\u5df2\u5220\u9664: " + this.selectedEntityId));
        this.statusColor = -11141291;
        this.selectedEntityId = null;
        this.pendingDeleteOverride = false;
        if (this.deleteButton != null) {
            this.deleteButton.m_93666_((Component)Component.m_237113_((String)"-\u8986\u76d6"));
            this.deleteButton.f_93623_ = false;
        }
        this.refreshList();
        this.rebuildForm();
    }

    private void clearPendingDeleteOverride() {
        if (this.pendingDeleteOverride) {
            this.pendingDeleteOverride = false;
            if (this.deleteButton != null) {
                this.deleteButton.m_93666_((Component)Component.m_237113_((String)"-\u8986\u76d6"));
            }
        }
    }

    private void back() {
        this.clearPendingDeleteOverride();
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)this.parent);
        }
    }

    private void rebuildForm() {
        this.formWidgets.forEach(x$0 -> this.m_169411_((GuiEventListener)x$0));
        this.formWidgets.clear();
        if (this.selectedEntityId == null) {
            return;
        }
        MutableEntityCombatStats stats = this.overrides().get(this.selectedEntityId);
        if (stats == null) {
            this.selectedEntityId = null;
            return;
        }
        this.floatEditBox(this.antiInterruptFieldRect, stats.getAntiInterrupt(), stats::setAntiInterrupt);
        this.floatEditBox(this.weightFieldRect, stats.getWeight(), stats::setWeight);
        this.floatEditBox(this.hardnessFieldRect, stats.getHardness(), stats::setHardness);
    }

    private void addFormWidget(AbstractWidget widget) {
        this.m_142416_((GuiEventListener)widget);
        this.formWidgets.add(widget);
    }

    private void floatEditBox(Rect rect, float current, Consumer<Float> setter) {
        EditBox box = new EditBox(this.f_96547_, rect.x(), rect.y(), rect.width(), rect.height(), (Component)Component.m_237119_());
        box.m_94199_(64);
        box.m_94144_(String.valueOf(current));
        box.m_94153_(s -> s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.") || YssEntityOverridesScreen.isFloat(s));
        box.m_94151_(s -> {
            if (s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.")) {
                return;
            }
            try {
                setter.accept(Float.valueOf(Float.parseFloat(s)));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        });
        this.addFormWidget((AbstractWidget)box);
    }

    private static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
        graphics.m_280614_(this.f_96547_, this.f_96539_, this.titleRect.x(), this.titleRect.y(), -1, false);
        if (this.selectedEntityId != null) {
            graphics.m_280056_(this.f_96547_, "\u5b9e\u4f53:", this.entityLabelRect.x(), this.entityLabelRect.y() + 4, -5592406, false);
            graphics.m_280056_(this.f_96547_, this.selectedEntityId, this.entityValueRect.x(), this.entityValueRect.y() + 4, -1, false);
            graphics.m_280056_(this.f_96547_, "\u6297\u6253\u65ad", this.antiInterruptRow.x(), this.antiInterruptRow.y() + 4, -5592406, false);
            graphics.m_280056_(this.f_96547_, "\u91cd\u91cf", this.weightRow.x(), this.weightRow.y() + 4, -5592406, false);
            graphics.m_280056_(this.f_96547_, "\u786c\u5ea6", this.hardnessRow.x(), this.hardnessRow.y() + 4, -5592406, false);
        } else {
            graphics.m_280056_(this.f_96547_, "\u8bf7\u9009\u62e9\u5de6\u4fa7\u6761\u76ee\u6216 +\u8986\u76d6 \u65b0\u5efa", this.detailLeft, this.listRect.y(), -5592406, false);
        }
        if (this.statusMessage != null) {
            graphics.m_280554_(this.f_96547_, (FormattedText)this.statusMessage, this.statusRect.x(), this.statusRect.y(), this.statusRect.width(), this.statusColor);
        }
    }

    private class OverrideList
    extends ObjectSelectionList<OverrideEntry> {
        public OverrideList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
            super(minecraft, width, height, y0, y1, itemHeight);
        }

        private void replaceOverrides(List<String> ids) {
            this.m_93516_();
            for (String id : ids) {
                this.m_7085_((AbstractSelectionList.Entry)new OverrideEntry(id));
            }
        }

        public int m_5759_() {
            return this.f_93388_ - 10;
        }

        public int m_5756_() {
            return this.getRight() - 6;
        }

        @Nullable
        private OverrideEntry findEntry(String entityId) {
            for (OverrideEntry entry : this.m_6702_()) {
                if (!entry.entityId.equals(entityId)) continue;
                return entry;
            }
            return null;
        }
    }

    private class OverrideEntry
    extends ObjectSelectionList.Entry<OverrideEntry> {
        private final String entityId;

        private OverrideEntry(String entityId) {
            this.entityId = entityId;
        }

        public void m_6311_(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            MutableEntityCombatStats stats = YssEntityOverridesScreen.this.overrides().get(this.entityId);
            String summary = stats == null ? "?" : String.format("a:%.2f w:%.2f h:%.2f", Float.valueOf(stats.getAntiInterrupt()), Float.valueOf(stats.getWeight()), Float.valueOf(stats.getHardness()));
            int color = ((Object)((Object)this)).equals(YssEntityOverridesScreen.this.overrideList.m_93511_()) ? -171 : -1;
            graphics.m_280056_(YssEntityOverridesScreen.this.f_96547_, this.entityId, left + 4, top + 2, color, false);
            graphics.m_280056_(YssEntityOverridesScreen.this.f_96547_, summary, left + 4, top + 14, -5592406, false);
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (button == 0) {
                YssEntityOverridesScreen.this.selectOverride(this.entityId);
                return true;
            }
            return false;
        }

        public Component m_142172_() {
            return Component.m_237113_((String)("\u5b9e\u4f53\u8986\u76d6 " + this.entityId));
        }
    }
}

