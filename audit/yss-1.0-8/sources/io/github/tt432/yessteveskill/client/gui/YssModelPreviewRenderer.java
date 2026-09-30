/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.client.gui.CustomGuiPlayerEntity
 *  com.elfmcys.ysm.client.sound.instance.SoundInstanceManager
 *  com.elfmcys.ysm.format.parser.pojo.model.CubeUv
 *  com.elfmcys.ysm.format.parser.pojo.model.FaceUv
 *  com.elfmcys.ysm.format.parser.pojo.model.UvFaces
 *  com.elfmcys.ysm.geckolib3.core.builder.Animation
 *  com.elfmcys.ysm.geckolib3.core.event.predicate.AnimationEvent
 *  com.elfmcys.ysm.geckolib3.core.keyframe.BoneAnimation
 *  com.elfmcys.ysm.geckolib3.core.keyframe.event.EventKeyFrame
 *  com.elfmcys.ysm.geckolib3.core.molang.context.AnimationContext
 *  com.elfmcys.ysm.geckolib3.core.molang.context.MolangContext
 *  com.elfmcys.ysm.geckolib3.core.molang.storage.MolangMemory
 *  com.elfmcys.ysm.geckolib3.core.molang.value.IValue
 *  com.elfmcys.ysm.geckolib3.geo.RenderContext
 *  com.elfmcys.ysm.geckolib3.model.AnimatableEntity
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone
 *  com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel
 *  com.elfmcys.ysm.geckolib3.model.provider.data.EntityModelData
 *  com.elfmcys.ysm.molang.runtime.ExpressionEvaluator
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package io.github.tt432.yessteveskill.client.gui;

import com.elfmcys.ysm.client.gui.CustomGuiPlayerEntity;
import com.elfmcys.ysm.client.sound.instance.SoundInstanceManager;
import com.elfmcys.ysm.format.parser.pojo.model.CubeUv;
import com.elfmcys.ysm.format.parser.pojo.model.FaceUv;
import com.elfmcys.ysm.format.parser.pojo.model.UvFaces;
import com.elfmcys.ysm.geckolib3.core.builder.Animation;
import com.elfmcys.ysm.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.ysm.geckolib3.core.keyframe.BoneAnimation;
import com.elfmcys.ysm.geckolib3.core.keyframe.event.EventKeyFrame;
import com.elfmcys.ysm.geckolib3.core.molang.context.AnimationContext;
import com.elfmcys.ysm.geckolib3.core.molang.context.MolangContext;
import com.elfmcys.ysm.geckolib3.core.molang.storage.MolangMemory;
import com.elfmcys.ysm.geckolib3.core.molang.value.IValue;
import com.elfmcys.ysm.geckolib3.geo.RenderContext;
import com.elfmcys.ysm.geckolib3.model.AnimatableEntity;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoBone;
import com.elfmcys.ysm.geckolib3.model.AnimatedGeoModel;
import com.elfmcys.ysm.geckolib3.model.provider.data.EntityModelData;
import com.elfmcys.ysm.molang.runtime.ExpressionEvaluator;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import io.github.tt432.yessteveskill.client.gui.layout.Rect;
import io.github.tt432.yessteveskill.yss.YssKeyFrameSampler;
import io.github.tt432.yessteveskill.yss.attack.YssAttackProjectLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

final class YssModelPreviewRenderer {
    private static final int[][] BOX_EDGES = new int[][]{{0, 1}, {0, 2}, {0, 4}, {1, 3}, {1, 5}, {2, 3}, {2, 6}, {3, 7}, {4, 5}, {4, 6}, {5, 7}, {6, 7}};
    private static final int[][] FACE_VERTICES = new int[][]{{3, 2, 0, 1}, {6, 7, 5, 4}, {2, 6, 4, 0}, {7, 3, 1, 5}, {3, 7, 6, 2}, {0, 4, 5, 1}};
    private static final int[][] FACE_VERTICES_MIRRORED = new int[][]{{6, 7, 5, 4}, {3, 2, 0, 1}, {2, 6, 4, 0}, {7, 3, 1, 5}, {0, 4, 5, 1}, {3, 7, 6, 2}};
    private static final float MIN_SCALE = 10.0f;
    private static final float MAX_SCALE = 120.0f;
    private static final int FULL_LIGHT = 0xF000F0;
    @Nullable
    private YssAttackProjectLoader.ModelGeometry geometry;
    @Nullable
    private AnimatedGeoModel animatedModel;
    @Nullable
    private String highlightBone;
    @Nullable
    private ResourceLocation texture;
    private float widthScale = 1.0f;
    private float heightScale = 1.0f;
    private String sourceLabel = "";
    private float yaw = 165.0f;
    private float pitch = -5.0f;
    private float panX;
    private float panY;
    private float scale = 35.0f;
    @Nullable
    private Animation previewAnimation;
    private float previewAnimationTime;
    @Nullable
    private AnimatedGeoModel runtimeModel;
    @Nullable
    private CustomGuiPlayerEntity samplingEntity;
    private final Map<Integer, Vector3f[]> animationPose = new HashMap<Integer, Vector3f[]>();
    private static ResourceLocation whiteTexture;
    private static final int[] WHITE_TINT;

