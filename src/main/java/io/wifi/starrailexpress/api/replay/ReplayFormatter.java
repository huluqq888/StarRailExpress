package io.wifi.starrailexpress.api.replay;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public final class ReplayFormatter {
    private static final int DEFAULT_SCREEN_MAX_LINES = 18;

    private final GameReplayManager manager;

    public ReplayFormatter(GameReplayManager manager) {
        this.manager = manager;
    }

    public Component formatChat(GameReplayData replayData, boolean includeHidden) {
        return manager.generateReplayFromData(replayData, includeHidden);
    }

    public Component formatScreen(GameReplayData replayData, List<ReplayTimelineEvent> events, int maxLines) {
        MutableComponent text = Component.empty();
        text.append(Component.translatable("sre.replay.header").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
                .append("\n");
        if (replayData.getWinningTeam() != null) {
            text.append(Component.translatable("sre.replay.winning_team", replayData.getWinningTitle())
                    .withStyle(ChatFormatting.WHITE)).append("\n");
        }
        text.append(Component.literal("---").withStyle(ChatFormatting.GRAY)).append("\n");

        int limit = maxLines <= 0 ? DEFAULT_SCREEN_MAX_LINES : maxLines;
        int shown = 0;
        int hiddenByLimit = 0;
        for (ReplayTimelineEvent event : events) {
            if (event.hidden()) {
                continue;
            }
            if (shown >= limit) {
                hiddenByLimit++;
                continue;
            }
            text.append(Component.literal(ReplayDisplayUtils.formatTime(event.relativeTimestamp())).withStyle(ChatFormatting.GRAY))
                    .append(" ")
                    .append(event.text())
                    .append("\n");
            shown++;
        }
        if (hiddenByLimit > 0) {
            text.append(Component.literal("... +" + hiddenByLimit).withStyle(ChatFormatting.DARK_GRAY));
        }
        return text;
    }
}
