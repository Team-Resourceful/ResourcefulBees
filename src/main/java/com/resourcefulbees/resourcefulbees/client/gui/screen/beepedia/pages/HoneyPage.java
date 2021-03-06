package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.honeydata.DefaultHoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyEffect;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ListButton;
import com.resourcefulbees.resourcefulbees.client.gui.widget.SubButtonList;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Foods;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.*;

public class HoneyPage extends BeepediaPage {

    private final ImageButton prevTab;
    private final ImageButton nextTab;
    private SubButtonList beeList = null;
    private final HoneyBottleData bottleData;
    private String honeySearch;
    private final TranslationTextComponent text;

    private final int hunger;
    private final float saturation;
    private final ItemStack bottle;
    private List<HoneyEffect> effects = new ArrayList<>();

    private static final int LIST_HEIGHT = 102;

    ResourceLocation hungerBar = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/hunger_bar.png");
    ResourceLocation hungerIcons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/hunger.png");
    ResourceLocation saturationIcons = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/saturation.png");

    public HoneyPage(BeepediaScreen beepedia, HoneyBottleData bottleData, String id, int left, int top) {
        super(beepedia, left, top, id);
        this.bottleData = bottleData;
        initSearch();
        if (bottleData instanceof DefaultHoneyBottleData) {
            DefaultHoneyBottleData data = (DefaultHoneyBottleData) bottleData;
            this.bottle = new ItemStack(data.bottle);
            this.hunger = Foods.HONEY_BOTTLE.getHealing();
            this.saturation = Foods.HONEY_BOTTLE.getSaturation();
            this.text = new TranslationTextComponent("fluid.resourcefulbees.honey");
        } else {
            this.bottle = new ItemStack(bottleData.getHoneyBottleRegistryObject().get());
            this.hunger = bottleData.getHunger();
            this.saturation = bottleData.getSaturation();
            this.effects = bottleData.getEffects();
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
    }

    private void toggleTab() {
        BeepediaScreen.currScreenState.setHoneyEffectsActive(!BeepediaScreen.currScreenState.isHoneyEffectsActive());
        beeList.setActive(effects.isEmpty() || !BeepediaScreen.currScreenState.isHoneyEffectsActive());
    }

    private void initSearch() {
        honeySearch = "";
        if (bottleData.getHoneyBottleRegistryObject() != null)
            honeySearch += " " + new TranslationTextComponent(bottleData.getHoneyBottleRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyFluidBlockRegistryObject() != null)
            honeySearch += " " + new TranslationTextComponent(bottleData.getHoneyFluidBlockRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyBlockItemRegistryObject() != null)
            honeySearch += " " + new TranslationTextComponent(bottleData.getHoneyBlockItemRegistryObject().get().getTranslationKey()).getString();
        if (bottleData.getHoneyBucketItemRegistryObject() != null)
            honeySearch += " " + new TranslationTextComponent(bottleData.getHoneyBucketItemRegistryObject().get().getTranslationKey()).getString();
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        if (beeList == null) return;
        beeList.updateList();
        beepedia.drawSlotNoToolTip(matrix, bottle, xPos, yPos + 10);
        beepedia.getMinecraft().textureManager.bindTexture(splitterImage);
        AbstractGui.drawTexture(matrix, xPos, yPos - 14, 0, 0, 165, 100, 165, 100);
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        font.draw(matrix, text.formatted(TextFormatting.WHITE), (float) xPos + 24, (float) yPos + 12, -1);
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
            ITextComponent translation = e.getValue().beeData.getTranslation();
            Button.IPressable onPress = button -> {
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

    private void drawBeesList(MatrixStack matrix, int xPos, int yPos) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.honey.bees_list");
        int padding = font.getWidth(title) / 2;
        font.draw(matrix, title.formatted(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
    }

    private void drawEffectsList(MatrixStack matrix, int xPos, int yPos) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.honey.effects_list");
        int padding = font.getWidth(title) / 2;
        font.draw(matrix, title.formatted(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
        for (int i = 0; i < effects.size(); i++) {

            // init effect
            Effect effect = effects.get(i).getEffect();
            TranslationTextComponent name = new TranslationTextComponent(effect.getName());
            int duration = effects.get(i).duration;
            name.append(new StringTextComponent(String.format(" (%02d:%02d)", (duration / 20) / 60, (duration / 20) % 60)));
            StringTextComponent chance = new StringTextComponent(new DecimalFormat("##%").format(effects.get(i).chance));
            int pos = yPos + 20 + (i * 21) + BeepediaScreen.currScreenState.getHoneyEffectsListPos();

            // create culling mask
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            double scale = beepedia.getMinecraft().getWindow().getGuiScaleFactor();
            int scissorY = (int) (beepedia.getMinecraft().getWindow().getFramebufferHeight() - (this.yPos + 156) * scale);
            GL11.glScissor((int) (this.xPos * scale), scissorY, (int) (SUB_PAGE_WIDTH * scale), (int) ((102) * scale));

            // draw slot
            beepedia.drawEmptySlot(matrix, xPos, pos);
            // draw effect icon
            TextureAtlasSprite sprite = Minecraft.getInstance().getPotionSpriteUploader().getSprite(effect);
            Minecraft.getInstance().getTextureManager().bindTexture(sprite.getAtlas().getId());
            AbstractGui.drawSprite(matrix, xPos + 1, pos + 1, beepedia.getZOffset(), 18, 18, sprite);
            // draw text
            font.draw(matrix, name.formatted(effect.isBeneficial() ? TextFormatting.BLUE : TextFormatting.RED), (float) xPos + 22, (float) pos + 1, -1);
            if (effects.get(i).chance < 1) {
                font.draw(matrix, chance.formatted(TextFormatting.DARK_GRAY), (float) xPos + 22, (float) pos + 11, -1);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    private void drawHungerBar(MatrixStack matrix) {
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        manager.bindTexture(hungerBar);
        AbstractGui.drawTexture(matrix, xPos + 23, yPos + 21, 0, 0, 90, 9, 90, 9);
        int pipPosition = 105;
        int pipCounter = Math.min(hunger, 20);
        manager.bindTexture(hungerIcons);
        while (pipCounter > 1) {
            AbstractGui.drawTexture(matrix, xPos + pipPosition, yPos + 21, 0, 0, 9, 9, 9, 18);
            pipCounter -= 2;
            pipPosition -= 9;
        }
        if (pipCounter == 1) {
            AbstractGui.drawTexture(matrix, xPos + pipPosition, yPos + 21, 0, 9, 9, 9, 9, 18);
        }
        float saturationWidth = Math.min(saturation * 90, 90);
        float saturationRemainder = 90 - saturationWidth;
        float saturationStart = 24 + saturationRemainder;
        manager.bindTexture(saturationIcons);
        AbstractGui.drawTexture(matrix, xPos + (int) saturationStart, yPos + 21, saturationRemainder, 0, (int) saturationWidth, 9, 90, 9);
    }

    @Override
    public String getSearch() {
        return honeySearch;
    }

    @Override
    public void openPage() {
        super.openPage();
        if (beeList == null) initBeeList();
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
