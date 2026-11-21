package net.jrdemiurge.enigmaticdice.network;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.event.ClientTimeHandler;
import net.jrdemiurge.enigmaticdice.item.custom.GravityCore;
import net.jrdemiurge.enigmaticdice.item.custom.crucibleofrile.ClientLookController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {

    public static final String PROTOCOL_VERSION = "1";

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToServer(
                DoubleJumpPacket.TYPE,
                DoubleJumpPacket.STREAM_CODEC,
                DoubleJumpPacket::handle
        );

        registrar.playToClient(
                TimeAccelStartPacket.TYPE,
                TimeAccelStartPacket.STREAM_CODEC,
                TimeAccelStartPacket::handle
        );

        registrar.playToClient(
                LookAtTargetPacket.TYPE,
                LookAtTargetPacket.STREAM_CODEC,
                LookAtTargetPacket::handle
        );
    }

    public record DoubleJumpPacket() implements CustomPacketPayload {
        public static final Type<DoubleJumpPacket> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(EnigmaticDice.MOD_ID, "double_jump"));
        public static final StreamCodec<FriendlyByteBuf, DoubleJumpPacket> STREAM_CODEC =
                StreamCodec.unit(new DoubleJumpPacket());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(DoubleJumpPacket payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    GravityCore.jump(player);
                }
            });
        }
    }

    public record TimeAccelStartPacket(int multiplier, int durationTicks) implements CustomPacketPayload {
        public static final Type<TimeAccelStartPacket> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(EnigmaticDice.MOD_ID, "time_accel_start"));
        public static final StreamCodec<FriendlyByteBuf, TimeAccelStartPacket> STREAM_CODEC =
                StreamCodec.of(
                        (buf, pkt) -> {
                            buf.writeVarInt(pkt.multiplier());
                            buf.writeVarInt(pkt.durationTicks());
                        },
                        buf -> new TimeAccelStartPacket(buf.readVarInt(), buf.readVarInt())
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(TimeAccelStartPacket payload, IPayloadContext context) {
            context.enqueueWork(() -> ClientTimeHandler.start(payload.multiplier(), payload.durationTicks()));
        }
    }

    public record LookAtTargetPacket(java.util.UUID targetUuid, int durationTicks) implements CustomPacketPayload {
        public static final Type<LookAtTargetPacket> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(EnigmaticDice.MOD_ID, "look_at_target"));
        public static final StreamCodec<FriendlyByteBuf, LookAtTargetPacket> STREAM_CODEC =
                StreamCodec.of(
                        (buf, pkt) -> {
                            buf.writeUUID(pkt.targetUuid());
                            buf.writeVarInt(pkt.durationTicks());
                        },
                        buf -> new LookAtTargetPacket(buf.readUUID(), buf.readVarInt())
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void handle(LookAtTargetPacket payload, IPayloadContext context) {
            context.enqueueWork(() -> ClientLookController.startOrExtend(payload.targetUuid(), payload.durationTicks()));
        }
    }
}
