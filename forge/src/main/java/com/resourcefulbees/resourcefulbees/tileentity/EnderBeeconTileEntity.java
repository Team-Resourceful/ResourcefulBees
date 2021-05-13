package com.resourcefulbees.resourcefulbees.tileentity;

import com.google.common.collect.Lists;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.EnderBeeconContainer;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.network.packets.UpdateClientBeeconMessage;
import com.resourcefulbees.resourcefulbees.registry.ModEffects;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class EnderBeeconTileEntity extends AbstractHoneyTankContainer implements TickableBlockEntity, MenuProvider {

    private List<BeamSegment> beamSegments = Lists.newArrayList();
    private List<BeamSegment> beams = Lists.newArrayList();
    private int worldHeight = -1;
    private final float[] afloat = {255f, 255f, 255f};
    private boolean updateBeecon = true;
    private boolean beeconActive = false;
    private boolean playSound = true;
    private boolean showBeam = true;

    private static final int FLUID_PULL_RATE = Config.BEECON_PULL_AMOUNT.get();

    private List<BeeconEffect> effects;
    private int range = 1;

    public EnderBeeconTileEntity(BlockEntityType<?> tileEntityType) {
        super(tileEntityType);
        setFluidTank(new BeeconFluidTank(16000, honeyFluidPredicate(), this));
        setFluidOptional(LazyOptional.of(this::getFluidTank));
        effects = readEffectsFromNBT(new CompoundTag());
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("gui.resourcefulbees.ender_beecon");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        assert level != null;
        return new EnderBeeconContainer(id, level, worldPosition, playerInventory);
    }

    @Override
    public CompoundTag writeNBT(CompoundTag tag) {
        CompoundTag inv = this.getTileStackHandler().serializeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inv);
        tag.putBoolean(NBTConstants.NBT_SHOW_BEAM, isShowBeam());
        tag.putBoolean(NBTConstants.NBT_PLAY_SOUND, playSound);
        tag.putInt(NBTConstants.NBT_BEECON_RANGE, range);
        if (effects != null && !effects.isEmpty()) {
            tag.put("active_effects", writeEffectsToNBT(new CompoundTag()));
        }
        return super.writeNBT(tag);
    }

    @Override
    public void readNBT(CompoundTag tag) {
        CompoundTag invTag = tag.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        getFluidTank().readFromNBT(tag.getCompound(NBTConstants.NBT_FLUID));
        effects = readEffectsFromNBT(tag.getCompound("active_effects"));
        range = tag.getInt(NBTConstants.NBT_BEECON_RANGE);
        range = range > 0 ? range : 1;
        if (tag.contains(NBTConstants.NBT_SHOW_BEAM)) setShowBeam(tag.getBoolean(NBTConstants.NBT_SHOW_BEAM));
        if (tag.contains(NBTConstants.NBT_PLAY_SOUND)) playSound = tag.getBoolean(NBTConstants.NBT_PLAY_SOUND);
        super.readNBT(tag);
    }

    @Override
    public void tick() {
        int i = this.worldPosition.getX();
        int j = this.worldPosition.getY();
        int k = this.worldPosition.getZ();
        BlockPos blockpos;
        if (this.worldHeight < j) {
            blockpos = this.worldPosition;
            this.beams = Lists.newArrayList();
            this.worldHeight = blockpos.getY() - 1;
        } else {
            blockpos = new BlockPos(i, this.worldHeight + 1, k);
        }
        if (doEffects()) {
            getFluidTank().drain(getDrain(), IFluidHandler.FluidAction.EXECUTE);
        }

        BeamSegment segment = this.beams.isEmpty() ? null : this.beams.get(this.beams.size() - 1);
        assert this.level != null; //will fix later - epic
        int l = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, i, k);

        for (int i1 = 0; i1 < 10 && blockpos.getY() <= l; ++i1) {
            BlockState blockstate = this.level.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (afloat != null) {
                if (this.beams.size() <= 1) {
                    segment = new EnderBeeconTileEntity.BeamSegment(afloat);
                    this.beams.add(segment);
                } else if (segment != null) {
                    if (Arrays.equals(afloat, segment.colors)) {
                        segment.incrementHeight();
                    } else {
                        segment = new EnderBeeconTileEntity.BeamSegment(new float[]{afloat[0], afloat[1], afloat[2]});
                        this.beams.add(segment);
                    }
                }
            } else {
                if (segment == null || blockstate.getLightBlock(this.level, blockpos) >= 15 && block != Blocks.BEDROCK) {
                    this.beams.clear();
                    this.worldHeight = l;
                    break;
                }
                segment.incrementHeight();
            }
            blockpos = blockpos.above();
            ++this.worldHeight;
        }


        if (this.level.getGameTime() % 80L == 0L && !this.beamSegments.isEmpty() && !getFluidTank().isEmpty()) {
            AABB box = getEffectBox();
            List<Bee> bees = level.getEntitiesOfClass(Bee.class, box);
            bees.stream()
                    .filter(CustomBeeEntity.class::isInstance)
                    .map(CustomBeeEntity.class::cast)
                    .forEach(CustomBeeEntity::setHasDisruptorInRange);
            this.addEffectsToBees(bees);
            if (playSound) this.playSound(SoundEvents.BEACON_AMBIENT);
        }

        doPullProcess();

        int j1 = getFluidTank().getFluidAmount();
        if (this.worldHeight >= l) {
            this.worldHeight = -1;
            boolean flag = j1 > 0;
            this.beamSegments = this.beams;
            if (!this.level.isClientSide) {
                if (flag && updateBeecon && !beeconActive) {
                    this.playSound(SoundEvents.BEACON_ACTIVATE);
                    beeconActive = true;
                } else if (!flag && updateBeecon && beeconActive) {
                    this.playSound(SoundEvents.BEACON_DEACTIVATE);
                    beeconActive = false;
                }
                updateBeecon = false;
            }
        }
        super.tick();
    }

    private void pullFluid(Fluid i, IFluidHandler handler) {
        int remainingSpace = getFluidTank().getSpace();
        FluidStack amountDrained;
        if (FLUID_PULL_RATE > remainingSpace) {
            amountDrained = handler.drain(new FluidStack(i, remainingSpace), IFluidHandler.FluidAction.EXECUTE);
        } else {
            amountDrained = handler.drain(new FluidStack(i, FLUID_PULL_RATE), IFluidHandler.FluidAction.EXECUTE);
        }
        getFluidTank().fill(amountDrained, IFluidHandler.FluidAction.EXECUTE);
    }

    private void doPullProcess() {
        assert level != null; //will fix later - epic
        BlockEntity tileEntity = level.getBlockEntity(worldPosition.below());
        if (tileEntity == null) return;
        LazyOptional<IFluidHandler> fluidCap = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.UP);
        fluidCap.map(iFluidHandler -> {
            int tanks = iFluidHandler.getTanks();
            for (int i = 0; i < tanks; i++) {
                if (!iFluidHandler.getFluidInTank(i).isEmpty() &&
                        iFluidHandler.getFluidInTank(i).getFluid().is(HONEY_FLUID_TAG) &&
                        (getFluidTank().isEmpty() || iFluidHandler.getFluidInTank(i).getFluid() == getFluidTank().getFluid().getFluid())) {
                    pullFluid(iFluidHandler.getFluidInTank(i).getFluid(), iFluidHandler);
                    return true;
                }
            }
            return false;
        });
    }

    public void toggleSound() {
        playSound = !playSound;
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 2);
        }
        setDirty();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX(), worldPosition.getY() + 255D, worldPosition.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getViewDistance() {
        return 256.0D;
    }

    public void sendGUINetworkPacket(ContainerListener player) {
        if (player instanceof ServerPlayer && (!(player instanceof FakePlayer))) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeFluidStack(getFluidTank().getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayer) player);
        }
    }

    public static void syncApiaryToPlayersUsing(Level world, BlockPos pos, CompoundTag data) {
        NetPacketHandler.sendToAllLoaded(new UpdateClientBeeconMessage(pos, data), world, pos);
    }

    @OnlyIn(Dist.CLIENT)
    public List<EnderBeeconTileEntity.BeamSegment> getBeamSegments() {
        return beamSegments;
    }

    public List<BeeconEffect> getEffects() {
        return effects;
    }

    public AABB getEffectBox() {
        assert this.level != null;
        AABB pos = new AABB(this.worldPosition).inflate(getAdjustedRange());
        return new AABB(pos.minX, 0, pos.minZ, pos.maxX, this.level.getMaxBuildHeight(), pos.maxZ);
    }

    public void toggleBeam() {
        setShowBeam(!isShowBeam());
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 2);
        }
        setDirty();
    }

    public boolean isShowBeam() {
        return showBeam;
    }

    public void setShowBeam(boolean showBeam) {
        this.showBeam = showBeam;
    }

    public static class BeamSegment {
        private final float[] colors;
        private int height;

        public BeamSegment(float[] colors) {
            this.colors = colors;
            this.height = 1;
        }

        protected void incrementHeight() {
            ++this.height;
        }

        @OnlyIn(Dist.CLIENT)
        public int getHeight() {
            return this.height;
        }
    }

    public class BeeconFluidTank extends InternalFluidTank {

        public BeeconFluidTank(int capacity, Predicate<FluidStack> validator, BlockEntity tileEntity) {
            super(capacity, validator, tileEntity);
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            updateBeecon = true;
        }
    }

    private void addEffectsToBees(List<Bee> bees) {
        assert this.level != null;
        if (!this.level.isClientSide && doEffects()) {
            for (Bee mob : bees) {
                for (BeeconEffect effect : effects) {
                    if (!effect.isActive()) continue;
                    mob.addEffect(new MobEffectInstance(effect.getEffect(), 120, 0, true, true));
                }
            }
        }
    }

    public int getAdjustedRange() {
        return range * Config.BEECON_RANGE_PER_EFFECT.get();
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
        setDirty();
        syncApiaryToPlayersUsing(this.level, this.getBlockPos(), this.writeNBT(new CompoundTag()));
    }

    public boolean doEffects() {
        if (getFluidTank().isEmpty()) return false;
        for (BeeconEffect effect : effects) {
            if (effect.isActive()) return true;
        }
        return false;
    }

    public void updateBeeconEffect(ResourceLocation effectLocation, boolean active) {
        MobEffect effect = ForgeRegistries.POTIONS.getValue(effectLocation);
        for (BeeconEffect e : effects) {
            if (e.getEffect() == effect) {
                e.setActive(active);
            }
        }
        setDirty();
        syncApiaryToPlayersUsing(this.level, this.getBlockPos(), this.writeNBT(new CompoundTag()));
    }

    public int getDrain() {
        double base = Config.BEECON_BASE_DRAIN.get();
        for (BeeconEffect e : effects) {
            if (e.isActive()) base += e.getValue();
        }
        base = (base * (range * Config.BEECON_RANGE_MULTIPLIER.get()));
        return (int) Math.ceil(base);
    }

    public boolean getEffectActive(MobEffect effect) {
        BeeconEffect e = getEffect(effect);
        return e != null && e.isActive();
    }

    public BeeconEffect getEffect(MobEffect effect) {
        for (BeeconEffect e : effects) {
            if (e.getEffect() == effect) return e;
        }
        return null;
    }

    public CompoundTag writeEffectsToNBT(CompoundTag nbt) {
        nbt.putBoolean("calming", getEffectActive(ModEffects.CALMING.get()));
        nbt.putBoolean("water_breathing", getEffectActive(MobEffects.WATER_BREATHING));
        nbt.putBoolean("fire_resistance", getEffectActive(MobEffects.FIRE_RESISTANCE));
        nbt.putBoolean("regeneration", getEffectActive(MobEffects.REGENERATION));
        return nbt;
    }

    public List<BeeconEffect> readEffectsFromNBT(CompoundTag nbt) {
        List<BeeconEffect> beeconEffects = new LinkedList<>();
        beeconEffects.add(new BeeconEffect(ModEffects.CALMING.get(), Config.BEECON_CALMING_VALUE.get(), nbt.getBoolean("calming")));
        beeconEffects.add(new BeeconEffect(MobEffects.WATER_BREATHING, Config.BEECON_WATER_BREATHING_VALUE.get(), nbt.getBoolean("water_breathing")));
        beeconEffects.add(new BeeconEffect(MobEffects.FIRE_RESISTANCE, Config.BEECON_FIRE_RESISTANCE_VALUE.get(), nbt.getBoolean("fire_resistance")));
        beeconEffects.add(new BeeconEffect(MobEffects.REGENERATION, Config.BEECON_REGENERATION_VALUE.get(), nbt.getBoolean("regeneration")));
        return beeconEffects;
    }


    public static class BeeconEffect {
        private MobEffect effect;
        private double value;
        private boolean active;

        public BeeconEffect(MobEffect effect, double multiplier, boolean active) {
            this.setEffect(effect);
            this.setValue(multiplier);
            this.setActive(active);
        }

        public MobEffect getEffect() {
            return effect;
        }

        public void setEffect(MobEffect effect) {
            this.effect = effect;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
