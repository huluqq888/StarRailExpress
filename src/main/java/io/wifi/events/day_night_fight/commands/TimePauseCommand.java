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

public class TimePauseCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                Commands.literal("tmm:dnf")
                        .then(Commands.literal("time_pause")
                                .requires(source -> source.hasPermission(2))
                                .executes(TimePauseCommand::toggleTimePause))
                        .then(Commands.literal("time_resume")
                                .requires(source -> source.hasPermission(2))
                                .executes(TimePauseCommand::resumeTime))
                        .then(Commands.literal("time_status")
                                .requires(source -> source.hasPermission(2))
                                .executes(TimePauseCommand::checkTimeStatus))
        ));
    }

    private static int toggleTimePause(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        SREGameWorldComponent gameWorldComponent = SREGameWorldComponent.KEY.get(level);
        
        if (!(gameWorldComponent.getGameMode() instanceof DNFGameMode dnfGameMode)) {
            ctx.getSource().sendFailure(Component.translatable("command.dnf.time_pause.not_in_dnf"));
            return 0;
        }
        
        dnfGameMode.timePaused = !dnfGameMode.timePaused;
        String status = dnfGameMode.timePaused ? "paused" : "resumed";
        ctx.getSource().sendSuccess(() -> Component.translatable("command.dnf.time_pause." + status), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int resumeTime(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        SREGameWorldComponent gameWorldComponent = SREGameWorldComponent.KEY.get(level);
        
        if (!(gameWorldComponent.getGameMode() instanceof DNFGameMode dnfGameMode)) {
            ctx.getSource().sendFailure(Component.translatable("command.dnf.time_pause.not_in_dnf"));
            return 0;
        }
        
        dnfGameMode.timePaused = false;
        ctx.getSource().sendSuccess(() -> Component.translatable("command.dnf.time_pause.resumed"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int checkTimeStatus(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        SREGameWorldComponent gameWorldComponent = SREGameWorldComponent.KEY.get(level);
        
        if (!(gameWorldComponent.getGameMode() instanceof DNFGameMode dnfGameMode)) {
            ctx.getSource().sendFailure(Component.translatable("command.dnf.time_pause.not_in_dnf"));
            return 0;
        }
        
        String status = dnfGameMode.timePaused ? "paused" : "running";
        ctx.getSource().sendSuccess(() -> Component.translatable("command.dnf.time_pause.status", status), true);
        return Command.SINGLE_SUCCESS;
    }
}