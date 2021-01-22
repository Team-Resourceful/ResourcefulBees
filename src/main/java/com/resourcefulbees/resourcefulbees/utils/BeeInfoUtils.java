package com.resourcefulbees.resourcefulbees.utils;

import com.google.common.base.Splitter;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.*;

public class BeeInfoUtils {

    public static void buildFamilyTree(CustomBeeData bee) {
        if (bee.getBreedData().hasParents()) {
            Iterator<String> parent1 = Splitter.on(",").trimResults().split(bee.getBreedData().getParent1()).iterator();
            Iterator<String> parent2 = Splitter.on(",").trimResults().split(bee.getBreedData().getParent2()).iterator();

            while (parent1.hasNext() && parent2.hasNext()) {
                String p1 = parent1.next();
                String p2 = parent2.next();
                BeeRegistry.getRegistry().FAMILY_TREE.computeIfAbsent(sortParents(p1, p2), k -> new RandomCollection<>()).add(bee.getBreedData().getBreedWeight(), bee);
            }
        }

        BeeRegistry.getRegistry().FAMILY_TREE.computeIfAbsent(Pair.of(bee.getName(), bee.getName()), k -> new RandomCollection<>()).add(bee.getBreedData().getBreedWeight(), bee);
    }

    public static Pair<String, String> sortParents(String parent1, String parent2) {
        return parent1.compareTo(parent2) > 0 ? Pair.of(parent1, parent2) : Pair.of(parent2, parent1);
    }

    public static void parseBiomes(CustomBeeData bee) {
        if (!bee.getSpawnData().getBiomeWhitelist().isEmpty()) {
            Set<ResourceLocation> whitelist = new HashSet<>(getBiomeSet(bee.getSpawnData().getBiomeWhitelist()));
            Set<ResourceLocation> blacklist = new HashSet<>();
            if (!bee.getSpawnData().getBiomeBlacklist().isEmpty())
                blacklist = getBiomeSet(bee.getSpawnData().getBiomeBlacklist());
            updateSpawnableBiomes(whitelist, blacklist,bee);
        }
    }

    private static Set<ResourceLocation> getBiomeSet(String list) {
        Set<ResourceLocation> set = new HashSet<>();
        if (list.contains(BeeConstants.TAG_PREFIX))
            set.addAll(parseBiomeListFromTag(list));
        else
            set.addAll(parseBiomeList(list));
        return set;
    }

    private static Set<ResourceLocation> parseBiomeListFromTag(String list) {
        Set<ResourceLocation> biomeSet = new HashSet<>();
        if (Config.USE_FORGE_DICTIONARIES.get()) {
            Splitter.on(",").trimResults().split(list.replace(BeeConstants.TAG_PREFIX, ""))
                    .forEach(s -> BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(s))
                            .forEach(biomeRegistryKey -> biomeSet.add(biomeRegistryKey.getValue())));
        } else {
            Splitter.on(",").trimResults().split(list.replace(BeeConstants.TAG_PREFIX,"")).forEach(s -> {
                if (com.resourcefulbees.resourcefulbees.registry.BiomeDictionary.TYPES.containsKey(s)) {
                    biomeSet.addAll(com.resourcefulbees.resourcefulbees.registry.BiomeDictionary.TYPES.get(s));
                }
            });
        }

