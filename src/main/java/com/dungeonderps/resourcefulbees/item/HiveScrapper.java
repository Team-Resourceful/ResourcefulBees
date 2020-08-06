package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.block.TieredBeehiveBlock;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.registry.ItemGroupResourcefulBees;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class HiveScrapper extends Item {
    public HiveScrapper() {
        super(new Properties().maxStackSize(1).maxDamage(25).group(ItemGroupResourcefulBees.RESOURCEFUL_BEES));
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) { return Config.SCRAPER_TIME_TO_USE.get() * 20; }

    @Nonnull
    @Override
    public UseAction getUseAction(@Nonnull ItemStack stack) { return UseAction.BOW; }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        BlockRayTraceResult rayTraceResult = rayTrace(world, player, RayTraceContext.FluidMode.NONE);
        ItemStack heldItem = player.getHeldItem(hand);

        //noinspection ConstantConditions
        if (rayTraceResult != null && rayTraceResult.getType() == RayTraceResult.Type.BLOCK && world.getBlockState(rayTraceResult.getPos()).getBlock() instanceof TieredBeehiveBlock) {
            player.setActiveHand(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
        } else {
            return new ActionResult<>(ActionResultType.FAIL, heldItem);
        }
    }
    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull LivingEntity entityLiving) {
        if (!(entityLiving instanceof PlayerEntity)) {
            return stack;
        }
        PlayerEntity player = (PlayerEntity) entityLiving;
        BlockRayTraceResult rayTraceResult = rayTrace(world, player, RayTraceContext.FluidMode.NONE);
        boolean angerBees = false;

        //noinspection ConstantConditions
        if (rayTraceResult == null || rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            player.stopActiveHand();
            return stack;
        }
        BlockPos pos = rayTraceResult.getPos();
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (block instanceof TieredBeehiveBlock) {
            TieredBeehiveBlock hive = (TieredBeehiveBlock)block;
            int honeyLevel = blockState.get(BeehiveBlock.HONEY_LEVEL);
            if (!world.isRemote) {
                if (honeyLevel >= 5){
                    hive.dropResourceHoneycomb(world, pos);
                    stack.damageItem(1, player, player1 -> player1.sendBreakAnimation(entityLiving.getActiveHand()));
                    angerBees = true;
                }
            }
            if (angerBees) {
                if (hive.isHiveSmoked(pos,world) || CampfireBlock.isSmokingBlockAt(world, pos)) {
                    hive.takeHoney(world, blockState, pos);
                }
                else {
                    if (hive.hasBees(world, pos)) {
                        hive.angerNearbyBees(world, pos);
                    }

                    hive.takeHoney(world, blockState, pos, player, BeehiveTileEntity.State.EMERGENCY);
                }
            }
        }

        player.getCooldownTracker().setCooldown(this, Config.SCRAPER_COOLDOWN.get() * 20);
        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (!(player instanceof PlayerEntity)) {
            return;
        }

        World world = player.world;
        BlockRayTraceResult rayTraceResult = rayTrace(world, (PlayerEntity) player, RayTraceContext.FluidMode.NONE);

        //noinspection ConstantConditions
        if (rayTraceResult == null || rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
            player.stopActiveHand();
        }
        //noinspection ConstantConditions
        if (rayTraceResult != null && rayTraceResult.getType() == RayTraceResult.Type.BLOCK && !(world.getBlockState(rayTraceResult.getPos()).getBlock() instanceof TieredBeehiveBlock)) {
            player.stopActiveHand();
        }
    }
}
