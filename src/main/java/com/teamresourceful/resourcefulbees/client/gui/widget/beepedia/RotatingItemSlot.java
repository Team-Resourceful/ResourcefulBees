package com.teamresourceful.resourcefulbees.client.gui.widget.beepedia;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.tooltip.ItemTooltip;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class RotatingItemSlot extends EmptySlot {

    private final Supplier<? extends Collection<ItemStack>> itemSupplier;
    private List<ItemStack> items;
    private ResourceLocation beeID;
    private int rotation;

    public RotatingItemSlot(int x, int y, int width, int height, CustomBeeData beeData, Supplier<? extends Collection<ItemStack>> items, boolean showNBT) {
        super(x, y, width, height, new StringTextComponent(""));
        this.itemSupplier = items;
        reset(beeData);
        ItemTooltip tooltip = new ItemTooltip(this.x, this.y, this.width, this.height, () -> this.items.get(rotation)).setDoNBT(showNBT);
        setTooltips(Collections.singletonList(tooltip));
    }

    public RotatingItemSlot(int x, int y, CustomBeeData beeData, Supplier<Set<ItemStack>> items, boolean showNBT) {
        this(x, y, 20, 20, beeData, items, showNBT);
    }

    public RotatingItemSlot(int x, int y, int width, int height, CustomBeeData beeData, Supplier<Set<ItemStack>> items) {
        this(x, y, width, height, beeData, items, true);
    }

    public RotatingItemSlot(int x, int y, CustomBeeData beeData, Supplier<Set<ItemStack>> items) {
        this(x, y, 20, 20, beeData, items, true);
    }

    private void reset(CustomBeeData beeData) {
        this.beeID = beeData.getRegistryID();
        this.items = new ArrayList<>(itemSupplier.get());
        this.rotation = 0;
    }

    public void tick(int tick, CustomBeeData beeData) {
        if (!beeData.getRegistryID().equals(beeID)) {
            reset(beeData);
        }
        if (items.isEmpty()) return;
        if (tick % 20 == 0 && !Screen.hasShiftDown()) {
            rotation++;
            if (rotation > items.size()) rotation = 0;
        }
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (items.isEmpty()) return;
        super.render(matrix, mouseX, mouseY, partialTicks);
        ItemSlot.renderItemStack(this, items.get(rotation));
    }
}