/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package io.github.tt432.yessteveskill.ysm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public final class YSMModelAssetsCompatibilityChecker {
    private static final String MANIFEST_RESOURCE = "META-INF/ysm-compat/io/github/tt432/yessteveskill/ysm/YSMModelAssets.json";
    private static final int MANIFEST_VERSION = 1;
    private static final int MAX_MANIFEST_BYTES = 0x4000000;
    private static final int MAX_JSON_ITEMS = 1000000;

    private YSMModelAssetsCompatibilityChecker() {
    }

    public static Result check() {
        return RuntimeCheck.check("$class");
    }

    private static final class RuntimeCheck {
        private RuntimeCheck() {
        }

        static Result check(String groupKey) {
            GroupSpec spec;
            try {
                spec = RuntimeCheck.readGroup(groupKey);
            }
            catch (Throwable throwable) {
                return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.MANIFEST_ERROR, YSMModelAssetsCompatibilityChecker.MANIFEST_RESOURCE, RuntimeCheck.describe(throwable));
            }
            Result sideResult = RuntimeCheck.checkSide(spec.side());
            if (sideResult != null) {
                return sideResult;
            }
            Result ysmResult = RuntimeCheck.checkYsmEntrypoint();
            if (ysmResult != null) {
                return ysmResult;
            }
            Result canaryResult = RuntimeCheck.checkReobfCanary(spec.canaryProbe());
            if (canaryResult != null) {
                return canaryResult;
            }
            ArrayList<Issue> issues = new ArrayList<Issue>(spec.issues());
            boolean incompatible = false;
            for (Requirement requirement : spec.requirements()) {
                try {
                    Issue issue = RuntimeCheck.verify(requirement);
                    if (issue == null) continue;
                    incompatible = true;
                    issues.add(issue);
                }
                catch (ProbeFailure failure) {
                    issues.add(new Issue(IssueKind.REOBF_PROBE_ERROR, requirement.display(), failure.getMessage()));
                    return new Result(Status.CHECK_FAILED, issues);
                }
                catch (Throwable throwable) {
                    issues.add(new Issue(IssueKind.REOBF_PROBE_ERROR, requirement.display(), RuntimeCheck.describe(throwable)));
                    return new Result(Status.CHECK_FAILED, issues);
                }
            }
            if (incompatible) {
                return new Result(Status.INCOMPATIBLE, issues);
            }
            return new Result(issues.isEmpty() ? Status.COMPATIBLE : Status.COMPATIBLE_WITH_WARNINGS, issues);
        }

        private static Result checkSide(String side) {
            if (!side.equals("CLIENT")) {
                return null;
            }
            try {
                Class<?> environment = Class.forName("net.minecraftforge.fml.loading.FMLEnvironment", false, RuntimeCheck.loader());
                Object dist = environment.getField("dist").get(null);
                if (!String.valueOf(dist).equals("CLIENT")) {
                    return new Result(Status.NOT_APPLICABLE, List.of());
                }
                return null;
            }
            catch (ClassNotFoundException exception) {
                return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.YSM_ENTRYPOINT_ERROR, "net.minecraftforge.fml.loading.FMLEnvironment.dist", "Cannot determine the physical side outside a Forge runtime.");
            }
            catch (Throwable throwable) {
                return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.YSM_ENTRYPOINT_ERROR, "net.minecraftforge.fml.loading.FMLEnvironment.dist", RuntimeCheck.describe(throwable));
            }
        }

        private static Result checkYsmEntrypoint() {
            Method method;
            Class<?> ysm;
            Boolean modLoaded = null;
            try {
                Class<?> modListClass = Class.forName("net.minecraftforge.fml.ModList", false, RuntimeCheck.loader());
                Object modList = modListClass.getMethod("get", new Class[0]).invoke(null, new Object[0]);
                modLoaded = (Boolean)modListClass.getMethod("isLoaded", String.class).invoke(modList, "ysm");
                if (!modLoaded.booleanValue()) {
                    return new Result(Status.YSM_NOT_LOADED, List.of());
                }
            }
            catch (ClassNotFoundException modListClass) {
            }
            catch (Throwable throwable) {
                return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.YSM_ENTRYPOINT_ERROR, "net.minecraftforge.fml.ModList#isLoaded", RuntimeCheck.describe(throwable));
            }
            try {
                ysm = Class.forName("com.elfmcys.ysm.YesSteveModel", false, RuntimeCheck.loader());
            }
            catch (ClassNotFoundException | NoClassDefFoundError exception) {
                return new Result(modLoaded == null ? Status.YSM_NOT_LOADED : Status.INCOMPATIBLE, List.of((Object)((Object)new Issue(IssueKind.MISSING_CLASS, "com.elfmcys.ysm.YesSteveModel", RuntimeCheck.describe(exception)))));
            }
            catch (Throwable throwable) {
                return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.YSM_ENTRYPOINT_ERROR, "com.elfmcys.ysm.YesSteveModel", RuntimeCheck.describe(throwable));
            }
            try {
                method = ysm.getMethod("isAvailable", new Class[0]);
            }
            catch (NoSuchMethodException exception) {
                return RuntimeCheck.result(Status.INCOMPATIBLE, IssueKind.MISSING_METHOD, "com.elfmcys.ysm.YesSteveModel#isAvailable()Z", RuntimeCheck.describe(exception));
            }
            if (!Modifier.isStatic(method.getModifiers()) || method.getReturnType() != Boolean.TYPE) {
                return RuntimeCheck.result(Status.INCOMPATIBLE, IssueKind.TYPE_MISMATCH, "com.elfmcys.ysm.YesSteveModel#isAvailable()Z", "Expected a public static boolean method.");
            }
            try {
                Object available = method.invoke(null, new Object[0]);
                if (!Boolean.TRUE.equals(available)) {
                    return new Result(Status.YSM_UNAVAILABLE, List.of());
                }
                return null;
            }
            catch (Throwable throwable) {
                return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.YSM_ENTRYPOINT_ERROR, "com.elfmcys.ysm.YesSteveModel#isAvailable()Z", RuntimeCheck.describe(throwable));
            }
        }

        private static Result checkReobfCanary(String canaryProbe) {
            boolean production;
            try {
                production = RuntimeCheck.isForgeProduction();
            }
            catch (ProbeFailure failure) {
                return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.REOBF_PROBE_ERROR, "Forge production environment", failure.getMessage());
            }
            if (!production) {
                return null;
            }
            if (canaryProbe.isEmpty()) {
                return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.REOBF_PROBE_ERROR, "net.minecraft.world.entity.Entity#tick()V", "Production artifact has no reobf canary.");
            }
            try {
                String mappedName = RuntimeCheck.declaredProbeMethodName(canaryProbe);
                if (mappedName.equals("tick")) {
                    return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.REOBF_PROBE_ERROR, canaryProbe, "The compatibility probes did not pass through the standard reobf task.");
                }
                return null;
            }
            catch (ProbeFailure failure) {
                return RuntimeCheck.result(Status.CHECK_FAILED, IssueKind.REOBF_PROBE_ERROR, canaryProbe, failure.getMessage());
            }
        }

        private static boolean isForgeProduction() throws ProbeFailure {
            try {
                Class<?> loaderClass = Class.forName("net.minecraftforge.fml.loading.FMLLoader", false, RuntimeCheck.loader());
                return Boolean.TRUE.equals(loaderClass.getMethod("isProduction", new Class[0]).invoke(null, new Object[0]));
            }
            catch (ClassNotFoundException ignored) {
                return false;
            }
            catch (Throwable throwable) {
                throw new ProbeFailure(RuntimeCheck.describe(throwable));
            }
        }

        private static Issue verify(Requirement requirement) throws ProbeFailure {
            try {
                Class<?> owner = Class.forName(requirement.owner(), false, RuntimeCheck.loader());
                return switch (requirement.kind()) {
                    default -> throw new IncompatibleClassChangeError();
                    case RequirementKind.CLASS -> null;
                    case RequirementKind.CLASS_OWNER -> RuntimeCheck.verifyClassOwner(owner, requirement);
                    case RequirementKind.EXTENDABLE_CLASS -> RuntimeCheck.verifyExtendableClass(owner, requirement);
                    case RequirementKind.INTERFACE -> RuntimeCheck.verifyInterface(owner, requirement);
                    case RequirementKind.INSTANTIABLE_CLASS -> RuntimeCheck.verifyInstantiableClass(owner, requirement);
                    case RequirementKind.METHOD -> RuntimeCheck.verifyMethod(owner, requirement);
                    case RequirementKind.FIELD -> RuntimeCheck.verifyField(owner, requirement);
                    case RequirementKind.CONSTRUCTOR -> RuntimeCheck.verifyConstructor(owner, requirement);
                };
            }
            catch (ClassNotFoundException | NoClassDefFoundError exception) {
                return new Issue(IssueKind.MISSING_CLASS, requirement.display(), RuntimeCheck.describe(exception));
            }
            catch (LinkageError error) {
                return new Issue(IssueKind.MISSING_CLASS, requirement.display(), RuntimeCheck.describe(error));
            }
        }

        private static Issue verifyClassOwner(Class<?> owner, Requirement requirement) {
            return !owner.isInterface() ? null : new Issue(IssueKind.TYPE_MISMATCH, requirement.display(), "Expected a class rather than an interface.");
        }

        private static Issue verifyExtendableClass(Class<?> owner, Requirement requirement) {
            if (owner.isInterface() || Modifier.isFinal(owner.getModifiers())) {
                return new Issue(IssueKind.TYPE_MISMATCH, requirement.display(), "Expected a non-final class that can remain in the extension hierarchy.");
            }
            return null;
        }

        private static Issue verifyInterface(Class<?> owner, Requirement requirement) {
            return owner.isInterface() ? null : new Issue(IssueKind.TYPE_MISMATCH, requirement.display(), "Expected an interface.");
        }

        private static Issue verifyInstantiableClass(Class<?> owner, Requirement requirement) {
            int modifiers = owner.getModifiers();
            if (owner.isInterface() || Modifier.isAbstract(modifiers)) {
                return new Issue(IssueKind.TYPE_MISMATCH, requirement.display(), "Expected a concrete class for a NEW instruction.");
            }
            return null;
        }

        private static Issue verifyMethod(Class<?> owner, Requirement requirement) throws ProbeFailure, ClassNotFoundException {
            String runtimeName = requirement.nameProbe().isEmpty() ? requirement.name() : RuntimeCheck.declaredProbeMethodName(requirement.nameProbe());
            Class<?> returnType = RuntimeCheck.resolveType(requirement.returnType());
            Class[] parameters = new Class[requirement.parameterTypes().size()];
            for (int index = 0; index < parameters.length; ++index) {
                parameters[index] = RuntimeCheck.resolveType(requirement.parameterTypes().get(index));
            }
            try {
                if (!RuntimeCheck.methodExists(owner, runtimeName, returnType, parameters, requirement.staticMember(), !requirement.declaredOnly(), Collections.newSetFromMap(new IdentityHashMap()))) {
                    return new Issue(IssueKind.MISSING_METHOD, requirement.display(), "No method with the runtime-mapped name and descriptor exists.");
                }
                return null;
            }
            catch (NoClassDefFoundError error) {
                return new Issue(IssueKind.MISSING_CLASS, requirement.display(), RuntimeCheck.describe(error));
            }
            catch (Throwable throwable) {
                throw new ProbeFailure(RuntimeCheck.describe(throwable));
            }
        }

        private static Issue verifyField(Class<?> owner, Requirement requirement) throws ProbeFailure, ClassNotFoundException {
            Class<?> expectedType = RuntimeCheck.resolveType(requirement.fieldType());
            try {
                Field field = RuntimeCheck.findField(owner, requirement.name(), Collections.newSetFromMap(new IdentityHashMap()));
                if (field == null) {
                    return new Issue(IssueKind.MISSING_FIELD, requirement.display(), "The field does not exist in the runtime hierarchy.");
                }
                if (Modifier.isStatic(field.getModifiers()) != requirement.staticMember()) {
                    return new Issue(IssueKind.TYPE_MISMATCH, requirement.display(), "The field static/instance access mode changed.");
                }
                if (field.getType() != expectedType) {
                    return new Issue(IssueKind.TYPE_MISMATCH, requirement.display(), "Expected " + expectedType.getTypeName() + " but found " + field.getType().getTypeName() + ".");
                }
                return null;
            }
            catch (NoClassDefFoundError error) {
                return new Issue(IssueKind.MISSING_CLASS, requirement.display(), RuntimeCheck.describe(error));
            }
            catch (Throwable throwable) {
                throw new ProbeFailure(RuntimeCheck.describe(throwable));
            }
        }

        private static Issue verifyConstructor(Class<?> owner, Requirement requirement) throws ProbeFailure, ClassNotFoundException {
            Object[] parameters = new Class[requirement.parameterTypes().size()];
            for (int index = 0; index < parameters.length; ++index) {
                parameters[index] = RuntimeCheck.resolveType(requirement.parameterTypes().get(index));
            }
            try {
                for (Constructor<?> constructor : owner.getDeclaredConstructors()) {
                    if (!Arrays.equals(constructor.getParameterTypes(), parameters)) continue;
                    return null;
                }
                return new Issue(IssueKind.MISSING_METHOD, requirement.display(), "The constructor does not exist on the runtime class.");
            }
            catch (NoClassDefFoundError error) {
                return new Issue(IssueKind.MISSING_CLASS, requirement.display(), RuntimeCheck.describe(error));
            }
            catch (Throwable throwable) {
                throw new ProbeFailure(RuntimeCheck.describe(throwable));
            }
        }

        private static boolean methodExists(Class<?> owner, String name, Class<?> returnType, Class<?>[] parameters, boolean staticMember, boolean searchParents, Set<Class<?>> visited) {
            if (owner == null || !visited.add(owner)) {
                return false;
            }
            for (Method method : owner.getDeclaredMethods()) {
                if (!method.getName().equals(name) || method.getReturnType() != returnType || !Arrays.equals(method.getParameterTypes(), parameters) || Modifier.isStatic(method.getModifiers()) != staticMember) continue;
                return true;
            }
            if (!searchParents) {
                return false;
            }
            if (RuntimeCheck.methodExists(owner.getSuperclass(), name, returnType, parameters, staticMember, true, visited)) {
                return true;
            }
            for (GenericDeclaration genericDeclaration : owner.getInterfaces()) {
                if (!RuntimeCheck.methodExists(genericDeclaration, name, returnType, parameters, staticMember, true, visited)) continue;
                return true;
            }
            return false;
        }

        private static Field findField(Class<?> owner, String name, Set<Class<?>> visited) {
            if (owner == null || !visited.add(owner)) {
                return null;
            }
            try {
                return owner.getDeclaredField(name);
            }
            catch (NoSuchFieldException ignored) {
                Field inherited = RuntimeCheck.findField(owner.getSuperclass(), name, visited);
                if (inherited != null) {
                    return inherited;
                }
                for (Class<?> interfaceType : owner.getInterfaces()) {
                    inherited = RuntimeCheck.findField(interfaceType, name, visited);
                    if (inherited == null) continue;
                    return inherited;
                }
                return null;
            }
        }

        private static String declaredProbeMethodName(String probeClass) throws ProbeFailure {
            try {
                Method[] methods = Class.forName(probeClass, false, RuntimeCheck.loader()).getDeclaredMethods();
                if (methods.length != 1) {
                    throw new ProbeFailure("Expected exactly one declared probe method, found " + methods.length + ".");
                }
                return methods[0].getName();
            }
            catch (ProbeFailure failure) {
                throw failure;
            }
            catch (Throwable throwable) {
                throw new ProbeFailure(RuntimeCheck.describe(throwable));
            }
        }

        private static Class<?> resolveType(String token) throws ClassNotFoundException, ProbeFailure {
            int dimensions;
            for (dimensions = 0; dimensions < token.length() && token.charAt(dimensions) == '['; ++dimensions) {
            }
            if (dimensions == token.length()) {
                throw new ProbeFailure("Invalid type token: " + token);
            }
            String baseToken = token.substring(dimensions);
            Class<Object> base = switch (baseToken.charAt(0)) {
                case 'V' -> Void.TYPE;
                case 'Z' -> Boolean.TYPE;
                case 'B' -> Byte.TYPE;
                case 'S' -> Short.TYPE;
                case 'I' -> Integer.TYPE;
                case 'J' -> Long.TYPE;
                case 'C' -> Character.TYPE;
                case 'F' -> Float.TYPE;
                case 'D' -> Double.TYPE;
                case 'L' -> Class.forName(baseToken.substring(1), false, RuntimeCheck.loader());
                case 'P' -> RuntimeCheck.resolveProbeType(baseToken.substring(1));
                default -> throw new ProbeFailure("Unknown type token: " + token);
            };
            for (int index = 0; index < dimensions; ++index) {
                base = Array.newInstance(base, 0).getClass();
            }
            return base;
        }

        private static Class<?> resolveProbeType(String probeToken) throws ProbeFailure {
            int separator = probeToken.lastIndexOf(35);
            if (separator <= 0 || separator == probeToken.length() - 1) {
                throw new ProbeFailure("Invalid reobf type probe token: " + probeToken);
            }
            String className = probeToken.substring(0, separator);
            String fieldName = probeToken.substring(separator + 1);
            try {
                return Class.forName(className, false, RuntimeCheck.loader()).getDeclaredField(fieldName).getType();
            }
            catch (Throwable throwable) {
                throw new ProbeFailure(RuntimeCheck.describe(throwable));
            }
        }

        private static GroupSpec readGroup(String requestedKey) throws IOException {
            Enum kind;
            byte[] bytes;
            InputStream resource = RuntimeCheck.loader().getResourceAsStream(YSMModelAssetsCompatibilityChecker.MANIFEST_RESOURCE);
            if (resource == null) {
                throw new FileNotFoundException(YSMModelAssetsCompatibilityChecker.MANIFEST_RESOURCE);
            }
            try (InputStream inputStream = resource;){
                bytes = resource.readNBytes(0x4000001);
            }
            if (bytes.length > 0x4000000) {
                throw new IOException("YSM compatibility manifest is too large.");
            }
            JsonElement document = JsonParser.parseString((String)new String(bytes, StandardCharsets.UTF_8));
            JsonObject root = RuntimeCheck.requireObject(document, "manifest");
            int version = RuntimeCheck.requireInteger(root.get("version"), "version");
            if (version != 1) {
                throw new IOException("Unsupported YSM compatibility manifest version: " + version);
            }
            String canary = RuntimeCheck.requireString(root.get("canaryProbe"), "canaryProbe");
            JsonObject groups = RuntimeCheck.requireObject(root.get("groups"), "groups");
            if (!groups.has(requestedKey)) {
                throw new IOException("Compatibility group not found: " + requestedKey);
            }
            JsonObject selected = RuntimeCheck.requireObject(groups.get(requestedKey), "group " + requestedKey);
            String side = RuntimeCheck.requireString(selected.get("side"), "side");
            JsonArray issueValues = RuntimeCheck.requireArray(selected.get("issues"), "issues");
            ArrayList<Issue> issues = new ArrayList<Issue>(issueValues.size());
            for (int index = 0; index < issueValues.size(); ++index) {
                JsonObject issue = RuntimeCheck.requireObject(issueValues.get(index), "issue[" + index + "]");
                String kindName = RuntimeCheck.requireString(issue.get("kind"), "issue.kind");
                String symbol = RuntimeCheck.requireString(issue.get("symbol"), "issue.symbol");
                Object detail = RuntimeCheck.requireString(issue.get("detail"), "issue.detail");
                try {
                    kind = IssueKind.valueOf(kindName);
                }
                catch (IllegalArgumentException ignored) {
                    kind = IssueKind.UNKNOWN_COVERAGE_GAP;
                    detail = kindName + ": " + (String)detail;
                }
                issues.add(new Issue((IssueKind)kind, symbol, (String)detail));
            }
            JsonArray requirementValues = RuntimeCheck.requireArray(selected.get("requirements"), "requirements");
            ArrayList<Requirement> requirements = new ArrayList<Requirement>(requirementValues.size());
            for (int index = 0; index < requirementValues.size(); ++index) {
                JsonObject requirement = RuntimeCheck.requireObject(requirementValues.get(index), "requirement[" + index + "]");
                String kindName = RuntimeCheck.requireString(requirement.get("kind"), "requirement.kind");
                try {
                    kind = RequirementKind.valueOf(kindName);
                }
                catch (IllegalArgumentException exception) {
                    throw new IOException("Bad requirement kind: " + kindName, exception);
                }
                String owner = RuntimeCheck.requireString(requirement.get("owner"), "requirement.owner");
                String name = RuntimeCheck.requireString(requirement.get("name"), "requirement.name");
                String descriptor = RuntimeCheck.requireString(requirement.get("descriptor"), "requirement.descriptor");
                boolean declaredOnly = RuntimeCheck.requireBoolean(requirement.get("declaredOnly"), "requirement.declaredOnly");
                boolean staticMember = RuntimeCheck.requireBoolean(requirement.get("staticMember"), "requirement.staticMember");
                String nameProbe = RuntimeCheck.requireString(requirement.get("nameProbe"), "requirement.nameProbe");
                String fieldType = RuntimeCheck.requireString(requirement.get("fieldType"), "requirement.fieldType");
                String returnType = RuntimeCheck.requireString(requirement.get("returnType"), "requirement.returnType");
                JsonArray parameterValues = RuntimeCheck.requireArray(requirement.get("parameterTypes"), "requirement.parameterTypes");
                ArrayList<String> parameters = new ArrayList<String>(parameterValues.size());
                for (int parameterIndex = 0; parameterIndex < parameterValues.size(); ++parameterIndex) {
                    parameters.add(RuntimeCheck.requireString(parameterValues.get(parameterIndex), "requirement.parameterTypes[" + parameterIndex + "]"));
                }
                requirements.add(new Requirement((RequirementKind)kind, owner, name, descriptor, declaredOnly, staticMember, nameProbe, fieldType, returnType, List.copyOf(parameters)));
            }
            return new GroupSpec(canary, side, List.copyOf(issues), List.copyOf(requirements));
        }

        private static JsonObject requireObject(JsonElement value, String label) throws IOException {
            if (value == null || !value.isJsonObject()) {
                throw RuntimeCheck.jsonTypeError(label, "object");
            }
            JsonObject object = value.getAsJsonObject();
            if (object.size() > 1000000) {
                throw new IOException("Too many entries in " + label + ".");
            }
            return object;
        }

        private static JsonArray requireArray(JsonElement value, String label) throws IOException {
            if (value == null || !value.isJsonArray()) {
                throw RuntimeCheck.jsonTypeError(label, "array");
            }
            JsonArray array = value.getAsJsonArray();
            if (array.size() > 1000000) {
                throw new IOException("Too many entries in " + label + ".");
            }
            return array;
        }

        private static String requireString(JsonElement value, String label) throws IOException {
            if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
                throw RuntimeCheck.jsonTypeError(label, "string");
            }
            return value.getAsString();
        }

        private static boolean requireBoolean(JsonElement value, String label) throws IOException {
            if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
                throw RuntimeCheck.jsonTypeError(label, "boolean");
            }
            return value.getAsBoolean();
        }

        private static int requireInteger(JsonElement value, String label) throws IOException {
            if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
                throw RuntimeCheck.jsonTypeError(label, "integer");
            }
            try {
                return new BigDecimal(value.getAsString()).intValueExact();
            }
            catch (ArithmeticException | NumberFormatException exception) {
                throw RuntimeCheck.jsonTypeError(label, "integer");
            }
        }

        private static IOException jsonTypeError(String label, String expected) {
            return new IOException("Expected " + label + " to be a JSON " + expected + ".");
        }

        private static Result result(Status status, IssueKind kind, String symbol, String detail) {
            return new Result(status, List.of((Object)((Object)new Issue(kind, symbol, detail))));
        }

        private static ClassLoader loader() {
            ClassLoader loader = YSMModelAssetsCompatibilityChecker.class.getClassLoader();
            return loader == null ? ClassLoader.getSystemClassLoader() : loader;
        }

        private static String describe(Throwable throwable) {
            InvocationTargetException invocation;
            Throwable current = throwable;
            if (throwable instanceof InvocationTargetException && (invocation = (InvocationTargetException)throwable).getCause() != null) {
                current = invocation.getCause();
            }
            String message = current.getMessage();
            return current.getClass().getName() + (String)(message == null ? "" : ": " + message);
        }

        private record GroupSpec(String canaryProbe, String side, List<Issue> issues, List<Requirement> requirements) {
        }

        private record Requirement(RequirementKind kind, String owner, String name, String descriptor, boolean declaredOnly, boolean staticMember, String nameProbe, String fieldType, String returnType, List<String> parameterTypes) {
            String display() {
                return switch (this.kind) {
                    default -> throw new IncompatibleClassChangeError();
                    case RequirementKind.CLASS, RequirementKind.CLASS_OWNER, RequirementKind.EXTENDABLE_CLASS, RequirementKind.INTERFACE, RequirementKind.INSTANTIABLE_CLASS -> this.owner;
                    case RequirementKind.METHOD, RequirementKind.CONSTRUCTOR -> this.owner + "#" + this.name + this.descriptor;
                    case RequirementKind.FIELD -> this.owner + "#" + this.name + ":" + this.descriptor;
                };
            }
        }

        private static final class ProbeFailure
        extends Exception {
            ProbeFailure(String message) {
                super(message);
            }
        }

        private static enum RequirementKind {
            CLASS,
            CLASS_OWNER,
            EXTENDABLE_CLASS,
            INTERFACE,
            INSTANTIABLE_CLASS,
            METHOD,
            FIELD,
            CONSTRUCTOR;

        }
    }

    public record Result(Status status, List<Issue> issues) {
        public Result {
            issues = List.copyOf(issues);
        }

        public boolean isCompatible() {
            return this.status == Status.COMPATIBLE || this.status == Status.COMPATIBLE_WITH_WARNINGS;
        }

        public boolean coverageComplete() {
            return this.issues.stream().noneMatch(issue -> switch (issue.kind()) {
                case IssueKind.DYNAMIC_TARGET_UNKNOWN, IssueKind.DYNAMIC_DISPATCH_UNKNOWN, IssueKind.OWNED_CODE_UNAVAILABLE, IssueKind.SIGNATURE_UNREADABLE, IssueKind.SIDE_MISMATCH, IssueKind.MIXIN_RUNTIME_TRANSFORM, IssueKind.REOBF_CANARY_UNAVAILABLE, IssueKind.UNKNOWN_COVERAGE_GAP -> true;
                default -> false;
            });
        }
    }

    public record Issue(IssueKind kind, String symbol, String detail) {
    }

    public static enum IssueKind {
        MISSING_CLASS,
        MISSING_METHOD,
        MISSING_FIELD,
        TYPE_MISMATCH,
        DYNAMIC_TARGET_UNKNOWN,
        DYNAMIC_DISPATCH_UNKNOWN,
        OWNED_CODE_UNAVAILABLE,
        SIGNATURE_UNREADABLE,
        SIDE_MISMATCH,
        MIXIN_RUNTIME_TRANSFORM,
        REOBF_CANARY_UNAVAILABLE,
        REOBF_PROBE_ERROR,
        YSM_ENTRYPOINT_ERROR,
        MANIFEST_ERROR,
        UNKNOWN_COVERAGE_GAP;

    }

    public static enum Status {
        COMPATIBLE,
        COMPATIBLE_WITH_WARNINGS,
        NOT_APPLICABLE,
        YSM_NOT_LOADED,
        YSM_UNAVAILABLE,
        INCOMPATIBLE,
        CHECK_FAILED;

    }
}

