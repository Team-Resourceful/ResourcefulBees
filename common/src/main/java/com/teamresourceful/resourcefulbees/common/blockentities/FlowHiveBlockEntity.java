package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.FlowHiveBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.recipes.FlowHiveRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidBlock;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.impl.ExtractOnlyFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.WrappedBlockFluidContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlowHiveBlockEntity extends BeeHolderBlockEntity implements BotariumFluidBlock<WrappedBlockFluidContainer> {

    private WrappedBlockFluidContainer tank;

    public FlowHiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FLOW_HIVE_ENTITY.get(), pos, state);
    }

    public WrappedBlockFluidContainer container() {
        return tank;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return CommonComponents.EMPTY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return null;
    }

    @Override
    protected void deliverNectar(CompoundTag nbt, Entity bee) {
        if (nbt.getBoolean("HasNectar")) {
            if (bee instanceof BeeCompat compat) compat.nectarDroppedOff();
            FlowHiveRecipe.findRecipe(bee.level().getRecipeManager(), bee.getType())
                .ifPresent(recipe -> {
                    if (!fluid().isEmpty() && recipe.fluid().matches(fluid().getFluid(), fluid().getCompound())) {
                        tank.internalInsert(recipe.fluid().toHolder(), false);
                    } else if (!recipe.fluid().isEmpty()) {
                        tank.internalInsert(recipe.fluid().toHolder(), false);
                    }
                });
        }
    }

    @Override
    protected int getMaxTimeInHive(@NotNull BeeCompat bee) {
        return (int) (bee.getMaxTimeInHive() * 0.5);
    }

    @Override
    public boolean hasSpace() {
        return beeCount() < 6;
    }

    @Override
    public boolean isAllowedBee() {
        return getBlockState().getBlock() instanceof FlowHiveBlock;
    }

    //region NBT HANDLING
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
    }
    //endregion


    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            sendToPlayersTrackingChunk();
        }
    }

    @Override
    public WrappedBlockFluidContainer getFluidContainer() {
        if (tank == null) {
            tank = new WrappedBlockFluidContainer(this, new ExtractOnlyFluidContainer(i -> FluidConstants.fromMillibuckets(16000), 1, (amount, fluid) -> true));
        }
        return this.tank;
    }

    public FluidHolder fluid() {
        return tank.getFluids().get(0);
    }
}
