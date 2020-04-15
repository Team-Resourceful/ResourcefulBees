package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.data.provider.ModBlockstateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class DataGen {
    private DataGen(){}

    public static void gatherData(GatherDataEvent event){
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeClient()){
            gen.addProvider(new ModBlockstateProvider(gen,helper));
            //gen.addProvider(new ModItemModelProvider(gen,helper));
            //gen.addProvider(new ModLanguageProvider(gen));
        }
        /*
        if (event.includeServer()){
            gen.addProvider(new ModLootTableProvider(gen));
            gen.addProvider(new ModRecipeProvider(gen));
            gen.addProvider(new ModItemTagsProvider(gen));
        }*/
    }

}
