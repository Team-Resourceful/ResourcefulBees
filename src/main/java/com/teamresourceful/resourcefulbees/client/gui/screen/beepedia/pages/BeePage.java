package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaImages;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.SubPageTypes;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.bees.*;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats.BeeBeepediaStats;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaScreenArea;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class BeePage extends BeepediaPage {

    public static BeeInfoPage info;
    public static BeeCombatPage combat;
    public static BeeMutationListPage mutations;
    public static BeeTraitListPage traits;
    public static BeeHoneyCombPage honeycomb;
    public static BeeSpawningPage spawning;
    public static BeeBreedingPage breeding;

    public static Map<String, BeeDataPage> pages = new HashMap<>();

    public static boolean init = false;
    private BeeBeepediaStats stats;

    public BeePage(BeepediaScreenArea screenArea) {
        super(screenArea);
    }

    public static void initPages() {
        if (init) return;
        info = new BeeInfoPage();
        combat = new BeeCombatPage();
        mutations = new BeeMutationListPage();
        traits = new BeeTraitListPage();
        honeycomb = new BeeHoneyCombPage();
        spawning = new BeeSpawningPage();
        breeding = new BeeBreedingPage();
        pages.put(SubPageTypes.INFO.name(), info);
        pages.put(SubPageTypes.COMBAT.name(), combat);
        pages.put(SubPageTypes.MUTATIONS.name(), mutations);
        pages.put(SubPageTypes.TRAITS.name(), traits);
        pages.put(SubPageTypes.HONEYCOMB.name(), honeycomb);
        pages.put(SubPageTypes.SPAWNING.name(), spawning);
        pages.put(SubPageTypes.BREEDING.name(), breeding);
        init = true;
    }

    public void preInit(BeepediaScreen beepedia, BeeBeepediaStats stats) {
        super.preInit(beepedia);
        this.stats = stats;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        beepedia.getMinecraft().textureManager.bind(BeepediaImages.SPLITTER_IMAGE);
        AbstractGui.blit(matrix, xPos, yPos, 0, 0, 186, 100, 186, 100);
        Minecraft.getInstance().font.draw(matrix, label.withStyle(TextFormatting.WHITE), (float) xPos + 40, (float) yPos + 10, -1);
        subPage.getRight().renderBackground(matrix, partialTick, mouseX, mouseY);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = beepedia.getMinecraft().getWindow().getGuiScale();
        int scissorY = (int) (beepedia.getMinecraft().getWindow().getHeight() - (yPos + 9 + 38) * scale);
        GL11.glScissor((int) (xPos * scale), scissorY, (int) (38 * scale), (int) (38 * scale));
        RenderUtils.renderEntity(matrix, getBee(), Minecraft.getInstance().level, (float) xPos + 10, (float) yPos + 2, -45, 2);
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


    public void addSearchBiome(String biome) {
        searchBiomes.add(biome);
    }

    public void addSearchBeeTag(String beeTag) {
        searchBeeTags.add(beeTag);
    }

    public void addSearchTrait(String trait) {
        searchTraits.add(trait);
    }

    public void addSearchExtra(String extra) {
        searchExtra.add(extra);
    }

    public void addSearchItem(String item) {
        searchItems.add(item);
    }

    public void addSearchItem(Block b) {
        addSearchItem(b.getName().getString());
        if (b.getRegistryName() != null) addSearchItem(b.getRegistryName().getPath());
    }

    public void addSearchItem(Item b) {
        addSearchItem(new ItemStack(b).getDisplayName().getString());
        if (b.getRegistryName() != null) addSearchItem(b.getRegistryName().getPath());
    }

    public void addSearchItem(FluidStack fluid) {
        addSearchItem(fluid.getDisplayName().getString());
        if (fluid.getFluid().getRegistryName() != null) addSearchItem(fluid.getFluid().getRegistryName().getPath());
    }

    public void addSearchBee(Entity entity, String bee) {
        searchBees.add(bee);
        searchBees.add(entity.getName().getString());
    }

    public void addSearchEntity(Entity entity) {
        searchEntity.add(entity.getName().getString());
    }
}
