package com.teamresourceful.resourcefulbees.client.gui.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CoreData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTooltip {

    private final int x;
    private final int y;
    private final int hoverWidth;
    private final int hoverHeight;

    protected AbstractTooltip(int x, int y, int hoverWidth, int hoverHeight) {
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
                Arrays.stream(NbtUtils.prettyPrint(nbt).split("\n"))
                        .map(TextComponent::new)
                        .map(c -> c.withStyle(ChatFormatting.DARK_PURPLE))
                        .forEach(tooltips::add);
            } else {
                tooltips.add(TranslationConstants.Jei.NBT.withStyle(ChatFormatting.DARK_PURPLE));
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
