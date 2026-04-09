package io.wifi.starrailexpress.client.util;

import net.minecraft.client.Minecraft;

import java.util.UUID;

public class TMMClientUtils {
    public static UUID getPlayerUidByName(String name) {
        var s = Minecraft.getInstance().getConnection().getPlayerInfo(name);
        if (s == null)
            return null;
        return s.getProfile().getId();
    }

    public static String getPlayerNameByUid(UUID uid) {
        var s = Minecraft.getInstance().getConnection().getPlayerInfo(uid);
        if (s == null)
            return null;
        return s.getProfile().getName();
    }
}
