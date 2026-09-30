/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.quickbuf.ProtoSource
 *  com.elfmcys.ysm.format.parser.pojo.model.Bone
 *  com.elfmcys.ysm.format.parser.pojo.model.Cube
 *  com.elfmcys.ysm.format.parser.pojo.model.CubeUv
 *  com.elfmcys.ysm.format.parser.pojo.model.GeoModel
 *  com.elfmcys.ysm.format.parser.pojo.model.Geometry
 *  com.elfmcys.ysm.geckolib3.core.molang.util.StringPool
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.Bone
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.CubeLegacy
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.Cubes
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.GeoModel
 *  com.elfmcys.ysm.proto.mixel.asset.model.data.GeoProperties
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package io.github.tt432.yessteveskill.yss.attack;

import com.elfmcys.quickbuf.ProtoSource;
import com.elfmcys.ysm.format.parser.pojo.model.Bone;
import com.elfmcys.ysm.format.parser.pojo.model.Cube;
import com.elfmcys.ysm.format.parser.pojo.model.CubeUv;
import com.elfmcys.ysm.format.parser.pojo.model.GeoModel;
import com.elfmcys.ysm.format.parser.pojo.model.Geometry;
import com.elfmcys.ysm.geckolib3.core.molang.util.StringPool;
import com.elfmcys.ysm.proto.mixel.asset.model.data.CubeLegacy;
import com.elfmcys.ysm.proto.mixel.asset.model.data.Cubes;
import com.elfmcys.ysm.proto.mixel.asset.model.data.GeoProperties;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.tt432.yessteveskill.combat.config.AnimationHitConfig;
import io.github.tt432.yessteveskill.combat.config.HitProjectConfig;
import io.github.tt432.yessteveskill.combat.config.HitSegment;
import io.github.tt432.yessteveskill.ysm.YSMModelAssets;
import io.github.tt432.yessteveskill.yss.YssPathHelper;
import io.github.tt432.yessteveskill.yss.YssRuntimeProjectResolver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YssAttackProjectLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"YesSteveSkill/YssAttackLoader");
    private static final Gson GSON = new Gson();
    private final Map<Path, CacheEntry> cache = new HashMap<Path, CacheEntry>();

    @Nullable
    public LoadedProject load(String modelId, @Nullable YSMModelAssets.ModelAssets modelAssets) {
        try {
            YssRuntimeProjectResolver.ResolvedProject resolvedProject = YssRuntimeProjectResolver.resolve(modelId);
            if (resolvedProject == null) {
                this.cache.remove(YssRuntimeProjectResolver.resolvePreferredSourcePath(modelId));
                return null;
            }
            Path sourcePath = resolvedProject.sourcePath();
            String geometrySource = resolvedProject.mainBytes() != null ? "yss" : (modelAssets == null || modelAssets.modelHash() == null ? "ysm:unavailable" : "ysm:" + modelAssets.modelHash());
            CacheEntry cached = this.cache.get(sourcePath);
            if (cached != null && cached.stamp().equals((Object)resolvedProject.stamp()) && cached.geometrySource().equals(geometrySource)) {
                return cached.project();
            }
            ModelGeometry geometry = this.loadModel(resolvedProject, modelId, modelAssets);
            HitProjectConfig hitConfig = this.loadHitConfig(resolvedProject.hitBytes());
            Set<String> attackBones = YssAttackProjectLoader.deriveAttackBones(hitConfig);
            LoadedProject project = new LoadedProject(sourcePath, resolvedProject.modelId(), geometry, hitConfig, attackBones, hitConfig.animations().keySet(), hitConfig.widthScale(), hitConfig.heightScale());
            this.cache.put(sourcePath, new CacheEntry(resolvedProject.stamp(), geometrySource, project));
            return project;
        }
        catch (Exception e) {
            Path sourcePath = YssRuntimeProjectResolver.resolvePreferredSourcePath(modelId);
            LOGGER.warn("Failed to load attack project for {} under {}", new Object[]{modelId, sourcePath, e});
            this.cache.remove(sourcePath);
            return null;
        }
    }

    public boolean hasHitConfig(String modelId) {
        return YssRuntimeProjectResolver.hasHitConfig(modelId);
    }

    private ModelGeometry loadModel(YssRuntimeProjectResolver.ResolvedProject project, String modelId, @Nullable YSMModelAssets.ModelAssets modelAssets) throws IOException {
        byte[] yssMain = project.mainBytes();
        if (yssMain != null) {
            return ModelGeometry.fromJson(yssMain, modelId);
        }
        if (modelAssets == null) {
            throw new IOException("YSM model assets are not loaded");
        }
        byte[] ysmMain = modelAssets.mainJson();
        if (ysmMain != null) {
            return ModelGeometry.fromJson(ysmMain, modelId);
        }
        if (modelAssets.mainModel() != null) {
            return ModelGeometry.from(modelAssets.mainModel());
        }
        throw new IOException("Current YSM model exposes no readable geometry");
    }

    private HitProjectConfig loadHitConfig(byte[] bytes) throws IOException {
        HitProjectConfig hitProjectConfig;
        InputStreamReader reader = new InputStreamReader((InputStream)new ByteArrayInputStream(bytes), StandardCharsets.UTF_8);
        try {
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            hitProjectConfig = HitProjectConfig.parse(root);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((Reader)reader).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (RuntimeException e) {
                throw new IOException("Invalid hit config", e);
            }
        }
        ((Reader)reader).close();
        return hitProjectConfig;
    }

    private static Set<String> deriveAttackBones(HitProjectConfig hitConfig) {
        LinkedHashSet<String> bones = new LinkedHashSet<String>();
        for (AnimationHitConfig anim : hitConfig.animations().values()) {
            for (HitSegment segment : anim.segments()) {
                bones.addAll(segment.bones());
            }
        }
        return Set.copyOf(bones);
    }

    private record CacheEntry(YssRuntimeProjectResolver.Stamp stamp, String geometrySource, LoadedProject project) {
    }

    public record LoadedProject(Path directory, String modelId, ModelGeometry geometry, HitProjectConfig hitConfig, Set<String> attackBones, Set<String> attackAnimations, float widthScale, float heightScale) {
    }

    public static final class ModelGeometry {
        private static final float DEFAULT_TEXTURE_SIZE = 64.0f;
        private final List<BoneGeometry> bones;
        private final Map<String, BoneGeometry> bonesByName;
        private final float textureWidth;
        private final float textureHeight;

        private ModelGeometry(List<BoneGeometry> bones, Map<String, BoneGeometry> bonesByName, float textureWidth, float textureHeight) {
            this.bones = List.copyOf(bones);
            this.bonesByName = Map.copyOf(bonesByName);
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        public float textureWidth() {
            return this.textureWidth;
        }

        public float textureHeight() {
            return this.textureHeight;
        }

        @Nullable
        public BoneGeometry bone(String name) {
            return this.bonesByName.get(name);
        }

        public List<BoneGeometry> bones() {
            return this.bones;
        }

        public static ModelGeometry fromJson(byte[] bytes, String modelId) throws IOException {
            GeoModel root;
            try (InputStreamReader reader = new InputStreamReader((InputStream)new ByteArrayInputStream(bytes), StandardCharsets.UTF_8);){
                root = (GeoModel)GSON.fromJson((Reader)reader, GeoModel.class);
            }
            if (root == null || root.minecraftGeometry == null || root.minecraftGeometry.isEmpty()) {
                throw new IOException("No geometry found");
            }
            String normalizedModelId = YssPathHelper.normalizeModelId(modelId).toLowerCase(Locale.ROOT);
            Geometry selected = null;
            for (Geometry candidate : root.minecraftGeometry) {
                String identifier = candidate.description == null ? "" : candidate.description.identifier;
                if (identifier == null || !identifier.toLowerCase(Locale.ROOT).contains(normalizedModelId)) continue;
                selected = candidate;
                break;
            }
            return ModelGeometry.from(selected == null ? (Geometry)root.minecraftGeometry.get(0) : selected);
        }

        public static ModelGeometry from(Geometry source) {
            ArrayList<BoneGeometry> bones = new ArrayList<BoneGeometry>(source.bones.size());
            LinkedHashMap<String, BoneGeometry> byName = new LinkedHashMap<String, BoneGeometry>();
            for (Bone sourceBone : source.bones) {
                if (sourceBone.name == null || sourceBone.name.isBlank()) continue;
                ArrayList<CubeGeometry> cubes = new ArrayList<CubeGeometry>();
                if (sourceBone.cubes != null) {
                    for (Cube cube : sourceBone.cubes) {
                        cubes.add(CubeGeometry.from(cube, sourceBone.inflate));
                    }
                }
                float[] pivot = CubeGeometry.vectorOrDefault(sourceBone.pivot, 0.0f, 0.0f, 0.0f);
                float[] rotation = CubeGeometry.vectorOrDefault(sourceBone.rotation, 0.0f, 0.0f, 0.0f);
                BoneGeometry bone = new BoneGeometry(sourceBone.name, sourceBone.parent, StringPool.computeIfAbsent((String)sourceBone.name), -pivot[0], pivot[1], pivot[2], (float)(-Math.toRadians(rotation[0])), (float)(-Math.toRadians(rotation[1])), (float)Math.toRadians(rotation[2]), sourceBone.mirror, List.copyOf(cubes));
                bones.add(bone);
                byName.put(sourceBone.name, bone);
            }
            float texW = source.description != null && source.description.textureWidth != 0.0f ? source.description.textureWidth : 64.0f;
            float texH = source.description != null && source.description.textureHeight != 0.0f ? source.description.textureHeight : 64.0f;
            return new ModelGeometry(bones, byName, texW, texH);
        }

        public static ModelGeometry from(com.elfmcys.ysm.proto.mixel.asset.model.data.GeoModel source) throws IOException {
            Cubes cubeData = Cubes.parseFrom((ProtoSource)ProtoSource.newInstance((ByteBuffer)source.cubes()));
            List legacyCubes = ModelGeometry.collect(cubeData.cubesLegacy());
            List<com.elfmcys.ysm.proto.mixel.asset.model.data.Bone> sourceBones = ModelGeometry.collect(source.bones());
            ArrayList<BoneGeometry> bones = new ArrayList<BoneGeometry>(sourceBones.size());
            LinkedHashMap<String, BoneGeometry> byName = new LinkedHashMap<String, BoneGeometry>();
            int cubeIndex = 0;
            for (com.elfmcys.ysm.proto.mixel.asset.model.data.Bone sourceBone : sourceBones) {
                if (sourceBone.nameNullOrBlank()) {
                    cubeIndex += sourceBone.cubeCount().orElse(0);
                    continue;
                }
                ArrayList<CubeGeometry> cubes = new ArrayList<CubeGeometry>();
                int cubeCount = sourceBone.cubeCount().orElse(0);
                for (int offset = 0; offset < cubeCount && cubeIndex < legacyCubes.size(); ++offset, ++cubeIndex) {
                    CubeGeometry cube = CubeGeometry.from((CubeLegacy)legacyCubes.get(cubeIndex));
                    if (cube.vertices().length < 24) continue;
                    cubes.add(cube);
                }
                float[] pivot = sourceBone.pivot().toFloatArray();
                float pivotX = pivot.length > 0 ? pivot[0] : 0.0f;
                float pivotY = pivot.length > 1 ? pivot[1] : 0.0f;
                float pivotZ = pivot.length > 2 ? pivot[2] : 0.0f;
                float[] rotate = sourceBone.rotate().toFloatArray();
                float rotationX = rotate.length > 0 ? rotate[0] : 0.0f;
                float rotationY = rotate.length > 1 ? rotate[1] : 0.0f;
                float rotationZ = rotate.length > 2 ? rotate[2] : 0.0f;
                String parent = sourceBone.parent().filter(value -> !value.isBlank()).orElse(null);
                BoneGeometry bone = new BoneGeometry(sourceBone.name(), parent, StringPool.computeIfAbsent((String)sourceBone.name()), pivotX, pivotY, pivotZ, rotationX, rotationY, rotationZ, false, List.copyOf(cubes));
                bones.add(bone);
                byName.put(sourceBone.name(), bone);
            }
            GeoProperties properties = source.properties();
            float texW = properties.textureWidth() > 0.0f ? properties.textureWidth() : 64.0f;
            float texH = properties.textureHeight() > 0.0f ? properties.textureHeight() : 64.0f;
            return new ModelGeometry(bones, byName, texW, texH);
        }

        private static <T> List<T> collect(Iterable<T> source) {
            ArrayList result = new ArrayList();
            source.forEach(result::add);
            return result;
        }

        public List<BoneGeometry> selectBones(Set<String> roots) {
            if (roots.isEmpty()) {
                return List.of();
            }
            ArrayList<BoneGeometry> selected = new ArrayList<BoneGeometry>();
            Iterator<BoneGeometry> iterator = this.bones.iterator();
            block0: while (iterator.hasNext()) {
                BoneGeometry bone;
                BoneGeometry current = bone = iterator.next();
                for (int depth = 0; current != null && depth <= this.bones.size(); ++depth) {
                    if (roots.contains(current.name())) {
                        selected.add(bone);
                        continue block0;
                    }
                    current = current.parent() == null ? null : this.bonesByName.get(current.parent());
                }
            }
            return List.copyOf(selected);
        }
    }

    public record MeshGeometry(float[] pos, int[] posIndices, float[] uv, int[] uvIndices, float[] normals, int faceCount) {
    }

    public record CubeGeometry(float[] vertices, @Nullable CubeUv uv, boolean mirror, float[] rawSize, @Nullable MeshGeometry mesh) {
        private static CubeGeometry from(Cube cube, float boneInflate) {
            float[] size = CubeGeometry.vectorOrDefault(cube.size, 1.0f, 1.0f, 1.0f);
            float[] origin = CubeGeometry.vectorOrDefault(cube.origin, 0.0f, 0.0f, 0.0f);
            float inflate = (cube.inflate == null ? boneInflate : cube.inflate.floatValue()) / 16.0f;
            float sx = size[0] / 16.0f;
            float sy = size[1] / 16.0f;
            float sz = size[2] / 16.0f;
            float ox = -(origin[0] + size[0]) / 16.0f;
            float oy = origin[1] / 16.0f;
            float oz = origin[2] / 16.0f;
            float minX = ox - inflate;
            float minY = oy - inflate;
            float minZ = oz - inflate;
            float maxX = ox + sx + inflate;
            float maxY = oy + sy + inflate;
            float maxZ = oz + sz + inflate;
            float[] vertices = new float[]{minX, minY, minZ, minX, minY, maxZ, minX, maxY, minZ, minX, maxY, maxZ, maxX, minY, minZ, maxX, minY, maxZ, maxX, maxY, minZ, maxX, maxY, maxZ};
            float[] rotation = CubeGeometry.vectorOrDefault(cube.rotation, 0.0f, 0.0f, 0.0f);
            float[] pivot = CubeGeometry.vectorOrDefault(cube.pivot, 0.0f, 0.0f, 0.0f);
            float pivotX = -pivot[0] / 16.0f;
            float pivotY = pivot[1] / 16.0f;
            float pivotZ = pivot[2] / 16.0f;
            float rotationX = (float)(-Math.toRadians(rotation[0]));
            float rotationY = (float)(-Math.toRadians(rotation[1]));
            float rotationZ = (float)Math.toRadians(rotation[2]);
            for (int offset = 0; offset < vertices.length; offset += 3) {
                CubeGeometry.rotateAround(vertices, offset, pivotX, pivotY, pivotZ, rotationX, rotationY, rotationZ);
            }
            return new CubeGeometry(vertices, cube.uv, cube.mirror, size, null);
        }

        private static CubeGeometry from(CubeLegacy cube) {
            float[] pos = cube.pos().toFloatArray();
            if (pos.length < 3) {
                return new CubeGeometry(new float[0], null, false, null, null);
            }
            float minX = Float.POSITIVE_INFINITY;
            float minY = Float.POSITIVE_INFINITY;
            float minZ = Float.POSITIVE_INFINITY;
            float maxX = Float.NEGATIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;
            float maxZ = Float.NEGATIVE_INFINITY;
            int offset = 0;
            while (offset + 2 < pos.length) {
                float x = pos[offset];
                float y = pos[offset + 1];
                float z = pos[offset + 2];
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                minZ = Math.min(minZ, z);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                maxZ = Math.max(maxZ, z);
                offset += 3;
            }
            return new CubeGeometry(CubeGeometry.boxVertices(minX, minY, minZ, maxX, maxY, maxZ), null, false, null, CubeGeometry.meshOrNull(cube, pos));
        }

        @Nullable
        private static MeshGeometry meshOrNull(CubeLegacy cube, float[] pos) {
            int faceCount = cube.faceCount();
            int[] posIndices = cube.posIndices().toIntArray();
            int[] uvIndices = cube.uvIndices().toIntArray();
            float[] uv = cube.uv().toFloatArray();
            float[] normals = cube.normal().toFloatArray();
            if (faceCount <= 0 || posIndices.length < faceCount * 4 || uvIndices.length < faceCount * 4 || uv.length < 2 || normals.length < faceCount * 3) {
                return null;
            }
            return new MeshGeometry(pos, posIndices, uv, uvIndices, normals, faceCount);
        }

        private static float[] boxVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return new float[]{minX, minY, minZ, minX, minY, maxZ, minX, maxY, minZ, minX, maxY, maxZ, maxX, minY, minZ, maxX, minY, maxZ, maxX, maxY, minZ, maxX, maxY, maxZ};
        }

        private static void rotateAround(float[] vertices, int offset, float pivotX, float pivotY, float pivotZ, float rotationX, float rotationY, float rotationZ) {
            float x = vertices[offset] - pivotX;
            float y = vertices[offset + 1] - pivotY;
            float z = vertices[offset + 2] - pivotZ;
            float x1 = x;
            float y1 = (float)((double)y * Math.cos(rotationX) - (double)z * Math.sin(rotationX));
            float z1 = (float)((double)y * Math.sin(rotationX) + (double)z * Math.cos(rotationX));
            float x2 = (float)((double)x1 * Math.cos(rotationY) + (double)z1 * Math.sin(rotationY));
            float y2 = y1;
            float z2 = (float)((double)(-x1) * Math.sin(rotationY) + (double)z1 * Math.cos(rotationY));
            vertices[offset] = (float)((double)x2 * Math.cos(rotationZ) - (double)y2 * Math.sin(rotationZ)) + pivotX;
            vertices[offset + 1] = (float)((double)x2 * Math.sin(rotationZ) + (double)y2 * Math.cos(rotationZ)) + pivotY;
            vertices[offset + 2] = z2 + pivotZ;
        }

        private static float[] vectorOrDefault(float[] value, float x, float y, float z) {
            float[] fArray;
            if (value == null || value.length < 3) {
                float[] fArray2 = new float[3];
                fArray2[0] = x;
                fArray2[1] = y;
                fArray = fArray2;
                fArray2[2] = z;
            } else {
                fArray = value;
            }
            return fArray;
        }
    }

    public record BoneGeometry(String name, @Nullable String parent, int pooledName, float pivotX, float pivotY, float pivotZ, float initialRotationX, float initialRotationY, float initialRotationZ, boolean mirror, List<CubeGeometry> cubes) {
    }
}

