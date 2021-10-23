package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.widget.ButtonTemplate;
import com.teamresourceful.resourcefulbees.client.gui.widget.ToggleImageButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.TooltipTextFieldWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BeepediaSearchHandler {

    // stored data
    private static String currentSearch = null;
    private static boolean beeStarred = false;
    private static boolean beeNotStarred = false;
    private static boolean beeWorld = false;
    private static boolean beeNotWorld = false;
    private static boolean beeBreedable = false;
    private static boolean beeNotBreedable = false;
    private static boolean beeMutates = false;
    private static boolean beeHelp = false;

    private final BeepediaScreen beepedia;

    // session buttons
    private TooltipTextFieldWidget searchBox;
    private ToggleImageButton beeStarredButton;
    private ToggleImageButton beeNotStarredButton;
    private ToggleImageButton beeWorldButton;
    private ToggleImageButton beeNotWorldButton;
    private ToggleImageButton beeBreedableButton;
    private ToggleImageButton beeNotBreedableButton;
    private ToggleImageButton beeMutatesButton;
    private ToggleImageButton beeHelpButton;
    private List<ToggleImageButton> beeButtons = new LinkedList<>();


    public BeepediaSearchHandler(BeepediaScreen beepedia) {
        this.beepedia = beepedia;
    }

    /**
     * Register the beepedia search bar and register search parameters for each page.
     *
     * @param x top left corner x position
     * @param y top left corner y position
     */
    public void registerSearch(int x, int y) {
        beeButtons.clear();
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
        searchBox = new TooltipTextFieldWidget(Minecraft.getInstance().font, x + 10, y + 147, 117, 10, new TranslationTextComponent("gui.resourcefulbees.beepedia.search"));
        searchBox.visible = false;
        beepedia.addWidget(searchBox);

        int buttonXOffset = x + 10;
        int buttonYOffset = y + 159;

        ButtonTemplate buttonTemplate = new ButtonTemplate(13, 13, 0, 0, 13, 128, 128, BeepediaImages.SEARCH_BUTTONS);
        // init search buttons
        beeStarredButton = new ToggleImageButton(buttonXOffset, buttonYOffset, 0, beeStarred, buttonTemplate, b -> {
            beeStarred = b.enabled;
            if (b.enabled) {
                beeNotStarredButton.enabled = false;
                beeNotStarred = false;
            }
        }, BeepediaLang.BEE_SEARCH_STARRED);
        beeButtons.add(beeStarredButton);

        beeNotStarredButton = new ToggleImageButton(buttonXOffset + 14, buttonYOffset, 13, beeNotStarred, buttonTemplate, b -> {
            beeNotStarred = b.enabled;
            if (b.enabled) {
                beeStarredButton.enabled = false;
                beeStarred = false;
            }
        }, BeepediaLang.BEE_SEARCH_NOT_STARRED);
        beeButtons.add(beeNotStarredButton);

        beeWorldButton = new ToggleImageButton(buttonXOffset + 28, buttonYOffset, 26, beeWorld, buttonTemplate, b -> {
            beeWorld = b.enabled;
            if (b.enabled) {
                beeNotWorldButton.enabled = false;
                beeNotWorld = false;
            }
        }, BeepediaLang.BEE_SEARCH_WORLD);
        beeButtons.add(beeWorldButton);

        beeNotWorldButton = new ToggleImageButton(buttonXOffset + 42, buttonYOffset, 39, beeNotWorld, buttonTemplate, b -> {
            beeNotWorld = b.enabled;
            if (b.enabled) {
                beeWorldButton.enabled = false;
                beeWorld = false;
            }
        }, BeepediaLang.BEE_SEARCH_NOT_WORLD);
        beeButtons.add(beeNotWorldButton);

        beeBreedableButton = new ToggleImageButton(buttonXOffset + 56, buttonYOffset, 52, beeBreedable, buttonTemplate, b -> {
            beeBreedable = b.enabled;
            if (b.enabled) {
                beeNotBreedableButton.enabled = false;
                beeNotBreedable = false;
            }
        }, BeepediaLang.BEE_SEARCH_BREEDABLE);
        beeButtons.add(beeBreedableButton);

        beeNotBreedableButton = new ToggleImageButton(buttonXOffset + 70, buttonYOffset, 65, beeNotBreedable, buttonTemplate, b -> {
            beeNotBreedable = b.enabled;
            if (b.enabled) {
                beeBreedableButton.enabled = false;
                beeBreedable = false;
            }
        }, BeepediaLang.BEE_SEARCH_NOT_BREEDABLE);
        beeButtons.add(beeNotBreedableButton);

        beeMutatesButton = new ToggleImageButton(buttonXOffset + 84, buttonYOffset, 78, beeMutates, buttonTemplate,
                b -> beeMutates = b.enabled, BeepediaLang.BEE_SEARCH_MUTATES);
        beeButtons.add(beeMutatesButton);

        beeHelpButton = new ToggleImageButton(buttonXOffset + 104, buttonYOffset, 91, beeHelp, buttonTemplate,
                b -> beeHelp = b.enabled, BeepediaLang.BEE_SEARCH_HELP);
        beeButtons.add(beeHelpButton);

        beepedia.addButtons(beeButtons);
        BeepediaScreen.setButtonsVisibility(searchBox.visible, beeButtons);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        searchBox.render(matrixStack, mouseX, mouseY, partialTick);
        registerTooltips();
    }

    private void registerTooltips() {

    }

    public void toggleSearch() {
        searchBox.visible = !searchBox.visible;
        BeepediaScreen.setButtonsVisibility(searchBox.visible, beeButtons);
    }
}
