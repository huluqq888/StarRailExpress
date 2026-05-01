package io.wifi.events.day_night_fight.client.renderer;

import io.wifi.events.day_night_fight.entity.UnderworldMonsterEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 里世界怪物渲染器
 * 完全透明,不渲染实体模型,只通过粒子效果展示位置
 */
@Environment(EnvType.CLIENT)
public class UnderworldMonsterRenderer extends EntityRenderer<UnderworldMonsterEntity> {
    public UnderworldMonsterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(UnderworldMonsterEntity entity) {
        return null; // 不需要纹理
    }

    /**
     * 不渲染实体模型
     */
    @Override
    public boolean shouldRender(UnderworldMonsterEntity livingEntity, net.minecraft.client.renderer.culling.Frustum camera, double camX, double camY, double camZ) {
        return false; // 不渲染实体
    }
}
