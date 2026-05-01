package io.wifi.events.day_night_fight.cca;

import io.wifi.events.day_night_fight.block.CluePointBlock;
import io.wifi.starrailexpress.SRE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DNFWorldComponent implements AutoSyncedComponent {
    public static final ComponentKey<DNFWorldComponent> KEY = ComponentRegistry.getOrCreate(
            SRE.id("dnf_world"), DNFWorldComponent.class);

    private final Level world;
    private int currentDay;
    private boolean night;
    private boolean trueEndingTriggered;
    private boolean waterPoisonedToday;
    private boolean waterPoisonedTomorrow;
    @Nullable
    private UUID waterPoisonerToday;
    @Nullable
    private UUID waterPoisonerTomorrow;
    private boolean meetingActive;
    @Nullable
    private UUID votedTarget;
    @Nullable
    private BlockPos foodBoxPos;
    @Nullable
    private BlockPos waterSourcePos;
    @Nullable
    private BlockPos meteorPos;
    @Nullable
    private BlockPos wallHolePos;
    @Nullable
    private AABB cafeteriaArea;
    private final Map<UUID, AABB> dormRooms = new HashMap<>();

    // 里世界配置
    private BlockPos underworldCenter = new BlockPos(0, 64, 0);
    private double underworldRadius = 50.0;
    private final List<BlockPos> activeCluePoints = new ArrayList<>();

    public DNFWorldComponent(Level world) {
        this.world = world;
    }

    // ========== DNF游戏状态方法 ==========

    public void resetRoundState() {
        currentDay = 0;
        night = false;
        trueEndingTriggered = false;
        waterPoisonedToday = false;
        waterPoisonedTomorrow = false;
        waterPoisonerToday = null;
        waterPoisonerTomorrow = null;
        meetingActive = false;
        votedTarget = null;
        sync();
    }

    public void setPhase(int day, boolean isNight) {
        this.currentDay = Math.max(0, day);
        this.night = isNight;
        sync();
    }

    public void rollWaterPoisonToToday() {
        waterPoisonedToday = waterPoisonedTomorrow;
        waterPoisonerToday = waterPoisonerTomorrow;
        waterPoisonedTomorrow = false;
        waterPoisonerTomorrow = null;
        sync();
    }

    public void sync() {
        KEY.sync(world);
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public boolean isNight() {
        return night;
    }

    public boolean isTrueEndingTriggered() {
        return trueEndingTriggered;
    }

    public void setTrueEndingTriggered(boolean trueEndingTriggered) {
        this.trueEndingTriggered = trueEndingTriggered;
        sync();
    }

    public boolean isWaterPoisonedToday() {
        return waterPoisonedToday;
    }

    @Nullable
    public UUID getWaterPoisonerToday() {
        return waterPoisonerToday;
    }

    public void clearWaterPoison() {
        waterPoisonedToday = false;
        waterPoisonedTomorrow = false;
        waterPoisonerToday = null;
        waterPoisonerTomorrow = null;
        sync();
    }

    public void poisonWaterTomorrow(UUID poisoner) {
        waterPoisonedTomorrow = true;
        waterPoisonerTomorrow = poisoner;
        sync();
    }

    public boolean isMeetingActive() {
        return meetingActive;
    }

    public void setMeetingActive(boolean meetingActive) {
        this.meetingActive = meetingActive;
        sync();
    }

    @Nullable
    public UUID getVotedTarget() {
        return votedTarget;
    }

    public void setVotedTarget(@Nullable UUID votedTarget) {
        this.votedTarget = votedTarget;
        sync();
    }

    public void clearVote() {
        meetingActive = false;
        votedTarget = null;
        sync();
    }

    @Nullable
    public BlockPos getFoodBoxPos() {
        return foodBoxPos;
    }

    public void setFoodBoxPos(@Nullable BlockPos foodBoxPos) {
        this.foodBoxPos = foodBoxPos == null ? null : foodBoxPos.immutable();
        sync();
    }

    @Nullable
    public BlockPos getWaterSourcePos() {
        return waterSourcePos;
    }

    public void setWaterSourcePos(@Nullable BlockPos waterSourcePos) {
        this.waterSourcePos = waterSourcePos == null ? null : waterSourcePos.immutable();
        sync();
    }

    @Nullable
    public BlockPos getMeteorPos() {
        return meteorPos;
    }

    public void setMeteorPos(@Nullable BlockPos meteorPos) {
        this.meteorPos = meteorPos == null ? null : meteorPos.immutable();
        sync();
    }

    @Nullable
    public BlockPos getWallHolePos() {
        return wallHolePos;
    }

    public void setWallHolePos(@Nullable BlockPos wallHolePos) {
        this.wallHolePos = wallHolePos == null ? null : wallHolePos.immutable();
        sync();
    }

    @Nullable
    public AABB getCafeteriaArea() {
        return cafeteriaArea;
    }

    public void setCafeteriaArea(@Nullable AABB cafeteriaArea) {
        this.cafeteriaArea = cafeteriaArea;
        sync();
    }

    public void setDormRoom(UUID player, AABB room) {
        dormRooms.put(player, room);
        sync();
    }

    @Nullable
    public AABB getDormRoom(UUID player) {
        return dormRooms.get(player);
    }

    public boolean isFoodBox(BlockPos pos) {
        return foodBoxPos != null && foodBoxPos.equals(pos);
    }

    public boolean isWaterSource(BlockPos pos) {
        return waterSourcePos != null && waterSourcePos.equals(pos);
    }

    public boolean isMeteor(BlockPos pos) {
        return meteorPos != null && meteorPos.equals(pos);
    }

    public boolean isWallHole(BlockPos pos) {
        return wallHolePos != null && wallHolePos.equals(pos);
    }

    @Nullable
    public Container getFoodBoxContainer() {
        return getContainer(foodBoxPos);
    }

    @Nullable
    public Container getWallHoleContainer() {
        return getContainer(wallHolePos);
    }

    @Nullable
    private Container getContainer(@Nullable BlockPos pos) {
        if (pos == null) {
            return null;
        }
        BlockEntity entity = world.getBlockEntity(pos);
        return entity instanceof Container container ? container : null;
    }

    public boolean addToFoodBox(ItemStack stack) {
        return addToContainer(getFoodBoxContainer(), stack);
    }

    public static boolean addToContainer(@Nullable Container container, ItemStack stack) {
        if (container == null || stack.isEmpty()) {
            return false;
        }
        if (!canFit(container, stack)) {
            return false;
        }
        ItemStack remaining = stack.copy();
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack current = container.getItem(slot);
            if (!current.isEmpty() && ItemStack.isSameItemSameComponents(current, remaining)
                    && current.getCount() < current.getMaxStackSize()) {
                int move = Math.min(remaining.getCount(), current.getMaxStackSize() - current.getCount());
                current.grow(move);
                remaining.shrink(move);
                container.setChanged();
                if (remaining.isEmpty()) {
                    return true;
                }
            }
        }
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (container.getItem(slot).isEmpty()) {
                int move = Math.min(remaining.getCount(), remaining.getMaxStackSize());
                ItemStack placed = remaining.copy();
                placed.setCount(move);
                container.setItem(slot, placed);
                remaining.shrink(move);
                container.setChanged();
                if (remaining.isEmpty()) {
                    return true;
                }
            }
        }
        stack.setCount(remaining.getCount());
        return false;
    }

    public static boolean canFit(Container container, ItemStack stack) {
        int capacity = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack current = container.getItem(slot);
            if (current.isEmpty()) {
                capacity += stack.getMaxStackSize();
            } else if (ItemStack.isSameItemSameComponents(current, stack)) {
                capacity += Math.max(0, current.getMaxStackSize() - current.getCount());
            }
            if (capacity >= stack.getCount()) {
                return true;
            }
        }
        return false;
    }

    // ========== 里世界系统方法 ==========

    public void setUnderworldCenter(BlockPos center) {
        this.underworldCenter = center;
        sync();
    }

    public void setUnderworldRadius(double radius) {
        this.underworldRadius = radius;
        sync();
    }

    public BlockPos getUnderworldCenter() {
        return underworldCenter;
    }

    public double getUnderworldRadius() {
        return underworldRadius;
    }

    public BlockPos generateCluePoint(Level world) {
        double angle = Math.random() * Math.PI * 2;
        double distance = Math.random() * underworldRadius;
        
        int x = underworldCenter.getX() + (int)(Math.cos(angle) * distance);
        int z = underworldCenter.getZ() + (int)(Math.sin(angle) * distance);
        int y = underworldCenter.getY();
        
        BlockPos pos = new BlockPos(x, y, z);
        BlockPos groundPos = world.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, pos);
        
        BlockPos cluePoint = groundPos;
        activeCluePoints.add(cluePoint);
        
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.setBlock(cluePoint, CluePointBlock.cluePointBlock().defaultBlockState(), 3);
        }
        
        sync();
        return cluePoint;
    }

    public void removeCluePoint(BlockPos pos) {
        activeCluePoints.removeIf(p -> p.equals(pos));
        sync();
    }

    public List<BlockPos> getActiveCluePoints() {
        return activeCluePoints;
    }

    public void clearCluePoints() {
        activeCluePoints.clear();
        sync();
    }

    // ========== NBT序列化 ==========

    private static void putBlockPos(CompoundTag tag, String key, @Nullable BlockPos pos) {
        if (pos == null) {
            return;
        }
        CompoundTag posTag = new CompoundTag();
        posTag.putInt("X", pos.getX());
        posTag.putInt("Y", pos.getY());
        posTag.putInt("Z", pos.getZ());
        tag.put(key, posTag);
    }

    @Nullable
    private static BlockPos getBlockPos(CompoundTag tag, String key) {
        if (!tag.contains(key, CompoundTag.TAG_COMPOUND)) {
            return null;
        }
        CompoundTag posTag = tag.getCompound(key);
        return new BlockPos(posTag.getInt("X"), posTag.getInt("Y"), posTag.getInt("Z"));
    }

    private static void putBox(CompoundTag tag, String key, @Nullable AABB box) {
        if (box == null) {
            return;
        }
        CompoundTag boxTag = new CompoundTag();
        boxTag.putDouble("MinX", box.minX);
        boxTag.putDouble("MinY", box.minY);
        boxTag.putDouble("MinZ", box.minZ);
        boxTag.putDouble("MaxX", box.maxX);
        boxTag.putDouble("MaxY", box.maxY);
        boxTag.putDouble("MaxZ", box.maxZ);
        tag.put(key, boxTag);
    }

    @Nullable
    private static AABB getBox(CompoundTag tag, String key) {
        if (!tag.contains(key, CompoundTag.TAG_COMPOUND)) {
            return null;
        }
        CompoundTag boxTag = tag.getCompound(key);
        return new AABB(boxTag.getDouble("MinX"), boxTag.getDouble("MinY"), boxTag.getDouble("MinZ"),
                boxTag.getDouble("MaxX"), boxTag.getDouble("MaxY"), boxTag.getDouble("MaxZ"));
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag, HolderLookup.Provider registryLookup) {
        tag.putInt("CurrentDay", currentDay);
        tag.putBoolean("Night", night);
        tag.putBoolean("TrueEndingTriggered", trueEndingTriggered);
        tag.putBoolean("WaterPoisonedToday", waterPoisonedToday);
        tag.putBoolean("WaterPoisonedTomorrow", waterPoisonedTomorrow);
        if (waterPoisonerToday != null) {
            tag.putUUID("WaterPoisonerToday", waterPoisonerToday);
        }
        if (waterPoisonerTomorrow != null) {
            tag.putUUID("WaterPoisonerTomorrow", waterPoisonerTomorrow);
        }
        tag.putBoolean("MeetingActive", meetingActive);
        if (votedTarget != null) {
            tag.putUUID("VotedTarget", votedTarget);
        }
        putBlockPos(tag, "FoodBoxPos", foodBoxPos);
        putBlockPos(tag, "WaterSourcePos", waterSourcePos);
        putBlockPos(tag, "MeteorPos", meteorPos);
        putBlockPos(tag, "WallHolePos", wallHolePos);
        putBox(tag, "CafeteriaArea", cafeteriaArea);

        ListTag dormList = new ListTag();
        for (Map.Entry<UUID, AABB> entry : dormRooms.entrySet()) {
            CompoundTag dormTag = new CompoundTag();
            dormTag.putUUID("Player", entry.getKey());
            putBox(dormTag, "Room", entry.getValue());
            dormList.add(dormTag);
        }
        tag.put("DormRooms", dormList);

        // 里世界配置
        tag.putLong("underworld_center", underworldCenter.asLong());
        tag.putDouble("underworld_radius", underworldRadius);
        
        ListTag clueList = new ListTag();
        for (BlockPos pos : activeCluePoints) {
            clueList.add(net.minecraft.nbt.LongTag.valueOf(pos.asLong()));
        }
        tag.put("clue_points", clueList);
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag, HolderLookup.Provider registryLookup) {
        currentDay = tag.getInt("CurrentDay");
        night = tag.getBoolean("Night");
        trueEndingTriggered = tag.getBoolean("TrueEndingTriggered");
        waterPoisonedToday = tag.getBoolean("WaterPoisonedToday");
        waterPoisonedTomorrow = tag.getBoolean("WaterPoisonedTomorrow");
        waterPoisonerToday = tag.contains("WaterPoisonerToday") ? tag.getUUID("WaterPoisonerToday") : null;
        waterPoisonerTomorrow = tag.contains("WaterPoisonerTomorrow") ? tag.getUUID("WaterPoisonerTomorrow") : null;
        meetingActive = tag.getBoolean("MeetingActive");
        votedTarget = tag.contains("VotedTarget") ? tag.getUUID("VotedTarget") : null;
        foodBoxPos = getBlockPos(tag, "FoodBoxPos");
        waterSourcePos = getBlockPos(tag, "WaterSourcePos");
        meteorPos = getBlockPos(tag, "MeteorPos");
        wallHolePos = getBlockPos(tag, "WallHolePos");
        cafeteriaArea = getBox(tag, "CafeteriaArea");

        dormRooms.clear();
        ListTag dormList = tag.getList("DormRooms", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < dormList.size(); i++) {
            CompoundTag dormTag = dormList.getCompound(i);
            if (dormTag.contains("Player") && dormTag.contains("Room", CompoundTag.TAG_COMPOUND)) {
                AABB room = getBox(dormTag, "Room");
                if (room != null) {
                    dormRooms.put(dormTag.getUUID("Player"), room);
                }
            }
        }

        // 里世界配置
        if (tag.contains("underworld_center", Tag.TAG_LONG)) {
            underworldCenter = BlockPos.of(tag.getLong("underworld_center"));
        }
        if (tag.contains("underworld_radius", Tag.TAG_DOUBLE)) {
            underworldRadius = tag.getDouble("underworld_radius");
        }
        
        activeCluePoints.clear();
        if (tag.contains("clue_points", Tag.TAG_LIST)) {
            ListTag list = tag.getList("clue_points", Tag.TAG_LONG);
            for (int i = 0; i < list.size(); i++) {
                activeCluePoints.add(BlockPos.of(((net.minecraft.nbt.LongTag) list.get(i)).getAsLong()));
            }
        }
    }
}
