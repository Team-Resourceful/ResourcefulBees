package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.common.block.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.blockentity.base.GUISyncedBlockEntity;
import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.menus.FakeFlowerMenu;
import com.teamresourceful.resourcefulbees.common.item.MutatedPollenItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.mixin.invokers.RecipeManagerInvoker;
import com.teamresourceful.resourcefulbees.common.recipe.recipes.MutationRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModRecipeTypes;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes.FAKE_FLOWER_ENTITY;

public class FakeFlowerEntity extends GUISyncedBlockEntity implements InstanceBlockEntityTicker {

    public static final int BLOCK_OUTPUT = 0;
    private final AutomationSensitiveItemStackHandler inventory = new TileStackHandler();
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(this::getInventory);

    public FakeFlowerEntity(BlockPos pos, BlockState state) {
        super(FAKE_FLOWER_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
        tag.put(NBTConstants.NBT_INVENTORY, inventory.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound(NBTConstants.NBT_INVENTORY));
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    public @NotNull AutomationSensitiveItemStackHandler getInventory() {
        return inventory;
    }

    public void createPollen(ResourcefulBee bee) {
        ItemStack pollen = new ItemStack(ModItems.MUTATED_POLLEN.get());
        ResourceLocation id = bee.getMutationData().id();
        if (level == null || id == null) return;
        MutationRecipe recipe = MutationRecipe.getRecipe(level, id);
        if (recipe == null) return;
        int count = bee.getMutationData().count();
        // add data
        CompoundTag tag = new CompoundTag();
        tag.putString(NBTConstants.POLLEN_ID, recipe.id().toString());
        tag.putInt(NBTConstants.POLLEN_BASE_COLOR, recipe.getPollenBaseColor().getValue());
        tag.putInt(NBTConstants.POLLEN_TOP_COLOR, recipe.getPollenTopColor().getValue());
        pollen.setTag(tag);
        // generate random amount
        int quart = count / 4;
        int total = Math.max(1, bee.getRandom().nextInt(count - quart) + quart);
        pollen.setCount(total);
        deliverItem(pollen);
    }

    private void deliverItem(ItemStack stack) {
        for (int i = 0; i < 5 && !stack.isEmpty(); i++) {
            stack = ModUtils.insertItem(inventory, i, stack);
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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (side == Direction.DOWN && cap.equals(ForgeCapabilities.ITEM_HANDLER)) return inventoryOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return TranslationConstants.FakeFLower.TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory arg, Player arg2) {
        if (level == null) return null;
        return new FakeFlowerMenu(i, arg, this);
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler() {
            super(5, (slot, stack, automation) -> false, (slot, automation) -> true);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getItem() == ModItems.MUTATED_POLLEN.get();
        }
    }
}
