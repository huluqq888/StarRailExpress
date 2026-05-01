package io.wifi.events.day_night_fight;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DNFRepairToolItem extends Item {
    public DNFRepairToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        DNFItems.appendDnfTooltip(stack, context, tooltip, flag, "item.starrailexpress.dnf_repair_tool.tooltip");
    }
}
