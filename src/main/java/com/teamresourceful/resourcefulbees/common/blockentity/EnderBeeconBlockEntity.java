package com.teamresourceful.resourcefulbees.common.blockentity;

import com.google.common.collect.Sets;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.block.EnderBeecon;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.inventory.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.network.packets.BeeconChangeMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEffects;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EnderBeeconBlockEntity extends GUISyncedBlockEntity {

    public static final Set<MobEffect> ALLOWED_EFFECTS = Sets.newHashSet(ModEffects.CALMING.get(), MobEffects.WATER_BREATHING, MobEffects.FIRE_RESISTANCE, MobEffects.REGENERATION);

    private final HoneyFluidTank tank = new HoneyFluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            sendToPlayersTrackingChunk();
            updateBeecon = true;
        }
    };
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    private boolean updateBeecon = true;
    private boolean beeconActive = false;

    private Set<MobEffect> effects = new LinkedHashSet<>();
    private int range = 10;

    public EnderBeeconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), pos, state);
    }

    //region MENU

    @NotNull
    @Override
    public Component getDisplayName() {
        return TranslationConstants.Guis.BEECON;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new EnderBeeconMenu(id, playerInventory, this);
    }

    //endregion

    //region SYNCABLE GUI
    @Override
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        tag.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundTag()));
        tag.putInt(NBTConstants.Beecon.RANGE, range);
        if (effects != null && !effects.isEmpty()) tag.put(NBTConstants.Beecon.ACTIVE_EFFECTS, writeEffectsToNBT(new ListTag()));
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        tank.readFromNBT(tag.getCompound(NBTConstants.NBT_TANK));
        effects = readEffectsFromNBT(tag.getList(NBTConstants.Beecon.ACTIVE_EFFECTS, Tag.TAG_STRING));
        range = tag.getInt(NBTConstants.Beecon.RANGE);
        range = Math.max(range, 10);
    }
    //endregion

    //region NBT
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("SyncData", getSyncData());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        readSyncData(tag.getCompound("SyncData"));
    }

    //endregion

    public static void serverTick(Level level, BlockPos pos, BlockState state, EnderBeeconBlockEntity entity) {
        if (level == null) return;

        // do drain tank
        if (entity.doEffects()) {
            entity.tank.drain(entity.getDrain(), IFluidHandler.FluidAction.EXECUTE);
        }

        // give effects
        if (level.getGameTime() % 80L == 0L && !entity.tank.isEmpty()) {
            List<Bee> bees = level.getEntitiesOfClass(Bee.class, getEffectBox(level, pos, entity.range));
            bees.stream().filter(CustomBeeEntity.class::isInstance).map(CustomBeeEntity.class::cast).forEach(CustomBeeEntity::setHasDisruptorInRange);
            addEffectsToBees(level, bees, entity);
            if (state.hasProperty(EnderBeecon.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeecon.SOUND)))
                level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1f, 1f);
        }

        // pull from below containers
        pullFluidFromBelow(level, pos, state, entity);

        // play activation sounds
        startUpCheck(level, pos, state, entity);
    }

    private static void startUpCheck(@NotNull Level level, BlockPos pos, BlockState state, EnderBeeconBlockEntity entity) {
        if (level.isClientSide) return;

        boolean flag = entity.tank.getFluidAmount() > 0;
        if (entity.updateBeecon) {
            if (flag && !entity.beeconActive) {
                if (state.hasProperty(EnderBeecon.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeecon.SOUND)))
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1f, 1f);
                entity.beeconActive = true;
            } else if (!flag && entity.beeconActive) {
                if (state.hasProperty(EnderBeecon.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeecon.SOUND)))
                    level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1f, 1f);
                entity.beeconActive = false;
            }
        }
        entity.updateBeecon = false;
    }

    private static void pullFluidFromBelow(@NotNull Level level, BlockPos pos, BlockState state, EnderBeeconBlockEntity entity) {
        BlockEntity tileEntity = level.getBlockEntity(pos.below());
        if (tileEntity == null) return;
        if (entity.tank.getSpace() == 0) return;
        tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.UP).ifPresent(handler -> {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack stack = handler.getFluidInTank(i);
                if (entity.tank.isFluidValid(stack) && (entity.tank.getFluid().isFluidEqual(stack) || entity.tank.getFluid().isEmpty())) {
                    int pullAmount = CommonConfig.BEECON_PULL_AMOUNT.get();
                    if (pullAmount > entity.tank.getSpace()) pullAmount = entity.tank.getSpace();
                    FluidStack amountDrained = handler.drain(new FluidStack(stack.getFluid(), pullAmount), IFluidHandler.FluidAction.EXECUTE);
                    entity.tank.fill(amountDrained, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        });
    }

    //region CLIENT

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX(), worldPosition.getY() + 255D, worldPosition.getZ());
    }

    //endregion

    public HoneyFluidTank getTank() {
        return tank;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        tankOptional.invalidate();
    }

    public Set<MobEffect> getEffects() {
        return effects;
    }

    public static AABB getEffectBox(@NotNull Level level, BlockPos pos, int range) {
        AABB aabb = new AABB(pos).inflate(range);
        return new AABB(aabb.minX, 0, aabb.minZ, aabb.maxX, level.getMaxBuildHeight(), aabb.maxZ);
    }

    private static void addEffectsToBees(@NotNull Level level, List<Bee> bees, EnderBeeconBlockEntity entity) {
        if (entity.doEffects()) {
            for (Bee mob : bees) for (MobEffect effect : entity.effects) mob.addEffect(new MobEffectInstance(effect, 120, 0, false, false));
        }
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public boolean doEffects() {
        if (tank.isEmpty()) return false;
        return !effects.isEmpty();
    }

    public void handleBeeconUpdate(BeeconChangeMessage.Option option, int value) {
        if (this.level == null) return;
        switch (option) {
            case EFFECT_OFF, EFFECT_ON -> {
                MobEffect effect = MobEffect.byId(value);
                if (effect != null && ALLOWED_EFFECTS.contains(effect)) {
                    if (option.equals(BeeconChangeMessage.Option.EFFECT_ON)) effects.add(effect);
                    else effects.remove(effect);
                    this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
                }
            }
            case BEAM -> this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(EnderBeecon.BEAM, value == 1), Block.UPDATE_ALL);
            case SOUND -> this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(EnderBeecon.SOUND, value == 1), Block.UPDATE_ALL);
            case RANGE -> {
                this.setRange(value);
                this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
            }
            default -> ResourcefulBees.LOGGER.error("UNKNOWN Beecon Configuration Option '{}' please report to github!", option);
        }
    }

    public int getDrain() {
        double base = CommonConfig.BEECON_BASE_DRAIN.get();
        for (MobEffect e : effects) base += getEffectValue(e);
        base = (base * (range * CommonConfig.BEECON_RANGE_MULTIPLIER.get() * 0.10d));
        return (int) Math.ceil(base);
    }

    public boolean hasEffect(MobEffect effect) {
        return effects.contains(effect);
    }

    //region effect nbt

    public ListTag writeEffectsToNBT(ListTag nbt) {
        for (MobEffect effect : effects) {
            ResourceLocation effectId = effect.getRegistryName();
            if (effectId != null) nbt.add(StringTag.valueOf(effectId.toString()));
        }
        return nbt;
    }

    public Set<MobEffect> readEffectsFromNBT(ListTag nbt) {
        Set<MobEffect> beeconEffects = new LinkedHashSet<>();
        for (Tag inbt : nbt) {
            if (inbt instanceof StringTag) {
                MobEffect effect = BeeInfoUtils.getEffect(inbt.getAsString());
                if (effect != null) {
                    beeconEffects.add(effect);
                }
            }
        }
        return beeconEffects;
    }

    public double getEffectValue(MobEffect effect) {
        if (ModEffects.CALMING.get().equals(effect)) return CommonConfig.BEECON_CALMING_VALUE.get();
        else if (MobEffects.WATER_BREATHING.equals(effect)) return CommonConfig.BEECON_WATER_BREATHING_VALUE.get();
        else if (MobEffects.FIRE_RESISTANCE.equals(effect)) return CommonConfig.BEECON_FIRE_RESISTANCE_VALUE.get();
        else if (MobEffects.REGENERATION.equals(effect)) return CommonConfig.BEECON_REGENERATION_VALUE.get();

        ResourcefulBees.LOGGER.error("Effect {} does not have an effect value", effect.getRegistryName());
        return 1D;
    }

    //endregion
}
