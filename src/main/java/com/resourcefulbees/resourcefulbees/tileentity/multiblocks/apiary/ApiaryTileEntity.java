package com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary;


import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryBreederBlock;
import com.resourcefulbees.resourcefulbees.block.multiblocks.apiary.ApiaryStorageBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.AutomationSensitiveItemStackHandler;
import com.resourcefulbees.resourcefulbees.container.UnvalidatedApiaryContainer;
import com.resourcefulbees.resourcefulbees.container.ValidatedApiaryContainer;
import com.resourcefulbees.resourcefulbees.item.BeeJar;
import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.UpdateClientApiaryMessage;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.MultiBlockHelper;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.MIN_HIVE_TIME;
import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.RAINBOW_COLOR;

public class ApiaryTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider, IApiaryMultiblock {
    public static final int IMPORT = 0;
    public static final int EXPORT = 2;
    public static final int EMPTY_JAR = 1;
    public final Map<String, ApiaryBee> bees = new LinkedHashMap<>();
    private final List<BlockPos> structureBlocks = new ArrayList<>();
    protected int tier;
    private boolean isValidApiary;
    private boolean previewed;
    private final ApiaryTileEntity.TileStackHandler tileStackHandler = new ApiaryTileEntity.TileStackHandler(3);
    private final LazyOptional<IItemHandler> lazyOptional = LazyOptional.of(this::getTileStackHandler);
    private int horizontalOffset = 0;
    private int verticalOffset = 0;
    private int numPlayersUsing;
    private int ticksSinceValidation;
    private int ticksSinceSync;
    private BlockPos storagePos;
    private BlockPos breederPos;
    private ApiaryStorageTileEntity apiaryStorage;
    private ApiaryBreederTileEntity apiaryBreeder;
    protected int ticksSinceBeesFlagged;


    public ApiaryTileEntity() {
        super(ModTileEntityTypes.APIARY_TILE_ENTITY.get());
    }

    //region PLAYER SYNCING
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
    //endregion

    public boolean isValidApiary(boolean runValidation) {
        if (runValidation) {
            runStructureValidation(null);
        }
        return isValidApiary;
    }

    public int getTier() {
        return tier;
    }

    public int getMaxBees() {
        return Config.APIARY_MAX_BEES.get();
    }

    public int getBeeCount() {
        return this.bees.size();
    }

    public ApiaryStorageTileEntity getApiaryStorage() {
        if (world != null && getStoragePos() != null) {
            TileEntity tile = world.getTileEntity(getStoragePos());
            if (tile instanceof ApiaryStorageTileEntity) {
                return (ApiaryStorageTileEntity) tile;
            }
        }
        setStoragePos(null);
        return null;
    }

