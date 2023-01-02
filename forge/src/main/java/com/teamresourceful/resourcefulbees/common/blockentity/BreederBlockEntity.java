package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.BoundSafeContainerData;
import com.teamresourceful.resourcefulbees.common.inventory.menus.BreederMenu;
import com.teamresourceful.resourcefulbees.common.item.upgrade.BreederTimeUpgradeItem;
import com.teamresourceful.resourcefulbees.common.item.upgrade.Upgrade;
import com.teamresourceful.resourcefulbees.common.item.upgrade.UpgradeType;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BreederBlockEntity extends BlockEntity implements MenuProvider {

    private final TileStackHandler inventory = new TileStackHandler();
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(this::getInventory);

    private final BreederRecipe[] recipes = {null, null};

    protected final BoundSafeContainerData times = new BoundSafeContainerData(2, 0);
    protected final BoundSafeContainerData endTimes = new BoundSafeContainerData(2, 0);

    private int timeReduction = 0;

    public BreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BREEDER_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(BreederBlockEntity entity) {
        boolean dirty = false;
        for (int i = 0; i < BreederConstants.NUM_OF_BREEDERS; i++) {
            if (entity.recipes[i] != null) {
                entity.times.increment(i);
                if (entity.times.get(i) >= entity.endTimes.get(i)) {
                    entity.times.set(i, 0);
                    entity.processBreed(i);
                    dirty = true;
                }
            } else {
                entity.times.set(i, 0);
                entity.endTimes.set(i, 0);
            }
            if (dirty) {
                entity.setChanged();
            }
        }
    }

    private void checkAndCacheRecipe(int i) {
        if (level == null) {
            recipes[i] = null;
            return;
        }
        SimpleContainer container = new SimpleContainer(getItem(BreederConstants.PARENT_1_SLOTS.get(i)), getItem(BreederConstants.FEED_1_SLOTS.get(i)),
                getItem(BreederConstants.PARENT_2_SLOTS.get(i)), getItem(BreederConstants.FEED_2_SLOTS.get(i)),
                getItem(BreederConstants.EMPTY_JAR_SLOTS.get(i))
        );
        recipes[i] = level.getRecipeManager().getRecipeFor(ModRecipes.BREEDER_RECIPE_TYPE.get(), container, level).orElse(null);
        if (recipes[i] != null) {
            endTimes.set(i, recipes[i].time()-timeReduction);
        }
    }

    private ItemStack getItem(int i) {
        return inventory.getStackInSlot(i);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        for (int i = 0; i < BreederConstants.NUM_OF_BREEDERS; i++) { checkAndCacheRecipe(i); }
    }

    private void processBreed(int slot) {
        BreederRecipe recipe = recipes[slot];
        if (recipe == null || level == null) return;

        BreederRecipe.BreederOutput output = recipe.outputs().next();

        boolean recipeSuccess = output.chance() >= level.random.nextFloat();

        if (recipeSuccess) {
            recipe.input().ifPresent(input -> getItem(BreederConstants.EMPTY_JAR_SLOTS.get(slot)).shrink(1));
            ItemStack stack = output.output().copy();
            stack.setCount(1);
            deliverItem(stack);

            completeBreed(BreederConstants.FEED_1_SLOTS, slot, recipe.parent1());
            completeBreed(BreederConstants.FEED_2_SLOTS, slot, recipe.parent2());
            checkAndCacheRecipe(slot);
        }
    }

    private void completeBreed(List<Integer> feedSlots, int slot, BreederRecipe.BreederPair parent) {
        getItem(feedSlots.get(slot)).shrink(parent.feedAmount());
        parent.returnItem().ifPresent(item -> {
            ItemStack returnItem = item.copy();
            returnItem.setCount(parent.feedAmount());
            deliverItem(returnItem);
        });
    }

    private void deliverItem(ItemStack stack) {
        for (int i = 11; i < 29 && !stack.isEmpty(); i++) {
            stack = ModUtils.insertItem(inventory, i, stack);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.NBT_INVENTORY, getInventory().serializeNBT());
        tag.putInt(NBTConstants.Breeder.TIME_REDUCTION, timeReduction);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBTWithoutCheckingSize(tag.getCompound(NBTConstants.NBT_INVENTORY));
        timeReduction = tag.getInt(NBTConstants.Breeder.TIME_REDUCTION);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) return inventoryOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new BreederMenu(id, playerInventory, this, times, endTimes);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TranslationConstants.Guis.APIARY_BREEDER;
    }

    public @NotNull TileStackHandler getInventory() {
        return inventory;
    }

    public class TileStackHandler extends AutomationSensitiveItemStackHandler {

        protected TileStackHandler() {
            super(29);
        }

        @Override
        public IAcceptor getAcceptor() {
            return (slot, stack, automation) -> {
                if (slot >= 0 && slot < 11) {
                    return slot != 0 || stack.getItem() instanceof Upgrade upgrade && upgrade.getUpgradeType().equals(UpgradeType.BREEDER);
                }
                return false;
            };
        }

        @Override
        public IRemover getRemover() {
            return (slot, automation) -> !automation || (slot > 10 && slot < 29);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();

            if (slot == 0){
                inventory.updateBreedTime(this);
            }
            if (MathUtils.inRangeInclusive(slot, 1, 10)) {
                for (int i = 0; i < BreederConstants.NUM_OF_BREEDERS; i++) { checkAndCacheRecipe(i); }
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? 4 : 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != 0 || stack.getItem() instanceof Upgrade upgrade && upgrade.getUpgradeType().equals(UpgradeType.BREEDER);
        }

        private void updateBreedTime(TileStackHandler stackHandler) {
            var stackInSlot = stackHandler.getStackInSlot(0);
            if (stackInSlot.getItem() instanceof BreederTimeUpgradeItem upgrade) {
                int reduction = calculateReduction(upgrade, stackInSlot);
                if (reduction != timeReduction) {
                    timeReduction = reduction;
                    for (int i = 0; i < BreederConstants.NUM_OF_BREEDERS; i++) {
                        BreederRecipe recipe = recipes[i];
                        if (recipe != null) endTimes.set(i, recipe.time() - timeReduction);
                    }
                }
            }
        }

        private static int calculateReduction(BreederTimeUpgradeItem upgrade, ItemStack stack) {
            return Math.max(100, upgrade.getUpgradeTier(stack) * stack.getCount());
        }
    }
}
