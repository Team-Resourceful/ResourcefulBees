package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.ingredients.IAmountSensitive;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.BoundSafeContainerData;
import com.teamresourceful.resourcefulbees.common.inventory.menus.ApiaryBreederContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.recipe.BreederRecipe;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.MathUtils;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants.NBT_BREEDER_COUNT;

public class ApiaryBreederBlockEntity extends GUISyncedBlockEntity {

    public static final List<Integer> PARENT_1_SLOTS = List.of(1,6);
    public static final List<Integer> FEED_1_SLOTS = List.of(2,7);
    public static final List<Integer> PARENT_2_SLOTS = List.of(3,8);
    public static final List<Integer> FEED_2_SLOTS = List.of(4,9);
    public static final List<Integer> EMPTY_JAR_SLOTS = List.of(5,10);

    private final ApiaryBreederBlockEntity.TileStackHandler inventory = new ApiaryBreederBlockEntity.TileStackHandler(29);
    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(this::getInventory);

    private int numberOfBreeders = 1;

    private BreederRecipe[] recipes = {null, null};

    protected final BoundSafeContainerData times = new BoundSafeContainerData(2, 0);
    protected final BoundSafeContainerData endTimes = new BoundSafeContainerData(2, 0);

    public ApiaryBreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.APIARY_BREEDER_TILE_ENTITY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ApiaryBreederBlockEntity entity) {
        boolean dirty = false;
        for (int i = 0; i < entity.getNumberOfBreeders(); i++) {
            if (entity.recipes[i] != null) {
                entity.times.increment(i, 1);
                if (entity.times.get(i) >= entity.recipes[i].time()) {
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
        SimpleContainer container = new SimpleContainer(getItem(PARENT_1_SLOTS.get(i)), getItem(FEED_1_SLOTS.get(i)),
                getItem(PARENT_2_SLOTS.get(i)), getItem(FEED_2_SLOTS.get(i)),
                getItem(EMPTY_JAR_SLOTS.get(i))
        );
        recipes[i] = level.getRecipeManager().getRecipeFor(BreederRecipe.BREEDER_RECIPE_TYPE, container, level).orElse(null);
        if (recipes[i] != null) {
            endTimes.set(i, recipes[i].time());
        }
    }

    private ItemStack getItem(int i) {
        return inventory.getStackInSlot(i);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        for (int i = 0; i < getNumberOfBreeders(); i++) { checkAndCacheRecipe(i); }
    }

    private void processBreed(int slot) {
        BreederRecipe recipe = recipes[slot];
        if (recipe == null || level == null) return;

        BreederRecipe.BreederOutput output = recipe.outputs().next();

        boolean recipeSuccess = output.chance() >= level.random.nextFloat();

        if (recipeSuccess) {
            int amount = recipe.input().filter(IAmountSensitive.class::isInstance)
                    .map(IAmountSensitive.class::cast)
                    .map(IAmountSensitive::getAmount)
                    .orElse(1);
            recipe.input().ifPresent(input -> getItem(EMPTY_JAR_SLOTS.get(slot)).shrink(amount));
            ItemStack stack = output.output().copy();
            stack.setCount(amount);
            deliverItem(stack);

            int feed1Amount = recipe.parent1().feedItem() instanceof IAmountSensitive amountSensitive ? amountSensitive.getAmount() : 1;
            getItem(FEED_1_SLOTS.get(slot)).shrink(feed1Amount);
            recipe.parent1().returnItem().ifPresent(item -> {
                ItemStack return1 = item.copy();
                return1.setCount(feed1Amount);
                deliverItem(return1);
            });
            int feed2Amount = recipe.parent1().feedItem() instanceof IAmountSensitive amountSensitive ? amountSensitive.getAmount() : 1;
            getItem(FEED_2_SLOTS.get(slot)).shrink(feed2Amount);
            recipe.parent2().returnItem().ifPresent(item -> {
                ItemStack return2 = item.copy();
                return2.setCount(feed2Amount);
                deliverItem(return2);
            });
        }
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
        if (getNumberOfBreeders() != 1) {
            tag.putInt(NBT_BREEDER_COUNT, getNumberOfBreeders());
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBTWithoutCheckingSize(tag.getCompound(NBTConstants.NBT_INVENTORY));
        if (tag.contains(NBT_BREEDER_COUNT)) this.setNumberOfBreeders(tag.getInt(NBT_BREEDER_COUNT));
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        if (getNumberOfBreeders() != 1) {
            tag.putInt(NBT_BREEDER_COUNT, getNumberOfBreeders());
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag.contains(NBT_BREEDER_COUNT)) this.setNumberOfBreeders(tag.getInt(NBT_BREEDER_COUNT));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return inventoryOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        return new ApiaryBreederContainer(id, playerInventory, this, times, endTimes);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TranslationConstants.Guis.APIARY_BREEDER;
    }

    public int getNumberOfBreeders() {
        return numberOfBreeders;
    }

    public void setNumberOfBreeders(int numberOfBreeders) {
        this.numberOfBreeders = numberOfBreeders;
    }

    public @NotNull TileStackHandler getInventory() {
        return inventory;
    }

    @Override
    public CompoundTag getSyncData() {
        return null;
    }

    @Override
    public void readSyncData(@NotNull CompoundTag tag) {

    }

    public class TileStackHandler extends AutomationSensitiveItemStackHandler {

        private int maxSlots = 8;

        public void setMaxSlots(int maxSlots) {
            this.maxSlots = maxSlots;
        }

        protected TileStackHandler(int slots) {
            super(slots);
        }

        @Override
        public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
            return (slot, stack, automation) -> !automation || (slot > 0 && slot < 11);
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return (slot, automation) -> !automation || (slot > 10 && slot < 29);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();

            //TODO see away to streamline this.
            if (MathUtils.inRangeInclusive(slot, 0, 0) && false){
                inventory.updateNumberOfBreeders(this);
                inventory.rebuildOpenContainers();
                inventory.updateBreedTime(this);
            }
            if (MathUtils.inRangeInclusive(slot, 1, 10)) {
                for (int i = 0; i < getNumberOfBreeders(); i++) { checkAndCacheRecipe(i); }
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? 1 : 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            //TODO ADD CHECK FOR IF SLOT IS UPGRADE SLOT
            return true;
        }

        private boolean isSlotVisible(int slot) {
            return slot <= maxSlots;
        }

        private void updateNumberOfBreeders(TileStackHandler tileStackHandler) {

        }

        private void updateBreedTime(TileStackHandler tileStackHandler) {

        }

        private void rebuildOpenContainers() {
            if (ApiaryBreederBlockEntity.this.level != null) {
                float f = 5.0F;
                BlockPos pos = ApiaryBreederBlockEntity.this.worldPosition;

                ApiaryBreederBlockEntity.this.level.getEntitiesOfClass(Player.class, new AABB(pos.getX() - f, pos.getY() - f, pos.getZ() - f, (pos.getX() + 1) + f, (pos.getY() + 1) + f, (pos.getZ() + 1) + f))
                        .stream()
                        .filter(playerEntity -> playerEntity.containerMenu instanceof ApiaryBreederContainer)
                        .filter(this::openContainerMatches)
                        .forEach(playerEntity -> ((ApiaryBreederContainer) playerEntity.containerMenu).setupSlots(true));
            }
        }

        private boolean openContainerMatches(Player playerEntity) {
            ApiaryBreederContainer openContainer = (ApiaryBreederContainer) playerEntity.containerMenu;
            ApiaryBreederBlockEntity apiaryBreederBlockEntity = openContainer.getEntity();
            return  ApiaryBreederBlockEntity.this == apiaryBreederBlockEntity;
        }
    }
}
