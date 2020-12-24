package com.resourcefulbees.resourcefulbees.tileentity;

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
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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

public class HoneyTankTileEntity extends TileEntity implements ITickableTileEntity {


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

    public final FluidTank fluidTank;
    private final LazyOptional<IFluidHandler> fluidOptional;
    public static final FluidStack HONEY_BOTTLE_FLUID_STACK = new FluidStack(ModFluids.HONEY_STILL.get(), ModConstants.HONEY_PER_BOTTLE);
    private TankTier tier;

    public HoneyTankTileEntity(TankTier tier) {
        super(ModTileEntityTypes.HONEY_TANK_TILE_ENTITY.get());
        fluidTank = new FluidTank(tier.maxFillAmount, honeyFluidPredicate());
        fluidOptional = LazyOptional.of(() -> fluidTank);
        this.tier = tier;
    }

    private static Predicate<FluidStack> honeyFluidPredicate() {
        return fluidStack -> fluidStack.getFluid().isIn(BeeInfoUtils.getFluidTag("forge:honey"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return fluidOptional.cast();
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        return super.write(getNBT(tag));
    }

    public CompoundNBT getNBT(CompoundNBT tag) {
        tag.putInt("tier", tier.tier);
        if (fluidTank.isEmpty()) return tag;
        tag.put("fluid", fluidTank.writeToNBT(new CompoundNBT()));
        return tag;
    }

    public void updateNBT(CompoundNBT tag) {
        fluidTank.readFromNBT(tag.getCompound("fluid"));
        tier = TankTier.getTier(tag.getInt("tier"));
        if (fluidTank.getTankCapacity(0) != tier.maxFillAmount) fluidTank.setCapacity(tier.maxFillAmount);
        if (fluidTank.getFluidAmount() > fluidTank.getTankCapacity(0))
            fluidTank.getFluid().setAmount(fluidTank.getTankCapacity(0));
    }

    @Override
    public void fromTag(@Nonnull BlockState state, CompoundNBT tag) {
        updateNBT(tag);
        super.fromTag(state, tag);
    }

    @Override
    public void tick() {

    }

    public void fillBottle(PlayerEntity player, Hand hand) {
        if (!fluidTank.getFluid().isFluidEqual(HONEY_BOTTLE_FLUID_STACK) || fluidTank.isEmpty()) {
            return;
        }
        if (fluidTank.getFluidAmount() >= ModConstants.HONEY_PER_BOTTLE) {
            fluidTank.drain(HONEY_BOTTLE_FLUID_STACK, IFluidHandler.FluidAction.EXECUTE);
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getCount() > 1) {
                stack.setCount(stack.getCount() - 1);
                player.addItemStackToInventory(new ItemStack(Items.HONEY_BOTTLE, 1));
            } else {
                player.setHeldItem(hand, new ItemStack(Items.HONEY_BOTTLE, 1));
            }
        }
    }

    public void emptyBottle(PlayerEntity player, Hand hand) {
        if (!fluidTank.getFluid().isFluidEqual(HONEY_BOTTLE_FLUID_STACK) && !fluidTank.isEmpty()) {
            return;
        }
        if (fluidTank.getFluidAmount() + ModConstants.HONEY_PER_BOTTLE <= fluidTank.getTankCapacity(0)) {
            fluidTank.fill(HONEY_BOTTLE_FLUID_STACK, IFluidHandler.FluidAction.EXECUTE);
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getCount() > 1) {
                stack.setCount(stack.getCount() - 1);
                player.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE, 1));
            } else {
                player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE, 1));
            }
        }
    }
}
