package org.agmas.noellesroles.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.agmas.noellesroles.Noellesroles;

public record CreateCreeperBombAreaPacket(Vec3 position)
        implements CustomPacketPayload {
    public static final ResourceLocation ABILITY_PAYLOAD_ID = ResourceLocation.fromNamespaceAndPath(Noellesroles.MOD_ID,
            "creeper_bomb");
    public static final Type<CreateCreeperBombAreaPacket> ID = new Type<>(ABILITY_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CreateCreeperBombAreaPacket> CODEC;

    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVec3(position);
    }

    public static CreateCreeperBombAreaPacket read(FriendlyByteBuf buf) {
        return new CreateCreeperBombAreaPacket(buf.readVec3());
    }

    static {
        CODEC = StreamCodec.ofMember(CreateCreeperBombAreaPacket::write, CreateCreeperBombAreaPacket::read);
    }
}