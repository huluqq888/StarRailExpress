package io.wifi.events.day_night_fight;

import io.wifi.starrailexpress.content.block.SmallDoorBlock;
import io.wifi.starrailexpress.content.block_entity.SmallDoorBlockEntity;
import io.wifi.starrailexpress.index.TMMSounds;
import io.wifi.starrailexpress.util.AdventureUsable;
import io.wifi.starrailexpress.util.SREItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;

public class DNFRepairToolItem extends Item implements AdventureUsable {

    public DNFRepairToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);

        if (player == null || !DNF.isDNFLocksmith(player)) {
            return InteractionResult.PASS;
        }

        if (!SREItemUtils.hasItem(player, DNFItems.REPAIR_TOOL)) {
            if (!world.isClientSide) {
                player.displayClientMessage(Component.translatable("message.dnf.locksmith.need_tool")
                        .withStyle(ChatFormatting.YELLOW), true);
            }
            return InteractionResult.FAIL;
        }

        // 检查是否为小门方块
        if (!(state.getBlock() instanceof SmallDoorBlock)) {
            return InteractionResult.PASS;
        }

        BlockPos lowerPos = state.getValue(SmallDoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();

        if (!(world.getBlockEntity(lowerPos) instanceof SmallDoorBlockEntity door)) {
            return InteractionResult.PASS;
        }

        // 蹲下右键：只修复锁匠撬锁留下的卡住状态
        if (player.isShiftKeyDown()) {
            if (!door.isJammed()) {
                return InteractionResult.PASS;
            }
            if (!world.isClientSide) {
                door.setJammed(0);
                unJamNearBy(context);
                if (!player.isCreative()) {
                    SREItemUtils.clearItem(player, DNFItems.REPAIR_TOOL, 1);
                }
                world.playSound(null, lowerPos.getX() + 0.5, lowerPos.getY() + 1, lowerPos.getZ() + 0.5,
                        SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.2f);
                player.displayClientMessage(Component.translatable("message.dnf.locksmith.repaired")
                        .withStyle(ChatFormatting.GREEN), true);
            }
            return InteractionResult.SUCCESS;
        }

        // 普通右键：修复被撬棍破坏的门
        if (door.isBlasted()) {
            if (!world.isClientSide) {
                door.setBlasted(false);
                door.setOpen(true);
                unBlastNearBy(context);
                door.setChanged();
                if (!player.isCreative()) {
                    SREItemUtils.clearItem(player, DNFItems.REPAIR_TOOL, 1);
                }
                world.playSound(null, lowerPos.getX() + 0.5, lowerPos.getY() + 1, lowerPos.getZ() + 0.5,
                        TMMSounds.BLOCK_DOOR_TOGGLE, SoundSource.BLOCKS, 0.7f, 1.5f);
                player.displayClientMessage(Component.translatable("message.dnf.locksmith.fix_blasted")
                        .withStyle(ChatFormatting.GREEN), true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * 修复附近被撬开的门（isBlasted）
     */
    public static void unBlastNearBy(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos clickpos = context.getClickedPos();
        Vec3i[] offsets = { new Vec3i(0, 0, -1), new Vec3i(0, 0, 1), new Vec3i(-1, 0, 0), new Vec3i(1, 0, 0) };
        for (Vec3i offset : offsets) {
            BlockPos offsetPos = clickpos.offset(offset);
            BlockState state = world.getBlockState(offsetPos);
            if (state.getBlock() instanceof SmallDoorBlock) {
                BlockPos lowerPos = state.getValue(SmallDoorBlock.HALF) == DoubleBlockHalf.LOWER ? offsetPos : offsetPos.below();
                if (world.getBlockEntity(lowerPos) instanceof SmallDoorBlockEntity entity) {
                    entity.setBlasted(false);
                    entity.setOpen(true);
                    entity.setChanged();
                }
            }
        }
    }

    /**
     * 解除附近卡住的门（isJammed）
     */
    public static void unJamNearBy(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos clickpos = context.getClickedPos();
        Vec3i[] offsets = { new Vec3i(0, 0, -1), new Vec3i(0, 0, 1), new Vec3i(-1, 0, 0), new Vec3i(1, 0, 0) };
        for (Vec3i offset : offsets) {
            BlockPos offsetPos = clickpos.offset(offset);
            BlockState state = world.getBlockState(offsetPos);
            if (state.getBlock() instanceof SmallDoorBlock) {
                BlockPos lowerPos = state.getValue(SmallDoorBlock.HALF) == DoubleBlockHalf.LOWER ? offsetPos : offsetPos.below();
                if (world.getBlockEntity(lowerPos) instanceof SmallDoorBlockEntity entity) {
                    entity.setJammed(0);
                    entity.setChanged();
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        DNFItems.appendDnfTooltip(stack, context, tooltip, flag, "item.starrailexpress.dnf_repair_tool.tooltip");
    }
}
