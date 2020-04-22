package com.dungeonderps.resourcefulbees.data.provider;

import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.IronBeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;

public class ModBlockstateProvider extends BlockStateProvider {
    public ModBlockstateProvider(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, ResourcefulBees.MOD_ID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        ironBeehive();
    }

    protected void ironBeehive() {
        ResourceLocation iron_block = mcLoc("block/iron_block");
        Block iron_beehive = RegistryHandler.IRON_BEEHIVE.get();
        String name = iron_beehive.getRegistryName().getPath();
        String nameHoney = iron_beehive.getRegistryName().getPath() + "_honey";

        ModelFile model = models().getBuilder(name)
                .parent(models().getExistingFile(mcLoc("block/orientable_with_bottom")))
                .texture("particle", iron_block)
                .texture("bottom", iron_block)
                .texture("top", iron_block)
                .texture("front", new ResourceLocation(ResourcefulBees.MOD_ID, "block/" + name + "_front"))
                .texture("side", new ResourceLocation(ResourcefulBees.MOD_ID, "block/" + name + "_side"));

        ModelFile modelHoney = models().getBuilder(nameHoney)
                .parent(models().getExistingFile(mcLoc("block/orientable_with_bottom")))
                .texture("particle", iron_block)
                .texture("bottom", iron_block)
                .texture("top", iron_block)
                .texture("front", "block/" + name + "_front_honey")
                .texture("side", "block/" + name + "_side");

        getVariantBuilder(iron_beehive).forAllStates(state -> state.get(IronBeehiveBlock.HONEY_LEVEL) == 5 ?
                ConfiguredModel.builder().modelFile(modelHoney).build() : ConfiguredModel.builder().modelFile(model).build());
    }
}
