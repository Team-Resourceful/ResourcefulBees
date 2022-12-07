package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import com.teamresourceful.resourcefullib.common.utils.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BeeBoxBlockEntity extends BlockEntity {

    private List<CompoundTag> bees;
    private List<StringTag> displayNames;

    public BeeBoxBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntityTypes.BEE_BOX_ENTITY.get(), pWorldPosition, pBlockState);
    }

    //region NBT
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(NBTConstants.NBT_BEES, TagUtils.toListTag(bees));
        tag.put(NBTConstants.NBT_DISPLAYNAMES, TagUtils.toListTag(displayNames));
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.bees = TagUtils.fromListTag(tag.getList(NBTConstants.NBT_BEES, Tag.TAG_COMPOUND), CompoundTag.class);
        this.displayNames = TagUtils.fromListTag(tag.getList(NBTConstants.NBT_DISPLAYNAMES, Tag.TAG_STRING), StringTag.class);
    }
    //endregion

    public void summonBees(Level level, BlockPos pos, Player player) {
        if (this.bees != null) {
            if (level.isClientSide()) return;
            this.bees.forEach(bee -> ModUtils.summonEntity(bee, level, player, pos));
        }
    }

    public boolean hasBees() {
        return this.bees != null && !this.bees.isEmpty();
    }


}
