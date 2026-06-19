package com.teamresourceful.resourcefulbees.common.blockentities;

import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.util.EntityUtils;
import com.teamresourceful.resourcefullib.common.nbt.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class BeeBoxBlockEntity extends BlockEntity {

    private List<CompoundTag> bees;
    private List<StringTag> displayNames;

    public BeeBoxBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntityTypes.BEE_BOX_ENTITY.get(), pWorldPosition, pBlockState);
    }

    //region NBT
//    @Override
//    protected void saveAdditional(@NonNull ValueOutput valueOutput) {
//        super.saveAdditional(valueOutput);
//        valueOutput.pu.put(NBTConstants.NBT_BEES, TagUtils.toListTag(bees));
//        valueOutput.put(NBTConstants.NBT_DISPLAYNAMES, TagUtils.toListTag(displayNames));
//    }
//
//    @Override
//    public void loadAdditional(@NotNull ValueInput input) {
//        super.loadAdditional(input);
//        this.bees = TagUtils.fromListTag(tag.getList(NBTConstants.NBT_BEES, Tag.TAG_COMPOUND), CompoundTag.class);
//        this.displayNames = TagUtils.fromListTag(tag.getList(NBTConstants.NBT_DISPLAYNAMES, Tag.TAG_STRING), StringTag.class);
//    }
    //endregion

    public void summonBees(Level level, BlockPos pos, Player player) {
        if (this.bees != null) {
            if (level.isClientSide()) return;
            this.bees.forEach(bee -> EntityUtils.summonEntity(bee, level, player, pos));
        }
    }

    public boolean hasBees() {
        return this.bees != null && !this.bees.isEmpty();
    }


}
