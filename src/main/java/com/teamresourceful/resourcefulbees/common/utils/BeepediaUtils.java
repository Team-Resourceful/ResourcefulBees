package com.teamresourceful.resourcefulbees.common.utils;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.beedata.breeding.BeeFamily;
import com.teamresourceful.resourcefulbees.api.capabilities.IBeepediaData;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.common.item.Beepedia;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.LightLevel;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

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

    public static TranslatableComponent getSizeName(float sizeModifier) {
        if (sizeModifier < 0.75) return TranslationConstants.Sizes.TINY;
        else if (sizeModifier < 1) return TranslationConstants.Sizes.SMALL;
        else if (sizeModifier == 1) return TranslationConstants.Sizes.REGULAR;
        else if (sizeModifier <= 1.5) return TranslationConstants.Sizes.LARGE;
        else return TranslationConstants.Sizes.GIANT;
    }

    public static Component getYesNo(boolean bool) {
        return bool ? TranslationConstants.Booleans.YES : TranslationConstants.Booleans.NO;
    }

    public static TranslatableComponent getLightName(LightLevel light) {
        return light.getDisplay();
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
