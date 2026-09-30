/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapability
 *  com.elfmcys.ysm.capability.PlayerAnimatableCapabilityProvider
 *  com.elfmcys.ysm.geckolib3.core.AnimationState
 *  com.elfmcys.ysm.geckolib3.core.builder.Animation
 *  com.elfmcys.ysm.geckolib3.core.builder.LoopType
 *  com.elfmcys.ysm.geckolib3.core.builder.controller.AnimationControllerData
 *  com.elfmcys.ysm.geckolib3.core.builder.controller.AnimationControllerState
 *  com.elfmcys.ysm.geckolib3.core.controller.IAnimationController
 *  com.elfmcys.ysm.geckolib3.core.molang.util.StringPool
 *  com.elfmcys.ysm.model.resource.client.ModelRenderTarget
 *  com.elfmcys.ysm.model.resource.client.PlayerModelResources
 *  net.minecraft.client.player.AbstractClientPlayer
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.ysm;

import com.elfmcys.ysm.capability.PlayerAnimatableCapability;
import com.elfmcys.ysm.capability.PlayerAnimatableCapabilityProvider;
import com.elfmcys.ysm.geckolib3.core.AnimationState;
import com.elfmcys.ysm.geckolib3.core.builder.Animation;
import com.elfmcys.ysm.geckolib3.core.builder.LoopType;
import com.elfmcys.ysm.geckolib3.core.builder.controller.AnimationControllerData;
import com.elfmcys.ysm.geckolib3.core.builder.controller.AnimationControllerState;
import com.elfmcys.ysm.geckolib3.core.controller.IAnimationController;
import com.elfmcys.ysm.geckolib3.core.molang.util.StringPool;
import com.elfmcys.ysm.model.resource.client.ModelRenderTarget;
import com.elfmcys.ysm.model.resource.client.PlayerModelResources;
import io.github.tt432.yessteveskill.ysm.YSMModelAssets;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.player.AbstractClientPlayer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public final class YSMClientAccess {
    private static final String CODED_STATE_PREFIX = "Coded -> ";
    private static final String ANIMATION_ANNOTATION_PREFIX = "\u2014\u2014";
    @Nullable
    private static final Method REAL_STATE_METHOD = YSMClientAccess.findControllerMethod("getAnimationState", new Class[0]);
    @Nullable
    private static final Method REAL_TICKS_METHOD = YSMClientAccess.findControllerMethod("getAnimationTicks", Float.TYPE);
    private final Map<PlayerAnimatableCapability, Map<SlotKey, PlaybackClock>> playbackClocks = new WeakHashMap<PlayerAnimatableCapability, Map<SlotKey, PlaybackClock>>();
    private final YSMModelAssets modelAssets = new YSMModelAssets();

    @Nullable
    public PlayerAnimatableCapability getCapability(AbstractClientPlayer player) {
        return (PlayerAnimatableCapability)player.getCapability(PlayerAnimatableCapabilityProvider.CAP).orElse(null);
    }

    public List<String> getAnimationNames(PlayerAnimatableCapability capability) {
        PlayerModelResources resources;
        ModelRenderTarget renderTarget = capability.getModelRenderTarget();
        PlayerModelResources playerModelResources = resources = renderTarget == null ? null : renderTarget.playerResources();
        if (resources == null) {
            return List.of();
        }
        return resources.animations().keySet().stream().filter(name -> !name.isBlank() && !name.startsWith(ANIMATION_ANNOTATION_PREFIX)).sorted().toList();
    }

    public CompletableFuture<YSMModelAssets.ModelAssets> requestModelAssets(PlayerAnimatableCapability capability) {
        return this.modelAssets.request(capability);
    }

    public CompletableFuture<YSMModelAssets.ModelAssets> requestModelAssets(String modelId) {
        return this.modelAssets.request(modelId);
    }

    @Nullable
    public YSMModelAssets.ModelAssets getModelAssetsNow(PlayerAnimatableCapability capability) {
        return this.modelAssets.getNow(capability);
    }

    @Nullable
    public Animation getAnimation(PlayerAnimatableCapability capability, String animationName) {
        return capability.getAnimation(animationName);
    }

    public List<ControllerInfo> getControllerInfos(PlayerAnimatableCapability capability, float renderTicks) {
        List<ControllerSlotInfo> slotInfos = this.getControllerSlotInfos(capability, renderTicks);
        LinkedHashMap<String, ControllerSlotInfo> deduplicated = new LinkedHashMap<String, ControllerSlotInfo>();
        for (ControllerSlotInfo slotInfo : slotInfos) {
            ControllerSlotInfo existing = (ControllerSlotInfo)((Object)deduplicated.get(slotInfo.name()));
            if (existing != null && !this.preferSlot(slotInfo, existing)) continue;
            deduplicated.put(slotInfo.name(), slotInfo);
        }
        ArrayList<ControllerInfo> result = new ArrayList<ControllerInfo>(deduplicated.size());
        for (ControllerSlotInfo slotInfo : deduplicated.values()) {
            result.add(new ControllerInfo(slotInfo.name(), slotInfo.stateName(), slotInfo.animationName(), slotInfo.animState(), slotInfo.animationTicks(), slotInfo.animationLength()));
        }
        return result;
    }

    public List<ControllerSlotInfo> getControllerSlotInfos(PlayerAnimatableCapability capability, float renderTicks) {
        ArrayList<ControllerSlotInfo> result = new ArrayList<ControllerSlotInfo>();
        Map clocks = this.playbackClocks.computeIfAbsent(capability, ignored -> new HashMap());
        HashSet<SlotKey> seenSlots = new HashSet<SlotKey>();
        for (IAnimationController controller : capability.getAnimationData().getAnimationControllers()) {
            String stateName = controller.getStateName();
            int previousSize = result.size();
            if (this.isCodedState(stateName)) {
                this.appendSlot(capability, controller, stateName, 0, this.currentCodedAnimation(stateName), renderTicks, clocks, seenSlots, result);
            } else {
                AnimationControllerState state = this.currentBedrockState(capability, controller.getName(), stateName);
                if (state != null && !state.isBuiltin() && state.subEntryName() == null) {
                    for (int slot = 0; slot < state.animations().size(); ++slot) {
                        this.appendSlot(capability, controller, stateName, slot, (String)((Pair)state.animations().get(slot)).getKey(), renderTicks, clocks, seenSlots, result);
                    }
                }
            }
            if (result.size() != previousSize) continue;
            this.appendSlot(capability, controller, stateName, 0, null, renderTicks, clocks, seenSlots, result);
        }
        clocks.keySet().removeIf(key -> !seenSlots.contains(key));
        return result;
    }

    private void appendSlot(PlayerAnimatableCapability capability, IAnimationController<?> controller, @Nullable String stateName, int slotIndex, @Nullable String animationName, float renderTicks, Map<SlotKey, PlaybackClock> clocks, Set<SlotKey> seenSlots, List<ControllerSlotInfo> result) {
        float realTicks;
        Animation animation = animationName == null ? null : capability.getAnimation(animationName);
        String effectiveName = animation == null ? null : animationName;
        String controllerName = controller.getName();
        SlotKey key = new SlotKey(controllerName, slotIndex);
        seenSlots.add(key);
        AnimationState realState = YSMClientAccess.reflectAnimationState(controller);
        if (realState != null && (realTicks = YSMClientAccess.reflectAnimationTicks(controller, renderTicks)) >= 0.0f) {
            result.add(new ControllerSlotInfo(controllerName, stateName, slotIndex, effectiveName, realState.ordinal(), realTicks, animation == null ? 0.0f : animation.animationLength));
            return;
        }
        int animationState = animation == null ? AnimationState.IDLE.ordinal() : AnimationState.RUNNING.ordinal();
        float animationTicks = this.observeAnimationTicks(clocks, key, stateName, effectiveName, renderTicks, animation);
        result.add(new ControllerSlotInfo(controllerName, stateName, slotIndex, effectiveName, animationState, animationTicks, animation == null ? 0.0f : animation.animationLength));
    }

    @Nullable
    private static AnimationState reflectAnimationState(IAnimationController<?> controller) {
        Method method = REAL_STATE_METHOD;
        if (method == null) {
            return null;
        }
        try {
            AnimationState state;
            Object value = method.invoke(controller, new Object[0]);
            return value instanceof AnimationState ? (state = (AnimationState)value) : null;
        }
        catch (ReflectiveOperationException | RuntimeException error) {
            return null;
        }
    }

    private static float reflectAnimationTicks(IAnimationController<?> controller, float renderTicks) {
        Method method = REAL_TICKS_METHOD;
        if (method == null) {
            return -1.0f;
        }
        try {
            float f;
            Object value = method.invoke(controller, Float.valueOf(renderTicks));
            if (value instanceof Float) {
                Float ticks = (Float)value;
                f = ticks.floatValue();
            } else {
                f = -1.0f;
            }
            return f;
        }
        catch (ReflectiveOperationException | RuntimeException error) {
            return -1.0f;
        }
    }

    @Nullable
    private static Method findControllerMethod(String name, Class<?> ... parameterTypes) {
        try {
            return IAnimationController.class.getMethod(name, parameterTypes);
        }
        catch (NoSuchMethodException error) {
            return null;
        }
    }

    private boolean isCodedState(String visibleStateName) {
        String stateName = this.stripBuiltinPrefix(visibleStateName);
        return stateName.equals("Coded") || stateName.startsWith(CODED_STATE_PREFIX);
    }

    @Nullable
    private String currentCodedAnimation(String visibleStateName) {
        String stateName = this.stripBuiltinPrefix(visibleStateName);
        if (!stateName.startsWith(CODED_STATE_PREFIX)) {
            return null;
        }
        String animationName = stateName.substring(CODED_STATE_PREFIX.length());
        return animationName.isBlank() ? null : animationName;
    }

    private String stripBuiltinPrefix(String stateName) {
        String prefix = "[builtin] ";
        return stateName.startsWith(prefix) ? stateName.substring(prefix.length()) : stateName;
    }

    @Nullable
    private AnimationControllerState currentBedrockState(PlayerAnimatableCapability capability, String controllerName, String visibleStateName) {
        AnimationControllerData data;
        Object dataName = controllerName;
        String stateName = visibleStateName;
        if (visibleStateName.startsWith("[")) {
            int hierarchyEnd = visibleStateName.indexOf("] ");
            if (hierarchyEnd <= 1 || hierarchyEnd + 2 >= visibleStateName.length()) {
                return null;
            }
            String hierarchy = visibleStateName.substring(1, hierarchyEnd);
            dataName = (String)dataName + "." + hierarchy;
            stateName = visibleStateName.substring(hierarchyEnd + 2);
        }
        return (data = capability.getAnimationControllerData((String)dataName)) == null ? null : (AnimationControllerState)data.states().get(StringPool.computeIfAbsent((String)stateName));
    }

    private float observeAnimationTicks(Map<SlotKey, PlaybackClock> clocks, SlotKey key, @Nullable String stateName, @Nullable String animationName, float renderTicks, @Nullable Animation animation) {
        PlaybackClock clock = clocks.get((Object)key);
        if (clock == null || !Objects.equals(clock.stateName, stateName) || !Objects.equals(clock.animationName, animationName) || renderTicks + 0.001f < clock.lastRenderTicks) {
            clock = new PlaybackClock(stateName, animationName, renderTicks);
            clocks.put(key, clock);
        }
        clock.lastRenderTicks = renderTicks;
        if (animation == null || animation.animationLength <= 0.0f) {
            return 0.0f;
        }
        float elapsed = Math.max(0.0f, renderTicks - clock.startRenderTicks);
        if (animation.loop == LoopType.LOOP) {
            return elapsed % animation.animationLength;
        }
        return Math.min(elapsed, animation.animationLength);
    }

    public String getModelId(PlayerAnimatableCapability capability) {
        return capability.getModelId();
    }

    private boolean preferSlot(ControllerSlotInfo candidate, ControllerSlotInfo existing) {
        if (candidate.isPlaying() != existing.isPlaying()) {
            return candidate.isPlaying();
        }
        return candidate.slotIndex() < existing.slotIndex();
    }

    public record ControllerSlotInfo(String name, @Nullable String stateName, int slotIndex, @Nullable String animationName, int animState, float animationTicks, float animationLength) {
        public boolean isPlaying() {
            return this.animationName != null && !this.animationName.isBlank() && this.animState != ControllerInfo.STATE_IDLE;
        }
    }

    public record ControllerInfo(String name, @Nullable String stateName, @Nullable String animationName, int animState, float animationTicks, float animationLength) {
        public static final int STATE_IDLE = AnimationState.IDLE.ordinal();
        public static final int STATE_BEGINNING_TRANSITION = AnimationState.BEGINNING_TRANSITION.ordinal();
        public static final int STATE_RUNNING = AnimationState.RUNNING.ordinal();
        public static final int STATE_ENDING_TRANSITION = AnimationState.ENDING_TRANSITION.ordinal();

        public boolean isPlaying() {
            return this.animationName != null && !this.animationName.isBlank() && this.animState != STATE_IDLE;
        }
    }

    private record SlotKey(String controllerName, int slotIndex) {
    }

    private static final class PlaybackClock {
        @Nullable
        private final String stateName;
        @Nullable
        private final String animationName;
        private final float startRenderTicks;
        private float lastRenderTicks;

        private PlaybackClock(@Nullable String stateName, @Nullable String animationName, float renderTicks) {
            this.stateName = stateName;
            this.animationName = animationName;
            this.startRenderTicks = renderTicks;
            this.lastRenderTicks = renderTicks;
        }
    }
}

