package com.resourcefulbees.resourcefulbees.entity;

import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityEventHandlers {

    public static void entityDies(LivingDeathEvent event) {
        //TODO: move this to it's own class in the entity package - epic
        //TODO: enable WIP Pollen Feature.

        if (event.getEntity() instanceof ResourcefulBee) {
            ResourcefulBee bee = (ResourcefulBee) event.getEntity();

            if (bee.hasNectar()) {

                World world = event.getEntity().world;
                ItemStack item = new ItemStack(ModItems.POLLEN.get());

                int count = Math.round((world.rand.nextFloat() + 1) * 2.5f);
                CompoundNBT beeNBT = new CompoundNBT();
                bee.writeUnlessPassenger(beeNBT);
                BlockPos flower = bee.getLastFlower();

                System.out.println(flower.getX() + " " + flower.getY() + " " + flower.getZ());

                CompoundNBT compoundNBT = new CompoundNBT();
                BlockState flowerState = world.getBlockState(flower);

                compoundNBT.putString("specific", ForgeRegistries.BLOCKS.getKey(flowerState.getBlock()).toString());

                item.setTag(compoundNBT);

                item.setCount(count);

                ItemEntity entityItem = new ItemEntity(world,
                        bee.getX(), bee.getY(), bee.getZ(),
                        item.copy());

                //To give the item the little and neat drop motion ;)
                entityItem.setMotion(
                        world.rand.nextGaussian() * 0.08F,
                        world.rand.nextGaussian() * 0.08F + 0.2F,
                        world.rand.nextGaussian() * 0.08F);


                world.addEntity(entityItem);
            }
        }

    }

}
