/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.yss.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.tt432.yessteveskill.combat.config.AnimationHitConfig;
import io.github.tt432.yessteveskill.combat.config.HitProjectConfig;
import io.github.tt432.yessteveskill.combat.config.HitSegment;
import io.github.tt432.yessteveskill.yss.YssPathHelper;
import io.github.tt432.yessteveskill.yss.project.YssBinaryProjectIO;
import io.github.tt432.yessteveskill.yss.project.YssProject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.invoke.CallSite;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YssProjectManager {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/YssProjectManager");
    private static final String MAIN_FILE_NAME = "main.json";
    private static final String HIT_FILE_NAME = "hit.json";
    private static final String ANIMATIONS_FILE_NAME = "animations.json";
    public static final YssProjectManager INSTANCE = new YssProjectManager();

    private YssProjectManager() {
    }

    public List<YssProject> loadProjects() {
        List list;
        block10: {
            Path root = YssPathHelper.resolveRootDirectory();
            try {
                Files.createDirectories(root, new FileAttribute[0]);
            }
            catch (IOException e) {
                LOGGER.warn("Failed to create yss root directory {}", (Object)root, (Object)e);
                return List.of();
            }
            Stream<Path> stream = Files.list(root);
            try {
                list = stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).sorted().map(this::loadProjectSafely).toList();
                if (stream == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    LOGGER.warn("Failed to list yss projects under {}", (Object)root, (Object)e);
                    return List.of();
                }
            }
            stream.close();
        }
        return list;
    }

    public YssProject loadProject(Path directory) {
        return this.loadProjectSafely(directory);
    }

    public ExportResult exportProject(YssProject project) throws IOException {
        ParsedProject parsed = this.parseProject(project.directory());
        if (parsed.errorMessage() != null) {
            throw new IOException(parsed.errorMessage());
        }
        Path exportPath = YssPathHelper.resolveProjectBinaryFile(parsed.modelId());
        Map<String, byte[]> files = this.buildExportFiles(parsed);
        YssBinaryProjectIO.write(exportPath, parsed.modelId(), files);
        return new ExportResult(exportPath, parsed.attackBones().size(), parsed.attackAnimations().size());
    }

    public Path createProject(String modelId) throws IOException {
        Path dir = YssPathHelper.resolveProjectDirectory(modelId);
        if (Files.exists(dir, new LinkOption[0])) {
            throw new IOException("\u5de5\u7a0b\u5df2\u5b58\u5728\uff1a" + dir.getFileName());
        }
        Files.createDirectories(dir, new FileAttribute[0]);
        return dir;
    }

    public Path copyProject(Path sourceDir, String newModelId) throws IOException {
        Path targetDir = YssPathHelper.resolveProjectDirectory(newModelId);
        if (Files.exists(targetDir, new LinkOption[0])) {
            throw new IOException("\u5de5\u7a0b\u5df2\u5b58\u5728\uff1a" + targetDir.getFileName());
        }
        if (Files.isSameFile(sourceDir, targetDir)) {
            throw new IOException("\u6e90\u5de5\u7a0b\u4e0e\u76ee\u6807\u76f8\u540c");
        }
        Files.createDirectories(targetDir, new FileAttribute[0]);
        try (Stream<Path> stream = Files.walk(sourceDir, new FileVisitOption[0]);){
            for (Path source : stream.toList()) {
                Path target = targetDir.resolve(sourceDir.relativize(source));
                Files.copy(source, target, new CopyOption[0]);
            }
        }
        return targetDir;
    }

    public void deleteProject(Path directory) throws IOException {
        if (!Files.exists(directory, new LinkOption[0])) {
            return;
        }
        try (Stream<Path> stream = Files.walk(directory, new FileVisitOption[0]);){
            for (Path path : stream.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    private Map<String, byte[]> buildExportFiles(ParsedProject parsed) {
        MainPruneResult prunedMain = this.pruneMainRoot(parsed.mainRoot(), parsed.modelId(), parsed.attackBones());
        JsonObject prunedHit = this.pruneHitRoot(parsed.hitRoot(), parsed.attackBones(), parsed.attackAnimations());
        LinkedHashMap<String, byte[]> files = new LinkedHashMap<String, byte[]>();
        if (parsed.hasExternalModel()) {
            files.put(MAIN_FILE_NAME, this.toJsonBytes(prunedMain.root()));
        }
        files.put(HIT_FILE_NAME, this.toJsonBytes(prunedHit));
        if (parsed.hasExternalAnimations()) {
            JsonObject prunedAnimations = this.pruneAnimationsRoot(parsed.animationsRoot(), parsed.attackAnimations(), parsed.hasExternalModel() ? prunedMain.relevantBones() : null);
            files.put(ANIMATIONS_FILE_NAME, this.toJsonBytes(prunedAnimations));
        }
        return files;
    }

    private YssProject loadProjectSafely(Path directory) {
        try {
            ParsedProject parsed = this.parseProject(directory);
            return new YssProject(parsed.directory(), parsed.name(), parsed.modelId(), parsed.attackBones(), parsed.attackAnimations(), parsed.errorMessage());
        }
        catch (Exception e) {
            LOGGER.warn("Failed to parse yss project {}", (Object)directory, (Object)e);
            String name = directory.getFileName().toString();
            return new YssProject(directory, name, YssPathHelper.normalizeModelId(name), List.of(), List.of(), "\u8bfb\u53d6\u9879\u76ee\u5931\u8d25\uff1a" + e.getMessage());
        }
    }

    private ParsedProject parseProject(Path directory) throws IOException {
        String name = directory.getFileName().toString();
        String modelId = YssPathHelper.normalizeModelId(name);
        Path mainPath = directory.resolve(MAIN_FILE_NAME);
        boolean hasExternalModel = Files.isRegularFile(mainPath, new LinkOption[0]);
        JsonObject mainRoot = this.readJsonObjectOptional(mainPath);
        JsonObject hitRoot = this.readJsonObjectOptional(directory.resolve(HIT_FILE_NAME));
        Path animationsPath = directory.resolve(ANIMATIONS_FILE_NAME);
        boolean hasExternalAnimations = Files.isRegularFile(animationsPath, new LinkOption[0]);
        JsonObject animationsRoot = this.readJsonObjectOptional(animationsPath);
        JsonElement selectedGeometry = this.selectGeometryElement(mainRoot, modelId);
        List<String> availableBones = this.extractModelBones(selectedGeometry);
        HitProjectConfig hitConfig = HitProjectConfig.parse(hitRoot);
        ArrayList<String> attackBones = new ArrayList<String>(YssProjectManager.collectAttackBones(hitConfig));
        List<String> availableAnimations = this.extractAnimationNames(animationsRoot);
        ArrayList<String> attackAnimations = new ArrayList<String>(hitConfig.animations().keySet());
        List<String> missingBones = hasExternalModel ? this.missingEntries(attackBones, availableBones) : List.of();
        List<String> missingAnimations = hasExternalAnimations ? this.missingEntries(attackAnimations, availableAnimations) : List.of();
        String errorMessage = this.buildErrorMessage(missingBones, missingAnimations);
        return new ParsedProject(directory, name, modelId, mainRoot, hitRoot, animationsRoot, hasExternalModel, hasExternalAnimations, availableBones, attackBones, availableAnimations, attackAnimations, errorMessage);
    }

    private MainPruneResult pruneMainRoot(JsonObject root, String modelId, List<String> attackBones) {
        JsonElement geometryElement = this.selectGeometryElement(root, modelId);
        if (geometryElement == null) {
            return new MainPruneResult(root.deepCopy(), Set.of());
        }
        JsonObject prunedRoot = root.deepCopy();
        GeometryPruneResult prunedGeometry = this.pruneGeometry(geometryElement, attackBones);
        if (prunedGeometry == null) {
            return new MainPruneResult(root.deepCopy(), Set.copyOf(this.extractModelBones(geometryElement)));
        }
        JsonArray filteredGeometries = new JsonArray();
        filteredGeometries.add((JsonElement)prunedGeometry.geometry());
        prunedRoot.add("minecraft:geometry", (JsonElement)filteredGeometries);
        return new MainPruneResult(prunedRoot, prunedGeometry.relevantBones());
    }

    @Nullable
    private JsonElement selectGeometryElement(JsonObject root, @Nullable String modelId) {
        JsonElement geometryElement = root.get("minecraft:geometry");
        if (geometryElement == null) {
            return null;
        }
        if (!geometryElement.isJsonArray()) {
            return geometryElement;
        }
        JsonArray geometries = geometryElement.getAsJsonArray();
        if (geometries.isEmpty()) {
            return null;
        }
        if (modelId != null) {
            String normalizedModelId = YssPathHelper.normalizeModelId(modelId).toLowerCase();
            for (JsonElement entry : geometries) {
                String identifier;
                JsonObject geometryObject;
                JsonObject description;
                if (!entry.isJsonObject() || (description = (geometryObject = entry.getAsJsonObject()).getAsJsonObject("description")) == null || (identifier = this.getString(description, "identifier")) == null || !identifier.toLowerCase().contains(normalizedModelId)) continue;
                return entry;
            }
        }
        for (JsonElement entry : geometries) {
            if (!entry.isJsonObject()) continue;
            return entry;
        }
        return null;
    }

    @Nullable
    private GeometryPruneResult pruneGeometry(JsonElement geometryElement, List<String> attackBones) {
        if (!geometryElement.isJsonObject()) {
            return null;
        }
        JsonObject geometryObject = geometryElement.getAsJsonObject();
        JsonElement bonesElement = geometryObject.get("bones");
        if (!(bonesElement instanceof JsonArray)) {
            return null;
        }
        JsonArray boneArray = (JsonArray)bonesElement;
        LinkedHashMap<String, Object> bonesByName = new LinkedHashMap<String, Object>();
        HashMap<String, String> parentByBone = new HashMap<String, String>();
        HashMap<String, List<String>> childrenByBone = new HashMap<String, List<String>>();
        for (JsonElement boneElement : boneArray) {
            Object boneObject;
            String boneName;
            if (!boneElement.isJsonObject() || (boneName = this.getString((JsonObject)(boneObject = boneElement.getAsJsonObject()), "name")) == null || boneName.isBlank()) continue;
            bonesByName.put(boneName, boneObject);
            String parentName = this.getString((JsonObject)boneObject, "parent");
            if (parentName == null || parentName.isBlank()) continue;
            parentByBone.put(boneName, parentName);
            childrenByBone.computeIfAbsent(parentName, ignored -> new ArrayList()).add(boneName);
        }
        LinkedHashSet<String> relevantBones = this.collectRelevantBones(attackBones, bonesByName.keySet(), parentByBone, childrenByBone);
        if (relevantBones.isEmpty()) {
            return null;
        }
        JsonArray filteredBones = new JsonArray();
        for (JsonElement boneElement : boneArray) {
            JsonObject boneObject;
            String boneName;
            if (!boneElement.isJsonObject() || (boneName = this.getString(boneObject = boneElement.getAsJsonObject(), "name")) == null || !relevantBones.contains(boneName)) continue;
            filteredBones.add((JsonElement)boneObject.deepCopy());
        }
        JsonObject prunedGeometry = geometryObject.deepCopy();
        prunedGeometry.add("bones", (JsonElement)filteredBones);
        return new GeometryPruneResult(prunedGeometry, Set.copyOf(relevantBones));
    }

    private LinkedHashSet<String> collectRelevantBones(List<String> attackBones, Set<String> availableBones, Map<String, String> parentByBone, Map<String, List<String>> childrenByBone) {
        LinkedHashSet<String> relevantBones = new LinkedHashSet<String>();
        for (String attackBone : attackBones) {
            if (!availableBones.contains(attackBone)) continue;
            this.addAncestors(attackBone, availableBones, parentByBone, relevantBones);
            this.addDescendants(attackBone, availableBones, childrenByBone, relevantBones);
        }
        return relevantBones;
    }

    private void addAncestors(String boneName, Set<String> availableBones, Map<String, String> parentByBone, LinkedHashSet<String> relevantBones) {
        String current = boneName;
        while (current != null && availableBones.contains(current) && relevantBones.add(current)) {
            current = parentByBone.get(current);
        }
    }

    private void addDescendants(String boneName, Set<String> availableBones, Map<String, List<String>> childrenByBone, LinkedHashSet<String> relevantBones) {
        ArrayDeque<String> queue = new ArrayDeque<String>();
        queue.addLast(boneName);
        while (!queue.isEmpty()) {
            String current = (String)queue.removeFirst();
            if (!availableBones.contains(current)) continue;
            relevantBones.add(current);
            for (String child : childrenByBone.getOrDefault(current, List.of())) {
                queue.addLast(child);
            }
        }
    }

    private JsonObject pruneHitRoot(JsonObject root, List<String> attackBones, List<String> attackAnimations) {
        if (root.get("animation") instanceof JsonArray) {
            JsonObject prunedRoot = new JsonObject();
            prunedRoot.add("bone", (JsonElement)this.toJsonArray(attackBones));
            prunedRoot.add("animation", (JsonElement)this.toJsonArray(attackAnimations));
            this.copyPrimitive(root, prunedRoot, "width_scale");
            this.copyPrimitive(root, prunedRoot, "height_scale");
            return prunedRoot;
        }
        return root.deepCopy();
    }

    private static LinkedHashSet<String> collectAttackBones(HitProjectConfig hitConfig) {
        LinkedHashSet<String> bones = new LinkedHashSet<String>();
        for (AnimationHitConfig anim : hitConfig.animations().values()) {
            for (HitSegment segment : anim.segments()) {
                bones.addAll(segment.bones());
            }
        }
        return bones;
    }

    private JsonObject pruneAnimationsRoot(JsonObject root, List<String> attackAnimations, @Nullable Set<String> relevantBones) {
        JsonObject prunedRoot = root.deepCopy();
        JsonElement animationsElement = root.get("animations");
        if (!(animationsElement instanceof JsonObject)) {
            return prunedRoot;
        }
        JsonObject animationsObject = (JsonObject)animationsElement;
        JsonObject filteredAnimations = new JsonObject();
        LinkedHashSet<String> attackAnimationSet = new LinkedHashSet<String>(attackAnimations);
        for (Map.Entry entry : animationsObject.entrySet()) {
            if (!attackAnimationSet.contains(entry.getKey())) {
                filteredAnimations.add((String)entry.getKey(), ((JsonElement)entry.getValue()).deepCopy());
                continue;
            }
            filteredAnimations.add((String)entry.getKey(), this.pruneAnimationEntry((JsonElement)entry.getValue(), relevantBones));
        }
        prunedRoot.add("animations", (JsonElement)filteredAnimations);
        return prunedRoot;
    }

    private JsonElement pruneAnimationEntry(JsonElement animationElement, Set<String> relevantBones) {
        if (relevantBones == null) {
            return animationElement.deepCopy();
        }
        if (!animationElement.isJsonObject()) {
            return animationElement.deepCopy();
        }
        JsonObject animationObject = animationElement.getAsJsonObject();
        JsonObject prunedAnimation = animationObject.deepCopy();
        JsonElement bonesElement = animationObject.get("bones");
        if (!(bonesElement instanceof JsonObject)) {
            return prunedAnimation;
        }
        JsonObject bonesObject = (JsonObject)bonesElement;
        JsonObject filteredBones = new JsonObject();
        for (Map.Entry entry : bonesObject.entrySet()) {
            if (!relevantBones.contains(entry.getKey())) continue;
            filteredBones.add((String)entry.getKey(), ((JsonElement)entry.getValue()).deepCopy());
        }
        prunedAnimation.add("bones", (JsonElement)filteredBones);
        return prunedAnimation;
    }

    private JsonArray toJsonArray(List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }

    private void copyPrimitive(JsonObject source, JsonObject target, String key) {
        JsonElement element = source.get(key);
        if (element != null && element.isJsonPrimitive()) {
            target.add(key, element.deepCopy());
        }
    }

    private byte[] toJsonBytes(JsonObject jsonObject) {
        return jsonObject.toString().getBytes(StandardCharsets.UTF_8);
    }

    private JsonObject readJsonObject(Path path) throws IOException {
        JsonObject jsonObject;
        block8: {
            BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            try {
                jsonObject = JsonParser.parseReader((Reader)reader).getAsJsonObject();
                if (reader == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    throw new IOException("\u65e0\u6548\u7684 JSON \u6587\u4ef6\uff1a" + path.getFileName(), e);
                }
            }
            ((Reader)reader).close();
        }
        return jsonObject;
    }

    private JsonObject readJsonObjectOptional(Path path) {
        if (!Files.exists(path, new LinkOption[0])) {
            return new JsonObject();
        }
        try {
            return this.readJsonObject(path);
        }
        catch (Exception e) {
            LOGGER.warn("Failed to read JSON file {}, using empty object", (Object)path, (Object)e);
            return new JsonObject();
        }
    }

    private List<String> extractModelBones(@Nullable JsonElement geometryElement) {
        LinkedHashSet<String> bones = new LinkedHashSet<String>();
        if (geometryElement == null) {
            return List.of();
        }
        if (geometryElement.isJsonArray()) {
            for (JsonElement entry : geometryElement.getAsJsonArray()) {
                this.collectBones(entry, bones);
            }
        } else {
            this.collectBones(geometryElement, bones);
        }
        return List.copyOf(bones);
    }

    private void collectBones(JsonElement geometryElement, LinkedHashSet<String> bones) {
        if (!geometryElement.isJsonObject()) {
            return;
        }
        JsonObject geometry = geometryElement.getAsJsonObject();
        JsonElement bonesElement = geometry.get("bones");
        if (!(bonesElement instanceof JsonArray)) {
            return;
        }
        JsonArray boneArray = (JsonArray)bonesElement;
        for (JsonElement boneElement : boneArray) {
            JsonObject boneObject;
            String boneName;
            if (!boneElement.isJsonObject() || (boneName = this.getString(boneObject = boneElement.getAsJsonObject(), "name")) == null || boneName.isBlank()) continue;
            bones.add(boneName);
        }
    }

    private List<String> extractAnimationNames(JsonObject root) {
        JsonElement animationsElement = root.get("animations");
        if (!(animationsElement instanceof JsonObject)) {
            return List.of();
        }
        JsonObject animationsObject = (JsonObject)animationsElement;
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        for (Map.Entry entry : animationsObject.entrySet()) {
            if (((String)entry.getKey()).isBlank()) continue;
            names.add((String)entry.getKey());
        }
        return List.copyOf(names);
    }

    private List<String> missingEntries(List<String> selected, List<String> available) {
        LinkedHashSet<String> availableSet = new LinkedHashSet<String>(available);
        ArrayList<String> missing = new ArrayList<String>();
        for (String value : selected) {
            if (availableSet.contains(value)) continue;
            missing.add(value);
        }
        return missing;
    }

    @Nullable
    private String buildErrorMessage(List<String> missingBones, List<String> missingAnimations) {
        ArrayList<CallSite> parts = new ArrayList<CallSite>();
        if (!missingBones.isEmpty()) {
            parts.add((CallSite)((Object)("hit.json \u4e2d\u8fd9\u4e9b\u9aa8\u9abc\u5728 main.json \u91cc\u4e0d\u5b58\u5728\uff1a" + String.join((CharSequence)", ", missingBones))));
        }
        if (!missingAnimations.isEmpty()) {
            parts.add((CallSite)((Object)("hit.json \u4e2d\u8fd9\u4e9b\u52a8\u753b\u5728 animations.json \u91cc\u4e0d\u5b58\u5728\uff1a" + String.join((CharSequence)", ", missingAnimations))));
        }
        if (parts.isEmpty()) {
            return null;
        }
        return String.join((CharSequence)"\uff1b", parts);
    }

    @Nullable
    private String getString(JsonObject object, String key) {
        JsonElement element = object.get(key);
        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            return null;
        }
        return element.getAsString();
    }

    private record ParsedProject(Path directory, String name, String modelId, JsonObject mainRoot, JsonObject hitRoot, JsonObject animationsRoot, boolean hasExternalModel, boolean hasExternalAnimations, List<String> availableBones, List<String> attackBones, List<String> availableAnimations, List<String> attackAnimations, @Nullable String errorMessage) {
    }

    public record ExportResult(Path path, int boneCount, int animationCount) {
    }

    private record MainPruneResult(JsonObject root, Set<String> relevantBones) {
    }

    private record GeometryPruneResult(JsonObject geometry, Set<String> relevantBones) {
    }
}

