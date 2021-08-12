package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.client.gui.widget.ListButton;
import com.resourcefulbees.resourcefulbees.client.gui.widget.SubButtonList;
import com.resourcefulbees.resourcefulbees.data.BeeTrait;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.lib.TraitConstants;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Collectors;

public class TraitPage extends BeepediaPage {

    public final BeeTrait trait;
    private final ImageButton prevTab;
    private final ImageButton nextTab;
    String translation;
    private SubButtonList list;
    TranslationTextComponent name;
    private final List<TraitSection> traitSections = new LinkedList<>();

    private static final int LIST_HEIGHT = 102;

    public TraitPage(BeepediaScreen beepedia, BeeTrait trait, String id, int left, int top) {
        super(beepedia, left, top, id);
        this.trait = trait;
        initTranslation();
        name = new TranslationTextComponent(trait.getTranslationKey());
        ItemStack stack = new ItemStack(trait.getBeepediaItem());
        newListButton(stack, name);
        prevTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) - 48, yPos + 40, 8, 11, 0, 0, 11, arrowImage, 16, 33, button -> toggleTab());
        nextTab = new ImageButton(xPos + (SUB_PAGE_WIDTH / 2) + 40, yPos + 40, 8, 11, 8, 0, 11, arrowImage, 16, 33, button -> toggleTab());
        beepedia.addButton(nextTab);
        beepedia.addButton(prevTab);
        nextTab.visible = false;
        prevTab.visible = false;
        addSpecialAbilities();
        addDamageImmunities();
        addPotionImmunities();
        addPotionDamageEffects();
        addDamageTypes();
        addParticle();
    }

    private void addParticle() {
        if (trait.hasParticleEffect()) {
            TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.particle");
            StringTextComponent text = new StringTextComponent(trait.getParticleEffect().writeToString());
            traitSections.add(new TraitSection(title, new ItemStack(Items.FIREWORK_ROCKET), text));
        }
    }

    private void addDamageTypes() {
        if (trait.hasDamageTypes()) {
            TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.damageTypes");
            StringTextComponent text = new StringTextComponent("");
            for (int i = 0; i < trait.getDamageTypes().size(); i++) {
                Pair<String, Integer> damage = trait.getDamageTypes().get(i);
                text.append(damage.getKey() + " ");
                text.append(new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.amplifier"));
                text.append(damage.getRight().toString());
                if (i != trait.getPotionDamageEffects().size() - 1) {
                    text.append(", ");
                }
            }
            traitSections.add(new TraitSection(title, new ItemStack(Items.IRON_SWORD), text));
        }
    }

    private void addPotionDamageEffects() {
        if (trait.hasDamagePotionEffects()) {
            TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.potion_damage_effects");
            StringTextComponent text = new StringTextComponent("");
            for (int i = 0; i < trait.getPotionDamageEffects().size(); i++) {
                Pair<Effect, Integer> effect = trait.getPotionDamageEffects().get(i);
                text.append(effect.getKey().getDisplayName());
                text.append(" ");

                if (effect.getRight() > 0) text.append(new StringTextComponent(effect.getRight().toString()));
                if (i != trait.getPotionDamageEffects().size() - 1) {
                    text.append(", ");
                }
            }
            traitSections.add(new TraitSection(title, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HARMING), text));
        }
    }

    private void addDamageImmunities() {
        if (trait.hasDamageImmunities()) {
            TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.damage_immunities");
            String typeList = String.join(", ", trait.getDamageImmunities());
            traitSections.add(new TraitSection(title, new ItemStack(Items.IRON_CHESTPLATE), new StringTextComponent(typeList)));
        }
    }

    private void addPotionImmunities() {
        if (trait.hasPotionImmunities()) {
            TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.potion_immunities");
            List<ITextComponent> effectNames = trait.getPotionImmunities().stream().map(Effect::getDisplayName).collect(Collectors.toList());
            StringTextComponent text = new StringTextComponent("");
            for (int i = 0; i < effectNames.size(); i++) {
                text.append(effectNames.get(i));
                if (i != effectNames.size() - 1) {
                    text.append(", ");
                }
            }
            traitSections.add(new TraitSection(title, new ItemStack(Items.MILK_BUCKET), text));
        }
    }

    private void addSpecialAbilities() {
        if (trait.hasSpecialAbilities()) {
            trait.getSpecialAbilities().forEach(s -> {
                TranslationTextComponent title = new TranslationTextComponent(String.format("trait.%s.%s", ResourcefulBees.MOD_ID, s));
                TranslationTextComponent text = new TranslationTextComponent(String.format("trait.%s.special.%s", ResourcefulBees.MOD_ID, s));
                ItemStack item;
                switch (s) {
                    case TraitConstants.FLAMMABLE:
                        item = new ItemStack(Items.FIRE_CHARGE);
                        break;
                    case TraitConstants.SLIMY:
                        item = new ItemStack(Items.SLIME_BALL);
                        break;
                    case TraitConstants.ANGRY:
                        item = new ItemStack(Items.BLAZE_POWDER);
                        break;
                    case TraitConstants.TELEPORT:
                        item = new ItemStack(Items.ENDER_PEARL);
                        break;
                    case TraitConstants.SPIDER:
                        item = new ItemStack(Items.COBWEB);
                        break;
                    default:
                        item = new ItemStack(Items.BARRIER);
                        break;
                }
                traitSections.add(new TraitSection(title, item, text));
            });
        }
    }

    private void toggleTab() {
        BeepediaScreen.currScreenState.setTraitsEffectsActive(!BeepediaScreen.currScreenState.isTraitsEffectsActive());
        list.setActive(!BeepediaScreen.currScreenState.isTraitsEffectsActive());
    }

    private void initTranslation() {
        translation = "";
        translation += String.join(" ", trait.getDamageImmunities());
        translation += String.join(" ", trait.getSpecialAbilities());
        translation += trait.getPotionImmunities().stream().map(effect -> effect.getDisplayName().getString()).collect(Collectors.joining(" "));
        translation += trait.getDamageTypes().stream().map(Pair::getLeft).collect(Collectors.joining(" "));
        translation += trait.getPotionDamageEffects().stream().map(pair -> pair.getLeft().getDisplayName().getString()).collect(Collectors.joining(" "));
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        if (list == null) return;
        list.updateList();
        beepedia.drawSlotNoToolTip(matrix, trait.getBeepediaItem(), xPos, yPos + 10);
        beepedia.getMinecraft().textureManager.bind(splitterImage);
        AbstractGui.blit(matrix, xPos, yPos - 14, 0, 0, 165, 100, 165, 100);
        FontRenderer font = Minecraft.getInstance().font;
        StringTextComponent key = new StringTextComponent(id);
        font.draw(matrix, name.withStyle(TextFormatting.WHITE), (float) xPos + 24, (float) yPos + 12, -1);
        font.draw(matrix, key.withStyle(TextFormatting.DARK_GRAY), (float) xPos + 24, (float) yPos + 22, -1);
        if (BeepediaScreen.currScreenState.isTraitsEffectsActive()) {
            drawEffectsList(matrix, xPos, yPos + 34);
        } else {
            drawBeesList(matrix, xPos, yPos + 34);
        }
    }

    private void drawBeesList(MatrixStack matrix, int xPos, int yPos) {
        FontRenderer font = Minecraft.getInstance().font;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.bees_list");
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
    }

    private void drawEffectsList(MatrixStack matrix, int xPos, int yPos) {
        FontRenderer font = Minecraft.getInstance().font;
        TranslationTextComponent title = new TranslationTextComponent("gui.resourcefulbees.beepedia.tab.traits.effects_list");
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(TextFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = beepedia.getMinecraft().getWindow().getGuiScale();
        int scissorY = (int) (beepedia.getMinecraft().getWindow().getHeight() - (this.yPos + 156) * scale);
        GL11.glScissor((int) (this.xPos * scale), scissorY, (int) (SUB_PAGE_WIDTH * scale), (int) ((102) * scale));

        int sectionPos = yPos + 20;
        for (int i = 0; i < traitSections.size(); i++) {
            if (i > 0) {
                sectionPos += traitSections.get(i - 1).getHeight();
            }
            traitSections.get(i).draw(matrix, xPos, sectionPos, BeepediaScreen.currScreenState.getTraitEffectsListPos());
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void initList() {
        Map<String, BeePage> beePages = beepedia.getBees(id);
        SortedMap<String, ListButton> buttons = new TreeMap<>();
        for (Map.Entry<String, BeePage> e : beePages.entrySet()) {
            ItemStack stack = new ItemStack(ModItems.BEE_JAR.get());
            BeeJar.fillJar(stack, e.getValue().beeData);
            Button.IPressable onPress = button -> {
                BeepediaScreen.saveScreenState();
                beepedia.setActive(BeepediaScreen.PageType.BEE, e.getKey());
            };
            ListButton button = new ListButton(0, 0, 100, 20, 0, 0, 20, listImage, stack, 2, 2, e.getValue().beeData.getTranslation(), 22, 6, onPress);
            beepedia.addButton(button);
            button.visible = false;
            buttons.put(e.getKey(), button);
        }
        list = new SubButtonList(xPos, yPos + 54, SUB_PAGE_WIDTH, 102, 21, null, buttons);
        list.setActive(false);
    }

    @Override
    public String getSearch() {
        return translation;
    }

    @Override
    public void openPage() {
        super.openPage();
        if (list == null) initList();
        list.setActive(!BeepediaScreen.currScreenState.isTraitsEffectsActive());
        list.setScrollPos(BeepediaScreen.currScreenState.getTraitBeeListPos());
        nextTab.visible = true;
        prevTab.visible = true;
        int effectsHeight = traitSections.stream().mapToInt(TraitSection::getHeight).sum();
        if (effectsHeight < LIST_HEIGHT) {
            BeepediaScreen.currScreenState.setTraitEffectsListPos(0);
        } else if (BeepediaScreen.currScreenState.getTraitEffectsListPos() > effectsHeight - LIST_HEIGHT) {
            BeepediaScreen.currScreenState.setTraitEffectsListPos(effectsHeight - LIST_HEIGHT);
        }
    }

    @Override
    public void closePage() {
        super.closePage();
        list.setActive(false);
        nextTab.visible = false;
        prevTab.visible = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        int startPos = 54;
        if (mouseX >= xPos && mouseY >= yPos + startPos && mouseX <= xPos + SUB_PAGE_WIDTH && mouseY <= yPos + startPos + LIST_HEIGHT) {
            if (!BeepediaScreen.currScreenState.isTraitsEffectsActive()) {
                list.updatePos((int) (scrollAmount * 8));
                BeepediaScreen.currScreenState.setTraitBeeListPos(list.getScrollPos());
            } else {
                return addScrollPos(scrollAmount * 8);
            }
            return true;
        }
        return false;
    }

    private boolean addScrollPos(double v) {
        int scrollPos = BeepediaScreen.currScreenState.getTraitEffectsListPos();
        int effectsHeight = traitSections.stream().mapToInt(TraitSection::getHeight).sum();
        if (effectsHeight < LIST_HEIGHT) return false;
        scrollPos += v;
        if (scrollPos > 0) scrollPos = 0;
        else if (scrollPos < -(effectsHeight - LIST_HEIGHT))
            scrollPos = -(effectsHeight - LIST_HEIGHT);
        BeepediaScreen.currScreenState.setTraitEffectsListPos(scrollPos);
        return true;
    }

    private class TraitSection {
        FontRenderer font;
        ITextComponent title;
        ItemStack displaySlot;
        ITextComponent text;
        int width = BeepediaPage.SUB_PAGE_WIDTH;

        public TraitSection(ITextComponent title, ItemStack displaySlot, ITextComponent text) {
            this.title = new StringTextComponent(title.getString()).withStyle(TextFormatting.WHITE);
            this.displaySlot = displaySlot;
            this.text = new StringTextComponent(text.getString()).withStyle(TextFormatting.GRAY);
            font = Minecraft.getInstance().font;
        }

        public void draw(MatrixStack matrix, int xPos, int yPos, int scrollPos) {
            beepedia.drawSlotNoToolTip(matrix, displaySlot, xPos, yPos + scrollPos);
            font.draw(matrix, title, xPos + 24F, yPos + 6F + scrollPos, -1);
            List<IReorderingProcessor> lines = font.split(text, width);
            for (int i = 0; i < lines.size(); i++) {
                IReorderingProcessor line = lines.get(i);
                font.draw(matrix, line, xPos, yPos + 24F + i * font.lineHeight + scrollPos, -1);
            }
        }

        public int getHeight() {
            return font.wordWrapHeight(text.getString(), width) + 30;
        }
    }

}
