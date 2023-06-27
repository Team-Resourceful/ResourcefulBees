package com.teamresourceful.resourcefulbees.common.blockentity.centrifuge;

import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeUtils;
import com.teamresourceful.resourcefulbees.common.block.centrifuge.CentrifugeBlock;
import com.teamresourceful.resourcefulbees.common.blockentity.base.InventorySyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentity.base.SelectableFluidContainerHandler;
import com.teamresourceful.resourcefulbees.common.capabilities.SelectableMultiFluidTank;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.menus.CentrifugeMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.FluidUtils;
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
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CentrifugeBlockEntity extends InventorySyncedBlockEntity implements GeoBlockEntity, SelectableFluidContainerHandler {

    private static final RawAnimation ROT_360 = RawAnimation.begin().thenPlay("animation.centrifuge.360");
    private static final RawAnimation ROT_45 = RawAnimation.begin().thenPlay("animation.centrifuge.45");
    private static final RawAnimation ROT_90 = RawAnimation.begin().thenPlay("animation.centrifuge.90");
    private static final RawAnimation ROT_135 = RawAnimation.begin().thenPlay("animation.centrifuge.135");
    private static final RawAnimation ROT_180 = RawAnimation.begin().thenPlay("animation.centrifuge.180");
    private static final RawAnimation ROT_225 = RawAnimation.begin().thenPlay("animation.centrifuge.225");
    private static final RawAnimation ROT_270 = RawAnimation.begin().thenPlay("animation.centrifuge.270");
    private static final RawAnimation ROT_315 = RawAnimation.begin().thenPlay("animation.centrifuge.315");


    private final SelectableMultiFluidTank tank = new SelectableMultiFluidTank(32000, fluid -> false);
    private final LazyOptional<SelectableMultiFluidTank> tankOptional = LazyOptional.of(() -> tank);
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

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

    private void deliverFluid(RecipeFluid fluid) {
        tank.forceFill(FluidUtils.fromRecipe(fluid), IFluidHandler.FluidAction.EXECUTE, true);
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
    protected  <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        int value = getBlockState().getValue(CentrifugeBlock.ROTATION);
        RawAnimation animation = switch (value) {
            case 2 -> ROT_45;
            case 3 -> ROT_90;
            case 4 -> ROT_135;
            case 5 -> ROT_180;
            case 6 -> ROT_225;
            case 7 -> ROT_270;
            case 8 -> ROT_315;
            default -> ROT_360;
        };
        event.getController().setAnimation(animation);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
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
