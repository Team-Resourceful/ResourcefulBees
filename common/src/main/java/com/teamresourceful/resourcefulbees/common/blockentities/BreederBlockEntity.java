package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.BasicWorldlyContainer;
import com.teamresourceful.resourcefulbees.common.items.upgrade.BreederTimeUpgradeItem;
import com.teamresourceful.resourcefulbees.common.items.upgrade.Upgrade;
import com.teamresourceful.resourcefulbees.common.items.upgrade.UpgradeType;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.menus.BoundSafeContainerData;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.recipes.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefulbees.common.util.ContainerUtils;
import com.teamresourceful.resourcefulbees.common.util.MathUtils;
import com.teamresourceful.resourcefulbees.platform.common.menu.ContentMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BreederBlockEntity extends BlockEntity implements ContentMenuProvider<PositionContent>, BasicWorldlyContainer {

    private final NonNullList<ItemStack> items = NonNullList.withSize(29, ItemStack.EMPTY);

    private final BreederRecipe[] recipes = {null, null};

    protected final BoundSafeContainerData times = new BoundSafeContainerData(2, 0);
    protected final BoundSafeContainerData endTimes = new BoundSafeContainerData(2, 0);

    private int timeReduction = 0;

    private boolean firstLoad = true;

    public BreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BREEDER_BLOCK_ENTITY.get(), pos, state);
    }

    public static void serverTick(BreederBlockEntity entity) {
        if (entity.firstLoad) {
            for (int i = 0; i < BreederConstants.NUM_OF_BREEDERS; i++) {
                entity.checkAndCacheRecipe(i);
            }
            entity.firstLoad = false;
        }
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

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
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
            stack = ContainerUtils.insertItem(this, i, stack);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.NBT_INVENTORY, serializeContainer());
        tag.putInt(NBTConstants.Breeder.TIME_REDUCTION, timeReduction);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        deserializeContainer(tag.getCompound(NBTConstants.NBT_INVENTORY));
        timeReduction = tag.getInt(NBTConstants.Breeder.TIME_REDUCTION);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new BreederMenu(id, playerInventory, this, times, endTimes);
    }

    @Override
    public PositionContent createContent() {
        return new PositionContent(this.worldPosition);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TranslationConstants.Guis.APIARY_BREEDER;
    }

    @Override
    public void setSlotChanged(int slot, ItemStack stack) {
        if (slot == 0){
            if (stack.getItem() instanceof BreederTimeUpgradeItem upgrade) {
                int reduction = calculateReduction(upgrade, stack);
                if (reduction != timeReduction) {
                    timeReduction = reduction;
                    for (int i = 0; i < BreederConstants.NUM_OF_BREEDERS; i++) {
                        BreederRecipe recipe = recipes[i];
                        if (recipe != null) endTimes.set(i, recipe.time() - timeReduction);
                    }
                }
            } else {
                timeReduction = 0;
            }
        }
        if (MathUtils.inRangeInclusive(slot, 1, 10)) {
            for (int i = 0; i < BreederConstants.NUM_OF_BREEDERS; i++) { checkAndCacheRecipe(i); }
        }
    }

    private static int calculateReduction(BreederTimeUpgradeItem upgrade, ItemStack stack) {
        return Math.max(100, upgrade.getUpgradeTier(stack) * stack.getCount());
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        if (index >= 0 && index < 11) {
            return index != 0 || stack.getItem() instanceof Upgrade upgrade && upgrade.isType(UpgradeType.BREEDER);
        }
        return false;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return BreederConstants.SLOTS;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return index > 10 && index < 29;
    }
}
