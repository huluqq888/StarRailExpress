package io.wifi.events.day_night_fight.block;

import io.wifi.events.day_night_fight.DNFItems;
import io.wifi.starrailexpress.SRE;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 洗衣机方块
 * 玩家使用肥皂右键洗衣机可以清洗衣物
 */
public class WashingMachineBlock extends Block {
    public WashingMachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        // 检查玩家是否手持肥皂
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.is(DNFItems.SOAP)) {
            player.displayClientMessage(Component.translatable("message.dnf.washing_machine.need_soap")
                    .withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.PASS;
        }

        // 使用现有的洗衣方法
        if (DNFItems.washClothes(serverPlayer)) {
            // 消耗肥皂耐久度
            if (!serverPlayer.isCreative()) {
                mainHand.hurtAndBreak(1, serverPlayer, serverPlayer.getEquipmentSlotForItem(mainHand));
            }

            // 播放音效
            world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 0.5f, 1.2f);
            
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
