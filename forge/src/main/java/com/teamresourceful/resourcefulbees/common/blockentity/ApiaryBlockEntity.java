package com.teamresourceful.resourcefulbees.common.blockentity;


import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.block.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.menus.ApiaryMenu;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.recipes.HiveRecipe;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler.ACCEPT_FALSE;
import static com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler.REMOVE_TRUE;

public class ApiaryBlockEntity extends BeeHolderBlockEntity {

    protected final ApiaryTier tier;

    private final AutomationSensitiveItemStackHandler inventory = new AutomationSensitiveItemStackHandler(27, ACCEPT_FALSE, REMOVE_TRUE);
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(() -> inventory);

    public ApiaryBlockEntity(ApiaryTier tier, BlockPos pos, BlockState state) {
        super(tier.getBlockEntityType(), pos, state);
        this.tier = tier;
    }

    public ApiaryTier getTier() {
        return tier;
    }

    //region BEE HANDLING
    protected void deliverNectar(CompoundTag nbt, Entity bee) {
        if (nbt.getBoolean("HasNectar")) {
            if (bee instanceof BeeCompat compat) compat.nectarDroppedOff();
            HiveRecipe.getApiaryOutput(tier, bee)
                .ifPresent(stack -> {
                    for (int i = 0; i < inventory.getSlots() && !stack.isEmpty(); i++) {
                        stack = ModUtils.insertItem(inventory, i, stack);
                    }
                });
        }
    }

    protected int getMaxTimeInHive(@NotNull BeeCompat bee) {
        return (int) (bee.getMaxTimeInHive() * tier.timeMod());
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
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBTWithoutCheckingSize(tag.getCompound(NBTConstants.NBT_INVENTORY));
        readSyncData(tag.getCompound(NBTConstants.SYNC_DATA));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.NBT_INVENTORY, inventory.serializeNBT());
        tag.put(NBTConstants.SYNC_DATA, getSyncData());
    }
    //endregion

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? inventoryOptional.cast() : super.getCapability(cap, side);
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

    public AutomationSensitiveItemStackHandler getInventory() {
        return inventory;
    }
}
