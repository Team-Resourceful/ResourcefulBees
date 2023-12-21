package com.teamresourceful.resourcefulbees.client.screen.beepedia;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.resources.ResourceLocation;

public final class BeepediaTextures {

    private BeepediaTextures() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final ResourceLocation BOOK = new ResourceLocation("textures/item/book.png");
    public static final ResourceLocation TRAIT = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/trait_icon.png");
    public static final ResourceLocation HOMEYCOMB = new ResourceLocation("textures/item/honeycomb.png");
    public static final ResourceLocation RECIPE_BOOK = new ResourceLocation("textures/item/knowledge_book.png");
    public static final ResourceLocation HONEY = new ResourceLocation(ModConstants.MOD_ID, "textures/item/catnip_honey_bottle.png");
    public static final ResourceLocation BEE = new ResourceLocation(ModConstants.MOD_ID, "textures/item/bee_jar_filled.png");

    public static final ResourceLocation HUNGER_BAR = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/hunger_bar.png");
    public static final ResourceLocation HUNGER = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/hunger.png");
    public static final ResourceLocation SATURATION = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/saturation.png");

}
