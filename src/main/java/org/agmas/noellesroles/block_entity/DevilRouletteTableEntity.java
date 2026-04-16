package org.agmas.noellesroles.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.agmas.noellesroles.init.ModBlocks;

public class DevilRouletteTableEntity extends BlockEntity {
    public DevilRouletteTableEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlocks.DEVIL_ROULETTE_TABLE_ENTITY, blockPos, blockState);
    }
    public void clientTick() {
    }
}
