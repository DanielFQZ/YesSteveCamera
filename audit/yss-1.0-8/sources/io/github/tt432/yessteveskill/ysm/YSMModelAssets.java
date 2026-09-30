/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.quickbuf.ProtoSource
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapability
 *  com.elfmcys.ysm.geckolib3.core.builder.Animation
 *  com.elfmcys.ysm.model.catalog.client.entry.CatalogModelMetadata
 *  com.elfmcys.ysm.model.catalog.client.entry.ClientCatalogEntry
 *  com.elfmcys.ysm.model.catalog.content.ModelContent
 *  com.elfmcys.ysm.model.catalog.source.CatalogRootKind
 *  com.elfmcys.ysm.model.catalog.source.ModelCatalogSource
 *  com.elfmcys.ysm.model.catalog.source.ModelCatalogSources
 *  com.elfmcys.ysm.model.domain.Hash256
 *  com.elfmcys.ysm.model.resource.client.ModelRenderTarget
 *  com.elfmcys.ysm.model.resource.client.PlayerModelResources
 *  com.elfmcys.ysm.model.resource.client.render.AnimationProtoMapper
 *  com.elfmcys.ysm.model.service.ClientModelService
 *  com.elfmcys.ysm.proto.mixel.asset.model.ModelData
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.Animation
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.AnimationFile
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.GeoModel
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap$Entry
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.ysm;

