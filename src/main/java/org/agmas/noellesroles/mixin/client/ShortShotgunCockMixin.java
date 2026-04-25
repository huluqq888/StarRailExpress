package org.agmas.noellesroles.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.agmas.noellesroles.init.ModItems;
import org.agmas.noellesroles.packet.ShortShotgunEquipPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class ShortShotgunCockMixin {
    private ItemStack lastHeldShotgun = ItemStack.EMPTY;
    private boolean hasSentEquipPacket = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = (LocalPlayer) (Object) this;

        if (mc.level == null || player == null) {
            return;
        }

        ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        boolean isHoldingShotgun = mainHandStack.is(ModItems.SHORT_SHOTGUN);

        if (isHoldingShotgun) {
            if (!hasSentEquipPacket && (lastHeldShotgun.isEmpty() || !lastHeldShotgun.is(ModItems.SHORT_SHOTGUN))) {
                // 发送网络包给服务端，服务端播放音效让附近所有玩家都能听到
                ClientPlayNetworking.send(new ShortShotgunEquipPayload());
                hasSentEquipPacket = true;
            }
        } else {
            hasSentEquipPacket = false;
        }

        lastHeldShotgun = mainHandStack.copy();
    }
}
