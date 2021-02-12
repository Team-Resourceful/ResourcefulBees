package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BeeBox extends Item {

    public BeeBox(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            World playerWorld = context.getPlayer().getEntityWorld();
            ItemStack stack = context.getItem();
            if (playerWorld.isRemote() || !isFilled(stack)) return ActionResultType.FAIL;
            World worldIn = context.getWorld();
            BlockPos pos = context.getPos();
            List<Entity> entities = getEntitiesFromStack(stack, worldIn, true);
            for (Entity entity : entities) {
                if (entity != null) {
                    BlockPos blockPos = pos.offset(context.getFace());
                    entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
                    worldIn.addEntity(entity);
                    if (entity instanceof BeeEntity) {
                        BeeEntity beeEntity = (BeeEntity) entity;
                        beeEntity.hivePos = null;
                        beeEntity.flowerPos = null;
                    }
                }
            }
            stack.shrink(1);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    @Nullable
    public List<Entity> getEntitiesFromStack(ItemStack stack, World world, boolean withInfo) {
        CompoundNBT tag = stack.getTag();
        List<Entity> entities = new ArrayList<>();
        if (tag != null) {
            ListNBT bees = tag.getList(NBTConstants.NBT_BEES, 10);
            List<CompoundNBT> beeNbt = bees.stream().map(b -> (CompoundNBT) b).collect(Collectors.toList());
            for (CompoundNBT bee : beeNbt) {
                EntityType<?> type = EntityType.byKey(bee.getCompound("EntityData").getString("id")).orElse(null);
                if (type != null) {
                    Entity entity = type.create(world);
                    if (entity != null && withInfo) entity.read(stack.getTag());
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    public static boolean isFilled(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().contains(NBTConstants.NBT_BEES);
    }
}
