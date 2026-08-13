package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.blockentities.base.ContentContainerBlock;
import com.teamresourceful.resourcefulbees.common.blocks.base.InstanceBlockEntityTicker;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.GuiTranslations;
import com.teamresourceful.resourcefulbees.common.menus.SolidificationChamberMenu;
import com.teamresourceful.resourcefulbees.common.menus.base.ResourcefulDataSlot;
import com.teamresourceful.resourcefulbees.common.menus.content.PositionContent;
import com.teamresourceful.resourcefulbees.common.recipes.SolidificationRecipe;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class SolidificationChamberBlockEntity extends BlockEntity implements InstanceBlockEntityTicker, ContentContainerBlock<PositionContent> {

    public static final int BLOCK_OUTPUT = 0;
    private static final int TANK_INPUT = 0;

    private final FluidHandler tank = new FluidHandler();
    private final ItemHandler container = new ItemHandler();

    private final ResourcefulDataSlot processTime = new ResourcefulDataSlot();
    private SolidificationRecipe cachedRecipe;

    private FluidResource lastFluid = FluidResource.EMPTY;
    public SolidificationChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY.get(), pos, state);
    }

    public ResourcefulDataSlot getProcessTime() {
        return processTime;
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return GuiTranslations.SOLIDIFICATION_CHAMBER;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        if (level == null) return null;
        return new SolidificationChamberMenu(id, playerInventory, this);
    }

    public boolean canProcessHoney() {
        if (level == null) {
            return false;
        }

        FluidResource fluid = fluidResource();

        if (fluid.isEmpty()) {
            clearRecipeCache();
            return false;
        }

        SolidificationRecipe recipe = getCachedRecipe(fluid);

        return recipe != null
                && tank.getAmountAsInt(TANK_INPUT) >= recipe.fluid().amount()
                && canAcceptOutput(recipe.stack().create());
    }

    private boolean canAcceptOutput(ItemStack result) {
        ItemResource resource = container.getResource(BLOCK_OUTPUT);

        if (resource.isEmpty()) {
            return result.getCount() <= result.getMaxStackSize();
        }

        ItemStack existing = resource.toStack(container.getAmountAsInt(BLOCK_OUTPUT));

        return ItemStack.isSameItemSameComponents(existing, result)
                && existing.getCount() + result.getCount() <= existing.getMaxStackSize();
    }

    private void clearRecipeCache() {
        cachedRecipe = null;
        lastFluid = FluidResource.EMPTY;
    }

    private SolidificationRecipe getCachedRecipe(FluidResource resource) {
        if (level == null) {
            return null;
        }

        if (resource.equals(lastFluid)) {
            return cachedRecipe;
        }

        lastFluid = resource;
        cachedRecipe = SolidificationRecipe.findRecipe(
                (RecipeManager) level.recipeAccess(),
                resource.toStack(tank.getAmountAsInt(TANK_INPUT)),
                level
        ).map(RecipeHolder::value).orElse(null);

        return cachedRecipe;
    }

    private boolean processResult() {
        if (cachedRecipe == null || lastFluid.isEmpty()) {
            return false;
        }

        SizedFluidIngredient fluidIngredient = cachedRecipe.fluid();
        ItemStack result = cachedRecipe.stack().create();

        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = tank.extract(
                    lastFluid,
                    fluidIngredient.amount(),
                    transaction
            );

            if (extracted != fluidIngredient.amount()) {
                return false;
            }

            int inserted = container.insertOutput(
                    ItemResource.of(result),
                    result.getCount(),
                    transaction
            );

            if (inserted != result.getCount()) {
                return false;
            }

            transaction.commit();
            return true;
        }
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!canProcessHoney()) {
            processTime.set(0);
            return;
        }

        if (processTime.increment() >= cachedRecipe.time() && processResult()) {
            processTime.set(0);
        }
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        input.readChild("tank", tank);
        input.readChild("inventory", container);
        clearRecipeCache();
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("tank", tank);
        output.putChild("inventory", container);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level instanceof ServerLevel serverLevel) {
            BlockState state = getBlockState();
            serverLevel.sendBlockUpdated(getBlockPos(), state, state, Block.UPDATE_CLIENTS);
        }
    }

    public FluidHandler fluidHandler() {
        return this.tank;
    }

    public FluidResource fluidResource() {
        return this.tank.getResource(TANK_INPUT);
    }

    public FluidStack fluidStack() {
        return fluidResource().toStack(tank.getAmountAsInt(TANK_INPUT));
    }

    public ItemHandler itemHandler() {
        return this.container;
    }

    @Override
    public PositionContent createContent(ServerPlayer player) {
        return new PositionContent(this.getBlockPos());
    }

    public class ItemHandler extends ItemStacksResourceHandler {

        public ItemHandler() {
            super(1);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
            SolidificationChamberBlockEntity.this.setChanged();
        }

        @Override
        public int insert(@NonNull ItemResource resource, int amount, @NonNull TransactionContext transaction) {
            return 0;
        }

        public int insertOutput(ItemResource resource, int amount, TransactionContext transaction) {
            return super.insert(resource, amount, transaction);
        }
    }

    public class FluidHandler extends FluidStacksResourceHandler {

        public FluidHandler() {
            super(1, 64000);
        }

        @Override
        protected void onContentsChanged(int index, @NonNull FluidStack previousContents) {
            SolidificationChamberBlockEntity.this.setChanged();
        }
    }
}
