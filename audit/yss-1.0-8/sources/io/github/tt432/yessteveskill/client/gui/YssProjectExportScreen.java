/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.Button
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
import io.github.tt432.yessteveskill.client.gui.YssNewProjectScreen;
import io.github.tt432.yessteveskill.client.gui.layout.HRow;
import io.github.tt432.yessteveskill.client.gui.layout.Layout;
import io.github.tt432.yessteveskill.client.gui.layout.Length;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import io.github.tt432.yessteveskill.yss.project.YssProject;
import io.github.tt432.yessteveskill.yss.project.YssProjectManager;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

public class YssProjectExportScreen
extends Screen {
    private static final int DETAIL_MAX_ITEMS = 8;
    private final YssProjectManager projectManager = YssProjectManager.INSTANCE;
    private ProjectList projectList;
    private Button exportButton;
    private Button editButton;
    private Button deleteButton;
    private Button newProjectButton;
    private boolean pendingDelete = false;
    private List<YssProject> projects = List.of();
    @Nullable
    private YssProject selectedProject;
    @Nullable
    private Component statusMessage;
    private int statusColor = -1;
    private Layout layout;
    private Rect listRect;
    private Rect editBtnRect;
    private Rect deleteBtnRect;
    private Rect newProjectBtnRect;
    private Rect exportBtnRect;
    private Rect titleRect;
    private Rect statusRect;
    private int detailLeft;
    private int detailWidth;

    public YssProjectExportScreen() {
        super((Component)Component.m_237113_((String)"YSS \u5bfc\u51fa"));
    }

    protected void m_7856_() {
        this.layout = Layout.of(this, this.f_96547_);
        Length margin = Length.em(1.5);
        Length listWidth = Length.clamp(Length.px(160), Length.pct(25.0), Length.px(280));
        Length listTop = Length.em(4.0);
        Length listBottomGap = Length.em(4.5);
        Length itemH = Length.em(2.0);
        Length detailGap = Length.em(1.5);
        Length btnH = Length.em(2.5);
        Length btnGap = Length.em(0.5);
        Length btnBottomGap = Length.em(1.0);
        Length editW = Length.clamp(Length.px(60), Length.em(7.5), Length.px(100));
        Length deleteW = Length.clamp(Length.px(40), Length.em(5.0), Length.px(70));
        Length newProjectW = Length.clamp(Length.px(55), Length.em(7.0), Length.px(95));
        Length exportW = editW;
        this.listRect = this.layout.vcol(margin, listTop, listWidth, Length.px(0)).remainingToBottom(listBottomGap);
        this.projectList = (ProjectList)this.m_142416_((GuiEventListener)new ProjectList(this.f_96541_, this.listRect.width(), this.listRect.height(), this.listRect.y(), this.listRect.bottom(), this.layout.resolveY(itemH)));
        this.projectList.m_93507_(this.listRect.x());
        this.detailLeft = this.layout.resolveX(margin) + this.layout.resolveX(listWidth) + this.layout.resolveX(detailGap);
        this.detailWidth = this.f_96543_ - this.detailLeft - this.layout.resolveX(margin);
        int exportBtnY = this.layout.fromBottom(btnBottomGap) - this.layout.resolveY(btnH);
        int editRowY = exportBtnY - this.layout.resolveY(btnH) - this.layout.resolveY(btnGap);
        HRow editRow = this.layout.hrow(Length.px(this.detailLeft), Length.px(editRowY), btnH, btnGap);
        this.editBtnRect = editRow.next(editW);
        this.deleteBtnRect = editRow.next(deleteW);
        this.newProjectBtnRect = editRow.next(newProjectW);
        this.exportBtnRect = this.layout.hrow(Length.px(this.detailLeft), Length.px(exportBtnY), btnH, btnGap).next(exportW);
        this.exportButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u5bfc\u51fa .yss"), button -> this.exportSelectedProject()).m_252987_(this.exportBtnRect.x(), this.exportBtnRect.y(), this.exportBtnRect.width(), this.exportBtnRect.height()).m_253136_());
        this.editButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u7f16\u8f91"), button -> this.openEditor()).m_252987_(this.editBtnRect.x(), this.editBtnRect.y(), this.editBtnRect.width(), this.editBtnRect.height()).m_253136_());
        this.deleteButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u5220\u9664"), button -> this.deleteSelectedProject()).m_252987_(this.deleteBtnRect.x(), this.deleteBtnRect.y(), this.deleteBtnRect.width(), this.deleteBtnRect.height()).m_253136_());
        this.newProjectButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u65b0\u5efa\u5de5\u7a0b"), button -> this.openNewProject()).m_252987_(this.newProjectBtnRect.x(), this.newProjectBtnRect.y(), this.newProjectBtnRect.width(), this.newProjectBtnRect.height()).m_253136_());
        this.titleRect = this.layout.vcol(Length.px(this.detailLeft), listTop, Length.px(this.detailWidth), Length.px(0)).next(Length.em(1.0));
        this.statusRect = this.layout.vcol(Length.px(this.detailLeft), Length.px(this.layout.fromBottom(Length.em(7.0))), Length.px(this.detailWidth), Length.px(0)).remainingToBottom(Length.px(0));
        this.reloadProjects();
    }

    private void reloadProjects() {
        this.projects = this.projectManager.loadProjects();
        ProjectEntry firstEntry = this.projectList.replaceProjects(this.projects);
        if (firstEntry != null) {
            this.setSelectedProject(firstEntry.project, firstEntry);
        } else {
            this.selectedProject = null;
            this.statusMessage = Component.m_237113_((String)"\u672a\u5728\u6587\u4ef6\u5939 config/yes_steve_skill \u4e0b\u627e\u5230\u5de5\u7a0b\u76ee\u5f55\uff08\u6587\u4ef6\u5939\uff09");
            this.statusColor = -22016;
            this.updateExportButtonState();
        }
    }

    private void setSelectedProject(@Nullable YssProject project, @Nullable ProjectEntry entry) {
        this.selectedProject = project;
        this.projectList.m_6987_((AbstractSelectionList.Entry)entry);
        this.clearPendingDelete();
        this.updateExportButtonState();
    }

    private void updateExportButtonState() {
        boolean selected = this.selectedProject != null;
        this.exportButton.f_93623_ = selected && this.selectedProject.canExport();
        this.editButton.f_93623_ = selected;
        this.deleteButton.f_93623_ = selected;
    }

    private void openEditor() {
        if (this.selectedProject == null) {
            return;
        }
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)new YssHitEditorScreen(this.selectedProject));
        }
    }

    private void openNewProject() {
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)new YssNewProjectScreen());
        }
    }

    private void deleteSelectedProject() {
        if (this.selectedProject == null) {
            return;
        }
        if (!this.pendingDelete) {
            this.pendingDelete = true;
            this.deleteButton.m_93666_((Component)Component.m_237113_((String)"\u786e\u8ba4\u5220\u9664?"));
            this.statusMessage = Component.m_237113_((String)"\u518d\u6b21\u70b9\u51fb\u300c\u786e\u8ba4\u5220\u9664\u300d\u5c06\u5220\u9664\u5de5\u7a0b\u76ee\u5f55");
            this.statusColor = -171;
            return;
        }
        this.pendingDelete = false;
        this.deleteButton.m_93666_((Component)Component.m_237113_((String)"\u5220\u9664"));
        try {
            this.projectManager.deleteProject(this.selectedProject.directory());
            this.statusMessage = Component.m_237113_((String)"\u5220\u9664\u6210\u529f");
            this.statusColor = -11141291;
            this.sendPlayerMessage(this.statusMessage);
            this.reloadProjects();
        }
        catch (IOException e) {
            String message = e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage();
            this.statusMessage = Component.m_237113_((String)("\u5220\u9664\u5931\u8d25\uff1a" + message));
            this.statusColor = -43691;
            this.sendPlayerMessage(this.statusMessage);
        }
    }

    private void clearPendingDelete() {
        if (this.pendingDelete) {
            this.pendingDelete = false;
            if (this.deleteButton != null) {
                this.deleteButton.m_93666_((Component)Component.m_237113_((String)"\u5220\u9664"));
            }
        }
    }

    private void exportSelectedProject() {
        if (this.selectedProject == null) {
            return;
        }
        try {
            YssProjectManager.ExportResult result = this.projectManager.exportProject(this.selectedProject);
            this.statusMessage = Component.m_237113_((String)("\u5bfc\u51fa\u6210\u529f\uff1a" + result.path().getFileName() + "\uff08\u9aa8\u9abc " + result.boneCount() + "\uff0c\u52a8\u753b " + result.animationCount() + "\uff09"));
            this.statusColor = -11141291;
            this.sendPlayerMessage(this.statusMessage);
            this.reloadProjects();
        }
        catch (IOException e) {
            String message = e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage();
            this.statusMessage = Component.m_237113_((String)("\u5bfc\u51fa\u5931\u8d25\uff1a" + message));
            this.statusColor = -43691;
            this.sendPlayerMessage(this.statusMessage);
        }
    }

    private void sendPlayerMessage(Component message) {
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null && minecraft.f_91074_ != null) {
            minecraft.f_91074_.m_213846_(message);
        }
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
        graphics.m_280614_(this.f_96547_, this.f_96539_, this.titleRect.x(), this.titleRect.y(), -1, false);
        int y = this.titleRect.bottom();
        if (this.selectedProject == null) {
            graphics.m_280056_(this.f_96547_, "\u8bf7\u9009\u62e9\u5de6\u4fa7\u5de5\u7a0b", this.detailLeft, y, -5592406, false);
        } else {
            graphics.m_280056_(this.f_96547_, "\u9879\u76ee\uff1a" + this.selectedProject.name(), this.detailLeft, y, -1, false);
            Objects.requireNonNull(this.f_96547_);
            graphics.m_280056_(this.f_96547_, "\u6a21\u578b\uff1a" + this.selectedProject.modelId(), this.detailLeft, y += 9 + 2, -5592406, false);
            Objects.requireNonNull(this.f_96547_);
            y += 9 + 6;
            y = this.drawListSection(graphics, "\u9aa8\u9abc", this.selectedProject.bones(), this.detailLeft, y);
            y += 4;
            y = this.drawListSection(graphics, "\u52a8\u753b", this.selectedProject.animations(), this.detailLeft, y);
            if (this.selectedProject.errorMessage() != null) {
                graphics.m_280554_(this.f_96547_, (FormattedText)Component.m_237113_((String)("\u9879\u76ee\u9519\u8bef\uff1a" + this.selectedProject.errorMessage())), this.detailLeft, y += 6, this.detailWidth, -43691);
            }
        }
        if (this.statusMessage != null) {
            graphics.m_280554_(this.f_96547_, (FormattedText)this.statusMessage, this.statusRect.x(), this.statusRect.y(), this.statusRect.width(), this.statusColor);
        }
    }

    private int drawListSection(GuiGraphics graphics, String title, List<String> values, int x, int y) {
        graphics.m_280056_(this.f_96547_, title + "\uff08" + values.size() + "\uff09", x, y, -2047872, false);
        Objects.requireNonNull(this.f_96547_);
        y += 9 + 1;
        if (values.isEmpty()) {
            graphics.m_280056_(this.f_96547_, "- \u65e0", x + 4, y, -5592406, false);
            Objects.requireNonNull(this.f_96547_);
            return y + 9;
        }
        int count = Math.min(values.size(), 8);
        for (int i = 0; i < count; ++i) {
            graphics.m_280056_(this.f_96547_, "- " + values.get(i), x + 4, y, -1, false);
            Objects.requireNonNull(this.f_96547_);
            y += 9;
        }
        if (values.size() > count) {
            graphics.m_280056_(this.f_96547_, "... \u8fd8\u6709 " + (values.size() - count) + " \u9879", x + 4, y, -5592406, false);
            Objects.requireNonNull(this.f_96547_);
            y += 9;
        }
        return y;
    }

    private class ProjectList
    extends ObjectSelectionList<ProjectEntry> {
        public ProjectList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
            super(minecraft, width, height, y0, y1, itemHeight);
        }

        @Nullable
        private ProjectEntry replaceProjects(List<YssProject> projects) {
            this.m_93516_();
            ProjectEntry firstEntry = null;
            for (YssProject project : projects) {
                ProjectEntry entry = new ProjectEntry(project);
                this.m_7085_((AbstractSelectionList.Entry)entry);
                if (firstEntry != null) continue;
                firstEntry = entry;
            }
            return firstEntry;
        }

        public int m_5759_() {
            return this.f_93388_ - 10;
        }

        public int m_5756_() {
            return this.getRight() - 6;
        }
    }

    private class ProjectEntry
    extends ObjectSelectionList.Entry<ProjectEntry> {
        private final YssProject project;

        private ProjectEntry(YssProject project) {
            this.project = project;
        }

        public void m_6311_(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int color = this.project.canExport() ? -1 : -30584;
            graphics.m_280056_(YssProjectExportScreen.this.f_96547_, this.project.name(), left + 4, top + 5, color, false);
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (button == 0) {
                YssProjectExportScreen.this.setSelectedProject(this.project, this);
                return true;
            }
            return false;
        }

        public Component m_142172_() {
            return Component.m_237113_((String)("\u5de5\u7a0b " + this.project.name()));
        }
    }
}

