package com.teamresourceful.resourcefulbees.utils;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.item.Beepedia;
import com.teamresourceful.resourcefulbees.lib.LightLevels;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.lib.NBTConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class BeepediaUtils {

    private BeepediaUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void loadBeepedia(ItemStack itemstack, Entity entity) {
        boolean complete = false;
        List<String> bees = new LinkedList<>();
        if (itemstack.hasTag() && itemstack.getTag() != null && !itemstack.isEmpty()) {
            complete = itemstack.getTag().getBoolean(Beepedia.COMPLETE_TAG) || itemstack.getTag().getBoolean(Beepedia.CREATIVE_TAG);
            bees = getBees(itemstack.getTag());
        }
        Minecraft.getInstance().setScreen(new BeepediaScreen(entity == null ? null : ((CustomBeeEntity) entity).getBeeType(), bees, complete));
    }

    private static List<String> getBees(CompoundTag tag) {
        if (tag.contains(NBTConstants.NBT_BEES)) {
            return tag.getList(NBTConstants.NBT_BEES, 8).copy().stream().map(Tag::getAsString).collect(Collectors.toList());
        } else {
            return new LinkedList<>();
        }
    }

    public static TranslatableComponent getSizeName(float sizeModifier) {
        if (sizeModifier < 0.75) {
            return new TranslatableComponent("bees.resourcefulbees.size.tiny");
        } else if (sizeModifier < 1) {
            return new TranslatableComponent("bees.resourcefulbees.size.small");
        } else if (sizeModifier == 1) {
            return new TranslatableComponent("bees.resourcefulbees.size.regular");
        } else if (sizeModifier <= 1.5) {
            return new TranslatableComponent("bees.resourcefulbees.size.large");
        } else {
            return new TranslatableComponent("bees.resourcefulbees.size.giant");
        }
    }

    public static Component getYesNo(boolean bool) {
        return bool ? new TranslatableComponent("gui.resourcefulbees.yes") : new TranslatableComponent("gui.resourcefulbees.no");
    }

    public static TranslatableComponent getLightName(LightLevels light) {
        switch (light) {
            case DAY:
                return new TranslatableComponent("gui.resourcefulbees.light.day");
            case NIGHT:
                return new TranslatableComponent("gui.resourcefulbees.light.night");
            default:
                return new TranslatableComponent("gui.resourcefulbees.light.any");
        }
    }
}
