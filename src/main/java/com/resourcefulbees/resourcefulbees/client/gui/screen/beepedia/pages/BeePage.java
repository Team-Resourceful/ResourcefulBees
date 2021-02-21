package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class BeePage extends BeepediaPage {

    CustomBeeData beeData;

    public Pair<BeepediaScreen.TabButton, BeeDataPage> subPage;
    Pair<BeepediaScreen.TabButton, BeeDataPage> beeInfoPage;
    Pair<BeepediaScreen.TabButton, BeeDataPage> mutations;
    Pair<BeepediaScreen.TabButton, BeeDataPage> traitListPage;
    Pair<BeepediaScreen.TabButton, BeeDataPage> centrifugePage;
    Pair<BeepediaScreen.TabButton, BeeDataPage> spawningPage;
    Pair<BeepediaScreen.TabButton, BeeDataPage> breedingPage;
    List<Pair<BeepediaScreen.TabButton, BeeDataPage>> tabs = new ArrayList<>();
    ResourceLocation buttonImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/button.png");

    private int tabCounter;

    public BeePage(BeepediaScreen beepedia, CustomBeeData beeData, String id, int xPos, int yPos) {
        super(beepedia, xPos, yPos, id);
        this.beeData = beeData;
        int subX = this.xPos;
        int subY = this.yPos + 30;

        tabCounter = 0;
        beeInfoPage = Pair.of(
                getTabButton(new ItemStack(Items.BOOK), onPress -> setSubPage(SubPageType.INFO)),
                new BeeInfoPage(beepedia, beeData, subX, subY, this)
        );
        tabs.add(beeInfoPage);
        if (beeData.getMutationData().testMutations()) {
            mutations = Pair.of(
                    getTabButton(new ItemStack(Items.FERMENTED_SPIDER_EYE), onPress -> setSubPage(SubPageType.MUTATIONS)),
                    new MutationListPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(mutations);
        }
        if (beeData.getTraitData().hasTraits() && beeData.hasTraitNames()) {
            traitListPage = Pair.of(
                    getTabButton(new ItemStack(Items.BLAZE_POWDER), onPress -> setSubPage(SubPageType.TRAIT_LIST)),
                    new TraitListPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(traitListPage);
        }
        if (beeData.hasHoneycomb()) {
            centrifugePage = Pair.of(
                    getTabButton(new ItemStack(ModItems.CENTRIFUGE_ITEM.get()), onPress -> setSubPage(SubPageType.CENTRIFUGE)),
                    new CentrifugePage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(centrifugePage);
        }
        if (beeData.getSpawnData().canSpawnInWorld()) {
            spawningPage = Pair.of(
                    getTabButton(new ItemStack(Items.SPAWNER), onPress -> setSubPage(SubPageType.SPAWNING)),
                    new SpawningPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(spawningPage);
        }
        if (beeData.getBreedData().isBreedable()) {
            breedingPage = Pair.of(
                    getTabButton(new ItemStack(ModItems.GOLD_FLOWER_ITEM.get()), onPress -> setSubPage(SubPageType.BREEDING)),
                    new BreedingPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(breedingPage);
        }

        setSubPage(SubPageType.INFO);
        ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
        BeeJar.fillJar(beeJar, beeData);
        newListButton(beeJar, beeData.getTranslation());
    }

    public BeepediaScreen.TabButton getTabButton(ItemStack stack, Button.IPressable pressable) {
        BeepediaScreen.TabButton button = new BeepediaScreen.TabButton(this.xPos + 4 + tabCounter * 20, this.yPos + 20, 20, 20, 0, 0, 20, buttonImage, stack, 2, 2, pressable);
        beepedia.addButton(button);
        button.visible = false;
        tabCounter++;
        return button;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.draw(matrix, beeData.getTranslation(), xPos, yPos + 10, Color.parse("white").getRgb());
        subPage.getRight().renderBackground(matrix, partialTick, mouseX, mouseY);
    }

    @Override
    public void openPage() {
        super.openPage();
        tabs.forEach(p -> p.getLeft().visible = true);
    }

    @Override
    public void closePage() {
        super.closePage();
        tabs.forEach(p -> p.getLeft().visible = false);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        subPage.getRight().renderForeground(matrixStack, mouseX, mouseY);
    }

    @Override
    public String getSearch() {
        return beeData.getTranslation().getString();
    }

    public void setSubPage(SubPageType beeSubPage) {
        switch (beeSubPage) {
            case INFO:
                setSubPage(beeInfoPage);
                break;
            case BREEDING:
                setSubPage(breedingPage);
                break;
            case SPAWNING:
                setSubPage(spawningPage);
                break;
            case MUTATIONS:
                setSubPage(mutations);
                break;
            case CENTRIFUGE:
                setSubPage(centrifugePage);
                break;
            case TRAIT_LIST:
                setSubPage(traitListPage);
                break;
        }
        BeepediaScreen.setBeeSubPage(beeSubPage);
    }

    private void setSubPage(Pair<BeepediaScreen.TabButton, BeeDataPage> beeDataPage) {
        if (subPage != null) subPage.getRight().closePage();
        if (beeDataPage == null) beeDataPage = this.beeInfoPage;
        this.subPage = beeDataPage;
        subPage.getRight().openPage();
    }

    public enum SubPageType {
        INFO,
        SPAWNING,
        BREEDING,
        MUTATIONS,
        CENTRIFUGE,
        TRAIT_LIST
    }
}
