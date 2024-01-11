package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.ContentContainerBlock;
import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.HoneyGenerator;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.config.HoneyGenConfig;
import com.teamresourceful.resourcefulbees.common.items.upgrade.Upgrade;
import com.teamresourceful.resourcefulbees.common.items.upgrade.UpgradeType;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.menus.HoneyGeneratorMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.recipes.HoneyGenRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.util.containers.AutomationSensitiveContainer;
import earth.terrarium.botarium.common.energy.EnergyApi;
import earth.terrarium.botarium.common.energy.base.BotariumEnergyBlock;
import earth.terrarium.botarium.common.energy.impl.ExtractOnlyEnergyContainer;
import earth.terrarium.botarium.common.energy.impl.WrappedBlockEnergyContainer;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public class HoneyGeneratorBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker, BotariumFluidBlock<WrappedBlockFluidContainer>, BotariumEnergyBlock<WrappedBlockEnergyContainer>, ContentContainerBlock<PositionContent> {

    public static final int ENERGY_FILL_UPGRADE_SLOT = 3;
    public static final int ENERGY_XFER_UPGRADE_SLOT = 2;
    public static final int ENERGY_CAP_UPGRADE_SLOT = 1;
    public static final int TANK_CAP_UPGRADE_SLOT = 0;

    public static final int ENERGY_TRANSFER_AMOUNT = HoneyGenConfig.energyTransferAmount;
    public static final int MAX_ENERGY_CAPACITY = HoneyGenConfig.maxEnergyCapacity;
    public static final int MAX_TANK_STORAGE = (int) FluidConstants.fromMillibuckets(HoneyGenConfig.maxTankCapacity);

    private FluidContainer fluidContainer;
    private WrappedBlockFluidContainer wrappedFluidContainer;

    private EnergyContainer energyContainer;
    private WrappedBlockEnergyContainer wrappedEnergyContainer;

    private AutomationSensitiveContainer container;

    private Optional<HoneyGenRecipe> recipe = Optional.empty();
    private boolean validRecipe = true;
    private ProcessStage processStage = ProcessStage.IDLE;
    private double energyFillModifier = 1;
    private double honeyDrainModifier = 1;

    public HoneyGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), pos, state);
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (processStage.isIdle() && this.canProcess()) this.startProcess(level);
        if (processStage.isProcessing()) {
            if (this.canProcess()) this.processEnergy();
            else this.processCompleted(level);
        }
        if (hasEnergy()) {
            EnergyApi.distributeEnergyNearby(this, ENERGY_TRANSFER_AMOUNT);
        }
    }

    private boolean hasEnergy() {
        return this.wrappedEnergyContainer.getStoredEnergy() > 0;
    }

    private boolean canProcess() {
        return !getFluid().isEmpty() && recipe.isPresent() && canAddEnergy(recipe.get().energyFillRate()) && canDrain(recipe.get().honeyDrainRate());
    }

    private boolean canAddEnergy(int energyAmount) {
        long newAmount = energyContainer.getStoredEnergy() + energyAmount;
        return newAmount <= energyContainer.getMaxCapacity();
    }

    private boolean canDrain(int drainAmount) {
        return getFluid().getFluidAmount() >= drainAmount;
    }

    private void startProcess(Level level) {
        processStage = ProcessStage.PROCESSING;
        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(HoneyGenerator.PROPERTY_ON, true));
    }

    private void processEnergy() {
        fluidContainer.internalExtract(getFluid().copyWithAmount(honeyDrainAmount()), false);
        energyContainer.internalInsert(energyFillAmount(), false);
    }

    private void processCompleted(Level level) {
        processStage = ProcessStage.IDLE;
        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(HoneyGenerator.PROPERTY_ON, false));
    }

    private void checkRecipe() {
        if (getFluid().isEmpty()) {
            recipe = Optional.empty();
            validRecipe = true;
        } else if (validRecipe && recipe.isEmpty() && level != null) {
            recipe = HoneyGenRecipe.findRecipe(level.getRecipeManager(), getFluid().getFluid(), getFluid().getCompound());
            validRecipe = recipe.isPresent();
        }
    }

    public int honeyDrainAmount() {
        return recipe.map(r -> {
            int drain = r.honeyDrainRate();
            return (int) (drain + drain * honeyDrainModifier);
        }).orElse(0);
    }

    public int energyFillAmount() {
        return recipe.map(r -> (int) (r.energyFillRate() * energyFillModifier)).orElse(0);
    }


    @Override
    public void setChanged() {
        super.setChanged();
        checkRecipe();
        sendToPlayersTrackingChunk();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString(NBTConstants.NBT_PROCESS_STAGE, processStage.toString());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(NBTConstants.NBT_PROCESS_STAGE)) {
            processStage = ProcessStage.valueOf(tag.getString(NBTConstants.NBT_PROCESS_STAGE).toUpperCase(Locale.ROOT));
        }
    }

    //overriding method as it provides guaranteed access to the level on first load in both forge and fabric
    @Override
    public void setLevel(@NotNull Level level) {
        super.setLevel(level);
        checkRecipe();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new HoneyGeneratorMenu(id, playerInventory, this);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return GuiTranslations.GENERATOR;
    }


    @Override
    public WrappedBlockEnergyContainer getEnergyStorage() {
        if (wrappedEnergyContainer == null) {
            energyContainer = new EnergyContainer();
            wrappedEnergyContainer = new WrappedBlockEnergyContainer(this, energyContainer);
        }
        return wrappedEnergyContainer;
    }

    public FluidHolder getFluid() {
        return getFluidContainer().getFluids().get(0);
    }

    @Override
    public WrappedBlockFluidContainer getFluidContainer() {
        if (wrappedFluidContainer == null) {
            fluidContainer = new FluidContainer();
            wrappedFluidContainer = new WrappedBlockFluidContainer(this, fluidContainer);
        }
        return wrappedFluidContainer;
    }

    @Override
    public AutomationSensitiveContainer getContainer() {
        if (container == null) {
            container = new Container(this);
        }
        return container;
    }

    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        energyContainer.serialize(tag);
        fluidContainer.serialize(tag);
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        energyContainer.deserialize(tag);
        fluidContainer.deserialize(tag);
    }

    @Override
    public PositionContent createContent() {
        return new PositionContent(getBlockPos());
    }

    private static class EnergyContainer extends ExtractOnlyEnergyContainer {

        private int capacity;
        private int maxExtract;

        public EnergyContainer() {
            super(HoneyGenConfig.maxEnergyCapacity);
            this.capacity = HoneyGenConfig.maxEnergyCapacity;
            this.maxExtract = HoneyGenConfig.energyTransferAmount;
        }

        @Override
        public long maxExtract() {
            return maxExtract;
        }

        @Override
        public long getMaxCapacity() {
            return capacity;
        }
    }

    private static class FluidContainer extends InsertOnlyFluidContainer {

        private int capacity;

        public FluidContainer() {
            super(i -> MAX_TANK_STORAGE, 1, (i, holder) -> holder.getFluid().is(ModFluidTags.HONEY));
        }

        @Override
        public long getTankCapacity(int slot) {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
            FluidHolder holder = this.getFluids().get(0);
            if (holder.getFluidAmount() > capacity) {
                holder.setAmount(capacity);
            }
        }
    }

    private class Container extends AutomationSensitiveContainer {

        public Container(BlockEntity entity) {
            super(entity, 4, player ->  true);
        }

        @Override
        public boolean canAccept(int slot, ItemStack stack, boolean automation) {
            if (automation) return false;
            return switch (slot) {
                case ENERGY_FILL_UPGRADE_SLOT -> stack.getItem() instanceof Upgrade upgrade && upgrade.isType(UpgradeType.ENERGY_FILL);
                case ENERGY_XFER_UPGRADE_SLOT -> stack.getItem() instanceof Upgrade upgrade && upgrade.isType(UpgradeType.ENERGY_XFER);
                case ENERGY_CAP_UPGRADE_SLOT -> stack.getItem() instanceof Upgrade upgrade && upgrade.isType(UpgradeType.ENERGY_CAPACITY);
                case TANK_CAP_UPGRADE_SLOT -> stack.getItem() instanceof Upgrade upgrade && upgrade.isType(UpgradeType.HONEY_CAPACITY);
                default -> false;
            };
        }

        @Override
        public boolean canRemove(int slot, boolean automation) {
            return !automation;
        }

        @Override
        public void deserialize(CompoundTag nbt) {
            super.deserialize(nbt);
            for (int i = 0; i < getContainerSize(); i++) {
                recalculateUpgrade(i);
            }
        }

        @Override
        public void setChanged(int slot) {
            super.setChanged(slot);
            recalculateUpgrade(slot);
        }

        private void recalculateUpgrade(int slot) {
            int upgrades = getItem(slot).getCount();
            switch (slot) {
                case ENERGY_XFER_UPGRADE_SLOT -> energyContainer.maxExtract = ((int) (ENERGY_TRANSFER_AMOUNT * Math.pow(HoneyGenConfig.energyTransferUpgradeBonus, upgrades)));
                case ENERGY_CAP_UPGRADE_SLOT -> energyContainer.capacity = ((int) (MAX_ENERGY_CAPACITY + MAX_ENERGY_CAPACITY * HoneyGenConfig.energyCapacityUpgradeBonus * upgrades));
                case TANK_CAP_UPGRADE_SLOT -> fluidContainer.setCapacity((int) (MAX_TANK_STORAGE + MAX_TANK_STORAGE * HoneyGenConfig.tankCapacityUpgradeBonus * upgrades));
                case ENERGY_FILL_UPGRADE_SLOT -> {
                    energyFillModifier = Math.pow(HoneyGenConfig.energyFillUpgradeBonus, upgrades);
                    honeyDrainModifier = HoneyGenConfig.honeyConsumptionUpgradePenalty * upgrades;
                }
            }
        }
    }
}
