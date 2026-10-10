package com.teamresourceful.resourcefulbees.common.blockentities;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.EnderBeeconBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.components.BeeconData;
import com.teamresourceful.resourcefulbees.common.components.TankData;
import com.teamresourceful.resourcefulbees.common.config.EnderBeeconConfig;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
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

import java.util.*;

public class EnderBeeconBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker, ContentMenuProvider<PositionContent> {

    private static final int TANK_INPUT = 0;
    private static final int TANK_CAPACITY = 64_000;

    private static final Codec<Pair<Holder<MobEffect>, Float>> EFFECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(Pair::getFirst),
            Codec.FLOAT.fieldOf("drain").forGetter(Pair::getSecond)
    ).apply(instance, Pair::of));

    private final FluidHandler tank = new FluidHandler();
    private final Set<Pair<Holder<MobEffect>, Float>> activeEffects = new HashSet<>();
    private final Set<Pair<Holder<MobEffect>, Float>> availableEffects = new HashSet<>();

    private boolean active = false;
    private int range = 10;
    private TankData tankData = TankData.EMPTY;
    private BlockCapabilityCache<ResourceHandler<FluidResource>, @Nullable Direction> fluidCache;
    private boolean fluidDirty = false;
    private Target targeting = Target.BEE;

    public EnderBeeconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateAvailableEffects();
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

    public Target target() {
        return targeting;
    }

    //endregion
    //region SYNCABLE GUI

    public TankData tankData() {
        return tankData;
    }

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentGetter components) {
        super.applyImplicitComponents(components);
        BeeconData client = components.getOrDefault(ModDataComponents.BEECON_DATA, BeeconData.EMPTY);
        this.activeEffects.clear();
        activeEffects.addAll(client.activeEffects());
        range = client.range();
        active = client.active();
        targeting = client.target();
        tankData = components.getOrDefault(ModDataComponents.SINGLE_TANK_DATA, TankData.EMPTY);
    }
    @Override
    protected void collectImplicitComponents(DataComponentMap.@NonNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.BEECON_DATA, new BeeconData(activeEffects, range, active, fluidStackInTank(), targeting));
        components.set(ModDataComponents.SINGLE_TANK_DATA, createTankDataPatch());
    }

    @Override
    public void removeComponentsFromTag(@NonNull ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard("beecon_data");
        output.discard("single_tank_data");
    }

    private TankData createTankDataPatch() {
        return new TankData(fluidStack(), tank.getCapacity());
    }

    @Override
    public DataComponentPatch getSyncData() {
        return DataComponentPatch.builder()
                .set(ModDataComponents.BEECON_DATA.get(), new BeeconData(activeEffects, range, active, fluidStackInTank(), targeting))
                .set(ModDataComponents.SINGLE_TANK_DATA.get(), createTankDataPatch())
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
                targeting = client.target();
            });
        }

        if (type == ModDataComponents.SINGLE_TANK_DATA.get()) {
            tankData = (TankData) data.orElseThrow();
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        input.readChild("tank", tank());
        tankData = createTankDataPatch();
        setRange(input.getIntOr("range", 10));
        activeEffects.clear();
        input.listOrEmpty("activeEffects", EFFECT_CODEC).forEach(activeEffects::add);
        active = input.getBooleanOr("isActive", false);
        targeting = input.read("target", Target.CODEC).orElse(Target.BEE);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("tank", tank());
        output.putInt("range", range);
        ValueOutput.TypedOutputList<Pair<Holder<MobEffect>, Float>> outputList = output.list("activeEffects", EFFECT_CODEC);
        for (Pair<Holder<MobEffect>, Float> effect : activeEffects) outputList.add(effect);
        output.putBoolean("isActive", active);
        output.store("target", Target.CODEC, targeting);
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
        if (level.getGameTime() % 80L == 0L && !tank.isEmpty()) {
            if (targeting == Target.BEE) {
                List<Bee> bees = getBeesInRange(level, pos);
                markDisruptorRange(bees);

                if (active) {
                    applyBeeconEffects(bees);
                }
            } else if (targeting == Target.PLAYER && active) {
                applyBeeconEffects(getPlayersInRange(level, pos));
            }

            if (active && state.hasProperty(EnderBeeconBlock.SOUND) && state.getValue(EnderBeeconBlock.SOUND)) {
                level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private List<Player> getPlayersInRange(Level level, BlockPos pos) {
        return level.getEntitiesOfClass(
                Player.class,
                getEffectBox(level, pos, range)
        );
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

    private void applyBeeconEffects(List<? extends LivingEntity> bees) {
        for (LivingEntity bee : bees) {
            for (Pair<Holder<MobEffect>, Float> effect : activeEffects) {
                bee.addEffect(new MobEffectInstance(effect.getFirst(), 120, 0, false, false));
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

    public FluidStack fluidStack() {
        return fluidResource().toStack(tank.getAmountAsInt(TANK_INPUT));
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

    public boolean isEffectActive(Pair<Holder<MobEffect>, Float> effect) {
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

    public void handleBeeconUpdate(BeeconPacketOption option, @Nullable Pair<Holder<MobEffect>, Float> effect, int value) {
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
            case TARGET -> {
                if (this.targeting != Target.ordinalOf(value)) {
                    this.targeting = Target.ordinalOf(value);
                    setChanged();
                    sendToListeningPlayers();
                }
            }
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        updateAvailableEffects();
        refreshActiveState();
    }

    public Set<Pair<Holder<MobEffect>, Float>> availableEffects() {
        return Collections.unmodifiableSet(availableEffects);
    }

    private void updateAvailableEffects() {
        availableEffects.clear();
        if (fluidResource().isEmpty()) {
            return;
        }

        Fluid honey = fluidResource().getFluid();
        if (honey instanceof CustomHoneyFluid.Still customHoneyFluid) {
            availableEffects.addAll(customHoneyFluid.getHoneyFluidData().beeconEffects());
        } else {
            availableEffects.add(Pair.of(ModEffects.CALMING.holder(), 10f));
        }
    }

    @Override
    public PositionContent createContent(ServerPlayer player) {
        return new PositionContent(this.getBlockPos());
    }

    public int drainAmount() {
        double base = EnderBeeconConfig.beeconBaseDrain;
        for (Pair<Holder<MobEffect>, Float> e : activeEffects) {
            base += e.getSecond();
        }
        base = (base * (range * EnderBeeconConfig.beeconRangeMultiplier * 0.10d)); //todo decide if this shoulstay as the calculation
        return Math.toIntExact(Math.round(base));
    }

    public FluidHandler tank() {
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
            tankData = createTankDataPatch();
            fluidDirty = true;
            EnderBeeconBlockEntity.this.setChanged();
        }

        public int getCapacity() {
            return capacity;
        }

        public boolean isEmpty() {
            return getResource(0).isEmpty();
        }
    }

    public enum Target {
        BEE,
        PLAYER;

        public static final EnumCodec<Target> CODEC = EnumCodec.of(Target.class);

        public static final StreamCodec<ByteBuf, Target> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

        public static Target ordinalOf(int ordinal) {
            if (ordinal == 1) {
                return PLAYER;
            }

            return BEE;
        }
    }
}
