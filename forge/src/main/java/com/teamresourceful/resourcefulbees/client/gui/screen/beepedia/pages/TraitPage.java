package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaPage;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.widget.ListButton;
import com.teamresourceful.resourcefulbees.client.gui.widget.SubButtonList;
import com.teamresourceful.resourcefulbees.api.beedata.BeeTrait;
import com.teamresourceful.resourcefulbees.item.BeeJar;
import com.teamresourceful.resourcefulbees.lib.TraitConstants;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Collectors;

public class TraitPage extends BeepediaPage {

    public final BeeTrait trait;
    private final ImageButton prevTab;
    private final ImageButton nextTab;
    private SubButtonList list;
    final TranslatableComponent name;
    private final List<TraitSection> traitSections = new LinkedList<>();

    private final List<String> searchImmunities = new LinkedList<>();
    private final List<String> searchDamage = new LinkedList<>();
    private final List<String> searchSpecial = new LinkedList<>();
    private final List<String> searchBees = new LinkedList<>();
    private List<String> searchAll = new LinkedList<>();

    private static final int LIST_HEIGHT = 102;

    public TraitPage(BeepediaScreen beepedia, BeeTrait trait, String id, int left, int top) {
        super(beepedia, left, top, id);
        this.trait = trait;
        name = new TranslatableComponent(trait.getTranslationKey());
        ItemStack stack = trait.getDisplayItem().getDefaultInstance();
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
        initList();
    }

    private void addParticle() {
        if (trait.hasParticleEffects()) {
            TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.particle");
            TextComponent text = new TextComponent(trait.getParticleEffects().iterator().next().getRegistryName().toString()); ///MOST LIKELY NEEDS TO BE FIXED/IS BROKEN
            traitSections.add(new TraitSection(title, new ItemStack(Items.FIREWORK_ROCKET), text));
        }
    }

    private void addDamageTypes() {
        if (trait.hasDamageTypes()) {
            TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.damageTypes");
            TextComponent text = new TextComponent("");
            trait.getDamageTypes().forEach(damageType -> {
                text.append(damageType.getType() + " ");
                text.append(new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.amplifier"));
                text.append(String.valueOf(damageType.getAmplifier()));
                text.append(", ");
            });
            traitSections.add(new TraitSection(title, new ItemStack(Items.IRON_SWORD), text));
        }
    }

    private void addPotionDamageEffects() {
        if (trait.hasDamagePotionEffects()) {
            TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.potion_damage_effects");
            TextComponent text = new TextComponent("");
            trait.getPotionDamageEffects().forEach(effect -> {
                text.append(effect.getEffect().getDisplayName());
                text.append(" ");
                if (effect.getStrength() > 0) text.append(String.valueOf(effect.getStrength()));
                text.append(", ");
            });
            traitSections.add(new TraitSection(title, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HARMING), text));
        }
    }

    private void addDamageImmunities() {
        if (trait.hasDamageImmunities()) {
            TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.damage_immunities");
            String typeList = String.join(", ", trait.getDamageImmunities());
            traitSections.add(new TraitSection(title, new ItemStack(Items.IRON_CHESTPLATE), new TextComponent(typeList)));
        }
    }

