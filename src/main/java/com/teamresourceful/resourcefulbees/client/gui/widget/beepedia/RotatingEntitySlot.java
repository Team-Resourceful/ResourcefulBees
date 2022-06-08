package com.teamresourceful.resourcefulbees.client.gui.widget.beepedia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.tooltip.EntityTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class RotatingEntitySlot extends EmptySlot{

    private final Supplier<? extends Collection<EntityType<?>>> entitySupplier;
    private boolean showNBT;
    private List<Entity> entities;
    private ResourceLocation beeID;
    private int rotation;

    public RotatingEntitySlot(int x, int y, int width, int height, Supplier<? extends Collection<EntityType<?>>> entitySupplier, CustomBeeData beeData, boolean showNBT) {
        super(x, y, width, height, Component.literal(""));
        this.entitySupplier = entitySupplier;
        EntityTooltip tooltip = new EntityTooltip(this.x, this.y, this.width, this.height, () -> entities.get(rotation)).setDoNBT(showNBT);
        setTooltips(Collections.singletonList(tooltip));
        reset(beeData);
    }

    public RotatingEntitySlot(int x, int y, int width, int height, Supplier<? extends Collection<EntityType<?>>> entitySupplier, CustomBeeData beeData) {
        this(x, y, width, height, entitySupplier, beeData, true);
    }

    public RotatingEntitySlot(int x, int y, Supplier<? extends Collection<EntityType<?>>> entitySupplier, CustomBeeData beeData) {
        this(x, y, 20, 20, entitySupplier, beeData, true);
    }

    public RotatingEntitySlot(int x, int y, Supplier<? extends Collection<EntityType<?>>> entitySupplier, CustomBeeData beeData, boolean showNBT) {
        this(x, y, 20, 20, entitySupplier, beeData, showNBT);
    }

    private void reset(CustomBeeData beeData) {
        this.beeID = beeData.registryID();
        Minecraft minecraft = Minecraft.getInstance();
        this.entities = entitySupplier.get().stream().map(e -> {
            assert minecraft.level != null;
            return e.create(minecraft.level);
        }).collect(Collectors.toList());
        this.rotation = 0;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (entities.isEmpty()) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        EntitySlot.renderEntity(matrixStack, this, entities.get(rotation));
    }
}
