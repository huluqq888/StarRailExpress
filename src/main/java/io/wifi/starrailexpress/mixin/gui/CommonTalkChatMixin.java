package io.wifi.starrailexpress.mixin.gui;

import com.kreezcraft.localizedchat.CommonClass;
// import com.kreezcraft.localizedchat.ConfigCache;
import net.exmo.sre.nametag.NameTagInventoryComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
// import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

// import static com.kreezcraft.localizedchat.CommonClass.compareCoordinatesDistance;
// import static com.kreezcraft.localizedchat.CommonClass.playerName;

@Mixin(CommonClass.class)
public class CommonTalkChatMixin {
    @Unique
    private static MutableComponent somePrefix(Player mainPlayer) {
        if (mainPlayer instanceof ServerPlayer ){
                return NameTagInventoryComponent.KEY.get(mainPlayer).generate();
        }
        return Component.literal("");
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean onChatMessage(ServerPlayer sender, String message) {
        return true;
    }
}
