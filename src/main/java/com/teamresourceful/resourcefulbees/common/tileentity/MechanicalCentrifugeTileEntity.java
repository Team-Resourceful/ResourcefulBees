package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.api.beedata.centrifuge.CentrifugeItemOutput;
import com.teamresourceful.resourcefulbees.api.beedata.outputs.ItemOutput;
import com.teamresourceful.resourcefulbees.common.block.MechanicalCentrifugeBlock;
import com.teamresourceful.resourcefulbees.common.container.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.container.MechanicalCentrifugeContainer;
import com.teamresourceful.resourcefulbees.common.recipe.CentrifugeRecipe;
import com.teamresourceful.resourcefulbees.common.registry.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.RecipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MechanicalCentrifugeTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int HONEYCOMB_SLOT = 0;

    public static final int OUTPUT1 = 1;
    public static final int OUTPUT2 = 2;
    public static final int OUTPUT3 = 3;

    private final AutomationSensitiveItemStackHandler itemStackHandler = new MechanicalCentrifugeTileEntity.TileStackHandler(4, getAcceptor(), getRemover());
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getItemStackHandler);
    private final LazyOptional<IItemHandler> automationHandler = LazyOptional.of(() -> new AutomationSensitiveItemStackHandler(4, AutomationSensitiveItemStackHandler.ACCEPT_FALSE, AutomationSensitiveItemStackHandler.REMOVE_FALSE));
    private int clicks;
    private CentrifugeRecipe recipe;
    private ItemStack failedMatch = ItemStack.EMPTY;

    public MechanicalCentrifugeTileEntity() {
        super(ModBlockEntityTypes.MECHANICAL_CENTRIFUGE_ENTITY.get());
    }


    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            boolean dirty = false;
            CentrifugeRecipe irecipe = getRecipe();
            if (!getItemStackHandler().getStackInSlot(HONEYCOMB_SLOT).isEmpty()) {
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

    private boolean isSlotValid(ItemStack slot, ItemStack item){
        if (slot.isEmpty()) return true;
        return ItemStack.isSame(slot, item) && (item.getCount() + slot.getCount()) <= slot.getMaxStackSize();
    }

    //TODO THIS MAY HAVE BROKE AFTER ADDING CENTRIFUGE OUTPUT OBJECTS!!! - epic
    public boolean canProcess(@Nullable CentrifugeRecipe recipe) {
        if (recipe != null && !recipe.isMultiblock()) {
            List<CentrifugeItemOutput> outputs = recipe.getItemOutputs();
            ItemStack comb = getItemStackHandler().getStackInSlot(HONEYCOMB_SLOT);
            List<ItemStack> outputSlots = Arrays.asList(getItemStackHandler().getStackInSlot(OUTPUT1), getItemStackHandler().getStackInSlot(OUTPUT2), getItemStackHandler().getStackInSlot(OUTPUT3));
            // see this ------------------------------------------------------------------------v
            for(int i=0;i<3;i++) if (!isSlotValid(outputSlots.get(i), outputs.get(i).getPool().next().getItemStack())) return false;
            if (comb.getCount() >= RecipeUtils.getIngredientCount(recipe.getIngredient())) {
                if (level != null) {
                    level.setBlockAndUpdate(worldPosition, getBlockState().setValue(MechanicalCentrifugeBlock.PROPERTY_ON, false));
                }
                return true;
            }
        }
        return false;
    }

    //TODO THIS MAY HAVE BROKE AFTER ADDING CENTRIFUGE OUTPUT OBJECTS!!! - epic
    private void processItem(@Nullable CentrifugeRecipe recipe) {
        if (recipe != null && this.canProcess(recipe) && level != null) {
            ItemStack comb = getItemStackHandler().getStackInSlot(HONEYCOMB_SLOT);
            List<ItemStack> slots = Arrays.asList(getItemStackHandler().getStackInSlot(OUTPUT1), getItemStackHandler().getStackInSlot(OUTPUT2), getItemStackHandler().getStackInSlot(OUTPUT3));
            for(int i = 0; i < 3; i++){
                // see this --------------------------------------------------v
                ItemOutput output = recipe.getItemOutputs().get(i).getPool().next();
                if (output.getChance() >= level.random.nextDouble()) {
                    if (slots.get(i).isEmpty()) {
                        getItemStackHandler().setStackInSlot(i + 1, output.getItemStack());
                    }else if (slots.get(i).getItem() == output.getItem()) {
                        slots.get(i).grow(output.getCount());
                    }
                }
            }
            comb.shrink(RecipeUtils.getIngredientCount(recipe.getIngredient()));
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
        tag.put("inv", this.getItemStackHandler().serializeNBT());
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
