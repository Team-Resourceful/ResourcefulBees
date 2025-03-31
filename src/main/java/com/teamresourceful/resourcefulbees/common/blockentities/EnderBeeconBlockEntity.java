package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.EnderBeeconBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.config.EnderBeeconConfig;
import com.teamresourceful.resourcefulbees.common.entities.entity.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconChangePacket.Option;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEffects;
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidBlock;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.impl.InsertOnlyFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.WrappedBlockFluidContainer;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class EnderBeeconBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker, BotariumFluidBlock<WrappedBlockFluidContainer>, ContentMenuProvider<PositionContent> {

    public static final Set<MobEffect> ALLOWED_EFFECTS = Set.of(ModEffects.CALMING.get(), MobEffects.WATER_BREATHING, MobEffects.FIRE_RESISTANCE, MobEffects.REGENERATION);

    private WrappedBlockFluidContainer tank;

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
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        tag.put(NBTConstants.NBT_TANK, tank.serialize(new CompoundTag()));
        tag.putInt(NBTConstants.Beecon.RANGE, range);
        if (effects != null && !effects.isEmpty())
            tag.put(NBTConstants.Beecon.ACTIVE_EFFECTS, writeEffectsToNBT(new ListTag()));
        return tag;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        tank.deserialize(tag.getCompound(NBTConstants.NBT_TANK));
        effects = readEffectsFromNBT(tag.getList(NBTConstants.Beecon.ACTIVE_EFFECTS, Tag.TAG_STRING));
        range = tag.getInt(NBTConstants.Beecon.RANGE);
        range = Math.max(range, 10);
    }
    //endregion

    //region NBT
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(NBTConstants.Beecon.RANGE, range);
        if (effects != null && !effects.isEmpty())
            tag.put(NBTConstants.Beecon.ACTIVE_EFFECTS, writeEffectsToNBT(new ListTag()));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        effects = readEffectsFromNBT(tag.getList(NBTConstants.Beecon.ACTIVE_EFFECTS, Tag.TAG_STRING));
        range = tag.getInt(NBTConstants.Beecon.RANGE);
        range = Math.max(range, 10);
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
            FluidHolder holder = this.getFluid().copyWithAmount(this.getDrain());
            this.tank.internalExtract(holder, false);
        }

        // give effects
        if (level.getGameTime() % 80L == 0L && !this.tank.isEmpty()) {
            List<Bee> bees = level.getEntitiesOfClass(Bee.class, getEffectBox(level, pos, this.range));
            bees.stream().filter(CustomBeeEntity.class::isInstance).map(CustomBeeEntity.class::cast).forEach(CustomBeeEntity::setDisruptorInRange);
            this.effects
                .stream()
                .map(effect -> new MobEffectInstance(effect, 120, 0, false, false))
                .forEach(effect -> bees.forEach(bee -> bee.addEffect(new MobEffectInstance(effect))));

            if (state.hasProperty(EnderBeeconBlock.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeeconBlock.SOUND)))
                level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1f, 1f);
        }

        // pull from below containers
        pullFluidFromBelow(level, pos);

        // play activation sounds
        startUpCheck(level, pos, state);
    }

    private void startUpCheck(@NotNull Level level, BlockPos pos, BlockState state) {
        boolean flag = this.getFluid().getFluidAmount() > 0;
        if (this.updateBeecon) {
            if (flag && !this.beeconActive) {
                if (state.hasProperty(EnderBeeconBlock.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeeconBlock.SOUND)))
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1f, 1f);
                this.beeconActive = true;
            } else if (!flag && this.beeconActive) {
                if (state.hasProperty(EnderBeeconBlock.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeeconBlock.SOUND)))
                    level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1f, 1f);
                this.beeconActive = false;
            }
        }
        this.updateBeecon = false;
    }

    private void pullFluidFromBelow(@NotNull Level level, BlockPos pos) {
        if (this.getFluid().getFluidAmount() >= this.tank.getTankCapacity(0)) return;
        BlockEntity tileEntity = level.getBlockEntity(pos.below());
        if (tileEntity == null) return;

        FluidHooks.safeGetBlockFluidManager(tileEntity, Direction.UP).ifPresent(manager -> {
            for (FluidHolder fluidTank : manager.getFluidTanks()) {
                if (fluidTank.isEmpty()) continue;
                FluidHolder holder = fluidTank.copyWithAmount(Math.min(fluidTank.getFluidAmount(), EnderBeeconConfig.beeconPullAmount));
                FluidHolder extracted = manager.extractFluid(holder, true);
                if (!extracted.isEmpty() && this.tank.internalInsert(extracted, true) > 0) {
                    manager.extractFluid(extracted, false);
                    this.tank.internalInsert(extracted, false);
                }
            }
        });
    }

    public FluidHolder getFluid() {
        return getFluidContainer().getFluids().get(0);
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

    public void handleBeeconUpdate(Option option, int value) {
        if (this.level == null) return;
        switch (option) {
            case EFFECT_OFF, EFFECT_ON -> {
                MobEffect effect = MobEffect.byId(value);
                if (effect != null && ALLOWED_EFFECTS.contains(effect)) {
                    if (option.equals(Option.EFFECT_ON)) effects.add(effect);
                    else effects.remove(effect);
                    this.sendToPlayersTrackingChunk();
                }
            }
            case BEAM ->
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(EnderBeeconBlock.BEAM, value == 1), Block.UPDATE_ALL);
            case SOUND ->
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(EnderBeeconBlock.SOUND, value == 1), Block.UPDATE_ALL);
            case RANGE -> {
                this.setRange(value);
                this.sendToPlayersTrackingChunk();
            }
            default ->
                ModConstants.LOGGER.error("UNKNOWN Beecon Configuration Option '{}' please report to GitHub!", option);
        }
    }

    public int getDrain() {
        double base = EnderBeeconConfig.beeconBaseDrain;
        for (MobEffect e : effects) {
            base += getEffectValue(e);
        }
        base = (base * (range * EnderBeeconConfig.beeconRangeMultiplier * 0.10d));
        return (int) base;
    }

    public boolean hasEffect(MobEffect effect) {
        return effects.contains(effect);
    }

    //region effect nbt

    public ListTag writeEffectsToNBT(ListTag nbt) {
        effects.stream()
            .map(BuiltInRegistries.MOB_EFFECT::getKey)
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
            .map(BuiltInRegistries.MOB_EFFECT::getOptional)
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


    @Override
    public void setChanged() {
        super.setChanged();
        sendToPlayersTrackingChunk();
        updateBeecon = true;
    }

    @Override
    public WrappedBlockFluidContainer getFluidContainer() {
        if (tank == null) {
            tank = new WrappedBlockFluidContainer(this, new InsertOnlyFluidContainer(i -> FluidHooks.buckets(16), 1, (amount, fluid) -> fluid.is(ModFluidTags.HONEY)));
        }
        return this.tank;
    }

    @Override
    public PositionContent createContent() {
        return new PositionContent(this.getBlockPos());
    }
}
