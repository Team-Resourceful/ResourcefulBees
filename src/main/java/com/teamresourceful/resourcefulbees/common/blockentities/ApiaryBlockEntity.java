package com.teamresourceful.resourcefulbees.common.blockentities;


import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.IOResourceHandler;
import com.teamresourceful.resourcefulbees.common.blocks.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.components.Upgrade;
import com.teamresourceful.resourcefulbees.common.items.PollenItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.ApiaryMenu;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ApiaryBlockEntity extends BeeHolderBlockEntity {

    protected final ApiaryTier tier;
    private final ApiaryResourceHandler resourceHandler = new ApiaryResourceHandler();
    private boolean producesPollen;

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
            HiveRecipe.getApiaryOutput(tier, bee).ifPresent(stack -> {
                try(Transaction transaction = Transaction.openRoot()) {
                    this.resourceHandler.insertOutput(ItemResource.of(stack), stack.count(), transaction);

                    if (producesPollen) {
                        for (int i = 0; i < tier.pollenRolls(); i++) {
                            if (level instanceof ServerLevel serverLevel
                                && serverLevel.getRandom().nextDouble() <= tier.pollenChance()) {
                                    this.resourceHandler.insertOutput(ItemResource.of(PollenItem.createRandom(serverLevel)), 1, transaction);
                                }
                            }
                        }
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

    public class ApiaryResourceHandler extends IOResourceHandler {


        public ApiaryResourceHandler() {
            super(28, 1);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
            if (index == BreederConstants.UPGRADE_SLOT) {
                var resource = getResource(index);
                if (resource.isEmpty()) {
                    clearUpgrade();
                } else {
                    handleUpgrade(resource.get(ModDataComponents.UPGRADE));
                }
            }

            ApiaryBlockEntity.this.setChanged();
        }

        private void clearUpgrade() {
            ApiaryBlockEntity.this.producesPollen = false;
        }

        private void handleUpgrade(Upgrade upgrade) {
            if (upgrade.isType(Upgrade.Type.APIARY_POLLEN)) {
                ApiaryBlockEntity.this.producesPollen = true;
            }
        }
    }
}