    private void addPotionImmunities() {
        if (trait.hasPotionImmunities()) {
            TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.potion_immunities");
            List<Component> effectNames = trait.getPotionImmunities().stream().map(MobEffect::getDisplayName).collect(Collectors.toList());
            TextComponent text = new TextComponent("");
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
                TranslatableComponent title = new TranslatableComponent(String.format("trait.%s.%s", ResourcefulBees.MOD_ID, s));
                TranslatableComponent text = new TranslatableComponent(String.format("trait.%s.special.%s", ResourcefulBees.MOD_ID, s));
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

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        if (list == null) return;
        list.updateList();
        beepedia.drawSlotNoToolTip(matrix, trait.getDisplayItem(), xPos, yPos + 10);
        beepedia.getMinecraft().textureManager.bind(splitterImage);
        GuiComponent.blit(matrix, xPos, yPos - 14, 0, 0, 186, 100, 186, 100);
        Font font = Minecraft.getInstance().font;
        TextComponent key = new TextComponent(id);
        font.draw(matrix, name.withStyle(ChatFormatting.WHITE), (float) xPos + 24, (float) yPos + 12, -1);
        font.draw(matrix, key.withStyle(ChatFormatting.DARK_GRAY), (float) xPos + 24, (float) yPos + 22, -1);
        if (BeepediaScreen.currScreenState.isTraitsEffectsActive()) {
            drawEffectsList(matrix, xPos, yPos + 34);
        } else {
            drawBeesList(matrix, xPos, yPos + 34);
        }
    }

    private void drawBeesList(PoseStack matrix, int xPos, int yPos) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.bees_list");
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);
    }

    private void drawEffectsList(PoseStack matrix, int xPos, int yPos) {
        Font font = Minecraft.getInstance().font;
        TranslatableComponent title = new TranslatableComponent("gui.resourcefulbees.beepedia.tab.traits.effects_list");
        int padding = font.width(title) / 2;
        font.draw(matrix, title.withStyle(ChatFormatting.WHITE), (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - padding, (float) yPos + 8, -1);

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
            Button.OnPress onPress = button -> {
                BeepediaScreen.saveScreenState();
                beepedia.setActive(BeepediaScreen.PageType.BEE, e.getKey());
            };
            ListButton button = new ListButton(0, 0, 100, 20, 0, 0, 20, listImage, stack, 2, 2, e.getValue().beeData.getDisplayName(), 22, 6, onPress);
            beepedia.addButton(button);
            button.visible = false;
            buttons.put(e.getKey(), button);
        }
        list = new SubButtonList(xPos, yPos + 54, SUB_PAGE_WIDTH, 102, 21, null, buttons);
        list.setActive(false);
    }


    @Override
    public void addSearch() {
        list.getSubList().forEach((s, b) -> {
            searchBees.add(s);
            searchBees.add(beepedia.getBee(s).getBee().getDisplayName().getString());
        });
        trait.getDamageTypes().forEach(damageType -> searchDamage.add(damageType.getType()));
        trait.getPotionDamageEffects().forEach(effect -> searchDamage.add(effect.getEffect().getDisplayName().getString()));
        trait.getPotionImmunities().forEach(p -> searchImmunities.add(p.getDisplayName().getString()));
        searchImmunities.addAll(trait.getDamageImmunities());
        searchSpecial.addAll(trait.getSpecialAbilities());
        searchAll.add(id);
        searchAll.addAll(searchDamage);
        searchAll.addAll(searchSpecial);
        searchAll.addAll(searchBees);
        searchAll.addAll(searchImmunities);
        searchAll = searchAll.stream().distinct().collect(Collectors.toList());
    }

    public boolean getTraitFromSearch(String search) {
        String[] params = search.split(" ");
        for (String param : params) {
            param = param.trim();
            switch (param.charAt(0)) {
                case '$':
                    if (!getSearch(searchBees, param.replaceFirst("\\$", ""))) return false;
                    break;
                case '#':
                    if (!getSearch(searchSpecial, param.replaceFirst("#", ""))) return false;
                    break;
                case '!':
                    if (!getSearch(searchImmunities, param.replaceFirst("!", ""))) return false;
                    break;
                case '&':
                    if (!getSearch(searchDamage, param.replaceFirst("&", ""))) return false;
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
        final Font font;
        final Component title;
        final ItemStack displaySlot;
        final Component text;
        final int width = BeepediaPage.SUB_PAGE_WIDTH;

        public TraitSection(Component title, ItemStack displaySlot, Component text) {
            this.title = new TextComponent(title.getString()).withStyle(ChatFormatting.WHITE);
            this.displaySlot = displaySlot;
            this.text = new TextComponent(text.getString()).withStyle(ChatFormatting.GRAY);
            font = Minecraft.getInstance().font;
        }

        public void draw(PoseStack matrix, int xPos, int yPos, int scrollPos) {
            beepedia.drawSlotNoToolTip(matrix, displaySlot, xPos, yPos + scrollPos);
            font.draw(matrix, title, xPos + 24F, yPos + 6F + scrollPos, -1);
            List<FormattedCharSequence> lines = font.split(text, width);
            for (int i = 0; i < lines.size(); i++) {
                FormattedCharSequence line = lines.get(i);
                font.draw(matrix, line, xPos, yPos + 24F + i * font.lineHeight + scrollPos, -1);
            }
        }

        public int getHeight() {
            return font.wordWrapHeight(text.getString(), width) + 30;
        }
    }

}
