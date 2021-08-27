package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.EnderBeeconContainer;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.network.packets.UpdateClientBeeconMessage;
import com.resourcefulbees.resourcefulbees.registry.ModEffects;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class EnderBeeconTileEntity extends AbstractHoneyTankContainer implements ITickableTileEntity, INamedContainerProvider {
    //TODO see about trimming the duplicate code if possible - epic

    private boolean updateBeecon = true;
    private boolean beeconActive = false;
    private boolean playSound = true;
    private boolean showBeam = true;

    private static final int FLUID_PULL_RATE = Config.BEECON_PULL_AMOUNT.get();

    private List<BeeconEffect> effects;

    public EnderBeeconTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        setFluidTank(new BeeconFluidTank(16000, honeyFluidPredicate(), this));
        setFluidOptional(LazyOptional.of(this::getFluidTank));
        effects = readEffectsFromNBT(new CompoundNBT());
    }

    @NotNull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.ender_beecon");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        assert level != null;
        return new EnderBeeconContainer(id, level, worldPosition, playerInventory);
    }

    @Override
    public CompoundNBT writeNBT(CompoundNBT tag) {
        CompoundNBT inv = this.getTileStackHandler().serializeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inv);
        tag.putBoolean(NBTConstants.NBT_SHOW_BEAM, isShowBeam());
        tag.putBoolean(NBTConstants.NBT_PLAY_SOUND, playSound);
        if (effects != null && !effects.isEmpty()) {
            tag.put("active_effects", writeEffectsToNBT(new CompoundNBT()));
        }
        return super.writeNBT(tag);
    }

    @Override
    public void readNBT(CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        getFluidTank().readFromNBT(tag.getCompound(NBTConstants.NBT_FLUID));
        effects = readEffectsFromNBT(tag.getCompound("active_effects"));
        if (tag.contains(NBTConstants.NBT_SHOW_BEAM)) setShowBeam(tag.getBoolean(NBTConstants.NBT_SHOW_BEAM));
        if (tag.contains(NBTConstants.NBT_PLAY_SOUND)) playSound = tag.getBoolean(NBTConstants.NBT_PLAY_SOUND);
        super.readNBT(tag);
    }

    @Override
    public void tick() {
        if (this.level == null) return;
        // do drain tank
        if (doEffects()) {
            getFluidTank().drain(getDrain(), IFluidHandler.FluidAction.EXECUTE);
        }

        // give effects
        if (this.level.getGameTime() % 80L == 0L && !getFluidTank().isEmpty()) {
            AxisAlignedBB box = getEffectBox();
            List<BeeEntity> bees = level.getEntitiesOfClass(BeeEntity.class, box);
            bees.stream()
                    .filter(CustomBeeEntity.class::isInstance)
                    .map(CustomBeeEntity.class::cast)
                    .forEach(CustomBeeEntity::setHasDisruptorInRange);
            this.addEffectsToBees(bees);
            if (playSound) this.playSound(SoundEvents.BEACON_AMBIENT);
        }

        // pull from below containers
        doPullProcess();

        // play activation sounds
        boolean flag = getFluidTank().getFluidAmount() > 0;
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
        TileEntity tileEntity = level.getBlockEntity(worldPosition.below());
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
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX(), worldPosition.getY() + 255D, worldPosition.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getViewDistance() {
        return 256.0D;
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(getFluidTank().getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    public static void syncApiaryToPlayersUsing(World world, BlockPos pos, CompoundNBT data) {
        NetPacketHandler.sendToAllLoaded(new UpdateClientBeeconMessage(pos, data), world, pos);
    }


    public List<BeeconEffect> getEffects() {
        return effects;
    }

    public AxisAlignedBB getEffectBox() {
        assert this.level != null;
        return (new AxisAlignedBB(this.worldPosition)).inflate(getRange()).expandTowards(0.0D, this.level.getMaxBuildHeight(), 0.0D);
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

    public class BeeconFluidTank extends InternalFluidTank {

        public BeeconFluidTank(int capacity, Predicate<FluidStack> validator, TileEntity tileEntity) {
            super(capacity, validator, tileEntity);
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            updateBeecon = true;
        }
    }

    private void addEffectsToBees(List<BeeEntity> bees) {
        assert this.level != null;
        if (!this.level.isClientSide && doEffects()) {
            for (BeeEntity mob : bees) {
                for (BeeconEffect effect : effects) {
                    if (!effect.isActive()) continue;
                    mob.addEffect(new EffectInstance(effect.getEffect(), 120, 0, false, false));
                }
            }
        }
    }

    public int getRange() {
        int range = Config.BEECON_RANGE_PER_EFFECT.get();
        for (BeeconEffect effect : effects) {
            if (effect.isActive()) range += Config.BEECON_RANGE_PER_EFFECT.get();
        }
        return range;
    }

    public boolean doEffects() {
        if (getFluidTank().isEmpty()) return false;
        for (BeeconEffect effect : effects) {
            if (effect.isActive()) return true;
        }
        return false;
    }

    public void updateBeeconEffect(ResourceLocation effectLocation, boolean active) {
        Effect effect = ForgeRegistries.POTIONS.getValue(effectLocation);
        for (BeeconEffect e : effects) {
            if (e.getEffect() == effect) {
                e.setActive(active);
            }
        }
        syncApiaryToPlayersUsing(this.level, this.getBlockPos(), this.writeNBT(new CompoundNBT()));
    }

    public int getDrain() {
        double base = Config.BEECON_BASE_DRAIN.get();
        for (BeeconEffect e : effects) {
            if (Config.BEECON_DO_MULTIPLIER.get()) {
                if (e.isActive()) base *= e.getValue();
            } else {
                if (e.isActive()) base += e.getValue();
            }
        }
        return (int) Math.ceil(base);
    }

    public boolean getEffectActive(Effect effect) {
        BeeconEffect e = getEffect(effect);
        return e != null && e.isActive();
    }

    public BeeconEffect getEffect(Effect effect) {
        for (BeeconEffect e : effects) {
            if (e.getEffect() == effect) return e;
        }
        return null;
    }

    public CompoundNBT writeEffectsToNBT(CompoundNBT nbt) {
        nbt.putBoolean("calming", getEffectActive(ModEffects.CALMING.get()));
        nbt.putBoolean("water_breathing", getEffectActive(Effects.WATER_BREATHING));
        nbt.putBoolean("fire_resistance", getEffectActive(Effects.FIRE_RESISTANCE));
        nbt.putBoolean("regeneration", getEffectActive(Effects.REGENERATION));
        return nbt;
    }

    public List<BeeconEffect> readEffectsFromNBT(CompoundNBT nbt) {
        List<BeeconEffect> beeconEffects = new LinkedList<>();
        beeconEffects.add(new BeeconEffect(ModEffects.CALMING.get(), Config.BEECON_CALMING_VALUE.get(), nbt.getBoolean("calming")));
        beeconEffects.add(new BeeconEffect(Effects.WATER_BREATHING, Config.BEECON_WATER_BREATHING_VALUE.get(), nbt.getBoolean("water_breathing")));
        beeconEffects.add(new BeeconEffect(Effects.FIRE_RESISTANCE, Config.BEECON_FIRE_RESISTANCE_VALUE.get(), nbt.getBoolean("fire_resistance")));
        beeconEffects.add(new BeeconEffect(Effects.REGENERATION, Config.BEECON_REGENERATION_VALUE.get(), nbt.getBoolean("regeneration")));
        return beeconEffects;
    }


    public static class BeeconEffect {
        private Effect effect;
        private double value;
        private boolean active;

        public BeeconEffect(Effect effect, double multiplier, boolean active) {
            this.setEffect(effect);
            this.setValue(multiplier);
            this.setActive(active);
        }

        public Effect getEffect() {
            return effect;
        }

        public void setEffect(Effect effect) {
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
