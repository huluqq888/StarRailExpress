package io.wifi.starrailexpress.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import io.wifi.starrailexpress.cca.SREPlayerSkinsComponent;
import org.agmas.noellesroles.commands.LootCommand;
import org.agmas.noellesroles.utils.lottery.LotteryManager;

import java.util.Collection;

///**
// * 金币修改指令
// */
//public class CoinModifier {
//
//    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
//        dispatcher.register(Commands.literal("SRE:coin2lottery")
//                .then(Commands.argument("chance", IntegerArgumentType.integer())
//                .executes(CoinModifier::exchange)));
//        dispatcher.register(Commands.literal("SRE:add loot coin")
//                .requires(source -> source.hasPermission(2))
//                        .then(Commands.argument("targets", EntityArgument.players())
//                                .then(Commands.argument("number", IntegerArgumentType.integer())
//                                        .executes(CoinModifier::addOrDegreeCoin)))
//        );
//    }

//    /**
//     * 执行兑换：花费 cost * chance 金币兑换 chance 次抽奖机会
//     */
//    private static int exchange(CommandContext<CommandSourceStack> context) {
//        try {
//            ServerPlayer player = context.getSource().getPlayer();
//            if (player == null) {
//                context.getSource().sendFailure(Component.translatable("commands.coin2lottery.error.no_player"));
//                return 0;
//            }
//            SREPlayerSkinsComponent skinsComponent = SREPlayerSkinsComponent.KEY.get(player);
//            int currentCoins = skinsComponent.getCoinNum();
//            int currentLootChance = skinsComponent.getLootChance();
//            int count = IntegerArgumentType.getInteger(context, "chance");
//            if (count <= 0) {
//                // 不接受 0 以下的次数
//                context.getSource().sendFailure(
//                        Component.translatable("commands.coin2lottery.error.unenble_change_the_count", count));
//                return 0;
//            }
//            // 最大抽奖次数
//            else if (count > 999) {
//                context.getSource().sendFailure(
//                        Component.translatable("commands.coin2lottery.error.unenble_change_the_count", count));
//                return 0;
//            }
//            // 价格
//            final int cost = LotteryManager.baseLootConsumeCoin * count;
//            // 检查是否有足够的金币
//            if (currentCoins < cost) {
//                context.getSource().sendFailure(
//                        Component.translatable("commands.coin2lottery.error.not_enough_coins", currentCoins, cost));
//                return 0;
//            }
//            // 扣除 cost 金币
//            skinsComponent.addCoinNum(-cost);
//            // 增加 1 次抽奖机会
//            skinsComponent.addLootChance(count);
//
//            // 同步到客户端
//            skinsComponent.sync();
//
//            // 发送成功消息
//            context.getSource().sendSuccess(
//                    () -> Component.translatable("commands.coin2lottery.success", cost, count, currentCoins - cost,
//                            currentLootChance + count),
//                    true);
//
//            return 1;
//        } catch (Exception e) {
//            context.getSource()
//                    .sendFailure(Component.translatable("commands.coin2lottery.error.failed", e.getMessage()));
//            return 0;
//        }
//    }
//    protected static int addOrDegreeCoin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
//        try {
//            int count = IntegerArgumentType.getInteger(context, "number");
//            Collection<? extends ServerPlayer> players = EntityArgument.getPlayers(context, "targets");
//            for (ServerPlayer player : players) {
//                SREPlayerSkinsComponent skinsComponent = SREPlayerSkinsComponent.KEY.get(player);
//                skinsComponent.addCoinNum(count);
//                int currentCoins = skinsComponent.getCoinNum();
//                context.getSource().sendSuccess(
//                        () -> Component.translatable("commands.coin.modifier.success.add",
//                                player.getName(), count, currentCoins),
//                        true);
//            }
//            return 1;
//        }
//        catch (Exception e) {
//            context.getSource()
//                    .sendFailure(Component.translatable("commands.coin.modifier.error.failed", e.getMessage()));
//            return 0;
//        }
//    }
//}
