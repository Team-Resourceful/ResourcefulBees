package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class BeepediaImages {

    private BeepediaImages() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static final ResourceLocation arrowImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/arrows.png");
    public static final ResourceLocation LIST_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/list_button.png");
    public static final ResourceLocation SPLITTER_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/bee_splitter.png");
    public static final ResourceLocation BUTTON_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/button.png");
    public static final ResourceLocation hungerBar = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/hunger_bar.png");
    public static final ResourceLocation hungerIcons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/hunger.png");
    public static final ResourceLocation saturationIcons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/saturation.png");
    public static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/screen.png");
    public static final ResourceLocation BEE_INFO_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/bee_screen.png");
    public static final ResourceLocation SLOT_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/slot.png");
    public static final ResourceLocation SHADES_BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/shades_of_bees.png");
    public static final ResourceLocation SHADES_BUTTON_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/shades_button.png");
    public static final ResourceLocation HOME_BUTTONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/home_buttons.png");
    public static final ResourceLocation DISCORD_BUTTON = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/discord.png");
    public static final ResourceLocation PATREON_BUTTON = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/patreon.png");
    public static final ResourceLocation ISSUES_BUTTON = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/issues.png");
    public static final ResourceLocation WIKI_BUTTON = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/wiki.png");
    public static final ResourceLocation LOGO = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/logo.png");
    public static final ResourceLocation BREEDING_IMAGE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/breeding.png");
    public static final ResourceLocation INFO_ICON = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");
    public static final ResourceLocation SEARCH_BUTTONS = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/search_buttons.png");
}
