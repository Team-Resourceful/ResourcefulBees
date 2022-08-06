package com.teamresourceful.resourcefulbees.client.gui.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.utils.RenderUtils;
import com.teamresourceful.resourcefulbees.common.item.BeeLocator;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BeeLocatorOverlay implements IGuiOverlay {

    public static final BeeLocatorOverlay INSTANCE = new BeeLocatorOverlay();

    private Entity displayBee;

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        Player player = gui.getMinecraft().player;
        if (player == null) return;
        ItemStack stack = player.getMainHandItem().getItem() instanceof BeeLocator ? player.getMainHandItem() : player.getOffhandItem();
        if (stack.getItem() instanceof BeeLocator && hasBeeAndPos(stack)) {
            BlockPos pos = NbtUtils.readBlockPos(stack.getTag().getCompound(NBTConstants.BeeLocator.LAST_BIOME));
            pos = new BlockPos(pos.getX(), player.blockPosition().getY(), pos.getZ());
            String bee = stack.getTag().getString(NBTConstants.BeeLocator.LAST_BEE);
            if (!BeeRegistry.containsBeeType(bee)) return;
            CustomBeeData data = BeeRegistry.getRegistry().getBeeData(bee);
            Entity entity = getDisplayBee(data.getEntityType(), player.level);
            if (entity == null) return;
            GuiComponent.fill(poseStack, 0, 0, 150, 50, 1325400064);
            RenderUtils.renderEntity(poseStack, entity, 10, 5, 45f, 1.5f);
            gui.getFont().draw(poseStack, data.displayName(), 45, 5, -14829228);

            poseStack.pushPose();
            poseStack.scale(0.75f, 0.75f, 0.75f);
            Component location = Component.translatable(TranslationConstants.BeeLocator.LOCATION, pos.getX(), pos.getZ());
            gui.getFont().draw(poseStack, location, 60, 20, 0xFFFFFFFF);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.scale(0.75f, 0.75f, 0.75f);
            Component distance = Component.translatable(TranslationConstants.BeeLocator.DISTANCE, pos.distManhattan(player.blockPosition()));
            gui.getFont().draw(poseStack, distance, 60, 30, 0xFFFFFFFF);
            poseStack.popPose();
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
