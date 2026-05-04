package io.wifi.events.day_night_fight.block_entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 全息展示方块实体
 * 存储展示的物品列表
 */
public class HologramDisplayBlockEntity extends BlockEntity {
    
    private static final int MAX_ITEMS = 9; // 最多展示9个物品
    
    private final List<ItemStack> items = new ArrayList<>();
    private int displayIndex = 0; // 当前显示的物品索引
    private long lastSwitchTime = 0; // 上次切换时间
    private static final long SWITCH_INTERVAL = 100; // 切换间隔（tick）

    public HologramDisplayBlockEntity(BlockPos pos, BlockState state) {
        super(io.wifi.starrailexpress.index.TMMBlockEntities.HOLOGRAM_DISPLAY, pos, state);
    }

    /**
     * 添加物品到展示台
     * @return 是否成功添加
     */
    public boolean addItem(ItemStack stack) {
        if (items.size() >= MAX_ITEMS || stack.isEmpty()) {
            return false;
        }
        items.add(stack);
        setChanged();
        sync();
        return true;
    }

    /**
     * 移除最后一个物品
     */
    public ItemStack removeLastItem() {
        if (items.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = items.remove(items.size() - 1);
        if (displayIndex >= items.size() && !items.isEmpty()) {
            displayIndex = items.size() - 1;
        }
        setChanged();
        sync();
        return removed;
    }


    /**
     * 获取所有物品
     */
    public List<ItemStack> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * 获取当前显示的物品
     */
    public ItemStack getCurrentDisplayItem() {
        if (items.isEmpty()) {
            return ItemStack.EMPTY;
        }
        updateDisplayIndex();
        return items.get(displayIndex);
    }

    /**
     * 更新显示索引（轮播效果）
     */
    private void updateDisplayIndex() {
        if (items.isEmpty()) {
            return;
        }
        
        long currentTime = level != null ? level.getGameTime() : 0;
        if (currentTime - lastSwitchTime >= SWITCH_INTERVAL) {
            displayIndex = (displayIndex + 1) % items.size();
            lastSwitchTime = currentTime;
        }
    }

    /**
     * 获取物品数量
     */
    public int getItemCount() {
        return items.size();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        
        ListTag itemsTag = new ListTag();
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.put("item", stack.save(registries));
                itemTag.putInt("index", i);
                itemsTag.add(itemTag);
            }
        }
        tag.put("items", itemsTag);
        tag.putInt("displayIndex", displayIndex);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        
        items.clear();
        if (tag.contains("items")) {
            ListTag itemsTag = tag.getList("items", CompoundTag.TAG_COMPOUND);
            for (int i = 0; i < itemsTag.size(); i++) {
                CompoundTag itemTag = itemsTag.getCompound(i);
                ItemStack stack = ItemStack.parse(registries, itemTag.getCompound("item")).orElse(ItemStack.EMPTY);
                if (!stack.isEmpty()) {
                    items.add(stack);
                }
            }
        }
        displayIndex = tag.getInt("displayIndex");
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    /**
     * 同步数据到客户端
     */
    public void sync() {
        if (level != null && !level.isClientSide) {
            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    /**
     * 客户端tick，用于动画效果
     */
    public void clientTick() {
        updateDisplayIndex();
    }
}
