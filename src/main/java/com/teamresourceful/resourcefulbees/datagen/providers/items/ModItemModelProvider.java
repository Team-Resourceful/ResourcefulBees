package com.teamresourceful.resourcefulbees.datagen.providers.items;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends BaseItemModelProvider {

    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.WAX.get());
        basicItem(ModItems.T1_NEST_UPGRADE.get());
        basicItem(ModItems.T2_NEST_UPGRADE.get());
        basicItem(ModItems.T3_NEST_UPGRADE.get());
        basicItem(ModItems.SMOKER.get());
        basicItem(ModItems.SMOKERCAN.get());
        basicItem(ModItems.BELLOW.get());
        basicItem(ModItems.OREO_COOKIE.get());
        basicItem(ModItems.WAXED_DOOR.get());
        basicItem(ModItems.WAXED_SIGN.get());
    }
}
