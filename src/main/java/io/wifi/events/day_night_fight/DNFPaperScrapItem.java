package io.wifi.events.day_night_fight;

import io.wifi.starrailexpress.content.item.NoteItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class DNFPaperScrapItem extends NoteItem {
    public DNFPaperScrapItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) {
            return InteractionResult.PASS;
        }
        if (context.getPlayer() != null && !DNF.isNight(context.getPlayer())) {
            context.getPlayer().displayClientMessage(Component.translatable("message.dnf.paper.night_only")
                    .withStyle(ChatFormatting.GRAY), true);
            return InteractionResult.FAIL;
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        tooltip.add(Component.translatable("item.starrailexpress.dnf_paper_scrap.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }
}
