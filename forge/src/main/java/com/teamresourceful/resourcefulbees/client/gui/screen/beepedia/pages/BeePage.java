package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CombatData;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.EntityMutation;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.ItemMutation;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.TabImageButton;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.item.BeeJar;
import com.teamresourceful.resourcefulbees.lib.HoneycombTypes;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import com.teamresourceful.resourcefulbees.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BeePage extends BeepediaPage {

    public final CustomBeeData beeData;

    private Entity bee = null;
    protected Pair<TabImageButton, BeeDataPage> subPage;
    private Pair<TabImageButton, BeeDataPage> beeInfoPage;
    private Pair<TabImageButton, BeeDataPage> beeCombatPage = null;
    private Pair<TabImageButton, BeeDataPage> mutationsPage = null;
    private Pair<TabImageButton, BeeDataPage> traitListPage = null;
    private Pair<TabImageButton, BeeDataPage> centrifugePage = null;
    private Pair<TabImageButton, BeeDataPage> spawningPage = null;
    private Pair<TabImageButton, BeeDataPage> breedingPage = null;
    final List<Pair<TabImageButton, BeeDataPage>> tabs = new ArrayList<>();
    final ResourceLocation buttonImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/button.png");

    private List<String> searchBiomes = new LinkedList<>();
    private List<String> searchBeeTags = new LinkedList<>();
    private List<String> searchTraits = new LinkedList<>();
    private List<String> searchBees = new LinkedList<>();
    private List<String> searchExtra = new LinkedList<>();
    private List<String> searchItems = new LinkedList<>();
    private List<String> searchEntity = new LinkedList<>();
    private List<String> searchAll = new LinkedList<>();

    private int tabCounter;
    final MutableComponent label;
    public final boolean beeUnlocked;

    public BeePage(BeepediaScreen beepedia, CustomBeeData beeData, String id, int xPos, int yPos) {
        super(beepedia, xPos, yPos, id);
        this.beeData = beeData;
        int subX = this.xPos + 1;
        int subY = this.yPos + 50;
        beeUnlocked = beepedia.itemBees.contains(id) || beepedia.complete;

        tabCounter = 0;

        registerInfoPage(subX, subY);
        registerCombatPage(subX, subY);
        registerMutationListPage(subX, subY);
        registerTraitListPage(subX, subY);
        registerHoneycombPage(subX, subY);
        registerSpawningPage(subX, subY);
        registerBreedPage(subX, subY);

        ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
        BeeJar.fillJar(beeJar, beeData);
        MutableComponent star = new TextComponent(beeUnlocked ? ChatFormatting.GREEN + "✦ " + ChatFormatting.RESET : "✧ ");
        star.append(beeData.getDisplayName());
        label = star;
        newListButton(beeJar, label);

        addSearch();
    }


    private void registerInfoPage(int subX, int subY) {
        beeInfoPage = Pair.of(
                getTabButton(new ItemStack(Items.BOOK), onPress -> setSubPage(SubPageType.INFO),
                        new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.info")),
                new BeeInfoPage(beepedia, beeData, subX, subY, this)
        );
        subPage = beeInfoPage;
        tabs.add(beeInfoPage);
    }

    private void registerCombatPage(int subX, int subY) {
        if (!beeData.getCombatData().equals(CombatData.DEFAULT)) {
            beeCombatPage = Pair.of(
                    getTabButton(new ItemStack(Items.IRON_SWORD), onPress -> setSubPage(SubPageType.COMBAT),
                            new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.combat")),
                    new BeeCombatPage(beepedia, beeData, subX, subY, this)
            );
            subPage = beeCombatPage;
            tabs.add(beeCombatPage);
        }
    }

    private void registerMutationListPage(int subX, int subY) {
        if (beeData.getMutationData().testMutations() && (!Config.BEEPEDIA_HIDE_LOCKED.get() || beeUnlocked)) {
            mutationsPage = Pair.of(
                    getTabButton(new ItemStack(Items.FERMENTED_SPIDER_EYE), onPress -> setSubPage(SubPageType.MUTATIONS),
                            new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations")),
                    new MutationListPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(mutationsPage);
        }
    }

    private void registerTraitListPage(int subX, int subY) {
        if (beeData.getTraitData().hasTraits() && !beeData.getTraitData().getTraits().isEmpty() && (!Config.BEEPEDIA_HIDE_LOCKED.get() || beeUnlocked)) {
            traitListPage = Pair.of(
                    getTabButton(new ItemStack(Items.BLAZE_POWDER), onPress -> setSubPage(SubPageType.TRAIT_LIST),
                            new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.traits")),
                    new TraitListPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(traitListPage);
        }
    }

    private void registerHoneycombPage(int subX, int subY) {
        if (!beeData.getHoneycombData().getHoneycombType().equals(HoneycombTypes.NONE) && (!Config.BEEPEDIA_HIDE_LOCKED.get() || beeUnlocked)) {
            centrifugePage = Pair.of(
                    getTabButton(new ItemStack(Items.HONEYCOMB), onPress -> setSubPage(SubPageType.HONEYCOMB),
                            new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.honeycombs")),
                    new HoneycombPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(centrifugePage);
        }
    }

    private void registerSpawningPage(int subX, int subY) {
        if (beeData.getSpawnData().canSpawnInWorld()) {
            spawningPage = Pair.of(
                    getTabButton(new ItemStack(Items.SPAWNER), onPress -> setSubPage(SubPageType.SPAWNING),
                            new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning")),
                    new SpawningPage(beepedia, beeData, subX, subY, this)
            );
            tabs.add(spawningPage);
        }
    }

    private void registerBreedPage(int subX, int subY) {
        List<EntityMutation> breedMutations = BeeRegistry.getRegistry().getMutationsContaining(beeData);
        List<ItemMutation> itemBreedMutation = BeeRegistry.getRegistry().getItemMutationsContaining(beeData);
        if (beeData.getBreedData().hasParents() || !breedMutations.isEmpty() || !itemBreedMutation.isEmpty()) {
            breedingPage = Pair.of(
                    getTabButton(new ItemStack(ModItems.GOLD_FLOWER_ITEM.get()), onPress -> setSubPage(SubPageType.BREEDING),
                            new TranslatableComponent("gui.resourcefulbees.beepedia.bee_subtab.breeding")),
                    new BreedingPage(beepedia, beeData, subX, subY, breedMutations, itemBreedMutation, this)
            );
            tabs.add(breedingPage);
        }
    }

    public TabImageButton getTabButton(ItemStack stack, Button.OnPress pressable, Component tooltip) {
        TabImageButton button = new TabImageButton(this.xPos + 40 + tabCounter * 21, this.yPos + 27,
                20, 20, 0, 0, 20, buttonImage, stack, 2, 2, pressable, beepedia.getTooltipProvider(tooltip));
        beepedia.addButton(button);
        button.visible = false;
        tabCounter++;
        return button;
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        beepedia.getMinecraft().textureManager.bind(splitterImage);
        GuiComponent.blit(matrix, xPos, yPos, 0, 0, 186, 100, 186, 100);
        Minecraft.getInstance().font.draw(matrix, label.withStyle(ChatFormatting.WHITE), (float) xPos + 40, (float) yPos + 10, -1);
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
    public void renderForeground(PoseStack matrix, int mouseX, int mouseY) {
        subPage.getRight().renderForeground(matrix, mouseX, mouseY);
    }

    @Override
    public void addSearch() {
        addSearchBee(getBee(), id);
        // init page tags
        if (breedingPage != null) addSearchBeeTag("canBreed");
        if (traitListPage != null) addSearchBeeTag("hasTraits");
        if (centrifugePage != null) addSearchBeeTag("hasComb");
        if (beeData.getCentrifugeData().hasCentrifugeOutput()) addSearchBeeTag("hasCentrifuge");
        if (mutationsPage != null) addSearchBeeTag("hasMutations");
        if (spawningPage != null) addSearchBeeTag("canSpawn");
        // generate search terms
        for (Pair<TabImageButton, BeeDataPage> tab : tabs) {
            tab.getRight().addSearch();
        }
        // reduce lists
        searchBees = searchBees.stream().distinct().collect(Collectors.toList());
        searchEntity = searchEntity.stream().distinct().collect(Collectors.toList());
        searchItems = searchItems.stream().distinct().collect(Collectors.toList());
        searchBiomes = searchBiomes.stream().distinct().collect(Collectors.toList());
        searchBeeTags = searchBeeTags.stream().distinct().collect(Collectors.toList());
        searchTraits = searchTraits.stream().distinct().collect(Collectors.toList());
        searchExtra = searchExtra.stream().distinct().collect(Collectors.toList());
        // set up prefixless searching
        searchAll.addAll(searchBees);
        searchAll.addAll(searchEntity);
        searchAll.addAll(searchItems);
        searchAll.addAll(searchBiomes);
        searchAll.addAll(searchBeeTags);
        searchAll.addAll(searchTraits);
        searchAll.addAll(searchExtra);
        searchAll = searchAll.stream().distinct().collect(Collectors.toList());
    }

    public boolean getBeeFromSearch(String search) {
        String[] params = search.split(" ");
        for (String param : params) {
            param = param.trim();
            switch (param.charAt(0)) {
                case '$':
                    if (!getSearch(searchBees, param.replaceFirst("\\$", ""))) return false;
                    break;
                case '#':
                    if (!getSearch(searchBeeTags, param.replaceFirst("#", ""))) return false;
                    break;
                case '!':
                    if (!getSearch(searchTraits, param.replaceFirst("!", ""))) return false;
                    break;
                case '&':
                    if (!getSearch(searchItems, param.replaceFirst("&", ""))) return false;
                    break;
                case '@':
                    if (!getSearch(searchBiomes, param.replaceFirst("@", ""))) return false;
                    break;
                case '-':
                    if (!getSearch(searchEntity, param.replaceFirst("-", ""))) return false;
                    break;
                default:
                    if (!getSearch(searchAll, param)) return false;
                    break;
            }
        }
        return true;
    }

    @Override
    public void tick(int ticksActive) {
        subPage.getRight().tick(ticksActive);
    }

    @Override
    public void drawTooltips(PoseStack matrixStack, int mouseX, int mouseY) {
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
                page = mutationsPage;
                break;
            case HONEYCOMB:
                page = centrifugePage;
                break;
            case TRAIT_LIST:
                page = traitListPage;
                break;
            case COMBAT:
                page = beeCombatPage;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + beeSubPage);
        }
        if (subPage != null) {
            this.subPage.getRight().closePage();
            this.subPage.getLeft().active = true;
        }
        this.subPage = page == null ? beeInfoPage : page;
        if (subPage != null) {
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

    public Entity getBee() {
        if (bee == null) bee = beepedia.initEntity(beeData.getRegistryID());
        return bee;
    }


    public enum SubPageType {
        INFO,
        SPAWNING,
        BREEDING,
        MUTATIONS,
        HONEYCOMB,
        TRAIT_LIST,
        COMBAT;
    }
}
