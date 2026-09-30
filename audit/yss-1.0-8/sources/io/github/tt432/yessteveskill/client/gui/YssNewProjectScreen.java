/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapabilityProvider
 *  com.elfmcys.ysm.client.gui.PlayerModelScreen
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.CycleButton
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.ObjectSelectionList
 *  net.minecraft.client.gui.components.ObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.client.gui;

import com.elfmcys.ysm.capability.PlayerAnimatableCapabilityProvider;
import com.elfmcys.ysm.client.gui.PlayerModelScreen;
import io.github.tt432.yessteveskill.client.gui.YssHitEditorScreen;
import io.github.tt432.yessteveskill.client.gui.YssProjectExportScreen;
import io.github.tt432.yessteveskill.client.gui.layout.HRow;
import io.github.tt432.yessteveskill.client.gui.layout.Layout;
import io.github.tt432.yessteveskill.client.gui.layout.Length;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import io.github.tt432.yessteveskill.client.gui.layout.VCol;
import io.github.tt432.yessteveskill.yss.project.YssProject;
import io.github.tt432.yessteveskill.yss.project.YssProjectManager;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

public class YssNewProjectScreen
extends Screen {
    private final YssProjectManager projectManager = YssProjectManager.INSTANCE;
    private Mode mode = Mode.EMPTY;
    private Layout layout;
    private Rect modelIdBoxRect;
    private Rect modeButtonRect;
    private Rect sourceListRect;
    private Rect confirmBtnRect;
    private Rect cancelBtnRect;
    private Rect titleRect;
    private Rect modeRect;
    private Rect helpRect;
    private Rect statusRect;
    private EditBox modelIdBox;
    private String modelIdText = "";
    private CycleButton<Mode> modeButton;
    private SourceList sourceList;
    private List<YssProject> projects = List.of();
    @Nullable
    private YssProject selectedSource;
    @Nullable
    private Component statusMessage;
    private int statusColor = -1;

    public YssNewProjectScreen() {
        super((Component)Component.m_237113_((String)"\u65b0\u5efa\u5de5\u7a0b"));
    }

    protected void m_7856_() {
        this.layout = Layout.of(this, this.f_96547_);
        Length margin = Length.em(1.5);
        Length listWidth = Length.clamp(Length.px(160), Length.pct(25.0), Length.px(240));
        Length listTop = Length.em(5.0);
        Length listBottomGap = Length.em(4.5);
        Length itemH = Length.em(2.0);
        Length topY = Length.em(2.0);
        Length topH = Length.em(2.25);
        Length topGap = Length.em(1.0);
        Length modelIdBoxW = Length.clamp(Length.px(120), Length.em(15.0), Length.px(200));
        Length ysmPickW = Length.em(9.0);
        Length modeBtnW = Length.em(11.0);
        Length detailGap = Length.em(1.5);
        Length confirmW = Length.em(11.0);
        Length cancelW = Length.em(10.0);
        Length btnH = Length.em(2.5);
        Length btnGap = Length.em(0.5);
        Length btnBottomGap = Length.em(1.0);
        HRow topRow = this.layout.hrow(margin, topY, topH, topGap);
        this.modelIdBoxRect = topRow.next(modelIdBoxW);
        Rect ysmPickRect = topRow.next(ysmPickW);
        this.modeButtonRect = topRow.next(modeBtnW);
        this.modelIdBox = (EditBox)this.m_142416_((GuiEventListener)new EditBox(this.f_96547_, this.modelIdBoxRect.x(), this.modelIdBoxRect.y(), this.modelIdBoxRect.width(), this.modelIdBoxRect.height(), (Component)Component.m_237113_((String)"modelId")));
        this.modelIdBox.m_94199_(128);
        this.modelIdBox.m_257771_((Component)Component.m_237113_((String)"\u8f93\u5165 modelId"));
        this.modelIdBox.m_94144_(this.modelIdText);
        this.modelIdBox.m_94151_(s -> {
            this.modelIdText = s;
        });
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u4eceYSM\u9009"), b -> {
            Minecraft minecraft = this.f_96541_;
            if (minecraft != null) {
                minecraft.m_91152_((Screen)new YssPlayerModelScreen(this));
            }
        }).m_252987_(ysmPickRect.x(), ysmPickRect.y(), ysmPickRect.width(), ysmPickRect.height()).m_253136_());
        this.modeButton = (CycleButton)this.m_142416_((GuiEventListener)CycleButton.m_168894_(m -> Component.m_237113_((String)(m == Mode.EMPTY ? "\u7a7a\u76ee\u5f55" : "\u590d\u5236\u73b0\u6709"))).m_168961_((Object[])Mode.values()).m_168948_((Object)this.mode).m_168936_(this.modeButtonRect.x(), this.modeButtonRect.y(), this.modeButtonRect.width(), this.modeButtonRect.height(), (Component)Component.m_237119_(), (b, v) -> {
            this.mode = v;
            this.selectedSource = null;
            this.rebuildSourceList();
        }));
        this.sourceListRect = this.layout.vcol(margin, listTop, listWidth, Length.px(0)).remainingToBottom(listBottomGap);
        this.sourceList = (SourceList)this.m_142416_((GuiEventListener)new SourceList(this.f_96541_, this.sourceListRect.width(), this.sourceListRect.height(), this.sourceListRect.y(), this.sourceListRect.bottom(), this.layout.resolveY(itemH)));
        this.sourceList.m_93507_(this.sourceListRect.x());
        this.projects = this.projectManager.loadProjects();
        this.rebuildSourceList();
        int detailLeft = this.layout.resolveX(margin) + this.layout.resolveX(listWidth) + this.layout.resolveX(detailGap);
        int btnY = this.layout.fromBottom(btnBottomGap) - this.layout.resolveY(btnH);
        HRow confirmRow = this.layout.hrow(Length.px(detailLeft), Length.px(btnY), btnH, btnGap);
        this.confirmBtnRect = confirmRow.next(confirmW);
        this.cancelBtnRect = confirmRow.next(cancelW);
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u786e\u8ba4\u65b0\u5efa"), b -> this.confirmCreate()).m_252987_(this.confirmBtnRect.x(), this.confirmBtnRect.y(), this.confirmBtnRect.width(), this.confirmBtnRect.height()).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u53d6\u6d88"), b -> this.cancel()).m_252987_(this.cancelBtnRect.x(), this.cancelBtnRect.y(), this.cancelBtnRect.width(), this.cancelBtnRect.height()).m_253136_());
        int detailRight = this.layout.fromRight(margin);
        int detailWidth = detailRight - detailLeft;
        VCol details = this.layout.vcol(Length.px(detailLeft), topY, Length.px(detailWidth), Length.em(0.25));
        this.titleRect = details.next(Length.em(1.5));
        this.modeRect = details.next(Length.em(1.5));
        this.helpRect = details.next(Length.em(1.0));
        this.statusRect = this.layout.vcol(Length.px(detailLeft), Length.px(this.layout.fromBottom(Length.em(7.0))), Length.px(detailWidth), Length.px(0)).next(Length.em(1.0));
    }

    private void rebuildSourceList() {
        if (this.sourceList == null) {
            return;
        }
        this.sourceList.replaceSources(this.mode == Mode.COPY ? this.projects : List.of());
        this.selectedSource = null;
    }

    private void confirmCreate() {
        String modelId = this.modelIdBox.m_94155_().trim();
        if (modelId.isEmpty()) {
            this.statusMessage = Component.m_237113_((String)"modelId \u4e0d\u80fd\u4e3a\u7a7a");
            this.statusColor = -43691;
            return;
        }
        try {
            Path dir;
            if (this.mode == Mode.COPY) {
                if (this.selectedSource == null) {
                    this.statusMessage = Component.m_237113_((String)"\u8bf7\u9009\u62e9\u6e90\u5de5\u7a0b");
                    this.statusColor = -43691;
                    return;
                }
                dir = this.projectManager.copyProject(this.selectedSource.directory(), modelId);
            } else {
                dir = this.projectManager.createProject(modelId);
            }
            YssProject newProject = this.projectManager.loadProject(dir);
            Minecraft minecraft = this.f_96541_;
            if (minecraft != null) {
                minecraft.m_91152_((Screen)new YssHitEditorScreen(newProject));
            }
        }
        catch (IOException e) {
            String message = e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage();
            this.statusMessage = Component.m_237113_((String)("\u65b0\u5efa\u5931\u8d25\uff1a" + message));
            this.statusColor = -43691;
        }
    }

    private void cancel() {
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)new YssProjectExportScreen());
        }
    }

    void fillModelIdFromYsm(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return;
        }
        this.modelIdText = modelId;
        if (this.modelIdBox != null) {
            this.modelIdBox.m_94144_(modelId);
        }
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
        graphics.m_280614_(this.f_96547_, this.f_96539_, this.titleRect.x(), this.titleRect.y(), -1, false);
        graphics.m_280056_(this.f_96547_, "\u65b9\u5f0f\uff1a" + (this.mode == Mode.EMPTY ? "\u7a7a\u76ee\u5f55" : "\u590d\u5236\u73b0\u6709"), this.modeRect.x(), this.modeRect.y(), -5592406, false);
        if (this.mode == Mode.COPY) {
            graphics.m_280056_(this.f_96547_, "\u9009\u62e9\u6e90\u5de5\u7a0b\uff08\u5de6\u4fa7\u5217\u8868\uff09", this.helpRect.x(), this.helpRect.y(), -5592406, false);
        } else {
            graphics.m_280056_(this.f_96547_, "\u7a7a\u76ee\u5f55\uff1a\u5efa\u76ee\u5f55\u540e\u62d6\u5165\u6587\u4ef6", this.helpRect.x(), this.helpRect.y(), -5592406, false);
        }
        if (this.statusMessage != null) {
            graphics.m_280554_(this.f_96547_, (FormattedText)this.statusMessage, this.statusRect.x(), this.statusRect.y(), this.statusRect.width(), this.statusColor);
        }
    }

    private static enum Mode {
        EMPTY,
        COPY;

    }

    private class SourceList
    extends ObjectSelectionList<SourceEntry> {
        SourceList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
            super(minecraft, width, height, y0, y1, itemHeight);
        }

        void replaceSources(List<YssProject> sources) {
            this.m_93516_();
            for (YssProject project : sources) {
                this.m_7085_((AbstractSelectionList.Entry)new SourceEntry(project));
            }
        }

        public int m_5759_() {
            return this.f_93388_ - 10;
        }

        public int m_5756_() {
            return this.getRight() - 6;
        }
    }

    private static final class YssPlayerModelScreen
    extends PlayerModelScreen {
        private final YssNewProjectScreen parent;

        YssPlayerModelScreen(YssNewProjectScreen parent) {
            this.parent = parent;
        }

        public void m_7379_() {
            Minecraft minecraft = Minecraft.m_91087_();
            LocalPlayer player = minecraft.f_91074_;
            if (player != null) {
                player.getCapability(PlayerAnimatableCapabilityProvider.CAP).ifPresent(capability -> {
                    if (capability.getModelHash() != null) {
                        this.parent.fillModelIdFromYsm(capability.getModelId());
                    }
                });
            }
            minecraft.m_91152_((Screen)this.parent);
        }
    }

    private class SourceEntry
    extends ObjectSelectionList.Entry<SourceEntry> {
        private final YssProject project;

        SourceEntry(YssProject project) {
            this.project = project;
        }

        public void m_6311_(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int color = ((Object)((Object)this)).equals(YssNewProjectScreen.this.sourceList.m_93511_()) ? -171 : -1;
            graphics.m_280056_(YssNewProjectScreen.this.f_96547_, this.project.name(), left + 4, top + 5, color, false);
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (button == 0) {
                YssNewProjectScreen.this.selectedSource = this.project;
                YssNewProjectScreen.this.sourceList.m_6987_((AbstractSelectionList.Entry)this);
                return true;
            }
            return false;
        }

        public Component m_142172_() {
            return Component.m_237113_((String)("\u6e90\u5de5\u7a0b " + this.project.name()));
        }
    }
}

