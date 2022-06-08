package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia;

import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.BeepediaListTypes;
import com.teamresourceful.resourcefulbees.client.gui.widget.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BeepediaSearchHandler extends Pane {

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
        super(10, 147, 177, 25);
        this.beepedia = beepedia;

        beeButtons.clear();
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
        searchBox = new TooltipTextFieldWidget(Minecraft.getInstance().font, 0, 0, 117, 10, Component.translatable("gui.resourcefulbees.beepedia.search"));
        searchBox.visible = BeepediaState.isSearchVisible();
        this.add(searchBox);

        initBeeButtons(12);

        this.addAll(beeButtons);
        updateButtonVisibility();
    }

    private void initBeeButtons(int yOffset) {
        ButtonTemplate buttonTemplate = new ButtonTemplate(13, 13, 0, 0, 13, 128, 128, BeepediaImages.SEARCH_BUTTONS);
        // init search buttons
        beeStarredButton = new ToggleImageButton(0, yOffset, 0, beeStarred, buttonTemplate, b -> {
            beeStarred = b.enabled;
            if (b.enabled) {
                beeNotStarredButton.enabled = false;
                beeNotStarred = false;
            }
        }, BeepediaLang.BEE_SEARCH_STARRED);
        beeButtons.add(beeStarredButton);

        beeNotStarredButton = new ToggleImageButton(14, yOffset, 13, beeNotStarred, buttonTemplate, b -> {
            beeNotStarred = b.enabled;
            if (b.enabled) {
                beeStarredButton.enabled = false;
                beeStarred = false;
            }
        }, BeepediaLang.BEE_SEARCH_NOT_STARRED);
        beeButtons.add(beeNotStarredButton);

        beeWorldButton = new ToggleImageButton(  28, yOffset, 26, beeWorld, buttonTemplate, b -> {
            beeWorld = b.enabled;
            if (b.enabled) {
                beeNotWorldButton.enabled = false;
                beeNotWorld = false;
            }
        }, BeepediaLang.BEE_SEARCH_WORLD);
        beeButtons.add(beeWorldButton);

        beeNotWorldButton = new ToggleImageButton( 42, yOffset, 39, beeNotWorld, buttonTemplate, b -> {
            beeNotWorld = b.enabled;
            if (b.enabled) {
                beeWorldButton.enabled = false;
                beeWorld = false;
            }
        }, BeepediaLang.BEE_SEARCH_NOT_WORLD);
        beeButtons.add(beeNotWorldButton);

        beeBreedableButton = new ToggleImageButton(56, yOffset, 52, beeBreedable, buttonTemplate, b -> {
            beeBreedable = b.enabled;
            if (b.enabled) {
                beeNotBreedableButton.enabled = false;
                beeNotBreedable = false;
            }
        }, BeepediaLang.BEE_SEARCH_BREEDABLE);
        beeButtons.add(beeBreedableButton);

        beeNotBreedableButton = new ToggleImageButton(  70, yOffset, 65, beeNotBreedable, buttonTemplate, b -> {
            beeNotBreedable = b.enabled;
            if (b.enabled) {
                beeBreedableButton.enabled = false;
                beeBreedable = false;
            }
        }, BeepediaLang.BEE_SEARCH_NOT_BREEDABLE);
        beeButtons.add(beeNotBreedableButton);

        beeMutatesButton = new ToggleImageButton(  84, yOffset, 78, beeMutates, buttonTemplate,
                b -> beeMutates = b.enabled, BeepediaLang.BEE_SEARCH_MUTATES);
        beeButtons.add(beeMutatesButton);

        beeHelpButton = new ToggleImageButton(  104, yOffset, 91, beeHelp, buttonTemplate,
                b -> beeHelp = b.enabled, BeepediaLang.BEE_SEARCH_HELP);
        beeButtons.add(beeHelpButton);
    }

    private void updateButtonVisibility() {
        TooltipScreen.setButtonsVisibility(BeepediaState.currentState.selectedList == BeepediaListTypes.BEES, beeButtons);
    }

    public void toggleSearch() {
        BeepediaState.toggleSearch();
        searchBox.visible = BeepediaState.isSearchVisible();
        updateButtonVisibility();
    }
}
