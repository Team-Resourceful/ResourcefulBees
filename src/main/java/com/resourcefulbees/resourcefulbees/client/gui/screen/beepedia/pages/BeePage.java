package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BeePage extends BeeDataPage {

    MutationListPage mutations;
    List<TraitPage> traitPageList;
    CentrifugePage centrifugePage;
    SpawningPage spawningPage;
    BreedingPage breedingPage;

    public BeePage(BeepediaScreen beepedia, CustomBeeData beeData) {
        super(beepedia, BeepediaScreen.Page.BEE, beeData);
        mutations = new MutationListPage(beepedia, beeData);
        traitPageList = beepedia.getTraits(beeData);
        centrifugePage = new CentrifugePage(beepedia, beeData);
        spawningPage = new SpawningPage(beepedia, beeData);
        breedingPage = new BreedingPage(beepedia, beeData);

        int top = beepedia.getGuiTop();
        int left = beepedia.getGuiLeft();
        ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
        BeeJar.fillJar(beeJar, beeData);
        listButton = new ListButton(left + 8, top, 100, 20, 0, 0, 20, listImage, beeJar, 2, 2, beeData.getTranslation(), 20, 10, onPress -> {
            beepedia.setActive(this);
        });
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {

    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

    }
}
