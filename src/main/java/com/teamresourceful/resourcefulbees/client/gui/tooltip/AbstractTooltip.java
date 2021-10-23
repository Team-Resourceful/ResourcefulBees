package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CoreData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTooltip {

    private final int x;
    private final int y;
    private final int hoverWidth;
    private final int hoverHeight;

    public AbstractTooltip(int x, int y, int hoverWidth, int hoverHeight) {
        this.x = x;
        this.y = y;
        this.hoverWidth = hoverWidth;
        this.hoverHeight = hoverHeight;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + this.hoverWidth) && mouseY < (double) (this.y + this.hoverHeight);
    }

    public void draw(Screen screen, MatrixStack matrix, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            if (Minecraft.getInstance().options.advancedItemTooltips) {
                screen.renderComponentTooltip(matrix, getAdvancedTooltip(), mouseX, mouseY);
            } else {
                screen.renderComponentTooltip(matrix, getTooltip(), mouseX, mouseY);
            }
        }
    }

    public abstract List<ITextComponent> getTooltip();

    public List<ITextComponent> getAdvancedTooltip() {
        return getTooltip();
    }

    protected List<ITextComponent> getNbtTooltips(CompoundNBT nbt) {
        List<ITextComponent> tooltips = new LinkedList<>();
        if (!nbt.isEmpty()) {
            if (Screen.hasShiftDown()) {
                String tag = nbt.getPrettyDisplay(" ", 0).getString();
                for (String s: tag.split("\n")) {
                    tooltips.add(new StringTextComponent(s).withStyle(TextFormatting.DARK_PURPLE));
                }
            } else {
                tooltips.add(new TranslationTextComponent("gui.resourcefulbees.jei.tooltip.show_nbt"));
            }
        }
        return tooltips;
    }

    public static List<ITextComponent> getBeeLore(EntityType<?> entityType, World world) {
        Entity entity = entityType.create(world);
        if (entity instanceof CustomBeeEntity){
            return getBeeLore(((CustomBeeEntity) entity).getCoreData());
        }else {
            return new LinkedList<>();
        }
    }

    public static List<ITextComponent> getBeeLore(Entity entity) {
        if (entity instanceof CustomBeeEntity){
            return getBeeLore(((CustomBeeEntity) entity).getCoreData());
        }else {
            return new LinkedList<>();
        }
    }

    public static List<ITextComponent> getBeeLore(CoreData coreData) {
        List<ITextComponent> tooltip = new LinkedList<>();
        if (coreData.getLore().isPresent()) { //TODO Optional#isPresent fix
            String lore = coreData.getLore().get();
            String[] loreTooltip = lore.split("\\r?\\n");
            for (String s: loreTooltip) {
                tooltip.add(new StringTextComponent(s).withStyle(coreData.getLoreColor().getAsStyle()));
            }
        }
        if (coreData.getCreator().isPresent()) {
            tooltip.add(BeeConstants.CREATOR_LORE_PREFIX.copy().append(coreData.getCreator().get()).withStyle(TextFormatting.GRAY));
        }
        return tooltip;
    }
}
