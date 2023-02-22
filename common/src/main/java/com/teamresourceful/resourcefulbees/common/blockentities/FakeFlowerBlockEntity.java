package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.api.compat.CustomBee;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BasicContainer;
import com.teamresourceful.resourcefulbees.common.blockentities.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.items.MutatedPollenItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.menus.FakeFlowerMenu;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.util.ContainerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FakeFlowerBlockEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker, BasicContainer, WorldlyContainer {

    private static final int[] SLOTS = new int[]{0, 1, 2, 3, 4};
    private final NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

    public FakeFlowerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FAKE_FLOWER_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
        tag.put(NBTConstants.NBT_INVENTORY, serializeContainer());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        deserializeContainer(tag.getCompound(NBTConstants.NBT_INVENTORY));
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    public void createPollen(CustomBee bee) {
        ResourceLocation id = bee.getMutationData().id();
        if (level == null || id == null) return;
        ItemStack pollen = MutatedPollenItem.getPollen(id, level);
        // generate random amount
        int count = bee.getMutationData().count();
        int quart = count / 4;
        int total = Math.max(1, this.level.getRandom().nextInt(count - quart) + quart);
        pollen.setCount(total);
        deliverItem(pollen);
    }

    private void deliverItem(ItemStack stack) {
        for (int i = 0; i < 5 && !stack.isEmpty(); i++) {
            stack = ContainerUtils.insertItem(this, i, stack);
            if (stack.isEmpty()) return;
        }
    }

    @Override
    public CompoundTag getSyncData() {
        return new CompoundTag();
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {
        //nothing yet
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TranslationConstants.FakeFLower.TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        if (level == null) return null;
        return new FakeFlowerMenu(i, inventory, this);
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return direction == Direction.DOWN && stack.getItem() == ModItems.MUTATED_POLLEN.get();
    }
}
