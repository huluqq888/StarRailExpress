package org.agmas.noellesroles.block_entity;

import com.mojang.math.Transformation;
import io.wifi.starrailexpress.util.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.agmas.noellesroles.init.ModBlocks;
import org.agmas.noellesroles.mini_gme.DevilRouletteGame;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevilRouletteTableEntity extends BlockEntity {
    public DevilRouletteTableEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlocks.DEVIL_ROULETTE_TABLE_ENTITY, blockPos, blockState);
        game = null;
        frontPlayer = null;
        backPlayer = null;
        Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos posOffset = new BlockPos(0, -1, 0);
        for (int i = 2; i >= -2; i -= 4) {
            for (int j = -1; j < 1; ++j) {
                // 中
                seatArea.add(worldPosition.offset(posOffset).relative(facing, i));
                // 左
                seatArea.add(worldPosition.offset(posOffset).relative(facing, i).relative(facing.getClockWise()));
                // 右
                seatArea.add(worldPosition.offset(posOffset).relative(facing, i).relative(facing.getCounterClockWise()));
            }
        }
    }
    @Override
    public void setRemoved() {
        super.setRemoved();
        for (var floatingText : floatingTexts.values())
            floatingText.discard();
        floatingTexts.clear();
    }
    /**
     * 创建悬浮文字显示
     * - 会自动根据方块坐标偏移至中心
     */
    protected void addFloatingTextInBlockPosCenter(BlockPos pos, Component text, int duration) {
        addFloatingText(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), text, duration);
    }
    /**
     * 创建悬浮文字显示
     * - 如果已存在则仅修改文本，不会重置之前的定时器
     */
    protected void addFloatingText(Vec3 pos, Component text, int duration, Vec3 scale) {
        if (level != null) {
            Display.TextDisplay displayText = null;
            if (floatingTexts.containsKey(text)) {
                displayText = floatingTexts.get(text);
            }
            else {
                displayText = new Display.TextDisplay(EntityType.TEXT_DISPLAY ,level);
                if (duration > 0)
                    Scheduler.schedule(displayText::discard, duration);
                level.addFreshEntity(displayText);
                floatingTexts.put(text, displayText);
            }
            displayText.setText(text);
            displayText.setPos(pos.x, pos.y, pos.z);
            // 设置缩放
            Matrix4f matrix = new Matrix4f().scale((float) scale.x, (float) scale.y, (float) scale.z);
            displayText.setTransformation(new Transformation(matrix));

            // 始终面向玩家
            displayText.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        }
    }
    protected void addFloatingText(Vec3 pos, Component text, int duration) {
        addFloatingText(pos, text, duration, new Vec3(1, 1, 1));
    }
    protected void removeFloatingText(Component text) {
        if (floatingTexts.containsKey(text)) {
            floatingTexts.get(text).discard();
            floatingTexts.remove(text);
        }
    }
    protected void replaceFloatingText(Component oldText, Component newText, int duration, Vec3 scale) {
        if (!floatingTexts.containsKey(oldText))
            return;
        var oldTextPos = floatingTexts.get(oldText).position();
        removeFloatingText(oldText);
        addFloatingText(oldTextPos, newText, duration, scale);
    }
    protected void replaceFloatingText(Component oldText, Component newText, int duration) {
        replaceFloatingText(oldText, newText, duration, new Vec3(1, 1, 1));
    }
    public void clientTick() {
        // 如果游戏未创建，则在两个方向显示对应的玩家名
        if (game == null) {
            return;
        }
    }
    public void startGame() {
        game = new DevilRouletteGame(frontPlayer, backPlayer);
        game.init();
        game.start();
        removeFloatingText(Component.translatable("noellesroles.game.devil_roulette.wait_start"));
        StringBuilder healthText = new StringBuilder();
        // 创建玩家生命显示
        healthText.append(frontPlayer.getName().getString()).append("\n");
        healthText.append("❤".repeat(DevilRouletteGame.START_HEALTH));
        replaceFloatingText(frontPlayer.getName(),
                Component.literal(String.valueOf(healthText)), -1, new Vec3(0.5, 0.5, 0.5));
        healthText = new StringBuilder();
        healthText.append(backPlayer.getName().getString()).append("\n");
        healthText.append("❤".repeat(DevilRouletteGame.START_HEALTH));
        replaceFloatingText(backPlayer.getName(),
                Component.literal(String.valueOf(healthText)), -1, new Vec3(0.5, 0.5, 0.5));
    }
    public boolean checkCanStartGame() {
        if (frontPlayer != null && backPlayer != null) {
            addFloatingTextInBlockPosCenter(worldPosition.offset(0,1,0), Component.translatable("noellesroles.game.devil_roulette.wait_start"), -1);
            return true;
        }
        return false;
    }
    public boolean checkPlayerInRightSeat(Player player, boolean isFront) {
        if (isFront) {
            return player == frontPlayer;
        }
        return player == backPlayer;
    }
    public boolean addPlayer(@NotNull Player player, boolean isFront) {
        BlockState state = getBlockState();
        if (isFront) {
            if (frontPlayer != null || backPlayer == player) {
                return false;
            }
            frontPlayer = player;
            BlockPos frontPos = worldPosition.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
            addFloatingTextInBlockPosCenter(frontPos, frontPlayer.getName(), -1);
        }
        else {
            if (backPlayer != null || frontPlayer == player) {
                return false;
            }
            backPlayer = player;
            BlockPos backPos = worldPosition.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());
            addFloatingTextInBlockPosCenter(backPos, backPlayer.getName(), -1);
        }
        checkCanStartGame();
        return true;
    }
    protected void removeFrontPlayer() {
        if (frontPlayer != null) {
            removeFloatingText(frontPlayer.getName());
        }
        frontPlayer = null;
    }
    protected void removeBackPlayer() {
        if (backPlayer != null) {
            removeFloatingText(backPlayer.getName());
        }
        backPlayer = null;
    }
    /** 移除同方向相同的玩家，如果成功则为true */
    public boolean removePlayerIfSame(Player player, boolean isFront) {
        if (isFront) {
            if (frontPlayer != null && frontPlayer == player) {
                removeFrontPlayer();
                return true;
            }
        }
        else {
            if (backPlayer != null && backPlayer == player) {
                removeBackPlayer();
                return true;
            }
        }
        return false;
    }
    public boolean isGameActive() {
        return game != null && !game.isGameEnd();
    }
    public boolean isSeatAvailable(BlockPos pos) {
        return seatArea.contains(pos);
    }
    public boolean isFrontSeat(BlockPos pos) {
        return seatArea.contains(pos) && seatArea.indexOf(pos) < seatArea.size() / 2;
    }
    public Direction getFacing() {
        if (this.level == null) return Direction.NORTH;
        BlockState state = this.getBlockState();

        // 如果方块有 HORIZONTAL_FACING 属性
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }

        // 如果方块有 FACING 属性（包含上下）
        if (state.hasProperty(BlockStateProperties.FACING)) {
            return state.getValue(BlockStateProperties.FACING);
        }

        return Direction.NORTH;  // 默认
    }
    public List<BlockPos> getSeatArea() {
        return seatArea;
    }
    /**
     * 悬浮文本
     */
    protected Map<Component, Display.TextDisplay> floatingTexts = new HashMap<>();
    /** 游戏可用的座位区域
     * <p>
     *     前3个是前方座位，后3个是后方座位
     * </p> */
    protected List<BlockPos> seatArea = new ArrayList<>();
    protected DevilRouletteGame game;
    /** 前方玩家 */
    protected Player frontPlayer;
    /** 后方玩家 */
    protected Player backPlayer;

}
