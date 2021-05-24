package com.teamresourceful.resourcefulbees.tileentity;

import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AbstractHoneyTank extends BlockEntity {

    private FluidTank fluidTank;
    private LazyOptional<IFluidHandler> fluidOptional;

    public AbstractHoneyTank(BlockEntityType<?> tileEntityType, int level) {
        super(tileEntityType);
        setFluidTank(new InternalFluidTank(level, honeyFluidPredicate(), this));
        setFluidOptional(LazyOptional.of(this::getFluidTank));
    }

    /**
     * WARN: This should only be used when implementing a unique fluid tank.
     *
     * @param tileEntityType the tile entity type of the tile entity.
     */
    public AbstractHoneyTank(BlockEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    protected static Predicate<FluidStack> honeyFluidPredicate() {
        return fluidStack -> fluidStack.getFluid().is(BeeInfoUtils.getFluidTag("forge:honey"));
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
        this.level.playSound(null, this.worldPosition, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
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
    public void load(@NotNull BlockState state, @NotNull CompoundTag tag) {
        super.load(state, tag);
        readNBT(tag);
    }

    // write to tag
    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag tag) {
        super.save(tag);
        writeNBT(tag);
        return tag;
    }

    @Override
    protected void invalidateCaps() {
        this.fluidOptional.invalidate();
        super.invalidateCaps();
    }

    public CompoundTag writeNBT(CompoundTag tag) {
        if (getFluidTank().isEmpty()) return tag;
        tag.put("fluid", getFluidTank().writeToNBT(new CompoundTag()));
        return tag;
    }

    public void readNBT(CompoundTag tag) {
        getFluidTank().readFromNBT(tag.getCompound("fluid"));
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, 0, writeNBT(new CompoundTag()));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        readNBT(nbt);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        return writeNBT(nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundTag tag) {
        super.handleUpdateTag(state, tag);
        readNBT(tag);
    }

    public void fillBottle(Player player, InteractionHand hand) {
        FluidStack fluidStack = new FluidStack(getFluidTank().getFluid(), ModConstants.HONEY_PER_BOTTLE);
        ItemStack itemStack = new ItemStack(BeeInfoUtils.getHoneyBottleFromFluid(getFluidTank().getFluid().getFluid()), 1);
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

    public void emptyBottle(Player player, InteractionHand hand) {
        FluidStack fluidStack = new FluidStack(BeeInfoUtils.getHoneyFluidFromBottle(player.getItemInHand(hand)), ModConstants.HONEY_PER_BOTTLE);
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
