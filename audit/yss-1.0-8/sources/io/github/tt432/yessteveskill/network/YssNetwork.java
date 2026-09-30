/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package io.github.tt432.yessteveskill.network;

import io.github.tt432.yessteveskill.combat.CombatStateCapability;
import io.github.tt432.yessteveskill.yss.attack.YssAttackModeCapability;
import io.github.tt432.yessteveskill.yss.attack.YssAttackServerExecutor;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class YssNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("yessteveskill", "main"), () -> "1", "1"::equals, "1"::equals);
    private static int nextMessageId;

    private YssNetwork() {
    }

    public static void initialize() {
        CHANNEL.messageBuilder(AttackModeSyncPacket.class, nextMessageId++, NetworkDirection.PLAY_TO_SERVER).encoder(AttackModeSyncPacket::encode).decoder(AttackModeSyncPacket::decode).consumerMainThread(AttackModeSyncPacket::handle).add();
        CHANNEL.messageBuilder(HitEventPacket.class, nextMessageId++, NetworkDirection.PLAY_TO_SERVER).encoder(HitEventPacket::encode).decoder(HitEventPacket::decode).consumerMainThread(HitEventPacket::handle).add();
        CHANNEL.messageBuilder(ModelIdSyncPacket.class, nextMessageId++, NetworkDirection.PLAY_TO_SERVER).encoder(ModelIdSyncPacket::encode).decoder(ModelIdSyncPacket::decode).consumerMainThread(ModelIdSyncPacket::handle).add();
        CHANNEL.messageBuilder(CasterMovePacket.class, nextMessageId++, NetworkDirection.PLAY_TO_SERVER).encoder(CasterMovePacket::encode).decoder(CasterMovePacket::decode).consumerMainThread(CasterMovePacket::handle).add();
        CHANNEL.messageBuilder(HoverPacket.class, nextMessageId++, NetworkDirection.PLAY_TO_SERVER).encoder(HoverPacket::encode).decoder(HoverPacket::decode).consumerMainThread(HoverPacket::handle).add();
        CHANNEL.messageBuilder(CombatStateSyncPacket.class, nextMessageId++, NetworkDirection.PLAY_TO_CLIENT).encoder(CombatStateSyncPacket::encode).decoder(CombatStateSyncPacket::decode).consumerMainThread(CombatStateSyncPacket::handle).add();
    }

    public static void sendAttackModeSync(boolean enabled) {
        CHANNEL.sendToServer((Object)new AttackModeSyncPacket(enabled));
    }

    public static void sendHitEvent(int segmentId, int[] targetIds, String animName, float clientAnimTime) {
        CHANNEL.sendToServer((Object)new HitEventPacket(segmentId, targetIds, animName, clientAnimTime));
    }

    public static void sendModelIdSync(String modelId) {
        CHANNEL.sendToServer((Object)new ModelIdSyncPacket(modelId));
    }

    public static void sendCasterMove(int moveIndex, String animName, float clientAnimTime) {
        CHANNEL.sendToServer((Object)new CasterMovePacket(moveIndex, animName, clientAnimTime));
    }

    public static void sendHover(String animName) {
        CHANNEL.sendToServer((Object)new HoverPacket(animName));
    }

    public static void sendCombatStateSync(LivingEntity entity, int hitstopTicks, int interruptedTicks, int knockupTicks, float overrideAntiInterrupt, int overrideAntiInterruptTicks, int hoverTicks) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), (Object)new CombatStateSyncPacket(entity.m_19879_(), hitstopTicks, interruptedTicks, knockupTicks, overrideAntiInterrupt, overrideAntiInterruptTicks, hoverTicks));
    }

    private record AttackModeSyncPacket(boolean enabled) {
        private static AttackModeSyncPacket decode(FriendlyByteBuf buf) {
            return new AttackModeSyncPacket(buf.readBoolean());
        }

        private void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(this.enabled);
        }

        private static void handle(AttackModeSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            ServerPlayer sender = contextSupplier.get().getSender();
            if (sender != null) {
                YssAttackModeCapability.setEnabled((Player)sender, packet.enabled);
                if (!packet.enabled) {
                    YssAttackModeCapability.setCachedModelId((Player)sender, null);
                }
            }
        }
    }

    private record HitEventPacket(int segmentId, int[] targetIds, String animName, float clientAnimTime) {
        private static HitEventPacket decode(FriendlyByteBuf buf) {
            int segmentId = buf.m_130242_();
            int[] targetIds = buf.m_130100_();
            String animName = buf.m_130277_();
            float clientAnimTime = buf.readFloat();
            return new HitEventPacket(segmentId, targetIds, animName, clientAnimTime);
        }

        private void encode(FriendlyByteBuf buf) {
            buf.m_130130_(this.segmentId);
            buf.m_130089_(this.targetIds);
            buf.m_130070_(this.animName);
            buf.writeFloat(this.clientAnimTime);
        }

        private static void handle(HitEventPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            ServerPlayer sender = contextSupplier.get().getSender();
            if (sender != null) {
                YssAttackServerExecutor.executeHitEvent(sender, packet.segmentId, packet.targetIds, packet.animName, packet.clientAnimTime);
            }
        }
    }

    private record ModelIdSyncPacket(String modelId) {
        private static ModelIdSyncPacket decode(FriendlyByteBuf buf) {
            return new ModelIdSyncPacket(buf.m_130277_());
        }

        private void encode(FriendlyByteBuf buf) {
            buf.m_130070_(this.modelId);
        }

        private static void handle(ModelIdSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            ServerPlayer sender = contextSupplier.get().getSender();
            if (sender != null) {
                YssAttackModeCapability.setCachedModelId((Player)sender, packet.modelId);
            }
        }
    }

    private record CasterMovePacket(int moveIndex, String animName, float clientAnimTime) {
        private static CasterMovePacket decode(FriendlyByteBuf buf) {
            int moveIndex = buf.m_130242_();
            String animName = buf.m_130277_();
            float clientAnimTime = buf.readFloat();
            return new CasterMovePacket(moveIndex, animName, clientAnimTime);
        }

        private void encode(FriendlyByteBuf buf) {
            buf.m_130130_(this.moveIndex);
            buf.m_130070_(this.animName);
            buf.writeFloat(this.clientAnimTime);
        }

        private static void handle(CasterMovePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            ServerPlayer sender = contextSupplier.get().getSender();
            if (sender != null) {
                YssAttackServerExecutor.executeCasterMove(sender, packet.moveIndex, packet.animName, packet.clientAnimTime);
            }
        }
    }

    private record HoverPacket(String animName) {
        private static HoverPacket decode(FriendlyByteBuf buf) {
            return new HoverPacket(buf.m_130277_());
        }

        private void encode(FriendlyByteBuf buf) {
            buf.m_130070_(this.animName);
        }

        private static void handle(HoverPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            ServerPlayer sender = contextSupplier.get().getSender();
            if (sender != null) {
                YssAttackServerExecutor.executeHover(sender, packet.animName);
            }
        }
    }

    private record CombatStateSyncPacket(int entityId, int hitstopTicks, int interruptedTicks, int knockupTicks, float overrideAntiInterrupt, int overrideAntiInterruptTicks, int hoverTicks) {
        private static CombatStateSyncPacket decode(FriendlyByteBuf buf) {
            return new CombatStateSyncPacket(buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.readFloat(), buf.m_130242_(), buf.m_130242_());
        }

        private void encode(FriendlyByteBuf buf) {
            buf.m_130130_(this.entityId);
            buf.m_130130_(this.hitstopTicks);
            buf.m_130130_(this.interruptedTicks);
            buf.m_130130_(this.knockupTicks);
            buf.writeFloat(this.overrideAntiInterrupt);
            buf.m_130130_(this.overrideAntiInterruptTicks);
            buf.m_130130_(this.hoverTicks);
        }

        private static void handle(CombatStateSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            LivingEntity le;
            CombatStateCapability cap;
            Entity e;
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91073_ != null && (e = mc.f_91073_.m_6815_(packet.entityId)) instanceof LivingEntity && (cap = CombatStateCapability.get(le = (LivingEntity)e)) != null) {
                cap.getState().syncFrom(packet.hitstopTicks, packet.knockupTicks, packet.interruptedTicks, packet.overrideAntiInterrupt, packet.overrideAntiInterruptTicks, packet.hoverTicks);
            }
        }
    }
}

