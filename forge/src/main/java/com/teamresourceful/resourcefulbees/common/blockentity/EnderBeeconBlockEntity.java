package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.block.EnderBeecon;
import com.teamresourceful.resourcefulbees.common.blockentity.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.config.EnderBeeconConfig;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.inventory.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.network.packets.client.BeeconChangePacket;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class EnderBeeconBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker {

    public static final Set<MobEffect> ALLOWED_EFFECTS = Set.of(ModEffects.CALMING.get(), MobEffects.WATER_BREATHING, MobEffects.FIRE_RESISTANCE, MobEffects.REGENERATION);

    private final FluidTank tank = new FluidTank(16000, fluidStack -> fluidStack.getFluid().is(ModFluidTags.HONEY)) {
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
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    //endregion

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level == null) return;

        // do drain tank
        if (this.doEffects()) {
            this.tank.drain(this.getDrain(), IFluidHandler.FluidAction.EXECUTE);
        }

        // give effects
        if (level.getGameTime() % 80L == 0L && !this.tank.isEmpty()) {
            List<Bee> bees = level.getEntitiesOfClass(Bee.class, getEffectBox(level, pos, this.range));
            bees.stream().filter(CustomBeeEntity.class::isInstance).map(CustomBeeEntity.class::cast).forEach(CustomBeeEntity::setHasDisruptorInRange);
            this.effects
                    .stream()
                    .map(effect -> new MobEffectInstance(effect, 120, 0, false, false))
                    .forEach(effect -> bees.forEach(bee -> bee.addEffect(new MobEffectInstance(effect))));

            if (state.hasProperty(EnderBeecon.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeecon.SOUND)))
                level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1f, 1f);
        }

        // pull from below containers
        pullFluidFromBelow(level, pos);

        // play activation sounds
        startUpCheck(level, pos, state);
    }

    private void startUpCheck(@NotNull Level level, BlockPos pos, BlockState state) {
        boolean flag = this.tank.getFluidAmount() > 0;
        if (this.updateBeecon) {
            if (flag && !this.beeconActive) {
                if (state.hasProperty(EnderBeecon.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeecon.SOUND)))
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1f, 1f);
                this.beeconActive = true;
            } else if (!flag && this.beeconActive) {
                if (state.hasProperty(EnderBeecon.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeecon.SOUND)))
                    level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1f, 1f);
                this.beeconActive = false;
            }
        }
        this.updateBeecon = false;
    }

    private void pullFluidFromBelow(@NotNull Level level, BlockPos pos) {
        if (this.tank.getSpace() == 0) return;
        BlockEntity tileEntity = level.getBlockEntity(pos.below());
        if (tileEntity == null) return;
        tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP).ifPresent(handler -> {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack stack = handler.getFluidInTank(i);
                if (this.tank.isFluidValid(stack) && (this.tank.getFluid().isFluidEqual(stack) || this.tank.getFluid().isEmpty())) {
                    int pullAmount = Math.min(EnderBeeconConfig.beeconPullAmount, this.tank.getSpace());
                    FluidStack amountDrained = handler.drain(new FluidStack(stack.getFluid(), pullAmount), IFluidHandler.FluidAction.EXECUTE);
                    this.tank.fill(amountDrained, IFluidHandler.FluidAction.EXECUTE);
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

    public FluidTank getTank() {
        return tank;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.FLUID_HANDLER)) return tankOptional.cast();
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

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public boolean doEffects() {
        return !tank.isEmpty() && !effects.isEmpty();
    }

    public void handleBeeconUpdate(BeeconChangePacket.Option option, int value) {
        if (this.level == null) return;
        switch (option) {
            case EFFECT_OFF, EFFECT_ON -> {
                MobEffect effect = MobEffect.byId(value);
                if (effect != null && ALLOWED_EFFECTS.contains(effect)) {
                    if (option.equals(BeeconChangePacket.Option.EFFECT_ON)) effects.add(effect);
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
            default -> ModConstants.LOGGER.error("UNKNOWN Beecon Configuration Option '{}' please report to github!", option);
        }
    }

    public int getDrain() {
        double base = EnderBeeconConfig.beeconBaseDrain;
        for (MobEffect e : effects) base += getEffectValue(e);
        base = (base * (range * EnderBeeconConfig.beeconRangeMultiplier * 0.10d));
        return (int) base;
    }

    public boolean hasEffect(MobEffect effect) {
        return effects.contains(effect);
    }

    //region effect nbt

    public ListTag writeEffectsToNBT(ListTag nbt) {
        effects.stream()
                .map(Registry.MOB_EFFECT::getKey)
                .filter(Objects::nonNull)
                .map(ResourceLocation::toString)
                .map(StringTag::valueOf)
                .forEachOrdered(nbt::add);
        return nbt;
    }

    public Set<MobEffect> readEffectsFromNBT(ListTag nbt) {
        return nbt.stream()
                .filter(StringTag.class::isInstance)
                .map(Tag::getAsString)
                .map(ResourceLocation::tryParse)
                .map(Registry.MOB_EFFECT::getOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public double getEffectValue(MobEffect effect) {
        if (ModEffects.CALMING.get().equals(effect)) return EnderBeeconConfig.beeconCalmingValue;
        else if (MobEffects.WATER_BREATHING.equals(effect)) return EnderBeeconConfig.beeconWaterBreathingValue;
        else if (MobEffects.FIRE_RESISTANCE.equals(effect)) return EnderBeeconConfig.beeconFireResistanceValue;
        else if (MobEffects.REGENERATION.equals(effect)) return EnderBeeconConfig.beeconRegenerationValue;

        ModConstants.LOGGER.error("Effect {} does not have an effect value", effect.getDescriptionId());
        return 1D;
    }

    //endregion
}
