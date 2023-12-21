package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class CentrifugeTranslations {

    private CentrifugeTranslations() throws UtilityClassException {
        throw new UtilityClassException();
    }


    @Translate("Close")
    public static final MutableComponent CLOSE = Component.translatable("gui.resourcefulbees.centrifuge.close");

    @Translate("Back")
    public static final MutableComponent BACK = Component.translatable("gui.resourcefulbees.centrifuge.back");

    @Translate("Help")
    public static final MutableComponent HELP = Component.translatable("gui.resourcefulbees.centrifuge.help");

    @Translate("View Block")
    public static final MutableComponent VIEW = Component.translatable("gui.resourcefulbees.centrifuge.view");

    @Translate("Location: %s")
    public static final String LOCATION = "gui.resourcefulbees.centrifuge.location";

    @Translate("Centrifuge has no Void Blocks!")
    public static final MutableComponent NO_VOID_BLOCKS = Component.translatable("gui.resourcefulbees.centrifuge.no_void_blocks");

    @Translate("Process Time Left: %s")
    public static final String TIME_LEFT = "gui.resourcefulbees.centrifuge.process_time_left";

    @Translate("Energy Usage: %s rf/t")
    public static final String ENERGY_USAGE = "gui.resourcefulbees.centrifuge.energy_usage";

    @Translate("Filter Recipe: %s")
    public static final String FILTER_RECIPE = "gui.resourcefulbees.centrifuge.recipe.filter";

    @Translate("Process Recipe: %s")
    public static final String PROCESS_RECIPE = "gui.resourcefulbees.centrifuge.recipe.process";

    @Translate("Processing Stage: %s")
    public static final String STAGE = "gui.resourcefulbees.centrifuge.process_stage";

    @Translate("%s Outputs: ")
    public static final String OUTPUTS = "gui.resourcefulbees.centrifuge.outputs";

    @Translate("  Output #:%s %s")
    public static final String OUTPUT_NUM = "gui.resourcefulbees.centrifuge.output_num";

    @Translate("Output not linked!")
    public static final MutableComponent OUTPUT_NOT_LINKED = Component.translatable("gui.resourcefulbees.centrifuge.output_not_linked");

    @Translate("Output %s")
    public static final String OUTPUT_SLOT = "gui.resourcefulbees.centrifuge.output_slot";

    @Translate("Unknown")
    public static final MutableComponent UNKNOWN = Component.translatable("gui.resourcefulbees.centrifuge.unknown");

    @Translate("Filter slot not set!")
    public static final MutableComponent FILTER_SLOT_NOT_SET = Component.translatable("gui.resourcefulbees.centrifuge.filter_slot_not_set");

    @Translate("No recipe being processed!")
    public static final MutableComponent RECIPE_NOT_PROCESSED = Component.translatable("gui.resourcefulbees.centrifuge.recipe_not_processed");

    @Translate("Item")
    public static final MutableComponent ITEM = Component.translatable("gui.resourcefulbees.centrifuge.item");

    @Translate("Fluid")
    public static final MutableComponent FLUID = Component.translatable("gui.resourcefulbees.centrifuge.fluid");

    @Translate("Please select the output location for each recipe output slot.")
    public static final MutableComponent INSTRUCTIONS = Component.translatable("gui.resourcefulbees.centrifuge.instructions");

    @Translate("Excess contents will be voided for output")
    public static final MutableComponent VOIDING_EXCESS = Component.translatable("gui.resourcefulbees.centrifuge.voiding_excess");

    @Translate("Excess contents will not be voided for output")
    public static final MutableComponent NOT_VOIDING_EXCESS = Component.translatable("gui.resourcefulbees.centrifuge.not_voiding_excess");

    @Translate("Output contents purged!")
    public static final MutableComponent CONTENTS_PURGED = Component.translatable("gui.resourcefulbees.centrifuge.contents_purged");

    @Translate("Multiblock sides only")
    public static final MutableComponent SIDES_ONLY_TOOLTIP = Component.translatable("gui.resourcefulbees.centrifuge.tooltip.sides_only");

    @Translate("Cannot be used on edges")
    public static final MutableComponent NO_EDGES_TOOLTIP = Component.translatable("gui.resourcefulbees.centrifuge.tooltip.no_edges");

    @Translate("Multiblock edges, corners, and faces only")
    public static final MutableComponent NO_INTERIOR_TOOLTIP = Component.translatable("gui.resourcefulbees.centrifuge.tooltip.no_interior");

    @Translate("Multiblock interior only")
    public static final MutableComponent INTERIOR_ONLY_TOOLTIP = Component.translatable("gui.resourcefulbees.centrifuge.tooltip.interior_only");

    @Translate("Multiblock top only")
    public static final MutableComponent TOP_ONLY_TOOLTIP = Component.translatable("gui.resourcefulbees.centrifuge.tooltip.top_only");

    @Translate("Slots: %s")
    public static final String SLOTS_TOOLTIP = "gui.resourcefulbees.centrifuge.tooltip.slots";

    @Translate("Capacity: %s rf")
    public static final String ENERGY_CAPACITY_TOOLTIP = "gui.resourcefulbees.centrifuge.tooltip.energy.capacity";

    @Translate("Receive: %s rf/t")
    public static final String ENERGY_RECEIVE_TOOLTIP = "gui.resourcefulbees.centrifuge.tooltip.energy.receive";

    @Translate("Capacity: %s mb")
    public static final String FLUID_CAPACITY = "gui.resourcefulbees.centrifuge.tooltip.fluid.capacity";

    @Translate("Limit 64 per multiblock")
    public static final MutableComponent GEARBOX_LIMIT_TOOLTIP = Component.translatable("gui.resourcefulbees.centrifuge.tooltip.gearbox.limit").withStyle(ChatFormatting.GOLD);

    @Translate("Limit 63 per multiblock")
    public static final MutableComponent PROCESSOR_LIMIT_TOOLTIP = Component.translatable("gui.resourcefulbees.centrifuge.tooltip.processor.limit").withStyle(ChatFormatting.GOLD);

    //region JEI INFO PANELS
    @Translate("Casings are used as filler for the shell of the multiblock. They can be placed on the edges, corners, and all faces")
    public static final MutableComponent CASING_JEI_INFO = Component.translatable("gui.resourcefulbees.jei.info.casing");

    @Translate("A centrifuge can have up to 64 gearboxes. Gearboxes are optional, but when added they reduce the time a recipe takes, with each additional gearbox having a diminishing effect. The time reduction from gearboxes is divided among all inputs in the centrifuge.\n\nTime modification can be calculated as:\nrecipeTime*((1-scale)/numInputs)^numGearboxes\n\nGearboxes also increase the energy required to process a recipe.\n\nThe energy required from a gearbox can be calculated as:\n1 + (gbxModifier * (1.1^numGearboxes))\n\nThis energy value is then multiplied by the energy required from processors.")
    public static final MutableComponent GEARBOX_JEI_INFO = Component.translatable("gui.resourcefulbees.jei.info.gearbox");

    @Translate("A centrifuge can have up to 63 processors. Inputs can process one additional copy of a recipe each iteration for each processor added. Processors are optional, but with the maximum number of processors, an input can process a full stack of recipes in one iteration. Recipe time is not affected by processors, making them efficient at handling a large ingest of items. Processors increase the energy required to process a recipe.\n\nThe energy required from a processor can be calculated as:\n1 + (cpuModifier * (1.1^numProcessors))\n\nThis energy value is then multiplied by the energy required from gearboxes.")
    public static final MutableComponent PROCESSOR_JEI_INFO = Component.translatable("gui.resourcefulbees.jei.info.processor");

    @Translate("Energy Inputs can only be placed on the sides of a centrifuge. Each Energy Input adds to the total capacity of the centrifuge. The tier of the Energy Input may not exceed the tier of the Terminal")
    public static final MutableComponent ENERGY_INPUT_JEI_INFO = Component.translatable("gui.resourcefulbees.jei.info.energy_input");

    @Translate("Inputs can only be placed on the top of a centrifuge. A recipe must be specified for the Input to function by placing ghost items in the filter slot. Inputs have 3 item output slots and 3 fluid output slots. An output MUST be linked to each of these slots. Linked outputs can all be the same or different. Flexibility in output linking allows for a more dynamic and customizable centrifuge with dedicated inputs and outputs. The tier of an Input may not exceed the tier of the Terminal")
    public static final MutableComponent ITEM_INPUT_JEI_INFO = Component.translatable("gui.resourcefulbees.jei.info.item_input");

    @Translate("%s Outputs can only be placed on the sides of a centrifuge. %s Outputs won't receive %s's unless linked to an input. Flexibility in output linking allows for a more dynamic and customizable centrifuge with dedicated inputs and outputs. The contents of an output can be purged via the interface. The tier of the output may not exceed the tier of the Terminal")
    public static final String OUTPUT_JEI_INFO = "gui.resourcefulbees.jei.info.output";

    @Translate("Terminals can only be placed on the sides of a centrifuge. A centrifuge may have only one terminal which determines the maximum tier. The interface of a terminal allows access to the interfaces of all blocks in a centrifuge.")
    public static final MutableComponent TERMINAL_JEI_INFO = Component.translatable("gui.resourcefulbees.jei.info.terminal");

    @Translate("Voids can only be placed on the sides of a centrifuge. Voids are optional but when added they enable the \"Void Excess\" button on centrifuge outputs. Voids also have filter slots which can be used to void specific items or fluids from all recipe results offering more fine-tuned control. The tier of the void may not exceed the tier of the Terminal")
    public static final MutableComponent VOID_JEI_INFO = Component.translatable("gui.resourcefulbees.jei.info.void");
    //endregion

    //region CONTROL PANEL TABS

    @Translate("Home")
    public static final MutableComponent HOME_TAB = Component.translatable("gui.resourcefulbees.centrifuge.control_tab.home");

    @Translate("Inputs")
    public static final MutableComponent INPUTS_TAB = Component.translatable("gui.resourcefulbees.centrifuge.control_tab.input");

    @Translate("Item Outputs")
    public static final MutableComponent ITEM_OUTPUTS_TAB = Component.translatable("gui.resourcefulbees.centrifuge.control_tab.output.item");

    @Translate("Fluid Outputs")
    public static final MutableComponent FLUID_OUTPUTS_TAB = Component.translatable("gui.resourcefulbees.centrifuge.control_tab.output.fluid");

    @Translate("Filters")
    public static final MutableComponent FILTERS_TAB = Component.translatable("gui.resourcefulbees.centrifuge.control_tab.filter");

    @Translate("Void Excess")
    public static final MutableComponent VOID_EXCESS_TAB = Component.translatable("gui.resourcefulbees.centrifuge.control_tab.void");

    @Translate("Purge Contents")
    public static final MutableComponent PURGE_TAB = Component.translatable("gui.resourcefulbees.centrifuge.control_tab.purge");

    @Translate("Inventory")
    public static final MutableComponent INVENTORY_TAB = Component.translatable("gui.resourcefulbees.centrifuge.control_tab.inventory");

    @Translate("Terminal")
    public static final MutableComponent TERMINAL_TAB = Component.translatable("gui.resourcefulbees.centrifuge.control_tab.terminal");
    //endregion

    @Translate("Output")
    public static final MutableComponent OUTPUT_NAV_TAB = Component.translatable("gui.resourcefulbees.centrifuge.nav.output");

    @Translate("Input")
    public static final MutableComponent INPUT_TAB = Component.translatable("gui.resourcefulbees.centrifuge.nav.input");

    @Translate("Filter")
    public static final MutableComponent FILTER_TAB = Component.translatable("gui.resourcefulbees.centrifuge.nav.filter");

    @Translate("Centrifuge Tank!")
    public static final MutableComponent TANK = Component.translatable("gui.resourcefulbees.centrifuge.tank");

    //region Terminal Home Panel

    @Translate("Max Tier: %s")
    public static final String MAX_TIER = "gui.resourcefulbees.centrifuge.terminal.home.max_tier";

    @Translate("Energy: %s/%srf")
    public static final String ENERGY = "gui.resourcefulbees.centrifuge.terminal.home.energy";

    @Translate("Inputs: %s")
    public static final String INPUTS = "gui.resourcefulbees.centrifuge.terminal.home.inputs";

    @Translate("Item Outputs: %s")
    public static final String ITEM_OUTPUTS = "gui.resourcefulbees.centrifuge.terminal.home.item_outputs";

    @Translate("Fluid Outputs: %s")
    public static final String FLUID_OUTPUTS = "gui.resourcefulbees.centrifuge.terminal.home.fluid_outputs";

    @Translate("Voids: %s")
    public static final String VOIDS = "gui.resourcefulbees.centrifuge.terminal.home.voids";

    @Translate("Energy Ports: %s")
    public static final String ENERGY_PORTS = "gui.resourcefulbees.centrifuge.terminal.home.energy_ports";

    @Translate("Gearboxes: %s")
    public static final String GEARBOXES = "gui.resourcefulbees.centrifuge.terminal.home.gearboxes";

    @Translate("Processors: %s")
    public static final String PROCESSORS = "gui.resourcefulbees.centrifuge.terminal.home.processors";

    @Translate("Recipe Power Modifier: %s")
    public static final String RECIPE_POWER = "gui.resourcefulbees.centrifuge.terminal.home.recipe.power";

    @Translate("Recipe Time Modifier: %s")
    public static final String RECIPE_TIME = "gui.resourcefulbees.centrifuge.terminal.home.recipe.time";
    //endregion

    //region Multiblock Validation
    @Translate("Wrong Output Location: %s")
    public static final String WRONG_OUTPUT_LOC = "multiblock.error.resourcefulbees.wrong_output_location";

    @Translate("Centrifuge must have at minimum 26 blocks!")
    public static final Component MIN_BLOCKS = Component.translatable("multiblock.error.resourcefulbees.min_blocks");

    @Translate("Centrifuge must have a Terminal!")
    public static final Component NO_TERMINAL = Component.translatable("multiblock.error.resourcefulbees.no_terminal");

    @Translate("Centrifuge must have an Input!")
    public static final Component NO_INPUTS = Component.translatable("multiblock.error.resourcefulbees.no_input");

    @Translate("Centrifuge must have an Item Output!")
    public static final Component NO_ITEM_OUTPUTS = Component.translatable("multiblock.error.resourcefulbees.no_item_output");

    @Translate("Centrifuge must have a Fluid Output!")
    public static final Component NO_FLUID_OUTPUTS = Component.translatable("multiblock.error.resourcefulbees.no_fluid_output");

    @Translate("Centrifuge must have an Energy Port!")
    public static final Component NO_ENERGY_PORTS = Component.translatable("multiblock.error.resourcefulbees.no_energy_port");

    @Translate("Void exceeds tier of Terminal!")
    public static final Component VOID_EXCEEDS_TIER = Component.translatable("multiblock.error.resourcefulbees.void_exceeds_tier");

    @Translate("Input exceeds tier of Terminal!")
    public static final Component INPUT_EXCEEDS_TIER = Component.translatable("multiblock.error.resourcefulbees.input_exceeds_tier");

    @Translate("Item Output exceeds tier of Terminal!")
    public static final Component ITEM_OUTPUT_EXCEEDS_TIER = Component.translatable("multiblock.error.resourcefulbees.item_output_exceeds_tier");

    @Translate("Fluid Output exceeds tier of Terminal!")
    public static final Component FLUID_OUTPUT_EXCEEDS_TIER = Component.translatable("multiblock.error.resourcefulbees.fluid_output_exceeds_tier");

    @Translate("Energy Port exceeds tier of Terminal!")
    public static final Component ENERGY_PORT_EXCEEDS_TIER = Component.translatable("multiblock.error.resourcefulbees.energy_port_exceeds_tier");

    @Translate("Centrifuge has too many terminals!")
    public static final Component TOO_MANY_TERMINALS = Component.translatable("multiblock.error.resourcefulbees.too_many_terminal");

    @Translate("Centrifuge has too many processors!")
    public static final Component TOO_MANY_CPUS = Component.translatable("multiblock.error.resourcefulbees.too_many_cpu");

    @Translate("Centrifuge has too many gearboxes!")
    public static final Component TOO_MANY_GEARBOXES = Component.translatable("multiblock.error.resourcefulbees.too_many_gearbox");

    @Translate("Centrifuge input can only be at the top!")
    public static final Component INPUT_NOT_ON_TOP = Component.translatable("multiblock.error.resourcefulbees.input_not_on_top");
    //endregion
}
