package com.teamresourceful.resourcefulbees.common.blockentities;


import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;
import com.teamresourceful.resourcefulbees.common.blockentities.base.ContentContainerBlock;
import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.CentrifugeBlock;
import com.teamresourceful.resourcefulbees.common.components.TankData;
import com.teamresourceful.resourcefulbees.common.menus.CentrifugeMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.FluidOutput;
import com.teamresourceful.resourcefulbees.common.recipes.centrifuge.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CentrifugeBlockEntity extends GUISyncedBlockEntity implements GeoBlockEntity, ContentContainerBlock<PositionContent> {

    private static final RawAnimation ROT_360 = RawAnimation.begin().thenPlay("animation.centrifuge.360");
    private static final RawAnimation ROT_45 = RawAnimation.begin().thenPlay("animation.centrifuge.45");
    private static final RawAnimation ROT_90 = RawAnimation.begin().thenPlay("animation.centrifuge.90");
    private static final RawAnimation ROT_135 = RawAnimation.begin().thenPlay("animation.centrifuge.135");
    private static final RawAnimation ROT_180 = RawAnimation.begin().thenPlay("animation.centrifuge.180");
    private static final RawAnimation ROT_225 = RawAnimation.begin().thenPlay("animation.centrifuge.225");
    private static final RawAnimation ROT_270 = RawAnimation.begin().thenPlay("animation.centrifuge.270");
    private static final RawAnimation ROT_315 = RawAnimation.begin().thenPlay("animation.centrifuge.315");

    private static final int SLOTS = 13;
    private static final int TANKS = 6;
    private static final int TANK_CAPACITY = 32000;

    private final CentrifugeItemResourceHandler inventory = new CentrifugeItemResourceHandler();
    private final CentrifugeFluidResourceHandler tank = new CentrifugeFluidResourceHandler();

    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private boolean firstCheck = true;

    private CentrifugeRecipe cachedRecipe;
    private int rotations = 0;
    private List<TankData> tankData = Collections.nCopies(TANKS, TankData.EMPTY);

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get(), pos, state);
    }

    public List<TankData> tankData() {
        return tankData;
    }

    public List<TankData> createTankDataPatch() {
        var list = new ArrayList<TankData>();
        for (int i = 0; i < tank.size(); i++) {
            list.add(new TankData(tank.fluidStack(i), tank.getCapacity()));
        }
        return list;
    }

    @Override
    public DataComponentPatch getSyncData() {
        return DataComponentPatch.builder()
                .set(ModDataComponents.TANK_DATA.get(), createTankDataPatch())
                .build();
    }

    @Override
    public <Data> void setSyncData(DataComponentType<Data> type, Optional<Data> data) {
        if (type == ModDataComponents.TANK_DATA.get()) {
            data.ifPresent(data1 -> tankData = (List<TankData>) data1);
        }
    }

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentGetter components) {
        super.applyImplicitComponents(components);
        tankData = components.getOrDefault(ModDataComponents.TANK_DATA.get(), new ArrayList<>());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NonNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.TANK_DATA.get(), createTankDataPatch());
    }

    @Override
    public void removeComponentsFromTag(@NonNull ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard("tank_data");
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("rotations", rotations);
        output.putChild("items", inventory);
        output.putChild("fluids", tank);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        rotations = input.getIntOr("rotations", 0);
        input.readChild("items", inventory);
        input.readChild("fluids", tank);
    }

    private void updateCachedRecipe() {
        if (level == null) {
            cachedRecipe = null;
            return;
        }
        firstCheck = false;
        var tempRecipe = CentrifugeRecipe.getRecipe(level, inventory.getResource(0).toStack());
        if (tempRecipe.isEmpty()) {
            cachedRecipe = null;
            return;
        }

        if (tempRecipe.get().value() != cachedRecipe) {
            rotations = 0;
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(CentrifugeBlock.ROTATION, 1));
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(CentrifugeBlock.USABLE, true));
        }
        cachedRecipe = tempRecipe.get().value();
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
        return cachedRecipe != null && (cachedRecipe.itemOutputs().isEmpty() || !inventory.isFull()) && (cachedRecipe.fluidOutputs().isEmpty() || !tank.isFull());
    }

    private void finishRecipe() {
        rotations = 0;
        if (cachedRecipe != null && level != null && !level.isClientSide()) {
            try(Transaction root = Transaction.openRoot()) {
                try(Transaction t1 = Transaction.open(root)) {
                    inventory.extract(0, inventory.getResource(0), 1, t1);
                    t1.commit();
                }
                try(Transaction t2 = Transaction.open(root)) {
                    cachedRecipe.itemOutputs()
                            .stream()
                            .filter(item -> level.getRandom().nextDouble() < item.chance())
                            .map(CentrifugeRecipe.Output::getRandomResult)
                            .map(ItemOutput::template)
                            .forEach(itemStack -> deliverItem(itemStack, t2));
                    t2.commit();
                }
                try(Transaction t3 = Transaction.open(root)) {
                    cachedRecipe.fluidOutputs()
                            .stream()
                            .filter(fluid -> level.getRandom().nextDouble() < fluid.chance())
                            .map(CentrifugeRecipe.Output::getRandomResult)
                            .map(FluidOutput::fluid)
                            .forEach(fluidStack -> deliverFluid(fluidStack, t3));
                    t3.commit();
                }
                root.commit();
            }
            updateCachedRecipe();
            level.playSound(null, this.worldPosition, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS);
        }
    }

    private void deliverItem(ItemStackTemplate stack, TransactionContext transactionContext) {
        inventory.insertOutput(ItemResource.of(stack), stack.count(), transactionContext);
    }

    private void deliverFluid(FluidStackTemplate fluid, TransactionContext transactionContext) {
        tank.internalInsert(FluidResource.of(fluid), fluid.amount(), transactionContext);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return CommonComponents.EMPTY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncID, @NotNull Inventory inventory, @NotNull Player player) {
        return new CentrifugeMenu(syncID, inventory, this);
    }

    //region Animation
    protected PlayState animationPredicate(AnimationTest<GeoAnimatable> animatable) {
        if (getLevel() != null) {
            RawAnimation animation = switch (getBlockState().getValue(CentrifugeBlock.ROTATION)) {
                case 2 -> ROT_45;
                case 3 -> ROT_90;
                case 4 -> ROT_135;
                case 5 -> ROT_180;
                case 6 -> ROT_225;
                case 7 -> ROT_270;
                case 8 -> ROT_315;
                default -> ROT_360;
            };
            animatable.controller().setAnimation(animation);
        }
        return PlayState.PAUSE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this::animationPredicate));
    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
    //endregion

    @Override
    public PositionContent createContent(ServerPlayer player) {
        return new PositionContent(getBlockPos());
    }

    @Override
    public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState state) {
        if (this.level != null) {
            Containers.dropContents(this.level, pos, this.inventory.copyToList());
            //todo can/should we drop fluid contents too?
        }
    }

    public CentrifugeItemResourceHandler itemResourceHandler() {
        return this.inventory;
    }

    public CentrifugeFluidResourceHandler fluidResourceHandler() {
        return this.tank;
    }

    public class CentrifugeItemResourceHandler extends ItemStacksResourceHandler {

        public CentrifugeItemResourceHandler() {
            super(SLOTS);
        }

        @Override
        public int insert(int index, @NonNull ItemResource resource, int amount, @NonNull TransactionContext transaction) {
            return index == 0 ? super.insert(index, resource, amount, transaction) : 0;
        }

        public void insertOutput(ItemResource resource, int amount, TransactionContext context) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

            int inserted = 0;
            int size = size();
            for (int index = 1; index < size; index++) {
                inserted += super.insert(index, resource, amount - inserted, context);
                if (inserted == amount) break;
            }
        }

        @Override
        public int extract(int index, @NonNull ItemResource resource, int amount, @NonNull TransactionContext transaction) {

            return super.extract(index, resource, amount, transaction);
            //return index != 0 ? super.extract(index, resource, amount, transaction) : 0;
        }

        public void extractInput(ItemResource resource, int amount, TransactionContext context) {
            super.extract(0, resource, amount, context);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
            CentrifugeBlockEntity.this.setChanged();
            CentrifugeBlockEntity.this.updateCachedRecipe();
        }

        public boolean isFull() {
            return stacks.stream().noneMatch(ItemStack::isEmpty);
        }
    }

    public class CentrifugeFluidResourceHandler extends FluidStacksResourceHandler {

        //todo make size dynamic to hold all fluids at once
        public CentrifugeFluidResourceHandler() {
            super(TANKS, TANK_CAPACITY);
        }

        @Override
        public int insert(@NonNull FluidResource resource, int amount, @NonNull TransactionContext transaction) {
            return 0;
        }

        public void internalInsert(FluidResource resource, int amount, TransactionContext context) {
            super.insert(resource, amount, context);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull FluidStack previousContents) {
            CentrifugeBlockEntity.this.setChanged();
            sendToListeningPlayers();
        }

        public boolean isFull() {
            return stacks.stream().noneMatch(FluidStack::isEmpty);
        }

        public FluidStack fluidStack(int index) {
            return stacks.get(index);
        }

        public int getCapacity() {
            return capacity;
        }
    }
}
