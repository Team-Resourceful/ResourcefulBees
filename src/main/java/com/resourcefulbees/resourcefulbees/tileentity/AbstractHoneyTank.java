package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AbstractHoneyTank extends TileEntity {

    private FluidTank fluidTank;
    private LazyOptional<IFluidHandler> fluidOptional;

    public AbstractHoneyTank(TileEntityType<?> tileEntityType, int level) {
        super(tileEntityType);
        setFluidTank(new InternalFluidTank(level, honeyFluidPredicate(), this));
        setFluidOptional(LazyOptional.of(this::getFluidTank));
    }

    /**
     * WARN: This should only be used when implementing a unique fluid tank.
     *
     * @param tileEntityType the tile entity type of the tile entity.
     */
    public AbstractHoneyTank(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    protected static Predicate<FluidStack> honeyFluidPredicate() {
        return fluidStack -> fluidStack.getFluid().is(BeeInfoUtils.getFluidTag("resourcefulbees:resourceful_honey"));
    }

    public LazyOptional<IFluidHandler> getFluidOptional() {
        return fluidOptional;
    }

    public void setFluidOptional(LazyOptional<IFluidHandler> fluidOptional) {
        this.fluidOptional = fluidOptional;
    }

    public @NotNull FluidTank getFluidTank() {
        return fluidTank;
    }

    public void setFluidTank(FluidTank fluidTank) {
        this.fluidTank = fluidTank;
    }

    public void playSound(SoundEvent soundEvent) {
        assert this.level != null;
        this.level.playSound(null, this.worldPosition, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return getFluidOptional().cast();
        return super.getCapability(cap, side);
    }

    public int getFluidLevel() {
        float fillPercentage = ((float) getFluidTank().getFluidAmount()) / ((float) getFluidTank().getTankCapacity(0));
        return (int) Math.ceil(fillPercentage * 100);
    }

    // read from tag
    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT tag) {
        super.load(state, tag);
        readNBT(tag);
    }

    // write to tag
    @NotNull
    @Override
    public CompoundNBT save(@NotNull CompoundNBT tag) {
        super.save(tag);
        writeNBT(tag);
        return tag;
    }

    @Override
    protected void invalidateCaps() {
        this.fluidOptional.invalidate();
        super.invalidateCaps();
    }

    public CompoundNBT writeNBT(CompoundNBT tag) {
        if (getFluidTank().isEmpty()) return tag;
        tag.put("fluid", getFluidTank().writeToNBT(new CompoundNBT()));
        return tag;
    }

    public void readNBT(CompoundNBT tag) {
        getFluidTank().readFromNBT(tag.getCompound("fluid"));
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

    @NotNull
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
        FluidStack fluidStack = new FluidStack(getFluidTank().getFluid(), ModConstants.HONEY_PER_BOTTLE);
        ItemStack itemStack = new ItemStack(BeeInfoUtils.getHoneyBottle(getFluidTank().getFluid().getFluid()), 1);
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
            playSound(SoundEvents.BOTTLE_FILL);
        }
    }

    public void emptyBottle(PlayerEntity player, Hand hand) {
        FluidStack fluidStack = new FluidStack(BeeInfoUtils.getFluidFromBottle(player.getItemInHand(hand)), ModConstants.HONEY_PER_BOTTLE);
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
            playSound(SoundEvents.BOTTLE_EMPTY);
        }
    }

}
