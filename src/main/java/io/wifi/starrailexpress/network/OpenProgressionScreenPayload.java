package io.wifi.starrailexpress.network;

import io.wifi.starrailexpress.SRE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenProgressionScreenPayload() implements CustomPacketPayload {
    public static final Type<OpenProgressionScreenPayload> ID = new Type<>(SRE.id("open_progression_screen"));
    public static final StreamCodec<FriendlyByteBuf, OpenProgressionScreenPayload> CODEC =
            CustomPacketPayload.codec(OpenProgressionScreenPayload::encode, OpenProgressionScreenPayload::decode);

    public static final OpenProgressionScreenPayload INSTANCE = new OpenProgressionScreenPayload();

    public static void encode(OpenProgressionScreenPayload payload, FriendlyByteBuf buf) {
    }

    public static OpenProgressionScreenPayload decode(FriendlyByteBuf buf) {
        return INSTANCE;
    }

    @Override
    public Type<OpenProgressionScreenPayload> type() {
        return ID;
    }
}