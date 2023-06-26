package com.teamresourceful.resourcefulbees.client.overlay;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.items.locator.BeeLocatorItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeLocatorTranslations;
import com.teamresourceful.resourcefulbees.platform.client.renderer.overlay.OverlayRenderer;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BeeLocatorOverlay implements OverlayRenderer {

    public static final BeeLocatorOverlay INSTANCE = new BeeLocatorOverlay();

    private Entity displayBee;

    @Override
    public void render(Minecraft mc, GuiGraphics graphics, float partialTick, int width, int height) {
        Player player = mc.player;
        if (player == null) return;
        ItemStack stack = player.getMainHandItem().getItem() instanceof BeeLocatorItem ? player.getMainHandItem() : player.getOffhandItem();
        if (stack.getItem() instanceof BeeLocatorItem && hasBeeAndPos(stack)) {
            BlockPos pos = NbtUtils.readBlockPos(stack.getTag().getCompound(NBTConstants.BeeLocator.LAST_BIOME));
            pos = new BlockPos(pos.getX(), player.blockPosition().getY(), pos.getZ());
            String bee = stack.getTag().getString(NBTConstants.BeeLocator.LAST_BEE);
            ResourceLocation biome = ResourceLocation.tryParse(stack.getTag().getString(NBTConstants.BeeLocator.LAST_BIOME_ID));
            if (!BeeRegistry.get().containsBeeType(bee)) return;
            CustomBeeData data = BeeRegistry.get().getBeeData(bee);
            Entity entity = getDisplayBee(data.entityType(), player.level());
            if (entity == null) return;
            graphics.fill(0, 0, 150, 50, 1325400064);
            ClientRenderUtils.renderEntity(graphics, entity, 10, 5, 45f, 1.5f);
            graphics.drawString(mc.font, data.displayName(), 45, 5, -14829228, false);

            try (var pose = new CloseablePoseStack(graphics)) {
                pose.scale(0.75f, 0.75f, 0.75f);
                Component location = Component.translatable(BeeLocatorTranslations.LOCATION, pos.getX(), pos.getZ());
                graphics.drawString(mc.font, location, 60, 20, 0xFFFFFFFF, false);
            }

            try (var pose = new CloseablePoseStack(graphics)) {
                pose.scale(0.75f, 0.75f, 0.75f);
                Component distance = Component.translatable(BeeLocatorTranslations.DISTANCE, pos.distManhattan(player.blockPosition()));
                graphics.drawString(mc.font, distance, 60, 30, 0xFFFFFFFF, false);
            }

            if (biome != null && !biome.getPath().isEmpty()) {
                try (var pose = new CloseablePoseStack(graphics)) {
                    pose.scale(0.75f, 0.75f, 0.75f);
                    Component text = Component.translatable(BeeLocatorTranslations.BIOME, Component.translatable(String.format("biome.%s.%s", biome.getNamespace(), biome.getPath())));
                    graphics.drawString(mc.font, text, 60, 40, 0xFFFFFFFF, false);
                }
            }
        }
    }

    private Entity getDisplayBee(EntityType<?> type, Level level) {
        if (this.displayBee == null) {
            this.displayBee = type.create(level);
        } else if (this.displayBee.getType() != type) {
            this.displayBee = type.create(level);
        }
        return this.displayBee;
    }

    private static boolean hasBeeAndPos(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBTConstants.BeeLocator.LAST_BIOME, Tag.TAG_COMPOUND) && stack.getTag().contains(NBTConstants.BeeLocator.LAST_BEE, Tag.TAG_STRING);
    }
}
