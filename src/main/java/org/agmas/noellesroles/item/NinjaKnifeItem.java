package org.agmas.noellesroles.item;

import io.wifi.starrailexpress.game.GameUtils;
import io.wifi.starrailexpress.item.KnifeItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.agmas.noellesroles.Noellesroles;

/**
 * 苦无 - 忍者专属近战武器
 * 特性：左键击杀玩家，静音，使用后消失，独立冷却组
 */
public class NinjaKnifeItem extends KnifeItem {

    public NinjaKnifeItem(Properties properties) {
        super(properties);
    }

    // 禁用右键
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingUseTicks) {}

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 0;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    // 左键击杀
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(target instanceof Player victim)) return false;
        if (!(attacker instanceof ServerPlayer killer)) return false;

        GameUtils.killPlayer(victim, true, killer, Noellesroles.id("ninja_knife_kill"));
        stack.shrink(1);  // 使用后消失
        return true;
    }

    // 独立冷却组（与普通刀分开）
    @Override
    public String getCooldownGroup() {
        return "ninja_knife";
    }

    // 禁用皮肤系统
    @Override
    public String getItemSkinType() {
        return null;
    }

    @Override
    public String[] getAvailableSkins() {
        return new String[0];
    }
}