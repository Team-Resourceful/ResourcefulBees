package com.teamresourceful.resourcefulbees.utils;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.item.Beepedia;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.lib.NBTConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
}
