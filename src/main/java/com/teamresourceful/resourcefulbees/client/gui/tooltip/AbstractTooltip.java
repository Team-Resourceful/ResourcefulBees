package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CoreData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
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

    public void draw(Screen screen, PoseStack matrix, int mouseX, int mouseY) {
        if (isHovered(mouseX, mouseY)) {
            if (Minecraft.getInstance().options.advancedItemTooltips) {
                screen.renderComponentTooltip(matrix, getAdvancedTooltip(), mouseX, mouseY);
            } else {
                screen.renderComponentTooltip(matrix, getTooltip(), mouseX, mouseY);
            }
        }
    }

    public abstract List<Component> getTooltip();

    public List<Component> getAdvancedTooltip() {
        return getTooltip();
    }

    protected List<Component> getNbtTooltips(CompoundTag nbt) {
        List<Component> tooltips = new LinkedList<>();
        if (!nbt.isEmpty()) {
            if (Screen.hasShiftDown()) {
                String tag = "ERROR"; // TODO nbt.getPrettyDisplay(" ", 0).getString();
                for (String s: tag.split("\n")) {
                    tooltips.add(new TextComponent(s).withStyle(ChatFormatting.DARK_PURPLE));
                }
            } else {
                tooltips.add(new TranslatableComponent("gui.resourcefulbees.jei.tooltip.show_nbt"));
            }
        }
        return tooltips;
    }

    public static List<Component> getBeeLore(EntityType<?> entityType, Level world) {
        Entity entity = entityType.create(world);
        if (entity instanceof CustomBeeEntity customBee){
            return getBeeLore(customBee.getCoreData());
        }else {
            return new LinkedList<>();
        }
    }

    public static List<Component> getBeeLore(Entity entity) {
        if (entity instanceof CustomBeeEntity customBee){
            return getBeeLore(customBee.getCoreData());
        }else {
            return new LinkedList<>();
        }
    }

    public static List<Component> getBeeLore(CoreData coreData) {
        return coreData.getLore();
    }
}