        return biomeSet;
    }

    private static Set<ResourceLocation> parseBiomeList(String list) {
        Set<ResourceLocation> biomeSet = new HashSet<>();
        Splitter.on(',').trimResults().split(list).forEach(s -> biomeSet.add(new ResourceLocation(s)));

        return biomeSet;
    }

    private static void updateSpawnableBiomes(Set<ResourceLocation> whitelist, Set<ResourceLocation> blacklist, CustomBeeData bee){
        whitelist.stream()
                .filter(resourceLocation -> !blacklist.contains(resourceLocation))
                .forEach(resourceLocation -> BeeRegistry.SPAWNABLE_BIOMES.computeIfAbsent(resourceLocation, k -> new RandomCollection<>()).add(bee.getSpawnData().getSpawnWeight(), bee));
    }

    /**
     * Returns new Resource Location with given input.
     *
     * @param resource Resource input as String in the form of "mod_id:item_or_block_id".
     * @return Returns New Resource Location for given input.
     */
    public static ResourceLocation getResource(String resource){ return new ResourceLocation(resource); }
    
    public static boolean isValidBlock(Block block){ return block != null && block != Blocks.AIR; }

    public static boolean isValidFluid(Fluid fluid){ return fluid != null && fluid != Fluids.EMPTY; }

    public static boolean isValidItem(Item item){ return item != null && item != Items.AIR; }

    public static boolean isValidEntityType(EntityType<?> entityType){ return entityType != null; }

    public static Item getItem(String itemName) { return ForgeRegistries.ITEMS.getValue(getResource(itemName)); }

    public static Block getBlock(String blockName) { return ForgeRegistries.BLOCKS.getValue(getResource(blockName)); }

    public static Fluid getFluid(String fluidName) { return ForgeRegistries.FLUIDS.getValue(getResource(fluidName)); }

    public static Biome getBiome(String biomeName) { return ForgeRegistries.BIOMES.getValue(getResource(biomeName)); }

    public static EntityType<?> getEntityType(String entityName) { return ForgeRegistries.ENTITIES.getValue(getResource(entityName)); }

    public static ITag<Item> getItemTag(String itemTag) { return ItemTags.getCollection().get(getResource(itemTag)); }

    public static ITag<Fluid> getFluidTag(String fluidTag) { return FluidTags.func_226157_a_().get(getResource(fluidTag));  }

    public static ITag<Block> getBlockTag(String blockTag) { return BlockTags.getCollection().get(getResource(blockTag)); }

    public static ITag<Block> getValidApiaryTag() { return BlockTags.getCollection().get(VALID_APIARY); }

    public static void makeValidApiaryTag() { BlockTags.makeWrapperTag("resourcefulbees:valid_apiary"); }

    private static final ResourceLocation VALID_APIARY = new ResourceLocation("resourcefulbees:valid_apiary");

    public static boolean isTag(String input) {
        if (ValidatorUtils.TAG_RESOURCE_PATTERN.matcher(input).matches()) {
            return true;
        } else if (input.equals(FLOWER_TAG_TALL)) {
            return true;
        } else if (input.equals(FLOWER_TAG_SMALL)) {
            return true;
        } else return input.equals(FLOWER_TAG_ALL);
    }

    public static boolean isValidBreedItem(@Nonnull ItemStack stack, String validBreedItem) {
        if (ValidatorUtils.TAG_RESOURCE_PATTERN.matcher(validBreedItem).matches()) {
            ITag<Item> itemTag = getItemTag(validBreedItem.replace(TAG_PREFIX, ""));
            return itemTag != null && stack.getItem().isIn(itemTag);
        } else {
            switch (validBreedItem) {
                case FLOWER_TAG_ALL:
                    return stack.getItem().isIn(ItemTags.FLOWERS);
                case FLOWER_TAG_SMALL:
                    return stack.getItem().isIn(ItemTags.SMALL_FLOWERS);
                case FLOWER_TAG_TALL:
                    return stack.getItem().isIn(ItemTags.TALL_FLOWERS);
                default:
                    return stack.getItem().equals(getItem(validBreedItem));
            }
        }
    }


    public static void flagBeesInRange(BlockPos pos, World world) {
        MutableBoundingBox box = MutableBoundingBox.createProper(pos.getX() + 10, pos.getY() + 10, pos.getZ() + 10, pos.getX() - 10, pos.getY() - 10, pos.getZ() - 10);
        AxisAlignedBB aabb = AxisAlignedBB.func_216363_a(box);
        assert world != null;
        List<CustomBeeEntity> list = world.getEntitiesWithinAABB(CustomBeeEntity.class, aabb);
        list.forEach(customBeeEntity -> customBeeEntity.setHasHiveInRange(true));
    }
}
