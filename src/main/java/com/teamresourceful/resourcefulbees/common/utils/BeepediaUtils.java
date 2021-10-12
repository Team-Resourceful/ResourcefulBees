package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.EntityMutation;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.ItemMutation;
import com.teamresourceful.resourcefulbees.api.capabilities.IBeepediaData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.common.item.BeeSpawnEggItem;
import com.teamresourceful.resourcefulbees.common.item.Beepedia;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.LightLevel;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class BeepediaUtils {

    private BeepediaUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void loadBeepedia(ItemStack itemstack, boolean hasShades, LazyOptional<IBeepediaData> data) {
        boolean complete = false;
        if (itemstack.hasTag() && itemstack.getTag() != null && !itemstack.isEmpty()) {
            complete = itemstack.getTag().getBoolean(Beepedia.COMPLETE_TAG) || itemstack.getTag().getBoolean(Beepedia.CREATIVE_TAG);
        }
        Minecraft.getInstance().setScreen(new BeepediaScreen(complete, hasShades, data));
    }

    public static TranslationTextComponent getSizeName(float sizeModifier) {
        if (sizeModifier < 0.75) {
            return new TranslationTextComponent("bees.resourcefulbees.size.tiny");
        } else if (sizeModifier < 1) {
            return new TranslationTextComponent("bees.resourcefulbees.size.small");
        } else if (sizeModifier == 1) {
            return new TranslationTextComponent("bees.resourcefulbees.size.regular");
        } else if (sizeModifier <= 1.5) {
            return new TranslationTextComponent("bees.resourcefulbees.size.large");
        } else {
            return new TranslationTextComponent("bees.resourcefulbees.size.giant");
        }
    }

    public static ITextComponent getYesNo(boolean bool) {
        return bool ? new TranslationTextComponent("gui.resourcefulbees.yes") : new TranslationTextComponent("gui.resourcefulbees.no");
    }

    public static TranslationTextComponent getLightName(LightLevel light) {
        switch (light) {
            case DAY:
                return new TranslationTextComponent("gui.resourcefulbees.light.day");
            case NIGHT:
                return new TranslationTextComponent("gui.resourcefulbees.light.night");
            default:
                return new TranslationTextComponent("gui.resourcefulbees.light.any");
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
