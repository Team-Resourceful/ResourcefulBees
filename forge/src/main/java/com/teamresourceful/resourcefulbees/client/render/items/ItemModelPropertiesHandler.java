package com.teamresourceful.resourcefulbees.client.render.items;

import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.item.Beepedia;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public final class ItemModelPropertiesHandler {

    private ItemModelPropertiesHandler() {
        throw new UtilityClassError();
    }

    public static void registerProperties() {
        ItemProperties.register(ModItems.BEE_JAR.get(), new ResourceLocation("filled"), (stack, level, entity, seed) -> BeeJar.isFilled(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.BEEPEDIA.get(), new ResourceLocation("creative"), (stack, level, entity, seed) -> Beepedia.isCreative(stack) ? 1.0F : 0.0F);
    }

}
