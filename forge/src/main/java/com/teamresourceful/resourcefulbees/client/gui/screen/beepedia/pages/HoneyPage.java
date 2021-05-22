package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.honeydata.DefaultHoneyBottleData;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyEffect;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.ListButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.SubButtonList;
import com.teamresourceful.resourcefulbees.item.BeeJar;
import com.teamresourceful.resourcefulbees.registry.ModBlocks;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.*;

public class HoneyPage extends BeepediaPage {

    private final ImageButton prevTab;
    private final ImageButton nextTab;
    private SubButtonList beeList = null;
    private boolean isDefault;
    private HoneyBottleData bottleData;
    private String honeySearch;
    private final TranslatableComponent text;

    private final int hunger;
    private final float saturation;
    private final ItemStack bottle;
    private List<HoneyEffect> effects = new ArrayList<>();

    private final List<String> searchEffects = new LinkedList<>();
    private final List<String> searchItems = new LinkedList<>();
    private final List<String> searchBees = new LinkedList<>();
    private final List<String> searchTags = new LinkedList<>();
    private final List<String> searchAll = new LinkedList<>();

    private static final int LIST_HEIGHT = 102;

    final ResourceLocation hungerBar = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/hunger_bar.png");
    final ResourceLocation hungerIcons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/hunger.png");
    final ResourceLocation saturationIcons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/saturation.png");

