package com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary;


import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.inventory.containers.ValidatedApiaryContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTier;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BeehiveEntityAccessor;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.tileentity.ISyncableGUI;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;

public class ApiaryTileEntity extends BlockEntity implements MenuProvider, ISyncableGUI {

    public final List<ApiaryBee> bees = new ArrayList<>();
    protected ApiaryTier tier;
    protected int ticksSinceBeesFlagged;

    public ApiaryTileEntity(ApiaryTier tier, BlockPos pos, BlockState state) {
        super(tier.getBlockEntityType(), pos, state);
        this.tier = tier;
    }

    public ApiaryTier getTier() {
        return tier;
    }

    public int getBeeCount() {
        return this.bees.size();
    }

    //region BEE HANDLING
    public boolean releaseBee(@NotNull BlockState state, ApiaryBee apiaryBee) {
        BlockPos blockPos = this.getBlockPos();
        Direction direction = state.getValue(BeehiveBlock.FACING);
        BlockPos blockPos1 = blockPos.relative(direction);
        CompoundTag nbt = apiaryBee.entityData;

        if (level != null && this.level.getBlockState(blockPos1).getCollisionShape(this.level, blockPos1).isEmpty()) {
            if (EntityType.loadEntityRecursive(nbt, this.level, entity1 -> entity1) instanceof Bee bee) {
                BeeInfoUtils.setEntityLocationAndAngle(blockPos, direction, bee);
                deliverNectar(nbt, bee);
                this.ageBee(apiaryBee.getTicksInHive(), bee);
                releaseBee(bee, this.level);
            }
            return true;
        }
        return false;
    }

