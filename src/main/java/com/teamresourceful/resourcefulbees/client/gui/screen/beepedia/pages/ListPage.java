package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.*;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.enums.BeepediaListTypes;
import com.teamresourceful.resourcefulbees.client.gui.widget.ButtonTemplate;
import com.teamresourceful.resourcefulbees.client.gui.widget.ToggleItemImageButton;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ListPage extends BeepediaPage {

    private ToggleItemImageButton bees;
    private ToggleItemImageButton traits;
    private ToggleItemImageButton honey;
    private ToggleItemImageButton combs;

    public ListPage(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    private boolean isActive(BeepediaListTypes type) {
        return BeepediaState.currentState.selectedList == type;
    }

    @Override
    public void tick(int ticksActive) {
        super.tick(ticksActive);
        BeepediaListTypes type = BeepediaState.currentState.selectedList;
        bees.enabled = type == BeepediaListTypes.BEES;
        traits.enabled = type == BeepediaListTypes.TRAITS;
        honey.enabled = type == BeepediaListTypes.HONEY;
        combs.enabled = type == BeepediaListTypes.COMBS;
        bees.active = type != BeepediaListTypes.BEES;
        traits.active = type != BeepediaListTypes.TRAITS;
        honey.active = type != BeepediaListTypes.HONEY;
        combs.active = type != BeepediaListTypes.COMBS;
    }

    private void updateState(BeepediaListTypes type) {
        BeepediaState.updateState(type, null, null, null, null);
    }

    private ToggleItemImageButton createButton(int xOffset, int yOffset, ButtonTemplate template, BeepediaListTypes type, Component tooltip, Item item) {
        return new ToggleItemImageButton(xOffset, yOffset, 0, isActive(type),
                template, button -> updateState(type), tooltip, 2, 2, new ItemStack(item));
    }

    @Override
    public void registerScreen(BeepediaScreen beepedia) {
        super.registerScreen(beepedia);
        int x = this.x + 46;
        int y = this.y + 8;
        ButtonTemplate template = new ButtonTemplate(20, 20, 0, 0, 20, 20, 60, BeepediaImages.BUTTON_IMAGE);
        bees = createButton(x, y, template, BeepediaListTypes.BEES, BeepediaLang.TAB_BEES, Items.BEEHIVE);
        traits = createButton(x + 21, y, template, BeepediaListTypes.TRAITS, BeepediaLang.TAB_TRAITS, ModItems.TRAIT_ICON.get());
        honey = createButton(x + 42, y, template, BeepediaListTypes.HONEY, BeepediaLang.TAB_HONEY, Items.HONEY_BOTTLE);
        combs = createButton(x + 63, y, template, BeepediaListTypes.COMBS, BeepediaLang.TAB_COMBS, Items.HONEYCOMB);
        children.add(bees);
        children.add(traits);
        children.add(honey);
        children.add(combs);
    }

    @Override
    public void renderBackground(PoseStack matrix, float partialTick, int mouseX, int mouseY) {
        Component title;
        Font font = Minecraft.getInstance().font;
        switch (BeepediaState.currentState.selectedList) {
            case COMBS:
                title = BeepediaLang.TAB_COMBS;
                break;
            case HONEY:
                title = BeepediaLang.TAB_HONEY;
                break;
            case TRAITS:
                title = BeepediaLang.TAB_TRAITS;
                break;
            default:
                title = BeepediaLang.TAB_BEES;
                break;
        }
        font.draw(matrix, title, x + 10.0f, y + 20.0f, 16777215);
    }

    @Override
    public void renderForeground(PoseStack matrix, int mouseX, int mouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    @Override
    public void addSearch(BeepediaPage parent) {

    }
}
