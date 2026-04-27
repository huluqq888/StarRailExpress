package io.wifi.events.day_night_fight;

import io.wifi.starrailexpress.util.AdventureUsable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class DNFLockpickItem extends Item implements AdventureUsable {
    public DNFLockpickItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return DNFItems.tryOpenWithLockpick(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        DNFItems.appendDnfTooltip(stack, context, tooltip, flag, "item.starrailexpress.dnf_lockpick.tooltip");
    }
}
