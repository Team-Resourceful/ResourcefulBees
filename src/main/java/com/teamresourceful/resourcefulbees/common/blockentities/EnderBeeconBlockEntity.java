package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.EnderBeeconBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.components.BeeconData;
import com.teamresourceful.resourcefulbees.common.config.EnderBeeconConfig;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconEffect;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class EnderBeeconBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker, ContentMenuProvider<PositionContent> {

    private static final int TANK_INPUT = 0;
    private static final int TANK_CAPACITY = 16_000;

    private final FluidHandler tank = new FluidHandler();
    private final EnumSet<BeeconEffect> activeEffects = EnumSet.noneOf(BeeconEffect.class);

    private boolean active = false;
    private int range = 10;
    private FluidStack clientFluid = FluidStack.EMPTY;
    private BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction> fluidCache;
    private boolean fluidDirty = false;

    public EnderBeeconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            fluidCache = BlockCapabilityCache.create(
                    Capabilities.Fluid.BLOCK,
                    serverLevel,
                    worldPosition.below(),
                    Direction.UP,
                    () -> !isRemoved(),
                    () -> {}
            );
        }
    }

    //region MENU
    @NotNull
    @Override
    public Component getDisplayName() {
        return GuiTranslations.BEECON;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new EnderBeeconMenu(id, playerInventory, this);
    }


    //endregion
    //region SYNCABLE GUI

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentGetter components) {
        super.applyImplicitComponents(components);
        BeeconData client = components.getOrDefault(ModDataComponents.BEECON_DATA, BeeconData.EMPTY);
        this.activeEffects.clear();
        activeEffects.addAll(client.activeEffects());
        range = client.range();
        active = client.active();
        clientFluid = client.fluid();
    }
    @Override
    protected void collectImplicitComponents(DataComponentMap.@NonNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.BEECON_DATA, new BeeconData(activeEffects, range, active, fluidStackInTank()));
    }

    @Override
    public void removeComponentsFromTag(@NonNull ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard("beecon_data");
    }

    @Override
    public DataComponentPatch getSyncData() {
        return DataComponentPatch.builder()
                .set(ModDataComponents.BEECON_DATA.get(), new BeeconData(activeEffects, range, active, fluidStackInTank()))
                .build();
    }

    @Override
    public <Data> void setSyncData(DataComponentType<Data> type, Optional<Data> data) {
        if (type == ModDataComponents.BEECON_DATA.get()) {
            this.activeEffects.clear();
            data.ifPresent(value -> {
                BeeconData client = (BeeconData) value;

                activeEffects.addAll(client.activeEffects());
                range = client.range();
                active = client.active();
                clientFluid = client.fluid();
            });
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        input.readChild("tank", fluidHandler());
        setRange(input.getIntOr("range", 10));
        activeEffects.clear();
        input.listOrEmpty("activeEffects", BeeconEffect.CODEC).forEach(activeEffects::add);
        active = input.getBooleanOr("isActive", false);
        clientFluid = fluidStackInTank();
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("tank", fluidHandler());
        output.putInt("range", range);
        ValueOutput.TypedOutputList<BeeconEffect> outputList = output.list("activeEffects", BeeconEffect.CODEC);
        for (BeeconEffect effect : activeEffects) outputList.add(effect);
        output.putBoolean("isActive", active);
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        // pull from below containers
        pullFluidFromBelow();

        // drain tank
        if (active) {
            drainTank();
        }

        // Fluid amount affects world rendering, so periodically
        // synchronize it to everyone tracking this Beecon.
        if (fluidDirty && level.getGameTime() % 5L == 0L) {
            sendToPlayersTrackingChunk();
            fluidDirty = false;
        }

        // give effects
        if (level.getGameTime() % 80L == 0L && !this.tank.isEmpty()) {
            List<Bee> bees = getBeesInRange(level, pos);
            markDisruptorRange(bees);
            if (active) {
                applyBeeconEffects(bees);
                if (state.hasProperty(EnderBeeconBlock.SOUND) && state.getValue(EnderBeeconBlock.SOUND)) {
                    level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1f, 1f);
                }
            }
        }
    }

    private void drainTank() {
        int drain = drainAmount();
        if (drain <= 0 || !canDrain(drain)) return;

        try (Transaction transaction = Transaction.openRoot()){
            int drained = tank.extract(fluidResource(), drain, transaction);
            if (drained == drain) {
                transaction.commit();
            }
        }
    }

    private List<Bee> getBeesInRange(Level level, BlockPos pos) {
        return level.getEntitiesOfClass(
                Bee.class,
                getEffectBox(level, pos, range)
        );
    }

    private void markDisruptorRange(List<Bee> bees) {
        for (Bee bee : bees) {
            if (bee instanceof CustomBeeEntity customBee) {
                customBee.setDisruptorInRange();
            }
        }
    }

    private void applyBeeconEffects(List<Bee> bees) {
        for (Bee bee : bees) {
            for (BeeconEffect effect : activeEffects) {
                bee.addEffect(new MobEffectInstance(effect.effectHolder(), 120, 0, false, false));
            }
        }
    }

    private void refreshActiveState() {
        boolean wasActive = active;
        active = canGiveEffects();

        if (wasActive != active) {
            playActivationSound(active);
        }
    }

    private void playActivationSound(boolean activating) {
        if (level == null) {
            return;
        }

        BlockState state = getBlockState();

        if (!state.hasProperty(EnderBeeconBlock.SOUND) || !state.getValue(EnderBeeconBlock.SOUND)) {
            return;
        }

        level.playSound(null, worldPosition, activating ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public boolean isActive() {
        return active;
    }

    private void pullFluidFromBelow() {
        if (tank.getAmountAsInt(TANK_INPUT) >= TANK_CAPACITY) {
            return;
        }

        if (fluidCache == null) {
            return;
        }

        ResourceHandler<FluidResource> source = fluidCache.getCapability();

        if (source == null) {
            return;
        }

        int remainingCapacity = TANK_CAPACITY - tank.getAmountAsInt(TANK_INPUT);
        int maxPull = Math.min(EnderBeeconConfig.beeconPullAmount, remainingCapacity);

        if (maxPull <= 0) {
            return;
        }

        for (int i = 0; i < source.size(); i++) {
            FluidResource resource = source.getResource(i);

            if (resource.isEmpty()) {
                continue;
            }

            if (!resource.is(ModFluidTags.HONEY)) {
                continue;
            }

            int amount = Math.min(source.getAmountAsInt(i), maxPull);

            if (amount <= 0) {
                continue;
            }

            try (Transaction transaction = Transaction.openRoot()) {
                int extracted = source.extract(i, resource, amount, transaction);

                if (extracted <= 0) {
                    continue;
                }

                int inserted = tank.insert(resource, extracted, transaction);

                if (inserted != extracted) {
                    continue;
                }

                transaction.commit();
                return;
            }
        }
    }

    public FluidResource fluidResource() {
        return tank.getResource(TANK_INPUT);
    }

    public FluidStack clientFluid() {
        return clientFluid;
    }

    public static AABB getEffectBox(@NotNull Level level, BlockPos pos, int range) {
        AABB aabb = new AABB(pos).inflate(range);
        return new AABB(aabb.minX, level.getMinY(), aabb.minZ, aabb.maxX, level.getMaxY(), aabb.maxZ);
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = Math.clamp(range, 10, 50);
    }

    public boolean isEffectActive(BeeconEffect effect) {
        return activeEffects.contains(effect);
    }

    public boolean canGiveEffects() {
        if (activeEffects.isEmpty()) {
            return false;
        }

        int drain = drainAmount();
        return drain > 0 && canDrain(drain);
    }

    private boolean canDrain(int amount) {
        return tank.getAmountAsInt(TANK_INPUT) >= amount;
    }

    public void handleBeeconUpdate(BeeconPacketOption option, @Nullable BeeconEffect effect, int value) {
        if (this.level == null) return;

        switch (option) {
            case EFFECT_ON -> {
                if (effect != null && activeEffects.add(effect)) {
                    refreshActiveState();
                    sendToListeningPlayers();
                }
            }
            case EFFECT_OFF -> {
                if (effect != null && activeEffects.remove(effect)) {
                    refreshActiveState();
                    sendToListeningPlayers();
                }
            }
            case BEAM ->
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(EnderBeeconBlock.BEAM, value == 1), Block.UPDATE_ALL);
            case SOUND ->
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(EnderBeeconBlock.SOUND, value == 1), Block.UPDATE_ALL);
            case RANGE -> {
                int oldRange = range;
                setRange(value);

                if (range != oldRange) {
                    refreshActiveState();
                    sendToListeningPlayers();
                }
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        refreshActiveState();
    }

    @Override
    public PositionContent createContent(ServerPlayer player) {
        return new PositionContent(this.getBlockPos());
    }

    public int drainAmount() {
        double base = EnderBeeconConfig.beeconBaseDrain;
        for (BeeconEffect e : activeEffects) {
            base += e.drainAmount();
        }
        base = (base * (range * EnderBeeconConfig.beeconRangeMultiplier * 0.10d));
        return Math.toIntExact(Math.round(base));
    }

    public FluidHandler fluidHandler() {
        return tank;
    }

    private @NonNull FluidStack fluidStackInTank() {
        FluidResource resource = tank.getResource(TANK_INPUT);

        return resource.isEmpty()
                ? FluidStack.EMPTY
                : resource.toStack(tank.getAmountAsInt(TANK_INPUT));
    }

    public class FluidHandler extends FluidStacksResourceHandler {

        public FluidHandler() {
            super(1, TANK_CAPACITY);
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return resource.is(ModFluidTags.HONEY);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull FluidStack previousContents) {
            fluidDirty = true;
            EnderBeeconBlockEntity.this.setChanged();
        }

        public boolean isEmpty() {
            return getResource(0).isEmpty();
        }
    }
}
