package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.ContentContainerBlock;
import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.HoneyGeneratorBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.components.BatteryData;
import com.teamresourceful.resourcefulbees.common.components.TankData;
import com.teamresourceful.resourcefulbees.common.config.HoneyGenConfig;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.ProcessStage;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.menus.HoneyGeneratorMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.recipes.HoneyGenRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class HoneyGeneratorBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker, ContentContainerBlock<PositionContent> {

    public static final int ENERGY_FILL_UPGRADE_SLOT = 3;
    public static final int ENERGY_XFER_UPGRADE_SLOT = 2;
    public static final int ENERGY_CAP_UPGRADE_SLOT = 1;
    public static final int TANK_CAP_UPGRADE_SLOT = 0;

    public static final int ENERGY_TRANSFER_AMOUNT = HoneyGenConfig.energyTransferAmount;
    public static final int MAX_ENERGY_CAPACITY = HoneyGenConfig.maxEnergyCapacity;
    public static final int MAX_TANK_CAPACITY = HoneyGenConfig.maxTankCapacity;

    private final FluidHandler tank = new FluidHandler();
    private final EnergyHandler battery = new EnergyHandler();
    private final ItemHandler inventory = new ItemHandler();

    private final Set<BlockCapabilityCache<net.neoforged.neoforge.transfer.energy.EnergyHandler, Direction>> energyCache = new HashSet<>();

    private Optional<RecipeHolder<HoneyGenRecipe>> recipe = Optional.empty();
    private boolean validRecipe = false;
    private ProcessStage processStage = ProcessStage.IDLE;
    private double energyFillModifier = 1;
    private double honeyDrainModifier = 1;

    private BatteryData batteryData = BatteryData.EMPTY;
    private List<TankData> tankData = Collections.nCopies(1, TankData.EMPTY);
    private boolean guiDirty = true;

    public HoneyGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), pos, state);
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (processStage.isIdle() && this.canProcess()) this.startProcess(level, pos, state);
        if (processStage.isProcessing()) {
            if (this.canProcess()) this.processEnergy();
            else this.processCompleted(level);
        }
        if (hasEnergy()) {
            distributeEnergyNearby();
        }

        if (guiDirty && level.getGameTime() % 5L == 0L) {
            sendToPlayersTrackingChunk();
            guiDirty = false;
        }
    }

    private boolean hasEnergy() {
        return battery.getAmountAsInt() > 0;
    }

    private boolean canProcess() {
        return !fluidResource().isEmpty() && checkRecipe() && canAddEnergy() && canDrainTank();
    }

    private boolean canAddEnergy() {
        int newAmount = battery.getAmountAsInt() + energyFillAmount();
        return newAmount <= battery.getCapacityAsInt();
    }

    private boolean canDrainTank() {
        return tank.getAmountAsInt(0) >= honeyDrainAmount();
    }

    private void startProcess(Level level, BlockPos pos, BlockState state) {
        processStage = ProcessStage.PROCESSING;
        level.setBlockAndUpdate(pos, state.setValue(HoneyGeneratorBlock.ACTIVE_PROPERTY, true));
    }

    private void processEnergy() {
        try (Transaction transaction = Transaction.openRoot()) {
            int drainAmount = honeyDrainAmount();
            int fillAmount = energyFillAmount();
            int extracted = tank.extract(fluidResource(), drainAmount, transaction);
            int inserted = battery.insert(fillAmount, transaction);
            if (extracted == drainAmount && inserted == fillAmount) {
                transaction.commit();
            }
        }
    }

    private void processCompleted(Level level) {
        processStage = ProcessStage.IDLE;
        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(HoneyGeneratorBlock.ACTIVE_PROPERTY, false));
    }

    private void distributeEnergyNearby() {
        energyCache.forEach(cache -> {
            try(Transaction transaction = Transaction.openRoot()) {
                int transferAmount = Math.min(battery.getAmountAsInt(), battery.maxExtract());
                int extracted = battery.extract(transferAmount, transaction);
                var cap = cache.getCapability();
                int inserted = cap == null ? 0 : cap.insert(transferAmount, transaction);
                if (inserted == extracted) {
                    transaction.commit();
                }
            }
        });
    }

    private boolean checkRecipe() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (fluidResource().isEmpty()) {
            recipe = Optional.empty();
            validRecipe = false;
            processStage = ProcessStage.IDLE;
        } else if (!validRecipe && recipe.isEmpty()) {
            recipe = HoneyGenRecipe.findRecipe(serverLevel.recipeAccess(), fluidStack(), serverLevel);
            validRecipe = recipe.isPresent();
        }

        return validRecipe;
    }

    public int honeyDrainAmount() {
        return recipe.map(r -> {
            int drain = r.value().honeyDrainRate();
            return (int) (drain + drain * honeyDrainModifier);
        }).orElse(0);
    }

    public int energyFillAmount() {
        return recipe.map(r -> (int) (r.value().energyFillRate() * energyFillModifier)).orElse(0);
    }


    @Override
    public void setChanged() {
        super.setChanged();
        //sendToPlayersTrackingChunk();
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        input.readChild("tank", tank);
        input.readChild("battery", battery);
        input.readChild("inventory", inventory);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("tank", tank);
        output.putChild("battery", battery);
        output.putChild("inventory", inventory);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            checkRecipe();
            inventory.recalculateUpgrade();
            for (Direction direction : Direction.values()) {
                var cache = BlockCapabilityCache.create(
                        Capabilities.Energy.BLOCK,
                        serverLevel,
                        worldPosition.relative(direction),
                        direction,
                        () -> !isRemoved(),
                        () -> {
                        }
                );
                energyCache.add(cache);
            }
        }
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

    public EnergyHandler battery() {
        return battery;
    }

    public FluidHandler tank() {
        return tank;
    }

    public ItemHandler inventory() {
        return inventory;
    }

    public FluidResource fluidResource() {
        return tank.getResource(0);
    }

    public FluidStack fluidStack() {
        return fluidResource().toStack(tank.getAmountAsInt(0));
    }

    public List<TankData> tankData() {
        return tankData;
    }

    public BatteryData batteryData() {
        return batteryData;
    }

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentGetter components) {
        super.applyImplicitComponents(components);
        tankData = components.getOrDefault(ModDataComponents.TANK_DATA, new ArrayList<>());
        batteryData = components.getOrDefault(ModDataComponents.BATTERY_DATA, BatteryData.EMPTY);
    }
    @Override
    protected void collectImplicitComponents(DataComponentMap.@NonNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.TANK_DATA, createTankDataPatch());
        components.set(ModDataComponents.BATTERY_DATA, createBatteryDataPatch());
    }

    private @NonNull List<TankData> createTankDataPatch() {
        return List.of(new TankData(fluidStack(), tank.getCapacityAsInt(0, fluidResource())));
    }

    private @NonNull BatteryData createBatteryDataPatch() {
        return new BatteryData(battery.getAmountAsInt(), battery.getCapacityAsInt(), battery.maxExtract());
    }

    @Override
    public void removeComponentsFromTag(@NonNull ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard("tank_data");
        output.discard("battery_data");
    }

    @Override
    public DataComponentPatch getSyncData() {
        return DataComponentPatch.builder()
                .set(ModDataComponents.TANK_DATA.get(), createTankDataPatch())
                .set(ModDataComponents.BATTERY_DATA.get(), createBatteryDataPatch())
                .build();
    }

    @Override
    public <Data> void setSyncData(
            DataComponentType<Data> type,
            Optional<Data> data
    ) {
        if (type == ModDataComponents.TANK_DATA.get()) {
            tankData = (List<TankData>) data.orElseThrow();
        }
        if (type == ModDataComponents.BATTERY_DATA.get()) {
            batteryData = (BatteryData) data.orElseThrow();
        }
    }

    @Override
    public PositionContent createContent(ServerPlayer player) {
        return new PositionContent(getBlockPos());
    }

    public class FluidHandler extends FluidStacksResourceHandler {

        public FluidHandler() {
            super(1, MAX_TANK_CAPACITY);
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return resource.is(ModFluidTags.HONEY);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull FluidStack previousContents) {
            super.onContentsChanged(index, previousContents);
            if (level instanceof ServerLevel) {
                checkRecipe();
                guiDirty = true;
            }
            HoneyGeneratorBlockEntity.this.setChanged();
        }

        public void setCapacity(int amount) {
            this.capacity = amount;
            if (stacks.getFirst().amount() >= amount) {
                stacks.getFirst().setAmount(amount);
            }
        }
    }

    public class EnergyHandler extends SimpleEnergyHandler {


        public EnergyHandler() {
            super(MAX_ENERGY_CAPACITY);
        }

        @Override
        protected void onEnergyChanged(int previousAmount) {
            super.onEnergyChanged(previousAmount);
            if (level instanceof ServerLevel) {
                guiDirty = true;
            }
            HoneyGeneratorBlockEntity.this.setChanged();
        }

        public void setCapacity(int amount) {
            capacity = amount;
            if (energy >= capacity) {
                energy = capacity;
            }
        }

        public void setMaxExtract(int amount) {
            if (amount >= capacity) {
                amount = capacity;
            }
            maxExtract = amount;
        }

        public int maxExtract() {
            return maxExtract;
        }
    }

    public class ItemHandler extends ItemStacksResourceHandler {


        public ItemHandler() {
            super(4);
        }
        @Override
        protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            recalculateUpgrade(index);
            if (level instanceof ServerLevel) {
                guiDirty = true;
            }
            HoneyGeneratorBlockEntity.this.setChanged();
        }

        private void recalculateUpgrade(int slot) {
            int upgrades = getAmountAsInt(slot);
            switch (slot) {
                case ENERGY_XFER_UPGRADE_SLOT -> battery.setMaxExtract((int) (ENERGY_TRANSFER_AMOUNT * Math.pow(HoneyGenConfig.energyTransferUpgradeBonus, upgrades)));
                case ENERGY_CAP_UPGRADE_SLOT -> battery.setCapacity((int) (MAX_ENERGY_CAPACITY + MAX_ENERGY_CAPACITY * HoneyGenConfig.energyCapacityUpgradeBonus * upgrades));
                case TANK_CAP_UPGRADE_SLOT -> tank.setCapacity((int) (MAX_TANK_CAPACITY + MAX_TANK_CAPACITY * HoneyGenConfig.tankCapacityUpgradeBonus * upgrades));
                case ENERGY_FILL_UPGRADE_SLOT -> {
                    energyFillModifier = Math.pow(HoneyGenConfig.energyFillUpgradeBonus, upgrades);
                    honeyDrainModifier = HoneyGenConfig.honeyConsumptionUpgradePenalty * upgrades;
                }
                default -> throw new IllegalStateException("Honey Generator only has 4 slots!");
            }
        }

        private void recalculateUpgrade() {
            for (int i = 0; i < 4; i++) {
                recalculateUpgrade(i);
            }
        }
    }
}
