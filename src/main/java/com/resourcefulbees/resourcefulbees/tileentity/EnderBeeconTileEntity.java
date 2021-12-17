package com.resourcefulbees.resourcefulbees.tileentity;

import com.google.common.collect.Sets;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.block.EnderBeecon;
import com.resourcefulbees.resourcefulbees.capabilities.HoneyFluidTank;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.EnderBeeconContainer;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.lib.TranslationConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.BeeconChangeMessage;
import com.resourcefulbees.resourcefulbees.network.packets.NewSyncGUIMessage;
import com.resourcefulbees.resourcefulbees.registry.ModEffects;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
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

public class EnderBeeconTileEntity extends TileEntity implements ISyncableGUI, ITickableTileEntity {

    public static final Set<Effect> ALLOWED_EFFECTS = Sets.newHashSet(ModEffects.CALMING.get(), Effects.WATER_BREATHING, Effects.FIRE_RESISTANCE, Effects.REGENERATION);

    private final HoneyFluidTank tank = new HoneyFluidTank(16000) {
        @Override
        protected void onContentsChanged() {
            updateBeecon = true;
            if (getLevel() != null && !getLevel().isClientSide())
                NetPacketHandler.sendToAllLoaded(new NewSyncGUIMessage(getBlockPos(), getBufToSend()), getLevel(), getBlockPos());
        }
    };
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    private boolean updateBeecon = true;
    private boolean beeconActive = false;

    private Set<Effect> effects = new LinkedHashSet<>();
    private int range = 1;

    public EnderBeeconTileEntity() {
        super(ModTileEntityTypes.ENDER_BEECON_TILE_ENTITY.get());
    }

    //region MENU

    @NotNull
    @Override
    public ITextComponent getDisplayName() {
        return TranslationConstants.Guis.BEECON;
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        if (level == null) return null;
        return new EnderBeeconContainer(id, level, worldPosition, playerInventory);
    }

    //endregion

    //region NBT

