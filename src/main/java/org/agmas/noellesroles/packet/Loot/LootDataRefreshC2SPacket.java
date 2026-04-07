package org.agmas.noellesroles.packet.Loot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.agmas.noellesroles.Noellesroles;

public record LootDataRefreshC2SPacket() implements CustomPacketPayload {
    public static final ResourceLocation LOOT_DATA_REFRESH_CLIENT_PAYLOAD_ID =
            ResourceLocation.fromNamespaceAndPath(Noellesroles.MOD_ID, "loot_data_refresh_client");
    public static final Type<LootDataRefreshC2SPacket> ID = new CustomPacketPayload.Type<>(LOOT_DATA_REFRESH_CLIENT_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, LootDataRefreshC2SPacket> CODEC;
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void write(FriendlyByteBuf buf) {
    }

    public static LootDataRefreshC2SPacket read(FriendlyByteBuf buf) {
        return new LootDataRefreshC2SPacket(
        );
    }
    static {
        CODEC = StreamCodec.ofMember(LootDataRefreshC2SPacket::write, LootDataRefreshC2SPacket::read);
    }
}
