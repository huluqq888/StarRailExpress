package io.wifi.starrailexpress.client.fourthroom;

import io.wifi.starrailexpress.fourthroom.block.FourthRoomTableBlock;
import io.wifi.starrailexpress.fourthroom.block.FourthRoomTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public final class FourthRoomTableHud {

    private FourthRoomTableHud() {
    }

    public static void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.player == null || minecraft.level == null) {
            return;
        }
        FourthRoomClientSnapshot snapshot = FourthRoomClientState.snapshot();
        FourthRoomTableBlockEntity table = FourthRoomCameraDirector.getLookedTable(minecraft);
        if (table == null || table.linkedRoomId() < 0 || !snapshot.active()) {
            return;
        }
        if (snapshot.viewer().roomId() != table.linkedRoomId()) {
            return;
        }

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        Font font = minecraft.font;
        List<FourthRoomClientSnapshot.CardView> hand = snapshot.viewer().hand();
        int selectedIndex = selectedHandIndex(minecraft, hand.size());
        FourthRoomClientSnapshot.CardView selectedCard = selectedIndex >= 0 ? hand.get(selectedIndex) : null;

        int panelWidth = Math.min(screenWidth - 44, 980);
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = screenHeight - 108;
        int panelHeight = 92;

        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA0B1016);
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + 2, 0xFFB9893B);
        guiGraphics.fill(panelX, panelY + 24, panelX + panelWidth, panelY + 25, 0x302B3E52);

        Component title = Component.literal(snapshot.viewer().yourTurn() ? "轮到你操作牌桌" : "四号房牌桌");
        Component status = Component.literal(snapshot.phaseDisplayName() + " · 当前行动者 "
                + (snapshot.activePlayerName().isBlank() ? "未定" : snapshot.activePlayerName()));
        guiGraphics.drawString(font, title, panelX + 10, panelY + 7, 0xFFF3E6C8, false);
        guiGraphics.drawString(font, status, panelX + 10, panelY + 15, 0xFF8BC8FF, false);

        String hint = buildHint(snapshot, table, selectedCard, lookedZone(minecraft));
        guiGraphics.drawString(font, font.plainSubstrByWidth(hint, panelWidth - 20), panelX + 10, panelY + 31,
                0xFFD9D9D9, false);

        if (selectedCard != null) {
            String selectedTitle = "当前手牌 [" + (selectedIndex + 1) + "] " + selectedCard.displayName();
            guiGraphics.drawString(font, font.plainSubstrByWidth(selectedTitle, panelWidth - 20), panelX + 10,
                    panelY + 43, selectedCard.gold() ? 0xFFF3D27A : 0xFFECE8DF, false);
            guiGraphics.drawString(font, font.plainSubstrByWidth(selectedCard.description(), panelWidth - 20),
                    panelX + 10, panelY + 53, 0xFFBFC7D1, false);
        } else if (hand.isEmpty()) {
            guiGraphics.drawString(font, "当前没有手牌", panelX + 10, panelY + 43, 0xFFBFC7D1, false);
        } else {
            guiGraphics.drawString(font, "滚动物品栏切换到有手牌的槽位", panelX + 10, panelY + 43, 0xFFBFC7D1, false);
        }

        renderHand(guiGraphics, font, hand, selectedIndex, panelX + 10, panelY + 64, panelWidth - 20);
    }

    private static void renderHand(GuiGraphics guiGraphics, Font font, List<FourthRoomClientSnapshot.CardView> hand,
                                   int selectedIndex, int left, int top, int width) {
        if (hand.isEmpty()) {
            return;
        }
        int gap = 6;
        int cardWidth = Math.max(42, Math.min(90, (width - gap * Math.max(0, hand.size() - 1)) / hand.size()));
        int totalWidth = hand.size() * cardWidth + gap * Math.max(0, hand.size() - 1);
        int startX = left + Math.max(0, (width - totalWidth) / 2);
        for (int index = 0; index < hand.size(); index++) {
            FourthRoomClientSnapshot.CardView card = hand.get(index);
            int x = startX + index * (cardWidth + gap);
            boolean selected = index == selectedIndex;
            int fill = selected
                    ? (card.gold() ? 0xE0A47721 : 0xE04A5E2D)
                    : card.gold() ? 0xB45A431C : card.skill() ? 0xB0194F52 : 0xB01A2430;
            int border = selected ? 0xFFF6D58B : card.requiresTarget() ? 0xFFBE7C66 : 0xFF546172;
            guiGraphics.fill(x, top, x + cardWidth, top + 26, fill);
            guiGraphics.fill(x, top, x + cardWidth, top + 1, border);
            guiGraphics.fill(x, top + 25, x + cardWidth, top + 26, border);
            guiGraphics.fill(x, top, x + 1, top + 26, border);
            guiGraphics.fill(x + cardWidth - 1, top, x + cardWidth, top + 26, border);
            String prefix = "[" + (index + 1) + "] ";
            guiGraphics.drawString(font, font.plainSubstrByWidth(prefix + card.displayName(), cardWidth - 8),
                    x + 4, top + 5, 0xFFF2EEE8, false);
            String tag = card.requiresTarget() ? "目标" : card.skill() ? "技能" : card.gold() ? "金卡" : "普通";
            guiGraphics.drawString(font, font.plainSubstrByWidth(tag, cardWidth - 8), x + 4, top + 15,
                    selected ? 0xFFF6E3B2 : 0xFFB8C2CD, false);
        }
    }

    private static String buildHint(FourthRoomClientSnapshot snapshot, FourthRoomTableBlockEntity table,
                                    FourthRoomClientSnapshot.CardView selectedCard,
                                    FourthRoomTableBlock.InteractionZone lookedZone) {
        StringBuilder builder = new StringBuilder();
        builder.append("滚动物品栏切换手牌");
        if (!snapshot.viewer().alive()) {
            builder.append(" · 你已出局，当前只能旁观");
            return builder.toString();
        }
        if (selectedCard != null) {
            if (selectedCard.requiresTarget()) {
                builder.append(" · 右键目标身份牌打出当前手牌");
            } else if (selectedCard.skill() || snapshot.viewer().yourTurn()) {
                builder.append(" · 右键桌面中央打出当前手牌");
            } else {
                builder.append(" · 当前手牌需等到自己回合");
            }
        }
        if (snapshot.viewer().canEndTurn()) {
            builder.append(lookedZone == FourthRoomTableBlock.InteractionZone.DRAW_PILE
                    ? " · 松手右键结束回合"
                    : " · 右键牌库结束回合");
        }
        if (snapshot.viewer().canReveal()) {
            builder.append(" · 潜行右键自己的身份牌翻开");
        }
        if (minecraftScreenHint(table, snapshot)) {
            builder.append(" · H 可打开补充面板");
        }
        return builder.toString();
    }

    private static boolean minecraftScreenHint(FourthRoomTableBlockEntity table, FourthRoomClientSnapshot snapshot) {
        return table.linkedRoomId() == snapshot.viewer().roomId() && snapshot.active();
    }

    private static FourthRoomTableBlock.InteractionZone lookedZone(Minecraft minecraft) {
        if (!(minecraft.hitResult instanceof BlockHitResult hitResult) || hitResult.getType() != HitResult.Type.BLOCK) {
            return FourthRoomTableBlock.InteractionZone.NONE;
        }
        if (minecraft.level == null) {
            return FourthRoomTableBlock.InteractionZone.NONE;
        }
        var state = minecraft.level.getBlockState(hitResult.getBlockPos());
        if (!(state.getBlock() instanceof FourthRoomTableBlock)) {
            return FourthRoomTableBlock.InteractionZone.NONE;
        }
        return FourthRoomTableBlock.resolveInteractionZone(state, hitResult.getBlockPos(), hitResult);
    }

    private static int selectedHandIndex(Minecraft minecraft, int handSize) {
        if (minecraft.player == null || handSize <= 0) {
            return -1;
        }
        int selectedSlot = minecraft.player.getInventory().selected;
        return selectedSlot >= 0 && selectedSlot < handSize ? selectedSlot : -1;
    }
}