package com.dungeonderps.resourcefulbees.tileentity;

import com.dungeonderps.resourcefulbees.block.MechanicalCentrifugeBlock;
import com.dungeonderps.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.dungeonderps.resourcefulbees.container.MechanicalCentrifugeContainer;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.google.gson.JsonElement;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MechanicalCentrifugeTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int HONEYCOMB_SLOT = 0;
    public static final int BOTTLE_SLOT = 1;

    public static final int HONEY_BOTTLE = 2;
    public static final int OUTPUT1 = 3;
    public static final int OUTPUT2 = 4;

    public AutomationSensitiveItemStackHandler h = new MechanicalCentrifugeTileEntity.TileStackHandler(5, getAcceptor(), getRemover());
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> h);
    public LazyOptional<IItemHandler> automationHandler = LazyOptional.of(() -> new AutomationSensitiveItemStackHandler(5, AutomationSensitiveItemStackHandler.ACCEPT_FALSE, AutomationSensitiveItemStackHandler.REMOVE_FALSE));
    public int clicks = 0;
    public CentrifugeRecipe recipe;
    public ItemStack failedMatch = ItemStack.EMPTY;

    public MechanicalCentrifugeTileEntity() {
        super(RegistryHandler.MECHANICAL_CENTRIFUGE_ENTITY.get());
    }


    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            boolean dirty = false;
            CentrifugeRecipe irecipe = getRecipe();
            if (!h.getStackInSlot(HONEYCOMB_SLOT).isEmpty() && !h.getStackInSlot(BOTTLE_SLOT).isEmpty()) {
                if (this.canProcess(irecipe)) {
                    if (this.clicks > 0)
                        world.setBlockState(pos,getBlockState().with(MechanicalCentrifugeBlock.PROPERTY_ON,true));
                    if (this.clicks >= 8) {
                        this.clicks = 0;
                        this.processItem(irecipe);
                        dirty = true;
                        world.setBlockState(pos,getBlockState().with(MechanicalCentrifugeBlock.PROPERTY_ON,false));
                    }
                }
            } else {
                clicks = 0;
                world.setBlockState(pos,getBlockState().with(MechanicalCentrifugeBlock.PROPERTY_ON,false));
            }
            if (dirty) {
                this.markDirty();
            }
        }
    }

    public boolean canProcess(@Nullable CentrifugeRecipe recipe) {
        if (recipe != null && !recipe.multiblock) {
            List<Pair<ItemStack, Double>> outputs = recipe.outputs;
            ItemStack glass_bottle = h.getStackInSlot(BOTTLE_SLOT);
            ItemStack combs = h.getStackInSlot(HONEYCOMB_SLOT);
            JsonElement count = recipe.ingredient.serialize().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
            int inputAmount = count !=null ? count.getAsInt() : 1;
            List<ItemStack> outputSlots = new ArrayList<>(
                    Arrays.asList(
                            h.getStackInSlot(OUTPUT1),
                            h.getStackInSlot(OUTPUT2),
                            h.getStackInSlot(HONEY_BOTTLE)
                    )
            );
            int processScore = 0;
            if (outputSlots.get(0).isEmpty() && outputSlots.get(1).isEmpty() && outputSlots.get(2).isEmpty() && glass_bottle.getItem() == Items.GLASS_BOTTLE && combs.getCount() >= inputAmount) return true;
            else {
                for(int i=0;i<3;i++){
                    if (outputSlots.get(i).isEmpty()) processScore++;
                    else if (outputSlots.get(i).getItem() == outputs.get(i).getLeft().getItem()
                            && outputSlots.get(i).getCount() + outputs.get(i).getLeft().getCount() <= outputSlots.get(i).getMaxStackSize())processScore++;
                }
                if (combs.getCount() >= inputAmount) processScore++;
                if (processScore == 4 && glass_bottle.getItem() == Items.GLASS_BOTTLE)
                    return true;
                else {
                    if (world != null)
                        world.setBlockState(pos,getBlockState().with(MechanicalCentrifugeBlock.PROPERTY_ON,false));
                    return false;
                }
            }
        }
        return false;
    }

    private void processItem(@Nullable CentrifugeRecipe recipe) {
        if (recipe != null && this.canProcess(recipe)) {
            JsonElement count = recipe.ingredient.serialize().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
            int inputAmount = count !=null ? count.getAsInt() : 1;
            ItemStack comb = h.getStackInSlot(HONEYCOMB_SLOT);
            ItemStack glass_bottle = h.getStackInSlot(BOTTLE_SLOT);
            List<Pair<ItemStack, Integer>> slots = new ArrayList<>(
                    Arrays.asList(
                            Pair.of(h.getStackInSlot(OUTPUT1),OUTPUT1),
                            Pair.of(h.getStackInSlot(OUTPUT2),OUTPUT2),
                            Pair.of(h.getStackInSlot(HONEY_BOTTLE),HONEY_BOTTLE)
                    )
            );
            if (world != null)
                for(int i = 0; i < 3; i++){
                    Pair<ItemStack, Double> output = recipe.outputs.get(i);
                    if (output.getRight() >= world.rand.nextDouble()) {
                        if (slots.get(i).getLeft().isEmpty()) {
                            this.h.setStackInSlot(slots.get(i).getRight(), output.getLeft().copy());
                        } else if (slots.get(i).getLeft().getItem() == output.getLeft().getItem()) {
                            slots.get(i).getLeft().grow(output.getLeft().getCount());
                        }
                        if (slots.get(i).getRight().equals(HONEY_BOTTLE)) glass_bottle.shrink(1);
                    }
                }
            comb.shrink(Math.min(inputAmount, 127));
        }
        clicks = 0;
    }

    public CentrifugeRecipe getRecipe() {
        ItemStack input = h.getStackInSlot(HONEYCOMB_SLOT);
        if (input.isEmpty() || input == failedMatch) return null;
        if (world != null)
            if (recipe != null && recipe.matches(new RecipeWrapper(h), world)) return recipe;
            else {
                CentrifugeRecipe rec = world.getRecipeManager().getRecipe(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, new RecipeWrapper(h), this.world).orElse(null);
                if (rec == null) failedMatch = input;
                else failedMatch = ItemStack.EMPTY;
                return recipe = rec;
            }
        return null;
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        CompoundNBT inv = this.h.serializeNBT();
        tag.put("inv", inv);
        tag.putInt("clicks", clicks);
        return super.write(tag);
    }

    @Override
    public void read(@Nonnull BlockState state, CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        h.deserializeNBT(invTag);
        clicks = tag.getInt("clicks");
        super.read(state, tag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, CompoundNBT tag) {
        this.read(state, tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return automationHandler.cast();
        return super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation && (slot == 0 || slot == 1);
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == 2 || slot == 3 || slot == 4;
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        //noinspection ConstantConditions
        return new MechanicalCentrifugeContainer(id, world, pos, playerInventory);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.mechanical_centrifuge");
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
            super(slots,acceptor,remover);
        }
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    }
}
