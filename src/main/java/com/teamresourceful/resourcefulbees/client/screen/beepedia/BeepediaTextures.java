package com.teamresourceful.resourcefulbees.client.screen.beepedia;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.resources.ResourceLocation;

public final class BeepediaTextures {

    private BeepediaTextures() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourceLocation BOOK = ResourceLocation.tryParse("textures/item/book.png");
    public static final ResourceLocation TRAIT = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/beepedia/trait_icon.png");
    public static final ResourceLocation HONEYCOMB = ResourceLocation.tryParse("textures/item/honeycomb.png");
    public static final ResourceLocation RECIPE_BOOK = ResourceLocation.tryParse("textures/item/knowledge_book.png");
    public static final ResourceLocation HONEY = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/item/catnip_honey_bottle.png");
    public static final ResourceLocation BEE = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/item/bee_jar_filled.png");

    public static final ResourceLocation HUNGER_BAR = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/beepedia/hunger_bar.png");
    public static final ResourceLocation HUNGER = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/beepedia/hunger.png");
    public static final ResourceLocation SATURATION = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "textures/gui/beepedia/saturation.png");

}
