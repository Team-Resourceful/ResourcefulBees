package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeItems;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public final class CentrifugeInfo {
    private CentrifugeInfo() {
        throw new UtilityClassError();
    }

    public static void register(@NotNull IRecipeRegistration registration) {
        CentrifugeInfo.registerCasingInfo(registration);
        CentrifugeInfo.registerGearboxInfo(registration);
        CentrifugeInfo.registerProcessorInfo(registration);
        CentrifugeInfo.registerTerminalInfo(registration);
        CentrifugeInfo.registerEnergyInputInfo(registration);
        CentrifugeInfo.registerInputInfo(registration);
        CentrifugeInfo.registerItemOutputInfo(registration);
        CentrifugeInfo.registerFluidOutputInfo(registration);
        CentrifugeInfo.registerVoidInfo(registration);
    }

    public static void registerCasingInfo(@NotNull IRecipeRegistration registration) {
        registration.addIngredientInfo(CentrifugeItems.CENTRIFUGE_CASING.get().getDefaultInstance(), VanillaTypes.ITEM_STACK, CentrifugeTranslations.CASING_JEI_INFO);
    }

    public static void registerGearboxInfo(@NotNull IRecipeRegistration registration) {
        registration.addIngredientInfo(CentrifugeItems.CENTRIFUGE_GEARBOX.get().getDefaultInstance(), VanillaTypes.ITEM_STACK, CentrifugeTranslations.GEARBOX_JEI_INFO);
    }

    public static void registerProcessorInfo(@NotNull IRecipeRegistration registration) {
        registration.addIngredientInfo(CentrifugeItems.CENTRIFUGE_PROCESSOR.get().getDefaultInstance(), VanillaTypes.ITEM_STACK, CentrifugeTranslations.PROCESSOR_JEI_INFO);
    }

    public static void registerEnergyInputInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> energyInputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_ENERGY_PORT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_ENERGY_PORT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_ENERGY_PORT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get().getDefaultInstance()
        );

        registration.addIngredientInfo(energyInputs, VanillaTypes.ITEM_STACK, CentrifugeTranslations.ENERGY_INPUT_JEI_INFO);
    }

    public static void registerInputInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> inputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_INPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_INPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_INPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_INPUT.get().getDefaultInstance()
        );

        registration.addIngredientInfo(inputs, VanillaTypes.ITEM_STACK, CentrifugeTranslations.ITEM_INPUT_JEI_INFO);
    }

    public static void registerItemOutputInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> itemOutputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_ITEM_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_ITEM_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_ITEM_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_ITEM_OUTPUT.get().getDefaultInstance()
        );
        registerOutputInfo(registration, itemOutputs, CentrifugeOutputType.ITEM);
    }

    public static void registerFluidOutputInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> itemOutputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_FLUID_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_FLUID_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_FLUID_OUTPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_FLUID_OUTPUT.get().getDefaultInstance()
        );
        registerOutputInfo(registration, itemOutputs, CentrifugeOutputType.FLUID);
    }

    private static void registerOutputInfo(@NotNull IRecipeRegistration registration, List<ItemStack> outputs, CentrifugeOutputType outputType) {
        String formattedOutput = WordUtils.capitalizeFully(outputType.name());
        registration.addIngredientInfo(outputs, VanillaTypes.ITEM_STACK, Component.translatable(CentrifugeTranslations.OUTPUT_JEI_INFO, formattedOutput, formattedOutput, formattedOutput.toLowerCase(Locale.ROOT)));
    }

    public static void registerTerminalInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> terminals = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_TERMINAL.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_TERMINAL.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_TERMINAL.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_TERMINAL.get().getDefaultInstance()
        );

        registration.addIngredientInfo(terminals, VanillaTypes.ITEM_STACK, CentrifugeTranslations.TERMINAL_JEI_INFO);
    }

    public static void registerVoidInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> dumps = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_VOID.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_VOID.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_VOID.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_VOID.get().getDefaultInstance()
        );

        registration.addIngredientInfo(dumps, VanillaTypes.ITEM_STACK, CentrifugeTranslations.VOID_JEI_INFO);
    }
}
