package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary;

import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.inventory.containers.UnvalidatedApiaryContainer;
import com.teamresourceful.resourcefulbees.common.inventory.containers.ValidatedApiaryContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTab;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BlockAccessor;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.MultiBlockHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ApiaryController extends BlockEntity implements MenuProvider, IApiaryMultiblock {

    private final List<BlockPos> structureBlocks = new ArrayList<>();
    protected boolean isValidApiary;
    private boolean previewed;
    private int horizontalOffset = 0;
    private int verticalOffset = 0;
    private int width = 7;
    private int height = 6;
    private int depth = 7;
    private List<ContainerListener> listeners = new ArrayList<>();
    private int ticksSinceValidation;
    private BlockPos storagePos;
    private ApiaryStorageTileEntity apiaryStorage;


    public ApiaryController(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
    }

    //region PLAYER SYNCING
    public void sendGUINetworkPacket(ContainerListener player) {
        if (player instanceof ServerPlayer serverPlayer && !(player instanceof FakePlayer)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            buffer.writeNbt(saveToNBT(new CompoundTag()));
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), serverPlayer);
        }
    }

    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {
        CompoundTag nbt = buffer.readNbt();
        if (nbt != null) loadFromNBT(nbt);
    }

    public void syncApiaryToPlayersUsing() {
        this.listeners.forEach(this::sendGUINetworkPacket);
    }

    public void setListeners(List<ContainerListener> listeners) {
        this.listeners = listeners;
    }
    //endregion

    private static boolean isValidApiaryBlock(BlockAccessor block) {
        return block.getHasCollision();
    }

    private static boolean isStructurePosition(BlockPos blockPos, BoundingBox box) {
        return blockPos.getX() == box.minX() ||
                blockPos.getX() == box.maxX() ||
                blockPos.getY() == box.maxY() ||
                blockPos.getZ() == box.minZ() ||
                blockPos.getZ() == box.maxZ();
    }

    public boolean isValidApiary(boolean runValidation) {
        if (runValidation) {
            runStructureValidation(null);
        }
        return isValidApiary;
    }

    public ApiaryStorageTileEntity getApiaryStorage() {
        if (level != null && getStoragePos() != null) {
            if (level.getBlockEntity(getStoragePos()) instanceof ApiaryStorageTileEntity storage) {
                return storage;
            }
        }
        setStoragePos(null);
        return null;
    }

    public void runStructureValidation(@Nullable ServerPlayer validatingPlayer) {
        if (this.level != null && !this.level.isClientSide()) {
            if (!this.isValidApiary || structureBlocks.isEmpty()) {
                buildStructureBlockList();
            }
            this.isValidApiary = validateStructure(this.level, validatingPlayer);
            this.level.setBlockAndUpdate(this.getBlockPos(), getBlockState().setValue(ApiaryBlock.VALIDATED, this.isValidApiary));
            if (validatingPlayer != null && this.isValidApiary) {
                NetworkHooks.openGui(validatingPlayer, this, this.getBlockPos());
            }
            this.ticksSinceValidation = 0;
        }
    }

    public boolean validateStructure(Level worldIn, @Nullable ServerPlayer validatingPlayer) {
        AtomicBoolean isStructureValid = new AtomicBoolean(true);
        this.apiaryStorage = getApiaryStorage();
        validateStorageLink();
        isStructureValid.set(validateBlocks(isStructureValid, worldIn, validatingPlayer));

        if (apiaryStorage == null) {
            isStructureValid.set(false);
            if (validatingPlayer != null) {
                validatingPlayer.displayClientMessage(new TextComponent("Missing Apiary Storage Block!"), false);
            }
        }

        if (validatingPlayer != null) {
            validatingPlayer.displayClientMessage(isStructureValid.get() ? TranslationConstants.Apiary.VALIDATED_SUCCESS : TranslationConstants.Apiary.VALIDATED_FAILED, true);
        }
        return isStructureValid.get();
    }

    private boolean validateBlocks(AtomicBoolean isStructureValid, Level worldIn, @Nullable ServerPlayer validatingPlayer) {
        for (BlockPos pos : structureBlocks) {
            if (isValidApiaryBlock((BlockAccessor) worldIn.getBlockState(pos).getBlock())) {
                tryLinkStorage(worldIn.getBlockEntity(pos));
            } else {
                isStructureValid.set(false);
                if (validatingPlayer != null)
                    validatingPlayer.displayClientMessage(new TextComponent(String.format("Block at position (X: %1$s Y: %2$s Z: %3$s) is invalid!", pos.getX(), pos.getY(), pos.getZ())), false);
            }
        }

        return isStructureValid.get();
    }

    public BoundingBox buildStructureBounds(int horizontalOffset, int verticalOffset) {
        return MultiBlockHelper.buildStructureBounds(this.getBlockPos(), width, height, depth, getAdjustedHOffset(horizontalOffset), -verticalOffset - 2, 0, this.getBlockState().getValue(ApiaryBlock.FACING));
    }

    private int getAdjustedHOffset(int horizontalOffset) {
        return -horizontalOffset - 3  - (int) ((width - 7) * 0.5);
    }

    private void buildStructureBlockList() {
        if (this.level != null) {
            BoundingBox box = buildStructureBounds(this.getHorizontalOffset(), this.getVerticalOffset());
            structureBlocks.clear();
            BlockPos.betweenClosedStream(box)
                    .filter(blockPos -> isStructurePosition(blockPos, box))
                    .forEach(blockPos -> structureBlocks.add(blockPos.immutable()));
        }
    }

    public void runCreativeBuild(ServerPlayer player) {
        if (this.level != null && player.isCreative()) {
            buildStructureBlockList();
            AtomicBoolean addedStorage = new AtomicBoolean(false);
            structureBlocks.stream()
                    .filter(this::blockAtPosIsNotApiary)
                    .forEach(blockPos -> {
                        if (addedStorage.get()) {
                            this.level.setBlockAndUpdate(blockPos, Blocks.GLASS.defaultBlockState());
                        } else {
                            //TODO this.level.setBlockAndUpdate(blockPos, ModBlocks.APIARY_STORAGE_BLOCK.get().defaultBlockState());
                            addedStorage.set(true);
                        }
                    });
            runStructureValidation(player);
        }
    }

    private boolean blockAtPosIsNotApiary(BlockPos blockPos) {
        return this.level != null && !(this.level.getBlockState(blockPos).getBlock() instanceof ApiaryBlock);
    }

    private void tryLinkStorage(BlockEntity tile) {
        if (tile instanceof ApiaryStorageTileEntity storage && apiaryStorage == null && storage.getApiaryPos() == null) {
            apiaryStorage = storage;
            setStoragePos(apiaryStorage.getBlockPos());
            apiaryStorage.setApiaryPos(this.worldPosition);
            if (level != null) {
                level.sendBlockUpdated(getStoragePos(), apiaryStorage.getBlockState(), apiaryStorage.getBlockState(), 2);
            }
        }
    }

    private void validateStorageLink() {
        if (apiaryStorage != null && (apiaryStorage.getApiaryPos() == null || positionMismatch(apiaryStorage.getApiaryPos()))) {
            apiaryStorage = null;
            storagePos = null;
            setChanged();
        }
    }

    private boolean positionMismatch(BlockPos pos) {
        return pos.compareTo(this.worldPosition) != 0;
    }

    public int getHorizontalOffset() {
        return horizontalOffset;
    }

    /**
     * Sets the horizontal offset for the apiary block entity in relationship to the apiary structure.
     * This value makes an assumption that the apiary is starting from the center-most position in the structure face
     * and that the apiary cannot be placed at the edges.
     *
     * Example: 7-wide face, apiary is at canter position with a value range of -2/+2
     * 14-wide face, apiary is starting at 7 blocks from right with a value range of -5/+6
     *
     * @param horizontalOffset Sets the horizontal offset for the apiary block in relationship to the apiary structure.
     */
    public void setHorizontalOffset(int horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
    }

    public int getVerticalOffset() {
        return verticalOffset;
    }

    /**
     * Sets the vertical offset for the apiary block entity in relationship to the apiary structure.
     * This value makes an assumption that the apiary is starting two blocks up from the bottom most block
     * and that the apiary cannot be placed at the edges.
     *
     * Example: 6-high face, apiary is two blocks up from bottom with a value range of -1/+2
     * 10-high face, apiary is starting at 2 blocks up from bottom with a value range of -1/+6
     *
     * @param verticalOffset Sets the vertical offset for the apiary block entity in relationship to the apiary structure.
     */
    public void setVerticalOffset(int verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    public boolean isPreviewed() {
        return previewed;
    }

    public void setPreviewed(boolean previewed) {
        this.previewed = previewed;
    }

    public BlockPos getStoragePos() {
        return storagePos;
    }

    public void setStoragePos(BlockPos storagePos) {
        this.storagePos = storagePos;
    }

    /**
     * Returns the width of the apiary structure.
     *
     * @return Width of the apiary structure.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the apiary structure.
     * Minimum value is 7.
     * Maximum value is 16.
     *
     * @param width Sets the width of the apiary structure.
     */
    public void setWidth(int width) {
        this.width = Mth.clamp(width, 7, 16);
    }

    /**
     * Returns the height of the apiary structure.
     *
     * @return Height of the apiary structure.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the apiary structure.
     * Minimum value is 6.
     * Maximum value is 10.
     *
     * @param height Sets the width of the apiary structure.
     */
    public void setHeight(int height) {
        this.height = Mth.clamp(height, 6, 10);
    }

    /**
     * Returns the depth of the apiary structure.
     *
     * @return Depth of the apiary structure.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets the depth of the apiary structure.
     * Minimum value is 7.
     * Maximum value is 16.
     *
     * @param depth Sets the width of the apiary structure.
     */
    public void setDepth(int depth) {
        this.depth = Mth.clamp(depth, 7, 16);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
        if (level != null) {
            if (isValidApiary(true)) {
                return new ValidatedApiaryContainer(i, level, worldPosition, playerInventory);
            }
            return new UnvalidatedApiaryContainer(i, level, worldPosition, playerInventory);
        }
        return null;
    }

    @Override
    public void switchTab(ServerPlayer player, ApiaryTab tab) {
        if (level != null && tab == ApiaryTab.STORAGE) {
            NetworkHooks.openGui(player, getApiaryStorage(), getStoragePos());
        }
    }

    public static void baseServerTick(Level level, BlockPos pos, BlockState state, ApiaryController controller) {
        if (controller.isValidApiary) {
            if (controller.ticksSinceValidation >= 20) controller.runStructureValidation(null);
            else controller.ticksSinceValidation++;
        }
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TranslationConstants.Guis.APIARY;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.loadFromNBT(nbt);
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag nbt) {
        super.save(nbt);
        return this.saveToNBT(nbt);
    }

    public void loadFromNBT(CompoundTag nbt) {
        if (nbt.contains(NBTConstants.NBT_VALID_APIARY))
            this.isValidApiary = nbt.getBoolean(NBTConstants.NBT_VALID_APIARY);
        if (nbt.contains(NBTConstants.NBT_VERT_OFFSET))
            this.setVerticalOffset(nbt.getInt(NBTConstants.NBT_VERT_OFFSET));
        if (nbt.contains(NBTConstants.NBT_HOR_OFFSET))
            this.setHorizontalOffset(nbt.getInt(NBTConstants.NBT_HOR_OFFSET));
        if (nbt.contains(NBTConstants.NBT_WIDTH))
            this.setWidth(nbt.getInt(NBTConstants.NBT_WIDTH));
        if (nbt.contains(NBTConstants.NBT_HEIGHT))
            this.setHeight(nbt.getInt(NBTConstants.NBT_HEIGHT));
        if (nbt.contains(NBTConstants.NBT_DEPTH))
            this.setDepth(nbt.getInt(NBTConstants.NBT_DEPTH));
        if (nbt.contains(NBTConstants.NBT_STORAGE_POS))
            setStoragePos(NbtUtils.readBlockPos(nbt.getCompound(NBTConstants.NBT_STORAGE_POS)));
    }

    public CompoundTag saveToNBT(CompoundTag nbt) {
        nbt.putBoolean(NBTConstants.NBT_VALID_APIARY, isValidApiary);
        nbt.putInt(NBTConstants.NBT_VERT_OFFSET, getVerticalOffset());
        nbt.putInt(NBTConstants.NBT_HOR_OFFSET, getHorizontalOffset());
        nbt.putInt(NBTConstants.NBT_WIDTH, getWidth());
        nbt.putInt(NBTConstants.NBT_HEIGHT, getHeight());
        nbt.putInt(NBTConstants.NBT_DEPTH, getDepth());
        if (getStoragePos() != null) nbt.put(NBTConstants.NBT_STORAGE_POS, NbtUtils.writeBlockPos(getStoragePos()));
        return nbt;
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        return save(new CompoundTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) loadFromNBT(tag);
    }
}
