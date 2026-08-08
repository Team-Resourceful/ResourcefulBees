package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.FlowHiveBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.recipes.FlowHiveRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class FlowHiveBlockEntity extends BeeHolderBlockEntity {

    private final FlowHiveFluidHandler fluidHandler = new FlowHiveFluidHandler();

    public FlowHiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FLOW_HIVE_ENTITY.get(), pos, state);
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
    protected void deliverNectar(boolean hasNectar, Entity bee) {
        if (hasNectar) {
            if (bee instanceof BeeCompat compat) compat.resourcefulBees$nectarDroppedOff();
            FlowHiveRecipe.findRecipe((RecipeManager) bee.level().recipeAccess(), bee.getType(), bee.level())
                    .map(RecipeHolder::value)
                    .ifPresent(recipe -> {
                        try(Transaction transaction = Transaction.openRoot()) {
                            fluidHandler.insertOutput(FluidResource.of(recipe.fluid()), recipe.fluid().amount(), transaction);
                            transaction.commit();
                        }
            });
        }
    }

    @Override
    protected int getMaxTimeInHive(@NotNull BeeCompat bee) {
        return (int) (bee.resourcefulBees$getMaxTimeInHive() * 0.5);
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
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        fluidHandler.deserialize(input);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        fluidHandler.serialize(output);
    }
    //endregion


    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            sendToPlayersTrackingChunk();
        }
    }

    public FlowHiveFluidHandler fluidHandler() {
        return fluidHandler;
    }

    public static class FlowHiveFluidHandler extends FluidStacksResourceHandler {

        public FlowHiveFluidHandler() {
            super(1, 32000);
        }

        @Override
        public int insert(@NonNull FluidResource resource, int amount, @NonNull TransactionContext transaction) {
            return 0;
        }

        public void insertOutput(FluidResource resource, int amount, TransactionContext transaction) {
            super.insert(0, resource, amount, transaction);
        }
    }
}
