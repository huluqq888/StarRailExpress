package org.agmas.noellesroles.item;

import io.wifi.starrailexpress.game.GameUtils;
import io.wifi.starrailexpress.item.RevolverItem;
import io.wifi.starrailexpress.network.original.GunShootPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.agmas.noellesroles.Noellesroles;

/**
 * 手里剑 - 忍者专属远程武器
 * 特性：右键射击，射程10格，静音，使用后消失
 */
public class NinjaShurikenItem extends RevolverItem {

    private static final float SHURIKEN_RANGE = 10.0F;

    public NinjaShurikenItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isSpectator()) return InteractionResultHolder.fail(stack);

        // 客户端：发送网络包（静音，无粒子）
        if (level.isClientSide) {
            HitResult collision = getShurikenTarget(player);
            if (collision instanceof EntityHitResult entityHitResult) {
                ClientPlayNetworking.send(new GunShootPayload(entityHitResult.getEntity().getId()));
            } else {
                ClientPlayNetworking.send(new GunShootPayload(-1));
            }
        }

        // 服务端：处理击杀
        if (!level.isClientSide && player instanceof ServerPlayer shooter) {
            HitResult collision = getShurikenTarget(player);
            if (collision instanceof EntityHitResult entityHitResult) {
                Entity target = entityHitResult.getEntity();
                if (target instanceof Player victim) {
                    GameUtils.killPlayer(victim, true, shooter, Noellesroles.id("shuriken_kill"));
                    stack.shrink(1);  // 使用后消失
                    return InteractionResultHolder.consume(stack);
                }
            }
        }

        return InteractionResultHolder.consume(stack);
    }

    public static HitResult getShurikenTarget(Player user) {
        return ProjectileUtil.getHitResultOnViewVector(user,
                entity -> entity instanceof Player player && GameUtils.isPlayerAliveAndSurvival(player),
                SHURIKEN_RANGE);
    }

    @Override
    public boolean canBeDepleted() {
        return false;  // 无耐久度
    }

    @Override
    public String getItemSkinType() {
        return null;  // 禁用皮肤系统
    }

    @Override
    public String[] getAvailableSkins() {
        return new String[0];
    }
}