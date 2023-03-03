package com.teamresourceful.resourcefulbees.datagen.providers.items;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends BaseItemModelProvider {

    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems.WAX.get());
        basicItem(ModItems.T2_NEST_UPGRADE.get());
        basicItem(ModItems.T3_NEST_UPGRADE.get());
        basicItem(ModItems.T4_NEST_UPGRADE.get());
        basicItem(ModItems.SMOKER.get());
        basicItem(ModItems.SMOKERCAN.get());
        basicItem(ModItems.BELLOW.get());
        basicItem(com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems.OREO_COOKIE.get());
        basicItem(ModItems.WAXED_DOOR.get());
        basicItem(ModItems.WAXED_SIGN.get());
        basicItem(ModItems.ENERGY_CAP_UPGRADE.get());
        basicItem(ModItems.ENERGY_XFER_UPGRADE.get());
        basicItem(ModItems.ENERGY_FILL_UPGRADE.get());
        basicItem(ModItems.HONEY_CAP_UPGRADE.get());
        var flower = ModItems.GOLD_FLOWER_ITEM.getId();
        getBuilder(flower.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(flower.getNamespace(), "block/" + flower.getPath()));
    }
}
