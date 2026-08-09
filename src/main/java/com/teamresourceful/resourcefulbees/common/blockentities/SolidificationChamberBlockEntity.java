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

    private boolean dirty;
    private SolidificationRecipe cachedRecipe;

    private FluidResource lastFluid;
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
        if (level == null) return false;
        FluidResource fluid = getFluid();
        if (fluid.isEmpty()) {
            cachedRecipe = null;
            lastFluid = null;
            return false;
        }
        final SolidificationRecipe recipe = fluid.equals(lastFluid) && cachedRecipe != null
                ? cachedRecipe
                : getRecipe(fluid);
        if (recipe == null) return false;

        boolean isTankReady = !fluid.isEmpty() && tank.getAmountAsInt(TANK_INPUT) >= recipe.fluid().amount();

        ItemResource output = container.getResource(BLOCK_OUTPUT);
        ItemStack stack = output.toStack(container.getAmountAsInt(BLOCK_OUTPUT));
        boolean canOutput = output.isEmpty() || ItemStack.isSameItemSameComponents(stack, recipe.stack()) && stack.getCount() < output.getMaxStackSize();

        cachedRecipe = recipe;
        lastFluid = fluid;
        return isTankReady && canOutput;
    }

    private SolidificationRecipe getRecipe(FluidResource resource) {
        if (level == null) return null;
        var recipe = SolidificationRecipe.findRecipe((RecipeManager) level.recipeAccess(), resource.toStack(tank.getAmountAsInt(TANK_INPUT)), level);
        return recipe.map(RecipeHolder::value).orElse(null);
    }

    public void processResult() {
        SizedFluidIngredient fluidIngredient = cachedRecipe.fluid();
        try (Transaction transaction = Transaction.openRoot()){
            tank.extract(lastFluid, fluidIngredient.amount(), transaction);
            container.insertOutput(ItemResource.of(cachedRecipe.stack()), cachedRecipe.stack().count(), transaction);
            transaction.commit();
        }
    }

    @Override
    public Side getSide() {
        return Side.SERVER;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.canProcessHoney() && processTime.increment() >= cachedRecipe.time()) {
            this.processResult();
            this.processTime.set(0);
        }


        if (this.dirty) {
            this.dirty = false;
            this.setChanged();
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

    public FluidResource getFluid() {
        return this.tank.getResource(TANK_INPUT);
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

        public void insertOutput(ItemResource resource, int amount, TransactionContext transaction) {
            super.insert(resource, amount, transaction);
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