    public HoneyPage(BeepediaScreen beepedia, HoneyBottleData bottleData, String id, int left, int top, boolean isDefault) {
        super(beepedia, left, top, id);
        this.isDefault = isDefault;
        if (isDefault) {
            this.bottle = new ItemStack(DefaultHoneyBottleData.bottle);
            this.hunger = Foods.HONEY_BOTTLE.getNutrition();
            this.saturation = Foods.HONEY_BOTTLE.getSaturationModifier();
            this.text = new TranslatableComponent("fluid.resourcefulbees.honey");
        } else {
            this.bottleData = bottleData;
            this.bottle = new ItemStack(bottleData.getHoneyBottleRegistryObject().get());
            this.hunger = bottleData.getHunger();
            this.saturation = bottleData.getSaturation();
            this.effects = bottleData.getHoneyEffects();
            if (bottleData.doGenerateHoneyFluid()) {
                this.text = bottleData.getFluidTranslation();
            } else {
                this.text = bottleData.getBottleTranslation();
            }
        }
        newListButton(bottle, text);
        prevTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 48, yPos + 40, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> toggleTab());
        nextTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 40, yPos + 40, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> toggleTab());
        beepedia.addButton(nextTab);
        beepedia.addButton(prevTab);
        nextTab.visible = false;
        prevTab.visible = false;
        initBeeList();
    }

    private void toggleTab() {
        BeepediaScreen.currScreenState.setHoneyEffectsActive(!BeepediaScreen.currScreenState.isHoneyEffectsActive());
        beeList.setActive(effects.isEmpty() || !BeepediaScreen.currScreenState.isHoneyEffectsActive());
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        if (beeList == null) return;
        beeList.updateList();
        beepedia.drawSlotNoToolTip(matrix, bottle, xPos, yPos + 10);
        beepedia.getMinecraft().textureManager.bind(splitterImage);
        GuiComponent.blit(matrix, xPos, yPos - 14, 0, 0, 165, 100, 165, 100);
        Font font = Minecraft.getInstance().font;
        font.draw(matrix, text.withStyle(ChatFormatting.WHITE), (float) xPos + 24, (float) yPos + 12, -1);
        drawHungerBar(matrix);
        if (BeepediaScreen.currScreenState.isHoneyEffectsActive() && !effects.isEmpty()) {
            drawEffectsList(matrix, xPos, yPos + 34);
        } else {
            drawBeesList(matrix, xPos, yPos + 34);
        }
    }

    private void initBeeList() {
        Map<String, BeePage> beePages = beepedia.getBees(bottle);
        SortedMap<String, ListButton> buttons = new TreeMap<>();
        for (Map.Entry<String, BeePage> e : beePages.entrySet()) {
            ItemStack stack = new ItemStack(ModItems.BEE_JAR.get());
            BeeJar.fillJar(stack, e.getValue().beeData);
            Component translation = e.getValue().beeData.getDisplayName();
            Button.OnPress onPress = button -> {
                BeepediaScreen.saveScreenState();
                beepedia.setActive(BeepediaScreen.PageType.BEE, e.getKey());
            };
            ListButton button = new ListButton(0, 0, 100, 20, 0, 0, 20, listImage, stack, 2, 2, translation, 22, 6, onPress);
            beepedia.addButton(button);
            button.visible = false;
            buttons.put(e.getKey(), button);
        }
        beeList = new SubButtonList(xPos, yPos + 54, SUB_PAGE_WIDTH, 102, 21, null, buttons);
        beeList.setActive(false);
    }

    private void drawBeesList(PoseStack matrix, int xPos, int yPos) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.honey.bees_list");
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
    }

    private void drawEffectsList(PoseStack matrix, int xPos, int yPos) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.honey.effects_list");
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
        for (int i = 0; i < effects.size(); i++) {

            // init effect
            MobEffect effect = effects.get(i).getEffect();
            TranslatableComponent name = new TranslatableComponent(effect.getDescriptionId());
            int duration = effects.get(i).getDuration();
            name.append(new TextComponent(String.format(" (%02d:%02d)", (duration / 20) / 60, (duration / 20) % 60)));
            TextComponent chance = new TextComponent(new DecimalFormat("##%").format(effects.get(i).getChance()));
            int pos = yPos + 20 + (i * 21) + BeepediaScreen.currScreenState.getHoneyEffectsListPos();

            // create culling mask
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            double scale = beepedia.getMinecraft().getWindow().getGuiScale();
            int scissorY = (int) (beepedia.getMinecraft().getWindow().getHeight() - (this.yPos + 156) * scale);
            GL11.glScissor((int) (this.xPos * scale), scissorY, (int) (SUB_PAGE_WIDTH * scale), (int) ((102) * scale));

            // draw slot
            beepedia.drawEmptySlot(matrix, xPos, pos);
            // draw effect icon
            TextureAtlasSprite sprite = Minecraft.getInstance().getMobEffectTextures().get(effect);
            Minecraft.getInstance().getTextureManager().bind(sprite.atlas().location());
            GuiComponent.blit(matrix, xPos + 1, pos + 1, beepedia.getBlitOffset(), 18, 18, sprite);
            // draw text
            font.draw(matrix, name.withStyle(effect.isBeneficial() ? ChatFormatting.BLUE : ChatFormatting.RED), (float) xPos + 22, (float) pos + 1, -1);
            if (effects.get(i).getChance() < 1) {
                font.draw(matrix, chance.withStyle(ChatFormatting.DARK_GRAY), (float) xPos + 22, (float) pos + 11, -1);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    private void drawHungerBar(PoseStack matrix) {
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        manager.bind(hungerBar);
        GuiComponent.blit(matrix, xPos + 23, yPos + 21, 0, 0, 90, 9, 90, 9);
        int pipPosition = 105;
        int pipCounter = Math.min(hunger, 20);
        manager.bind(hungerIcons);
        while (pipCounter > 1) {
            GuiComponent.blit(matrix, xPos + pipPosition, yPos + 21, 0, 0, 9, 9, 9, 18);
            pipCounter -= 2;
            pipPosition -= 9;
        }
        if (pipCounter == 1) {
            GuiComponent.blit(matrix, xPos + pipPosition, yPos + 21, 0, 9, 9, 9, 9, 18);
        }
        float saturationWidth = Math.min(saturation * 90, 90);
        float saturationRemainder = 90 - saturationWidth;
        float saturationStart = 24 + saturationRemainder;
        manager.bind(saturationIcons);
        GuiComponent.blit(matrix, xPos + (int) saturationStart, yPos + 21, saturationRemainder, 0, (int) saturationWidth, 9, 90, 9);
    }

    @Override
    public void addSearch() {
        beeList.getSubList().forEach((s, b) -> {
            searchBees.add(s);
            searchBees.add(beepedia.getBee(s).getBee().getDisplayName().getString());
        });
        effects.forEach(e -> searchEffects.add(e.getEffect().getDisplayName().getString()));
        if (isDefault) {
            searchItems.add(new TranslatableComponent(Items.HONEY_BOTTLE.getDescriptionId()).getString());
            searchItems.add(new TranslatableComponent(Items.HONEY_BLOCK.getDescriptionId()).getString());
            searchItems.add(new TranslatableComponent(ModBlocks.HONEY_FLUID_BLOCK.get().getDescriptionId()).getString());
            searchItems.add(new TranslatableComponent(ModItems.HONEY_FLUID_BUCKET.get().getDescriptionId()).getString());
        }else {
            if (bottleData.getHoneyBottleRegistryObject() != null)
                searchItems.add(new TranslatableComponent(bottleData.getHoneyBottleRegistryObject().get().getDescriptionId()).getString());
            if (bottleData.getHoneyFluidBlockRegistryObject() != null)
                searchItems.add(new TranslatableComponent(bottleData.getHoneyFluidBlockRegistryObject().get().getDescriptionId()).getString());
            if (bottleData.getHoneyBlockItemRegistryObject() != null)
                searchItems.add(new TranslatableComponent(bottleData.getHoneyBlockItemRegistryObject().get().getDescriptionId()).getString());
            if (bottleData.getHoneyBucketItemRegistryObject() != null)
                searchItems.add(new TranslatableComponent(bottleData.getHoneyBucketItemRegistryObject().get().getDescriptionId()).getString());
            if (bottleData.getColor().isRainbow()) searchTags.add("isRainbow");
        }
        if (!effects.isEmpty()) searchTags.add("hasEffects");
        if (!beeList.getSubList().isEmpty()) searchTags.add("hasBees");
        searchAll.add(id);
        searchAll.addAll(searchItems);
        searchAll.addAll(searchBees);
        searchAll.addAll(searchTags);
    }

    public boolean getHoneyFromSearch(String search) {
        String[] params = search.split(" ");
        for (String param : params) {
            param = param.trim();
            switch (param.charAt(0)) {
                case '$':
                    if (!getSearch(searchBees, param.replaceFirst("\\$", ""))) return false;
                    break;
                case '#':
                    if (!getSearch(searchTags, param.replaceFirst("#", ""))) return false;
                    break;
                case '&':
                    if (!getSearch(searchItems, param.replaceFirst("&", ""))) return false;
                    break;
                case '!':
                    if (!getSearch(searchEffects, param.replaceFirst("=", ""))) return false;
                    break;
                default:
                    if (!getSearch(searchAll, param)) return false;
                    break;
            }
        }
        return true;
    }



    @Override
    public void openPage() {
        super.openPage();
        nextTab.visible = !effects.isEmpty();
        prevTab.visible = !effects.isEmpty();
        beeList.setActive(effects.isEmpty() || !BeepediaScreen.currScreenState.isHoneyEffectsActive());
        beeList.setScrollPos(BeepediaScreen.currScreenState.getHoneyBeeListPos());
        int iconHeight = 21;
        int effectsHeight = effects.size() * iconHeight;
        if (effectsHeight < LIST_HEIGHT) {
            BeepediaScreen.currScreenState.setHoneyEffectsListPos(0);
        } else if (BeepediaScreen.currScreenState.getHoneyEffectsListPos() > effectsHeight - LIST_HEIGHT) {
            BeepediaScreen.currScreenState.setHoneyEffectsListPos(effectsHeight - LIST_HEIGHT);
        }
    }

    @Override
    public void closePage() {
        super.closePage();
        nextTab.visible = false;
        prevTab.visible = false;
        if (beeList != null) beeList.setActive(false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        int startPos = 54;
        if (mouseX >= xPos && mouseY >= yPos + startPos && mouseX <= xPos + SUB_PAGE_WIDTH && mouseY <= yPos + startPos + LIST_HEIGHT) {
            if (effects.isEmpty() || !BeepediaScreen.currScreenState.isHoneyEffectsActive()) {
                beeList.updatePos((int) (scrollAmount * 8));
                BeepediaScreen.currScreenState.setHoneyBeeListPos(beeList.getScrollPos());
            } else {
                int scrollPos = BeepediaScreen.currScreenState.getHoneyEffectsListPos();
                int iconHeight = 21;
                int effectsHeight = effects.size() * iconHeight;
                if (effectsHeight < LIST_HEIGHT) return false;
                scrollPos += scrollAmount * 8;
                if (scrollPos > 0) scrollPos = 0;
                else if (scrollPos < -(effectsHeight - LIST_HEIGHT))
                    scrollPos = -(effectsHeight - LIST_HEIGHT);
                BeepediaScreen.currScreenState.setHoneyEffectsListPos(scrollPos);
            }
            return true;
        }
        return false;
    }
}
