package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.TooltipBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
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
    public void appendHoverText(ItemStack stack, Level world, @NotNull List list, @NotNull TooltipFlag par4)
    {
        if(stack.getTag() == null || !stack.getTag().contains("specific")) {
            list.add(new TooltipBuilder().addTip("Unknown Type").build().get(0));
        } else {
            list.add(BeeInfoUtils.getItem(stack.getTag().getString("specific")).getDescription());
        }
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level world = context.getLevel();
        BlockPos position = context.getClickedPos();

        //TODO: Add Fertilizer range in config
        final int range = 4;

        if(!world.isClientSide) {
            List<BlockPos> validCoords = new ArrayList<>();

            //Checks every block in range
            for (int x = -range - 1; x < range; x++) {
                for (int z = -range - 1; z < range; z++) {

                    //Shifts y a bit up and down
                    for (int y = -2; y <= 2; y++) {
                        BlockPos pos = position.offset(x + 1, y + 1, z + 1);
                        BlockPos dirt = position.offset(x + 1, y, z + 1);
                        if (world.isEmptyBlock(pos) && pos.getY() < 255
                                && (world.getBlockState(dirt).getMaterial() == Material.DIRT || world.getBlockState(dirt).getMaterial() == Material.GRASS)) {
                            validCoords.add(pos);
                        }
                    }
                }
            }

            int flowerCount = Math.min(validCoords.size(), 6);

            if(flowerCount > 0) {

                //Getting ItemStack to see what Type the fertilizer is.
                ItemStack itemStack = context.getItemInHand();

                List<Item> flowers;

                if (itemStack.getTag() == null || !itemStack.getTag().contains("specific")) {
                    //Getting flowers
                    ResourceLocation tagId = new ResourceLocation("minecraft", "small_flowers");
                    List<Item> temp = ItemTags.getAllTags().getTagOrEmpty(tagId).getValues();

                    //We need somehow to copy it to a new array list. If we remove this, it breaks *shrugs* // Actually it is because of the way java works. It copies the direct reference to the list and that is unchangeable
                    flowers = new ArrayList<>(temp);

                    flowers.remove(Items.WITHER_ROSE);
                } else {
                    flowers = new ArrayList<>();

                    String flower = itemStack.getTag().getString("specific");
                    flowers.add(BeeInfoUtils.getItem(flower));

                }

                for (int i = 0; i < flowerCount; i++) {

                    BlockPos coords = validCoords.get(world.random.nextInt(validCoords.size()));
                    validCoords.remove(coords);

                    //Getting random flower from the small_flowers tag
                    world.setBlockAndUpdate(coords, Block.byItem(flowers.get(world.random.nextInt(flowers.size()))).defaultBlockState());
                }

                context.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

}
