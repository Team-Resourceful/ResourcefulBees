package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.ContentContainerBlock;
import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.config.SolidficationConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.SolidificationChamberMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.util.containers.AutomationSensitiveContainer;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidBlock;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.impl.InsertOnlyFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.WrappedBlockFluidContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SolidificationChamberBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker, BotariumFluidBlock<WrappedBlockFluidContainer>, ContentContainerBlock<PositionContent> {

    public static final int BLOCK_OUTPUT = 0;

    private WrappedBlockFluidContainer tank;

    private AutomationSensitiveContainer container;

    private boolean dirty;
    private int processingFill;

    private SolidificationRecipe cachedRecipe;
    private Fluid lastFluid;

    public SolidificationChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY.get(), pos, state);
    }

    public float getProcessPercent() {
        if (!canProcessHoney()) return 0;
        if (processingFill == SolidficationConfig.honeyProcessTime * SolidficationConfig.solidficationTimeMultiplier) return 1;
        return processingFill / ((float) SolidficationConfig.honeyProcessTime * SolidficationConfig.solidficationTimeMultiplier);
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return GuiTranslations.SOLIDIFICATION_CHAMBER;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        if (level == null) return null;
        return new SolidificationChamberMenu(id, playerInventory, this);
    }

    public boolean canProcessHoney() {
        if (level == null) return false;
        FluidHolder holder = getFluid();
        if (holder.isEmpty()) {
            cachedRecipe = null;
            lastFluid = null;
            return false;
        }
        ItemStack outputStack = getContainer().getItem(BLOCK_OUTPUT);
        final SolidificationRecipe recipe = holder.getFluid().equals(lastFluid) && cachedRecipe != null ?
                cachedRecipe : SolidificationRecipe.findRecipe(level.getRecipeManager(), holder.getFluid(), holder.getCompound()).orElse(null);
        if (recipe == null) return false;

        boolean isTankReady = !holder.isEmpty() && holder.getFluidAmount() >= recipe.fluid().amount();
        boolean canOutput = outputStack.isEmpty() || ItemStack.isSameItemSameTags(recipe.stack(), outputStack) && outputStack.getCount() < outputStack.getMaxStackSize();

        cachedRecipe = recipe;
        lastFluid = holder.getFluid();
        return isTankReady && canOutput;
    }

    public void processHoney() {
        FluidHolder recipeFluid = cachedRecipe.fluid().toHolder();
        FluidHolder holder = tank.internalExtract(recipeFluid, true);
        if (recipeFluid.getFluidAmount() != holder.getFluidAmount()) return;
        tank.internalExtract(recipeFluid, false);
        ItemStack outputStack = getContainer().getItem(BLOCK_OUTPUT);
        if (outputStack.isEmpty()) outputStack = cachedRecipe.stack().copy();
        else outputStack.grow(1);
        getContainer().setItemInternal(BLOCK_OUTPUT, outputStack);
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.canProcessHoney()) {
            if (this.processingFill >= SolidficationConfig.honeyProcessTime * SolidficationConfig.solidficationTimeMultiplier) {
                this.processHoney();
                this.processingFill = 0;
            }
            this.processingFill++;
        }

        if (this.dirty) {
            this.dirty = false;
            this.setChanged();
        }
    }

    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        tag.put(NBTConstants.NBT_TANK, tank.serialize(new CompoundTag()));
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        tank.deserialize(tag.getCompound(NBTConstants.NBT_TANK));
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sendToPlayersTrackingChunk();
    }

    @Override
    public WrappedBlockFluidContainer getFluidContainer() {
        if (tank == null) {
            tank = new WrappedBlockFluidContainer(this, new InsertOnlyFluidContainer(i -> FluidConstants.fromMillibuckets(16000), 1, (amount, fluid) -> this.level != null && SolidificationRecipe.matches(level.getRecipeManager(), fluid.getFluid(), fluid.getCompound())));
        }
        return this.tank;
    }

    public FluidHolder getFluid() {
        return getFluidContainer().getFluids().get(0);
    }

    @Override
    public AutomationSensitiveContainer getContainer() {
        if (container == null) {
            container = new Container(this, 2, player -> true);
        }
        return container;
    }

    @Override
    public PositionContent createContent() {
        return new PositionContent(this.getBlockPos());
    }

    protected static class Container extends AutomationSensitiveContainer {

        public Container(BlockEntity entity, int size, Predicate<Player> canPlayerAccess) {
            super(entity, size, canPlayerAccess);
        }

        @Override
        public boolean canAccept(int slot, ItemStack stack, boolean automation) {
            return false;
        }

        @Override
        public boolean canRemove(int slot, boolean automation) {
            return !automation || slot == BLOCK_OUTPUT;
        }
    }
}
