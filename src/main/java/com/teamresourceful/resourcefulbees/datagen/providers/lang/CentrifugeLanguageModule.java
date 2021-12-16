package com.teamresourceful.resourcefulbees.datagen.providers.lang;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.datagen.bases.BaseLanguageProvider;
import com.teamresourceful.resourcefulbees.datagen.bases.LanguageModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.Set;

public class CentrifugeLanguageModule extends LanguageModule {

    private static final Set<String> TYPES = Set.of("void", "terminal", "input/item", "input/energy", "output/item", "output/fluid");

    @Override
    public void addEntries(BaseLanguageProvider provider) {
        addCentrifuge(provider);
        addCentrifugeGui(provider);
    }

    private void addCentrifugeGui(BaseLanguageProvider provider) {
        Arrays.stream(CentrifugeTier.values()).forEach(tier -> {
            provider.add("gui.centrifuge.input.item." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Input");
            provider.add("gui.centrifuge.output.item." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Item Output");
            provider.add("gui.centrifuge.output.fluid." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Fluid Output");
            provider.add("gui.centrifuge.terminal." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Terminal");
            provider.add("gui.centrifuge.void." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Void");
        });
    }

    private static String formatType(String type) {
        if (!type.contains("/")) return StringUtils.capitalize(type);
        String[] splits = type.split("/");
        return WordUtils.capitalize(splits[1] + " " + splits[0]);
    }

    private void addCentrifuge(BaseLanguageProvider provider) {
        provider.addBlock(ModBlocks.CENTRIFUGE_CASING, "Centrifuge Casing");
        provider.addBlock(ModBlocks.CENTRIFUGE_PROCESSOR, "Centrifuge Processor");
        provider.addBlock(ModBlocks.CENTRIFUGE_GEARBOX, "Centrifuge Gearbox");

        for (CentrifugeTier tier : CentrifugeTier.values()) {
            for (String type : TYPES) {
                provider.add("block.resourcefulbees.centrifuge." + type.replace("/", ".") + "." + tier.getName(), StringUtils.capitalize(tier.getName()) + " Centrifuge " + formatType(type));
            }
        }
    }
}