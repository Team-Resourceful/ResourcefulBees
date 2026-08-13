package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.BoundSafeContainerData;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.ParentInput;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefulbees.common.lib.util.MathUtils;
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class BreederBlockEntity extends BlockEntity implements ContentMenuProvider<PositionContent> {

    private final ResourceHandler resourceHandler = new ResourceHandler();
    private final BreederRecipe[] recipes = {null, null};
    private final BoundSafeContainerData times = new BoundSafeContainerData(2, 0);
    private final BoundSafeContainerData endTimes = new BoundSafeContainerData(2, 0);
    private boolean firstLoad = true;

    public BreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BREEDER_BLOCK_ENTITY.get(), pos, state);
    }

    public ResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    public static void serverTick(BreederBlockEntity entity) {
        if (entity.firstLoad) {
            for (int i = 0; i < BreederConstants.BREEDERS; i++) {
                entity.checkAndCacheRecipe(i);
            }
            entity.firstLoad = false;
        }
        boolean dirty = false;
        for (int i = 0; i < BreederConstants.BREEDERS; i++) {
            if (entity.recipes[i] != null) {
                entity.times.increment(i);
                if (entity.times.get(i) >= entity.endTimes.get(i)) {
                    entity.times.set(i, 0);
                    entity.processBreed(i);
                }
                dirty = true;
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
        if (level == null || level.getServer() == null) {
            recipes[i] = null;
            return;
        }

        var recipeHolder = level.getServer().getRecipeManager().getRecipeFor(ModRecipes.BREEDER_RECIPE_TYPE.get(), makeRecipeInput(i), level).orElse(null);
        recipes[i] = recipeHolder == null ? null : recipeHolder.value();
        if (recipes[i] != null) {
            recalculateRecipeEndtimes(resourceHandler.getAmountAsInt(BreederConstants.UPGRADE_SLOT));
        }
    }

    private BreederRecipe.Input makeRecipeInput(int i) {
        return new BreederRecipe.Input(
                getItem(BreederConstants.PARENT_1_SLOTS.get(i)),
                getItem(BreederConstants.FEED_1_SLOTS.get(i)),
                getItem(BreederConstants.PARENT_2_SLOTS.get(i)),
                getItem(BreederConstants.FEED_2_SLOTS.get(i)),
                getItem(BreederConstants.EMPTY_JAR_SLOTS.get(i))
        );
    }

    private void processBreed(int breeder) {
        BreederRecipe recipe = recipes[breeder];
        if (recipe == null || level == null) return;

        var output = recipe.outputs().next();
        var recipeSuccess = output.chance() >= level.getRandom().nextFloat();

        if (recipeSuccess) {

            try (Transaction transaction = Transaction.openRoot()) {
                recipe.optionalIngredient().ifPresent(_ ->
                        resourceHandler.extract(
                                BreederConstants.EMPTY_JAR_SLOTS.get(breeder),
                                resourceHandler.getResource(BreederConstants.EMPTY_JAR_SLOTS.get(breeder)),
                                1,
                                transaction)
                );
                ItemStack stack = output.child().create();
                stack.setCount(1);
                deliverItem(stack, transaction);

                extractFeedItems(BreederConstants.FEED_1_SLOTS, breeder, recipe.parent1(), transaction);
                extractFeedItems(BreederConstants.FEED_2_SLOTS, breeder, recipe.parent2(), transaction);
                transaction.commit();
            }
            checkAndCacheRecipe(breeder);
        }
    }

    private void extractFeedItems(List<Integer> feedSlots, int breeder, ParentInput parent, TransactionContext context) {
        getItem(feedSlots.get(breeder)).shrink(parent.feedAmount());
        resourceHandler.extract(
                feedSlots.get(breeder),
                resourceHandler.getResource(feedSlots.get(breeder)),
                parent.feedAmount(),
                context
        );
        parent.returnItem().ifPresent(item -> {
            ItemStack returnItem = item.create();
            returnItem.setCount(parent.feedAmount());
            deliverItem(returnItem, context);
        });
    }

    private void deliverItem(ItemStack stack, TransactionContext context) {
        resourceHandler.insertOutput(ItemResource.of(stack), stack.count(), context);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new BreederMenu(id, playerInventory, this, times, endTimes);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return GuiTranslations.APIARY_BREEDER;
    }

    private void recalculateRecipeEndtimes(int numUpgrades) {
        var modifier = 1 - numUpgrades * 0.10;
        for (int i = 0; i < BreederConstants.BREEDERS; i++) {
            BreederRecipe recipe = recipes[i];
            if (recipe != null) endTimes.set(i, (int) Math.round(recipe.time() * modifier));
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        resourceHandler.deserialize(input);
        input.readChild("end_times", endTimes);
        input.readChild("times", times);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        resourceHandler.serialize(output);
        output.putChild("end_times", endTimes);
        output.putChild("times", times);
    }

    @Override
    public PositionContent createContent(ServerPlayer player) {
        return new PositionContent(this.worldPosition);
    }

    public ItemStack getItem(int index) {
        return resourceHandler.getResource(index).toStack();
    }

    public class ResourceHandler extends ItemStacksResourceHandler {
        public ResourceHandler() {
            super(29);
        }

        @Override
        public int insert(int index, @NonNull ItemResource resource, int amount, @NonNull TransactionContext transaction) {
            return index <= 11 ? super.insert(index, resource, amount, transaction) : 0;
        }

        public void insertOutput(ItemResource resource, int amount, TransactionContext context) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);

            int inserted = 0;
            int size = size();
            for (int index = 11; index < size; index++) {
                inserted += super.insert(index, resource, amount - inserted, context);
                if (inserted == amount) break;
            }
        }

        @Override
        public int extract(int index, @NonNull ItemResource resource, int amount, @NonNull TransactionContext transaction) {
            return super.extract(index, resource, amount, transaction);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
            if (index == BreederConstants.UPGRADE_SLOT) {
                recalculateRecipeEndtimes(getAmountAsInt(index));
            }
            if (MathUtils.inRangeInclusive(index, 1, 10)) {
                for (int i = 0; i < BreederConstants.BREEDERS; i++) { checkAndCacheRecipe(i); }
            }
            BreederBlockEntity.this.setChanged();
        }
    }
}
