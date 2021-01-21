package com.resourcefulbees.resourcefulbees.tileentity;

import com.google.common.collect.Lists;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.EnderBeeconContainer;
import com.resourcefulbees.resourcefulbees.effects.ModEffects;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import com.resourcefulbees.resourcefulbees.network.packets.UpdateClientApiaryMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class EnderBeeconTileEntity extends HoneyTankTileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int RANGE = 10;
    private List<BeamSegment> beamSegments = Lists.newArrayList();
    private List<BeamSegment> beams = Lists.newArrayList();
    private int worldHeight = -1;
    private float[] afloat = {255f, 255f, 255f};
    private boolean updateBeecon = true;
    private boolean beeconActive = false;
    public AutomationSensitiveItemStackHandler h = new EnderBeeconTileEntity.TileStackHandler(5, getAcceptor(), getRemover());

    private List<BeeconEffect> effects = new LinkedList<>();


    public EnderBeeconTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType, TankTier.NETHER);
        fluidTank = new EnderBeeconTileEntity.InternalFluidTank(tier.maxFillAmount, honeyFluidPredicate());
    }

    public static MutableBoundingBox getDisruptorBox(BlockPos pos) {
        return MutableBoundingBox.createProper(pos.getX() + RANGE, pos.getY() + RANGE, pos.getZ() + RANGE, pos.getX() - RANGE, pos.getY() - RANGE, pos.getZ() - RANGE);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.ender_beecon");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new EnderBeeconContainer(id, world, pos, playerInventory);
    }

    @Override
    public CompoundNBT writeNBT(CompoundNBT tag) {
        tag.putInt("tier", TankTier.NETHER.getTier());
        tag.put("active_effects", writeEffectsToNBT(new CompoundNBT()));
        if (fluidTank.isEmpty()) return tag;
        tag.put("fluid", fluidTank.writeToNBT(new CompoundNBT()));
        return tag;
    }

    @Override
    public void readNBT(CompoundNBT tag) {
        fluidTank.readFromNBT(tag.getCompound("fluid"));
        effects = readEffectsFromNBT(tag.getCompound("active_effects"));
        tier = TankTier.NETHER;
        if (fluidTank.getTankCapacity(0) != tier.maxFillAmount) fluidTank.setCapacity(tier.maxFillAmount);
        if (fluidTank.getFluidAmount() > fluidTank.getTankCapacity(0))
            fluidTank.getFluid().setAmount(fluidTank.getTankCapacity(0));
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || slot == 0;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == 1;
    }

    @Override
    public void tick() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        BlockPos blockpos;
        if (this.worldHeight < j) {
            blockpos = this.pos;
            this.beams = Lists.newArrayList();
            this.worldHeight = blockpos.getY() - 1;
        } else {
            blockpos = new BlockPos(i, this.worldHeight + 1, k);
        }
        if (doEffects()) {
            fluidTank.drain(getDrain(), IFluidHandler.FluidAction.EXECUTE);
        }

        BeamSegment segment = this.beams.isEmpty() ? null : this.beams.get(this.beams.size() - 1);
        int l = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, i, k);

        for (int i1 = 0; i1 < 10 && blockpos.getY() <= l; ++i1) {
            BlockState blockstate = this.world.getBlockState(blockpos);
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
                if (segment == null || blockstate.getOpacity(this.world, blockpos) >= 15 && block != Blocks.BEDROCK) {
                    this.beams.clear();
                    this.worldHeight = l;
                    break;
                }
                segment.incrementHeight();
            }
            blockpos = blockpos.up();
            ++this.worldHeight;
        }


        if (this.world.getGameTime() % 80L == 0L) {
            if (!this.beamSegments.isEmpty() && !fluidTank.isEmpty()) {
                this.addEffectsToBees();
                this.playSound(SoundEvents.BLOCK_BEACON_AMBIENT);
            }
        }

        int j1 = fluidTank.getFluidAmount();
        if (this.worldHeight >= l) {
            this.worldHeight = -1;
            boolean flag = j1 > 0;
            this.beamSegments = this.beams;
            if (!this.world.isRemote) {
                if (flag && updateBeecon && !beeconActive) {
                    this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);
                    beeconActive = true;
                } else if (!flag && updateBeecon && beeconActive) {
                    this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
                    beeconActive = false;
                }
                updateBeecon = false;
            }
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 255, pos.getZ());
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(fluidTank.getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.pos, buffer), (ServerPlayerEntity) player);
        }
    }

    public static void syncApiaryToPlayersUsing(World world, BlockPos pos, CompoundNBT data) {
        NetPacketHandler.sendToAllLoaded(new UpdateClientApiaryMessage(pos, data), world, pos);
    }


    @OnlyIn(Dist.CLIENT)
    public List<EnderBeeconTileEntity.BeamSegment> getBeamSegments() {
        return beamSegments;
    }

    public List<BeeconEffect> getEffects() {
        return effects;
    }


    public static class BeamSegment {
        private final float[] colors;
        private int height;

        public BeamSegment(float[] p_i45669_1_) {
            this.colors = p_i45669_1_;
            this.height = 1;
        }

        protected void incrementHeight() {
            ++this.height;
        }

        /**
         * Returns RGB (0 to 1.0) colors of this beam segment
         */
        @OnlyIn(Dist.CLIENT)
        public float[] getColors() {
            return this.colors;
        }

        @OnlyIn(Dist.CLIENT)
        public int getHeight() {
            return this.height;
        }
    }

    public void playSound(SoundEvent p_205736_1_) {
        this.world.playSound((PlayerEntity) null, this.pos, p_205736_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }


    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
            super(slots, acceptor, remover);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    }

    public class InternalFluidTank extends FluidTank {

        public InternalFluidTank(int capacity) {
            super(capacity);
        }

        public InternalFluidTank(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            if (world != null) {
                BlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 2);
            }
            updateBeecon = true;
        }
    }

    private void addEffectsToBees() {
        if (!this.world.isRemote && doEffects()) {
            int range = getRange();
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.pos)).grow(range).expand(0.0D, (double) this.world.getHeight(), 0.0D);
            List<BeeEntity> mobs = this.world.getEntitiesWithinAABB(BeeEntity.class, axisalignedbb);
            int j = (9 + 8) * 20;
            for (BeeconEffect effect : effects) {
                if (!effect.active) continue;
                EffectInstance effectInstance = new EffectInstance(effect.effect, j, 0, true, true);
                for (BeeEntity mob : mobs) {
                    mob.addPotionEffect(effectInstance);
                }
            }
        }
    }

    public int getRange() {
        int range = 10;
        for (BeeconEffect effect : effects) {
            if (effect.active) range += 10;
        }
        return range;
    }

    public boolean doEffects() {
        if (fluidTank.isEmpty()) return false;
        for (BeeconEffect effect : effects) {
            if (effect.active) return true;
        }
        return false;
    }

    public void updateBeeconEffect(ResourceLocation effectLocation, boolean active) {
        Effect effect = ForgeRegistries.POTIONS.getValue(effectLocation);
        for (BeeconEffect e : effects) {
            if (e.effect == effect) {
                e.active = active;
            }
        }
        syncApiaryToPlayersUsing(this.world, this.getPos(), this.writeNBT(new CompoundNBT()));
    }

    public int getDrain() {
        int base = Config.BEECON_BASE_DRAIN.get();
        for (BeeconEffect e : effects) {
            if (Config.BEECON_DO_MULTIPLIER.get()) {
                if (e.active) base *= e.value;
            } else {
                if (e.active) base += e.value;
            }
        }
        return base;
    }

    public boolean toggleEffect(Effect effect) {
        BeeconEffect e = getEffect(effect);
        e.active = !e.active;
        return e.active;
    }

    public boolean getEffectActive(Effect effect) {
        return getEffect(effect).active;
    }

    public BeeconEffect getEffect(Effect effect) {
        for (BeeconEffect e : effects) {
            if (e.effect == effect) return e;
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

    public class BeeconEffect {
        public Effect effect;
        public int value;
        public boolean active;

        public BeeconEffect(Effect effect, int multiplier, boolean active) {
            this.effect = effect;
            this.value = multiplier;
            this.active = active;
        }
    }
}
