package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.api.IBeeCompat;
import com.teamresourceful.resourcefulbees.common.block.FlowHiveBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.FlowHiveRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlowHiveBlockEntity extends BeeHolderBlockEntity {

    private final FluidTank tank = new FluidTank(16000, fluid -> false) {
        @Override
        protected void onContentsChanged() {
            sendToPlayersTrackingChunk();
        }
    };
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    public FlowHiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FLOW_HIVE_ENTITY.get(), pos, state);
    }

    public FluidTank getTank() {
        return tank;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.FLUID_HANDLER)) return tankOptional.cast();
        return super.getCapability(cap, side);
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
            if (bee instanceof IBeeCompat compat) compat.nectarDroppedOff();
            FlowHiveRecipe.findRecipe(bee.level.getRecipeManager(), bee.getType())
                .ifPresent(recipe -> {
                    if (!tank.getFluid().isEmpty() && tank.getFluid().isFluidEqual(recipe.fluid())) {
                        tank.setFluid(new FluidStack(tank.getFluid(), Math.min(tank.getCapacity(), tank.getFluidAmount() + recipe.fluid().getAmount())));
                    } else if (!recipe.fluid().isEmpty()) {
                        tank.setFluid(new FluidStack(recipe.fluid(), Math.min(tank.getCapacity(), recipe.fluid().getAmount())));
                    }
                });
        }
    }

    @Override
    protected int getMaxTimeInHive(@NotNull IBeeCompat bee) {
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
        tank.readFromNBT(tag.getCompound(NBTConstants.NBT_TANK));
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundTag()));
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
    }
    //endregion
}
