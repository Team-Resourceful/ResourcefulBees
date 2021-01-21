package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.item.CustomHoneyBottleItem;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class HoneyTankTileEntity extends TileEntity {


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

    public FluidTank fluidTank;
    public final LazyOptional<IFluidHandler> fluidOptional;
    public static final FluidStack HONEY_BOTTLE_FLUID_STACK = new FluidStack(ModFluids.HONEY_STILL.get(), ModConstants.HONEY_PER_BOTTLE);
    public TankTier tier;

    public HoneyTankTileEntity(TankTier tier) {
        super(ModTileEntityTypes.HONEY_TANK_TILE_ENTITY.get());
        fluidTank = new InternalFluidTank(tier.maxFillAmount, honeyFluidPredicate());
        fluidOptional = LazyOptional.of(() -> fluidTank);
        this.tier = tier;
    }

    public HoneyTankTileEntity(TileEntityType<?> tileEntityType, TankTier tier) {
        super(tileEntityType);
        this.fluidTank = new InternalFluidTank(tier.maxFillAmount, honeyFluidPredicate());
        fluidOptional = LazyOptional.of(() -> fluidTank);
        this.tier = tier;
    }


    protected static Predicate<FluidStack> honeyFluidPredicate() {
        return fluidStack -> fluidStack.getFluid().isIn(BeeInfoUtils.getFluidTag("forge:honey"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return fluidOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.fluidOptional.invalidate();
        super.invalidateCaps();
    }

    // read from tag
    @Override
    public void fromTag(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        super.fromTag(state, tag);
        readNBT(tag);
    }

    // write to tag
    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT tag) {
        super.write(tag);
        writeNBT(tag);
        return tag;
    }

    public CompoundNBT writeNBT(CompoundNBT tag) {
        tag.putInt("tier", tier.tier);
        if (fluidTank.isEmpty()) return tag;
        tag.put("fluid", fluidTank.writeToNBT(new CompoundNBT()));
        return tag;
    }

    public void readNBT(CompoundNBT tag) {
        fluidTank.readFromNBT(tag.getCompound("fluid"));
        tier = TankTier.getTier(tag.getInt("tier"));
        if (fluidTank.getTankCapacity(0) != tier.maxFillAmount) fluidTank.setCapacity(tier.maxFillAmount);
        if (fluidTank.getFluidAmount() > fluidTank.getTankCapacity(0))
            fluidTank.getFluid().setAmount(fluidTank.getTankCapacity(0));
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 0, writeNBT(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        readNBT(nbt);
    }

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
        FluidStack fluidStack = fluidTank.getFluid();
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
        if (fluidTank.isEmpty()) return;
        if (fluidTank.getFluidAmount() >= ModConstants.HONEY_PER_BOTTLE) {
            fluidTank.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getCount() > 1) {
                stack.setCount(stack.getCount() - 1);
                player.addItemStackToInventory(itemStack);
            } else {
                player.setHeldItem(hand, itemStack);
            }
        }
        playSound(SoundEvents.ITEM_BOTTLE_FILL);
    }

    public void emptyBottle(PlayerEntity player, Hand hand) {
        FluidStack fluidStack;
        if (player.getHeldItem(hand).getItem() instanceof CustomHoneyBottleItem) {
            CustomHoneyBottleItem item = (CustomHoneyBottleItem) player.getHeldItem(hand).getItem();
            fluidStack = new FluidStack(item.getHoneyData().getHoneyStillFluidRegistryObject().get(), ModConstants.HONEY_PER_BOTTLE);
        } else {
            if (player.getHeldItem(hand).getItem() == ModItems.CATNIP_HONEY_BOTTLE.get()) {
                fluidStack = new FluidStack(ModFluids.CATNIP_HONEY_STILL.get(), ModConstants.HONEY_PER_BOTTLE);
            } else {
                fluidStack = HONEY_BOTTLE_FLUID_STACK;
            }
        }
        if (!fluidTank.getFluid().isFluidEqual(fluidStack) && !fluidTank.isEmpty()) {
            return;
        }
        if (fluidTank.getFluidAmount() + ModConstants.HONEY_PER_BOTTLE <= fluidTank.getTankCapacity(0)) {
            fluidTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getCount() > 1) {
                stack.setCount(stack.getCount() - 1);
                player.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE, 1));
            } else {
                player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE, 1));
            }
        }
        playSound(SoundEvents.ITEM_BOTTLE_EMPTY);
    }

    public void playSound(SoundEvent p_205736_1_) {
        this.world.playSound((PlayerEntity) null, this.pos, p_205736_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public int getLevel() {
        float fillPercentage = ((float) fluidTank.getFluidAmount()) / ((float) fluidTank.getTankCapacity(0));
        return (int) Math.ceil(fillPercentage * 100);
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
        }
    }
}
