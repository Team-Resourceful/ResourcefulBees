package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.item.CustomHoneyBottleItem;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class HoneyTankTileEntity extends TileEntity {

    public @NotNull FluidTank getFluidTank() {
        return fluidTank;
    }

    public void setFluidTank(FluidTank fluidTank) {
        this.fluidTank = fluidTank;
    }

    public TankTier getTier() {
        return tier;
    }

    public void setTier(TankTier tier) {
        this.tier = tier;
    }

    public enum TankTier {
        PURPUR(3, 64000, ModBlocks.PURPUR_HONEY_TANK, ModItems.PURPUR_HONEY_TANK_ITEM),
        NETHER(2, 16000, ModBlocks.NETHER_HONEY_TANK, ModItems.NETHER_HONEY_TANK_ITEM),
        WOODEN(1, 4000, ModBlocks.WOODEN_HONEY_TANK, ModItems.WOODEN_HONEY_TANK_ITEM);

        private final RegistryObject<Block> tankBlock;
        private final RegistryObject<Item> tankItem;
        int maxFillAmount;
        int tier;

        TankTier(int tier, int maxFillAmount, RegistryObject<Block> tankBlock, RegistryObject<Item> tankItem) {
            this.tier = tier;
            this.maxFillAmount = maxFillAmount;
            this.tankBlock = tankBlock;
            this.tankItem = tankItem;
        }

        public static TankTier getTier(Item item) {
            for (TankTier t : TankTier.values()) {
                if (t.tankItem.get() == item) return t;
            }
            return WOODEN;
        }

        public RegistryObject<Block> getTankBlock() {
            return tankBlock;
        }

        public RegistryObject<Item> getTankItem() {
            return tankItem;
        }

        public int getTier() {
            return tier;
        }

        public int getMaxFillAmount() {
            return maxFillAmount;
        }

        public static TankTier getTier(int tier) {
            for (TankTier t : TankTier.values()) {
                if (t.tier == tier) return t;
            }
            return WOODEN;
        }
    }

    public static final Logger LOGGER = LogManager.getLogger();

    private FluidTank fluidTank;
    public final LazyOptional<IFluidHandler> fluidOptional;
    public static final FluidStack HONEY_BOTTLE_FLUID_STACK = new FluidStack(ModFluids.HONEY_STILL.get(), ModConstants.HONEY_PER_BOTTLE);
    private TankTier tier;

    public HoneyTankTileEntity(TankTier tier) {
        super(ModTileEntityTypes.HONEY_TANK_TILE_ENTITY.get());
        setFluidTank(new InternalFluidTank(tier.maxFillAmount, honeyFluidPredicate()));
        fluidOptional = LazyOptional.of(this::getFluidTank);
        this.setTier(tier);
    }

    public HoneyTankTileEntity(TileEntityType<?> tileEntityType, TankTier tier) {
        super(tileEntityType);
        this.setFluidTank(new InternalFluidTank(tier.maxFillAmount, honeyFluidPredicate()));
        fluidOptional = LazyOptional.of(this::getFluidTank);
        this.setTier(tier);
    }


    protected static Predicate<FluidStack> honeyFluidPredicate() {
        return fluidStack -> fluidStack.getFluid().is(BeeInfoUtils.getFluidTag("forge:honey"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return fluidOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getBlockPos().below().south().west(), this.getBlockPos().above().north().east());
    }

    @Override
    protected void invalidateCaps() {
        this.fluidOptional.invalidate();
        super.invalidateCaps();
    }

    // read from tag
    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        super.load(state, tag);
        readNBT(tag);
    }

    // write to tag
    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        super.save(tag);
        writeNBT(tag);
        return tag;
    }

    public CompoundNBT writeNBT(CompoundNBT tag) {
        tag.putInt("tier", getTier().tier);
        if (getFluidTank().isEmpty()) return tag;
        tag.put("fluid", getFluidTank().writeToNBT(new CompoundNBT()));
        return tag;
    }

    public void readNBT(CompoundNBT tag) {
        getFluidTank().readFromNBT(tag.getCompound("fluid"));
        setTier(TankTier.getTier(tag.getInt("tier")));
        if (getFluidTank().getTankCapacity(0) != getTier().maxFillAmount) getFluidTank().setCapacity(getTier().maxFillAmount);
        if (getFluidTank().getFluidAmount() > getFluidTank().getTankCapacity(0))
            getFluidTank().getFluid().setAmount(getFluidTank().getTankCapacity(0));
    }


    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, writeNBT(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        readNBT(nbt);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        return writeNBT(nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        readNBT(tag);
    }

    public void fillBottle(PlayerEntity player, Hand hand) {
        FluidStack fluidStack = getFluidTank().getFluid();
        ItemStack itemStack;
        if (fluidStack.getFluid() instanceof HoneyFlowingFluid) {
            HoneyFlowingFluid fluid = (HoneyFlowingFluid) fluidStack.getFluid();
            itemStack = new ItemStack(fluid.getHoneyData().getHoneyBottleRegistryObject().get(), 1);
            fluidStack = new FluidStack(fluid.getHoneyData().getHoneyStillFluidRegistryObject().get(), ModConstants.HONEY_PER_BOTTLE);
        } else {
            if (fluidStack.getFluid() == ModFluids.CATNIP_HONEY_STILL.get()) {
                itemStack = new ItemStack(ModItems.CATNIP_HONEY_BOTTLE.get(), 1);
                fluidStack = new FluidStack(ModFluids.CATNIP_HONEY_STILL.get(), ModConstants.HONEY_PER_BOTTLE);
            } else {
                itemStack = new ItemStack(Items.HONEY_BOTTLE, 1);
                fluidStack = new FluidStack(ModFluids.HONEY_STILL.get(), ModConstants.HONEY_PER_BOTTLE);
            }
        }
        if (getFluidTank().isEmpty()) return;
        if (getFluidTank().getFluidAmount() >= ModConstants.HONEY_PER_BOTTLE) {
            getFluidTank().drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getCount() > 1) {
                stack.setCount(stack.getCount() - 1);
                player.addItem(itemStack);
            } else {
                player.setItemInHand(hand, itemStack);
            }
        }
        playSound(SoundEvents.BOTTLE_FILL);
    }

    public void emptyBottle(PlayerEntity player, Hand hand) {
        FluidStack fluidStack;
        if (player.getItemInHand(hand).getItem() instanceof CustomHoneyBottleItem) {
            CustomHoneyBottleItem item = (CustomHoneyBottleItem) player.getItemInHand(hand).getItem();
            fluidStack = new FluidStack(item.getHoneyData().getHoneyStillFluidRegistryObject().get(), ModConstants.HONEY_PER_BOTTLE);
        } else {
            if (player.getItemInHand(hand).getItem() == ModItems.CATNIP_HONEY_BOTTLE.get()) {
                fluidStack = new FluidStack(ModFluids.CATNIP_HONEY_STILL.get(), ModConstants.HONEY_PER_BOTTLE);
            } else {
                fluidStack = HONEY_BOTTLE_FLUID_STACK;
            }
        }
        if (!getFluidTank().getFluid().isFluidEqual(fluidStack) && !getFluidTank().isEmpty()) {
            return;
        }
        if (getFluidTank().getFluidAmount() + ModConstants.HONEY_PER_BOTTLE <= getFluidTank().getTankCapacity(0)) {
            getFluidTank().fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getCount() > 1) {
                stack.setCount(stack.getCount() - 1);
                player.addItem(new ItemStack(Items.GLASS_BOTTLE, 1));
            } else {
                player.setItemInHand(hand, new ItemStack(Items.GLASS_BOTTLE, 1));
            }
        }
        playSound(SoundEvents.BOTTLE_EMPTY);
    }

    public void playSound(SoundEvent soundEvent) {
        assert this.level != null;
        this.level.playSound(null, this.worldPosition, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public int getFluidLevel() {
        float fillPercentage = ((float) getFluidTank().getFluidAmount()) / ((float) getFluidTank().getTankCapacity(0));
        return (int) Math.ceil(fillPercentage * 100);
    }

    public class InternalFluidTank extends FluidTank {

        public InternalFluidTank(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            if (level != null) {
                BlockState state = level.getBlockState(worldPosition);
                level.sendBlockUpdated(worldPosition, state, state, 2);
            }
        }
    }
}
