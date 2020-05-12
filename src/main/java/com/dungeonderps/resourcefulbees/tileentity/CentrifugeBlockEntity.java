
package com.dungeonderps.resourcefulbees.tileentity;
//import com.tfar.beesourceful.block.CentrifugeBlock;
//import com.tfar.beesourceful.inventory.AutomationSensitiveItemStackHandler;
//import com.tfar.beesourceful.recipe.CentrifugeRecipe;
//import com.tfar.beesourceful.recipe.CentrifugeRecipeType;
import com.dungeonderps.resourcefulbees.RegistryHandler;
import com.dungeonderps.resourcefulbees.block.CentrifugeBlock;
import com.dungeonderps.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.dungeonderps.resourcefulbees.container.CentrifugeContainer;
import com.dungeonderps.resourcefulbees.recipe.CentrifugeRecipe;
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
import java.util.List;

public class CentrifugeBlockEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {


    public static final int HONEYCOMB_SLOT = 0;
    public static final int BOTTLE_SLOT = 1;

    public static final int HONEY_BOTTLE = 2;
    public static final int OUTPUT1 = 3;
    public static final int OUTPUT2 = 4;

    public AutomationSensitiveItemStackHandler h = new TileStackHandler(5);
    public LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> h);
    public int time = 0;
    public CentrifugeRecipe recipe;
    public ItemStack failedMatch = ItemStack.EMPTY;
    public int totalTime = 0;

    public CentrifugeBlockEntity() {
        super(RegistryHandler.HONEYCOMB_BLOCK_ENTITY.get());
    }


    @Override
    public void tick() {
        if (!world.isRemote) {
            boolean dirty = false;
            if (!h.getStackInSlot(HONEYCOMB_SLOT).isEmpty() && !h.getStackInSlot(BOTTLE_SLOT).isEmpty()) {
                CentrifugeRecipe irecipe = getRecipe();
                boolean valid = this.canProcess(irecipe);
                if (valid) {
                    world.setBlockState(pos,getBlockState().with(CentrifugeBlock.PROPERTY_ON,true));
                    this.totalTime = this.getTime();
                    ++this.time;
                    if (this.time == this.totalTime) {
                        this.time = 0;
                        this.totalTime = this.getTime();
                        this.processItem(irecipe);
                        dirty = true;
                    }
                }
            } else {
                time = 0;
                world.setBlockState(pos,getBlockState().with(CentrifugeBlock.PROPERTY_ON,false));
            }
            if (dirty) {
                this.markDirty();
            }
        }
    }


    protected boolean canProcess(@Nullable CentrifugeRecipe recipe) {
        if (recipe != null) {
            List<Pair<ItemStack, Double>> outputs = recipe.outputs;
            ItemStack recipeOutput1 = outputs.get(0).getLeft();
            ItemStack recipeOutput2 = outputs.get(1).getLeft();
            ItemStack stackInOutput1 = h.getStackInSlot(OUTPUT1);
            ItemStack stackInOutput2 = h.getStackInSlot(OUTPUT2);

            if (stackInOutput1.isEmpty() && stackInOutput2.isEmpty()) return true;

            if (stackInOutput1.isEmpty() && stackInOutput2.getItem() == recipeOutput2.getItem())return true;

            if (stackInOutput2.isEmpty() && stackInOutput1.getItem() == recipeOutput1.getItem())return true;

            else if (!stackInOutput1.isItemEqual(recipeOutput1) || !stackInOutput2.isItemEqual(recipeOutput2)) return false;
            else return stackInOutput1.getCount() + recipeOutput1.getCount() <= stackInOutput1.getMaxStackSize() &&
                        stackInOutput2.getCount() + recipeOutput2.getCount() <= stackInOutput2.getMaxStackSize();
        }
        return false;
    }

    private void processItem(@Nullable CentrifugeRecipe recipe) {
        if (recipe != null && this.canProcess(recipe)) {
            ItemStack comb = h.getStackInSlot(HONEYCOMB_SLOT);
            List<Pair<ItemStack, Double>> outputs = recipe.outputs;

            ItemStack output1 = outputs.get(0).getLeft();
            ItemStack output2 = outputs.get(1).getLeft();
            ItemStack glass_bottle = h.getStackInSlot(BOTTLE_SLOT);

            ItemStack stackInOutput1 = h.getStackInSlot(OUTPUT1);
            ItemStack stackInOutput2 = h.getStackInSlot(OUTPUT2);

            ItemStack honey_bottle = h.getStackInSlot(HONEY_BOTTLE);
            if (outputs.get(0).getRight() >= world.rand.nextDouble()) {
                if (stackInOutput1.isEmpty()) {
                    this.h.setStackInSlot(OUTPUT1, output1.copy());
                } else if (stackInOutput1.getItem() == output1.getItem()) {
                    stackInOutput1.grow(output1.getCount());
                }
            }

            if (outputs.get(1).getRight() >= world.rand.nextDouble()) {
                if (stackInOutput2.isEmpty()) {
                    this.h.setStackInSlot(OUTPUT2, output2.copy());
                } else if (stackInOutput2.getItem() == output2.getItem()) {
                    stackInOutput2.grow(output2.getCount());
                }
            }

            if (.2 >= world.rand.nextDouble()) {
                if (honey_bottle.isEmpty()) {
                    this.h.setStackInSlot(HONEY_BOTTLE, new ItemStack(Items.HONEY_BOTTLE));
                } else {
                    honey_bottle.grow(1);
                }
                glass_bottle.shrink(1);
            }
            comb.shrink(1);
        }
        time = 0;
    }

    protected int getTime() {
        CentrifugeRecipe rec = getRecipe();
        if (rec == null) return 200;
        return rec.time;
    }

    protected CentrifugeRecipe getRecipe() {
        ItemStack input = h.getStackInSlot(HONEYCOMB_SLOT);
        if (input.isEmpty() || input == failedMatch) return null;
        if (recipe != null && recipe.matches(new RecipeWrapper(h), world)) return recipe;
        else {
            CentrifugeRecipe rec = world.getRecipeManager().
                    getRecipe(CentrifugeRecipe.CENTRIFUGE, new RecipeWrapper(h), this.world).orElse(null);
            if (rec == null) failedMatch = input;
            else failedMatch = ItemStack.EMPTY;
            return recipe = rec;
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        CompoundNBT inv = this.h.serializeNBT();
        tag.put("inv", inv);
        tag.putInt("time", time);
        tag.putInt("totalTime", totalTime);
        return super.write(tag);
    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        h.deserializeNBT(invTag);
        time = tag.getInt("time");
        totalTime = tag.getInt("totalTime");
        super.read(tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? lazyOptional.cast() :
                super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || slot == 0 || slot == 1;//&& StackUtil.isValid(FurnaceRecipes.instance().getSmeltingResult(stack));
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == 2 || slot == 3 || slot == 4;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("Centrifuge");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new CentrifugeContainer(i, world, pos, playerInventory);
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {

        protected TileStackHandler(int slots) {
            super(slots);
        }

        @Override
        public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
            return CentrifugeBlockEntity.this.getAcceptor();
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return CentrifugeBlockEntity.this.getRemover();
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    }

}

