package com.dungeonderps.resourcefulbees.tileentity;


import com.dungeonderps.resourcefulbees.block.ApiaryBlock;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.dungeonderps.resourcefulbees.container.UnvalidatedApiaryContainer;
import com.dungeonderps.resourcefulbees.container.ValidatedApiaryContainer;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.item.BeeJar;
import com.dungeonderps.resourcefulbees.lib.ApiaryTabs;
import com.dungeonderps.resourcefulbees.network.NetPacketHandler;
import com.dungeonderps.resourcefulbees.network.packets.UpdateClientApiaryMessage;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.dungeonderps.resourcefulbees.lib.BeeConstants.*;

public class ApiaryTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider, IApiaryMultiblock {
    public static final int IMPORT = 0;
    public static final int EXPORT = 2;
    public static final int EMPTY_JAR = 1;
    public final LinkedHashMap<String, ApiaryBee> BEES = new LinkedHashMap<>();
    public final List<BlockPos> STRUCTURE_BLOCKS = new ArrayList<>();
    protected int TIER;
    public Stack<String> honeycombs = new Stack<>();
    private boolean isValidApiary;
    public boolean previewed;
    public AutomationSensitiveItemStackHandler h = new ApiaryTileEntity.TileStackHandler(4);
    public LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(() -> h);
    public int horizontalOffset = 0;
    public int verticalOffset = 0;
    public int numPlayersUsing;
    private int ticksSinceValidation = 290;
    private int ticksSinceSync;
    public BlockPos apiaryStoragePos;


    public ApiaryTileEntity() {
        super(RegistryHandler.APIARY_TILE_ENTITY.get());
    }

    public static int calculatePlayersUsingSync(World world, ApiaryTileEntity apiaryTileEntity, int ticksSinceSync, int posX, int posY, int posZ, int numPlayersUsing) {
        if (!world.isRemote && numPlayersUsing != 0 && (ticksSinceSync + posX + posY + posZ) % 200 == 0) {
            numPlayersUsing = calculatePlayersUsing(world, apiaryTileEntity, posX, posY, posZ);
        }

        return numPlayersUsing;
    }

    public static int calculatePlayersUsing(World world, ApiaryTileEntity apiaryTileEntity, int posX, int posY, int posZ) {
        int i = 0;
        float f = 5.0F;

        for (PlayerEntity playerentity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(posX - f, posY - f, posZ - f, (posX + 1) + f, (posY + 1) + f, (posZ + 1) + f))) {
            if (playerentity.openContainer instanceof ValidatedApiaryContainer) {
                ApiaryTileEntity apiaryTileEntity1 = ((ValidatedApiaryContainer) playerentity.openContainer).apiaryTileEntity;
                if (apiaryTileEntity1 == apiaryTileEntity) {
                    ++i;
                }
            }
        }