    YssModelPreviewRenderer() {
    }

    void setContent(@Nullable YssAttackProjectLoader.ModelGeometry geometry, @Nullable AnimatedGeoModel animatedModel, @Nullable ResourceLocation texture, float widthScale, float heightScale, String sourceLabel, @Nullable String highlightBone) {
        this.geometry = geometry;
        this.animatedModel = animatedModel;
        this.texture = texture;
        this.widthScale = widthScale > 0.0f ? widthScale : 1.0f;
        this.heightScale = heightScale > 0.0f ? heightScale : 1.0f;
        this.sourceLabel = sourceLabel;
        this.highlightBone = highlightBone;
    }

    void setAnimation(@Nullable Animation animation, float timeSeconds) {
        this.previewAnimation = animation;
        this.previewAnimationTime = timeSeconds;
    }

    void setRuntimeModel(@Nullable AnimatedGeoModel model) {
        this.runtimeModel = model;
    }

    void setSamplingEntity(@Nullable CustomGuiPlayerEntity entity) {
        this.samplingEntity = entity;
    }

    @Nullable
    private static ExpressionEvaluator<?> buildSamplingEvaluator(CustomGuiPlayerEntity entity, float tick) {
        try {
            AnimationEvent event = new AnimationEvent((AnimatableEntity)entity, (int)tick, 0.0f, 0.0f, RenderContext.levelImmutable(), new EntityModelData(), null);
            MolangMemory memory = new MolangMemory();
            MolangContext context = new MolangContext((Object)((Player)entity.getEntity()), event, memory, RandomSource.m_216327_(), new SoundInstanceManager());
            AnimationContext animationContext = new AnimationContext();
            animationContext.setAnimTime(tick / 20.0f);
            context.setAnimationContext(animationContext);
            return ExpressionEvaluator.evaluator((Object)context);
        }
        catch (RuntimeException e) {
            return null;
        }
    }

