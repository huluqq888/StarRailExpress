package io.wifi.events.day_night_fight.entity;

import io.wifi.events.day_night_fight.clue.ClueSystem;
import io.wifi.events.day_night_fight.cca.SREPlayerClueComponent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.agmas.noellesroles.init.ModEffects;

import java.util.List;
import java.util.UUID;

public class ClueEntity extends Entity {
    private static final int INTERACTION_RADIUS = 4;
    
    private UUID clueUuid;
    private String clueTitle;
    private String clueContent;
    private long clueCreatedAt;
    private Display.TextDisplay textDisplay;

    public ClueEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.clueUuid = UUID.randomUUID();
        this.clueTitle = "";
        this.clueContent = "";
        this.clueCreatedAt = System.currentTimeMillis();
    }

    public void setClueData(UUID uuid, String title, String content, long createdAt) {
        this.clueUuid = uuid;
        this.clueTitle = title;
        this.clueContent = content;
        this.clueCreatedAt = createdAt;
    }

    public UUID getClueUuid() {
        return clueUuid;
    }

    public String getClueTitle() {
        return clueTitle;
    }

    public String getClueContent() {
        return clueContent;
    }

    public long getClueCreatedAt() {
        return clueCreatedAt;
    }

    @Override
    public void tick() {
        super.tick();
        this.noPhysics = true;
        this.setDeltaMovement(Vec3.ZERO);
        level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 1, 0);

        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            checkNearbyPlayers(serverLevel);
        }
    }

    private void checkNearbyPlayers(ServerLevel level) {
        AABB searchArea = this.getBoundingBox().inflate(INTERACTION_RADIUS);
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, searchArea);

        for (ServerPlayer player : nearbyPlayers) {
            // 检查玩家是否有幽灵状态效果
            if (player.hasEffect(ModEffects.GHOST_STATE)) {
                SREPlayerClueComponent clueComponent = SREPlayerClueComponent.KEY.get(player);
                
                // 检查玩家是否已经解锁过这个线索
                boolean alreadyHasClue = clueComponent.clues.stream()
                        .anyMatch(clue -> clue.clueEntityUuid().equals(clueUuid));
                
                if (!alreadyHasClue) {
                    SREPlayerClueComponent.ClueEntry entry = new SREPlayerClueComponent.ClueEntry(
                            clueUuid, clueTitle, clueContent, clueCreatedAt);
                    ClueSystem.recordClue(player, entry);
                }
            }
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("ClueUuid")) {
            clueUuid = tag.getUUID("ClueUuid");
        }
        if (tag.contains("ClueTitle")) {
            clueTitle = tag.getString("ClueTitle");
        }
        if (tag.contains("ClueContent")) {
            clueContent = tag.getString("ClueContent");
        }
        if (tag.contains("ClueCreatedAt")) {
            clueCreatedAt = tag.getLong("ClueCreatedAt");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putUUID("ClueUuid", clueUuid);
        tag.putString("ClueTitle", clueTitle);
        tag.putString("ClueContent", clueContent);
        tag.putLong("ClueCreatedAt", clueCreatedAt);
    }
}