        return i;
    }

    public static void syncApiaryToPlayersUsing(World world, BlockPos pos, CompoundNBT data) {
        NetPacketHandler.sendToAllLoaded(new UpdateClientApiaryMessage(pos, data), world, pos);
    }

    public boolean isValidApiary() {
        return isValidApiary;
    }

    public int getTier() {
        return TIER;
    }

    public int getMaxBees() {
        return Config.APIARY_MAX_BEES.get();
    }

    public int getBeeCount() {
        return this.BEES.size();
    }

    public boolean releaseBee(@Nonnull BlockState state, @Nonnull CompoundNBT nbt, @Nonnull State beehiveState, @Nullable BlockPos flowerPos, boolean exportBee) {
        BlockPos blockpos = this.getPos();
        if (exportBee || !shouldStayInHive(beehiveState)) {
            nbt.remove("Passengers");
            nbt.remove("Leash");
            nbt.remove("UUID");
            Direction direction = state.get(BeehiveBlock.FACING);
            BlockPos blockpos1 = blockpos.offset(direction);
            if (world == null || this.world.getBlockState(blockpos1).getCollisionShape(this.world, blockpos1).isEmpty()) {
                Entity entity = EntityType.loadEntityAndExecute(nbt, this.world, entity1 -> entity1);
                if (entity != null) {
                    float f = entity.getWidth();
                    double d0 = 0.55D + f / 2.0F;
                    double d1 = blockpos.getX() + 0.5D + d0 * direction.getXOffset();
                    double d2 = blockpos.getY() + 0.5D - (entity.getHeight() / 2.0F);
                    double d3 = blockpos.getZ() + 0.5D + d0 * direction.getZOffset();
                    entity.setLocationAndAngles(d1, d2, d3, entity.rotationYaw, entity.rotationPitch);
                    if (entity instanceof CustomBeeEntity) {
                        CustomBeeEntity beeEntity = (CustomBeeEntity) entity;
                        if (flowerPos != null && !beeEntity.hasFlower() && this.world.rand.nextFloat() < 0.9F) {
                            beeEntity.setFlowerPos(flowerPos);
                        }

                        if (beehiveState == State.HONEY_DELIVERED) {
                            beeEntity.onHoneyDelivered();
                            if (!exportBee && beeEntity.getBeeInfo().getHoneycombColor() != null && !beeEntity.getBeeInfo().getHoneycombColor().isEmpty() && isValidApiary) {
                                getApiaryStorage().deliverHoneycomb(beeEntity.getBeeType(), getTier());
                            }
                        }

                        beeEntity.resetTicksWithoutNectar();

                        if (exportBee) {
                            export(beeEntity);
                        } else {
                            BlockPos hivePos = this.getPos();
                            this.world.playSound(null, hivePos.getX(), hivePos.getY(), hivePos.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            this.world.addEntity(entity);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ApiaryStorageTileEntity getApiaryStorage(){
        if (world != null && apiaryStoragePos != null) {
            TileEntity tile = world.getTileEntity(apiaryStoragePos);
            if (tile instanceof ApiaryStorageTileEntity) {
                return (ApiaryStorageTileEntity) tile;
            }
        }
        return null;
    }

    public boolean tryEnterHive(Entity bee, boolean hasNectar, boolean imported) {
        if (isValidApiary)
            return this.tryEnterHive(bee, hasNectar, 0, imported);
        return false;
    }

    public boolean tryEnterHive(@Nonnull Entity bee, boolean hasNectar, int ticksInHive, boolean imported) {
        if (this.world != null) {
            if (bee instanceof CustomBeeEntity) {
                CustomBeeEntity bee1 = (CustomBeeEntity) bee;
                String type = bee1.getBeeType();

                if (!this.BEES.containsKey(type) && this.BEES.size() < getMaxBees()) {
                    bee.removePassengers();
                    CompoundNBT nbt = new CompoundNBT();
                    bee.writeUnlessPassenger(nbt);

                    int maxTimeInHive = bee1.getBeeInfo().getMaxTimeInHive();
                    maxTimeInHive = (int) (maxTimeInHive * (1 - (0.10F + this.getTier() * .05)));
                    int finalMaxTimeInHive = maxTimeInHive;

                    this.BEES.computeIfAbsent(bee1.getBeeType(), k -> new ApiaryBee(nbt, ticksInHive, hasNectar ? finalMaxTimeInHive : MIN_HIVE_TIME, bee1.getFlowerPos(), bee1.getBeeType()));
                    BlockPos pos = this.getPos();
                    this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    if (imported) {
                        this.BEES.get(bee1.getBeeType()).isLocked = true;
                    }

                    if (this.numPlayersUsing > 0)
                        syncApiaryToPlayersUsing(this.world, this.getPos(), this.saveToNBT(new CompoundNBT()));

                    bee.remove();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void tick() {
        if (world != null) {
            BlockPos blockpos = this.getPos();
            int x = blockpos.getX();
            int y = blockpos.getY();
            int z = blockpos.getZ();
            ++ticksSinceSync;
            this.numPlayersUsing = calculatePlayersUsingSync(this.world, this, this.ticksSinceSync, x, y, z, this.numPlayersUsing);

            this.tickBees();

            if (!world.isRemote && isValidApiary) {
                if (ticksSinceValidation >= 300)
                    runStructureValidation(null);
                else
                    ticksSinceValidation++;
                if (this.BEES.size() > 0 && this.world.getRandom().nextDouble() < 0.005D) {
                    double d0 = blockpos.getX() + 0.5D;
                    double d1 = blockpos.getY();
                    double d2 = blockpos.getZ() + 0.5D;
                    this.world.playSound(null, d0, d1, d2, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            } else if (!world.isRemote && apiaryStoragePos != null) {
                apiaryStoragePos = null;
            }
        }
    }

    private void tickBees() {
        if (this.world != null) {
            Iterator<Map.Entry<String, ApiaryBee>> iterator = this.BEES.entrySet().iterator();
            BlockState blockstate = this.getBlockState();

            while (iterator.hasNext()) {
                Map.Entry<String, ApiaryBee> element = iterator.next();
                ApiaryBee apiaryBee = element.getValue();
                if (!apiaryBee.isLocked && apiaryBee.ticksInHive > apiaryBee.minOccupationTicks && !world.isRemote) {

                    CompoundNBT compoundnbt = apiaryBee.entityData;
                    State state = compoundnbt.getBoolean("HasNectar") ? State.HONEY_DELIVERED : State.BEE_RELEASED;
                    if (this.releaseBee(blockstate, compoundnbt, state, apiaryBee.savedFlowerPos, false)) {
                        iterator.remove();
                        if (this.numPlayersUsing > 0 && !this.world.isRemote)
                            syncApiaryToPlayersUsing(this.world, this.getPos(), this.saveToNBT(new CompoundNBT()));
                    }
                } else {
                    apiaryBee.ticksInHive++;
                }
            }
        }
    }

    @Nonnull
    public ListNBT writeBees() {
        ListNBT listnbt = new ListNBT();

        this.BEES.forEach((key, apiaryBee) -> {
            apiaryBee.entityData.remove("UUID");
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.put("EntityData", apiaryBee.entityData);
            compoundnbt.putInt("TicksInHive", apiaryBee.ticksInHive);
            compoundnbt.putInt("MinOccupationTicks", apiaryBee.minOccupationTicks);
            compoundnbt.putBoolean(NBT_LOCKED, apiaryBee.isLocked);
            compoundnbt.putString(NBT_BEE_TYPE, apiaryBee.beeType);
            if (apiaryBee.savedFlowerPos != null) {
                compoundnbt.put(NBT_FLOWER_POS, NBTUtil.writeBlockPos(apiaryBee.savedFlowerPos));
            }
            listnbt.add(compoundnbt);
        });

        return listnbt;
    }

    public boolean shouldStayInHive(State beehiveState) {
        if (world != null)
            return (this.world.isNightTime() || this.world.isRaining()) && beehiveState != State.EMERGENCY;
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isFullOfBees() {
        return this.BEES.size() >= getMaxBees();
    }

    public boolean isAllowedBee() {
        Block hive = getBlockState().getBlock();
        return isValidApiary && hive instanceof ApiaryBlock;
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        this.loadFromNBT(nbt);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);
        return this.saveToNBT(nbt);
    }

    public void loadFromNBT(CompoundNBT nbt) {
        ListNBT listnbt = nbt.getList(NBT_BEES, 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT data = listnbt.getCompound(i);

            BlockPos savedFlowerPos = data.contains(NBT_FLOWER_POS) ? NBTUtil.readBlockPos(data.getCompound(NBT_FLOWER_POS)) : null;

            String beeType = data.getString(NBT_BEE_TYPE);

            this.BEES.computeIfAbsent(data.getString(NBT_BEE_TYPE), k -> new ApiaryBee(
                    data.getCompound("EntityData"),
                    data.getInt("TicksInHive"),
                    data.getInt("MinOccupationTicks"),
                    savedFlowerPos,
                    beeType));

            this.BEES.get(beeType).isLocked = data.getBoolean(NBT_LOCKED);
        }

        if (nbt.contains(NBT_VALID_APIARY))
            this.isValidApiary = nbt.getBoolean(NBT_VALID_APIARY);
        if (nbt.contains(NBT_VERT_OFFSET))
            this.verticalOffset = nbt.getInt(NBT_VERT_OFFSET);
        if (nbt.contains(NBT_HOR_OFFSET))
            this.horizontalOffset = nbt.getInt(NBT_HOR_OFFSET);
        CompoundNBT invTag = nbt.getCompound(NBT_INVENTORY);
        h.deserializeNBT(invTag);

        if (nbt.contains(NBT_STORAGE_POS))
            apiaryStoragePos = NBTUtil.readBlockPos(nbt.getCompound(NBT_STORAGE_POS));
        if (nbt.contains(NBT_TIER)) {
            setTier(nbt.getInt(NBT_TIER));
        }
    }

    public CompoundNBT saveToNBT(CompoundNBT nbt) {
        CompoundNBT inv = this.h.serializeNBT();
        nbt.put(NBT_INVENTORY, inv);
        nbt.put(NBT_BEES, this.writeBees());
        nbt.putBoolean(NBT_VALID_APIARY, isValidApiary);
        nbt.putInt(NBT_VERT_OFFSET, verticalOffset);
        nbt.putInt(NBT_HOR_OFFSET, horizontalOffset);
        if (apiaryStoragePos != null)
            nbt.put(NBT_STORAGE_POS, NBTUtil.writeBlockPos(apiaryStoragePos));
        nbt.putInt(NBT_TIER, getTier());
        return nbt;
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, CompoundNBT tag) {
        this.read(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return super.getUpdatePacket();
    }

    public void lockOrUnlockBee(String beeType) {
        this.BEES.get(beeType).isLocked = !this.BEES.get(beeType).isLocked;
        syncApiaryToPlayersUsing(this.world, this.getPos(), this.saveToNBT(new CompoundNBT()));
    }

    //region Import/Export
    public void importBee(ServerPlayerEntity player) {
        World world = this.world;
        boolean imported = false;

        if (world != null) {
            if (!this.h.getStackInSlot(IMPORT).isEmpty() && this.h.getStackInSlot(EMPTY_JAR).getCount() < 16) {
                ItemStack filledJar = this.h.getStackInSlot(IMPORT);
                ItemStack emptyJar = this.h.getStackInSlot(EMPTY_JAR);

                if (filledJar.getItem() instanceof BeeJar) {
                    BeeJar jarItem = (BeeJar) filledJar.getItem();
                    Entity entity = jarItem.getEntityFromStack(filledJar, world, true);

                    if (entity instanceof CustomBeeEntity) {
                        CustomBeeEntity beeEntity = (CustomBeeEntity) entity;
                        imported = tryEnterHive(beeEntity, beeEntity.hasNectar(), true);

                        if (imported) {
                            filledJar.shrink(1);
                            if (emptyJar.isEmpty()) {
                                this.h.setStackInSlot(EMPTY_JAR, new ItemStack(jarItem));
                            } else {
                                emptyJar.grow(1);
                            }
                        }
                    }
                }
            }
        }

        player.sendStatusMessage(new TranslationTextComponent("gui.resourcefulbees.apiary.import." + imported), true);
    }

    public void exportBee(ServerPlayerEntity player, String beeType) {
        boolean exported = false;
        ApiaryBee bee = this.BEES.get(beeType);
        CompoundNBT data = bee.entityData;
        State state = data.getBoolean("HasNectar") ? State.HONEY_DELIVERED : State.BEE_RELEASED;

        if (bee.isLocked && h.getStackInSlot(EXPORT).isEmpty() && !h.getStackInSlot(EMPTY_JAR).isEmpty()) {
            exported = releaseBee(this.getBlockState(), data, state, bee.savedFlowerPos, true);
        }
        if (exported) {
            this.BEES.remove(beeType);
            this.h.getStackInSlot(EMPTY_JAR).shrink(1);
            if (this.numPlayersUsing > 0 && this.world != null && !this.world.isRemote)
                syncApiaryToPlayersUsing(this.world, this.getPos(), this.saveToNBT(new CompoundNBT()));
        }

        player.sendStatusMessage(new TranslationTextComponent("gui.resourcefulbees.apiary.export." + exported), true);
    }

    public void export(CustomBeeEntity beeEntity) {
        ItemStack beeJar = new ItemStack(RegistryHandler.BEE_JAR.get());
        CompoundNBT data = new CompoundNBT();
        data.putString(NBT_ENTITY, "resourcefulbees:bee");
        beeEntity.writeWithoutTypeId(data);
        String primaryColor = beeEntity.getBeeInfo().getPrimaryColor();
        data.putString(NBT_COLOR, primaryColor != null && !primaryColor.isEmpty() ? primaryColor : String.valueOf(DEFAULT_ITEM_COLOR));
        beeJar.setTag(data);
        this.h.setStackInSlot(EXPORT, beeJar);
    }
    //endregion

    //region Validation
    public static ITag<Block> getValidApiaryTag(){
        return BeeInfoUtils.getBlockTag("resourcefulbees:valid_apiary");
    }

    public void runStructureValidation(@Nullable ServerPlayerEntity validatingPlayer) {
        if (this.world != null && !this.world.isRemote()) {
            if (!this.isValidApiary || STRUCTURE_BLOCKS.isEmpty())
                buildStructureBlockList();
            this.isValidApiary = validateStructure(this.world, validatingPlayer);
            this.world.setBlockState(this.getPos(), getBlockState().with(ApiaryBlock.VALIDATED, this.isValidApiary));
            if (validatingPlayer != null && this.isValidApiary) {
                NetworkHooks.openGui(validatingPlayer, this, this.getPos());
            }
            this.ticksSinceValidation = 0;
        }
    }

    public boolean validateStructure(World worldIn, @Nullable ServerPlayerEntity validatingPlayer) {
        boolean isStructureValid = true;
        ApiaryStorageTileEntity apiaryStorage = getApiaryStorage();
        for (BlockPos pos : STRUCTURE_BLOCKS) {
            Block block = worldIn.getBlockState(pos).getBlock();
            if (block.isIn(BeeInfoUtils.getValidApiaryTag())) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof ApiaryStorageTileEntity) {
                    ApiaryStorageTileEntity apiaryStorageTile = (ApiaryStorageTileEntity) tile;
                    if (apiaryStorage == null && apiaryStorageTile.getApiaryPos() == null) {
                        apiaryStorage = apiaryStorageTile;
                        apiaryStoragePos = apiaryStorageTile.getPos();
                        apiaryStorageTile.setApiaryPos(this.pos);
                    }
                }
            } else {
                isStructureValid = false;
                if (validatingPlayer != null) {
                    validatingPlayer.sendStatusMessage(new StringTextComponent(String.format("Block at position (X: %1$s Y: %2$s Z: %3$s) is invalid!", pos.getX(), pos.getY(), pos.getZ())), false);
                }
            }
        }
        if (apiaryStorage == null){
            isStructureValid = false;
            if (validatingPlayer != null) {
                validatingPlayer.sendStatusMessage(new StringTextComponent("Missing Apiary Storage Block!"), false);
            }
        }

        if (validatingPlayer != null) {
            validatingPlayer.sendStatusMessage(new TranslationTextComponent("gui.resourcefulbees.apiary.validated." + isStructureValid), true);
        }

        return isStructureValid;
    }

    public MutableBoundingBox buildStructureBounds(int horizontalOffset, int verticalOffset) {
        MutableBoundingBox box;
        int posX = this.getPos().getX();
        int posY = this.getPos().getY();
        int posZ = this.getPos().getZ();

        switch (this.getBlockState().get(ApiaryBlock.FACING)) {
            case NORTH:
                posX -= 3 + horizontalOffset;
                posY -= 2 + verticalOffset;
                box = new MutableBoundingBox(posX, posY, posZ, posX + 6, posY + 5, posZ - 6);
                break;
            case EAST:
                posZ -= 3 + horizontalOffset;
                posY -= 2 + verticalOffset;
                box = new MutableBoundingBox(posX, posY, posZ, posX + 6, posY + 5, posZ + 6);
                break;
            case SOUTH:
                posX -= 3 - horizontalOffset;
                posY -= 2 + verticalOffset;
                box = new MutableBoundingBox(posX, posY, posZ, posX + 6, posY + 5, posZ + 6);
                break;
            default:
                posZ -= 3 - horizontalOffset;
                posY -= 2 + verticalOffset;
                box = new MutableBoundingBox(posX, posY, posZ, posX - 6, posY + 5, posZ + 6);
        }
        return box;
    }

    private void buildStructureBlockList() {
        if (this.world != null) {
            MutableBoundingBox box = buildStructureBounds(this.horizontalOffset, this.verticalOffset);
            STRUCTURE_BLOCKS.clear();
            BlockPos.getAllInBox(box).forEach((blockPos -> {
                if (blockPos.getX() == box.minX || blockPos.getX() == box.maxX ||
                        blockPos.getY() == box.minY || blockPos.getY() == box.maxY ||
                        blockPos.getZ() == box.minZ || blockPos.getZ() == box.maxZ) {
                    BlockPos savedPos = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    STRUCTURE_BLOCKS.add(savedPos);
                }
            }));
        }
    }
    //endregion

    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        if (world != null) {

            if (this.isValidApiary) {
                this.isValidApiary = validateStructure(world, (ServerPlayerEntity) playerEntity);
                if (this.isValidApiary) {
                    numPlayersUsing++;
                    return new ValidatedApiaryContainer(i, world, pos, playerInventory);
                }
            } else {
                numPlayersUsing++;
                return new UnvalidatedApiaryContainer(i, world, pos, playerInventory);
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? lazyOptional.cast() :
                super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || slot == 0 || slot == 1;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == 2 || slot == 3 || slot == 4;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.apiary");
    }

    public void runCreativeBuild(ServerPlayerEntity player) {
        if (this.world != null) {
            buildStructureBlockList();
            boolean addedStorage = false;
            for (BlockPos pos : STRUCTURE_BLOCKS) {
                Block block = this.world.getBlockState(pos).getBlock();
                if (!(block instanceof ApiaryBlock)) {
                    if (addedStorage) {
                        this.world.setBlockState(pos, Blocks.GLASS.getDefaultState());
                    }
                    else {
                        this.world.setBlockState(pos, RegistryHandler.APIARY_STORAGE_BLOCK.get().getDefaultState());
                        addedStorage = true;
                    }
                }
            }
            runStructureValidation(player);
        }
    }

    public void setTier(int tier) {
        this.TIER = tier;
    }

    @Override
    public void switchTab(ServerPlayerEntity player, ApiaryTabs tab) {
        if (world != null) {
            if (tab == ApiaryTabs.STORAGE) {
                NetworkHooks.openGui(player, getApiaryStorage(), apiaryStoragePos);
            }
        }
    }

    public enum State {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY
    }

    public static class ApiaryBee {
        public final CompoundNBT entityData;
        public final int minOccupationTicks;
        public final BlockPos savedFlowerPos;
        public final String beeType;
        public int ticksInHive;
        public boolean isLocked = false;

        public ApiaryBee(CompoundNBT nbt, int ticksinhive, int minoccupationticks, @Nullable BlockPos flowerPos, String beeType) {
            nbt.remove("UUID");
            this.entityData = nbt;
            this.ticksInHive = ticksinhive;
            this.minOccupationTicks = minoccupationticks;
            this.savedFlowerPos = flowerPos;
            this.beeType = beeType;
        }
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {

        protected TileStackHandler(int slots) {
            super(slots);
        }

        @Override
        public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
            return ApiaryTileEntity.this.getAcceptor();
        }

        @Override
        public AutomationSensitiveItemStackHandler.IRemover getRemover() {
            return ApiaryTileEntity.this.getRemover();
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            markDirty();
        }
    }
}
