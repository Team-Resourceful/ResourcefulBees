package com.resourcefulbees.resourcefulbees.client.render.items;

import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;

public class ItemModelPropertiesHandler {

    public static void registerProperties() {
        ItemModelsProperties.func_239418_a_(RegistryHandler.BEE_JAR.get(), new ResourceLocation("filled"),
                (itemStack, p_239422_1_, livingEntity) -> itemStack.getItem() instanceof BeeJar && BeeJar.isFilled(itemStack) ? 1.0F : 0.0F);
    }

}
