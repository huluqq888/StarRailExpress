package io.wifi.starrailexpress.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.wifi.starrailexpress.cca.SREGameWorldComponent;
import io.wifi.starrailexpress.data.ServerMapConfig;
import io.wifi.starrailexpress.game.GameUtils;
import io.wifi.starrailexpress.network.ShowSelectedMapUIPayload;
import io.wifi.starrailexpress.voting.MapVotingManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class MapVoteCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("tmm:votemap")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> startVoting(context.getSource(), 60 * 20)) // 默认60秒
                        .then(Commands.argument("time",
                                IntegerArgumentType.integer(10 * 20, 300 * 20)) // 时间范围10-300秒
                                .executes(context -> startVoting(context.getSource(),
                                        IntegerArgumentType.getInteger(context,
                                                "time"))))
                        .then(Commands.literal("pause")
                                .executes(context -> pauseVoting(context.getSource())))
                        .then(Commands.literal("resume")
                                .executes(context -> resumeVoting(context.getSource())))
                        .then(Commands.literal("stop")
                                .executes(context -> stopVoting(context.getSource()))));
    }

    private static int startVoting(CommandSourceStack source, int time) {
        if (GameUtils.isStartingGame) {
            source.sendFailure(Component.literal("Game is starting! You cannot open map voting screen!"));
            return 0;
        }
        SREGameWorldComponent gameWorldComponent = SREGameWorldComponent.KEY.get(source.getLevel());
        if (gameWorldComponent.isRunning()) {
            source.sendFailure(Component.literal("Game has started! You cannot open map voting screen!"));
            return 0;
        }
        MapVotingManager votingManager = MapVotingManager.getInstance();

        if (votingManager.isVotingActive()) {
            source.sendFailure(Component.translatable("command.sre.votemap.already_running"));
            return 0;
        }

        if (votingManager.isVotingPaused()) {
            source.sendFailure(Component.literal("投票系统已暂停，无法发起新的投票！"));
            return 0;
        }

        votingManager.startVoting(time);
        String mapconfigs = ShowSelectedMapUIPayload
                .convertServerMapConfigToString(ServerMapConfig.getInstance(source.getServer()));

        source.getServer().getPlayerList().getPlayers().forEach(
                serverPlayer -> {
                    ServerPlayNetworking.send(serverPlayer,
                            new ShowSelectedMapUIPayload(mapconfigs));
                });
        source.sendSuccess(() -> Component.translatable("command.sre.votemap.success"), false);

        return 1;
    }

    private static int pauseVoting(CommandSourceStack source) {
        MapVotingManager votingManager = MapVotingManager.getInstance();

        if (!votingManager.isVotingActive()) {
            source.sendFailure(Component.literal("当前没有正在进行的投票！"));
            return 0;
        }

        if (votingManager.isVotingPaused()) {
            source.sendFailure(Component.literal("投票已经处于暂停状态！"));
            return 0;
        }

        votingManager.pauseVoting();
        source.sendSuccess(() -> Component.literal("投票已暂停"), true);
        return 1;
    }

    private static int resumeVoting(CommandSourceStack source) {
        MapVotingManager votingManager = MapVotingManager.getInstance();

        if (!votingManager.isVotingActive()) {
            source.sendFailure(Component.literal("当前没有正在进行的投票！"));
            return 0;
        }

        if (!votingManager.isVotingPaused()) {
            source.sendFailure(Component.literal("投票未处于暂停状态！"));
            return 0;
        }

        votingManager.resumeVoting();
        source.sendSuccess(() -> Component.literal("投票已恢复"), true);
        return 1;
    }

    private static int stopVoting(CommandSourceStack source) {
        MapVotingManager votingManager = MapVotingManager.getInstance();

        if (!votingManager.isVotingActive()) {
            source.sendFailure(Component.literal("当前没有正在进行的投票！"));
            return 0;
        }

        votingManager.stopVoting();
        source.sendSuccess(() -> Component.literal("投票已终止"), true);
        return 1;
    }
}