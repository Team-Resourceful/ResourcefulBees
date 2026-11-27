package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.IOResourceHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.lib.util.MathUtils;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import com.teamresourceful.resourcefulbees.common.menus.base.BoundSafeContainerData;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.recipes.breeder.ParentInput;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModRecipes;
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class BreederBlockEntity extends BlockEntity implements ContentMenuProvider<PositionContent> {

    private final ResourceHandler inventory = new ResourceHandler();
    private final BreederRecipe[] recipes = {null, null};
    private final BoundSafeContainerData times = new BoundSafeContainerData(2, 0);
    private final BoundSafeContainerData endTimes = new BoundSafeContainerData(2, 0);
    private boolean firstLoad = true;

    public BreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BREEDER_BLOCK_ENTITY.get(), pos, state);
    }

    public ResourceHandler getInventory() {
        return inventory;
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
        if (level == null || level.isClientSide()) {
            recipes[i] = null;
            return;
        }

        var recipeHolder = ((ServerLevel) level).recipeAccess().getRecipeFor(ModRecipes.BREEDER_RECIPE_TYPE.get(), makeRecipeInput(i), level).orElse(null);
        recipes[i] = recipeHolder == null ? null : recipeHolder.value();
        if (recipes[i] != null) {
            recalculateRecipeEndtimes(inventory.getAmountAsInt(BreederConstants.UPGRADE_SLOT));
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
        if (output.chance() < level.getRandom().nextFloat()) return;

        if (!recipe.matches(makeRecipeInput(breeder), level)) {
            checkAndCacheRecipe(breeder);
            return;
        }

        ItemStack result = output.child().create();
        result.setCount(1);

        if (tryProcessBreed(breeder, recipe, result)) {
            checkAndCacheRecipe(breeder);
        }
    }

    private boolean tryProcessBreed(int breeder, BreederRecipe recipe, ItemStack result) {
        int feed1Slot = BreederConstants.FEED_1_SLOTS.get(breeder);
        int feed2Slot = BreederConstants.FEED_2_SLOTS.get(breeder);
        int optionalSlot = BreederConstants.EMPTY_JAR_SLOTS.get(breeder);

        boolean normal = recipe.parent1().matches(
                getItem(BreederConstants.PARENT_1_SLOTS.get(breeder)),
                getItem(feed1Slot)
        ) && recipe.parent2().matches(
                getItem(BreederConstants.PARENT_2_SLOTS.get(breeder)),
                getItem(feed2Slot)
        );

        ParentInput first = normal ? recipe.parent1() : recipe.parent2();
        ParentInput second = normal ? recipe.parent2() : recipe.parent1();

        try (Transaction transaction = Transaction.openRoot()) {
            if (!deliverItem(result, transaction)) return false;

            if (!extractAmount(optionalSlot, 1, transaction)) return false;
            if (!extractAmount(feed1Slot, first.feedAmount(), transaction)) return false;
            if (!extractAmount(feed2Slot, second.feedAmount(), transaction)) return false;

            if (!deliverReturnItem(first, transaction)) return false;
            if (!deliverReturnItem(second, transaction)) return false;

            transaction.commit();
            return true;
        }
    }

    private boolean extractAmount(int slot, int amount, TransactionContext context) {
        return inventory.extract(slot, inventory.getResource(slot), amount, context) == amount;
    }

    private boolean deliverReturnItem(ParentInput parent, TransactionContext context) {
        if (parent.returnItem().isEmpty()) return true;

        ItemStack stack = parent.returnItem().get().create();
        stack.setCount(parent.feedAmount());

        return deliverItem(stack, context);
    }


    private boolean deliverItem(ItemStack stack, TransactionContext context) {
        return inventory.insertOutput(ItemResource.of(stack), stack.getCount(), context) == stack.getCount();
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
        inventory.deserialize(input);
        input.readChild("end_times", endTimes);
        input.readChild("times", times);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output);
        output.putChild("end_times", endTimes);
        output.putChild("times", times);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NonNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, inventory.toContainerContents());
    }

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentGetter components) {
        super.applyImplicitComponents(components);
        inventory.fromContainerContents(components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    public PositionContent createContent(ServerPlayer player) {
        return new PositionContent(this.worldPosition);
    }

    public ItemStack getItem(int index) {
        ItemStack stack = inventory.getResource(index).toStack();
        stack.setCount(inventory.getAmountAsInt(index));
        return stack;
    }

    public class ResourceHandler extends IOResourceHandler {
        public ResourceHandler() {
            super(29, 11);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
            if (index == BreederConstants.UPGRADE_SLOT) {
                recalculateRecipeEndtimes(getAmountAsInt(index));
            }
            if (MathUtils.inRangeInclusive(index, 1, 10)) {
                for (int i = 0; i < BreederConstants.BREEDERS; i++) {
                    checkAndCacheRecipe(i);
                }
            }
            BreederBlockEntity.this.setChanged();
        }
    }
}
