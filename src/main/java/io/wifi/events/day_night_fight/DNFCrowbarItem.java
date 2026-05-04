package io.wifi.events.day_night_fight;

import io.wifi.starrailexpress.content.block_entity.DoorBlockEntity;
import io.wifi.events.day_night_fight.cca.DNFPlayerComponent;
import io.wifi.starrailexpress.content.item.CrowbarItem;
import io.wifi.starrailexpress.index.TMMSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class DNFCrowbarItem extends CrowbarItem {
    public DNFCrowbarItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (!DNF.isDNFKiller(player)) {
            player.displayClientMessage(Component.translatable("message.dnf.item.killer_only")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        if (!DNF.isNight(player)) {
            player.displayClientMessage(Component.translatable("message.dnf.killer.night_only")
                    .withStyle(ChatFormatting.DARK_RED), true);
            return InteractionResult.FAIL;
        }

        BlockEntity entity = world.getBlockEntity(context.getClickedPos());
        if (!(entity instanceof DoorBlockEntity)) {
            entity = world.getBlockEntity(context.getClickedPos().below());
        }
        if (!(entity instanceof DoorBlockEntity door) || door.isBlasted()) {
            return InteractionResult.PASS;
        }

        if (!world.isClientSide) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer
                    && !DNFPlayerComponent.KEY.get(serverPlayer).tryUseCrowbar(serverPlayer)) {
                return InteractionResult.FAIL;
            }
            world.playSound(null, context.getClickedPos(), TMMSounds.ITEM_CROWBAR_PRY, SoundSource.BLOCKS, 2.5f, .75f);
            player.swing(InteractionHand.MAIN_HAND, true);
            door.blast();
            if (!player.isCreative()) {
                player.getCooldowns().addCooldown(this, DNF.CROWBAR_COOLDOWN_TICKS);
            }
            player.displayClientMessage(Component.translatable("message.dnf.crowbar.blasted")
                    .withStyle(ChatFormatting.DARK_RED), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        DNFItems.appendDnfTooltip(stack, context, tooltip, flag, "item.starrailexpress.dnf_crowbar.tooltip");
    }
}
