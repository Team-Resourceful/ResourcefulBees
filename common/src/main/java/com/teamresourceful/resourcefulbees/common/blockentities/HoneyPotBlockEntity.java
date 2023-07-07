package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.menus.HoneyPotMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidBlock;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.WrappedBlockFluidContainer;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyPotBlockEntity extends GUISyncedBlockEntity implements BotariumFluidBlock<WrappedBlockFluidContainer>, ContentMenuProvider<PositionContent> {

    private WrappedBlockFluidContainer tank;

    public HoneyPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HONEY_POT_TILE_ENTITY.get(), pos, state);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new HoneyPotMenu(id, playerInventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return GuiTranslations.POT;
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
            tank = new WrappedBlockFluidContainer(this, new HoneyFluidContainer());
        }
        return this.tank;
    }

    public FluidHolder getFluid() {
        return tank.getFluids().get(0);
    }

    @Override
    public PositionContent createContent() {
        return new PositionContent(this.getBlockPos());
    }

    private static class HoneyFluidContainer extends SimpleFluidContainer {

        public HoneyFluidContainer() {
            super(i -> FluidHooks.buckets(64), 1, (i, holder) -> holder.is(ModFluidTags.HONEY));
        }

        @Override
        public long insertFluid(FluidHolder fluid, boolean simulate) {
            long inserted = super.insertFluid(fluid, simulate);
            return fluidFilter.test(0, fluid) ? fluid.getFluidAmount() : inserted;
        }
    }
}
