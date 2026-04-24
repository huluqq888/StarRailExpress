package io.wifi.starrailexpress.content.block.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public interface TaskInstinctShowableInterface {
    @Environment(EnvType.CLIENT)

    /**
     * 仅客户端：是否渲染
     * 
     * @return
     */
    boolean shouldRenderTaskInstinct(BlockState state, BlockPos pos, Player player);

    @Environment(EnvType.CLIENT)

    /**
     * 仅客户端：渲染颜色
     * 
     * @return
     */
    java.awt.Color taskInstinctRenderColor(BlockState state, BlockPos pos, Player player);

    /**
     * 需要13+。可不改
     * 因为fork新增的猫咪任务占用了12，所以13开始
     * 
     * @return
     */
    public default int taskInstinctId(){
        return 13;
    }
}
