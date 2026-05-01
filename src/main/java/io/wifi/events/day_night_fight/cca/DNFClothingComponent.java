package io.wifi.events.day_night_fight.cca;

import io.wifi.starrailexpress.SRE;
import io.wifi.starrailexpress.api.RoleComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

/**
 * 衣物系统CCA组件
 * 管理玩家衣物的耐久度和肮脏程度
 */
public class DNFClothingComponent implements RoleComponent {
    public static final ComponentKey<DNFClothingComponent> KEY = ComponentRegistry.getOrCreate(
            SRE.id("dnf_clothing"), DNFClothingComponent.class);

    private final Player player;
    
    // 衣物耐久度 (0-100)
    private int clothingDurability = 100;
    
    // 是否穿着衣物
    private boolean hasClothing = true;

    public DNFClothingComponent(Player player) {
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void init() {
        clothingDurability = 100;
        hasClothing = true;
        sync();
    }

    @Override
    public void clear() {
        clothingDurability = 100;
        hasClothing = false;
    }

    @Override
    public void writeToSyncNbt(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        tag.putInt("clothing_durability", clothingDurability);
        tag.putBoolean("has_clothing", hasClothing);
    }

    @Override
    public void readFromSyncNbt(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        clothingDurability = tag.getInt("clothing_durability");
        hasClothing = tag.getBoolean("has_clothing");
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        tag.putInt("clothing_durability", clothingDurability);
        tag.putBoolean("has_clothing", hasClothing);
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        clothingDurability = tag.getInt("clothing_durability");
        hasClothing = tag.getBoolean("has_clothing");
    }

    public void sync() {
        if (player instanceof ServerPlayer sp) {
            KEY.sync(sp);
        }
    }

    /**
     * 每天结束时扣除耐久度
     */
    public void degradeClothing(int amount) {
        if (!hasClothing) return;
        
        clothingDurability = Math.max(10, clothingDurability - amount); // 最低保持10,不会完全破碎
        sync();
    }

    /**
     * 使用洗衣机恢复耐久度
     */
    public void restoreClothing(int amount) {
        if (!hasClothing) return;
        
        clothingDurability = Math.min(100, clothingDurability + amount);
        sync();
    }

    /**
     * 获取肮脏程度百分比 (0-100, 100表示最脏)
     */
    public int getDirtinessPercent() {
        if (!hasClothing) return 100;
        return 100 - clothingDurability;
    }

    /**
     * 获取衣物耐久度
     */
    public int getClothingDurability() {
        return clothingDurability;
    }

    /**
     * 是否穿着衣物
     */
    public boolean hasClothing() {
        return hasClothing;
    }

    /**
     * 根据衣物状态计算SAN值惩罚
     * @return SAN值惩罚百分比 (0-100)
     */
    public int getSanPenalty() {
        if (!hasClothing) {
            return 80; // 没穿: -80%
        }
        
        int dirtiness = getDirtinessPercent();
        if (dirtiness >= 70) {
            return 60; // 肮脏的: -60%
        } else if (dirtiness >= 40) {
            return 30; // 中等: -30%
        } else {
            return 0; // 干净: 无惩罚
        }
    }

    /**
     * 设置是否穿着衣物
     */
    public void setHasClothing(boolean hasClothing) {
        this.hasClothing = hasClothing;
        sync();
    }
}
