package io.wifi.starrailexpress.mixin.gui;

import com.kreezcraft.localizedchat.CommonClass;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;


@Mixin(CommonClass.class)
public class CommonTalkChatMixin {
    /**
     * @author
     * @reason 避免阻挡消息链条
     */
    @Overwrite
    public static boolean onChatMessage(ServerPlayer sender, String message) {
        return false;
    }
}
