package io.wifi.starrailexpress.api.replay.screen;

import io.wifi.starrailexpress.api.replay.GameReplayManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix4f;
import com.mojang.math.Transformation;

import java.util.Optional;
import java.util.UUID;

public final class ReplayScreenService {
    private ReplayScreenService() {
    }

    public static ReplayScreenSavedData.ReplayScreenEntry createScreen(ServerLevel level, String id, BlockPos origin,
            int width, int height, Direction direction) {
        Direction horizontal = normalize(direction);
        ReplayScreenSavedData.ReplayScreenEntry entry = new ReplayScreenSavedData.ReplayScreenEntry(id,
                level.dimension(), origin.immutable(), width, height, horizontal, null);
        buildBackground(level, entry);
        ReplayScreenSavedData.get(level).putScreen(entry, false);
        return entry;
    }

    public static boolean removeScreen(ServerLevel level, String id) {
        ReplayScreenSavedData data = ReplayScreenSavedData.get(level);
        Optional<ReplayScreenSavedData.ReplayScreenEntry> removed = data.removeScreen(id);
        removed.ifPresent(entry -> {
            ServerLevel screenLevel = level.getServer().getLevel(entry.dimension());
            if (screenLevel != null) {
                clearTextDisplay(screenLevel, entry);
            }
        });
        return removed.isPresent();
    }

    public static boolean showDefault(ServerLevel currentLevel, GameReplayManager manager) {
        ReplayScreenSavedData data = ReplayScreenSavedData.get(currentLevel);
        return data.getDefaultScreen().map(entry -> show(currentLevel, entry, manager)).orElse(false);
    }

    public static boolean show(ServerLevel currentLevel, String id, GameReplayManager manager) {
        ReplayScreenSavedData data = ReplayScreenSavedData.get(currentLevel);
        return data.getScreen(id).map(entry -> show(currentLevel, entry, manager)).orElse(false);
    }

    public static boolean show(ServerLevel currentLevel, ReplayScreenSavedData.ReplayScreenEntry entry,
            GameReplayManager manager) {
        ServerLevel level = currentLevel.getServer().getLevel(entry.dimension());
        if (level == null) {
            return false;
        }
        buildBackground(level, entry);
        clearTextDisplay(level, entry);
        Component text = manager.generateScreenReplay(Math.max(6, entry.height() - 2));
        Display.TextDisplay display = new ReplayTextDisplay(EntityType.TEXT_DISPLAY, level);
        display.setText(text);
        display.setNoGravity(true);
        display.setBillboardConstraints(Display.BillboardConstraints.FIXED);
        display.setYRot(yawFor(entry.direction()));
        display.setXRot(0.0F);
        display.setTransformation(new Transformation(new Matrix4f().scale(textScale(entry))));
        BlockPos origin = entry.origin();
        double x = origin.getX() + 0.5D;
        double y = origin.getY() + Math.max(1.0D, entry.height() / 2.0D);
        double z = origin.getZ() + 0.5D;
        if (entry.direction().getAxis() == Direction.Axis.Z) {
            x += (entry.width() - 1) / 2.0D;
            z += entry.direction().getStepZ() * 0.58D;
        } else {
            x += entry.direction().getStepX() * 0.58D;
            z += (entry.width() - 1) / 2.0D;
        }
        display.moveTo(x, y, z, yawFor(entry.direction()), 0.0F);
        display.setCustomName(Component.literal("SRE Replay Screen: " + entry.id()).withStyle(ChatFormatting.GRAY));
        display.setCustomNameVisible(false);
        level.addFreshEntity(display);
        ReplayScreenSavedData.get(level).updateLastTextDisplay(entry.id(), display.getUUID());
        return true;
    }

    public static void buildBackground(ServerLevel level, ReplayScreenSavedData.ReplayScreenEntry entry) {
        BlockPos origin = entry.origin();
        for (int w = 0; w < entry.width(); w++) {
            for (int h = 0; h < entry.height(); h++) {
                BlockPos pos = backgroundPos(origin, entry.direction(), w, h);
                level.setBlock(pos, Blocks.BLACK_WOOL.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private static BlockPos backgroundPos(BlockPos origin, Direction direction, int widthOffset, int heightOffset) {
        if (direction.getAxis() == Direction.Axis.Z) {
            return origin.offset(widthOffset, heightOffset, 0);
        }
        return origin.offset(0, heightOffset, widthOffset);
    }

    private static void clearTextDisplay(ServerLevel level, ReplayScreenSavedData.ReplayScreenEntry entry) {
        UUID entityId = entry.lastTextDisplay();
        if (entityId == null) {
            return;
        }
        Entity entity = level.getEntity(entityId);
        if (entity != null) {
            entity.discard();
        }
        ReplayScreenSavedData.get(level).updateLastTextDisplay(entry.id(), null);
    }

    private static Direction normalize(Direction direction) {
        if (direction == null || direction.getAxis().isVertical()) {
            return Direction.NORTH;
        }
        return direction;
    }

    private static float yawFor(Direction direction) {
        return switch (normalize(direction)) {
            case NORTH -> 180.0F;
            case SOUTH -> 0.0F;
            case WEST -> 90.0F;
            case EAST -> -90.0F;
            default -> 180.0F;
        };
    }

    private static float textScale(ReplayScreenSavedData.ReplayScreenEntry entry) {
        return Math.max(0.35F, Math.min(1.25F, entry.width() / 8.0F));
    }

    private static final class ReplayTextDisplay extends Display.TextDisplay {
        private ReplayTextDisplay(EntityType<?> entityType, Level level) {
            super(entityType, level);
        }
    }
}
