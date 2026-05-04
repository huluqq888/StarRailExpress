package io.wifi.events.day_night_fight.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.wifi.events.day_night_fight.DNFGameMode;
import io.wifi.starrailexpress.cca.SREGameWorldComponent;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class TimeSkipCommand {
    public static void register() {
//        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
//                Commands.literal("tmm:dnf_a")
//
//        ));
    }

    public static int skipToNextDay(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        SREGameWorldComponent gameWorldComponent = SREGameWorldComponent.KEY.get(level);
        
        if (!(gameWorldComponent.getGameMode() instanceof DNFGameMode dnfGameMode)) {
            ctx.getSource().sendFailure(Component.translatable("command.dnf.time_skip.not_in_dnf"));
            return 0;
        }
        
        // 跳过到下一天
        dnfGameMode.skipToNextDay(level, gameWorldComponent);
        ctx.getSource().sendSuccess(() -> Component.translatable("command.dnf.time_skip.day_skipped"), true);
        return Command.SINGLE_SUCCESS;
    }

    public static int skipToNextPhase(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        SREGameWorldComponent gameWorldComponent = SREGameWorldComponent.KEY.get(level);
        
        if (!(gameWorldComponent.getGameMode() instanceof DNFGameMode dnfGameMode)) {
            ctx.getSource().sendFailure(Component.translatable("command.dnf.time_skip.not_in_dnf"));
            return 0;
        }
        
        // 跳过到下一阶段
        dnfGameMode.skipToNextPhase(level, gameWorldComponent);
        ctx.getSource().sendSuccess(() -> Component.translatable("command.dnf.time_skip.phase_skipped"), true);
        return Command.SINGLE_SUCCESS;
    }
}