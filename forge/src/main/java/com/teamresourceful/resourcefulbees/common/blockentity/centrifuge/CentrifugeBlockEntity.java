package com.teamresourceful.resourcefulbees.common.blockentity.centrifuge;

import com.teamresourceful.resourcefulbees.common.block.centrifuge.CentrifugeBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.base.SelectableFluidContainerHandler;
import com.teamresourceful.resourcefulbees.common.blockentity.base.InventorySyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.capabilities.SelectableMultiFluidTank;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.menus.CentrifugeMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class CentrifugeBlockEntity extends InventorySyncedBlockEntity implements IAnimatable, SelectableFluidContainerHandler {

    private final SelectableMultiFluidTank tank = new SelectableMultiFluidTank(32000, fluid -> false);
    private final LazyOptional<SelectableMultiFluidTank> tankOptional = LazyOptional.of(() -> tank);
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private boolean firstCheck = true;

    private CentrifugeRecipe cachedRecipe;
    private int rotations = 0;
    private boolean outputFull = false;

    public CentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get(), pos, state);
    }

    //region NBT
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        tag.put(NBTConstants.NBT_TANK, tank.serializeNBT());
        tag.putInt(NBTConstants.ROTATIONS, rotations);
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        tank.deserializeNBT(tag.getCompound(NBTConstants.NBT_TANK));
        rotations = tag.getInt(NBTConstants.ROTATIONS);
    }
    //endregion

    private void updateCachedRecipe() {
        firstCheck = false;
        var tempRecipe = CentrifugeUtils.getRecipe(getLevel(), getInventory().getStackInSlot(0)).orElse(null);
        if (tempRecipe != cachedRecipe) {
            rotations = 0;
            if (level != null) {
                level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(CentrifugeBlock.ROTATION, 1));
                level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(CentrifugeBlock.USABLE, tempRecipe != null));
            }
        }
        cachedRecipe = tempRecipe;
    }

    /**
     * This method is the method used by other blocks to activate this block, this block does not start to initiate any of the processing itself.
     */
    public int use() {
        if (canProcess()) {
            BlockState state = getBlockState();
            if (state.getValue(CentrifugeBlock.ROTATION) == 8) {
                rotations++;
            }
            if (rotations >= cachedRecipe.getRotations()) {
                finishRecipe();
                if (level != null)
                    level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(CentrifugeBlock.ROTATION, 1));
                return 1;
            } else {
                if (level != null) {
                    var cycle = getBlockState().cycle(CentrifugeBlock.ROTATION);
                    level.setBlockAndUpdate(getBlockPos(), cycle);
                    return cycle.getValue(CentrifugeBlock.ROTATION);
                }
            }
        } else {
            rotations = 0;
            if (level != null) {
                level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(CentrifugeBlock.ROTATION, 1));
            }
        }
        return 1;
    }

    private boolean canProcess() {
        if (cachedRecipe == null && firstCheck) updateCachedRecipe();
        return cachedRecipe != null && (cachedRecipe.itemOutputs().isEmpty() || !outputFull) && (cachedRecipe.fluidOutputs().isEmpty() || !tank.isFull());
    }

    private void finishRecipe() {
        rotations = 0;
        if (cachedRecipe != null && level != null && !level.isClientSide()) {
            getInventory().getStackInSlot(0).shrink(1);
            cachedRecipe.itemOutputs()
                    .stream()
                    .filter(item -> level.random.nextDouble() < item.chance())
                    .map(item -> item.pool().next())
                    .map(ItemOutput::itemStack)
                    .forEach(this::deliverItem);
            cachedRecipe.fluidOutputs()
                    .stream()
                    .filter(fluid -> level.random.nextDouble() < fluid.chance())
                    .map(fluid -> fluid.pool().next())
                    .map(FluidOutput::fluid)
                    .forEach(this::deliverFluid);
            outputFull = getInventory().getItems().stream().noneMatch(ItemStack::isEmpty);
            updateCachedRecipe();
        }
    }

    private void deliverItem(ItemStack stack) {
        for (int i = 1; i < 13 && !stack.isEmpty(); i++) {
            stack = ModUtils.insertItem(getInventory(), i, stack);
        }
    }

    private void deliverFluid(FluidStack stack) {
        tank.forceFill(stack, IFluidHandler.FluidAction.EXECUTE, true);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.FLUID_HANDLER)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return CommonComponents.EMPTY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncID, @NotNull Inventory inventory, @NotNull Player player) {
        if (level == null) return null;
        return new CentrifugeMenu(syncID, inventory, this);
    }

    @Override
    public AutomationSensitiveItemStackHandler createInventory() {
        return new TileStackHandler();
    }

    //region Animation
    protected  <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        int value = getBlockState().getValue(CentrifugeBlock.ROTATION);
        var animationBuilder = new AnimationBuilder();
        switch (value) {
            case 1 -> animationBuilder.addAnimation("animation.centrifuge.360", ILoopType.EDefaultLoopTypes.PLAY_ONCE).addAnimation("animation.centrifuge.0", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
            case 2 -> animationBuilder.addAnimation("animation.centrifuge.45", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
            case 3 -> animationBuilder.addAnimation("animation.centrifuge.90", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
            case 4 -> animationBuilder.addAnimation("animation.centrifuge.135", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
            case 5 -> animationBuilder.addAnimation("animation.centrifuge.180", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
            case 6 -> animationBuilder.addAnimation("animation.centrifuge.225", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
            case 7 -> animationBuilder.addAnimation("animation.centrifuge.270", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
            case 8 -> animationBuilder.addAnimation("animation.centrifuge.315", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
        }
        event.getController().setAnimation(animationBuilder);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
    //endregion

    @Override
    public void setFluid(int tank, FluidStack fluid) {
        SelectableFluidContainerHandler.super.setFluid(tank, fluid);
        sendToListeningPlayers();
    }

    @Override
    public SelectableMultiFluidTank getContainer(int tank) {
        return this.tank;
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler() {
            super(13, (slot, stack, automation) -> slot == 0, (slot, automation) -> !automation || slot != 0);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            if (slot == 0) {
                updateCachedRecipe();
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return CentrifugeUtils.getRecipe(getLevel(), stack).isPresent();
        }
    }
}
