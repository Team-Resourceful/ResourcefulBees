package com.teamresourceful.resourcefulbees.utils;

import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.EntityMutation;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.ItemMutation;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.item.Beepedia;
import com.teamresourceful.resourcefulbees.lib.enums.LightLevel;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

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

    public static TranslatableComponent getLightName(LightLevel light) {
        switch (light) {
            case DAY:
                return new TranslatableComponent("gui.resourcefulbees.light.day");
            case NIGHT:
                return new TranslatableComponent("gui.resourcefulbees.light.night");
            default:
                return new TranslatableComponent("gui.resourcefulbees.light.any");
        }
    }

    public static List<EntityMutation> getMutationsContaining(CustomBeeData beeData) {
        List<EntityMutation> mutations = new LinkedList<>();
        BeeRegistry.getRegistry().getBees().forEach((s, bee) ->  bee.getMutationData().getEntityMutations().forEach((entityType, randomCollection) ->  {
            if (entityType.getRegistryName() != null && entityType.getRegistryName().equals(beeData.getRegistryID())) {
                mutations.add(new EntityMutation(BeeInfoUtils.getEntityType(beeData.getRegistryID()), entityType, randomCollection, bee.getMutationData().getMutationCount()));
            } else {
                randomCollection.forEach(entityOutput -> {
                    if (entityOutput.getEntityType().getRegistryName() != null && entityOutput.getEntityType().getRegistryName().equals(beeData.getRegistryID())) {
                        mutations.add(new EntityMutation(beeData.getEntityType(), entityType, randomCollection, bee.getMutationData().getMutationCount()));
                    }
                });
            }
        }));
        return mutations;
    }

    public static List<ItemMutation> getItemMutationsContaining(CustomBeeData beeData) {
        List<ItemMutation> mutations = new LinkedList<>();
        BeeRegistry.getRegistry().getBees().forEach((s, beeData1) ->   //THIS MAY BE BROKE AND NEED FIXING!
                beeData1.getMutationData().getItemMutations().forEach((block, randomCollection) ->  randomCollection.forEach(itemOutput -> {
                    if (itemOutput.getItem() == BeeSpawnEggItem.byId(beeData.getEntityType())) {
                        mutations.add(new ItemMutation(BeeInfoUtils.getEntityType(beeData1.getRegistryID()), block, randomCollection, beeData1.getMutationData().getMutationCount()));
                    }
                }))
        );
        return mutations;
    }

    public static List<BeeFamily> getChildren(CustomBeeData parent) {
        return BeeRegistry.getRegistry().getFamilyTree().entrySet().stream()
                .filter(mapEntry -> parentMatchesBee(mapEntry.getKey(), parent))
                .flatMap(entry -> entry.getValue().getMap().values().stream())
                .collect(Collectors.toList());
    }

    private static boolean parentMatchesBee(Pair<String, String> parents, CustomBeeData beeData) {
        return parents.getRight().equals(beeData.getCoreData().getName()) || parents.getLeft().equals(beeData.getCoreData().getName());
    }
}
