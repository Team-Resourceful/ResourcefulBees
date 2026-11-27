package com.teamresourceful.resourcefulbees.common.lib.constants.translations;

import com.teamresourceful.resourcefulbees.common.lib.tools.Translate;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class HoneyDipperTranslations {

    private HoneyDipperTranslations() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @Translate("Hive position for [%s] has been set to %s")
    public static final String HIVE_SET = "items.resourcefulbees.honey_dipper.hive_set";

    @Translate("Flower position for [%s] has been set to %s")
    public static final String FLOWER_SET = "items.resourcefulbees.honey_dipper.flower_set";

    @Translate("Fake Golden Flower Position for [%s] has been set to %s")
    public static final String FAKE_FLOWER_SET = "items.resourcefulbees.honey_dipper.fake_flower_set";

    @Translate("Bee Selection Cleared!")
    public static final MutableComponent SELECTION_CLEARED = Component.translatable("items.resourcefulbees.honey_dipper.cleared");

    @Translate("[%s] has been selected!")
    public static final String BEE_SET = "items.resourcefulbees.honey_dipper.bee_set";

    @Translate("Colony controller at %s has been selected!")
    public static final String CONTROLLER_SET = "items.resourcefulbees.honey_dipper.controller_set";

    @Translate("Honey Dipper selection cleared!")
    public static final String DIPPER_CLEARED = "items.resourcefulbees.honey_dipper.dipper_cleared";

    @Translate("The selected colony controller is in another dimension.")
    public static final String WRONG_DIMENSION = "items.resourcefulbees.honey_dipper.wrong_dimension";

    @Translate("The selected colony controller is not loaded.")
    public static final String CONTROLLER_UNLOADED = "items.resourcefulbees.honey_dipper.controller_unloaded";

    @Translate("The selected colony controller no longer exists.")
    public static final String CONTROLLER_INVALID = "items.resourcefulbees.honey_dipper.controller_invalid";

    @Translate("Endpoint is outside the colony operating radius.")
    public static final String OUT_OF_RANGE = "items.resourcefulbees.honey_dipper.out_of_range";

    @Translate("No supported logistics capability is available on this face.")
    public static final String NO_CAPABILITY = "items.resourcefulbees.honey_dipper.no_capability";

    @Translate("Source selected: %s")
    public static final String SOURCE_SELECTED = "items.resourcefulbees.honey_dipper.source_selected";

    @Translate("Source and destination cannot be the same endpoint.")
    public static final String SAME_ENDPOINT = "items.resourcefulbees.honey_dipper.same_endpoint";

    @Translate("The selected source is no longer valid.")
    public static final String SOURCE_INVALID = "items.resourcefulbees.honey_dipper.source_invalid";

    @Translate("The colony controller rejected the route.")
    public static final String ROUTE_REJECTED = "items.resourcefulbees.honey_dipper.route_rejected";

    @Translate("Courier route created. Destination: %s")
    public static final String ROUTE_CREATED = "items.resourcefulbees.honey_dipper.route_created";

    @Translate("Source and destination have no matching logistics capability.")
    public static final String NO_MATCHING_CAPABILITY = "items.resourcefulbees.honey_dipper.no_matching_capability";

    @Translate("Multiple resource types match. Select a resource type to continue.")
    public static final String AMBIGUOUS_CAPABILITY = "items.resourcefulbees.honey_dipper.ambiguous_capability";

}
