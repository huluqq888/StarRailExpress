package io.wifi.events.day_night_fight.block;

import com.mojang.serialization.MapCodec;
import io.wifi.events.day_night_fight.block_entity.HologramDisplayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * 全息展示方块
 * 可以放置多个物品，并以大型全息效果显示物品的描述信息
 */
public class HologramDisplayBlock extends BaseEntityBlock {
    public static final MapCodec<HologramDisplayBlock> CODEC = simpleCodec(HologramDisplayBlock::new);
    public HologramDisplayBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HologramDisplayBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof HologramDisplayBlockEntity blockEntity) {
            ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            
            // 如果手持物品，添加到展示台
            if (!heldItem.isEmpty()) {
                if (blockEntity.addItem(heldItem.copy())) {
                    // 如果不是创造模式，消耗物品
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            } else {
                // 空手点击，打开GUI或移除最后一个物品
                if (!blockEntity.getItems().isEmpty()) {
                    ItemStack removedItem = blockEntity.removeLastItem();
                    if (!removedItem.isEmpty()) {
                        player.getInventory().add(removedItem);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public @org.jspecify.annotations.Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return (level1, pos, blockState1, blockEntity) -> {
            if (blockEntity instanceof HologramDisplayBlockEntity hologramDisplayBlockEntity) {
                hologramDisplayBlockEntity.clientTick();
            }
        };
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof HologramDisplayBlockEntity blockEntity) {
                // 掉落所有物品
                for (ItemStack item : blockEntity.getItems()) {
                    popResource(level, pos, item);
                }
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }
}
