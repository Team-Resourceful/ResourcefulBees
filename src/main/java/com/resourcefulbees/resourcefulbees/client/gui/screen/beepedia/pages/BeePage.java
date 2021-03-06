package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BeePage extends BeepediaPage {

    public CustomBeeData beeData;


    Entity bee = null;
    protected Pair<TabImageButton, BeeDataPage> subPage;
    Pair<TabImageButton, BeeDataPage> beeInfoPage;
    Pair<TabImageButton, BeeDataPage> mutations;
    Pair<TabImageButton, BeeDataPage> traitListPage;
    Pair<TabImageButton, BeeDataPage> centrifugePage;
    Pair<TabImageButton, BeeDataPage> spawningPage;
    Pair<TabImageButton, BeeDataPage> breedingPage;
    List<Pair<TabImageButton, BeeDataPage>> tabs = new ArrayList<>();
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
                    getTabButton(new ItemStack(Items.HONEYCOMB), onPress -> setSubPage(SubPageType.HONEYCOMB),
                            new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.honeycombs")),
                    new HoneycombPage(beepedia, beeData, subX, subY, this)
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

    public TabImageButton getTabButton(ItemStack stack, Button.IPressable pressable, ITextComponent tooltip) {
        TabImageButton button = new TabImageButton(this.xPos + 40 + tabCounter * 21, this.yPos + 27,
                20, 20, 0, 0, 20, buttonImage, stack, 2, 2, pressable, beepedia.getTooltipProvider(tooltip));
        beepedia.addButton(button);
        button.visible = false;
        tabCounter++;
        return button;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        beepedia.getMinecraft().textureManager.bindTexture(splitterImage);
        AbstractGui.drawTexture(matrix, xPos, yPos, 0, 0, 165, 100, 165, 100);
        Minecraft.getInstance().fontRenderer.draw(matrix, beeData.getTranslation().formatted(TextFormatting.WHITE), (float) xPos + 40, (float) yPos + 10, -1);
        subPage.getRight().renderBackground(matrix, partialTick, mouseX, mouseY);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = beepedia.getMinecraft().getWindow().getGuiScaleFactor();
        int scissorY = (int) (beepedia.getMinecraft().getWindow().getFramebufferHeight() - (yPos + 9 + 38) * scale);
        GL11.glScissor((int) (xPos * scale), scissorY, (int) (38 * scale), (int) (38 * scale));
        RenderUtils.renderEntity(matrix, getBee(), Minecraft.getInstance().world, (float) xPos + 10, (float) yPos + 2, -45, 2);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

    }

    @Override
    public void openPage() {
        super.openPage();
        if (BeepediaScreen.currScreenState.getPageID() != null) {
            setSubPage(BeepediaScreen.currScreenState.getBeeSubPage());
        } else {
            setSubPage(SubPageType.INFO);
        }
        tabs.forEach(p -> p.getLeft().visible = true);
    }

    @Override
    public void closePage() {
        super.closePage();
        if (subPage != null) this.subPage.getRight().closePage();
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return subPage.getRight().mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return subPage.getRight().mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void setSubPage(SubPageType beeSubPage) {
        Pair<TabImageButton, BeeDataPage> page;
        switch (beeSubPage) {
            case BREEDING:
                page = breedingPage;
                break;
            case INFO:
                page = beeInfoPage;
                break;
            case SPAWNING:
                page = spawningPage;
                break;
            case MUTATIONS:
                page = mutations;
                break;
            case HONEYCOMB:
                page = centrifugePage;
                break;
            case TRAIT_LIST:
                page = traitListPage;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + beeSubPage);
        }
        if (subPage != null) {
            this.subPage.getRight().closePage();
            this.subPage.getLeft().active = true;
        }
        this.subPage = page == null ? beeInfoPage : page;
        if(subPage != null) {
            if (!(subPage.getRight() instanceof SpawningPage)) {
                BeepediaScreen.currScreenState.setBiomesOpen(false);
                BeepediaScreen.currScreenState.setSpawningScroll(0);
            }
            if (!(subPage.getRight() instanceof TraitListPage)) {
                BeepediaScreen.currScreenState.setTraitsScroll(0);
            }
            this.subPage.getLeft().active = false;
            this.subPage.getRight().openPage();
        }

        BeepediaScreen.currScreenState.setBeeSubPage(beeSubPage);
    }

    public Entity getBee() {
        if (bee == null) bee = beepedia.initEntity(beeData.getEntityTypeRegistryID());
        return bee;
    }

    public enum SubPageType {
        INFO,
        SPAWNING,
        BREEDING,
        MUTATIONS,
        HONEYCOMB,
        TRAIT_LIST
    }
}
