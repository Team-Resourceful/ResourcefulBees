package com.resourcefulbees.resourcefulbees.tileentity;

import com.google.gson.JsonElement;
import com.resourcefulbees.resourcefulbees.block.MechanicalCentrifugeBlock;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.MechanicalCentrifugeContainer;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MechanicalCentrifugeTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int HONEYCOMB_SLOT = 0;
    public static final int BOTTLE_SLOT = 1;

    public static final int HONEY_BOTTLE = 2;
    public static final int OUTPUT1 = 3;
    public static final int OUTPUT2 = 4;

    private final AutomationSensitiveItemStackHandler itemStackHandler = new MechanicalCentrifugeTileEntity.TileStackHandler(5, getAcceptor(), getRemover());
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getItemStackHandler);
    private final LazyOptional<IItemHandler> automationHandler = LazyOptional.of(() -> new AutomationSensitiveItemStackHandler(5, AutomationSensitiveItemStackHandler.ACCEPT_FALSE, AutomationSensitiveItemStackHandler.REMOVE_FALSE));
    private int clicks;
    private CentrifugeRecipe recipe;
    private ItemStack failedMatch = ItemStack.EMPTY;

    public MechanicalCentrifugeTileEntity() {
        super(ModTileEntityTypes.MECHANICAL_CENTRIFUGE_ENTITY.get());
    }


    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            boolean dirty = false;
            CentrifugeRecipe irecipe = getRecipe();
            if (!getItemStackHandler().getStackInSlot(HONEYCOMB_SLOT).isEmpty() && !getItemStackHandler().getStackInSlot(BOTTLE_SLOT).isEmpty()) {
                if (this.canProcess(irecipe)) {
                    if (this.getClicks() > 0)
                        level.setBlockAndUpdate(worldPosition,getBlockState().setValue(MechanicalCentrifugeBlock.PROPERTY_ON,true));
                    if (this.getClicks() >= 8) {
                        this.setClicks(0);
                        this.processItem(irecipe);
                        dirty = true;
                        level.setBlockAndUpdate(worldPosition,getBlockState().setValue(MechanicalCentrifugeBlock.PROPERTY_ON,false));
                    }
                }
            } else {
                setClicks(0);
                level.setBlockAndUpdate(worldPosition,getBlockState().setValue(MechanicalCentrifugeBlock.PROPERTY_ON,false));
            }
            if (dirty) {
                this.setChanged();
            }
        }
    }

    public boolean canProcess(@Nullable CentrifugeRecipe recipe) {
        if (recipe != null && !recipe.multiblock) {
            List<Pair<ItemStack, Float>> outputs = recipe.itemOutputs;
            ItemStack glassBottle = getItemStackHandler().getStackInSlot(BOTTLE_SLOT);
            ItemStack combs = getItemStackHandler().getStackInSlot(HONEYCOMB_SLOT);
            JsonElement count = recipe.ingredient.toJson().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
            int inputAmount = count !=null ? count.getAsInt() : 1;
            List<ItemStack> outputSlots = new ArrayList<>(
                    Arrays.asList(
                            getItemStackHandler().getStackInSlot(OUTPUT1),
                            getItemStackHandler().getStackInSlot(OUTPUT2),
                            getItemStackHandler().getStackInSlot(HONEY_BOTTLE)
                    )
            );
            int processScore = 0;
            if (outputSlots.get(0).isEmpty() && outputSlots.get(1).isEmpty() && outputSlots.get(2).isEmpty() && glassBottle.getItem() == Items.GLASS_BOTTLE && combs.getCount() >= inputAmount) return true;
            else {
                for(int i=0;i<3;i++) {
                    if (outputSlots.get(i).isEmpty() || (outputSlots.get(i).getItem() == outputs.get(i).getLeft().getItem()
                            && outputSlots.get(i).getCount() + outputs.get(i).getLeft().getCount() <= outputSlots.get(i).getMaxStackSize())) {
                        processScore++;
                    }
                }
                if (combs.getCount() >= inputAmount) {
                    processScore++;
                }
                if (processScore == 4 && glassBottle.getItem() == Items.GLASS_BOTTLE) {
                    return true;
                } else {
                    if (level != null) {
                        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MechanicalCentrifugeBlock.PROPERTY_ON, false));
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private void processItem(@Nullable CentrifugeRecipe recipe) {
        if (recipe != null && this.canProcess(recipe)) {
            JsonElement count = recipe.ingredient.toJson().getAsJsonObject().get(BeeConstants.INGREDIENT_COUNT);
            int inputAmount = count !=null ? count.getAsInt() : 1;
            ItemStack comb = getItemStackHandler().getStackInSlot(HONEYCOMB_SLOT);
            ItemStack glassBottle = getItemStackHandler().getStackInSlot(BOTTLE_SLOT);
            List<Pair<ItemStack, Integer>> slots = new ArrayList<>(
                    Arrays.asList(
                            Pair.of(getItemStackHandler().getStackInSlot(OUTPUT1),OUTPUT1),
                            Pair.of(getItemStackHandler().getStackInSlot(OUTPUT2),OUTPUT2),
                            Pair.of(getItemStackHandler().getStackInSlot(HONEY_BOTTLE),HONEY_BOTTLE)
                    )
            );
            if (level != null)
                for(int i = 0; i < 3; i++){
                    Pair<ItemStack, Float> output = recipe.itemOutputs.get(i);
                    if (output.getRight() >= level.random.nextDouble()) {
                        if (slots.get(i).getLeft().isEmpty()) {
                            this.getItemStackHandler().setStackInSlot(slots.get(i).getRight(), output.getLeft().copy());
                        } else if (slots.get(i).getLeft().getItem() == output.getLeft().getItem()) {
                            slots.get(i).getLeft().grow(output.getLeft().getCount());
                        }
                        if (slots.get(i).getRight().equals(HONEY_BOTTLE)) glassBottle.shrink(1);
                    }
                }
            comb.shrink(inputAmount);
        }
        setClicks(0);
    }

    public CentrifugeRecipe getRecipe() {
        ItemStack input = getItemStackHandler().getStackInSlot(HONEYCOMB_SLOT);
        if (input.isEmpty() || input == getFailedMatch() || level == null) {
            return null;
        }
        if (recipe == null || !recipe.matches(new RecipeWrapper(getItemStackHandler()), level)) {
            CentrifugeRecipe rec = level.getRecipeManager().getRecipeFor(CentrifugeRecipe.CENTRIFUGE_RECIPE_TYPE, new RecipeWrapper(getItemStackHandler()), this.level).orElse(null);
            setFailedMatch(rec == null ? input : ItemStack.EMPTY);
            recipe = rec;
        }
        return recipe;
    }

    @Override
    public void load(@NotNull BlockState state, CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        getItemStackHandler().deserializeNBT(invTag);
        setClicks(tag.getInt("clicks"));
        super.load(state, tag);
    }

    @NotNull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT inv = this.getItemStackHandler().serializeNBT();
        tag.put("inv", inv);
        tag.putInt("clicks", getClicks());
        return super.save(tag);
    }

    @NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundNBT tag) { this.load(state, tag); }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return getAutomationHandler().cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.lazyOptional.invalidate();
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation && (slot == 0 || slot == 1);
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == 2 || slot == 3 || slot == 4;
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        //noinspection ConstantConditions
        return new MechanicalCentrifugeContainer(id, level, worldPosition, playerInventory);
    }

    @NotNull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.mechanical_centrifuge");
    }

    public @NotNull AutomationSensitiveItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }

    public LazyOptional<IItemHandler> getAutomationHandler() {
        return automationHandler;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public void setRecipe(CentrifugeRecipe recipe) {
        this.recipe = recipe;
    }

    public ItemStack getFailedMatch() {
        return failedMatch;
    }

    public void setFailedMatch(ItemStack failedMatch) {
        this.failedMatch = failedMatch;
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
            super(slots,acceptor,remover);
        }
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    }
}
