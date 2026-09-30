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
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.client.gui;

import io.github.tt432.yessteveskill.client.gui.YssHitEditorScreen;
import io.github.tt432.yessteveskill.client.gui.YssModelPreviewRenderer;
import io.github.tt432.yessteveskill.client.gui.layout.Layout;
import io.github.tt432.yessteveskill.client.gui.layout.Length;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import io.github.tt432.yessteveskill.client.gui.layout.VCol;
import io.github.tt432.yessteveskill.yss.attack.YssAttackProjectLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class YssBoneSelectScreen
extends Screen {
    private final YssHitEditorScreen parent;
    private final List<String> availableBones;
    private final Consumer<List<String>> callback;
    private final LinkedHashSet<String> selectedBones;
    private final List<String> initialSelected;
    @Nullable
    private final YssAttackProjectLoader.ModelGeometry geometry;
    private final Map<String, String> parentByBone;
    private final Map<String, List<String>> childrenByBone;
    private final List<BoneNode> treeRoots;
    private BoneList boneList;
    private Layout layout;
    private Rect boneListRect;
    private Rect previewRect;
    private Rect titleRect;
    private Rect countRect;
    @Nullable
    private String hoveredBone;

    public YssBoneSelectScreen(YssHitEditorScreen parent, List<String> availableBones, List<String> initialSelected, Consumer<List<String>> callback) {
        super((Component)Component.m_237113_((String)"\u9aa8\u9abc\u52fe\u9009"));
        this.parent = parent;
        this.availableBones = availableBones;
        this.callback = callback;
        this.selectedBones = new LinkedHashSet<String>(initialSelected);
        this.initialSelected = List.copyOf(initialSelected);
        this.geometry = parent.previewGeometry();
        this.parentByBone = new LinkedHashMap<String, String>();
        this.childrenByBone = new LinkedHashMap<String, List<String>>();
        for (String string : availableBones) {
            this.childrenByBone.put(string, new ArrayList());
        }
        if (this.geometry != null) {
            for (YssAttackProjectLoader.BoneGeometry boneGeometry : this.geometry.bones()) {
                String name = boneGeometry.name();
                String p = boneGeometry.parent();
                if (!availableBones.contains(name)) continue;
                this.parentByBone.put(name, p);
                if (p == null || !availableBones.contains(p)) continue;
                this.childrenByBone.computeIfAbsent(p, k -> new ArrayList()).add(name);
            }
        }
        this.treeRoots = this.buildTree();
    }

    private List<BoneNode> buildTree() {
        ArrayList<BoneNode> roots = new ArrayList<BoneNode>();
        for (String bone : this.availableBones) {
            String p = this.parentByBone.get(bone);
            if (p != null && this.availableBones.contains(p)) continue;
            roots.add(new BoneNode(bone, 0, this));
        }
        for (BoneNode root : roots) {
            root.populateChildren();
        }
        return roots;
    }

    private List<BoneNode> flatNodes() {
        ArrayList<BoneNode> flat = new ArrayList<BoneNode>();
        for (BoneNode root : this.treeRoots) {
            this.collectNodes(root, flat);
        }
        return flat;
    }

    private void collectNodes(BoneNode node, List<BoneNode> out) {
        out.add(node);
        for (BoneNode child : node.children) {
            this.collectNodes(child, out);
        }
    }

    protected void m_7856_() {
        this.layout = Layout.of(this, this.f_96547_);
        Length margin = Length.em(1.5);
        Length listWidth = Length.clamp(Length.px(160), Length.pct(25.0), Length.px(280));
        Length previewWidth = Length.clamp(Length.px(140), Length.pct(30.0), Length.px(300));
        VCol listCol = this.layout.vcol(margin, Length.em(3.5), listWidth, Length.px(0));
        this.boneListRect = listCol.remainingToBottom(Length.em(6.0));
        int itemHeight = this.layout.resolveY(Length.em(1.8));
        this.boneList = (BoneList)this.m_142416_((GuiEventListener)new BoneList(this.f_96541_, this.boneListRect.width(), this.boneListRect.height(), this.boneListRect.y(), this.boneListRect.bottom(), itemHeight));
        this.boneList.m_93507_(this.boneListRect.x());
        for (BoneNode node : this.flatNodes()) {
            this.boneList.addBoneEntry(new BoneEntry(node));
        }
        int previewTop = this.layout.resolveY(Length.em(3.5));
        int previewBottom = this.layout.fromBottom(Length.em(4.5));
        int listRight = this.layout.resolveX(margin) + this.layout.resolveX(listWidth);
        int previewLeft = listRight + this.layout.resolveX(Length.em(1.0));
        int previewW = Math.min(this.layout.resolveX(previewWidth), this.f_96543_ - previewLeft - this.layout.resolveX(margin));
        this.previewRect = new Rect(previewLeft, previewTop, previewW, Math.max(this.layout.resolveY(Length.em(16.0)), previewBottom - previewTop));
        Length btnW = Length.clamp(Length.px(60), Length.em(8.0), Length.px(90));
        Length btnH = Length.em(2.0);
        int btnW_px = this.layout.resolveX(btnW);
        int btnH_px = this.layout.resolveY(btnH);
        int backX = this.layout.resolveX(margin);
        int titleY = this.layout.resolveY(Length.em(1.5));
        Rect backRect = new Rect(backX, titleY, btnW_px, btnH_px);
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u8fd4\u56de"), b -> this.m_7379_()).m_252987_(backRect.x(), backRect.y(), backRect.width(), backRect.height()).m_253136_());
        int backGap = this.layout.resolveX(Length.em(0.5));
        this.titleRect = new Rect(backX + btnW_px + backGap, titleY, this.layout.resolveX(listWidth), this.layout.resolveY(Length.em(1.0)));
        int countY = this.layout.fromBottom(Length.em(4.5));
        this.countRect = this.layout.vcol(margin, Length.px(countY), listWidth, Length.px(0)).next(Length.em(1.0));
        int totalBtnW = btnW_px;
        int btnX = this.layout.fromRight(margin) - totalBtnW;
        int btnY = this.layout.fromBottom(Length.em(1.0)) - btnH_px;
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u786e\u5b9a"), b -> this.confirm()).m_252987_(btnX, btnY, btnW_px, btnH_px).m_253136_());
    }

    private void confirm() {
        this.callback.accept(new ArrayList<String>(this.selectedBones));
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)this.parent);
        }
    }

    private boolean hasChanges() {
        return !this.selectedBones.equals(new LinkedHashSet<String>(this.initialSelected));
    }

    public void m_7379_() {
        Minecraft minecraft = this.f_96541_;
        if (minecraft == null) {
            return;
        }
        if (this.hasChanges()) {
            minecraft.m_91152_((Screen)new ConfirmScreen(confirmed -> {
                if (confirmed) {
                    this.callback.accept(new ArrayList<String>(this.selectedBones));
                }
                minecraft.m_91152_((Screen)this.parent);
            }, (Component)Component.m_237113_((String)"\u4fdd\u5b58\u9aa8\u9abc\u9009\u62e9\uff1f"), (Component)Component.m_237113_((String)"\u9009\u62e9\u5df2\u4fee\u6539\uff0c\u662f\u5426\u4fdd\u5b58\uff1f")));
        } else {
            minecraft.m_91152_((Screen)this.parent);
        }
    }

    void setHoveredBone(@Nullable String bone) {
        this.hoveredBone = bone;
    }

    @Nullable
    String hoveredBone() {
        return this.hoveredBone;
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        this.hoveredBone = null;
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
        graphics.m_280430_(this.f_96547_, (Component)Component.m_237113_((String)"\u9aa8\u9abc\u52fe\u9009"), this.titleRect.x(), this.titleRect.y(), -1);
        graphics.m_280430_(this.f_96547_, (Component)Component.m_237113_((String)("\u5df2\u9009 " + this.selectedBones.size() + "/" + this.availableBones.size())), this.countRect.x(), this.countRect.y(), -3355444);
        if (this.geometry != null) {
            YssModelPreviewRenderer renderer = this.parent.modelPreview();
            renderer.setContent(this.geometry, this.parent.previewAnimatedModel(), this.parent.previewTextureLocation(), this.parent.previewWidthScale(), this.parent.previewHeightScale(), "\u9aa8\u9abc\u9884\u89c8", this.hoveredBone);
            renderer.render(graphics, this.previewRect);
        } else {
            graphics.m_280509_(this.previewRect.x(), this.previewRect.y(), this.previewRect.right(), this.previewRect.bottom(), -803726558);
            graphics.m_280137_(this.f_96547_, "\u65e0\u6a21\u578b\u51e0\u4f55", this.previewRect.x() + this.previewRect.width() / 2, this.previewRect.y() + this.previewRect.height() / 2, -5592406);
        }
    }

    public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.geometry != null && this.parent.modelPreview().drag(this.previewRect, mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.m_7979_(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.geometry != null && this.parent.modelPreview().scroll(this.previewRect, mouseX, mouseY, delta)) {
            return true;
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (this.geometry != null && this.parent.modelPreview().contains(this.previewRect, mouseX, mouseY) && (button == 0 || button == 1)) {
            return true;
        }
        return super.m_6375_(mouseX, mouseY, button);
    }

    static final class BoneNode {
        final String name;
        final int depth;
        final YssBoneSelectScreen screen;
        final List<BoneNode> children = new ArrayList<BoneNode>();

        BoneNode(String name, int depth, YssBoneSelectScreen screen) {
            this.name = name;
            this.depth = depth;
            this.screen = screen;
        }

        void populateChildren() {
            List<String> childNames = this.screen.childrenByBone.getOrDefault(this.name, List.of());
            for (String childName : childNames) {
                BoneNode child = new BoneNode(childName, this.depth + 1, this.screen);
                this.children.add(child);
                child.populateChildren();
            }
        }
    }

    class BoneList
    extends ObjectSelectionList<BoneEntry> {
        private static final int H_BAR_HEIGHT = 5;
        private int scrollX;
        private int contentWidth;
        private boolean hDragging;
        private double hDragStartMouseX;
        private int hDragStartScrollX;

        BoneList(Minecraft mc, int width, int height, int y0, int y1, int itemHeight) {
            super(mc, width, height, y0, y1, itemHeight);
            this.contentWidth = -1;
        }

        void addBoneEntry(BoneEntry entry) {
            this.m_7085_((AbstractSelectionList.Entry)entry);
        }

        public int m_5759_() {
            return this.f_93388_ - 10;
        }

        public int m_5756_() {
            return this.getRight() - 6;
        }

        int getScrollX() {
            return this.scrollX;
        }

        private int contentWidth() {
            if (this.contentWidth < 0) {
                int max = 0;
                for (BoneEntry entry : this.m_6702_()) {
                    max = Math.max(max, entry.requiredWidth());
                }
                this.contentWidth = max;
            }
            return this.contentWidth;
        }

        private int visibleWidth() {
            return this.m_5759_() - 8;
        }

        private int maxScrollX() {
            return Math.max(0, this.contentWidth() - this.visibleWidth());
        }

        private int trackLeft() {
            return this.getLeft();
        }

        private int trackRight() {
            return this.m_5756_() - 1;
        }

        private int trackTop() {
            return this.getBottom() + 2;
        }

        private int thumbWidth() {
            int trackW = this.trackRight() - this.trackLeft();
            return Mth.m_14045_((int)(trackW * this.visibleWidth() / Math.max(1, this.contentWidth())), (int)10, (int)trackW);
        }

        private int thumbLeft() {
            int max = this.maxScrollX();
            int trackW = this.trackRight() - this.trackLeft();
            int range = trackW - this.thumbWidth();
            return max <= 0 || range <= 0 ? this.trackLeft() : this.trackLeft() + range * this.scrollX / max;
        }

        private boolean overHScrollbar(double mouseX, double mouseY) {
            return this.maxScrollX() > 0 && mouseX >= (double)this.trackLeft() && mouseX < (double)this.trackRight() && mouseY >= (double)this.trackTop() && mouseY < (double)(this.trackTop() + 5);
        }

        public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            this.scrollX = Mth.m_14045_((int)this.scrollX, (int)0, (int)this.maxScrollX());
            super.m_88315_(graphics, mouseX, mouseY, partialTick);
            if (this.maxScrollX() > 0) {
                int top = this.trackTop();
                graphics.m_280509_(this.trackLeft(), top, this.trackRight(), top + 5, -16777216);
                int thumbX = this.thumbLeft();
                int thumbW = this.thumbWidth();
                graphics.m_280509_(thumbX, top, thumbX + thumbW, top + 5, -8355712);
                graphics.m_280509_(thumbX, top, thumbX + thumbW - 1, top + 5 - 1, -4144960);
            }
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (button == 0 && this.overHScrollbar(mouseX, mouseY)) {
                int range;
                this.hDragging = true;
                int thumbX = this.thumbLeft();
                int thumbW = this.thumbWidth();
                if ((mouseX < (double)thumbX || mouseX > (double)(thumbX + thumbW)) && (range = this.trackRight() - this.trackLeft() - thumbW) > 0) {
                    this.scrollX = Mth.m_14045_((int)((int)((mouseX - (double)this.trackLeft() - (double)thumbW / 2.0) * (double)this.maxScrollX() / (double)range)), (int)0, (int)this.maxScrollX());
                }
                this.hDragStartMouseX = mouseX;
                this.hDragStartScrollX = this.scrollX;
                return true;
            }
            return super.m_6375_(mouseX, mouseY, button);
        }

        public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (this.hDragging && button == 0) {
                int range = this.trackRight() - this.trackLeft() - this.thumbWidth();
                if (range > 0) {
                    double ratio = (double)this.maxScrollX() / (double)range;
                    this.scrollX = Mth.m_14045_((int)((int)((double)this.hDragStartScrollX + (mouseX - this.hDragStartMouseX) * ratio)), (int)0, (int)this.maxScrollX());
                }
                return true;
            }
            return super.m_7979_(mouseX, mouseY, button, dragX, dragY);
        }

        public boolean m_6348_(double mouseX, double mouseY, int button) {
            this.hDragging = false;
            return super.m_6348_(mouseX, mouseY, button);
        }

        public boolean m_6050_(double mouseX, double mouseY, double delta) {
            if (Screen.m_96638_() && this.maxScrollX() > 0 && this.m_5953_(mouseX, mouseY)) {
                this.scrollX = Mth.m_14045_((int)((int)((double)this.scrollX - delta * 12.0)), (int)0, (int)this.maxScrollX());
                return true;
            }
            return super.m_6050_(mouseX, mouseY, delta);
        }
    }

    class BoneEntry
    extends ObjectSelectionList.Entry<BoneEntry> {
        private final BoneNode node;

        BoneEntry(BoneNode node) {
            this.node = node;
        }

        String boneName() {
            return this.node.name;
        }

        public void m_6311_(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
            boolean selected = YssBoneSelectScreen.this.selectedBones.contains(this.node.name);
            int indent = this.node.depth * 8;
            String prefix = selected ? "[\u2713] " : "[ ] ";
            int color = hovered ? -154 : -1;
            int scrollX = YssBoneSelectScreen.this.boneList.getScrollX();
            graphics.m_280430_(YssBoneSelectScreen.this.f_96547_, (Component)Component.m_237113_((String)("  ".repeat(this.node.depth) + prefix + this.node.name)), left + 4 + indent - scrollX, top + 4, color);
            if (hovered) {
                YssBoneSelectScreen.this.setHoveredBone(this.node.name);
            }
        }

        int requiredWidth() {
            int indent = this.node.depth * 8;
            return 4 + indent + YssBoneSelectScreen.this.f_96547_.m_92895_("  ".repeat(this.node.depth) + "[\u2713] " + this.node.name);
        }

        public Component m_142172_() {
            return Component.m_237113_((String)this.node.name);
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (button == 0) {
                if (YssBoneSelectScreen.this.selectedBones.contains(this.node.name)) {
                    YssBoneSelectScreen.this.selectedBones.remove(this.node.name);
                } else {
                    YssBoneSelectScreen.this.selectedBones.add(this.node.name);
                }
                return true;
            }
            return false;
        }
    }
}

