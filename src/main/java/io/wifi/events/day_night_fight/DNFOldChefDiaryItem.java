package io.wifi.events.day_night_fight;

import io.wifi.events.day_night_fight.cca.DNFPlayerComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class DNFOldChefDiaryItem extends Item {
    public DNFOldChefDiaryItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (world.isClientSide || !(user instanceof ServerPlayer player)) {
            return InteractionResultHolder.pass(stack);
        }
        if (!DNF.isDayNightFightMode(world)) {
            return InteractionResultHolder.pass(stack);
        }
        if (DNF.isDNFChef(player)) {
            DNFPlayerComponent.KEY.get(player).readChefDiary(player);
        } else {
            player.displayClientMessage(Component.translatable("message.dnf.chef.diary_read")
                    .withStyle(ChatFormatting.DARK_GREEN), false);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        DNFItems.appendDnfTooltip(stack, context, tooltip, flag,
                "item.starrailexpress.dnf_old_chef_diary.tooltip");
    }
}
