/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapability
 *  com.elfmcys.ysm.client.gui.CustomGuiPlayerEntity
 *  com.elfmcys.ysm.geckolib3.core.builder.Animation
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel
 *  com.elfmcys.ysm.model.catalog.client.entry.CatalogModelMetadata
 *  com.elfmcys.ysm.model.catalog.client.entry.ClientCatalogEntry
 *  com.elfmcys.ysm.model.domain.Hash256
 *  com.elfmcys.ysm.model.resource.client.ModelRenderTarget
 *  com.elfmcys.ysm.model.service.ClientModelService
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.ObjectSelectionList
 *  net.minecraft.client.gui.components.ObjectSelectionList$Entry
 *  net.minecraft.client.gui.components.Renderable
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.client.gui;

import com.elfmcys.ysm.capability.PlayerAnimatableCapability;
import com.elfmcys.ysm.client.gui.CustomGuiPlayerEntity;
import com.elfmcys.ysm.geckolib3.core.builder.Animation;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel;
import com.elfmcys.ysm.model.catalog.client.entry.CatalogModelMetadata;
import com.elfmcys.ysm.model.catalog.client.entry.ClientCatalogEntry;
import com.elfmcys.ysm.model.domain.Hash256;
import com.elfmcys.ysm.model.resource.client.ModelRenderTarget;
import com.elfmcys.ysm.model.service.ClientModelService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tt432.yessteveskill.YesSteveSkill;
import io.github.tt432.yessteveskill.client.dnd.DragDropManager;
import io.github.tt432.yessteveskill.client.dnd.PositionedDropTarget;
import io.github.tt432.yessteveskill.client.gui.YssBoneSelectScreen;
import io.github.tt432.yessteveskill.client.gui.YssCheckbox;
import io.github.tt432.yessteveskill.client.gui.YssDropdownWidget;
import io.github.tt432.yessteveskill.client.gui.YssEntityOverridesScreen;
import io.github.tt432.yessteveskill.client.gui.YssEnumButtonGroup;
import io.github.tt432.yessteveskill.client.gui.YssModelPreviewRenderer;
import io.github.tt432.yessteveskill.client.gui.YssMovementSettingsScreen;
import io.github.tt432.yessteveskill.client.gui.YssNewAnimationScreen;
import io.github.tt432.yessteveskill.client.gui.YssPreviewAnimationLoader;
import io.github.tt432.yessteveskill.client.gui.YssProjectExportScreen;
import io.github.tt432.yessteveskill.client.gui.layout.HRow;
import io.github.tt432.yessteveskill.client.gui.layout.Layout;
import io.github.tt432.yessteveskill.client.gui.layout.Length;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import io.github.tt432.yessteveskill.client.gui.layout.VCol;
import io.github.tt432.yessteveskill.combat.config.CasterMoveDirection;
import io.github.tt432.yessteveskill.combat.config.CasterMoveType;
import io.github.tt432.yessteveskill.combat.config.DamageTiming;
import io.github.tt432.yessteveskill.combat.config.HitReset;
import io.github.tt432.yessteveskill.combat.config.KnockbackDirection;
import io.github.tt432.yessteveskill.editor.data.AnimationTimelineInfo;
import io.github.tt432.yessteveskill.editor.data.HitProjectEditingState;
import io.github.tt432.yessteveskill.editor.data.MutableAnimationHitConfig;
import io.github.tt432.yessteveskill.editor.data.MutableCasterMoveConfig;
import io.github.tt432.yessteveskill.editor.data.MutableEntityCombatStats;
import io.github.tt432.yessteveskill.editor.data.MutableHitReset;
import io.github.tt432.yessteveskill.editor.data.MutableHitSegment;
import io.github.tt432.yessteveskill.editor.data.MutableHitstopConfig;
import io.github.tt432.yessteveskill.editor.data.MutableHoverConfig;
import io.github.tt432.yessteveskill.editor.data.MutableInterruptConfig;
import io.github.tt432.yessteveskill.editor.data.MutableKnockbackConfig;
import io.github.tt432.yessteveskill.editor.data.MutableKnockupConfig;
import io.github.tt432.yessteveskill.editor.data.MutableTimeWindow;
import io.github.tt432.yessteveskill.ysm.YSMClientAccess;
import io.github.tt432.yessteveskill.ysm.YSMModelAssets;
import io.github.tt432.yessteveskill.yss.YssPathHelper;
import io.github.tt432.yessteveskill.yss.attack.YssAttackProjectLoader;
import io.github.tt432.yessteveskill.yss.project.YssProject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class YssHitEditorScreen
extends Screen
implements PositionedDropTarget {
    private static final int COLOR_SCALE_LINE = -7829368;
    private static final int COLOR_KEYFRAME_LINE = -12303292;
    private static final int COLOR_TRACK_BG = 0x20808080;
    private static final int COLOR_SEGMENT = -4173744;
    private static final int COLOR_SEGMENT_FULL = 1623216208;
    private static final int COLOR_MOVE = -11513664;
    private static final int COLOR_MOVE_FULL = 1615876288;
    private static final int COLOR_HOVER = -11485104;
    private static final int COLOR_SELECTED = -1;
    private static final int COLOR_PREVIEW_CURSOR = -11141121;
    private final YssProject project;
    private HitProjectEditingState editingState;
    private JsonObject animationsRoot;
    private boolean hasExternalAnimations;
    private Map<String, Animation> externalPreviewAnimations = Map.of();
    private Map<String, Animation> standaloneAnimations = Map.of();
    private List<YSMModelAssets.AnimationGroup> standaloneAnimationGroups = List.of();
    private float standaloneWidthScale = 1.0f;
    private float standaloneHeightScale = 1.0f;
    @Nullable
    private YSMModelAssets.ModelAssets ysmModelAssets;
    @Nullable
    private YssAttackProjectLoader.ModelGeometry previewGeometry;
    private String previewGeometrySource = "";
    private final YssModelPreviewRenderer modelPreview = new YssModelPreviewRenderer();
    @Nullable
    private ResourceLocation previewTextureId;
    private static long PREVIEW_TEXTURE_COUNTER = 0L;
    private final Map<String, Float> previewTimes = new HashMap<String, Float>();
    private AnimationList animationList;
    private EditBox animSearchBox;
    private String animSearchText = "";
    private Button saveButton;
    private Button backButton;
    @Nullable
    private String selectedAnim;
    @Nullable
    private Component statusMessage;
    private int statusColor = -1;
    private boolean pendingConfirm = false;
    private Button addAnimButton;
    private boolean dirty;
    private Button entityOverridesButton;
    private Button movementSettingsButton;
    @Nullable
    private List<String> availableBones;
    @Nullable
    private SelectedTrack selectedTrack;
    private int selectedIndex = -1;
    private DragMode dragMode = DragMode.NONE;
    private double dragLastX;
    private int timelineTop;
    private int timelineLeft;
    private int timelineWidth;
    private float timelineLength = 1.0f;
    private Rect timelineRect;
    private boolean previewCursorDragging;
    private int previewDragButton = -1;
    private Layout layout;
    private Rect searchRect;
    private Rect searchBoxRect;
    private Rect listRect;
    private Rect toolbarRect;
    private Rect animNameRect;
    private Rect timingRect;
    private Rect eoRect;
    private Rect saveRect;
    private Rect backRect;
    private Rect statusRect;
    private Rect dropHintRect;
    private Rect previewRect;
    @Nullable
    private YssCheckbox parallelCheckbox;
    private boolean parallelEnabled;
    @Nullable
    private CustomGuiPlayerEntity parallelEntity;
    private Rect formAreaRect;
    private int detailLeft;
    private int detailWidth;
    private int fieldXOffset;
    private int fieldW;
    private int formRowH;
    private int formGap;
    private int labelYOffset;
    private int widgetH;
    private int scaleHeight;
    private int trackHeight;
    private int trackBarHeight;
    private int trackBarYOffset;
    private int edgePixels;
    private int formScrollOffset;
    private int formMaxScroll;
    private final Map<AbstractWidget, int[]> formWidgetOrigins = new HashMap<AbstractWidget, int[]>();
    private boolean formSectionDisabled;
    private final Map<String, Object> disabledMechanismCache = new HashMap<String, Object>();
    private static final int LABEL_COLOR = 0xFFFFFF;
    private final List<AbstractWidget> formWidgets = new ArrayList<AbstractWidget>();
    private final List<FormLabel> formLabels = new ArrayList<FormLabel>();
    @Nullable
    private EditBox startTimeBox;
    @Nullable
    private EditBox endTimeBox;
    private static final int TYPE_UNKNOWN = 0;
    private static final int TYPE_MODEL = 1;
    private static final int TYPE_ANIMATION = 2;
    private String cachedHoverPath;
    private int cachedHoverType = 0;

    public YssHitEditorScreen(YssProject project) {
        super((Component)Component.m_237113_((String)"YSS \u7f16\u8f91"));
        this.project = project;
    }

    protected void m_7856_() {
        AnimationEntry selectedEntry;
        if (this.editingState == null) {
            this.editingState = HitProjectEditingState.load(this.project.directory());
        }
        if (this.animationsRoot == null) {
            this.hasExternalAnimations = Files.isRegularFile(this.project.directory().resolve("animations.json"), new LinkOption[0]);
            this.animationsRoot = this.readAnimationsJson(this.project.directory());
            this.reloadExternalPreviewAnimations();
        }
        this.refreshPreviewGeometry();
        if (this.availableBones == null) {
            this.availableBones = this.resolveAvailableBones();
        }
        this.layout = Layout.of(this, this.f_96547_);
        Length margin = Length.em(2.0);
        Length animListWidth = Length.clamp(Length.px(110), Length.pct(22.0), Length.px(220));
        Length listTop = Length.em(2.5);
        Length listBottomGap = Length.em(2.0);
        Length detailLeftPadding = Length.em(1.5);
        Length searchHeight = Length.em(1.75);
        Length searchGap = Length.em(0.25);
        this.scaleHeight = this.layout.resolveY(Length.em(1.5));
        this.trackHeight = this.layout.resolveY(Length.em(2.25));
        this.trackBarHeight = this.layout.resolveY(Length.em(1.5));
        this.trackBarYOffset = this.layout.resolveY(Length.em(0.625));
        this.edgePixels = this.layout.resolveY(Length.em(0.625));
        int minTimelineWidth = this.layout.resolveX(Length.clamp(Length.px(80), Length.pct(20.0), Length.px(200)));
        int timelineHeight = this.scaleHeight + this.layout.resolveY(Length.em(0.25)) + 3 * this.trackHeight;
        this.fieldXOffset = this.layout.resolveX(Length.em(7.5));
        this.fieldW = this.layout.resolveX(Length.clamp(Length.px(100), Length.em(15.0), Length.px(220)));
        this.formRowH = this.layout.resolveY(Length.em(2.5));
        this.formGap = this.layout.resolveY(Length.em(1.0));
        this.labelYOffset = this.layout.resolveY(Length.em(0.625));
        this.widgetH = this.layout.resolveY(Length.em(2.25));
        VCol leftCol = this.layout.vcol(margin, listTop, animListWidth, searchGap);
        this.searchRect = leftCol.next(searchHeight);
        int addBtnWidth = this.layout.resolveX(Length.em(2.5));
        int searchBtnGap = this.layout.resolveX(Length.em(0.25));
        this.searchBoxRect = new Rect(this.searchRect.x(), this.searchRect.y(), this.searchRect.width() - addBtnWidth - searchBtnGap, this.searchRect.height());
        this.animSearchBox = (EditBox)this.m_142416_((GuiEventListener)new EditBox(this.f_96547_, this.searchBoxRect.x(), this.searchBoxRect.y(), this.searchBoxRect.width(), this.searchBoxRect.height(), (Component)Component.m_237113_((String)"\u641c\u7d22")));
        this.animSearchBox.m_94199_(64);
        this.animSearchBox.m_94144_(this.animSearchText);
        this.animSearchBox.m_94151_(s -> {
            this.animSearchText = s.trim();
            this.refreshAnimationList();
        });
        this.addAnimButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"+"), button -> this.openNewAnimation()).m_252987_(this.searchRect.right() - addBtnWidth, this.searchRect.y(), addBtnWidth, this.searchRect.height()).m_253136_());
        this.listRect = leftCol.remainingToBottom(listBottomGap);
        int listItemHeight = this.layout.resolveY(Length.em(2.25));
        this.animationList = (AnimationList)this.m_142416_((GuiEventListener)new AnimationList(this.f_96541_, this.listRect.width(), this.listRect.height(), this.listRect.y(), this.listRect.bottom(), listItemHeight));
        this.animationList.m_93507_(this.listRect.x());
        AnimationEntry firstEntry = this.animationList.replaceAnimations(this.filteredAnimationNames());
        AnimationEntry animationEntry = selectedEntry = this.selectedAnim == null ? null : this.animationList.findEntry(this.selectedAnim);
        if (selectedEntry == null) {
            String configuredName;
            Iterator<String> iterator = this.editingState.getAnimations().keySet().iterator();
            while (iterator.hasNext() && (selectedEntry = this.animationList.findEntry(configuredName = iterator.next())) == null) {
            }
        }
        if (selectedEntry != null) {
            this.selectAnimation(selectedEntry);
        } else if (firstEntry != null) {
            this.selectAnimation(firstEntry);
        } else {
            this.selectedAnim = null;
        }
        this.detailLeft = this.layout.resolveX(margin) + this.layout.resolveX(animListWidth) + this.layout.resolveX(detailLeftPadding);
        int fullDetailWidth = this.f_96543_ - this.detailLeft - this.layout.resolveX(margin);
        int previewGap = this.layout.resolveX(Length.em(1.0));
        int previewWidth = Math.max(this.layout.resolveX(Length.em(12.0)), Math.min(this.layout.resolveX(Length.em(26.0)), fullDetailWidth * 30 / 100));
        int minimumDetailWidth = Math.max(minTimelineWidth, this.layout.resolveX(Length.em(17.0)));
        previewWidth = Math.min(previewWidth, Math.max(this.layout.resolveX(Length.em(10.0)), fullDetailWidth - minimumDetailWidth - previewGap));
        this.detailWidth = Math.max(minTimelineWidth, fullDetailWidth - previewWidth - previewGap);
        int previewTop = this.layout.resolveY(listTop);
        int previewBottom = this.layout.fromBottom(Length.em(5.0));
        this.previewRect = new Rect(this.detailLeft + this.detailWidth + previewGap, previewTop, previewWidth, Math.max(this.layout.resolveY(Length.em(16.0)), previewBottom - previewTop));
        VCol detailCol = this.layout.vcol(Length.px(this.detailLeft), listTop, Length.px(this.detailWidth), Length.px(0));
        this.toolbarRect = detailCol.next(Length.em(2.5));
        this.timelineRect = detailCol.next(Length.px(timelineHeight));
        this.timelineLeft = this.timelineRect.x();
        this.timelineTop = this.timelineRect.y();
        this.timelineWidth = Math.max(this.timelineRect.width(), minTimelineWidth);
        int toolbarGap = this.layout.resolveX(Length.em(1.0));
        int singleBtnWidth = Math.max(60, Math.min(120, this.toolbarRect.width() / 4));
        int eoWidth = singleBtnWidth * 2 + toolbarGap / 2;
        this.eoRect = new Rect(this.toolbarRect.right() - eoWidth, this.toolbarRect.y(), eoWidth, this.toolbarRect.height());
        int infoWidth = this.toolbarRect.width() - eoWidth - toolbarGap;
        this.animNameRect = new Rect(this.toolbarRect.x(), this.toolbarRect.y(), infoWidth * 2 / 3, this.toolbarRect.height());
        this.timingRect = new Rect(this.animNameRect.right(), this.toolbarRect.y(), infoWidth - infoWidth * 2 / 3, this.toolbarRect.height());
        this.movementSettingsButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u5168\u5c40\u8bbe\u7f6e"), b -> this.openMovementSettings()).m_252987_(this.eoRect.x(), this.eoRect.y(), singleBtnWidth, this.eoRect.height()).m_253136_());
        this.entityOverridesButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u5b9e\u4f53\u8986\u76d6"), b -> this.openEntityOverrides()).m_252987_(this.eoRect.right() - singleBtnWidth, this.eoRect.y(), singleBtnWidth, this.eoRect.height()).m_253136_());
        int parallelCbY = this.previewRect.bottom() + this.layout.resolveY(Length.em(0.4));
        this.parallelCheckbox = (YssCheckbox)this.m_142416_((GuiEventListener)new YssCheckbox(this.previewRect.x(), parallelCbY, 14, 14, this.parallelEnabled, value -> {
            this.parallelEnabled = value;
        }));
        this.formAreaRect = new Rect(this.detailLeft, this.timelineRect.bottom() + this.formGap, this.detailWidth, Math.max(0, this.layout.fromBottom(Length.em(5.5)) - this.timelineRect.bottom() - this.formGap));
        Length rightBtnW = Length.clamp(Length.px(60), Length.em(10.0), Length.px(100));
        Length rightBtnGap = Length.em(0.5);
        int rightBtnY = this.layout.fromBottom(Length.em(1.0)) - this.layout.resolveY(Length.em(2.5));
        HRow rightBtnRow = this.layout.hrow(Length.px(this.detailLeft), Length.px(rightBtnY), Length.em(2.5), rightBtnGap);
        this.saveRect = rightBtnRow.next(rightBtnW);
        this.backRect = rightBtnRow.next(rightBtnW);
        this.saveButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u4fdd\u5b58"), button -> this.save()).m_252987_(this.saveRect.x(), this.saveRect.y(), this.saveRect.width(), this.saveRect.height()).m_253136_());
        this.backButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"\u8fd4\u56de"), button -> this.back()).m_252987_(this.backRect.x(), this.backRect.y(), this.backRect.width(), this.backRect.height()).m_253136_());
        this.statusRect = new Rect(this.detailLeft, this.layout.fromBottom(Length.em(5.5)), this.detailWidth, 0);
        this.dropHintRect = new Rect(this.layout.resolveX(margin), this.layout.fromBottom(Length.em(4.0)), 0, 0);
        this.rebuildForm();
        this.ensureParallelEntity();
    }

    private void selectAnimation(AnimationEntry entry) {
        this.selectedAnim = entry.animName;
        this.animationList.m_6987_((AbstractSelectionList.Entry)entry);
        this.formScrollOffset = 0;
        this.clearPendingConfirm();
        this.rebuildForm();
    }

    private JsonObject readAnimationsJson(Path projectDir) {
        Path path = projectDir.resolve("animations.json");
        if (!Files.exists(path, new LinkOption[0])) {
            return new JsonObject();
        }
        try {
            String content = Files.readString((Path)path, (Charset)StandardCharsets.UTF_8);
            return JsonParser.parseString((String)content).getAsJsonObject();
        }
        catch (Exception e) {
            return new JsonObject();
        }
    }

    private void reloadExternalPreviewAnimations() {
        if (!this.hasExternalAnimations || this.animationsRoot == null) {
            this.externalPreviewAnimations = Map.of();
            return;
        }
        try {
            this.externalPreviewAnimations = YssPreviewAnimationLoader.load(this.animationsRoot);
        }
        catch (RuntimeException error) {
            this.externalPreviewAnimations = Map.of();
            this.statusMessage = Component.m_237113_((String)("animations.json \u65e0\u6cd5\u7528\u4e8e\u52a8\u4f5c\u9884\u89c8\uff1a" + error.getMessage()));
            this.statusColor = -43691;
        }
    }

    private void refreshPreviewGeometry() {
        Path yssMain = this.project.directory().resolve("main.json");
        if (Files.isRegularFile(yssMain, new LinkOption[0])) {
            this.releasePreviewTexture();
            try {
                this.previewGeometry = YssAttackProjectLoader.ModelGeometry.fromJson(Files.readAllBytes(yssMain), this.project.modelId());
                this.previewGeometrySource = "YSS main.json";
                this.availableBones = this.resolveAvailableBones();
            }
            catch (Exception error) {
                this.previewGeometry = null;
                this.availableBones = List.of();
                this.statusMessage = Component.m_237113_((String)("main.json \u65e0\u6cd5\u7528\u4e8e\u6a21\u578b\u9884\u89c8\uff1a" + error.getMessage()));
                this.statusColor = -43691;
            }
            this.requestYsmAssets();
            return;
        }
        this.requestYsmAssets();
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        if (capability != null) {
            YSMModelAssets.ModelAssets assets = YesSteveSkill.getYsmAccess().getModelAssetsNow(capability);
            if (assets != null) {
                this.applyYsmAssets(assets);
            }
            return;
        }
        this.requestStandaloneAssets();
    }

    private void requestStandaloneAssets() {
        YesSteveSkill.getYsmAccess().requestModelAssets(this.project.modelId()).thenAccept(assets -> Minecraft.m_91087_().execute(() -> {
            if (assets.modelHash() == null) {
                this.previewGeometry = null;
                this.previewGeometrySource = "YSM \u6a21\u578b\u672a\u627e\u5230";
                this.availableBones = List.of();
                this.standaloneAnimations = Map.of();
                this.standaloneAnimationGroups = List.of();
                return;
            }
            try {
                this.previewGeometry = assets.mainJson() != null ? YssAttackProjectLoader.ModelGeometry.fromJson(assets.mainJson(), this.project.modelId()) : (assets.mainModel() != null ? YssAttackProjectLoader.ModelGeometry.from(assets.mainModel()) : null);
                this.previewGeometrySource = "YSM \u6a21\u578b\u8d44\u4ea7";
                this.availableBones = this.resolveAvailableBones();
                this.standaloneAnimations = assets.animations();
                this.standaloneAnimationGroups = assets.animationGroups();
                this.standaloneWidthScale = assets.widthScale();
                this.standaloneHeightScale = assets.heightScale();
                if (assets.texturePng() != null) {
                    this.acquirePreviewTexture(assets.texturePng());
                }
            }
            catch (Exception error) {
                this.previewGeometry = null;
                this.previewGeometrySource = "YSM \u6a21\u578b\u8d44\u4ea7\uff08\u8bfb\u53d6\u5931\u8d25\uff09";
                this.availableBones = List.of();
                this.standaloneAnimations = Map.of();
                this.standaloneAnimationGroups = List.of();
                this.statusMessage = Component.m_237113_((String)("YSM \u51e0\u4f55\u65e0\u6cd5\u7528\u4e8e\u6a21\u578b\u9884\u89c8\uff1a" + error.getMessage()));
                this.statusColor = -43691;
            }
        }));
    }

    private void requestYsmAssets() {
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        if (capability == null) {
            return;
        }
        YesSteveSkill.getYsmAccess().requestModelAssets(capability).thenAccept(assets -> Minecraft.m_91087_().execute(() -> {
            this.ysmModelAssets = assets;
            if (Files.isRegularFile(this.project.directory().resolve("main.json"), new LinkOption[0])) {
                return;
            }
            PlayerAnimatableCapability current = this.currentProjectCapability();
            if (current == null) {
                this.requestStandaloneAssets();
                return;
            }
            this.applyYsmAssets((YSMModelAssets.ModelAssets)((Object)assets));
        }));
    }

    private void applyYsmAssets(YSMModelAssets.ModelAssets assets) {
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        if (capability == null || assets.modelHash() == null || capability.getModelRenderTarget() == null || !assets.modelHash().equals((Object)capability.getModelRenderTarget().modelHash())) {
            return;
        }
        try {
            this.previewGeometry = assets.mainJson() != null ? YssAttackProjectLoader.ModelGeometry.fromJson(assets.mainJson(), this.project.modelId()) : (assets.mainModel() != null ? YssAttackProjectLoader.ModelGeometry.from(assets.mainModel()) : null);
            this.previewGeometrySource = "YSM \u5f53\u524d\u6a21\u578b";
            this.availableBones = this.resolveAvailableBones();
        }
        catch (Exception error) {
            this.previewGeometry = null;
            this.previewGeometrySource = "YSM \u5f53\u524d\u6a21\u578b\uff08\u8bfb\u53d6\u5931\u8d25\uff09";
            this.availableBones = this.resolveAvailableBones();
            this.statusMessage = Component.m_237113_((String)("YSM \u51e0\u4f55\u65e0\u6cd5\u7528\u4e8e\u6a21\u578b\u9884\u89c8\uff1a" + error.getMessage()));
            this.statusColor = -43691;
        }
    }

    private List<String> resolveAvailableBones() {
        if (this.previewGeometry != null) {
            return this.previewGeometry.bones().stream().map(YssAttackProjectLoader.BoneGeometry::name).toList();
        }
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        if (capability == null || capability.getLoadedGeoModel() == null) {
            return List.of();
        }
        return capability.getLoadedGeoModel().getSortedBones().stream().map(AnimatedGeoBone::getName).toList();
    }

    @Nullable
    Hash256 previewModelHash() {
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        if (capability == null || capability.getModelRenderTarget() == null) {
            return null;
        }
        return capability.getModelRenderTarget().modelHash();
    }

    @Nullable
    String previewTextureName() {
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        return capability == null ? null : capability.getTextureName();
    }

    @Nullable
    AnimatedGeoModel previewAnimatedModel() {
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        return capability == null ? null : capability.getLoadedGeoModel();
    }

    float previewWidthScale() {
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        if (capability != null && capability.getModelRenderTarget() != null) {
            return capability.getModelRenderTarget().info().getPlayerSettings().widthScale();
        }
        return this.standaloneWidthScale;
    }

    float previewHeightScale() {
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        if (capability != null && capability.getModelRenderTarget() != null) {
            return capability.getModelRenderTarget().info().getPlayerSettings().heightScale();
        }
        return this.standaloneHeightScale;
    }

    void acquirePreviewTexture(byte[] pngBytes) {
        this.releasePreviewTexture();
        if (pngBytes == null) {
            return;
        }
        try {
            NativeImage image = NativeImage.m_85058_((InputStream)new ByteArrayInputStream(pngBytes));
            ResourceLocation id = new ResourceLocation("yessteveskill", "preview_tex_" + ++PREVIEW_TEXTURE_COUNTER);
            Minecraft.m_91087_().m_91097_().m_118495_(id, (AbstractTexture)new DynamicTexture(image));
            this.previewTextureId = id;
        }
        catch (IOException e) {
            this.previewTextureId = null;
        }
    }

    void releasePreviewTexture() {
        if (this.previewTextureId != null) {
            Minecraft.m_91087_().m_91097_().m_118513_(this.previewTextureId);
            this.previewTextureId = null;
        }
    }

    @Nullable
    ResourceLocation previewTextureLocation() {
        if (this.previewTextureId != null) {
            return this.previewTextureId;
        }
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        if (capability != null) {
            try {
                return capability.getTextureLocation();
            }
            catch (RuntimeException ignored) {
                return null;
            }
        }
        return null;
    }

    @Nullable
    YssAttackProjectLoader.ModelGeometry previewGeometry() {
        return this.previewGeometry;
    }

    YssModelPreviewRenderer modelPreview() {
        return this.modelPreview;
    }

    public void m_7861_() {
        this.releaseParallelEntity();
        this.releasePreviewTexture();
        this.modelPreview.reset();
        super.m_7861_();
    }

    private void save() {
        if (!this.pendingConfirm) {
            List<String> errors = this.validateConfig();
            if (!errors.isEmpty()) {
                String first = errors.get(0);
                this.statusMessage = Component.m_237113_((String)("\u6821\u9a8c\u5931\u8d25\uff1a" + first + (String)(errors.size() > 1 ? " (\u5171" + errors.size() + "\u5904)" : "")));
                this.statusColor = -43691;
                this.sendPlayerMessage(this.statusMessage);
                return;
            }
            this.pendingConfirm = true;
            this.statusMessage = Component.m_237113_((String)"\u518d\u6b21\u70b9\u51fb\u300c\u4fdd\u5b58\u300d\u786e\u8ba4\u8986\u76d6 hit.json");
            this.statusColor = -171;
            if (this.saveButton != null) {
                this.saveButton.m_93666_((Component)Component.m_237113_((String)"\u786e\u8ba4\u4fdd\u5b58?"));
            }
            return;
        }
        this.doSave();
    }

    private void doSave() {
        this.pendingConfirm = false;
        this.dirty = false;
        if (this.saveButton != null) {
            this.saveButton.m_93666_((Component)Component.m_237113_((String)"\u4fdd\u5b58"));
        }
        try {
            this.editingState.save(this.project.directory());
            this.statusMessage = Component.m_237113_((String)"\u4fdd\u5b58\u6210\u529f");
            this.statusColor = -11141291;
            this.sendPlayerMessage(this.statusMessage);
        }
        catch (Exception e) {
            String message = e.getMessage() == null ? "\u672a\u77e5\u9519\u8bef" : e.getMessage();
            this.statusMessage = Component.m_237113_((String)("\u4fdd\u5b58\u5931\u8d25\uff1a" + message));
            this.statusColor = -43691;
            this.sendPlayerMessage(this.statusMessage);
        }
    }

    private List<String> validateConfig() {
        ArrayList<String> errors = new ArrayList<String>();
        for (Map.Entry<String, MutableAnimationHitConfig> entry : this.editingState.getAnimations().entrySet()) {
            String animName = entry.getKey();
            MutableAnimationHitConfig cfg = entry.getValue();
            if (cfg.getHover() != null) {
                try {
                    cfg.getHover().toRecord();
                }
                catch (Exception ex) {
                    errors.add(animName + " \u6ede\u7a7a: " + ex.getMessage());
                }
            }
            List<MutableHitSegment> segments = cfg.getSegments();
            for (int i = 0; i < segments.size(); ++i) {
                try {
                    segments.get(i).toRecord();
                    continue;
                }
                catch (Exception ex) {
                    errors.add(animName + " \u6bb5#" + i + ": " + ex.getMessage());
                }
            }
            List<MutableCasterMoveConfig> moves = cfg.getCasterMoves();
            for (int i = 0; i < moves.size(); ++i) {
                try {
                    moves.get(i).toRecord();
                    continue;
                }
                catch (Exception ex) {
                    errors.add(animName + " \u4f4d\u79fb#" + i + ": " + ex.getMessage());
                }
            }
        }
        for (Map.Entry<String, Object> entry : this.editingState.getEntityOverrides().entrySet()) {
            try {
                ((MutableEntityCombatStats)entry.getValue()).toRecord();
            }
            catch (Exception ex) {
                errors.add("\u5b9e\u4f53\u8986\u76d6 " + entry.getKey() + ": " + ex.getMessage());
            }
        }
        return errors;
    }

    private void clearPendingConfirm() {
        if (this.pendingConfirm) {
            this.pendingConfirm = false;
            if (this.saveButton != null) {
                this.saveButton.m_93666_((Component)Component.m_237113_((String)"\u4fdd\u5b58"));
            }
        }
    }

    private void openNewAnimation() {
        this.clearPendingConfirm();
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)new YssNewAnimationScreen(this));
        }
    }

    private void requestDeleteAnimation(String animName) {
        this.clearPendingConfirm();
        Minecraft minecraft = this.f_96541_;
        if (minecraft == null) {
            return;
        }
        minecraft.m_91152_((Screen)new ConfirmScreen(confirmed -> {
            if (confirmed) {
                this.doDeleteAnim(animName);
            }
            minecraft.m_91152_((Screen)this);
        }, (Component)Component.m_237113_((String)"\u5220\u9664\u52a8\u753b\uff1f"), (Component)Component.m_237113_((String)("\u786e\u5b9a\u5220\u9664\u52a8\u753b " + animName + "\uff1f"))));
    }

    private void doDeleteAnim(String animName) {
        this.editingState.getAnimations().remove(animName);
        this.markDirty();
        this.statusMessage = Component.m_237113_((String)("\u5df2\u5220\u9664\u52a8\u753b " + animName));
        this.statusColor = -11141291;
        this.sendPlayerMessage(this.statusMessage);
        if (animName.equals(this.selectedAnim)) {
            this.selectedAnim = null;
        }
        this.clearSelection();
        this.refreshAnimationList();
    }

    private void refreshAnimationList() {
        this.animationList.replaceAnimations(this.filteredAnimationNames());
        if (this.selectedAnim != null) {
            AnimationEntry existing = this.animationList.findEntry(this.selectedAnim);
            if (existing == null) {
                this.selectedAnim = null;
            } else {
                this.animationList.m_6987_((AbstractSelectionList.Entry)existing);
            }
        }
    }

    @Nullable
    private Hash256 findProjectModelHash() {
        String normalized = YssPathHelper.normalizeModelId(this.project.modelId());
        ClientModelService service = ClientModelService.instance();
        for (ClientCatalogEntry entry : service.catalog().models().values()) {
            if (!normalized.equalsIgnoreCase(YssPathHelper.normalizeModelId(entry.displayPath()))) continue;
            return entry.modelHash();
        }
        return null;
    }

    private void ensureParallelEntity() {
        if (this.parallelEntity != null) {
            return;
        }
        Hash256 hash = this.findProjectModelHash();
        if (hash == null) {
            return;
        }
        ClientModelService service = ClientModelService.instance();
        String textureName = service.catalog().find(hash).map(CatalogModelMetadata::from).map(CatalogModelMetadata::defaultTexture).orElse("");
        CustomGuiPlayerEntity entity = new CustomGuiPlayerEntity();
        entity.updateModelAndTexture(hash, textureName);
        this.parallelEntity = entity;
    }

    private void releaseParallelEntity() {
        if (this.parallelEntity != null) {
            this.parallelEntity.reset();
            this.parallelEntity = null;
        }
    }

    private List<String> filteredAnimationNames() {
        String filter = this.animSearchText;
        String lower = filter.toLowerCase(Locale.ROOT);
        return this.editingState.getAnimations().keySet().stream().filter(name -> filter.isEmpty() || name.toLowerCase(Locale.ROOT).contains(lower)).sorted().toList();
    }

    List<String> candidateAnimationNames() {
        return this.candidateAnimationGroups().stream().flatMap(group -> group.animationNames().stream()).toList();
    }

    List<YSMModelAssets.AnimationGroup> candidateAnimationGroups() {
        Set<String> configured = this.editingState.getAnimations().keySet();
        List<YSMModelAssets.AnimationGroup> sourceGroups = this.hasExternalAnimations ? List.of((Object)((Object)new YSMModelAssets.AnimationGroup("animations.json", this.externalAnimationNames()))) : (this.ysmModelAssets != null && !this.ysmModelAssets.animationGroups().isEmpty() ? this.ysmModelAssets.animationGroups() : (!this.standaloneAnimationGroups.isEmpty() ? this.standaloneAnimationGroups : List.of((Object)((Object)new YSMModelAssets.AnimationGroup("YSM \u9ed8\u8ba4\u52a8\u753b", this.currentYsmAnimationNames())))));
        ArrayList<YSMModelAssets.AnimationGroup> result = new ArrayList<YSMModelAssets.AnimationGroup>();
        for (YSMModelAssets.AnimationGroup group : sourceGroups) {
            List candidates = group.animationNames().stream().filter(name -> !configured.contains(name)).sorted().toList();
            if (candidates.isEmpty()) continue;
            result.add(new YSMModelAssets.AnimationGroup(group.fileName(), candidates));
        }
        return List.copyOf(result);
    }

    private List<String> externalAnimationNames() {
        if (this.animationsRoot == null) {
            return List.of();
        }
        JsonElement element = this.animationsRoot.get("animations");
        if (!(element instanceof JsonObject)) {
            return List.of();
        }
        JsonObject animations = (JsonObject)element;
        return animations.keySet().stream().filter(name -> !name.isBlank()).sorted().toList();
    }

    private List<String> currentYsmAnimationNames() {
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        return capability == null ? List.of() : YesSteveSkill.getYsmAccess().getAnimationNames(capability);
    }

    @Nullable
    private PlayerAnimatableCapability currentProjectCapability() {
        String projectModelId;
        Minecraft minecraft = this.f_96541_;
        if (minecraft == null || minecraft.f_91074_ == null) {
            return null;
        }
        YSMClientAccess access = YesSteveSkill.getYsmAccess();
        PlayerAnimatableCapability capability = access.getCapability((AbstractClientPlayer)minecraft.f_91074_);
        if (capability == null) {
            return null;
        }
        String activeModelId = YssPathHelper.normalizeModelId(access.getModelId(capability));
        if (!activeModelId.equalsIgnoreCase(projectModelId = YssPathHelper.normalizeModelId(this.project.modelId()))) {
            return null;
        }
        ModelRenderTarget renderTarget = capability.getModelRenderTarget();
        if (renderTarget == null) {
            return null;
        }
        Hash256 capHash = capability.getModelHash();
        if (capHash != null && !capHash.equals((Object)renderTarget.modelHash())) {
            return null;
        }
        return capability;
    }

    private AnimationTimelineInfo animationTimelineInfo(String animationName) {
        if (this.hasExternalAnimations) {
            return AnimationTimelineInfo.from(this.animationsRoot, animationName);
        }
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        if (capability != null && this.standaloneAnimations.isEmpty()) {
            Animation animation = YesSteveSkill.getYsmAccess().getAnimation(capability, animationName);
            return animation == null ? new AnimationTimelineInfo(0.0f, List.of()) : AnimationTimelineInfo.from(animation);
        }
        Animation animation = this.previewAnimation(animationName);
        return animation == null ? new AnimationTimelineInfo(0.0f, List.of()) : AnimationTimelineInfo.from(animation);
    }

    @Nullable
    private Animation previewAnimation(@Nullable String animationName) {
        if (animationName == null || animationName.isBlank()) {
            return null;
        }
        if (this.hasExternalAnimations) {
            return this.externalPreviewAnimations.get(animationName);
        }
        if (!this.standaloneAnimations.isEmpty()) {
            Animation animation = this.standaloneAnimations.get(animationName);
            if (animation != null) {
                return animation;
            }
            return this.defaultPreviewAnimation(animationName);
        }
        PlayerAnimatableCapability capability = this.currentProjectCapability();
        return capability == null ? null : YesSteveSkill.getYsmAccess().getAnimation(capability, animationName);
    }

    @Nullable
    private Animation defaultPreviewAnimation(String animationName) {
        ClientModelService service = ClientModelService.instance();
        ModelRenderTarget target = service.defaultRenderTarget();
        if (target == null || target.playerResources() == null) {
            return null;
        }
        return target.playerResources().animations().get((Object)animationName);
    }

    private float previewTime() {
        return this.selectedAnim == null ? 0.0f : this.previewTimes.getOrDefault(this.selectedAnim, Float.valueOf(0.0f)).floatValue();
    }

    private void setPreviewTime(float value) {
        if (this.selectedAnim == null) {
            return;
        }
        this.previewTimes.put(this.selectedAnim, Float.valueOf(Mth.m_14036_((float)value, (float)0.0f, (float)Math.max(0.0f, this.timelineLength))));
    }

    boolean addAnimation(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        if (this.editingState.getAnimations().containsKey(name)) {
            return false;
        }
        this.editingState.getAnimations().put(name, new MutableAnimationHitConfig());
        this.markDirty();
        this.selectedAnim = name;
        return true;
    }

    public void m_7379_() {
        this.back();
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 83 && (modifiers & 2) != 0) {
            List<String> errors;
            this.pendingConfirm = false;
            if (this.saveButton != null) {
                this.saveButton.m_93666_((Component)Component.m_237113_((String)"\u4fdd\u5b58"));
            }
            if (!(errors = this.validateConfig()).isEmpty()) {
                String first = errors.get(0);
                this.statusMessage = Component.m_237113_((String)("\u6821\u9a8c\u5931\u8d25\uff1a" + first + (String)(errors.size() > 1 ? " (\u5171" + errors.size() + "\u5904)" : "")));
                this.statusColor = -43691;
                this.sendPlayerMessage(this.statusMessage);
                return true;
            }
            this.doSave();
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    private void back() {
        this.clearPendingConfirm();
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)new YssProjectExportScreen());
        }
    }

    HitProjectEditingState editingState() {
        return this.editingState;
    }

    private void openMovementSettings() {
        this.clearPendingConfirm();
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)new YssMovementSettingsScreen(this));
        }
    }

    private void openEntityOverrides() {
        this.clearPendingConfirm();
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)new YssEntityOverridesScreen(this));
        }
    }

    private void openBoneSelect(MutableHitSegment seg) {
        if (this.availableBones == null || this.availableBones.isEmpty()) {
            this.statusMessage = Component.m_237113_((String)"\u65e0\u53ef\u7528\u9aa8\u9abc\uff0c\u8bf7\u5148\u5bfc\u5165 main.json");
            this.statusColor = -171;
            this.sendPlayerMessage(this.statusMessage);
            return;
        }
        this.clearPendingConfirm();
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null) {
            minecraft.m_91152_((Screen)new YssBoneSelectScreen(this, this.availableBones, new ArrayList<String>(seg.getBones()), selected -> {
                seg.setBones((List<String>)selected);
                this.markDirty();
                this.rebuildForm();
            }));
        }
    }

    private void sendPlayerMessage(Component message) {
        Minecraft minecraft = this.f_96541_;
        if (minecraft != null && minecraft.f_91074_ != null) {
            minecraft.f_91074_.m_213846_(message);
        }
    }

    public void m_7400_(List<Path> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        boolean mainImported = false;
        boolean animationsImported = false;
        for (Path file : files) {
            String name;
            if (file == null) continue;
            String string = name = file.getFileName() == null ? "" : file.getFileName().toString();
            if ("main.json".equals(name)) {
                if (!this.importAs(file, true)) {
                    this.sendPlayerMessage(this.statusMessage);
                    return;
                }
                mainImported = true;
                continue;
            }
            if (!"animations.json".equals(name)) continue;
            if (!this.importAs(file, false)) {
                this.sendPlayerMessage(this.statusMessage);
                return;
            }
            animationsImported = true;
        }
        if (mainImported && animationsImported) {
            this.statusMessage = Component.m_237113_((String)"\u5df2\u5bfc\u5165 main.json + animations.json");
            this.statusColor = -11141291;
        } else if (mainImported) {
            this.statusMessage = Component.m_237113_((String)"\u5df2\u5bfc\u5165 main.json");
            this.statusColor = -11141291;
        } else if (animationsImported) {
            this.statusMessage = Component.m_237113_((String)"\u5df2\u5bfc\u5165 animations.json");
            this.statusColor = -11141291;
        } else {
            this.statusMessage = Component.m_237113_((String)"\u65e0\u652f\u6301\u7684\u6587\u4ef6\uff08\u9700 main.json \u6216 animations.json\uff09");
            this.statusColor = -171;
        }
        this.sendPlayerMessage(this.statusMessage);
    }

    private boolean importAs(Path src, boolean asModel) {
        try {
            Path dir = this.project.directory();
            Path target = dir.resolve(asModel ? "main.json" : "animations.json");
            Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);
            if (asModel) {
                this.refreshPreviewGeometry();
            } else {
                this.hasExternalAnimations = true;
                this.animationsRoot = this.readAnimationsJson(dir);
                this.reloadExternalPreviewAnimations();
                this.refreshAnimationList();
                this.clearSelection();
            }
            return true;
        }
        catch (IOException ex) {
            this.statusMessage = Component.m_237113_((String)("\u5bfc\u5165\u5931\u8d25: " + ex.getMessage()));
            this.statusColor = -43691;
            return false;
        }
    }

    private int detectFileType(Path file) {
        try {
            String content = Files.readString((Path)file, (Charset)StandardCharsets.UTF_8);
            JsonElement el = JsonParser.parseString((String)content);
            if (!el.isJsonObject()) {
                return 0;
            }
            JsonObject obj = el.getAsJsonObject();
            if (obj.has("minecraft:geometry")) {
                return 1;
            }
            JsonElement anims = obj.get("animations");
            if (anims != null && anims.isJsonObject()) {
                return 2;
            }
            return 0;
        }
        catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onFilesDropWithPosition(List<Path> files, int dropX, int dropY) {
        if (files == null || files.isEmpty()) {
            return;
        }
        Path first = files.get(0);
        boolean asModel = dropX < this.f_96543_ / 2;
        int type = this.detectFileType(first);
        if (asModel && type != 1) {
            this.statusMessage = Component.m_237113_((String)(type == 2 ? "\u8be5\u6587\u4ef6\u662f\u52a8\u753b\uff0c\u8bf7\u62d6\u5230\u53f3\u4fa7\u52a8\u753b\u533a" : "\u65e0\u6cd5\u8bc6\u522b\u7684\u6a21\u578b\u6587\u4ef6"));
            this.statusColor = -43691;
            this.sendPlayerMessage(this.statusMessage);
            return;
        }
        if (!asModel && type != 2) {
            this.statusMessage = Component.m_237113_((String)(type == 1 ? "\u8be5\u6587\u4ef6\u662f\u6a21\u578b\uff0c\u8bf7\u62d6\u5230\u5de6\u4fa7\u6a21\u578b\u533a" : "\u65e0\u6cd5\u8bc6\u522b\u7684\u52a8\u753b\u6587\u4ef6"));
            this.statusColor = -43691;
            this.sendPlayerMessage(this.statusMessage);
            return;
        }
        if (this.importAs(first, asModel)) {
            String typeLabel = asModel ? "\u6a21\u578b (main.json)" : "\u52a8\u753b (animations.json)";
            this.statusMessage = Component.m_237113_((String)(files.size() > 1 ? "\u5df2\u5bfc\u5165 " + typeLabel + "\uff08\u4ec5\u9996\u4e2a\uff0c\u5176\u4f59\u5ffd\u7565\uff09" : "\u5df2\u5bfc\u5165 " + typeLabel));
            this.statusColor = -11141291;
        }
        this.sendPlayerMessage(this.statusMessage);
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean parallelActive;
        this.m_280273_(graphics);
        if (this.saveButton != null) {
            Object label;
            Object object = this.pendingConfirm ? "\u786e\u8ba4\u4fdd\u5b58?" : (label = this.dirty ? "\u4fdd\u5b58*" : "\u4fdd\u5b58");
            if (!this.saveButton.m_6035_().getString().equals(label)) {
                this.saveButton.m_93666_((Component)Component.m_237113_((String)label));
            }
        }
        for (Renderable r : this.f_169369_) {
            if (this.formWidgets.contains(r)) continue;
            r.m_88315_(graphics, mouseX, mouseY, partialTick);
        }
        if (!this.formWidgets.isEmpty() && this.formAreaRect != null) {
            Window window = Minecraft.m_91087_().m_91268_();
            double guiScale = window.m_85449_();
            RenderSystem.enableScissor((int)((int)((double)this.formAreaRect.x() * guiScale)), (int)((int)((double)window.m_85442_() - (double)this.formAreaRect.bottom() * guiScale)), (int)((int)((double)this.formAreaRect.width() * guiScale)), (int)((int)((double)this.formAreaRect.height() * guiScale)));
            for (Renderable r : this.f_169369_) {
                if (!this.formWidgets.contains(r)) continue;
                r.m_88315_(graphics, mouseX, mouseY, partialTick);
            }
            for (FormLabel fl : this.formLabels) {
                graphics.m_280056_(this.f_96547_, fl.text(), this.detailLeft, fl.y() - this.formScrollOffset + this.labelYOffset, 0xFFFFFF, false);
            }
            RenderSystem.disableScissor();
            for (FormLabel fl : this.formLabels) {
                if (fl.tooltip() == null) continue;
                int labelY = fl.y() - this.formScrollOffset;
                if (mouseX < this.detailLeft || mouseX >= this.detailLeft + this.fieldXOffset || mouseY < labelY || mouseY >= labelY + this.formRowH) continue;
                graphics.m_280557_(this.f_96547_, (Component)Component.m_237113_((String)fl.tooltip()), mouseX, mouseY);
                break;
            }
        }
        if (this.searchBoxRect != null) {
            int sx = this.searchBoxRect.x() - 1;
            int sy = this.searchBoxRect.y() - 1;
            int sw = this.searchBoxRect.width() + 2;
            int sh = this.searchBoxRect.height() + 2;
            graphics.m_280509_(sx, sy, sx + sw, sy + 1, -10048769);
            graphics.m_280509_(sx, sy + sh - 1, sx + sw, sy + sh, -10048769);
            graphics.m_280509_(sx, sy, sx + 1, sy + sh, -10048769);
            graphics.m_280509_(sx + sw - 1, sy, sx + sw, sy + sh, -10048769);
        }
        boolean bl = parallelActive = this.parallelEnabled && this.parallelEntity != null && this.parallelEntity.isModelPresent();
        if (parallelActive) {
            this.parallelEntity.getPreviewInfo().setPreview(this.selectedAnim);
            ++((Player)this.parallelEntity.getEntity()).f_19797_;
            this.parallelEntity.update(partialTick);
            this.modelPreview.setRuntimeModel(this.parallelEntity.getLoadedGeoModel());
            this.modelPreview.setContent(this.previewGeometry, this.parallelEntity.getLoadedGeoModel(), this.parallelEntity.getTextureLocation(), this.previewWidthScale(), this.previewHeightScale(), this.previewGeometrySource, null);
            this.modelPreview.setAnimation(this.previewAnimation(this.selectedAnim), this.previewTime());
            this.modelPreview.render(graphics, this.previewRect);
        } else {
            this.modelPreview.setRuntimeModel(null);
            this.modelPreview.setSamplingEntity(this.parallelEntity);
            this.modelPreview.setContent(this.previewGeometry, this.previewAnimatedModel(), this.previewTextureLocation(), this.previewWidthScale(), this.previewHeightScale(), this.previewGeometrySource, null);
            this.modelPreview.setAnimation(this.previewAnimation(this.selectedAnim), this.previewTime());
            this.modelPreview.render(graphics, this.previewRect);
        }
        if (this.parallelCheckbox != null) {
            graphics.m_280056_(this.f_96547_, "\u5e76\u884c\u52a8\u753b", this.parallelCheckbox.m_252754_() + this.parallelCheckbox.m_5711_() + 4, this.parallelCheckbox.m_252907_() + 2, -4473925, false);
        }
        if (this.selectedAnim == null) {
            graphics.m_280056_(this.f_96547_, "\u8bf7\u9009\u62e9\u5de6\u4fa7\u52a8\u753b", this.animNameRect.x(), this.animNameRect.y(), -5592406, false);
        } else {
            graphics.m_280056_(this.f_96547_, "\u52a8\u753b\uff1a" + this.selectedAnim, this.animNameRect.x(), this.animNameRect.y(), -1, false);
            MutableAnimationHitConfig animCfg = this.editingState.getAnimations().get(this.selectedAnim);
            if (animCfg == null) {
                graphics.m_280056_(this.f_96547_, "\uff08\u65e0 hit.json \u914d\u7f6e\uff0c\u8bf7\u70b9\u51fb\u300c+\u52a8\u753b\u300d\u6dfb\u52a0\uff09", this.timingRect.x(), this.timingRect.y(), -5592406, false);
            } else {
                AnimationTimelineInfo info = this.animationTimelineInfo(this.selectedAnim);
                graphics.m_280056_(this.f_96547_, "\u65f6\u957f\uff1a" + info.length() + "s | \u5173\u952e\u5e27\uff1a" + info.keyframeTimes().size(), this.timingRect.x(), this.timingRect.y(), -5592406, false);
                this.renderTimeline(graphics, animCfg, info);
            }
        }
        if (this.statusMessage != null) {
            graphics.m_280554_(this.f_96547_, (FormattedText)this.statusMessage, this.statusRect.x(), this.statusRect.y(), this.statusRect.width(), this.statusColor);
        }
        graphics.m_280056_(this.f_96547_, "\u2193 \u62d6\u5165 main.json / animations.json", this.dropHintRect.x(), this.dropHintRect.y(), -7829368, false);
        for (AbstractWidget w : this.formWidgets) {
            YssDropdownWidget d;
            if (!(w instanceof YssDropdownWidget) || !(d = (YssDropdownWidget)w).isExpanded()) continue;
            d.renderOverlay(graphics, mouseX, mouseY);
            break;
        }
        if (DragDropManager.INSTANCE.isDragging()) {
            this.renderDropGuideOverlay(graphics);
        }
    }

    private void renderDropGuideOverlay(GuiGraphics graphics) {
        int hintColor;
        String hint;
        int fileType;
        int w = this.f_96543_;
        int h = this.f_96544_;
        int midX = w / 2;
        int hoverX = DragDropManager.INSTANCE.getHoverX();
        int hoverY = DragDropManager.INSTANCE.getHoverY();
        boolean onLeft = hoverX < midX;
        String hoveredPath = DragDropManager.INSTANCE.getHoveredFirstPath();
        if (hoveredPath == null) {
            fileType = 0;
        } else if (!hoveredPath.equals(this.cachedHoverPath)) {
            this.cachedHoverPath = hoveredPath;
            try {
                this.cachedHoverType = this.detectFileType(Path.of((String)hoveredPath, (String[])new String[0]));
            }
            catch (Exception e) {
                this.cachedHoverType = 0;
            }
            fileType = this.cachedHoverType;
        } else {
            fileType = this.cachedHoverType;
        }
        boolean leftMatch = fileType == 1;
        boolean rightMatch = fileType == 2;
        graphics.m_280509_(0, 0, w, h, 0x60000000);
        int leftColor = leftMatch ? -870297392 : 1077977343;
        int rightColor = rightMatch ? -870297392 : 1077977343;
        graphics.m_280509_(0, 0, midX, h, leftColor);
        graphics.m_280509_(midX, 0, w, h, rightColor);
        graphics.m_280509_(midX - 1, 0, midX + 1, h, -1);
        int n = h / 2;
        Objects.requireNonNull(this.f_96547_);
        int labelY = n - 9 / 2;
        graphics.m_280653_(this.f_96547_, (Component)Component.m_237113_((String)"\u6a21\u578b"), w / 4, labelY, -1);
        graphics.m_280653_(this.f_96547_, (Component)Component.m_237113_((String)"\u52a8\u753b"), w - w / 4, labelY, -1);
        if (onLeft) {
            if (leftMatch) {
                hint = "\u677e\u624b \u2192 \u5bfc\u5165\u6a21\u578b (main.json)";
                hintColor = -1;
            } else {
                hint = fileType == 2 ? "\u8be5\u6587\u4ef6\u662f\u52a8\u753b\uff0c\u8bf7\u62d6\u5230\u53f3\u4fa7" : "\u65e0\u6cd5\u8bc6\u522b\u7684\u6587\u4ef6\u7c7b\u578b";
                hintColor = fileType == 2 ? -22016 : -43691;
            }
        } else if (rightMatch) {
            hint = "\u677e\u624b \u2192 \u5bfc\u5165\u52a8\u753b (animations.json)";
            hintColor = -1;
        } else {
            hint = fileType == 1 ? "\u8be5\u6587\u4ef6\u662f\u6a21\u578b\uff0c\u8bf7\u62d6\u5230\u5de6\u4fa7" : "\u65e0\u6cd5\u8bc6\u522b\u7684\u6587\u4ef6\u7c7b\u578b";
            hintColor = fileType == 1 ? -22016 : -43691;
        }
        MutableComponent mutableComponent = Component.m_237113_((String)hint);
        Objects.requireNonNull(this.f_96547_);
        graphics.m_280653_(this.f_96547_, (Component)mutableComponent, hoverX, Math.max(4, hoverY - 9 - 6), hintColor);
    }

    private void renderTimeline(GuiGraphics graphics, MutableAnimationHitConfig animCfg, AnimationTimelineInfo info) {
        float cursorTime;
        int left = this.timelineRect.x();
        int top = this.timelineRect.y();
        int w = this.timelineRect.width();
        this.timelineLeft = left;
        this.timelineTop = top;
        this.timelineWidth = w;
        this.timelineLength = this.computeTimelineLength(info, animCfg);
        int scaleBottom = top + this.scaleHeight;
        graphics.m_280509_(left, top, left + w, scaleBottom, 0x20808080);
        graphics.m_280509_(left, top, left + 1, scaleBottom, -7829368);
        graphics.m_280056_(this.f_96547_, "0.0", left + 3, top + 4, -5592406, false);
        int endX = this.timeToX(this.timelineLength);
        graphics.m_280509_(endX, top, endX + 1, scaleBottom, -7829368);
        String endLabel = String.format("%.2f", Float.valueOf(this.timelineLength));
        graphics.m_280056_(this.f_96547_, endLabel, endX - this.f_96547_.m_92895_(endLabel) - 2, top + 4, -5592406, false);
        for (float kt : info.keyframeTimes()) {
            if (kt < 0.0f || kt > this.timelineLength) continue;
            int kx = this.timeToX(kt);
            graphics.m_280509_(kx, top, kx + 1, scaleBottom, -12303292);
        }
        graphics.m_280509_(left, scaleBottom, left + w, scaleBottom + 1, -7829368);
        int trackY = scaleBottom + 2;
        List<MutableHitSegment> segments = animCfg.getSegments();
        this.drawTrackBackground(graphics, left, trackY, w, "\u6bb5");
        for (int i = 0; i < segments.size(); ++i) {
            MutableHitSegment seg = segments.get(i);
            MutableTimeWindow time = seg.getTime();
            int color = time == null ? 1623216208 : -4173744;
            String label = "#" + i + (String)(seg.getId() == null || seg.getId().isEmpty() ? "" : " " + seg.getId());
            this.drawTimeBar(graphics, SelectedTrack.SEGMENT, i, time, trackY, color, label);
        }
        List<MutableCasterMoveConfig> moves = animCfg.getCasterMoves();
        this.drawTrackBackground(graphics, left, trackY += this.trackHeight, w, "\u4f4d\u79fb");
        for (int i = 0; i < moves.size(); ++i) {
            MutableCasterMoveConfig move = moves.get(i);
            MutableTimeWindow time = move.getTime();
            int color = time == null ? 1615876288 : -11513664;
            this.drawTimeBar(graphics, SelectedTrack.MOVE, i, time, trackY, color, "#" + i);
        }
        MutableHoverConfig hover = animCfg.getHover();
        this.drawTrackBackground(graphics, left, trackY += this.trackHeight, w, "\u6ede\u7a7a");
        if (hover != null) {
            int barY = trackY + this.trackBarYOffset;
            int hx0 = this.timeToX(0.0f);
            int hx1 = this.timeToX(Math.min(hover.getDuration(), this.timelineLength));
            graphics.m_280509_(hx0, barY, hx1, barY + this.trackBarHeight, -11485104);
            if (this.selectedTrack == SelectedTrack.HOVER) {
                this.drawSelectionBorder(graphics, hx0, barY, hx1, barY + this.trackBarHeight);
            }
            graphics.m_280056_(this.f_96547_, "hover", hx0 + 3, barY + 3, -1, false);
        }
        if ((cursorTime = Mth.m_14036_((float)this.previewTime(), (float)0.0f, (float)this.timelineLength)) != this.previewTime()) {
            this.setPreviewTime(cursorTime);
        }
        int cursorX = this.timeToX(cursorTime);
        int cursorBottom = trackY + this.trackHeight;
        graphics.m_280509_(cursorX, top, cursorX + 2, cursorBottom, -11141121);
        graphics.m_280509_(cursorX - 3, top, cursorX + 5, top + 2, -11141121);
        graphics.m_280509_(cursorX - 2, top + 2, cursorX + 4, top + 4, -11141121);
        graphics.m_280509_(cursorX - 1, top + 4, cursorX + 3, top + 6, -11141121);
        String cursorLabel = String.format("%.2fs", Float.valueOf(cursorTime));
        int labelX = Math.min(left + w - this.f_96547_.m_92895_(cursorLabel) - 2, cursorX + 5);
        graphics.m_280056_(this.f_96547_, cursorLabel, Math.max(left + 2, labelX), top + 4, -11141121, false);
    }

    private void drawTrackBackground(GuiGraphics graphics, int left, int trackY, int w, String label) {
        graphics.m_280509_(left, trackY, left + w, trackY + this.trackHeight, 0x20808080);
        graphics.m_280056_(this.f_96547_, label, left + 2, trackY + 1, -3355444, false);
    }

    private void drawTimeBar(GuiGraphics graphics, SelectedTrack track, int index, @Nullable MutableTimeWindow time, int trackY, int color, String label) {
        boolean selected;
        int x1;
        int x0;
        boolean full;
        int barY = trackY + this.trackBarYOffset;
        boolean bl = full = time == null;
        if (full) {
            x0 = this.timelineLeft;
            x1 = this.timelineLeft + this.timelineWidth;
        } else {
            x0 = this.timeToX(time.getStart());
            x1 = this.timeToX(time.getEnd());
        }
        graphics.m_280509_(x0, barY, x1, barY + this.trackBarHeight, color);
        boolean bl2 = selected = this.selectedTrack == track && this.selectedIndex == index;
        if (selected) {
            this.drawSelectionBorder(graphics, x0, barY, x1, barY + this.trackBarHeight);
            if (!full) {
                graphics.m_280509_(x0, barY, x0 + 2, barY + this.trackBarHeight, -1);
                graphics.m_280509_(x1 - 2, barY, x1, barY + this.trackBarHeight, -1);
            }
        }
        if (x1 - x0 > this.f_96547_.m_92895_(label) + 4) {
            graphics.m_280056_(this.f_96547_, label, x0 + 3, barY + 3, -1, false);
        }
    }

    private void drawSelectionBorder(GuiGraphics graphics, int x0, int y0, int x1, int y1) {
        graphics.m_280509_(x0, y0, x1, y0 + 1, -1);
        graphics.m_280509_(x0, y1 - 1, x1, y1, -1);
        graphics.m_280509_(x0, y0, x0 + 1, y1, -1);
        graphics.m_280509_(x1 - 1, y0, x1, y1, -1);
    }

    private int timeToX(float t) {
        if (this.timelineWidth <= 0 || this.timelineLength <= 0.0f) {
            return this.timelineLeft;
        }
        float ratio = t / this.timelineLength;
        if (ratio < 0.0f) {
            ratio = 0.0f;
        }
        if (ratio > 1.0f) {
            ratio = 1.0f;
        }
        return this.timelineLeft + (int)(ratio * (float)this.timelineWidth);
    }

    private float pixelToTime(double x) {
        if (this.timelineWidth <= 0 || this.timelineLength <= 0.0f) {
            return 0.0f;
        }
        float t = (float)((x - (double)this.timelineLeft) / (double)this.timelineWidth * (double)this.timelineLength);
        if (t < 0.0f) {
            t = 0.0f;
        }
        if (t > this.timelineLength) {
            t = this.timelineLength;
        }
        return t;
    }

    private float computeTimelineLength(AnimationTimelineInfo info, MutableAnimationHitConfig animCfg) {
        MutableTimeWindow t;
        float max = info.length();
        if (max > 0.0f) {
            return max;
        }
        for (MutableHitSegment seg : animCfg.getSegments()) {
            t = seg.getTime();
            if (t == null || !(t.getEnd() > max)) continue;
            max = t.getEnd();
        }
        for (MutableCasterMoveConfig move : animCfg.getCasterMoves()) {
            t = move.getTime();
            if (t == null || !(t.getEnd() > max)) continue;
            max = t.getEnd();
        }
        MutableHoverConfig hover = animCfg.getHover();
        if (hover != null && hover.getDuration() > max) {
            max = hover.getDuration();
        }
        return max < 1.0f ? 1.0f : max;
    }

    public boolean m_6375_(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (AbstractWidget w : this.formWidgets) {
                YssDropdownWidget d;
                if (!(w instanceof YssDropdownWidget) || !(d = (YssDropdownWidget)w).isExpanded()) continue;
                d.handleOverlayClick(mouseX, mouseY);
                return true;
            }
            if (this.hitTestPreviewCursor(mouseX, mouseY)) {
                this.previewCursorDragging = true;
                return true;
            }
        }
        if ((button == 0 || button == 1) && this.modelPreview.contains(this.previewRect, mouseX, mouseY)) {
            this.previewDragButton = button;
            return true;
        }
        if (button == 0 && this.hitTestTimeline(mouseX, mouseY)) {
            return true;
        }
        return super.m_6375_(mouseX, mouseY, button);
    }

    private boolean hitTestPreviewCursor(double mouseX, double mouseY) {
        if (this.selectedAnim == null || this.timelineWidth <= 0 || mouseX < (double)this.timelineLeft || mouseX > (double)(this.timelineLeft + this.timelineWidth) || mouseY < (double)this.timelineTop || mouseY > (double)(this.timelineTop + this.scaleHeight)) {
            return false;
        }
        this.setPreviewTime(this.pixelToTime(mouseX));
        return true;
    }

    private boolean hitTestTimeline(double mouseX, double mouseY) {
        int barY;
        if (this.timelineWidth <= 0 || this.selectedAnim == null) {
            return false;
        }
        MutableAnimationHitConfig animCfg = this.editingState.getAnimations().get(this.selectedAnim);
        if (animCfg == null) {
            return false;
        }
        int scaleBottom = this.timelineTop + this.scaleHeight;
        int trackY = scaleBottom + 2;
        int timelineBottom = trackY + 3 * this.trackHeight;
        if (mouseY < (double)this.timelineTop || mouseY > (double)timelineBottom || mouseX < (double)this.timelineLeft || mouseX > (double)(this.timelineLeft + this.timelineWidth)) {
            return false;
        }
        if (this.hitTestTrack(SelectedTrack.SEGMENT, animCfg.getSegments(), trackY, mouseX, mouseY, MutableHitSegment::getTime)) {
            return true;
        }
        if (this.hitTestTrack(SelectedTrack.MOVE, animCfg.getCasterMoves(), trackY += this.trackHeight, mouseX, mouseY, MutableCasterMoveConfig::getTime)) {
            return true;
        }
        trackY += this.trackHeight;
        MutableHoverConfig hover = animCfg.getHover();
        if (hover != null && mouseY >= (double)(barY = trackY + this.trackBarYOffset) && mouseY <= (double)(barY + this.trackBarHeight)) {
            int hx0 = this.timeToX(0.0f);
            int hx1 = this.timeToX(Math.min(hover.getDuration(), this.timelineLength));
            if (mouseX >= (double)hx0 && mouseX <= (double)hx1) {
                this.select(SelectedTrack.HOVER, 0, DragMode.NONE);
                this.dragLastX = mouseX;
                return true;
            }
        }
        this.clearSelection();
        return false;
    }

    private <T> boolean hitTestTrack(SelectedTrack track, List<T> items, int trackY, double mouseX, double mouseY, Function<T, MutableTimeWindow> timeGetter) {
        int barY = trackY + this.trackBarYOffset;
        if (mouseY < (double)barY || mouseY > (double)(barY + this.trackBarHeight)) {
            return false;
        }
        for (int i = items.size() - 1; i >= 0; --i) {
            int x1;
            int x0;
            T item = items.get(i);
            MutableTimeWindow time = timeGetter.apply(item);
            if (time == null) {
                x0 = this.timelineLeft;
                x1 = this.timelineLeft + this.timelineWidth;
            } else {
                x0 = this.timeToX(time.getStart());
                x1 = this.timeToX(time.getEnd());
            }
            if (!(mouseX >= (double)x0) || !(mouseX <= (double)x1)) continue;
            DragMode mode = time == null ? DragMode.NONE : (Math.abs(mouseX - (double)x0) <= (double)this.edgePixels ? DragMode.START : (Math.abs(mouseX - (double)x1) <= (double)this.edgePixels ? DragMode.END : DragMode.MOVE));
            this.select(track, i, mode);
            this.dragLastX = mouseX;
            return true;
        }
        return false;
    }

    private void select(SelectedTrack track, int index, DragMode mode) {
        if (track != this.selectedTrack || index != this.selectedIndex) {
            this.formScrollOffset = 0;
        }
        this.selectedTrack = track;
        this.selectedIndex = index;
        this.dragMode = mode;
        this.rebuildForm();
    }

    private void clearSelection() {
        this.selectedTrack = null;
        this.selectedIndex = -1;
        this.dragMode = DragMode.NONE;
        this.formScrollOffset = 0;
        this.rebuildForm();
    }

    private void rebuildForm() {
        this.formWidgets.forEach(x$0 -> this.m_169411_((GuiEventListener)x$0));
        this.formWidgets.clear();
        this.formLabels.clear();
        this.formWidgetOrigins.clear();
        this.startTimeBox = null;
        this.endTimeBox = null;
        this.formMaxScroll = 0;
        this.formSectionDisabled = false;
        if (this.selectedAnim == null) {
            return;
        }
        MutableAnimationHitConfig animCfg = this.editingState.getAnimations().get(this.selectedAnim);
        if (animCfg == null) {
            return;
        }
        int formTop = (this.formAreaRect != null ? this.formAreaRect.y() : 0) + this.formGap;
        this.buildActionBar(this.detailLeft, formTop, animCfg);
        int optY = formTop + this.formRowH + this.layout.resolveY(Length.em(0.5));
        this.formLabel(optY, "\u6839\u8fd0\u52a8", "\u52fe\u9009\u540e\u5c06 Root \u5206\u7ec4\u7684\u4f4d\u79fb\u8f6c\u6362\u4e3a\u5b9e\u4f53\u771f\u5b9e\u4f4d\u79fb");
        YssCheckbox rootMotion = new YssCheckbox(this.detailLeft + this.fieldXOffset, optY, this.fieldW, this.widgetH, animCfg.isRootMotion(), value -> {
            animCfg.setRootMotion((boolean)value);
            this.markDirty();
        });
        this.addFormWidget(rootMotion);
        if (this.selectedTrack == null) {
            return;
        }
        int formY = optY + this.formRowH + this.layout.resolveY(Length.em(0.5));
        switch (this.selectedTrack) {
            case SEGMENT: {
                List<MutableHitSegment> segs = animCfg.getSegments();
                if (this.selectedIndex < 0 || this.selectedIndex >= segs.size()) break;
                this.buildSegmentForm(segs.get(this.selectedIndex), this.detailLeft, formY);
                break;
            }
            case MOVE: {
                List<MutableCasterMoveConfig> moves = animCfg.getCasterMoves();
                if (this.selectedIndex < 0 || this.selectedIndex >= moves.size()) break;
                this.buildMoveForm(moves.get(this.selectedIndex), this.detailLeft, formY);
                break;
            }
            case HOVER: {
                MutableHoverConfig hover = animCfg.getHover();
                if (hover == null) break;
                this.buildHoverForm(hover, this.detailLeft, formY);
            }
        }
        if (this.formAreaRect != null) {
            int contentBottom = 0;
            for (AbstractWidget w : this.formWidgets) {
                contentBottom = Math.max(contentBottom, w.m_252907_() + w.m_93694_());
            }
            this.formMaxScroll = Math.max(0, contentBottom - this.formAreaRect.bottom());
            this.formScrollOffset = Mth.m_14045_((int)this.formScrollOffset, (int)0, (int)this.formMaxScroll);
            this.updateFormWidgetPositions();
        }
    }

    private void updateFormWidgetPositions() {
        for (Map.Entry<AbstractWidget, int[]> entry : this.formWidgetOrigins.entrySet()) {
            int[] origin = entry.getValue();
            entry.getKey().m_252865_(origin[0]);
            entry.getKey().m_253211_(origin[1] - this.formScrollOffset);
        }
    }

    private void buildActionBar(int x, int y, MutableAnimationHitConfig animCfg) {
        Length btnW = Length.clamp(Length.px(40), Length.em(6.25), Length.px(60));
        Length btnGap = Length.em(0.5);
        Length btnH = Length.em(2.25);
        HRow row = this.layout.hrow(Length.px(x), Length.px(y), btnH, btnGap);
        Rect addSegRect = row.next(btnW);
        Button addSeg = Button.m_253074_((Component)Component.m_237113_((String)"+\u6bb5"), b -> {
            animCfg.getSegments().add(new MutableHitSegment());
            this.markDirty();
            this.select(SelectedTrack.SEGMENT, animCfg.getSegments().size() - 1, DragMode.NONE);
        }).m_252987_(addSegRect.x(), addSegRect.y(), addSegRect.width(), addSegRect.height()).m_253136_();
        this.addFormWidget((AbstractWidget)addSeg);
        Rect addMoveRect = row.next(btnW);
        Button addMove = Button.m_253074_((Component)Component.m_237113_((String)"+\u4f4d\u79fb"), b -> {
            animCfg.getCasterMoves().add(new MutableCasterMoveConfig());
            this.markDirty();
            this.select(SelectedTrack.MOVE, animCfg.getCasterMoves().size() - 1, DragMode.NONE);
        }).m_252987_(addMoveRect.x(), addMoveRect.y(), addMoveRect.width(), addMoveRect.height()).m_253136_();
        this.addFormWidget((AbstractWidget)addMove);
        if (animCfg.getHover() == null) {
            Rect addHoverRect = row.next(btnW);
            Button addHover = Button.m_253074_((Component)Component.m_237113_((String)"+\u6ede\u7a7a"), b -> {
                animCfg.setHover(new MutableHoverConfig());
                this.markDirty();
                this.select(SelectedTrack.HOVER, 0, DragMode.NONE);
            }).m_252987_(addHoverRect.x(), addHoverRect.y(), addHoverRect.width(), addHoverRect.height()).m_253136_();
            this.addFormWidget((AbstractWidget)addHover);
        }
        if (this.selectedTrack != null) {
            Rect deleteRect = row.next(btnW);
            Button delete = Button.m_253074_((Component)Component.m_237113_((String)"\u5220\u9664"), b -> {
                this.deleteSelected(animCfg);
                this.markDirty();
            }).m_252987_(deleteRect.x(), deleteRect.y(), deleteRect.width(), deleteRect.height()).m_253136_();
            this.addFormWidget((AbstractWidget)delete);
        }
    }

    private void deleteSelected(MutableAnimationHitConfig animCfg) {
        if (this.selectedTrack == null) {
            return;
        }
        switch (this.selectedTrack) {
            case SEGMENT: {
                List<MutableHitSegment> segs = animCfg.getSegments();
                if (this.selectedIndex < 0 || this.selectedIndex >= segs.size()) break;
                segs.remove(this.selectedIndex);
                break;
            }
            case MOVE: {
                List<MutableCasterMoveConfig> moves = animCfg.getCasterMoves();
                if (this.selectedIndex < 0 || this.selectedIndex >= moves.size()) break;
                moves.remove(this.selectedIndex);
                break;
            }
            case HOVER: {
                animCfg.setHover(null);
            }
        }
        this.clearSelection();
    }

    private void buildSegmentForm(MutableHitSegment seg, int x, int y) {
        int fieldX = x + this.fieldXOffset;
        int w = this.fieldW;
        int[] row = new int[]{y};
        this.formLabel(row[0], "\u6807\u8bc6(\u4ec5\u65e5\u5fd7)", "\u6bb5\u7684\u552f\u4e00\u6807\u8bc6\uff0c\u4ec5\u7528\u4e8e\u65e5\u5fd7");
        this.stringEditBox(fieldX, row[0], w, seg.getId(), seg::setId);
        row[0] = row[0] + this.formRowH;
        this.buildTimeRow(seg.getTime(), x, row, seg::setTime);
        this.formLabel(row[0], "\u4f24\u5bb3", "\u57fa\u7840\u4f24\u5bb3\u503c\uff0c>0 \u751f\u6548\uff0c\u22640 \u4e0d\u9020\u4f24\u5bb3");
        this.floatEditBox(fieldX, row[0], w, seg.getDamage(), seg::setDamage);
        row[0] = row[0] + this.formRowH;
        this.formLabel(row[0], "\u51fa\u4f24\u65f6\u673a", "\u5373\u65f6=\u547d\u4e2d\u7acb\u5373\u7ed3\u7b97\uff1b\u5ef6\u8fdf=\u76ee\u6807\u505c\u5e27\u7ed3\u675f\u540e\u7ed3\u7b97");
        this.addFormWidget(new YssEnumButtonGroup(fieldX, row[0], w, this.widgetH, (Enum[])DamageTiming.values(), (Enum)seg.getDamageTiming(), v -> {
            seg.setDamageTiming((DamageTiming)((Object)v));
            this.markDirty();
        }));
        row[0] = row[0] + this.formRowH;
        this.formLabel(row[0], "\u5224\u5b9a\u9aa8\u9abc", "\u53c2\u4e0e\u5224\u5b9a\u7684\u6a21\u578b\u9aa8\u9abc");
        Button bonesButton = Button.m_253074_((Component)Component.m_237113_((String)("\u9aa8\u9abc: " + seg.getBones().size() + " \u7f16\u8f91")), b -> this.openBoneSelect(seg)).m_252987_(fieldX, row[0], w, this.widgetH).m_253136_();
        this.addFormWidget((AbstractWidget)bonesButton);
        row[0] = row[0] + this.formRowH;
        MutableHitReset hitReset = seg.getHitReset();
        this.formLabel(row[0], "\u547d\u4e2d\u91cd\u7f6e", "\u79bb\u5f00\u91cd\u7f6e=\u79bb\u5f00\u518d\u8fdb\u5165\u53ef\u518d\u547d\u4e2d\uff1b\u4ec5\u4e00\u6b21=\u6574\u6bb5\u4e00\u6b21\uff1b\u95f4\u9694=\u51b7\u5374\u540e\u53ef\u518d\u547d\u4e2d");
        this.addFormWidget(new YssEnumButtonGroup(fieldX, row[0], w, this.widgetH, (Enum[])HitReset.Type.values(), (Enum)hitReset.getType(), v -> {
            hitReset.setType((HitReset.Type)((Object)v));
            this.markDirty();
        }));
        row[0] = row[0] + this.formRowH;
        if (hitReset.getType() == HitReset.Type.INTERVAL) {
            this.formLabel(row[0], "\u51b7\u5374\u95f4\u9694", "\u95f4\u9694\u7c7b\u578b\u7684\u51b7\u5374\u65f6\u95f4(\u79d2)");
            this.floatEditBox(fieldX, row[0], w, hitReset.getInterval(), hitReset::setInterval);
            row[0] = row[0] + this.formRowH;
        }
        this.buildNullableMechanism(seg.getKnockback(), x, row, "\u51fb\u9000", "\u51fb\u9000\u673a\u5236", seg::setKnockback, MutableKnockbackConfig::new, kb -> {
            this.formLabel(row[0], "\u529b\u5ea6", "\u51fb\u9000\u901f\u5ea6\uff0c\u53d7\u76ee\u6807\u91cd\u91cf\u8870\u51cf(\u6bcf\u70b9-10%)");
            this.floatEditBox(fieldX, row[0], w, kb.getPower(), kb::setPower);
            row[0] = row[0] + this.formRowH;
            this.formLabel(row[0], "\u65b9\u5411", "\u6280\u80fd\u65b9\u5411=\u65bd\u6cd5\u8005\u671d\u5411\uff1b\u5f84\u5411=\u65bd\u6cd5\u8005\u6307\u5411\u76ee\u6807");
            this.dropdown(fieldX, row[0], w, KnockbackDirection.values(), kb.getDirection(), kb::setDirection);
            row[0] = row[0] + this.formRowH;
            this.formLabel(row[0], "\u89d2\u5ea6\u504f\u79fb", "\u65b9\u5411\u65cb\u8f6c(\u5ea6)\uff0c\u5f84\u5411 0=\u5f39\u5f00/90=\u5377\u8d77");
            this.floatEditBox(fieldX, row[0], w, kb.getAngleOffset(), kb::setAngleOffset);
            row[0] = row[0] + this.formRowH;
        });
        this.buildNullableMechanism(seg.getKnockup(), x, row, "\u51fb\u98de", "\u51fb\u98de\u673a\u5236", seg::setKnockup, MutableKnockupConfig::new, ku -> {
            this.formLabel(row[0], "\u529b\u5ea6", "\u7ad6\u76f4\u901f\u5ea6\uff0c\u6b63=\u5411\u4e0a\uff0c\u8d1f=\u5411\u4e0b");
            this.floatEditBox(fieldX, row[0], w, ku.getPower(), ku::setPower);
            row[0] = row[0] + this.formRowH;
        });
        this.buildNullableMechanism(seg.getHitstop(), x, row, "\u505c\u5e27", "\u505c\u5e27\u673a\u5236", seg::setHitstop, MutableHitstopConfig::new, hs -> {
            this.formLabel(row[0], "\u5f3a\u5236", "\u65e0\u89c6\u76ee\u6807\u786c\u5ea6\uff0c\u56fa\u5b9a\u65f6\u957f");
            this.toggleButton(fieldX, row[0], w, hs.isForced() ? "\u662f" : "\u5426", hs.isForced(), v -> {
                hs.setForced((boolean)v);
                this.rebuildForm();
            });
            row[0] = row[0] + this.formRowH;
            this.buildNullableMechanism(hs.getSelf(), x, row, "\u81ea\u505c\u5e27", "\u65bd\u6cd5\u8005\u81ea\u8eab\u505c\u5e27", hs::setSelf, MutableHitstopConfig.MutableSelf::new, self -> {
                this.formLabel(row[0], "\u65f6\u957f", "\u505c\u5e27 tick \u6570(1\u79d2=20tick)");
                this.intEditBox(fieldX, row[0], w, self.getTicks(), self::setTicks);
                row[0] = row[0] + this.formRowH;
                this.formLabel(row[0], "\u6309\u786c\u5ea6\u7f29\u653e", "\u65f6\u957f\u6309\u76ee\u6807\u786c\u5ea6\u7f29\u51cf");
                this.toggleButton(fieldX, row[0], w, self.isScaleByTargetHardness() ? "\u662f" : "\u5426", self.isScaleByTargetHardness(), v -> {
                    self.setScaleByTargetHardness((boolean)v);
                    this.rebuildForm();
                });
                row[0] = row[0] + this.formRowH;
                this.formLabel(row[0], "\u591a\u76ee\u6807\u9012\u51cf", "\u6bcf\u591a\u547d\u4e2d\u4e00\u4e2a\u76ee\u6807\uff0c\u65f6\u957f\u4e58\u6b64\u7cfb\u6570");
                this.floatEditBox(fieldX, row[0], w, self.getMultiTargetDecay(), self::setMultiTargetDecay);
                row[0] = row[0] + this.formRowH;
            });
            this.buildNullableMechanism(hs.getTarget(), x, row, "\u76ee\u6807\u505c\u5e27", "\u88ab\u547d\u4e2d\u8005\u505c\u5e27", hs::setTarget, MutableHitstopConfig.MutableTarget::new, target -> {
                this.formLabel(row[0], "\u65f6\u957f", "\u505c\u5e27 tick \u6570");
                this.intEditBox(fieldX, row[0], w, target.getTicks(), target::setTicks);
                row[0] = row[0] + this.formRowH;
                this.formLabel(row[0], "\u6309\u786c\u5ea6\u7f29\u653e", "\u65f6\u957f\u6309\u76ee\u6807\u81ea\u8eab\u786c\u5ea6\u7f29\u51cf");
                this.toggleButton(fieldX, row[0], w, target.isScaleByHardness() ? "\u662f" : "\u5426", target.isScaleByHardness(), v -> {
                    target.setScaleByHardness((boolean)v);
                    this.rebuildForm();
                });
                row[0] = row[0] + this.formRowH;
            });
        });
        this.buildNullableMechanism(seg.getInterrupt(), x, row, "\u6253\u65ad", "\u6253\u65ad\u673a\u5236", seg::setInterrupt, MutableInterruptConfig::new, ir -> {
            this.formLabel(row[0], "\u529b\u5ea6", "\u5927\u4e8e\u76ee\u6807\u6297\u6253\u65ad\u5219\u6210\u529f\u6253\u65ad\u8fdb\u53d7\u51fb\u6001");
            this.floatEditBox(fieldX, row[0], w, ir.getPower(), ir::setPower);
            row[0] = row[0] + this.formRowH;
        });
    }

    private void buildMoveForm(MutableCasterMoveConfig move, int x, int y) {
        int fieldX = x + this.fieldXOffset;
        int w = this.fieldW;
        int[] row = new int[]{y};
        this.buildTimeRow(move.getTime(), x, row, move::setTime);
        this.formLabel(row[0], "\u65b9\u5411", "\u671d\u5411=\u76f8\u5bf9\u65bd\u6cd5\u8005\u671d\u5411\uff1b\u4e16\u754c=\u4e16\u754c\u7edd\u5bf9\u65b9\u5411");
        this.dropdown(fieldX, row[0], w, CasterMoveDirection.values(), move.getDirection(), move::setDirection);
        row[0] = row[0] + this.formRowH;
        this.formLabel(row[0], "\u89d2\u5ea6\u504f\u79fb", "\u65b9\u5411\u65cb\u8f6c(\u5ea6)");
        this.floatEditBox(fieldX, row[0], w, move.getAngleOffset(), move::setAngleOffset);
        row[0] = row[0] + this.formRowH;
        this.formLabel(row[0], "\u529b\u5ea6", "\u4f4d\u79fb\u901f\u5ea6\uff1b\u51b2\u91cf=\u5355\u6b21\uff0c\u6301\u7eed=\u6bcf\u5e27\u53e0\u52a0");
        this.floatEditBox(fieldX, row[0], w, move.getPower(), move::setPower);
        row[0] = row[0] + this.formRowH;
        this.formLabel(row[0], "\u7c7b\u578b", "\u51b2\u91cf=\u77ac\u95f4\u5355\u6b21\uff1b\u6301\u7eed=\u65f6\u95f4\u7a97\u5185\u6301\u7eed\u63a8\u529b");
        this.dropdown(fieldX, row[0], w, CasterMoveType.values(), move.getType(), move::setType);
        row[0] = row[0] + this.formRowH;
    }

    private void buildHoverForm(MutableHoverConfig hover, int x, int y) {
        int fieldX = x + this.fieldXOffset;
        int w = this.fieldW;
        int[] row = new int[]{y};
        this.formLabel(row[0], "\u65f6\u957f", "\u6ede\u7a7a\u6301\u7eed\u65f6\u95f4(\u79d2)");
        this.floatEditBox(fieldX, row[0], w, hover.getDuration(), hover::setDuration);
        row[0] = row[0] + this.formRowH;
        this.formLabel(row[0], "\u91cd\u529b\u500d\u7387", "\u4e0b\u843d\u91cd\u529b\u4e58\u6570\uff0c0=\u65e0\u91cd\u529b\uff0c1=\u6b63\u5e38");
        this.floatEditBox(fieldX, row[0], w, hover.getGravityScale(), hover::setGravityScale);
        row[0] = row[0] + this.formRowH;
        this.formLabel(row[0], "\u7981\u7528\u8f93\u5165", "\u6ede\u7a7a\u671f\u95f4\u7981\u7528 WASD");
        this.toggleButton(fieldX, row[0], w, hover.isBlockInput() ? "\u662f" : "\u5426", hover.isBlockInput(), v -> {
            hover.setBlockInput((boolean)v);
            this.rebuildForm();
        });
        row[0] = row[0] + this.formRowH;
    }

    private void buildTimeRow(@Nullable MutableTimeWindow time, int x, int[] row, Consumer<MutableTimeWindow> setter) {
        int fieldX = x + this.fieldXOffset;
        int w = this.fieldW;
        this.formLabel(row[0], "\u65f6\u95f4\u7a97", "\u5224\u5b9a/\u4f4d\u79fb\u751f\u6548\u65f6\u95f4\u533a\u95f4(\u79d2)");
        MutableTimeWindow window = time != null ? time : new MutableTimeWindow(0.0f, this.timelineLength);
        int timeFieldGap = this.layout.resolveX(Length.em(0.75));
        int halfW = (w - timeFieldGap) / 2;
        this.startTimeBox = this.floatEditBox(fieldX, row[0], halfW, window.getStart(), v -> {
            window.setStart(v.floatValue());
            setter.accept(window);
        });
        this.endTimeBox = this.floatEditBox(fieldX + halfW + timeFieldGap, row[0], halfW, window.getEnd(), v -> {
            window.setEnd(v.floatValue());
            setter.accept(window);
        });
        row[0] = row[0] + this.formRowH;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> void buildNullableMechanism(@Nullable T current, int x, int[] row, String label, String tooltip, Consumer<T> setter, Supplier<T> factory, Consumer<T> body) {
        boolean enabled = current != null;
        this.formLabel(row[0], label, tooltip);
        Object display = enabled ? current : this.disabledMechanismCache.getOrDefault(label, factory.get());
        YssCheckbox checkbox = new YssCheckbox(x + this.fieldXOffset, row[0], this.fieldW, this.widgetH, enabled, value -> {
            this.markDirty();
            if (value.booleanValue()) {
                Object cached = this.disabledMechanismCache.remove(label);
                setter.accept(cached != null ? cached : factory.get());
            } else {
                this.disabledMechanismCache.put(label, display);
                setter.accept(null);
            }
            this.rebuildForm();
        });
        this.addFormWidget(checkbox);
        row[0] = row[0] + this.formRowH;
        boolean prevDisabled = this.formSectionDisabled;
        this.formSectionDisabled = !enabled;
        try {
            body.accept(display);
        }
        finally {
            this.formSectionDisabled = prevDisabled;
        }
    }

    void markDirty() {
        this.dirty = true;
    }

    private void addFormWidget(AbstractWidget widget) {
        this.m_142416_((GuiEventListener)widget);
        this.formWidgets.add(widget);
        this.formWidgetOrigins.put(widget, new int[]{widget.m_252754_(), widget.m_252907_()});
        if (this.formSectionDisabled) {
            if (widget instanceof EditBox) {
                EditBox box = (EditBox)widget;
                box.m_94186_(false);
            } else {
                widget.f_93623_ = false;
            }
        }
    }

    private void formLabel(int y, String text, @Nullable String tooltip) {
        this.formLabels.add(new FormLabel(y, text, tooltip));
    }

    private void stringEditBox(int x, int y, int w, String current, Consumer<String> setter) {
        EditBox box = new EditBox(this.f_96547_, x, y, w, this.widgetH, (Component)Component.m_237119_());
        box.m_94199_(1024);
        box.m_94144_(current == null ? "" : current);
        box.m_94151_(s -> {
            setter.accept((String)s);
            this.markDirty();
        });
        this.addFormWidget((AbstractWidget)box);
    }

    private EditBox floatEditBox(int x, int y, int w, float current, Consumer<Float> setter) {
        EditBox box = new EditBox(this.f_96547_, x, y, w, this.widgetH, (Component)Component.m_237119_());
        box.m_94199_(64);
        box.m_94144_(String.valueOf(current));
        box.m_94153_(s -> s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.") || YssHitEditorScreen.isFloat(s));
        box.m_94151_(s -> {
            if (s.isEmpty() || s.equals("-") || s.equals(".") || s.equals("-.")) {
                return;
            }
            try {
                setter.accept(Float.valueOf(Float.parseFloat(s)));
                this.markDirty();
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        });
        this.addFormWidget((AbstractWidget)box);
        return box;
    }

    private void intEditBox(int x, int y, int w, int current, Consumer<Integer> setter) {
        EditBox box = new EditBox(this.f_96547_, x, y, w, this.widgetH, (Component)Component.m_237119_());
        box.m_94199_(16);
        box.m_94144_(String.valueOf(current));
        box.m_94153_(s -> s.isEmpty() || s.equals("-") || YssHitEditorScreen.isInt(s));
        box.m_94151_(s -> {
            if (s.isEmpty() || s.equals("-")) {
                return;
            }
            try {
                setter.accept(Integer.parseInt(s));
                this.markDirty();
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        });
        this.addFormWidget((AbstractWidget)box);
    }

    private <T extends Enum<T>> void dropdown(int x, int y, int w, T[] values, T current, Consumer<T> setter) {
        this.addFormWidget(new YssDropdownWidget(x, y, w, this.widgetH, values, current, v -> {
            setter.accept(v);
            this.markDirty();
        }));
    }

    private void toggleButton(int x, int y, int w, String displayText, boolean current, Consumer<Boolean> setter) {
        Button btn = Button.m_253074_((Component)Component.m_237113_((String)displayText), b -> {
            setter.accept(!current);
            this.markDirty();
        }).m_252987_(x, y, w, this.widgetH).m_253136_();
        this.addFormWidget((AbstractWidget)btn);
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

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.previewCursorDragging) {
            this.setPreviewTime(this.pixelToTime(mouseX));
            return true;
        }
        if (button == this.previewDragButton && this.modelPreview.drag(this.previewRect, mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        if (button != 0 || this.selectedTrack == null || this.dragMode == DragMode.NONE) {
            return super.m_7979_(mouseX, mouseY, button, dragX, dragY);
        }
        MutableTimeWindow time = this.getSelectedTimeWindow();
        if (time == null) {
            return false;
        }
        float newTime = this.pixelToTime(mouseX);
        switch (this.dragMode) {
            case START: {
                time.setStart(Math.min(newTime, time.getEnd()));
                break;
            }
            case END: {
                time.setEnd(Math.max(newTime, time.getStart()));
                break;
            }
            case MOVE: {
                float deltaTime = this.pixelToTime(mouseX) - this.pixelToTime(this.dragLastX);
                float newStart = time.getStart() + deltaTime;
                float newEnd = time.getEnd() + deltaTime;
                if (newStart < 0.0f) {
                    newEnd -= newStart;
                    newStart = 0.0f;
                }
                if (newEnd > this.timelineLength) {
                    newStart -= newEnd - this.timelineLength;
                    newEnd = this.timelineLength;
                }
                if (newStart < 0.0f) {
                    newStart = 0.0f;
                }
                time.setStart(newStart);
                time.setEnd(newEnd);
                break;
            }
            default: {
                return false;
            }
        }
        this.dragLastX = mouseX;
        if (this.startTimeBox != null) {
            this.startTimeBox.m_94144_(String.valueOf(time.getStart()));
        }
        if (this.endTimeBox != null) {
            this.endTimeBox.m_94144_(String.valueOf(time.getEnd()));
        }
        this.markDirty();
        return true;
    }

    public boolean m_6348_(double mouseX, double mouseY, int button) {
        boolean handled;
        boolean bl = handled = button == 0 && this.previewCursorDragging || button == this.previewDragButton;
        if (button == 0) {
            this.previewCursorDragging = false;
        }
        if (button == this.previewDragButton) {
            this.previewDragButton = -1;
        }
        return handled || super.m_6348_(mouseX, mouseY, button);
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (this.formAreaRect != null && this.formMaxScroll > 0 && mouseX >= (double)this.formAreaRect.x() && mouseX <= (double)this.formAreaRect.right() && mouseY >= (double)this.formAreaRect.y() && mouseY <= (double)this.formAreaRect.bottom()) {
            int old = this.formScrollOffset;
            this.formScrollOffset = Mth.m_14045_((int)(this.formScrollOffset - (int)(delta * 16.0)), (int)0, (int)this.formMaxScroll);
            if (this.formScrollOffset != old) {
                this.updateFormWidgetPositions();
                return true;
            }
        }
        return this.modelPreview.scroll(this.previewRect, mouseX, mouseY, delta) || super.m_6050_(mouseX, mouseY, delta);
    }

    @Nullable
    private MutableTimeWindow getSelectedTimeWindow() {
        if (this.selectedTrack == null || this.selectedAnim == null) {
            return null;
        }
        MutableAnimationHitConfig animCfg = this.editingState.getAnimations().get(this.selectedAnim);
        if (animCfg == null) {
            return null;
        }
        return switch (this.selectedTrack) {
            default -> throw new IncompatibleClassChangeError();
            case SelectedTrack.SEGMENT -> {
                List<MutableHitSegment> segs = animCfg.getSegments();
                if (this.selectedIndex >= 0 && this.selectedIndex < segs.size()) {
                    yield segs.get(this.selectedIndex).getTime();
                }
                yield null;
            }
            case SelectedTrack.MOVE -> {
                List<MutableCasterMoveConfig> moves = animCfg.getCasterMoves();
                if (this.selectedIndex >= 0 && this.selectedIndex < moves.size()) {
                    yield moves.get(this.selectedIndex).getTime();
                }
                yield null;
            }
            case SelectedTrack.HOVER -> null;
        };
    }

    private static enum DragMode {
        NONE,
        START,
        END,
        MOVE;

    }

    private class AnimationList
    extends ObjectSelectionList<AnimationEntry> {
        public AnimationList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
            super(minecraft, width, height, y0, y1, itemHeight);
        }

        @Nullable
        private AnimationEntry replaceAnimations(List<String> animations) {
            this.m_93516_();
            AnimationEntry firstEntry = null;
            for (String animName : animations) {
                AnimationEntry entry = new AnimationEntry(animName);
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

        int rowTop(int index) {
            return super.m_7610_(index);
        }

        int rowHeight() {
            return this.f_93387_;
        }

        @Nullable
        private AnimationEntry findEntry(String animName) {
            for (AnimationEntry entry : this.m_6702_()) {
                if (!entry.animName.equals(animName)) continue;
                return entry;
            }
            return null;
        }
    }

    private class AnimationEntry
    extends ObjectSelectionList.Entry<AnimationEntry> {
        private static final int DELETE_BTN_W = 16;
        private final String animName;

        private AnimationEntry(String animName) {
            this.animName = animName;
        }

        public void m_6311_(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int color = ((Object)((Object)this)).equals(YssHitEditorScreen.this.animationList.m_93511_()) ? -171 : -1;
            graphics.m_280056_(YssHitEditorScreen.this.f_96547_, this.animName, left + 4, top + 5, color, false);
            if (hovering) {
                int bx = left + width - 16 - 4;
                int by = top + (height - 14) / 2;
                boolean hover = mouseX >= bx && mouseX <= bx + 16 && mouseY >= by && mouseY <= by + 14;
                graphics.m_280509_(bx, by, bx + 16, by + 14, hover ? -8376272 : -12566464);
                graphics.m_280056_(YssHitEditorScreen.this.f_96547_, "-", bx + 4, by + 2, -1, false);
            }
        }

        public boolean m_6375_(double mouseX, double mouseY, int button) {
            if (button != 0) {
                return false;
            }
            int rowIndex = YssHitEditorScreen.this.animationList.m_6702_().indexOf((Object)this);
            int rowTop = YssHitEditorScreen.this.animationList.rowTop(rowIndex);
            int rowBottom = rowTop + YssHitEditorScreen.this.animationList.rowHeight();
            if (mouseY >= (double)rowTop && mouseY <= (double)rowBottom && mouseX >= (double)(YssHitEditorScreen.this.animationList.m_5747_() + YssHitEditorScreen.this.animationList.m_5759_() - 16 - 4) && mouseX <= (double)(YssHitEditorScreen.this.animationList.m_5747_() + YssHitEditorScreen.this.animationList.m_5759_())) {
                YssHitEditorScreen.this.requestDeleteAnimation(this.animName);
                return true;
            }
            YssHitEditorScreen.this.selectAnimation(this);
            return true;
        }

        public Component m_142172_() {
            return Component.m_237113_((String)("\u52a8\u753b " + this.animName));
        }
    }

    private record FormLabel(int y, String text, @Nullable String tooltip) {
    }

    private static enum SelectedTrack {
        SEGMENT,
        MOVE,
        HOVER;

    }
}

