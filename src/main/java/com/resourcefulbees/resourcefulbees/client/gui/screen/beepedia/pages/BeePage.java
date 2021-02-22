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
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BeePage extends BeepediaPage {

    CustomBeeData beeData;


    Entity bee = null;
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
        int subX = this.xPos + 1;
        int subY = this.yPos + 50;

        tabCounter = 0;
        beeInfoPage = Pair.of(
                getTabButton(new ItemStack(Items.BOOK), onPress -> setSubPage(SubPageType.INFO),
                        new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.info")),
                new BeeInfoPage(beepedia, beeData, subX, subY, this)
        );
        subPage = beeInfoPage;
        tabs.add(beeInfoPage);
        if (beeData.getMutationData().testMutations()) {
            mutations = Pair.of(
                    getTabButton(new ItemStack(Items.FERMENTED_SPIDER_EYE), onPress -> setSubPage(SubPageType.MUTATIONS),
                            new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations")),
                    new MutationListPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(mutations);
        }
        if (beeData.getTraitData().hasTraits() && beeData.hasTraitNames()) {
            traitListPage = Pair.of(
                    getTabButton(new ItemStack(Items.BLAZE_POWDER), onPress -> setSubPage(SubPageType.TRAIT_LIST),
                            new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.traits")),
                    new TraitListPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(traitListPage);
        }
        if (beeData.hasHoneycomb()) {
            centrifugePage = Pair.of(
                    getTabButton(new ItemStack(ModItems.CENTRIFUGE_ITEM.get()), onPress -> setSubPage(SubPageType.CENTRIFUGE),
                            new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.centrifuge")),
                    new CentrifugePage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(centrifugePage);
        }
        if (beeData.getSpawnData().canSpawnInWorld()) {
            spawningPage = Pair.of(
                    getTabButton(new ItemStack(Items.SPAWNER), onPress -> setSubPage(SubPageType.SPAWNING),
                            new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning")),
                    new SpawningPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(spawningPage);
        }
        if (beeData.getBreedData().isBreedable()) {
            breedingPage = Pair.of(
                    getTabButton(new ItemStack(ModItems.GOLD_FLOWER_ITEM.get()), onPress -> setSubPage(SubPageType.BREEDING),
                            new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding")),
                    new BreedingPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(breedingPage);
        }

        ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
        BeeJar.fillJar(beeJar, beeData);
        newListButton(beeJar, beeData.getTranslation());
    }

    public BeepediaScreen.TabButton getTabButton(ItemStack stack, Button.IPressable pressable, ITextComponent tooltip) {
        BeepediaScreen.TabButton button = new BeepediaScreen.TabButton(this.xPos + 40 + tabCounter * 21, this.yPos + 27,
                20, 20, 0, 0, 20, buttonImage, stack, 2, 2, pressable, beepedia.getTooltipProvider(tooltip));
        beepedia.addButton(button);
        button.visible = false;
        tabCounter++;
        return button;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        if (bee == null) bee = beepedia.initEntity(beeData.getEntityTypeRegistryID());
        Minecraft.getInstance().fontRenderer.draw(matrix, beeData.getTranslation(), xPos + 40, yPos + 10, Color.parse("white").getRgb());
        subPage.getRight().renderBackground(matrix, partialTick, mouseX, mouseY);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = beepedia.getMinecraft().getWindow().getGuiScaleFactor();
        int scissorY = (int) (beepedia.getMinecraft().getWindow().getFramebufferHeight() - (yPos + 9 + 38) * scale);
        GL11.glScissor((int) (xPos * scale), scissorY, (int) (38 * scale), (int) (38 * scale));
        beepedia.renderEntity(matrix, bee, Minecraft.getInstance().world, xPos + 10, yPos + 32, -45, 2);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

    }

    @Override
    public void openPage() {
        super.openPage();
        if (BeepediaScreen.getPageID() != null && BeepediaScreen.getPageID().equals(id)) {
            setSubPage(BeepediaScreen.getBeeSubPage());
        } else {
            setSubPage(SubPageType.INFO);
        }
        tabs.forEach(p -> p.getLeft().visible = true);
    }

    @Override
    public void closePage() {
        super.closePage();
        tabs.forEach(p -> p.getLeft().visible = false);
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {
        subPage.getRight().renderForeground(matrix, mouseX, mouseY);
    }

    @Override
    public String getSearch() {
        return beeData.getTranslation().getString();
    }

    @Override
    public void tick(int ticksActive) {
        subPage.getRight().tick(ticksActive);
    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {
        subPage.getRight().drawTooltips(matrixStack, mouseX, mouseY);
        tabs.forEach(p -> p.getLeft().renderToolTip(matrixStack, mouseX, mouseY));
    }

    public void setSubPage(SubPageType beeSubPage) {
        Pair<BeepediaScreen.TabButton, BeeDataPage> page = null;
        switch (beeSubPage) {
            case INFO:
                page = beeInfoPage;
                break;
            case BREEDING:
                page = breedingPage;
                break;
            case SPAWNING:
                page = spawningPage;
                break;
            case MUTATIONS:
                page = mutations;
                break;
            case CENTRIFUGE:
                page = centrifugePage;
                break;
            case TRAIT_LIST:
                page = traitListPage;
                break;
        }
        if (page == null) page = beeInfoPage;
        setSubPage(page);
        BeepediaScreen.setBeeSubPage(beeSubPage);
    }

    private void setSubPage(Pair<BeepediaScreen.TabButton, BeeDataPage> beeDataPage) {
        if (subPage != null) {
            subPage.getRight().closePage();
            this.subPage.getLeft().active = true;
        }
        if (beeDataPage == null) beeDataPage = this.beeInfoPage;
        this.subPage = beeDataPage;
        this.subPage.getLeft().active = false;
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
