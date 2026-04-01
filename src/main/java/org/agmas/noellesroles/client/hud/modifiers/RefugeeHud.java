package org.agmas.noellesroles.client.hud.modifiers;

import io.wifi.starrailexpress.client.SREClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.client.event.CommonHudRenderCallback;
import pro.fazeclan.river.stupid_express.constants.SEModifiers;
import pro.fazeclan.river.stupid_express.modifier.refugee.cca.RefugeeComponent;

public abstract class RefugeeHud {

    public static void register() {
        CommonHudRenderCallback.EVENT.register((context, deltaTracker) -> {
            final var client = Minecraft.getInstance();
            if (client.player == null || client.level == null)
                return;

            // 检查玩家是否有难民modifier
            WorldModifierComponent worldModifierComponent = WorldModifierComponent.KEY.get(client.level);
            if (!worldModifierComponent.isModifier(client.player.getUUID(), SEModifiers.REFUGEE)) {
                return;
            }

            // 检查玩家是否为旁观者模式
            if (!SREClient.isPlayerSpectator()) {
                return;
            }

            // 获取难民组件
            RefugeeComponent refugeeComponent = RefugeeComponent.KEY.get(client.level);
            if (refugeeComponent == null) {
                return;
            }

            // 计算剩余时间
            long currentTime = client.level.getGameTime();
            long revivalTime = refugeeComponent.getRevivalTime(client.player.getUUID());

            if (revivalTime == -1) {
                return;
            }

            long ticksRemaining = revivalTime - currentTime;
            int secondsRemaining = (int) ((ticksRemaining + 19) / 20);

            Component text = Component.translatable("gui.stupid_express.refugee.revival", secondsRemaining);
            int color = 0x55ff55; // 绿色

            int screenWidth = client.getWindow().getGuiScaledWidth();
            int screenHeight = client.getWindow().getGuiScaledHeight();
            int textWidth = client.font.width(text);

            // 右下角显示
            int x = screenWidth - textWidth - 10;
            int y = screenHeight - 20;

            context.drawString(client.font, text, x, y, color);
        });
    }
}