    @Override
    public @NotNull CompoundNBT save(@NotNull CompoundNBT nbt) {
        nbt.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundNBT()));
        nbt.putInt(NBTConstants.Beecon.RANGE, range);

        nbt.put(NBTConstants.Beecon.ACTIVE_EFFECTS, writeEffectsToNBT(new ListNBT()));
        return super.save(nbt);
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT nbt) {
        super.load(state, nbt);
        tank.readFromNBT(nbt.getCompound(NBTConstants.NBT_TANK));
        effects = readEffectsFromNBT(nbt.getList(NBTConstants.Beecon.ACTIVE_EFFECTS, Constants.NBT.TAG_STRING));
        range = nbt.getInt(NBTConstants.Beecon.RANGE);
        range = range > 0 ? range : 1;
    }

    @Override
    public @NotNull CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(this.getBlockState(), pkt.getTag());
    }

    //endregion

    @Override
    public void tick() {
        if (this.level == null) return;

        // do drain tank
        if (doEffects()) {
            tank.drain(getDrain(), IFluidHandler.FluidAction.EXECUTE);
        }

        // give effects
        if (this.level.getGameTime() % 80L == 0L && !tank.isEmpty()) {
            BlockState state = level.getBlockState(this.worldPosition);

            List<BeeEntity> bees = level.getEntitiesOfClass(BeeEntity.class, getEffectBox(this.level));
            bees.stream().filter(CustomBeeEntity.class::isInstance).map(CustomBeeEntity.class::cast).forEach(CustomBeeEntity::setHasDisruptorInRange);
            this.addEffectsToBees(this.level, bees);
            if (state.hasProperty(EnderBeecon.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeecon.SOUND)))
                level.playSound(null, this.worldPosition, SoundEvents.BEACON_AMBIENT, SoundCategory.BLOCKS, 1f, 1f);
        }

        // pull from below containers
        pullFluidFromBelow(this.level);

        // play activation sounds
        startUpCheck(this.level);
    }

    private void startUpCheck(@NotNull World level) {
        if (level.isClientSide) return;

        boolean flag = tank.getFluidAmount() > 0;
        BlockState state = level.getBlockState(this.worldPosition);
        if (updateBeecon) {
            if (flag && !beeconActive) {
                if (state.hasProperty(EnderBeecon.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeecon.SOUND)))
                    level.playSound(null, this.worldPosition, SoundEvents.BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
                beeconActive = true;
            } else if (!flag && beeconActive) {
                if (state.hasProperty(EnderBeecon.SOUND) && Boolean.TRUE.equals(state.getValue(EnderBeecon.SOUND)))
                    level.playSound(null, this.worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
                beeconActive = false;
            }
        }
        updateBeecon = false;
    }

    private void pullFluidFromBelow(@NotNull World level) {
        TileEntity tileEntity = level.getBlockEntity(worldPosition.below());
        if (tileEntity == null) return;
        if (tank.getSpace() == 0) return;
        tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.UP).ifPresent(handler -> {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack stack = handler.getFluidInTank(i);
                if (tank.isFluidValid(stack) && (tank.getFluid().isFluidEqual(stack) || tank.getFluid().isEmpty())) {
                    int pullAmount = Config.BEECON_PULL_AMOUNT.get();
                    if (pullAmount > tank.getSpace()) pullAmount = tank.getSpace();
                    FluidStack amountDrained = handler.drain(new FluidStack(stack.getFluid(), pullAmount), IFluidHandler.FluidAction.EXECUTE);
                    tank.fill(amountDrained, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        });
    }

    //region CLIENT

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX(), worldPosition.getY() + 255D, worldPosition.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getViewDistance() {
        return 256.0D;
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
    protected void invalidateCaps() {
        super.invalidateCaps();
        tankOptional.invalidate();
    }

    public Set<Effect> getEffects() {
        return effects;
    }

    public AxisAlignedBB getEffectBox(@NotNull World level) {
        AxisAlignedBB pos = new AxisAlignedBB(this.worldPosition).inflate(range);
        return new AxisAlignedBB(pos.minX, 0, pos.minZ, pos.maxX, level.getMaxBuildHeight(), pos.maxZ);
    }

    private void addEffectsToBees(@NotNull World level, List<BeeEntity> bees) {
        if (!level.isClientSide && doEffects()) {
            for (BeeEntity mob : bees)
                for (Effect effect : effects) mob.addEffect(new EffectInstance(effect, 120, 0, false, false));
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
            case EFFECT_OFF:
            case EFFECT_ON:
                Effect effect = Effect.byId(value);
                if (effect != null && ALLOWED_EFFECTS.contains(effect)) {
                    if (option.equals(BeeconChangeMessage.Option.EFFECT_ON)) effects.add(effect);
                    else effects.remove(effect);
                    this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
                }
                break;
            case BEAM:
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(EnderBeecon.BEAM, value == 1), Constants.BlockFlags.DEFAULT);
                break;
            case SOUND:
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(EnderBeecon.SOUND, value == 1), Constants.BlockFlags.DEFAULT);
                break;
            case RANGE:
                this.setRange(value);
                this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
                break;
            default:
                ResourcefulBees.LOGGER.error("UNKNOWN Beecon Configuration Option '{}' please report to github!", option);
        }
    }

    public int getDrain() {
        double base = Config.BEECON_BASE_DRAIN.get();
        for (Effect e : effects) base += getEffectValue(e);
        base = (base * (range * Config.BEECON_RANGE_MULTIPLIER.get() * 0.10d));
        return (int) Math.ceil(base);
    }

    public boolean hasEffect(Effect effect) {
        return effects.contains(effect);
    }

    //region effect nbt

    public ListNBT writeEffectsToNBT(ListNBT nbt) {
        for (Effect effect : effects) {
            ResourceLocation effectId = effect.getEffect().getRegistryName();
            if (effectId != null) nbt.add(StringNBT.valueOf(effectId.toString()));
        }
        return nbt;
    }

    public Set<Effect> readEffectsFromNBT(ListNBT nbt) {
        Set<Effect> beeconEffects = new LinkedHashSet<>();
        for (INBT inbt : nbt) {
            if (inbt instanceof StringNBT) {
                Effect effect = BeeInfoUtils.getEffect(inbt.getAsString());
                if (effect != null) {
                    beeconEffects.add(effect);
                }
            }
        }
        return beeconEffects;
    }

    public double getEffectValue(Effect effect) {
        if (ModEffects.CALMING.get().equals(effect)) return Config.BEECON_CALMING_VALUE.get();
        else if (Effects.WATER_BREATHING.equals(effect)) return Config.BEECON_WATER_BREATHING_VALUE.get();
        else if (Effects.FIRE_RESISTANCE.equals(effect)) return Config.BEECON_FIRE_RESISTANCE_VALUE.get();
        else if (Effects.REGENERATION.equals(effect)) return Config.BEECON_REGENERATION_VALUE.get();

        ResourcefulBees.LOGGER.error("Effect {} does not have an effect value", effect.getRegistryName());
        return 1D;
    }

    //endregion

    //region SYNCABLE GUI

    private PacketBuffer getBufToSend() {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeFluidStack(tank.getFluid());
        return buffer;
    }

    @Override
    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer)) {
            NetPacketHandler.sendToPlayer(new NewSyncGUIMessage(this.worldPosition, getBufToSend()), (ServerPlayerEntity) player);
        }
    }

    @Override
    public void handleGUINetworkPacket(PacketBuffer buffer) {
        tank.setFluid(buffer.readFluidStack());
    }

    //endregion
}