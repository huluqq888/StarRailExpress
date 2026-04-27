package org.agmas.noellesroles.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.agmas.noellesroles.Noellesroles;

public record ShortShotgunEquipPayload() implements CustomPacketPayload {

    public static final Type<ShortShotgunEquipPayload> ID = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Noellesroles.MOD_ID, "short_shotgun_equip")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ShortShotgunEquipPayload> CODEC = StreamCodec.ofMember(
            (packet, buf) -> {
                // 无需写入数据，只是触发音效
            },
            buf -> new ShortShotgunEquipPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
