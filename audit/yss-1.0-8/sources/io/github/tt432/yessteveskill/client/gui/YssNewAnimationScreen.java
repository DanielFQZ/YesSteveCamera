/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.ObjectSelectionList
 *  net.minecraft.client.gui.components.ObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.client.gui;

import io.github.tt432.yessteveskill.client.gui.YssHitEditorScreen;
import io.github.tt432.yessteveskill.client.gui.layout.HRow;
import io.github.tt432.yessteveskill.client.gui.layout.Layout;
import io.github.tt432.yessteveskill.client.gui.layout.Length;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import io.github.tt432.yessteveskill.ysm.YSMModelAssets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class YssNewAnimationScreen
extends Screen {
    private final YssHitEditorScreen parent;
    private Layout layout;
    private Rect animNameBoxRect;
    private Rect candidateListRect;
    private Rect confirmBtnRect;
    private Rect cancelBtnRect;
    private Rect titleRect;
    private Rect statusRect;
    private EditBox animNameBox;
    private CandidateList candidateList;
    private List<String> allCandidates = List.of();
    private List<YSMModelAssets.AnimationGroup> allCandidateGroups = List.of();
    @Nullable
    private Component statusMessage;
    private int statusColor = -1;

    public YssNewAnimationScreen(YssHitEditorScreen parent) {
        super((Component)Component.m_237113_((String)"\u65b0\u5efa\u52a8\u753b"));
        this.parent = parent;
    }

    protected void m_7856_() {
        this.layout = Layout.of(this, this.f_96547_);
        Length boxW = Length.clamp(Length.px(160), Length.pct(30.0), Length.px(240));
        Length boxH = Length.em(2.25);
        Length boxY = Length.em(2.0);
        Length listTop = Length.em(5.0);
        Length listBottomGap = Length.em(7.5);
        Length itemH = Length.em(2.0);
        Length btnW = Length.clamp(Length.px(70), Length.em(10.0), Length.px(110));
        Length btnH = Length.em(2.5);
        Length btnGap = Length.em(1.0);
        Length btnBottomGap = Length.em(1.0);
        Length statusBottomGap = Length.em(6.0);
        Length titleY = Length.em(1.0);
        int centerX = this.layout.centerX(0);
        int listX = centerX - this.layout.resolveX(boxW) / 2;
        this.animNameBoxRect = new Rect(listX, this.layout.resolveY(boxY), this.layout.resolveX(boxW), this.layout.resolveY(boxH));
        this.animNameBox = (EditBox)this.m_142416_((GuiEventListener)new EditBox(this.f_96547_, this.animNameBoxRect.x(), this.animNameBoxRect.y(), this.animNameBoxRect.width(), this.animNameBoxRect.height(), (Component)Component.m_237119_()));
        this.animNameBox.m_94199_(1024);
        this.animNameBox.m_257771_((Component)Component.m_237113_((String)"\u641c\u7d22\u6216\u8f93\u5165\u52a8\u753b\u540d..."));
        this.animNameBox.m_94151_(text -> this.refreshFilter());
        this.candidateListRect = this.layout.vcol(Length.px(listX), listTop, boxW, Length.px(0)).remainingToBottom(listBottomGap);
        this.candidateList = (CandidateList)this.m_142416_((GuiEventListener)new CandidateList(this.f_96541_, this.candidateListRect.width(), this.candidateListRect.height(), this.candidateListRect.y(), this.candidateListRect.bottom(), this.layout.resolveY(itemH)));
        this.candidateList.m_93507_(this.candidateListRect.x());
        this.allCandidateGroups = this.parent.candidateAnimationGroups();
        this.allCandidates = this.allCandidateGroups.stream().flatMap(group -> group.animationNames().stream()).toList();
        this.refreshFilter();
        int totalBtnW = this.layout.resolveX(btnW) * 2 + this.layout.resolveX(btnGap);
        int btnX = centerX - totalBtnW / 2;
        int btnY = this.layout.fromBottom(btnBottomGap) - this.layout.resolveY(btnH);
        HRow buttonRow = new HRow(btnX, btnY, this.layout.resolveY(btnH), this.layout.resolveX(btnGap));
        this.confirmBtnRect = buttonRow.next(this.layout.resolveX(btnW));
        this.cancelBtnRect = buttonRow.next(this.layout.resolveX(btnW));
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u786e\u8ba4"), b -> this.confirm()).m_252987_(this.confirmBtnRect.x(), this.confirmBtnRect.y(), this.confirmBtnRect.width(), this.confirmBtnRect.height()).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u53d6\u6d88"), b -> this.cancel()).m_252987_(this.cancelBtnRect.x(), this.cancelBtnRect.y(), this.cancelBtnRect.width(), this.cancelBtnRect.height()).m_253136_());
        int n = this.layout.resolveY(titleY);
        Objects.requireNonNull(this.f_96547_);
        this.titleRect = new Rect(centerX, n, 0, 9);
        int n2 = this.layout.fromBottom(statusBottomGap);
        Objects.requireNonNull(this.f_96547_);
        this.statusRect = new Rect(centerX, n2, 0, 9);
    }

    private void refreshFilter() {
        String raw = this.animNameBox.m_94155_();
        String filter = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        this.candidateList.replaceGroups(this.allCandidateGroups, filter);
    }

    private void confirm() {
        String name;
        String raw = this.animNameBox.m_94155_();
        String string = name = raw == null ? "" : raw.trim();
        if (name.isEmpty()) {
            this.statusMessage = Component.m_237113_((String)"\u52a8\u753b\u540d\u4e0d\u80fd\u4e3a\u7a7a");
            this.statusColor = -43691;
            return;
        }
        if (!this.parent.addAnimation(name)) {
            this.statusMessage = Component.m_237113_((String)("\u52a8\u753b\u5df2\u5b58\u5728\uff1a" + name));
            this.statusColor = -43691;
            return;
        }
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)this.parent);
        }
    }

    private void cancel() {
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)this.parent);
        }
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
        graphics.m_280653_(this.f_96547_, this.f_96539_, this.titleRect.x(), this.titleRect.y(), -1);
        if (this.statusMessage != null) {
            graphics.m_280653_(this.f_96547_, this.statusMessage, this.statusRect.x(), this.statusRect.y(), this.statusColor);
        }
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.cancel();
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    private class CandidateList
    extends ObjectSelectionList<CandidateEntry> {
        CandidateList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
            super(minecraft, width, height, y0, y1, itemHeight);
        }

        void replaceGroups(List<YSMModelAssets.AnimationGroup> groups, String filter) {
            this.m_93516_();
            for (YSMModelAssets.AnimationGroup group : groups) {
                List matches = group.animationNames().stream().filter(name -> filter.isEmpty() || name.toLowerCase(Locale.ROOT).contains(filter)).toList();
                if (matches.isEmpty()) continue;
                this.m_7085_((AbstractSelectionList.Entry)new CandidateEntry(group.fileName(), null));
                for (String name2 : matches) {
                    this.m_7085_((AbstractSelectionList.Entry)new CandidateEntry(name2, name2));
                }
            }
        }

        public int m_5759_() {
            return this.f_93388_ - 10;
        }

        public int m_5756_() {
            return this.getRight() - 6;
        }
    }

    private class CandidateEntry
    extends ObjectSelectionList.Entry<CandidateEntry> {
        private final String label;
        @Nullable
        private final String animName;

        private CandidateEntry(@Nullable String label, String animName) {
            this.label = label;
            this.animName = animName;
        }

        public void m_6311_(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            boolean header;
            boolean bl = header = this.animName == null;
            int color = header ? -2047872 : (((Object)((Object)this)).equals(YssNewAnimationScreen.this.candidateList.m_93511_()) ? -171 : -1);
            graphics.m_280056_(YssNewAnimationScreen.this.f_96547_, this.label, left + (header ? 4 : 16), top + 5, color, false);
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (button == 0 && this.animName != null) {
                YssNewAnimationScreen.this.animNameBox.m_94144_(this.animName);
                YssNewAnimationScreen.this.candidateList.m_6987_((AbstractSelectionList.Entry)this);
                return true;
            }
            return false;
        }

        public Component m_142172_() {
            return this.animName == null ? Component.m_237113_((String)("\u52a8\u753b\u6587\u4ef6 " + this.label)) : Component.m_237113_((String)("\u52a8\u753b\u5019\u9009 " + this.animName));
        }
    }
}

