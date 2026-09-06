package com.teamresourceful.resourcefulbees.common.blockentities;


import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.ApiaryMenu;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ApiaryBlockEntity extends BeeHolderBlockEntity {

    protected final ApiaryTier tier;
    private final ApiaryResourceHandler resourceHandler = new ApiaryResourceHandler();

    public ApiaryBlockEntity(ApiaryTier tier, BlockPos pos, BlockState state) {
        super(tier.getBlockEntityType(), pos, state);
        this.tier = tier;
    }

    public ApiaryTier getTier() {
        return tier;
    }

    public ApiaryResourceHandler resourceHandler() {
        return this.resourceHandler;
    }

    //region BEE HANDLING
    protected void deliverNectar(boolean hasNectar, Entity bee) {
        if (hasNectar) {
            if (bee instanceof BeeCompat compat) compat.resourcefulBees$nectarDroppedOff();
            HiveRecipe.getApiaryOutput(tier, bee)
                .ifPresent(stack -> {
                    try(Transaction transaction = Transaction.openRoot()) {
                        this.resourceHandler.internalInsert(ItemResource.of(stack), stack.count(), transaction);
                        transaction.commit();
                    }
                });
        }
    }

    public int getMaxTimeInHive(@NotNull BeeCompat bee) {
        return (int) (bee.resourcefulBees$getMaxTimeInHive() * tier.timeMod());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ApiaryBlockEntity apiaryTile) {
        BeeHolderBlockEntity.serverTick(level, pos, state, apiaryTile);
    }

    public boolean hasSpace() {
        return this.bees.size() < tier.maxBees();
    }

    public boolean isAllowedBee() {
        return getBlockState().getBlock() instanceof ApiaryBlock;
    }
    //endregion

    //region NBT HANDLING

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        this.resourceHandler.serialize(output);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        this.resourceHandler.deserialize(input);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NonNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, this.resourceHandler.toContainerContents());
    }

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.resourceHandler.fromContainerContents(components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    public @NotNull Component getDisplayName() {
        return GuiTranslations.APIARY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ApiaryMenu(id, inventory, this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sendToPlayersTrackingChunk();
    }

    public class ApiaryResourceHandler extends ItemStacksResourceHandler {

        public ApiaryResourceHandler() {
            super(27);
        }

        public ItemContainerContents toContainerContents() {
            return ItemContainerContents.fromItems(this.copyToList());
        }

        public void fromContainerContents(ItemContainerContents contents) {
            NonNullList<ItemStack> items = NonNullList.withSize(this.size(), ItemStack.EMPTY);
            contents.copyInto(items);

            for (int slot = 0; slot < this.size(); slot++) {
                ItemStack stack = items.get(slot);

                if (stack.isEmpty()) {
                    this.set(slot, ItemResource.EMPTY, 0);
                } else {
                    this.set(slot, ItemResource.of(stack), stack.getCount());
                }
            }
        }

        @Override
        public int insert(@NonNull ItemResource resource, int amount, @NonNull TransactionContext transaction) {
            return 0;
        }

        public void internalInsert(ItemResource resource, int amount, TransactionContext transactionContext) {
            super.insert(resource, amount, transactionContext);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
            ApiaryBlockEntity.this.setChanged();
        }
    }
}
