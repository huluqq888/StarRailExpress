package io.wifi.events.day_night_fight.block_entity;

import io.wifi.starrailexpress.content.block_entity.BeveragePlateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DNFServingPlateBlockEntity extends BeveragePlateBlockEntity {
    public static final int MAX_FOOD = 3;

    public DNFServingPlateBlockEntity(BlockPos pos, BlockState state) {
        super(DNFBlockEntities.SERVING_PLATE, pos, state);
        setDrink(false);
    }

    public boolean hasFood() {
        return !getStoredItems().isEmpty();
    }

    public boolean canAddFood() {
        return getStoredItems().size() < MAX_FOOD;
    }

    public ItemStack takeFood() {
        if (getStoredItems().isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = removeItem(0).copy();
        stack.setCount(1);
        if (getStoredItems().isEmpty()) {
            setPoisoner(null);
            setArmorer(null);
        }
        return stack;
    }
}