    public ApiaryBreederTileEntity getApiaryBreeder() {
        if (world != null && getBreederPos() != null) {
            TileEntity tile = world.getTileEntity(getBreederPos());
            if (tile instanceof ApiaryBreederTileEntity) {
                return (ApiaryBreederTileEntity) tile;
            }
        }
        setBreederPos(null);
        return null;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    //region BEE HANDLING
    public boolean releaseBee(@Nonnull BlockState state, ApiaryBee apiaryBee, boolean exportBee) {
        BlockPos blockPos = this.getPos();
        Direction direction = state.get(BeehiveBlock.FACING);
        BlockPos blockPos1 = blockPos.offset(direction);
        CompoundNBT nbt = apiaryBee.entityData;

        if (world != null && this.world.getBlockState(blockPos1).getCollisionShape(this.world, blockPos1).isEmpty()) {
            nbt.remove("Passengers");
            nbt.remove("Leash");
            nbt.remove("UUID");
            Entity entity = EntityType.func_220335_a(nbt, this.world, entity1 -> entity1);
            if (entity == null) return true;
            BeeInfoUtils.setEntityLocationAndAngle(blockPos, direction, entity);

            if (entity instanceof BeeEntity) {
                BeeEntity vanillaBeeEntity = (BeeEntity) entity;

                BlockPos flowerPos = apiaryBee.savedFlowerPos;
                if (flowerPos != null && !vanillaBeeEntity.hasFlower() && this.world.rand.nextFloat() < 0.9F) {
                    vanillaBeeEntity.setFlowerPos(flowerPos);
                }

                if (nbt.getBoolean("HasNectar")) {
                    vanillaBeeEntity.onHoneyDelivered();

                    if (!exportBee && isValidApiary(true)) {
                        getApiaryStorage().deliverHoneycomb(((BeeEntity) entity), getTier());
                    }
                }

                this.ageBee(apiaryBee.getTicksInHive(), vanillaBeeEntity);

                if (exportBee) {
                    exportBee(vanillaBeeEntity);
                } else {
                    BlockPos hivePos = this.getPos();
                    this.world.playSound(null, hivePos.getX(), hivePos.getY(), hivePos.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.world.addEntity(entity);
                }
            }
            return true;
        }
        return false;
    }

    private void ageBee(int ticksInHive, BeeEntity beeEntity) {
        BeeInfoUtils.ageBee(ticksInHive, beeEntity);
    }

    public boolean tryEnterHive(Entity bee, boolean hasNectar, boolean imported) {
        if (isValidApiary(true))
            return this.tryEnterHive(bee, hasNectar, 0, imported);
        return false;
    }

    public boolean tryEnterHive(@Nonnull Entity bee, boolean hasNectar, int ticksInHive, boolean imported) {
        if (this.world != null && bee instanceof BeeEntity) {
            BeeEntity beeEntity = (BeeEntity) bee;
            String type = BeeConstants.VANILLA_BEE_TYPE;
            String beeColor = BeeConstants.VANILLA_BEE_COLOR;

            if (bee instanceof ICustomBee) {
                type = ((ICustomBee) bee).getBeeType();
            }

            if (!this.bees.containsKey(type) && this.bees.size() < getMaxBees()) {
                bee.removePassengers();
                CompoundNBT nbt = new CompoundNBT();
                bee.writeUnlessPassenger(nbt);

                int maxTimeInHive = getMaxTimeInHive(BeeConstants.MAX_TIME_IN_HIVE);
                if (bee instanceof ICustomBee) {
                    ICustomBee iCustomBee = (ICustomBee) bee;
                    maxTimeInHive = getMaxTimeInHive(iCustomBee.getBeeData().getMaxTimeInHive());

                    if (iCustomBee.getBeeData().getColorData().hasPrimaryColor()) {
                        beeColor = iCustomBee.getBeeData().getColorData().getPrimaryColor();
                    } else if (iCustomBee.getBeeData().getColorData().isRainbowBee()) {
                        beeColor = RAINBOW_COLOR;
                    } else if (iCustomBee.getBeeData().getColorData().hasHoneycombColor()) {
                        beeColor = iCustomBee.getBeeData().getColorData().getHoneycombColor();
                    }
                }

                int finalMaxTimeInHive = maxTimeInHive;
                String finalType = type;

                String finalBeeColor = beeColor;

                ITextComponent displayName = bee.getName();

                this.bees.computeIfAbsent(finalType, k -> new ApiaryBee(nbt, ticksInHive, hasNectar ? finalMaxTimeInHive : MIN_HIVE_TIME, beeEntity.getFlowerPos(), finalType, finalBeeColor, displayName));
                BlockPos pos = this.getPos();
                this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if (imported) {
                    this.bees.get(type).setLocked(true);
                }

                if (this.getNumPlayersUsing() > 0)
                    syncApiaryToPlayersUsing(this.world, this.getPos(), this.saveToNBT(new CompoundNBT()));

                bee.remove();
                return true;
            }
        }
        return false;
    }

    private int getMaxTimeInHive(int timeInput) {
        if (this.tier != 1) {
            if (this.tier == 0) {
                return (int) (timeInput * 1.05);
            } else {
                return (int) (timeInput * (1 - getTier() * .05));
            }
        }
        return timeInput;
    }

    @Override
    public void tick() {
        if (world != null) {
            BlockPos blockpos = this.getPos();
            int x = blockpos.getX();
            int y = blockpos.getY();
            int z = blockpos.getZ();
            ++ticksSinceSync;
            this.setNumPlayersUsing(calculatePlayersUsingSync(this.world, this, this.ticksSinceSync, x, y, z, this.getNumPlayersUsing()));

            this.tickBees();

            if (!world.isRemote && isValidApiary) {
                if (ticksSinceValidation >= 20) runStructureValidation(null);
                else ticksSinceValidation++;

                if (this.bees.size() > 0 && this.world.getRandom().nextDouble() < 0.005D) {
                    double d0 = blockpos.getX() + 0.5D;
                    double d1 = blockpos.getY();
                    double d2 = blockpos.getZ() + 0.5D;
                    this.world.playSound(null, d0, d1, d2, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                ticksSinceBeesFlagged++;
                if (ticksSinceBeesFlagged == 80) {
                    BeeInfoUtils.flagBeesInRange(pos, world);
                    ticksSinceBeesFlagged = 0;
                }
            }
        }
    }

    private void tickBees() {
        if (this.world != null) {
            Iterator<Map.Entry<String, ApiaryBee>> iterator = this.bees.entrySet().iterator();
            BlockState blockstate = this.getBlockState();

            while (iterator.hasNext()) {
                Map.Entry<String, ApiaryBee> element = iterator.next();
                ApiaryBee apiaryBee = element.getValue();
                if (!apiaryBee.isLocked() && apiaryBee.getTicksInHive() > apiaryBee.minOccupationTicks && !world.isRemote) {


                    if (this.releaseBee(blockstate, apiaryBee, false)) {
                        iterator.remove();
                        if (this.getNumPlayersUsing() > 0 && !this.world.isRemote)
                            syncApiaryToPlayersUsing(this.world, this.getPos(), this.saveToNBT(new CompoundNBT()));
                    }
                } else {
                    apiaryBee.setTicksInHive(apiaryBee.getTicksInHive() + 1);
                    apiaryBee.setTicksInHive(Math.min(apiaryBee.getTicksInHive(), Integer.MAX_VALUE - 1));
                }
            }
        }
    }

    public boolean isFullOfBees() {
        return this.bees.size() >= getMaxBees();
    }

    public boolean isAllowedBee() {
        Block hive = getBlockState().getBlock();
        return isValidApiary(false) && hive instanceof ApiaryBlock;
    }

    public void lockOrUnlockBee(String beeType) {
        this.bees.get(beeType).setLocked(!this.bees.get(beeType).isLocked());
        syncApiaryToPlayersUsing(this.world, this.getPos(), this.saveToNBT(new CompoundNBT()));
    }
    //endregion

    //region NBT HANDLING

    @Nonnull
    public ListNBT writeBees() {
        ListNBT listnbt = new ListNBT();

        this.bees.forEach((key, apiaryBee) -> {
            apiaryBee.entityData.remove("UUID");
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.put("EntityData", apiaryBee.entityData);
            compoundnbt.putInt("TicksInHive", apiaryBee.getTicksInHive());
            compoundnbt.putInt("MinOccupationTicks", apiaryBee.minOccupationTicks);
            compoundnbt.putBoolean(NBTConstants.NBT_LOCKED, apiaryBee.isLocked());
            compoundnbt.putString(NBTConstants.NBT_BEE_TYPE, apiaryBee.beeType);
            compoundnbt.putString(NBTConstants.NBT_COLOR, apiaryBee.beeColor);
            compoundnbt.putString(NBTConstants.NBT_BEE_NAME, ITextComponent.Serializer.toJson(apiaryBee.displayName));
            if (apiaryBee.savedFlowerPos != null) {
                compoundnbt.put(NBTConstants.NBT_FLOWER_POS, NBTUtil.writeBlockPos(apiaryBee.savedFlowerPos));
            }
            listnbt.add(compoundnbt);
        });

        return listnbt;
    }

    public void loadBees(CompoundNBT nbt) {
        ListNBT listnbt = nbt.getList(NBTConstants.NBT_BEES, 10);

        if (!listnbt.isEmpty()) {
            for (int i = 0; i < listnbt.size(); ++i) {
                CompoundNBT data = listnbt.getCompound(i);

                BlockPos savedFlowerPos = data.contains(NBTConstants.NBT_FLOWER_POS) ? NBTUtil.readBlockPos(data.getCompound(NBTConstants.NBT_FLOWER_POS)) : null;
                String beeType = data.getString(NBTConstants.NBT_BEE_TYPE);
                String beeColor = data.contains(NBTConstants.NBT_COLOR) ? data.getString(NBTConstants.NBT_COLOR) : BeeConstants.VANILLA_BEE_COLOR;
                ITextComponent displayName = data.contains(NBTConstants.NBT_BEE_NAME) ? ITextComponent.Serializer.fromJson(data.getString(NBTConstants.NBT_BEE_NAME)) : new StringTextComponent("Temp Bee Name");

                this.bees.computeIfAbsent(data.getString(NBTConstants.NBT_BEE_TYPE), k -> new ApiaryBee(
                        data.getCompound("EntityData"),
                        data.getInt("TicksInHive"),
                        data.getInt("MinOccupationTicks"),
                        savedFlowerPos,
                        beeType,
                        beeColor,
                        displayName));

                this.bees.get(beeType).setLocked(data.getBoolean(NBTConstants.NBT_LOCKED));
            }
        }
    }

    @Override
    public void fromTag(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.fromTag(state, nbt);
        this.loadFromNBT(nbt);
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);
        return this.saveToNBT(nbt);
    }

    public void loadFromNBT(CompoundNBT nbt) {
        loadBees(nbt);

        if (nbt.contains(NBTConstants.NBT_VALID_APIARY))
            this.isValidApiary = nbt.getBoolean(NBTConstants.NBT_VALID_APIARY);
        if (nbt.contains(NBTConstants.NBT_VERT_OFFSET))
            this.setVerticalOffset(nbt.getInt(NBTConstants.NBT_VERT_OFFSET));
        if (nbt.contains(NBTConstants.NBT_HOR_OFFSET))
            this.setHorizontalOffset(nbt.getInt(NBTConstants.NBT_HOR_OFFSET));
        CompoundNBT invTag = nbt.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);

        if (nbt.contains(NBTConstants.NBT_STORAGE_POS))
            setStoragePos(NBTUtil.readBlockPos(nbt.getCompound(NBTConstants.NBT_STORAGE_POS)));
        if (nbt.contains(NBTConstants.NBT_BREEDER_POS))
            setBreederPos(NBTUtil.readBlockPos(nbt.getCompound(NBTConstants.NBT_BREEDER_POS)));
        if (nbt.contains(NBTConstants.NBT_TIER)) {
            setTier(nbt.getInt(NBTConstants.NBT_TIER));
        }
        validateBees();
    }

    private void validateBees() {
        if (this.world == null) return;
        bees.forEach((s, apiaryBee) -> {
            String id = apiaryBee.entityData.getString("id");
            EntityType<?> type = BeeInfoUtils.getEntityType(id);
            if (type == EntityType.PIG) bees.remove(s);
        });
    }

    public CompoundNBT saveToNBT(CompoundNBT nbt) {
        CompoundNBT inv = this.getTileStackHandler().serializeNBT();
        nbt.put(NBTConstants.NBT_INVENTORY, inv);
        nbt.put(NBTConstants.NBT_BEES, this.writeBees());
        nbt.putBoolean(NBTConstants.NBT_VALID_APIARY, isValidApiary);
        nbt.putInt(NBTConstants.NBT_VERT_OFFSET, getVerticalOffset());
        nbt.putInt(NBTConstants.NBT_HOR_OFFSET, getHorizontalOffset());
        if (getStoragePos() != null)
            nbt.put(NBTConstants.NBT_STORAGE_POS, NBTUtil.writeBlockPos(getStoragePos()));
        if (getBreederPos() != null)
            nbt.put(NBTConstants.NBT_BREEDER_POS, NBTUtil.writeBlockPos(getBreederPos()));
        nbt.putInt(NBTConstants.NBT_TIER, getTier());
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
        this.fromTag(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return super.getUpdatePacket();
    }
    //endregion

    //region IMPORT/EXPORT
    public void importBee(ServerPlayerEntity player) {
        World world = this.world;
        boolean imported = false;

        if (world != null && !this.getTileStackHandler().getStackInSlot(IMPORT).isEmpty() && this.getTileStackHandler().getStackInSlot(EMPTY_JAR).getCount() < 16) {
            ItemStack filledJar = this.getTileStackHandler().getStackInSlot(IMPORT);
            ItemStack emptyJar = this.getTileStackHandler().getStackInSlot(EMPTY_JAR);

            if (filledJar.getItem() instanceof BeeJar) {
                BeeJar jarItem = (BeeJar) filledJar.getItem();
                Entity entity = jarItem.getEntityFromStack(filledJar, world, true);

                if (entity instanceof BeeEntity) {
                    BeeEntity beeEntity = (BeeEntity) entity;
                    imported = tryEnterHive(beeEntity, beeEntity.hasNectar(), true);

                    if (imported) {
                        filledJar.shrink(1);
                        if (emptyJar.isEmpty()) {
                            this.getTileStackHandler().setStackInSlot(EMPTY_JAR, new ItemStack(jarItem));
                        } else {
                            emptyJar.grow(1);
                        }
                    }
                }
            }
        }

        player.sendStatusMessage(new TranslationTextComponent("gui.resourcefulbees.apiary.import." + imported), true);
    }

    public void exportBee(ServerPlayerEntity player, String beeType) {
        boolean exported = false;
        ApiaryBee bee = this.bees.get(beeType);

        if (bee.isLocked() && getTileStackHandler().getStackInSlot(EXPORT).isEmpty() && !getTileStackHandler().getStackInSlot(EMPTY_JAR).isEmpty()) {
            exported = releaseBee(this.getBlockState(), bee, true);
        }
        if (exported) {
            this.bees.remove(beeType);
            this.getTileStackHandler().getStackInSlot(EMPTY_JAR).shrink(1);
            if (this.getNumPlayersUsing() > 0 && this.world != null && !this.world.isRemote)
                syncApiaryToPlayersUsing(this.world, this.getPos(), this.saveToNBT(new CompoundNBT()));
        }

        player.sendStatusMessage(new TranslationTextComponent("gui.resourcefulbees.apiary.export." + exported), true);
    }

    public void exportBee(BeeEntity beeEntity) {
        ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
        beeJar.setTag(BeeJar.createTag(beeEntity));
        BeeJar.renameJar(beeJar, beeEntity);
        this.getTileStackHandler().setStackInSlot(EXPORT, beeJar);
    }
    //endregion

    //region STRUCTURE VALIDATION
    public void runStructureValidation(@Nullable ServerPlayerEntity validatingPlayer) {
        if (this.world != null && !this.world.isRemote()) {
            if (!this.isValidApiary || structureBlocks.isEmpty())
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
        AtomicBoolean isStructureValid = new AtomicBoolean(true);
        this.apiaryStorage = getApiaryStorage();
        this.apiaryBreeder = getApiaryBreeder();
        validateLinks();
        isStructureValid.set(validateBlocks(isStructureValid, worldIn, validatingPlayer));

        if (apiaryStorage == null) {
            isStructureValid.set(false);
            if (validatingPlayer != null) {
                validatingPlayer.sendStatusMessage(new StringTextComponent("Missing Apiary Storage Block!"), false);
            }
        }

        if (validatingPlayer != null) {
            validatingPlayer.sendStatusMessage(new TranslationTextComponent("gui.resourcefulbees.apiary.validated." + isStructureValid.get()), true);
        }
        return isStructureValid.get();
    }

    private boolean validateBlocks(AtomicBoolean isStructureValid, World worldIn, @Nullable ServerPlayerEntity validatingPlayer) {
        structureBlocks.forEach(pos -> {
            Block block = worldIn.getBlockState(pos).getBlock();
            if (block.isIn(BeeInfoUtils.getValidApiaryTag()) || block instanceof ApiaryBreederBlock || block instanceof ApiaryStorageBlock || block instanceof ApiaryBlock) {
                TileEntity tile = worldIn.getTileEntity(pos);
                linkStorageAndBreeder(tile);
            } else {
                isStructureValid.set(false);
                if (validatingPlayer != null)
                    validatingPlayer.sendStatusMessage(new StringTextComponent(String.format("Block at position (X: %1$s Y: %2$s Z: %3$s) is invalid!", pos.getX(), pos.getY(), pos.getZ())), false);
            }
        });

        return isStructureValid.get();
    }


    public MutableBoundingBox buildStructureBounds(int horizontalOffset, int verticalOffset) {
        return MultiBlockHelper.buildStructureBounds(this.getPos(), 7, 6, 7, -horizontalOffset - 3, -verticalOffset - 2, 0, this.getBlockState().get(ApiaryBlock.FACING));
    }

    private void buildStructureBlockList() {
        if (this.world != null) {
            MutableBoundingBox box = buildStructureBounds(this.getHorizontalOffset(), this.getVerticalOffset());
            structureBlocks.clear();
            BlockPos.stream(box).forEach((blockPos -> {
                if (blockPos.getX() == box.minX || blockPos.getX() == box.maxX ||
                        blockPos.getY() == box.minY || blockPos.getY() == box.maxY ||
                        blockPos.getZ() == box.minZ || blockPos.getZ() == box.maxZ) {
                    BlockPos savedPos = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    structureBlocks.add(savedPos);
                }
            }));
        }
    }

    public void runCreativeBuild(ServerPlayerEntity player) {
        if (this.world != null) {
            buildStructureBlockList();
            boolean addedStorage = false;
            for (BlockPos pos : structureBlocks) {
                Block block = this.world.getBlockState(pos).getBlock();
                if (!(block instanceof ApiaryBlock)) {
                    if (addedStorage) {
                        this.world.setBlockState(pos, net.minecraft.block.Blocks.GLASS.getDefaultState());
                    } else {
                        this.world.setBlockState(pos, ModBlocks.APIARY_STORAGE_BLOCK.get().getDefaultState());
                        addedStorage = true;
                    }
                }
            }
            runStructureValidation(player);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean linkStorageAndBreeder(TileEntity tile) {
        if (tile instanceof ApiaryStorageTileEntity && apiaryStorage == null && ((ApiaryStorageTileEntity) tile).getApiaryPos() == null) {
            apiaryStorage = (ApiaryStorageTileEntity) tile;
            setStoragePos(apiaryStorage.getPos());
            apiaryStorage.setApiaryPos(this.pos);
            if (world != null) {
                world.notifyBlockUpdate(getStoragePos(), apiaryStorage.getBlockState(), apiaryStorage.getBlockState(), 2);
            }
            return true;
        }

        if (tile instanceof ApiaryBreederTileEntity && apiaryBreeder == null && ((ApiaryBreederTileEntity) tile).getApiaryPos() == null) {
            apiaryBreeder = (ApiaryBreederTileEntity) tile;
            setBreederPos(apiaryBreeder.getPos());
            apiaryBreeder.setApiaryPos(this.pos);
            if (world != null) {
                world.notifyBlockUpdate(getBreederPos(), apiaryBreeder.getBlockState(), apiaryBreeder.getBlockState(), 2);
            }
            return true;
        }

        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean validateLinks() {
        boolean linksValid = true;
        if (apiaryStorage != null && (apiaryStorage.getApiaryPos() == null || positionsMismatch(apiaryStorage.getApiaryPos()))) {
            apiaryStorage = null;
            setStoragePos(null);
            linksValid = false;
        }
        if (apiaryBreeder != null && (apiaryBreeder.getApiaryPos() == null || positionsMismatch(apiaryBreeder.getApiaryPos()))) {
            apiaryBreeder = null;
            setBreederPos(null);
            linksValid = false;
        }
        if (!linksValid) {
            markDirty();
        }
        return linksValid;
    }

    private boolean positionsMismatch(BlockPos pos) {
        return pos.compareTo(this.pos) != 0;
    }
    //endregion

    //region SCREEN HANDLING
    @Nullable
    @Override
    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        if (world != null) {
            setNumPlayersUsing(getNumPlayersUsing() + 1);
            if (isValidApiary(true)) {
                return new ValidatedApiaryContainer(i, world, pos, playerInventory);
            }
            return new UnvalidatedApiaryContainer(i, world, pos, playerInventory);
        }
        return null;
    }

    @Override
    public void switchTab(ServerPlayerEntity player, ApiaryTabs tab) {
        if (world != null) {
            if (tab == ApiaryTabs.STORAGE) {
                NetworkHooks.openGui(player, getApiaryStorage(), getStoragePos());
            }
            if (tab == ApiaryTabs.BREED) {
                NetworkHooks.openGui(player, getApiaryBreeder(), getBreederPos());
            }
        }
    }

    //endregion

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? getLazyOptional().cast() :
                super.getCapability(cap, side);
    }

    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || slot == 0 || slot == 1;
    }

    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || slot == 1 || slot == 2;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.apiary");
    }

    public boolean isPreviewed() {
        return previewed;
    }

    public void setPreviewed(boolean previewed) {
        this.previewed = previewed;
    }

    public @NotNull TileStackHandler getTileStackHandler() {
        return tileStackHandler;
    }

    public LazyOptional<IItemHandler> getLazyOptional() {
        return lazyOptional;
    }

    public int getHorizontalOffset() {
        return horizontalOffset;
    }

    public void setHorizontalOffset(int horizontalOffset) {
        this.horizontalOffset = horizontalOffset;
    }

    public int getVerticalOffset() {
        return verticalOffset;
    }

    public void setVerticalOffset(int verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    public int getNumPlayersUsing() {
        return numPlayersUsing;
    }

    public void setNumPlayersUsing(int numPlayersUsing) {
        this.numPlayersUsing = numPlayersUsing;
    }

    public BlockPos getStoragePos() {
        return storagePos;
    }

    public void setStoragePos(BlockPos storagePos) {
        this.storagePos = storagePos;
    }

    public BlockPos getBreederPos() {
        return breederPos;
    }

    public void setBreederPos(BlockPos breederPos) {
        this.breederPos = breederPos;
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
        private int ticksInHive;
        private boolean isLocked = false;
        public final String beeColor;
        public final ITextComponent displayName;

        public ApiaryBee(CompoundNBT nbt, int ticksInHive, int minOccupationTicks, @Nullable BlockPos flowerPos, String beeType, String beeColor, ITextComponent displayName) {
            nbt.remove("UUID");
            this.entityData = nbt;
            this.setTicksInHive(ticksInHive);
            this.minOccupationTicks = minOccupationTicks;
            this.savedFlowerPos = flowerPos;
            this.beeType = beeType;
            this.beeColor = beeColor;
            this.displayName = displayName;
        }

        public int getTicksInHive() {
            return ticksInHive;
        }

        public void setTicksInHive(int ticksInHive) {
            this.ticksInHive = ticksInHive;
        }

        public boolean isLocked() {
            return isLocked;
        }

        public void setLocked(boolean locked) {
            isLocked = locked;
        }
    }

    public class TileStackHandler extends AutomationSensitiveItemStackHandler {

        protected TileStackHandler(int slots) {
            super(slots);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == IMPORT) {
                return stack.getItem() instanceof BeeJar && BeeJar.isFilled(stack);
            } else if (slot == EMPTY_JAR) {
                return stack.getItem() instanceof BeeJar && !BeeJar.isFilled(stack);
            } else {
                return false;
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == IMPORT) {
                return 1;
            }
            return super.getSlotLimit(slot);
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
