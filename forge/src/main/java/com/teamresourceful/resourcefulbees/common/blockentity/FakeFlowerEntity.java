package com.teamresourceful.resourcefulbees.common.blockentity;

import com.teamresourceful.resourcefulbees.common.entity.passive.ResourcefulBee;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes.FAKE_FLOWER_ENTITY;

public class FakeFlowerEntity extends BlockEntity implements Container {

    public FakeFlowerEntity(BlockPos pos, BlockState state) {
        super(FAKE_FLOWER_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int slot) {
        return null;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return null;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {

    }

    public void sendMutations(ResourcefulBee resourcefulBee) {

    }
}
