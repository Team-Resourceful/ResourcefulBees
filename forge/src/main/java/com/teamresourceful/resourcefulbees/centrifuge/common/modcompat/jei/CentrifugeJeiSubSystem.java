package com.teamresourceful.resourcefulbees.centrifuge.common.modcompat.jei;

import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeInputScreen;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeVoidScreen;
import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeItems;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.modcompat.jei.CentrifugeCategory;
import com.teamresourceful.resourcefulbees.common.subsystems.JeiSubsystem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class CentrifugeJeiSubSystem implements JeiSubsystem {

    @Override
    public void addRecipeCatalysts(RecipeCatalystsEvent event) {
        event.add(CentrifugeItems.CENTRIFUGE_BASIC_INPUT.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        event.add(CentrifugeItems.CENTRIFUGE_ADVANCED_INPUT.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        event.add(CentrifugeItems.CENTRIFUGE_ELITE_INPUT.get().getDefaultInstance(), CentrifugeCategory.RECIPE);
        event.add(CentrifugeItems.CENTRIFUGE_ULTIMATE_INPUT.get().getDefaultInstance(), CentrifugeCategory.RECIPE);

    }

    @Override
    public void addScreenHandlers(ScreenHandlersEvent event) {
        event.addGhostHandler(CentrifugeInputScreen.class, new CentrifugeInputGhostIngredientHandler<>());
        event.addGhostHandler(CentrifugeVoidScreen.class, new CentrifugeInputGhostIngredientHandler<>());
    }

    @Override
    public void addExtraInfo(ExtraInfoEvent event) {
        CentrifugeJeiSubSystem.registerCasingInfo(event);
        CentrifugeJeiSubSystem.registerGearboxInfo(event);
        CentrifugeJeiSubSystem.registerProcessorInfo(event);
        CentrifugeJeiSubSystem.registerTerminalInfo(event);
        CentrifugeJeiSubSystem.registerEnergyInputInfo(event);
        CentrifugeJeiSubSystem.registerInputInfo(event);
        CentrifugeJeiSubSystem.registerItemOutputInfo(event);
        CentrifugeJeiSubSystem.registerFluidOutputInfo(event);
        CentrifugeJeiSubSystem.registerVoidInfo(event);
    }

    public static void registerCasingInfo(@NotNull ExtraInfoEvent event) {
        event.addExtraInfo(CentrifugeItems.CENTRIFUGE_CASING.get().getDefaultInstance(), CentrifugeTranslations.CASING_JEI_INFO);
    }

    public static void registerGearboxInfo(@NotNull ExtraInfoEvent event) {
        event.addExtraInfo(CentrifugeItems.CENTRIFUGE_GEARBOX.get().getDefaultInstance(), CentrifugeTranslations.GEARBOX_JEI_INFO);
    }

    public static void registerProcessorInfo(@NotNull ExtraInfoEvent event) {
        event.addExtraInfo(CentrifugeItems.CENTRIFUGE_PROCESSOR.get().getDefaultInstance(), CentrifugeTranslations.PROCESSOR_JEI_INFO);
    }

    public static void registerEnergyInputInfo(@NotNull ExtraInfoEvent event) {
        List<ItemStack> energyInputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_ENERGY_PORT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_ENERGY_PORT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_ENERGY_PORT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get().getDefaultInstance()
        );

        event.addExtraInfo(energyInputs, CentrifugeTranslations.ENERGY_INPUT_JEI_INFO);
    }

    public static void registerInputInfo(@NotNull ExtraInfoEvent event) {
        List<ItemStack> inputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_INPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_INPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_INPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_INPUT.get().getDefaultInstance()
        );
        event.addExtraInfo(inputs, CentrifugeTranslations.ITEM_INPUT_JEI_INFO);
    }

    public static void registerItemOutputInfo(@NotNull ExtraInfoEvent event) {
        List<ItemStack> itemOutputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_ITEM_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_ITEM_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get().getDefaultInstance()
        );
        registerOutputInfo(event, itemOutputs, CentrifugeOutputType.ITEM);
    }

    public static void registerFluidOutputInfo(@NotNull ExtraInfoEvent event) {
        List<ItemStack> itemOutputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_FLUID_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_FLUID_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get().getDefaultInstance()
        );
        registerOutputInfo(event, itemOutputs, CentrifugeOutputType.FLUID);
    }

    private static void registerOutputInfo(@NotNull ExtraInfoEvent event, List<ItemStack> outputs, CentrifugeOutputType outputType) {
        String formattedOutput = WordUtils.capitalizeFully(outputType.name());
        event.addExtraInfo(outputs, Component.translatable(CentrifugeTranslations.OUTPUT_JEI_INFO, formattedOutput, formattedOutput, formattedOutput.toLowerCase(Locale.ROOT)));
    }

    public static void registerTerminalInfo(@NotNull ExtraInfoEvent event) {
        List<ItemStack> terminals = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_TERMINAL.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_TERMINAL.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_TERMINAL.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_TERMINAL.get().getDefaultInstance()
        );
        event.addExtraInfo(terminals, CentrifugeTranslations.TERMINAL_JEI_INFO);
    }

    public static void registerVoidInfo(@NotNull ExtraInfoEvent event) {
        List<ItemStack> dumps = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_VOID.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_VOID.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_VOID.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_VOID.get().getDefaultInstance()
        );

        event.addExtraInfo(dumps, CentrifugeTranslations.VOID_JEI_INFO);
    }
}
