package com.teamresourceful.resourcefulbees.common.blockentities;


import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.ContentContainerBlock;
import com.teamresourceful.resourcefulbees.common.blocks.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.ApiaryMenu;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
//import com.teamresourceful.resourcefulbees.common.util.containers.AutomationSensitiveContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApiaryBlockEntity extends BeeHolderBlockEntity implements ContentContainerBlock<PositionContent> {

    protected final ApiaryTier tier;

    private static final int[] SLOTS = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    private final NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);

    //private AutomationSensitiveContainer container;

    public ApiaryBlockEntity(ApiaryTier tier, BlockPos pos, BlockState state) {
        super(tier.getBlockEntityType(), pos, state);
        this.tier = tier;
    }

    public ApiaryTier getTier() {
        return tier;
    }

    //region BEE HANDLING
    protected void deliverNectar(CompoundTag nbt, Entity bee) {
        if (nbt.getBooleanOr("HasNectar", false)) {
            if (bee instanceof BeeCompat compat) compat.resourcefulBees$nectarDroppedOff();
            /*HiveRecipe.getApiaryOutput(tier, bee)
                .ifPresent(stack -> {
                    for (int i = 0; i < getContainer().getContainerSize() && !stack.isEmpty(); i++) {
                        stack = ContainerUtils.internalInsertItem(getContainer(), i, stack);
                        if (stack.isEmpty()) break;
                    }
                });*/
        }
    }

    protected int getMaxTimeInHive(@NotNull BeeCompat bee) {
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
/*    @Override
    public void load(@NotNull CompoundTag tag) {
        //todo super.load(tag);
        container.deserialize();
        //getContainer().deserialize(tag);
        //deserializeContainer(tag.getCompound(NBTConstants.NBT_INVENTORY));
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA).orElse(new CompoundTag()));
    }*/

/*    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        super.readSyncData(tag);
        getContainer().deserialize(tag);
    }
    //todo figure out why I have to override these methods while centrifuge and honeygen don't for the item container.
    @Override
    public @NotNull CompoundTag getSyncData() {
        return getContainer().serialize(super.getSyncData());
    }*/

/*    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        //todo super.saveAdditional(tag);
        //tag.put(NBTConstants.NBT_INVENTORY, serializeContainer());
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
    }*/
    //endregion

    @Override
    public @NotNull Component getDisplayName() {
        return GuiTranslations.APIARY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ApiaryMenu(id, inventory, this);
    }

//    @Override
//    public PositionContent createContent() {
//        return new PositionContent(this.worldPosition);
//    }

/*    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return SLOTS;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return false;
    }*/

/*    @Override
    public AutomationSensitiveContainer getContainer() {
        if (container == null) {
            container = new Container(this);
        }
        return container;
    }*/

    @Override
    public PositionContent createContent(ServerPlayer player) {
        return null;
    }

/*    private static class Container extends AutomationSensitiveContainer {

        public Container(BlockEntity entity) {
            super(entity, 27, player -> true);
        }

        @Override
        public boolean canAccept(int slot, ItemStack stack, boolean automation) {
            return false;
        }

        @Override
        public boolean canRemove(int slot, boolean automation) {
            return true;
        }
    }*/
}
