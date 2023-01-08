package com.teamresourceful.resourcefulbees.common.compat.jei;

import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeItems;
import com.teamresourceful.resourcefulbees.common.lib.enums.CentrifugeOutputType;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//TODO make these translatable, can probably eliminate class after doing so
public final class CentrifugeInfo {
    private CentrifugeInfo() {
        throw new UtilityClassError();
    }

    public static void registerCasingInfo(@NotNull IRecipeRegistration registration) {
        registration.addIngredientInfo(CentrifugeItems.CENTRIFUGE_CASING.get().getDefaultInstance(), VanillaTypes.ITEM_STACK,
                Component.literal("Casings are used as filler for the shell of the multiblock. They can be placed on the edges, corners, and all faces")
        );
    }

    public static void registerGearboxInfo(@NotNull IRecipeRegistration registration) {
        registration.addIngredientInfo(CentrifugeItems.CENTRIFUGE_GEARBOX.get().getDefaultInstance(), VanillaTypes.ITEM_STACK,
                Component.literal(
                        "A centrifuge can have up to 64 gearboxes. Gearboxes are optional, but when added they reduce the time a recipe takes, with each additional gearbox having a diminishing effect. The time reduction from gearboxes is divided among all inputs in the centrifuge.\n\nTime modification can be calculated as:\nrecipeTime*((1-scale)/numInputs)^numGearboxes\n\nGearboxes also increase the energy required to process a recipe.\n\nThe energy required from a gearbox can be calculated as:\n1 + (gbxModifier * (1.1^numGearboxes))\n\nThis energy value is then multiplied by the energy required from processors."
                )
        );
    }

    public static void registerProcessorInfo(@NotNull IRecipeRegistration registration) {
        registration.addIngredientInfo(CentrifugeItems.CENTRIFUGE_PROCESSOR.get().getDefaultInstance(), VanillaTypes.ITEM_STACK,
                Component.literal("A centrifuge can have up to 63 processors. Inputs can process one additional copy of a recipe each iteration for each processor added. Processors are optional, but with the maximum number of processors, an input can process a full stack of recipes in one iteration. Recipe time is not affected by processors, making them efficient at handling a large ingest of items. Processors increase the energy required to process a recipe.\n\nThe energy required from a processor can be calculated as:\n1 + (cpuModifier * (1.1^numProcessors))\n\nThis energy value is then multiplied by the energy required from gearboxes.")
        );
    }

    public static void registerEnergyInputInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> energyInputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_ENERGY_PORT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_ENERGY_PORT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_ENERGY_PORT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_ENERGY_PORT.get().getDefaultInstance()
        );

        registration.addIngredientInfo(energyInputs, VanillaTypes.ITEM_STACK,
                Component.literal("Energy Inputs can only be placed on the sides of a centrifuge. Each Energy Input adds to the total capacity of the centrifuge. The tier of the Energy Input may not exceed the tier of the Terminal")
        );
    }

    public static void registerInputInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> inputs = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_INPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_INPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_INPUT.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_INPUT.get().getDefaultInstance()
        );

        registration.addIngredientInfo(inputs, VanillaTypes.ITEM_STACK,
                Component.literal("Inputs can only be placed on the top of a centrifuge. A recipe must be specified for the Input to function by placing ghost items in the filter slot. Inputs have 3 item output slots and 3 fluid output slots. An output MUST be linked to each of these slots. Linked outputs can all be the same or different. Flexibility in output linking allows for a more dynamic and customizable centrifuge with dedicated inputs and outputs. The tier of an Input may not exceed the tier of the Terminal")
        );
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
        registration.addIngredientInfo(outputs, VanillaTypes.ITEM_STACK,
                Component.literal(formattedOutput + " Outputs can only be placed on the sides of a centrifuge. " + formattedOutput + " Outputs won't receive " + formattedOutput.toLowerCase() +"'s unless linked to an input. Flexibility in output linking allows for a more dynamic and customizable centrifuge with dedicated inputs and outputs. The contents of an output can be purged via the interface. The tier of the output may not exceed the tier of the Terminal")
        );
    }

    public static void registerTerminalInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> terminals = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_TERMINAL.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_TERMINAL.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_TERMINAL.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_TERMINAL.get().getDefaultInstance()
        );

        registration.addIngredientInfo(terminals, VanillaTypes.ITEM_STACK,
                Component.literal("Terminals can only be placed on the sides of a centrifuge. A centrifuge may have only one terminal which determines the maximum tier. The interface of a terminal allows access to the interfaces of all blocks in a centrifuge.")
        );
    }

    public static void registerVoidInfo(@NotNull IRecipeRegistration registration) {
        List<ItemStack> dumps = List.of(
                CentrifugeItems.CENTRIFUGE_BASIC_VOID.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ADVANCED_VOID.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ELITE_VOID.get().getDefaultInstance(),
                CentrifugeItems.CENTRIFUGE_ULTIMATE_VOID.get().getDefaultInstance()
        );

        registration.addIngredientInfo(dumps, VanillaTypes.ITEM_STACK,
                Component.literal("Voids can only be placed on the sides of a centrifuge. Voids are optional but when added they enable the \"Void Excess\" button on centrifuge outputs. Voids also have filter slots which can be used to void specific items or fluids from all recipe results offering more fine-tuned control. The tier of the void may not exceed the tier of the Terminal")
        );
    }
}
