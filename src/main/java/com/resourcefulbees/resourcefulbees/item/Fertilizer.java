package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Fertilizer extends Item {

    //TODO - This system needs to be rewritten properly

    public Fertilizer(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, World world, @NotNull List list, @NotNull ITooltipFlag par4)
    {
        if(stack.getTag() == null || !stack.getTag().contains("specific")) {
            list.add(new TooltipBuilder().addTip("Unknown Type").build().get(0));
        } else {
            list.add(BeeInfoUtils.getItem(stack.getTag().getString("specific")).getName());
        }
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {

        World world = context.getWorld();
        BlockPos position = context.getPos();

        //TODO: Add Fertilizer range in config
        final int range = 4;

        if(!world.isRemote) {
            List<BlockPos> validCoords = new ArrayList<>();

            //Checks every block in range
            for (int x = -range - 1; x < range; x++) {
                for (int z = -range - 1; z < range; z++) {

                    //Shifts y a bit up and down
                    for (int y = -2; y <= 2; y++) {
                        BlockPos pos = position.add(x + 1, y + 1, z + 1);
                        BlockPos dirt = position.add(x + 1, y, z + 1);
                        if (world.isAirBlock(pos) && pos.getY() < 255
                                && (world.getBlockState(dirt).getMaterial() == Material.EARTH || world.getBlockState(dirt).getMaterial() == Material.ORGANIC)) {
                            validCoords.add(pos);
                        }
                    }
                }
            }

            int flowerCount = Math.min(validCoords.size(), 6);

            if(flowerCount > 0) {

                //Getting ItemStack to see what Type the fertilizer is.
                ItemStack itemStack = context.getItem();

                List<Item> flowers;

                if (itemStack.getTag() == null || !itemStack.getTag().contains("specific")) {
                    //Getting flowers
                    ResourceLocation tagId = new ResourceLocation("minecraft", "small_flowers");
                    List<Item> temp = ItemTags.getCollection().getTagOrEmpty(tagId).values();

                    //We need somehow to copy it to a new array list. If we remove this, it breaks *shrugs* // Actually it is because of the way java works. It copies the direct reference to the list and that is unchangeable
                    flowers = new ArrayList<>(temp);

                    flowers.remove(Items.WITHER_ROSE);
                } else {
                    flowers = new ArrayList<>();

                    String flower = itemStack.getTag().getString("specific");
                    flowers.add(BeeInfoUtils.getItem(flower));

                }

                for (int i = 0; i < flowerCount; i++) {

                    BlockPos coords = validCoords.get(world.rand.nextInt(validCoords.size()));
                    validCoords.remove(coords);

                    //Getting random flower from the small_flowers tag
                    world.setBlockState(coords, Block.getBlockFromItem(flowers.get(world.rand.nextInt(flowers.size()))).getDefaultState());
                }

                context.getItem().shrink(1);
            }
        }

        return ActionResultType.SUCCESS;
    }

}
