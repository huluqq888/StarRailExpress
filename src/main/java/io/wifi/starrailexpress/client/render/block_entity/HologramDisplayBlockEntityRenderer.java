package io.wifi.starrailexpress.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.wifi.events.day_night_fight.block_entity.HologramDisplayBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 全息展示方块渲染器
 * 显示大型全息物品和描述文字
 */
public class HologramDisplayBlockEntityRenderer implements BlockEntityRenderer<HologramDisplayBlockEntity> {
    
    private final ItemRenderer itemRenderer;
    private final Font font;
    
    // 全息效果参数
    private static final float HOLOGRAM_SCALE = 2.0f; // 全息物品放大倍数
    private static final float FLOAT_HEIGHT = 2.5f; // 悬浮高度（向上偏移更多）
    private static final float ROTATION_SPEED = 1.0f; // 旋转速度
    private static final int TEXT_COLOR = 0x00FFFF; // 青色全息文字颜色
    private static final float ALPHA_BASE = 0.8f; // 基础透明度
    private static final float ITEM_SPACING = 0.8f; // 物品之间的间距

    public HologramDisplayBlockEntityRenderer(@NotNull BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.font = context.getFont();
    }

    @Override
    public void render(@NotNull HologramDisplayBlockEntity entity, float tickDelta, @NotNull PoseStack poseStack,
                      @NotNull MultiBufferSource bufferSource, int light, int overlay) {
        
        List<ItemStack> items = entity.getItems();
        if (items.isEmpty()) {
            return;
        }

        // 渲染所有物品
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            if (item.isEmpty()) continue;
            
            poseStack.pushPose();
            
            // 计算每个物品的位置（水平排列）
            float xOffset = (i - (items.size() - 1) / 2.0f) * ITEM_SPACING;
            poseStack.translate(0.5 + xOffset, FLOAT_HEIGHT, 0.5);
            
            // 添加上下浮动动画（每个物品略有不同）
            float floatOffset = (float) Math.sin((entity.getLevel().getGameTime() + tickDelta) * 0.05 + i * 0.5) * 0.1f;
            poseStack.translate(0, floatOffset, 0);
            
            // 旋转动画
            float rotation = (entity.getLevel().getGameTime() + tickDelta) * ROTATION_SPEED;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
            
            // 缩放为全息效果
            poseStack.scale(HOLOGRAM_SCALE, HOLOGRAM_SCALE, HOLOGRAM_SCALE);
            
            // 渲染物品（带发光效果）
            renderHolographicItem(item, poseStack, bufferSource, light, overlay);
            
            poseStack.popPose();
        }
        
        // 渲染第一个物品的描述文字
        if (!items.get(0).isEmpty()) {
            renderItemDescription(items.get(0), entity, poseStack, bufferSource, tickDelta, light);
        }
    }

    /**
     * 渲染全息物品（带发光和透明效果）
     */
    private void renderHolographicItem(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource,
                                      int light, int overlay) {
        // 保存当前状态
        poseStack.pushPose();
        
        // 添加轻微脉冲效果
        float pulse = 1.0f + (float) Math.sin(Minecraft.getInstance().level.getGameTime() * 0.1) * 0.05f;
        poseStack.scale(pulse, pulse, pulse);
        
        // 渲染物品
        itemRenderer.renderStatic(
            stack,
            net.minecraft.world.item.ItemDisplayContext.FIXED,
            light,
            overlay,
            poseStack,
            bufferSource,
            Minecraft.getInstance().level,
            0
        );
        
        poseStack.popPose();
    }

    /**
     * 渲染物品描述文字（全息效果）
     */
    private void renderItemDescription(ItemStack stack, HologramDisplayBlockEntity entity,
                                      PoseStack poseStack, MultiBufferSource bufferSource, float tickDelta,int light) {
        
        // 获取物品的显示名称和描述
        Component itemName = stack.getHoverName();
        List<Component> tooltips = stack.getTooltipLines(
                Item.TooltipContext.EMPTY,
            Minecraft.getInstance().player,
            TooltipFlag.NORMAL
        );
        
        if (tooltips.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        
        // 设置文字位置（在物品上方）
        poseStack.translate(0.5, FLOAT_HEIGHT + 1.5f, 0.5);
        
        // 文字始终面向玩家
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        
        // 计算缩放（让文字更大更易读）
        float textScale = 0.025f;
        poseStack.scale(textScale, -textScale, textScale); // Y轴翻转让文字正向显示
        
        // 渲染物品名称（大字体，居中）
        String nameText = itemName.getString();
        if (nameText == null || nameText.isEmpty()) {
            nameText = stack.getItem().getDescription().getString();
        }
        int nameWidth = font.width(nameText);
        
        // 添加闪烁效果（确保alpha在合理范围内）
        float alpha = ALPHA_BASE + (float) Math.sin((entity.getLevel().getGameTime() + tickDelta) * 0.1) * 0.1f;
        alpha = Math.max(0.5f, Math.min(1.0f, alpha)); // 限制alpha在0.5-1.0之间
        int colorWithAlpha = ((int) (alpha * 255) << 24) | TEXT_COLOR;
        
        // 渲染名称（带阴影）- 修正Y坐标为正值
        font.drawInBatch(
            nameText,
            -nameWidth / 2.0f,
            0,  // 修正：从0开始，向上为负
            colorWithAlpha,
            false,  // 修正：不使用阴影，避免渲染问题
            poseStack.last().pose(),
            bufferSource,
            Font.DisplayMode.NORMAL,
            0,
            light
        );
        
        // 渲染描述文字（多行）
        int lineHeight = 12;  // 增加行高
        int startY = 12;  // 从名称下方开始
        int maxLines = 3; // 最多显示3行描述
        
        int displayedLines = 0;
        for (int i = 1; i < tooltips.size() && displayedLines < maxLines; i++) {
            Component tooltip = tooltips.get(i);
            String tooltipText = tooltip.getString();
            
            if (tooltipText == null || tooltipText.isEmpty()) {
                continue;
            }
            
            int tooltipWidth = font.width(tooltipText);
            int lineColor = ((int) (alpha * 220) << 24) | 0xAAAAAA; // 灰色描述文字，提高不透明度
            
            font.drawInBatch(
                tooltipText,
                -tooltipWidth / 2.0f,
                startY + displayedLines * lineHeight,
                lineColor,
                false,  // 修正：不使用阴影
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                light
            );
            displayedLines++;
        }
        
        // 显示物品总数指示
        if (entity.getItemCount() > 1) {
            String countText = String.format("共 %d 个物品", entity.getItemCount());
            int countWidth = font.width(countText);
            int countColor = ((int) (alpha * 255) << 24) | 0xFFAA00; // 金色
            
            font.drawInBatch(
                countText,
                -countWidth / 2.0f,
                startY + displayedLines * lineHeight + 5,
                countColor,
                false,  // 修正：不使用阴影
                poseStack.last().pose(),
                bufferSource,
                Font.DisplayMode.NORMAL,
                0,
                light
            );
        }
        
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull HologramDisplayBlockEntity entity) {
        return true; // 允许从远处渲染
    }

    @Override
    public int getViewDistance() {
        return 64; // 增加渲染距离
    }
}
