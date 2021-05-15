package com.teamresourceful.resourcefulbees.client.render.items;

import com.teamresourceful.resourcefulbees.item.BeeJar;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ItemModelPropertiesHandler {

    private ItemModelPropertiesHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void registerProperties() {
        ItemProperties.register(ModItems.BEE_JAR.get(), new ResourceLocation("filled"),
                (itemStack, clientWorld, livingEntity) -> itemStack.getItem() instanceof BeeJar && BeeJar.isFilled(itemStack) ? 1.0F : 0.0F);
    }

}