import com.elfmcys.quickbuf.ProtoSource;
import com.elfmcys.ysm.capability.PlayerAnimatableCapability;
import com.elfmcys.ysm.model.catalog.client.entry.CatalogModelMetadata;
import com.elfmcys.ysm.model.catalog.client.entry.ClientCatalogEntry;
import com.elfmcys.ysm.model.catalog.content.ModelContent;
import com.elfmcys.ysm.model.catalog.source.CatalogRootKind;
import com.elfmcys.ysm.model.catalog.source.ModelCatalogSource;
import com.elfmcys.ysm.model.catalog.source.ModelCatalogSources;
import com.elfmcys.ysm.model.domain.Hash256;
import com.elfmcys.ysm.model.resource.client.ModelRenderTarget;
import com.elfmcys.ysm.model.resource.client.PlayerModelResources;
import com.elfmcys.ysm.model.resource.client.render.AnimationProtoMapper;
import com.elfmcys.ysm.model.service.ClientModelService;
import com.elfmcys.ysm.proto.mixel.asset.model.ModelData;
import com.elfmcys.ysm.proto.mixel.asset.model.data.Animation;
import com.elfmcys.ysm.proto.mixel.asset.model.data.AnimationFile;
import com.elfmcys.ysm.proto.mixel.asset.model.data.GeoModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.tt432.yessteveskill.client.gui.YssPreviewAnimationLoader;
import io.github.tt432.yessteveskill.yss.YssPathHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class YSMModelAssets {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/YSMAssets");
    private static final String ANIMATION_ANNOTATION_PREFIX = "\u2014\u2014";
    private static final String FALLBACK_GROUP = "YSM \u9ed8\u8ba4\u52a8\u753b";
    private final Map<Hash256, CompletableFuture<ModelAssets>> cache = new ConcurrentHashMap<Hash256, CompletableFuture<ModelAssets>>();

    public CompletableFuture<ModelAssets> request(PlayerAnimatableCapability capability) {
        PlayerModelResources resources;
        ModelRenderTarget target = capability.getModelRenderTarget();
        PlayerModelResources playerModelResources = resources = target == null ? null : target.playerResources();
        if (target == null || resources == null) {
            return CompletableFuture.completedFuture(ModelAssets.empty());
        }
        Set availableNames = (Set)resources.animations().keySet().stream().filter(YSMModelAssets::isAnimationName).collect(Collectors.toUnmodifiableSet());
        return this.cache.computeIfAbsent(target.modelHash(), ignored -> this.load(target.modelHash(), target.renderTargetId(), availableNames, availableNames, resources.defaultTextureName()));
    }

    public CompletableFuture<ModelAssets> request(String modelId) {
        Hash256 hash = this.findHashByModelId(modelId);
        if (hash == null) {
            return CompletableFuture.completedFuture(ModelAssets.empty());
        }
        return this.cache.computeIfAbsent(hash, ignored -> this.load(hash, "player", Set.of(), YSMModelAssets.defaultAnimationNames(), null));
    }

    private static Set<String> defaultAnimationNames() {
        ClientModelService service = ClientModelService.instance();
        ModelRenderTarget target = service.defaultRenderTarget();
        if (target == null || target.playerResources() == null) {
            return Set.of();
        }
        return (Set)target.playerResources().animations().keySet().stream().filter(YSMModelAssets::isAnimationName).collect(Collectors.toUnmodifiableSet());
    }

    @Nullable
    private Hash256 findHashByModelId(String modelId) {
        if (modelId == null || modelId.isBlank()) {
            return null;
        }
        String normalized = YssPathHelper.normalizeModelId(modelId);
        Hash256 exact = null;
        Hash256 bySegment = null;
        for (ClientCatalogEntry entry : ClientModelService.instance().catalog().models().values()) {
            if (modelId.equalsIgnoreCase(entry.displayPath())) {
                exact = entry.modelHash();
                break;
            }
            if (bySegment != null || !normalized.equalsIgnoreCase(YssPathHelper.normalizeModelId(entry.displayPath()))) continue;
            bySegment = entry.modelHash();
        }
        return exact != null ? exact : bySegment;
    }

    @Nullable
    public ModelAssets getNow(PlayerAnimatableCapability capability) {
        return this.request(capability).getNow(null);
    }

    private CompletableFuture<ModelAssets> load(Hash256 modelHash, String renderTargetId, Set<String> availableNames, Set<String> fallbackNames, @Nullable String knownTextureName) {
        ClientModelService service = ClientModelService.instance();
        ClientCatalogEntry entry = service.catalog().find(modelHash).orElse(null);
        if (entry == null) {
            return CompletableFuture.completedFuture(this.flatFallback(modelHash, fallbackNames));
        }
        CatalogModelMetadata meta = CatalogModelMetadata.from((ClientCatalogEntry)entry);
        String textureName = knownTextureName != null ? knownTextureName : meta.defaultTexture();
        float widthScale = meta.info().getPlayerSettings().widthScale();
        float heightScale = meta.info().getPlayerSettings().heightScale();
        Path rawDirectory = this.resolveRawDirectory(entry);
        if (rawDirectory != null) {
            try {
                return CompletableFuture.completedFuture(this.loadRaw(modelHash, rawDirectory, availableNames, fallbackNames, textureName, widthScale, heightScale));
            }
            catch (Exception error) {
                LOGGER.warn("Failed to read raw YSM assets from {}", (Object)rawDirectory, (Object)error);
            }
        }
        if (!entry.hasLocalSource()) {
            return CompletableFuture.completedFuture(this.flatFallback(modelHash, fallbackNames));
        }
        try {
            ModelContent content = entry.content();
            ModelData data = content.modelFile().requireRenderTarget(renderTargetId).readDefinition(() -> false, content.chunks());
            return CompletableFuture.completedFuture(this.loadCompiled(modelHash, data, availableNames, fallbackNames, textureName, widthScale, heightScale));
        }
        catch (IOException | RuntimeException error) {
            LOGGER.warn("Failed to read compiled YSM definition for {}", (Object)modelHash, (Object)error);
            return CompletableFuture.completedFuture(this.flatFallback(modelHash, fallbackNames));
        }
    }

    @Nullable
    private Path resolveRawDirectory(ClientCatalogEntry entry) {
        CatalogRootKind rootKind;
        switch (entry.origin()) {
            default: {
                throw new IncompatibleClassChangeError();
            }
            case BUILTIN: {
                CatalogRootKind catalogRootKind = CatalogRootKind.BUILTIN;
                break;
            }
            case CUSTOM: {
                CatalogRootKind catalogRootKind = CatalogRootKind.CUSTOM;
                break;
            }
            case AUTH: {
                CatalogRootKind catalogRootKind = CatalogRootKind.AUTH;
                break;
            }
            case SERVER: {
                CatalogRootKind catalogRootKind = rootKind = null;
            }
        }
        if (rootKind == null) {
            return null;
        }
        for (ModelCatalogSource source : ModelCatalogSources.sources()) {
            Path candidate;
            if (source.rootKind() != rootKind || !Files.isDirectory(candidate = source.path().resolve(entry.displayPath()).normalize(), new LinkOption[0])) continue;
            return candidate;
        }
        return null;
    }

    private ModelAssets loadRaw(Hash256 modelHash, Path directory, Set<String> availableNames, Set<String> fallbackNames, String textureName, float widthScale, float heightScale) throws IOException {
        Path manifestPath = directory.resolve("ysm.json");
        JsonObject manifest = Files.isRegularFile(manifestPath, new LinkOption[0]) ? JsonParser.parseString((String)Files.readString((Path)manifestPath, (Charset)StandardCharsets.UTF_8)).getAsJsonObject() : new JsonObject();
        byte[] texturePng = this.resolveTexturePng(directory, manifest, textureName);
        Path mainPath = this.resolveManifestFile(directory, manifest, "model", "main");
        if (mainPath == null) {
            Path legacyMain = directory.resolve("main.json");
            mainPath = Files.isRegularFile(legacyMain, new LinkOption[0]) ? legacyMain : null;
        }
        byte[] mainJson = mainPath == null ? null : Files.readAllBytes(mainPath);
        LinkedHashMap<String, Path> animationFiles = this.resolveAnimationFiles(directory, manifest);
        if (animationFiles.isEmpty()) {
            try (Stream<Path> paths = Files.walk(directory, 3, new FileVisitOption[0]);){
                paths.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(path -> path.getFileName().toString().endsWith(".animation.json")).sorted().forEach(path -> animationFiles.putIfAbsent(path.getFileName().toString(), (Path)path));
            }
        }
        ArrayList<AnimationGroup> groups = new ArrayList<AnimationGroup>();
        LinkedHashMap<String, com.elfmcys.ysm.geckolib3.core.builder.Animation> animations = new LinkedHashMap<String, com.elfmcys.ysm.geckolib3.core.builder.Animation>();
        LinkedHashSet<String> grouped = new LinkedHashSet<String>();
        for (Map.Entry<String, Path> entry : animationFiles.entrySet()) {
            List<String> names = this.readAnimationNames(entry.getValue(), availableNames);
            if (names.isEmpty()) continue;
            groups.add(new AnimationGroup(entry.getKey(), names));
            grouped.addAll(names);
            try {
                JsonObject root = JsonParser.parseString((String)Files.readString((Path)entry.getValue(), (Charset)StandardCharsets.UTF_8)).getAsJsonObject();
                animations.putAll(YssPreviewAnimationLoader.load(root));
            }
            catch (Exception error) {
                LOGGER.warn("Failed to parse raw YSM animation file {}", (Object)entry.getValue(), (Object)error);
            }
        }
        this.appendFallbackGroup(groups, grouped, fallbackNames);
        groups.sort(Comparator.comparing(AnimationGroup::fileName));
        return new ModelAssets(modelHash, mainJson, null, List.copyOf(groups), animations, textureName, texturePng, widthScale, heightScale);
    }

    @Nullable
    private byte[] resolveTexturePng(Path directory, JsonObject manifest, String textureName) throws IOException {
        Path fallback;
        Object object;
        JsonObject player = this.nestedObject(manifest, "files", "player");
        if (player != null && (object = player.get("texture")) instanceof JsonArray) {
            JsonArray textures = (JsonArray)object;
            for (JsonElement element : textures) {
                Path path;
                if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString() || (path = YSMModelAssets.safeResolve(directory, element.getAsString())) == null) continue;
                return Files.readAllBytes(path);
            }
        }
        if (textureName != null && !textureName.isBlank() && (fallback = YSMModelAssets.safeResolve(directory, "textures/" + textureName + ".png")) != null) {
            return Files.readAllBytes(fallback);
        }
        return null;
    }

    @Nullable
    private static Path safeResolve(Path directory, String relative) {
        Path path = directory.resolve(relative).normalize();
        return path.startsWith(directory.normalize()) && Files.isRegularFile(path, new LinkOption[0]) ? path : null;
    }

    @Nullable
    private Path resolveManifestFile(Path directory, JsonObject manifest, String category, String key) {
        JsonElement jsonElement;
        JsonObject player = this.nestedObject(manifest, "files", "player");
        if (player == null || !((jsonElement = player.get(category)) instanceof JsonObject)) {
            return null;
        }
        JsonObject files = (JsonObject)jsonElement;
        JsonElement value = files.get(key);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
            return null;
        }
        Path path = directory.resolve(value.getAsString()).normalize();
        return path.startsWith(directory.normalize()) && Files.isRegularFile(path, new LinkOption[0]) ? path : null;
    }

    private LinkedHashMap<String, Path> resolveAnimationFiles(Path directory, JsonObject manifest) {
        Object object;
        LinkedHashMap<String, Path> result = new LinkedHashMap<String, Path>();
        JsonObject player = this.nestedObject(manifest, "files", "player");
        if (player == null || !((object = player.get("animation")) instanceof JsonObject)) {
            return result;
        }
        JsonObject files = (JsonObject)object;
        for (Map.Entry entry : files.entrySet()) {
            Path path;
            JsonElement value = (JsonElement)entry.getValue();
            if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString() || !(path = directory.resolve(value.getAsString()).normalize()).startsWith(directory.normalize()) || !Files.isRegularFile(path, new LinkOption[0])) continue;
            result.put(path.getFileName().toString(), path);
        }
        return result;
    }

    @Nullable
    private JsonObject nestedObject(JsonObject root, String first, String second) {
        JsonObject secondObject;
        JsonElement jsonElement = root.get(first);
        if (!(jsonElement instanceof JsonObject)) {
            return null;
        }
        JsonObject firstObject = (JsonObject)jsonElement;
        JsonElement jsonElement2 = firstObject.get(second);
        return jsonElement2 instanceof JsonObject ? (secondObject = (JsonObject)jsonElement2) : null;
    }

    private List<String> readAnimationNames(Path path, Set<String> availableNames) throws IOException {
        JsonObject root = JsonParser.parseString((String)Files.readString((Path)path, (Charset)StandardCharsets.UTF_8)).getAsJsonObject();
        JsonElement jsonElement = root.get("animations");
        if (!(jsonElement instanceof JsonObject)) {
            return List.of();
        }
        JsonObject animations = (JsonObject)jsonElement;
        return animations.keySet().stream().filter(YSMModelAssets::isAnimationName).filter(name -> availableNames.isEmpty() || availableNames.contains(name)).sorted().toList();
    }

    private ModelAssets loadCompiled(Hash256 modelHash, ModelData data, Set<String> availableNames, Set<String> fallbackNames, String textureName, float widthScale, float heightScale) {
        GeoModel mainModel = null;
        ByteBuffer mainModelData = (ByteBuffer)data.geoModels().get((Object)"main");
        if (mainModelData != null) {
            try {
                mainModel = GeoModel.parseFrom((ProtoSource)ProtoSource.newInstance((ByteBuffer)mainModelData));
            }
            catch (IOException error) {
                LOGGER.warn("Failed to decode compiled YSM geometry for {}", (Object)modelHash, (Object)error);
            }
        }
        ArrayList<AnimationGroup> groups = new ArrayList<AnimationGroup>();
        LinkedHashMap<String, com.elfmcys.ysm.geckolib3.core.builder.Animation> animations = new LinkedHashMap<String, com.elfmcys.ysm.geckolib3.core.builder.Animation>();
        LinkedHashSet<String> grouped = new LinkedHashSet<String>();
        if (!data.animationFiles().isEmpty()) {
            for (Object2ObjectMap.Entry file : data.animationFiles().object2ObjectEntrySet()) {
                ArrayList<String> names = new ArrayList<String>();
                if (!((AnimationFile)file.getValue()).animations().isEmpty()) {
                    for (Animation animation : ((AnimationFile)file.getValue()).animations()) {
                        String name;
                        if (animation.nameNullOrBlank() || !YSMModelAssets.isAnimationName(name = animation.name()) || !availableNames.isEmpty() && !availableNames.contains(name)) continue;
                        names.add(name);
                        animations.put(name, AnimationProtoMapper.animation((Animation)animation));
                    }
                }
                if (names.isEmpty()) continue;
                names.sort(String::compareTo);
                Object fileName = ((String)file.getKey()).endsWith(".animation.json") ? (String)file.getKey() : (String)file.getKey() + ".animation.json";
                groups.add(new AnimationGroup((String)fileName, List.copyOf(names)));
                grouped.addAll(names);
            }
        }
        this.appendFallbackGroup(groups, grouped, fallbackNames);
        groups.sort(Comparator.comparing(AnimationGroup::fileName));
        return new ModelAssets(modelHash, null, mainModel, List.copyOf(groups), animations, textureName, null, widthScale, heightScale);
    }

    private void appendFallbackGroup(List<AnimationGroup> groups, Set<String> grouped, Set<String> availableNames) {
        List remaining = availableNames.stream().filter(name -> !grouped.contains(name)).sorted().toList();
        if (!remaining.isEmpty()) {
            groups.add(new AnimationGroup(FALLBACK_GROUP, remaining));
        }
    }

    private ModelAssets flatFallback(Hash256 modelHash, Set<String> fallbackNames) {
        List names = fallbackNames.stream().sorted().toList();
        List groups = names.isEmpty() ? List.of() : List.of((Object)((Object)new AnimationGroup(FALLBACK_GROUP, names)));
        return new ModelAssets(modelHash, null, null, groups, Map.of(), null, null, 1.0f, 1.0f);
    }

    private static boolean isAnimationName(String name) {
        return name != null && !name.isBlank() && !name.startsWith(ANIMATION_ANNOTATION_PREFIX);
    }

    public record ModelAssets(@Nullable Hash256 modelHash, @Nullable byte[] mainJson, @Nullable GeoModel mainModel, List<AnimationGroup> animationGroups, Map<String, com.elfmcys.ysm.geckolib3.core.builder.Animation> animations, @Nullable String textureName, @Nullable byte[] texturePng, float widthScale, float heightScale) {
        @Nullable
        private final byte[] mainJson;
        @Nullable
        private final byte[] texturePng;

        public ModelAssets(@Nullable Hash256 modelHash, @Nullable byte[] mainJson, @Nullable GeoModel mainModel, List<AnimationGroup> animationGroups, Map<String, com.elfmcys.ysm.geckolib3.core.builder.Animation> animations, @Nullable String textureName, @Nullable byte[] texturePng, float widthScale, float heightScale) {
            mainJson = mainJson == null ? null : (byte[])mainJson.clone();
            texturePng = texturePng == null ? null : (byte[])texturePng.clone();
            animationGroups = List.copyOf(animationGroups);
            animations = Map.copyOf(animations);
        }

        private static ModelAssets empty() {
            return new ModelAssets(null, null, null, List.of(), Map.of(), null, null, 1.0f, 1.0f);
        }

        public byte[] mainJson() {
            return this.mainJson == null ? null : (byte[])this.mainJson.clone();
        }

        public byte[] texturePng() {
            return this.texturePng == null ? null : (byte[])this.texturePng.clone();
        }
    }

    public record AnimationGroup(String fileName, List<String> animationNames) {
        public AnimationGroup {
            animationNames = List.copyOf(animationNames);
        }
    }
}