    private void releaseBee(Bee vanillaBeeEntity, Level level) {
        BlockPos hivePos = this.getBlockPos();
        level.playSound(null, hivePos.getX(), hivePos.getY(), hivePos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.addFreshEntity(vanillaBeeEntity);
    }

    private void deliverNectar(CompoundTag nbt, Bee vanillaBeeEntity) {
        if (nbt.getBoolean("HasNectar")) {
            vanillaBeeEntity.dropOffNectar();
            //TODO DEPOSIT COMB
        }
    }

    private void ageBee(int ticksInHive, Bee beeEntity) {
        BeeInfoUtils.ageBee(ticksInHive, beeEntity);
    }

    public void tryEnterHive(@NotNull Entity bee, boolean hasNectar, int ticksInHive) {
        if (this.level != null && hasSpace() && bee instanceof Bee) {
            bee.ejectPassengers();
            CompoundTag nbt = new CompoundTag();
            bee.save(nbt);
            this.bees.add(new ApiaryBee(nbt, ticksInHive, hasNectar ? getMaxTimeInHive(bee) : MIN_HIVE_TIME, bee.getName()));
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            bee.discard();
        }
    }

    private int getMaxTimeInHive(@NotNull Entity bee) {
        return getMaxTimeInHive(bee instanceof ICustomBee ? ((ICustomBee) bee).getCoreData().getMaxTimeInHive() : BeeConstants.MAX_TIME_IN_HIVE);
    }

    private int getMaxTimeInHive(int timeInput) {
        return (int) (timeInput * tier.getTimeModifier());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ApiaryTileEntity apiaryTile) {
        ApiaryBee apiaryBee;
        for (Iterator<ApiaryBee> iterator = apiaryTile.bees.iterator(); iterator.hasNext();) {
            apiaryBee = iterator.next();
            if (apiaryTile.canRelease(apiaryBee) && apiaryTile.releaseBee(state, apiaryBee)) iterator.remove();
            else apiaryBee.setTicksInHive(Math.min(apiaryBee.getTicksInHive() + 1, Integer.MAX_VALUE - 1));
        }

        if (apiaryTile.bees.size() > 0 && level.getRandom().nextDouble() < 0.005D) {
            level.playSound(null, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        apiaryTile.ticksSinceBeesFlagged++;
        if (apiaryTile.ticksSinceBeesFlagged == 80) {
            BeeInfoUtils.flagBeesInRange(pos, level);
            apiaryTile.ticksSinceBeesFlagged = 0;
        }
    }

    private boolean canRelease(ApiaryBee apiaryBee) {
        return !apiaryBee.isLocked() && apiaryBee.getTicksInHive() > apiaryBee.minOccupationTicks;
    }

    public boolean hasSpace() {
        return this.bees.size() < tier.getMaxBees();
    }

    public boolean isAllowedBee() {
        return getBlockState().getBlock() instanceof ApiaryBlock;
    }

    public void lockOrUnlockBee(int bee) {
        if (bee < bees.size() && bee >= 0) this.bees.get(bee).toggleLocked();
    }
    //endregion

    //region NBT HANDLING

    @NotNull
    public ListTag writeBees() {
        ListTag listTag = new ListTag();
        this.bees.forEach(apiaryBee -> {
            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.put("EntityData", apiaryBee.entityData);
            compoundnbt.putInt("TicksInHive", apiaryBee.getTicksInHive());
            compoundnbt.putInt("MinOccupationTicks", apiaryBee.minOccupationTicks);
            compoundnbt.putBoolean(NBTConstants.NBT_LOCKED, apiaryBee.isLocked());
            compoundnbt.putString(NBTConstants.NBT_BEE_NAME, Component.Serializer.toJson(apiaryBee.displayName));
            listTag.add(compoundnbt);
        });
        return listTag;
    }

    public void loadBees(CompoundTag nbt) {
        ListTag listTag = nbt.getList(NBTConstants.NBT_BEES, 10);

        if (!listTag.isEmpty()) {
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag data = listTag.getCompound(i);
                Component displayName = data.contains(NBTConstants.NBT_BEE_NAME) ? Component.Serializer.fromJson(data.getString(NBTConstants.NBT_BEE_NAME)) : new TextComponent("Temp Bee Name");
                this.bees.add(new ApiaryBee(data.getCompound("EntityData"), data.getInt("TicksInHive"), data.getInt("MinOccupationTicks"), displayName, data.getBoolean(NBTConstants.NBT_LOCKED)));
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        loadBees(nbt);
        bees.removeIf(bee -> BeeInfoUtils.getEntityType(bee.entityData.getString("id")) == EntityType.PIG);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.NBT_BEES, this.writeBees());
    }

    //endregion

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return /*cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? getLazyOptional().cast() :*/
                super.getCapability(cap, side);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TranslationConstants.Guis.APIARY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return level == null ? null : new ValidatedApiaryContainer(id, level, worldPosition, inventory);
    }

    public void sendData(ServerPlayer player) {
        if (!(player instanceof FakePlayer)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            CompoundTag tag = new CompoundTag();
            tag.put(NBTConstants.NBT_BEES, writeBees());
            buffer.writeNbt(tag);
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), player);
        }
    }

    @Override
    public void sendGUINetworkPacket(ContainerListener player) {
    }

    @Override
    public void handleGUINetworkPacket(FriendlyByteBuf buffer) {
        CompoundTag nbt = buffer.readNbt();
        if (nbt != null) {
            bees.clear();
            loadBees(nbt);
        }
    }

    public static class ApiaryBee {
        public final Component displayName;
        public final int minOccupationTicks;
        public final CompoundTag entityData;

        private boolean locked;
        private int ticksInHive;

        public ApiaryBee(CompoundTag entityData, int ticksInHive, int minOccupationTicks, Component displayName, boolean locked) {
            this(entityData, ticksInHive, minOccupationTicks, displayName);
            this.locked = locked;
        }

        public ApiaryBee(CompoundTag entityData, int ticksInHive, int minOccupationTicks, Component displayName) {
            BeehiveEntityAccessor.callRemoveIgnoredBeeTags(entityData);
            this.entityData = entityData;
            this.ticksInHive = ticksInHive;
            this.minOccupationTicks = minOccupationTicks;
            this.displayName = displayName;
        }

        public boolean isLocked() {
            return locked;
        }

        public void toggleLocked() {
            setLocked(!locked);
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public int getTicksInHive() {
            return ticksInHive;
        }

        public void setTicksInHive(int ticksInHive) {
            this.ticksInHive = ticksInHive;
        }
    }
}
