package io.wifi.events.day_night_fight;

import io.wifi.starrailexpress.SREConfig;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public final class DNFConfig {
    private DNFConfig() {
    }

    public static BlockPos configuredMeetingPos() {
        SREConfig config = SREConfig.instance();
        return new BlockPos(config.dnfMeetingX, config.dnfMeetingY, config.dnfMeetingZ);
    }

    public static double configuredMeetingRadius() {
        return Math.max(1.0, SREConfig.instance().dnfMeetingRadius);
    }

    public static BlockPos configuredUnderworldCenter() {
        SREConfig config = SREConfig.instance();
        return new BlockPos(config.underworldLabCenterX, config.underworldLabCenterY, config.underworldLabCenterZ);
    }

    public static double configuredUnderworldRadius() {
        return Math.max(1.0, SREConfig.instance().underworldLabRadius);
    }

    public static int configuredLabTeleportOffsetY() {
        return SREConfig.instance().underworldLabTeleportOffsetY;
    }

    @Nullable
    public static BlockPos configuredFoodBoxPos() {
        SREConfig config = SREConfig.instance();
        return config.dnfUseConfiguredFoodBoxPos
                ? new BlockPos(config.dnfFoodBoxX, config.dnfFoodBoxY, config.dnfFoodBoxZ)
                : null;
    }

    @Nullable
    public static BlockPos configuredWaterSourcePos() {
        SREConfig config = SREConfig.instance();
        return config.dnfUseConfiguredWaterSourcePos
                ? new BlockPos(config.dnfWaterSourceX, config.dnfWaterSourceY, config.dnfWaterSourceZ)
                : null;
    }

    @Nullable
    public static BlockPos configuredMeteorPos() {
        SREConfig config = SREConfig.instance();
        return config.dnfUseConfiguredMeteorPos
                ? new BlockPos(config.dnfMeteorX, config.dnfMeteorY, config.dnfMeteorZ)
                : null;
    }

    @Nullable
    public static BlockPos configuredWallHolePos() {
        SREConfig config = SREConfig.instance();
        return config.dnfUseConfiguredWallHolePos
                ? new BlockPos(config.dnfWallHoleX, config.dnfWallHoleY, config.dnfWallHoleZ)
                : null;
    }

    @Nullable
    public static BlockPos configuredOldChefDiaryPos() {
        SREConfig config = SREConfig.instance();
        return config.dnfUseConfiguredOldChefDiaryPos
                ? new BlockPos(config.dnfOldChefDiaryX, config.dnfOldChefDiaryY, config.dnfOldChefDiaryZ)
                : null;
    }

    @Nullable
    public static AABB configuredCafeteriaArea() {
        SREConfig config = SREConfig.instance();
        if (!config.dnfUseConfiguredCafeteriaArea) {
            return null;
        }
        return new AABB(config.dnfCafeteriaMinX, config.dnfCafeteriaMinY, config.dnfCafeteriaMinZ,
                config.dnfCafeteriaMaxX, config.dnfCafeteriaMaxY, config.dnfCafeteriaMaxZ);
    }

    public static void saveFoodBoxPos(BlockPos pos) {
        SREConfig config = SREConfig.instance();
        config.dnfUseConfiguredFoodBoxPos = true;
        config.dnfFoodBoxX = pos.getX();
        config.dnfFoodBoxY = pos.getY();
        config.dnfFoodBoxZ = pos.getZ();
        SREConfig.HANDLER.save();
    }

    public static void saveWaterSourcePos(BlockPos pos) {
        SREConfig config = SREConfig.instance();
        config.dnfUseConfiguredWaterSourcePos = true;
        config.dnfWaterSourceX = pos.getX();
        config.dnfWaterSourceY = pos.getY();
        config.dnfWaterSourceZ = pos.getZ();
        SREConfig.HANDLER.save();
    }

    public static void saveMeteorPos(BlockPos pos) {
        SREConfig config = SREConfig.instance();
        config.dnfUseConfiguredMeteorPos = true;
        config.dnfMeteorX = pos.getX();
        config.dnfMeteorY = pos.getY();
        config.dnfMeteorZ = pos.getZ();
        SREConfig.HANDLER.save();
    }

    public static void saveWallHolePos(BlockPos pos) {
        SREConfig config = SREConfig.instance();
        config.dnfUseConfiguredWallHolePos = true;
        config.dnfWallHoleX = pos.getX();
        config.dnfWallHoleY = pos.getY();
        config.dnfWallHoleZ = pos.getZ();
        SREConfig.HANDLER.save();
    }

    public static void saveOldChefDiaryPos(BlockPos pos) {
        SREConfig config = SREConfig.instance();
        config.dnfUseConfiguredOldChefDiaryPos = true;
        config.dnfOldChefDiaryX = pos.getX();
        config.dnfOldChefDiaryY = pos.getY();
        config.dnfOldChefDiaryZ = pos.getZ();
        SREConfig.HANDLER.save();
    }

    public static void saveMeetingPos(BlockPos pos) {
        SREConfig config = SREConfig.instance();
        config.dnfMeetingX = pos.getX();
        config.dnfMeetingY = pos.getY();
        config.dnfMeetingZ = pos.getZ();
        SREConfig.HANDLER.save();
    }

    public static void saveMeetingRadius(double radius) {
        SREConfig.instance().dnfMeetingRadius = Math.max(1.0, radius);
        SREConfig.HANDLER.save();
    }

    public static void saveUnderworldCenter(BlockPos pos) {
        SREConfig config = SREConfig.instance();
        config.underworldLabCenterX = pos.getX();
        config.underworldLabCenterY = pos.getY();
        config.underworldLabCenterZ = pos.getZ();
        SREConfig.HANDLER.save();
        DNF.applyUnderworldConfig();
    }

    public static void saveUnderworldRadius(double radius) {
        SREConfig.instance().underworldLabRadius = Math.max(1.0, radius);
        SREConfig.HANDLER.save();
        DNF.applyUnderworldConfig();
    }

    public static void saveCafeteriaArea(AABB area) {
        SREConfig config = SREConfig.instance();
        config.dnfUseConfiguredCafeteriaArea = true;
        config.dnfCafeteriaMinX = area.minX;
        config.dnfCafeteriaMinY = area.minY;
        config.dnfCafeteriaMinZ = area.minZ;
        config.dnfCafeteriaMaxX = area.maxX;
        config.dnfCafeteriaMaxY = area.maxY;
        config.dnfCafeteriaMaxZ = area.maxZ;
        SREConfig.HANDLER.save();
    }
}