    void render(GuiGraphics graphics, Rect rect) {
        graphics.m_280509_(rect.x(), rect.y(), rect.right(), rect.bottom(), -803726558);
        graphics.m_280509_(rect.x(), rect.y(), rect.right(), rect.y() + 1, -10459019);
        graphics.m_280509_(rect.x(), rect.bottom() - 1, rect.right(), rect.bottom(), -10459019);
        graphics.m_280509_(rect.x(), rect.y(), rect.x() + 1, rect.bottom(), -10459019);
        graphics.m_280509_(rect.right() - 1, rect.y(), rect.right(), rect.bottom(), -10459019);
        if (this.geometry == null) {
            graphics.m_280137_(Minecraft.m_91087_().f_91062_, "\u6682\u65e0\u53ef\u9884\u89c8\u51e0\u4f55", rect.x() + rect.width() / 2, rect.y() + rect.height() / 2, -5592406);
            this.renderLabels(graphics, rect);
            return;
        }
        Window window = Minecraft.m_91087_().m_91268_();
        double guiScale = window.m_85449_();
        RenderSystem.enableScissor((int)((int)((double)rect.x() * guiScale)), (int)((int)((double)window.m_85442_() - (double)rect.bottom() * guiScale)), (int)((int)((double)rect.width() * guiScale)), (int)((int)((double)rect.height() * guiScale)));
        PoseStack viewStack = RenderSystem.getModelViewStack();
        viewStack.m_85836_();
        float posX = (float)rect.x() + (float)rect.width() / 2.0f + this.panX;
        float posY = (float)rect.y() + (float)rect.height() * 0.62f + this.panY;
        viewStack.m_85837_((double)posX, (double)posY, 1250.0);
        viewStack.m_85841_(1.0f, 1.0f, -1.0f);
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack = new PoseStack();
        poseStack.m_85837_(0.0, 0.0, 1000.0);
        poseStack.m_85841_(this.scale, this.scale, this.scale);
        poseStack.m_85837_(0.0, 0.8, 0.0);
        poseStack.m_85841_(this.widthScale, this.heightScale, this.widthScale);
        poseStack.m_252781_(Axis.f_252403_.m_252977_(180.0f));
        poseStack.m_252781_(Axis.f_252529_.m_252977_(-10.0f + this.pitch));
        poseStack.m_252781_(Axis.f_252436_.m_252977_(this.yaw));
        poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0f));
        Lighting.m_166384_();
        this.sampleAnimation();
        this.renderSolidFaces(poseStack);
        Lighting.m_84931_();
        this.renderHighlightWireframe(poseStack);
        viewStack.m_85849_();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableScissor();
        this.renderLabels(graphics, rect);
    }

    private void sampleAnimation() {
        ExpressionEvaluator<?> evaluator;
        this.animationPose.clear();
        if (this.previewAnimation == null) {
            return;
        }
        float tick = this.previewAnimationTime * 20.0f;
        float length = this.previewAnimation.animationLength;
        if (length > 0.0f && tick >= length) {
            tick %= length;
        }
        ExpressionEvaluator<?> expressionEvaluator = evaluator = this.samplingEntity == null ? null : YssModelPreviewRenderer.buildSamplingEvaluator(this.samplingEntity, tick);
        if (evaluator != null) {
            EventKeyFrame frame;
            Iterator iterator = this.previewAnimation.customInstructionKeyframes.iterator();
            while (iterator.hasNext() && !((frame = (EventKeyFrame)iterator.next()).getStartTick() > tick)) {
                for (IValue value : (IValue[])frame.getEventData()) {
                    try {
                        value.eval(evaluator);
                    }
                    catch (RuntimeException runtimeException) {
                        // empty catch block
                    }
                }
            }
        }
        for (BoneAnimation boneAnimation : this.previewAnimation.boneAnimations) {
            Vector3f rotation = YssKeyFrameSampler.sample(boneAnimation.rotationKeyFrames, tick, evaluator);
            Vector3f position = YssKeyFrameSampler.sample(boneAnimation.positionKeyFrames, tick, evaluator);
            Vector3f scale = YssKeyFrameSampler.sample(boneAnimation.scaleKeyFrames, tick, evaluator);
            if (rotation == null && position == null && scale == null) continue;
            this.animationPose.put(boneAnimation.bonePooledName, new Vector3f[]{rotation, position, scale});
        }
    }

    private void renderSolidFaces(PoseStack poseStack) {
        HashMap<Integer, Matrix4f> poses = new HashMap<Integer, Matrix4f>();
        HashSet<Integer> resolving = new HashSet<Integer>();
        ResourceLocation tex = this.texture != null ? this.texture : YssModelPreviewRenderer.whiteTexture();
        ResourceLocation white = YssModelPreviewRenderer.whiteTexture();
        float texW = this.geometry.textureWidth() > 0.0f ? this.geometry.textureWidth() : 64.0f;
        float texH = this.geometry.textureHeight() > 0.0f ? this.geometry.textureHeight() : 64.0f;
        MultiBufferSource.BufferSource buffers = Minecraft.m_91087_().m_91269_().m_110104_();
        Matrix4f poseMatrix = poseStack.m_85850_().m_252922_();
        for (YssAttackProjectLoader.BoneGeometry bone : this.geometry.bones()) {
            Matrix4f bonePose;
            AnimatedGeoBone animatedBone;
            AnimatedGeoBone animatedGeoBone = animatedBone = this.animatedModel == null ? null : (AnimatedGeoBone)this.animatedModel.getBoneMap().get(bone.pooledName());
            if (this.animatedModel != null && (animatedBone == null || animatedBone.areCubesHidden()) || (bonePose = this.resolveBonePose(bone, poses, resolving)) == null) continue;
            for (YssAttackProjectLoader.CubeGeometry cube : bone.cubes()) {
                VertexConsumer consumer;
                ResourceLocation useTex;
                float[] vertices = cube.vertices();
                if (vertices.length < 24) continue;
                YssAttackProjectLoader.MeshGeometry mesh = cube.mesh();
                if (mesh != null) {
                    boolean hasTexture = this.texture != null;
                    ResourceLocation useTex2 = hasTexture ? tex : white;
                    VertexConsumer consumer2 = buffers.m_6299_(RenderType.m_110458_((ResourceLocation)useTex2));
                    int[] tint = hasTexture ? WHITE_TINT : YssModelPreviewRenderer.boneColor(bone.name());
                    YssModelPreviewRenderer.addMeshQuads(consumer2, poseMatrix, bonePose, mesh, tint);
                    continue;
                }
                Vector3f[] corners = YssModelPreviewRenderer.toCorners(vertices);
                boolean mirroredLayout = bone.mirror() || cube.mirror();
                boolean cubeMirror = cube.mirror();
                CubeUv uv = cube.uv();
                if (uv != null && uv.boxUv != null && uv.boxUv.length >= 2 && cube.rawSize() != null) {
                    useTex = this.texture != null ? tex : white;
                    consumer = buffers.m_6299_(RenderType.m_110458_((ResourceLocation)useTex));
                    YssModelPreviewRenderer.addBoxQuads(consumer, poseMatrix, bonePose, corners, uv.boxUv, cube.rawSize(), mirroredLayout, cubeMirror, texW, texH, this.texture != null);
                    continue;
                }
                if (uv != null && uv.perFaceUv != null) {
                    useTex = this.texture != null ? tex : white;
                    consumer = buffers.m_6299_(RenderType.m_110458_((ResourceLocation)useTex));
                    YssModelPreviewRenderer.addPerFaceQuads(consumer, poseMatrix, bonePose, corners, uv.perFaceUv, mirroredLayout, cubeMirror, texW, texH, this.texture != null);
                    continue;
                }
                VertexConsumer consumer3 = buffers.m_6299_(RenderType.m_110458_((ResourceLocation)white));
                int[] bc = YssModelPreviewRenderer.boneColor(bone.name());
                YssModelPreviewRenderer.addSolidQuads(consumer3, poseMatrix, bonePose, corners, bc);
            }
        }
        buffers.m_109911_();
    }

    private void renderHighlightWireframe(PoseStack poseStack) {
        if (this.highlightBone == null || this.geometry == null) {
            return;
        }
        if (this.geometry.bone(this.highlightBone) == null) {
            return;
        }
        LinkedHashSet<String> highlight = new LinkedHashSet<String>();
        highlight.add(this.highlightBone);
        this.collectDescendants(this.highlightBone, highlight);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(() -> GameRenderer.m_172811_());
        Tesselator tesselator = Tesselator.m_85913_();
        BufferBuilder builder = tesselator.m_85915_();
        builder.m_166779_(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.f_85815_);
        Matrix4f pose = poseStack.m_85850_().m_252922_();
        HashMap<Integer, Matrix4f> poses = new HashMap<Integer, Matrix4f>();
        HashSet<Integer> resolving = new HashSet<Integer>();
        for (String boneName : highlight) {
            Matrix4f bonePose;
            YssAttackProjectLoader.BoneGeometry bone = this.geometry.bone(boneName);
            if (bone == null || (bonePose = this.resolveBonePose(bone, poses, resolving)) == null) continue;
            boolean self = boneName.equals(this.highlightBone);
            int red = self ? 90 : 255;
            int green = self ? 255 : 235;
            int blue = self ? 120 : 80;
            for (YssAttackProjectLoader.CubeGeometry cube : bone.cubes()) {
                float[] vertices = cube.vertices();
                if (vertices.length < 24) continue;
                Vector3f[] transformed = new Vector3f[8];
                for (int index = 0; index < 8; ++index) {
                    int offset = index * 3;
                    Vector4f value = new Vector4f(vertices[offset], vertices[offset + 1], vertices[offset + 2], 1.0f);
                    bonePose.transform(value);
                    transformed[index] = new Vector3f(value.x, value.y, value.z);
                }
                for (int[] edge : BOX_EDGES) {
                    Vector3f fv = transformed[edge[0]];
                    Vector3f tv = transformed[edge[1]];
                    builder.m_252986_(pose, fv.x, fv.y, fv.z).m_6122_(red, green, blue, 255).m_5752_();
                    builder.m_252986_(pose, tv.x, tv.y, tv.z).m_6122_(red, green, blue, 255).m_5752_();
                }
            }
        }
        tesselator.m_85914_();
        RenderSystem.enableDepthTest();
    }

    private void collectDescendants(String boneName, Set<String> out) {
        for (YssAttackProjectLoader.BoneGeometry bone : this.geometry.bones()) {
            if (!boneName.equals(bone.parent())) continue;
            out.add(bone.name());
            this.collectDescendants(bone.name(), out);
        }
    }

    private void renderLabels(GuiGraphics graphics, Rect rect) {
        Font font = Minecraft.m_91087_().f_91062_;
        graphics.m_280056_(font, this.sourceLabel, rect.x() + 4, rect.y() + 4, -2047872, false);
        int n = rect.x() + 4;
        int n2 = rect.bottom();
        Objects.requireNonNull(font);
        graphics.m_280056_(font, "\u5de6\u62d6\u65cb\u8f6c  \u53f3\u62d6\u4f4d\u79fb  \u6eda\u8f6e\u7f29\u653e", n, n2 - 9 - 4, -7431768, false);
        if (this.highlightBone != null) {
            String string = "\u9ad8\u4eae\uff1a" + this.highlightBone;
            int n3 = rect.x() + 4;
            int n4 = rect.y();
            Objects.requireNonNull(font);
            graphics.m_280056_(font, string, n3, n4 + 9 + 6, -5296, false);
        }
    }

    boolean contains(Rect rect, double mouseX, double mouseY) {
        return mouseX >= (double)rect.x() && mouseX <= (double)rect.right() && mouseY >= (double)rect.y() && mouseY <= (double)rect.bottom();
    }

    boolean drag(Rect rect, double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.contains(rect, mouseX, mouseY)) {
            return false;
        }
        if (button == 0) {
            this.yaw += (float)(dragX * 1.5);
            this.pitch = Mth.m_14036_((float)(this.pitch - (float)dragY), (float)-90.0f, (float)90.0f);
            return true;
        }
        if (button == 1) {
            this.panX += (float)dragX;
            this.panY += (float)dragY;
            return true;
        }
        return false;
    }

    boolean scroll(Rect rect, double mouseX, double mouseY, double delta) {
        if (delta == 0.0 || !this.contains(rect, mouseX, mouseY)) {
            return false;
        }
        this.scale = Mth.m_14036_((float)(this.scale + (float)delta * 0.07f * this.scale), (float)10.0f, (float)120.0f);
        return true;
    }

    float yaw() {
        return this.yaw;
    }

    float pitch() {
        return this.pitch;
    }

    float panX() {
        return this.panX;
    }

    float panY() {
        return this.panY;
    }

    float scale() {
        return this.scale;
    }

    private static ResourceLocation whiteTexture() {
        if (whiteTexture == null) {
            NativeImage image = new NativeImage(1, 1, true);
            image.m_84988_(0, 0, -1);
            DynamicTexture dynamic = new DynamicTexture(image);
            whiteTexture = Minecraft.m_91087_().m_91097_().m_118490_("yss_white", dynamic);
        }
        return whiteTexture;
    }

    private static Vector3f[] toCorners(float[] vertices) {
        Vector3f[] corners = new Vector3f[8];
        for (int index = 0; index < 8; ++index) {
            int offset = index * 3;
            corners[index] = new Vector3f(vertices[offset], vertices[offset + 1], vertices[offset + 2]);
        }
        return corners;
    }

    private static Vector3f transformVertex(Vector3f v, Matrix4f bonePose) {
        Vector4f value = new Vector4f(v.x, v.y, v.z, 1.0f);
        bonePose.transform(value);
        return new Vector3f(value.x, value.y, value.z);
    }

    private static void addBoxQuads(VertexConsumer consumer, Matrix4f poseMatrix, Matrix4f bonePose, Vector3f[] corners, float[] boxUv, float[] rawSize, boolean mirroredLayout, boolean cubeMirror, float texW, float texH, boolean hasTexture) {
        float x = (float)Math.floor(rawSize[0]);
        float y = (float)Math.floor(rawSize[1]);
        float z = (float)Math.floor(rawSize[2]);
        float u = boxUv[0];
        float vv = boxUv[1];
        YssModelPreviewRenderer.addFace(consumer, poseMatrix, bonePose, corners, mirroredLayout ? FACE_VERTICES_MIRRORED[0] : FACE_VERTICES[0], u + z + x, vv + z, z, y, cubeMirror, texW, texH, hasTexture);
        YssModelPreviewRenderer.addFace(consumer, poseMatrix, bonePose, corners, mirroredLayout ? FACE_VERTICES_MIRRORED[1] : FACE_VERTICES[1], u, vv + z, z, y, cubeMirror, texW, texH, hasTexture);
        YssModelPreviewRenderer.addFace(consumer, poseMatrix, bonePose, corners, mirroredLayout ? FACE_VERTICES_MIRRORED[2] : FACE_VERTICES[2], u + z, vv + z, x, y, cubeMirror, texW, texH, hasTexture);
        YssModelPreviewRenderer.addFace(consumer, poseMatrix, bonePose, corners, mirroredLayout ? FACE_VERTICES_MIRRORED[3] : FACE_VERTICES[3], u + z + x + z, vv + z, x, y, cubeMirror, texW, texH, hasTexture);
        YssModelPreviewRenderer.addFace(consumer, poseMatrix, bonePose, corners, mirroredLayout ? FACE_VERTICES_MIRRORED[4] : FACE_VERTICES[4], u + z, vv, x, z, cubeMirror, texW, texH, hasTexture);
        YssModelPreviewRenderer.addFace(consumer, poseMatrix, bonePose, corners, mirroredLayout ? FACE_VERTICES_MIRRORED[5] : FACE_VERTICES[5], u + z + x, vv + z, x, -z, cubeMirror, texW, texH, hasTexture);
    }

    private static void addPerFaceQuads(VertexConsumer consumer, Matrix4f poseMatrix, Matrix4f bonePose, Vector3f[] corners, UvFaces faces, boolean mirroredLayout, boolean cubeMirror, float texW, float texH, boolean hasTexture) {
        FaceUv[] defs = new FaceUv[]{faces.west, faces.east, faces.north, faces.south, faces.up, faces.down};
        for (int i = 0; i < 6; ++i) {
            FaceUv face = defs[i];
            if (face == null || face.uv == null || face.uvSize == null || face.uv.length < 2 || face.uvSize.length < 2) continue;
            int[] idx = mirroredLayout ? FACE_VERTICES_MIRRORED[i] : FACE_VERTICES[i];
            YssModelPreviewRenderer.addFace(consumer, poseMatrix, bonePose, corners, idx, face.uv[0], face.uv[1], face.uvSize[0], face.uvSize[1], cubeMirror, texW, texH, hasTexture);
        }
    }

    private static void addSolidQuads(VertexConsumer consumer, Matrix4f poseMatrix, Matrix4f bonePose, Vector3f[] corners, int[] bc) {
        for (int i = 0; i < 6; ++i) {
            int[] idx = FACE_VERTICES[i];
            Vector3f a = YssModelPreviewRenderer.transformVertex(corners[idx[0]], bonePose);
            Vector3f b = YssModelPreviewRenderer.transformVertex(corners[idx[1]], bonePose);
            Vector3f c = YssModelPreviewRenderer.transformVertex(corners[idx[2]], bonePose);
            Vector3f d = YssModelPreviewRenderer.transformVertex(corners[idx[3]], bonePose);
            Vector3f[] tv = new Vector3f[]{a, b, c, d};
            for (int vi = 0; vi < 4; ++vi) {
                Vector3f p = tv[vi];
                consumer.m_252986_(poseMatrix, p.x, p.y, p.z).m_6122_(bc[0], bc[1], bc[2], 255).m_7421_(0.0f, 0.0f).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(0.0f, 1.0f, 0.0f).m_5752_();
            }
        }
    }

    private static void addMeshQuads(VertexConsumer consumer, Matrix4f poseMatrix, Matrix4f bonePose, YssAttackProjectLoader.MeshGeometry mesh, int[] tint) {
        float[] pos = mesh.pos();
        float[] uv = mesh.uv();
        int[] posIndices = mesh.posIndices();
        int[] uvIndices = mesh.uvIndices();
        Vector4f transformed = new Vector4f();
        Vector3f[] quad = new Vector3f[4];
        float[] quadUv = new float[8];
        for (int face = 0; face < mesh.faceCount(); ++face) {
            Vector3f ac;
            int base = face * 4;
            for (int corner = 0; corner < 4; ++corner) {
                int posIndex = posIndices[base + corner] * 3;
                int uvIndex = uvIndices[base + corner] * 2;
                if (posIndex + 2 >= pos.length || uvIndex + 1 >= uv.length) break;
                transformed.set(pos[posIndex], pos[posIndex + 1], pos[posIndex + 2], 1.0f);
                bonePose.transform(transformed);
                quad[corner] = new Vector3f(transformed.x, transformed.y, transformed.z);
                quadUv[corner * 2] = uv[uvIndex];
                quadUv[corner * 2 + 1] = uv[uvIndex + 1];
            }
            if (quad[3] == null) continue;
            Vector3f ab = new Vector3f((Vector3fc)quad[1]).sub((Vector3fc)quad[0]);
            Vector3f normal = ab.cross((Vector3fc)(ac = new Vector3f((Vector3fc)quad[2]).sub((Vector3fc)quad[0])));
            float length = normal.length();
            if (length > 1.0E-6f) {
                normal.div(length);
            } else {
                normal.set(0.0f, 1.0f, 0.0f);
            }
            for (int corner = 0; corner < 4; ++corner) {
                Vector3f p = quad[corner];
                consumer.m_252986_(poseMatrix, p.x, p.y, p.z).m_6122_(tint[0], tint[1], tint[2], 255).m_7421_(quadUv[corner * 2], quadUv[corner * 2 + 1]).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(normal.x, normal.y, normal.z).m_5752_();
            }
            quad[3] = null;
        }
    }

    private static void addFace(VertexConsumer consumer, Matrix4f poseMatrix, Matrix4f bonePose, Vector3f[] corners, int[] idx, float u0, float v0, float us, float vs, boolean mirror, float texW, float texH, boolean hasTexture) {
        Vector3f ac;
        Vector3f a = YssModelPreviewRenderer.transformVertex(corners[idx[0]], bonePose);
        Vector3f b = YssModelPreviewRenderer.transformVertex(corners[idx[1]], bonePose);
        Vector3f c = YssModelPreviewRenderer.transformVertex(corners[idx[2]], bonePose);
        Vector3f d = YssModelPreviewRenderer.transformVertex(corners[idx[3]], bonePose);
        float u1n = u0 / texW;
        float u2n = (u0 + us) / texW;
        float v1n = v0 / texH;
        float v2n = (v0 + vs) / texH;
        float[] uv = !mirror ? new float[]{u2n, v1n, u1n, v1n, u1n, v2n, u2n, v2n} : new float[]{u1n, v1n, u2n, v1n, u2n, v2n, u1n, v2n};
        Vector3f ab = new Vector3f((Vector3fc)b).sub((Vector3fc)a);
        Vector3f normal = ab.cross((Vector3fc)(ac = new Vector3f((Vector3fc)c).sub((Vector3fc)a)));
        float length = normal.length();
        if (length > 1.0E-6f) {
            normal.div(length);
        } else {
            normal.set(0.0f, 1.0f, 0.0f);
        }
        Vector3f[] tv = new Vector3f[]{a, b, c, d};
        for (int vi = 0; vi < 4; ++vi) {
            Vector3f p = tv[vi];
            consumer.m_252986_(poseMatrix, p.x, p.y, p.z).m_6122_(255, 255, 255, 255).m_7421_(uv[vi * 2], uv[vi * 2 + 1]).m_86008_(OverlayTexture.f_118083_).m_85969_(0xF000F0).m_5601_(normal.x, normal.y, normal.z).m_5752_();
        }
    }

    private static int[] boneColor(String name) {
        int hash = name == null ? 0 : name.hashCode();
        float h = (float)(hash & 0xFFFF) / 65535.0f;
        return YssModelPreviewRenderer.hsbToRgb(h, 0.5f, 0.85f);
    }

    private static int[] hsbToRgb(float h, float s, float v) {
        float gg;
        float rr;
        float f = h * 6.0f;
        int i = (int)f;
        float f1 = f - (float)i;
        float p = v * (1.0f - s);
        float q = v * (1.0f - s * f1);
        float t = v * (1.0f - s * (1.0f - f1));
        return new int[]{(int)(rr * 255.0f), (int)(gg * 255.0f), (int)((switch (i % 6) {
            case 0 -> {
                rr = v;
                gg = t;
                yield p;
            }
            case 1 -> {
                rr = q;
                gg = v;
                yield p;
            }
            case 2 -> {
                rr = p;
                gg = v;
                yield t;
            }
            case 3 -> {
                rr = p;
                gg = q;
                yield v;
            }
            case 4 -> {
                rr = t;
                gg = p;
                yield v;
            }
            default -> {
                rr = v;
                gg = p;
                yield q;
            }
        }) * 255.0f)};
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private Matrix4f resolveBonePose(YssAttackProjectLoader.BoneGeometry bone, Map<Integer, Matrix4f> poses, Set<Integer> resolving) {
        Matrix4f cached = poses.get(bone.pooledName());
        if (cached != null) {
            return cached;
        }
        if (!resolving.add(bone.pooledName())) {
            return null;
        }
        try {
            Matrix4f pose;
            AnimatedGeoBone animatedBone;
            AnimatedGeoBone animatedGeoBone = animatedBone = this.animatedModel == null ? null : (AnimatedGeoBone)this.animatedModel.getBoneMap().get(bone.pooledName());
            if (bone.parent() == null) {
                pose = new Matrix4f();
            } else {
                YssAttackProjectLoader.BoneGeometry parent;
                YssAttackProjectLoader.BoneGeometry boneGeometry = parent = this.geometry == null ? null : this.geometry.bone(bone.parent());
                if (parent == null) {
                    Matrix4f matrix4f = null;
                    return matrix4f;
                }
                Matrix4f parentPose = this.resolveBonePose(parent, poses, resolving);
                if (parentPose == null) {
                    Matrix4f matrix4f = null;
                    return matrix4f;
                }
                pose = new Matrix4f((Matrix4fc)parentPose);
            }
            this.applyLocalPose(pose, bone, animatedBone);
            poses.put(bone.pooledName(), pose);
            Matrix4f matrix4f = pose;
            return matrix4f;
        }
        finally {
            resolving.remove(bone.pooledName());
        }
    }

    private void applyLocalPose(Matrix4f pose, YssAttackProjectLoader.BoneGeometry bone, @Nullable AnimatedGeoBone animatedBone) {
        float scaleZ;
        float scaleY;
        float scaleX;
        float rotationZ;
        float rotationY;
        float rotationX;
        float positionZ;
        float positionY;
        float positionX;
        float pivotZ;
        float pivotY;
        float pivotX;
        Vector3f[] sampled;
        float mu = 0.0625f;
        Vector3f[] vector3fArray = sampled = this.previewAnimation == null ? null : this.animationPose.get(bone.pooledName());
        if (this.runtimeModel != null && animatedBone != null) {
            pivot = animatedBone.getPivot();
            pivotX = pivot.x;
            pivotY = pivot.y;
            pivotZ = pivot.z;
            positionX = animatedBone.getPositionX();
            positionY = animatedBone.getPositionY();
            positionZ = animatedBone.getPositionZ();
            rotationX = animatedBone.getRotationX();
            rotationY = animatedBone.getRotationY();
            rotationZ = animatedBone.getRotationZ();
            scaleX = animatedBone.getScaleX();
            scaleY = animatedBone.getScaleY();
            scaleZ = animatedBone.getScaleZ();
        } else if (animatedBone != null) {
            pivot = animatedBone.getPivot();
            pivotX = pivot.x;
            pivotY = pivot.y;
            pivotZ = pivot.z;
            Vector3f bindRotation = animatedBone.getInitialRotation();
            rotationX = bindRotation.x;
            rotationY = bindRotation.y;
            rotationZ = bindRotation.z;
            positionX = 0.0f;
            positionY = 0.0f;
            positionZ = 0.0f;
            scaleX = 1.0f;
            scaleY = 1.0f;
            scaleZ = 1.0f;
        } else {
            pivotX = bone.pivotX();
            pivotY = bone.pivotY();
            pivotZ = bone.pivotZ();
            rotationX = bone.initialRotationX();
            rotationY = bone.initialRotationY();
            rotationZ = bone.initialRotationZ();
            positionX = 0.0f;
            positionY = 0.0f;
            positionZ = 0.0f;
            scaleX = 1.0f;
            scaleY = 1.0f;
            scaleZ = 1.0f;
        }
        if (sampled != null) {
            Vector3f scale;
            Vector3f position;
            Vector3f rotation = sampled[0];
            if (rotation != null) {
                rotationX = rotation.x;
                rotationY = rotation.y;
                rotationZ = rotation.z;
            }
            if ((position = sampled[1]) != null) {
                positionX = position.x;
                positionY = position.y;
                positionZ = position.z;
            }
            if ((scale = sampled[2]) != null) {
                scaleX = scale.x;
                scaleY = scale.y;
                scaleZ = scale.z;
            }
        }
        pose.translate((pivotX - positionX) * mu, (pivotY + positionY) * mu, (pivotZ + positionZ) * mu).rotateZYX(rotationZ, rotationY, rotationX).scale(scaleX, scaleY, scaleZ).translate(-pivotX * mu, -pivotY * mu, -pivotZ * mu);
    }

    void reset() {
    }

    static {
        WHITE_TINT = new int[]{255, 255, 255};
    }
}

