package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.SelectableFluidContainerHandler;
import com.teamresourceful.resourcefulbees.common.blocks.CentrifugeBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.menus.CentrifugeMenu;
import com.teamresourceful.resourcefulbees.common.recipes.base.RecipeFluid;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.util.ContainerUtils;
import com.teamresourceful.resourcefulbees.common.util.containers.AutomationSensitiveContainer;
import com.teamresourceful.resourcefulbees.common.util.containers.SelectableFluidContainer;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidBlock;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.impl.WrappedBlockFluidContainer;
import earth.terrarium.botarium.common.item.ItemContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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

import java.util.function.Predicate;

public class CentrifugeBlockEntity extends GUISyncedBlockEntity implements GeoBlockEntity, SelectableFluidContainerHandler, ItemContainerBlock, BotariumFluidBlock<WrappedBlockFluidContainer> {

    private static final RawAnimation ROT_360 = RawAnimation.begin().thenPlay("animation.centrifuge.360");
    private static final RawAnimation ROT_45 = RawAnimation.begin().thenPlay("animation.centrifuge.45");
    private static final RawAnimation ROT_90 = RawAnimation.begin().thenPlay("animation.centrifuge.90");
    private static final RawAnimation ROT_135 = RawAnimation.begin().thenPlay("animation.centrifuge.135");
    private static final RawAnimation ROT_180 = RawAnimation.begin().thenPlay("animation.centrifuge.180");
    private static final RawAnimation ROT_225 = RawAnimation.begin().thenPlay("animation.centrifuge.225");
    private static final RawAnimation ROT_270 = RawAnimation.begin().thenPlay("animation.centrifuge.270");
    private static final RawAnimation ROT_315 = RawAnimation.begin().thenPlay("animation.centrifuge.315");

    private AutomationSensitiveContainer container;
    private SelectableFluidContainer fluidContainer;
    private WrappedBlockFluidContainer wrappedFluidContainer;

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

    @Override
    public void setChanged() {
        super.setChanged();
        sendToPlayersTrackingChunk();
    }

    //region NBT
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(NBTConstants.ROTATIONS, rotations);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        rotations = tag.getInt(NBTConstants.ROTATIONS);
    }

    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBTConstants.ROTATIONS, rotations);
        wrappedFluidContainer.serialize(tag);
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        rotations = tag.getInt(NBTConstants.ROTATIONS);
        wrappedFluidContainer.deserialize(tag);
    }
    //endregion

    private void updateCachedRecipe() {
        firstCheck = false;
        var tempRecipe = CentrifugeRecipe.getRecipe(getLevel(), getContainer().getItem(0)).orElse(null);
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
        return cachedRecipe != null && (cachedRecipe.itemOutputs().isEmpty() || !outputFull) && (cachedRecipe.fluidOutputs().isEmpty() || !fluidContainer.isFull());
    }

    private void finishRecipe() {
        rotations = 0;
        if (cachedRecipe != null && level != null && !level.isClientSide()) {
            getContainer().getItem(0).shrink(1);
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
            outputFull = getContainer().items().stream().noneMatch(ItemStack::isEmpty);
            updateCachedRecipe();
        }
    }

    private void deliverItem(ItemStack stack) {
        for (int i = 1; i < 13 && !stack.isEmpty(); i++) {
            stack = ContainerUtils.internalInsertItem(getContainer(), i, stack);
        }
    }

    private void deliverFluid(RecipeFluid fluid) {
        wrappedFluidContainer.internalInsert(fluid.toHolder(), false);
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
    public void setFluid(FluidHolder fluid) {
        SelectableFluidContainerHandler.super.setFluid(fluid);
        sendToListeningPlayers();
    }

    @Override
    public SelectableFluidContainer getSelectableFluidContainer() {
        return fluidContainer;
    }

    @Override
    public AutomationSensitiveContainer getContainer() {
        if (container == null) {
            container = new Container(this);
        }
        return container;
    }

    @Override
    public WrappedBlockFluidContainer getFluidContainer() {
        if (wrappedFluidContainer == null) {
            fluidContainer = new FluidContainer(32000, fluid -> true);
            wrappedFluidContainer = new WrappedBlockFluidContainer(this, fluidContainer);
        }
        return wrappedFluidContainer;
    }

    private static class FluidContainer extends SelectableFluidContainer {

        public FluidContainer(long maxAmount, Predicate<FluidHolder> fluidFilter) {
            super(maxAmount, fluidFilter);
        }

        @Override
        public boolean allowsInsertion() {
            return false;
        }
    }

    private class Container extends AutomationSensitiveContainer {

        public Container(BlockEntity entity) {
            super(entity, 13, player -> true);
        }

        @Override
        public boolean canAccept(int slot, ItemStack stack, boolean automation) {
            return slot == 0 && CentrifugeRecipe.getRecipe(getLevel(), stack).isPresent();
        }

        @Override
        public boolean canRemove(int slot, boolean automation) {
            return !automation || slot != 0;
        }

        @Override
        public void setChanged(int slot) {
            super.setChanged(slot);
            if (slot == 0) {
                updateCachedRecipe();
            }
        }
    }
}
