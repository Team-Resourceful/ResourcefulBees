package com.teamresourceful.resourcefulbees.client.overlay;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.rendering.OverlayRenderer;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.components.BeeLocatorData;
import com.teamresourceful.resourcefulbees.common.items.locator.BeeLocatorItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeLocatorTranslations;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BeeLocatorOverlay implements OverlayRenderer {

    public static final BeeLocatorOverlay INSTANCE = new BeeLocatorOverlay();

    private static final int PREVIEW_ENTITY_ID = -1001;

    private Entity displayBee;

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker partialTick) {
        Minecraft mc = Minecraft.getInstance();

        Player player = mc.player;
        if (player == null) {
            return;
        }

        ItemStack stack = getLocator(player);
        if (stack.isEmpty()) {
            return;
        }

        BeeLocatorData locatorData = stack.get(ModDataComponents.BEE_LOCATOR_DATA.get());

        if (locatorData == null) {
            return;
        }

        BeeRegistry beeRegistry = BeeRegistry.get();

        if (!beeRegistry.containsBeeType(locatorData.bee())) {
            return;
        }

        CustomBeeData beeData = beeRegistry.getBeeData(locatorData.bee());
        Entity entity = getDisplayBee(beeData.entityType(), player.level());

        if (entity == null) {
            return;
        }

        BlockPos targetPos = locatorData.position();
        graphics.fill(0, 0, 150, 50, 1325400064);
        ClientRenderUtils.renderEntity(graphics, entity, 5, 5, 35, 40, -135f, .75f);
        graphics.text(mc.font, beeData.displayName(), 45, 5, -14829228, false);
        drawScaledText(graphics, mc, Component.translatable(BeeLocatorTranslations.LOCATION, targetPos.getX(), targetPos.getZ()), 60, 20);

        if (locatorData.dimension().equals(player.level().dimension())) {
            BlockPos horizontalTarget = new BlockPos(targetPos.getX(), player.blockPosition().getY(), targetPos.getZ());

            drawScaledText(graphics, mc, Component.translatable(BeeLocatorTranslations.DISTANCE, horizontalTarget.distManhattan(player.blockPosition())), 60, 30);
        } else {
            drawScaledText(graphics, mc, Component.translatable(BeeLocatorTranslations.DIMENSION, getDimensionName(locatorData.dimension().identifier())), 60, 30);
        }

        Identifier biome = locatorData.biome();
        Component biomeName = Component.translatable("biome.%s.%s".formatted(biome.getNamespace(), biome.getPath()));
        drawScaledText(graphics, mc, Component.translatable(BeeLocatorTranslations.BIOME, biomeName), 60, 40);
    }

    private static ItemStack getLocator(Player player) {
        ItemStack mainHand = player.getMainHandItem();

        if (mainHand.getItem() instanceof BeeLocatorItem) {
            return mainHand;
        }

        ItemStack offHand = player.getOffhandItem();

        if (offHand.getItem() instanceof BeeLocatorItem) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }

    private Entity getDisplayBee(EntityType<?> type, Level level) {
        if (displayBee == null || displayBee.getType() != type) {
            displayBee = type.create(level, EntitySpawnReason.COMMAND);

            if (displayBee != null) {
                displayBee.setId(PREVIEW_ENTITY_ID);
            }
        }

        return displayBee;
    }

    private static Component getDimensionName(Identifier dimension) {
        return Component.translatable("dimension.%s.%s".formatted(dimension.getNamespace(), dimension.getPath()));
    }

    private static void drawScaledText(GuiGraphicsExtractor graphics, Minecraft mc, Component text, int x, int y) {
        graphics.pose().pushMatrix();

        try {
            graphics.pose().scale(0.75f, 0.75f);
            graphics.text(mc.font, text, x, y, 0xFFFFFFFF, false);
        } finally {
            graphics.pose().popMatrix();
        }
    }
}