package io.wifi.events.day_night_fight;

import io.wifi.starrailexpress.cca.SREPlayerPsychoComponent;
import io.wifi.starrailexpress.util.SREItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import org.agmas.noellesroles.init.ModItems;

public class DNFKillerPsychoItem extends Item {
    public DNFKillerPsychoItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!(user instanceof ServerPlayer player)) {
            return InteractionResultHolder.success(user.getItemInHand(hand));
        }
        if (!DNF.isNight(player)){
            return InteractionResultHolder.fail(user.getItemInHand(hand));
        }
        if (!DNF.isDNFManiac(player) && !DNF.isDNFKiller(player)) {
            player.displayClientMessage(Component.translatable("message.dnf.item.killer_only").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(user.getItemInHand(hand));
        }
        SREPlayerPsychoComponent psycho = SREPlayerPsychoComponent.KEY.get(player);
        if (psycho.getPsychoTicks() > 0) {
            psycho.stopPsychoAndSync();
            return InteractionResultHolder.success(user.getItemInHand(hand));
        }
        psycho.startPsycho_time(Integer.MAX_VALUE / 4, 10);
        psycho.sync();
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 10, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 10, false, false, false));
        world.playSound(null, player.blockPosition(), SoundEvents.WARDEN_ROAR, SoundSource.PLAYERS, 1.0f, 0.8f);
        if (world instanceof ServerLevel serverLevel) {
            double x = player.getX();
            double y = player.getY() + 1;
            double z = player.getZ();
            
            // 大幅增加粒子数量并添加多种粒子效果以营造更强烈的视觉冲击
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, x, y, z, 40, 3.0, 1.5, 3.0, 0.1);
            serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 55, 3.5, 2.0, 3.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 35, 2.5, 1.0, 2.5, 0.08);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 35, 2.0, 1.0, 2.0, 0.1);
            serverLevel.sendParticles(ParticleTypes.SCULK_CHARGE_POP, x, y, z, 35, 2.0, 1.0, 2.0, 0.1);
        }
        SREItemUtils.clearItem(player, DNFItems.ABYSS_TENTACLE);
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }
}
