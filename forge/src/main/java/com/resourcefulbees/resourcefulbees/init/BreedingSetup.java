package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils.TAG_RESOURCE_PATTERN;

public class BreedingSetup {

    private BreedingSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static void setupFeedItems() {
        BeeRegistry.getRegistry().getBees().values().forEach(BreedingSetup::setupFeedItems);
    }

    private static void setupFeedItems(CustomBeeData customBeeData) {
        String feedItem = customBeeData.getBreedData().getFeedItem();
        if (TAG_RESOURCE_PATTERN.matcher(feedItem).matches()){
            Tag<Item> itemTag = BeeInfoUtils.getItemTag(feedItem.replace(BeeConstants.TAG_PREFIX, ""));
            if (itemTag != null){
                itemTag.getValues().stream().filter(item -> item != Items.AIR)
                    .forEach(item -> customBeeData.getBreedData().addFeedItem(item));
            }else {
                LOGGER.warn("Could not find Tag: {}, for bee: {}", feedItem, customBeeData.getName());
            }
        }else {
            switch (feedItem){
                case BeeConstants.FLOWER_TAG_ALL:
                    ItemTags.FLOWERS.getValues().forEach(item -> customBeeData.getBreedData().addFeedItem(item));
                    break;
                case BeeConstants.FLOWER_TAG_SMALL:
                    ItemTags.SMALL_FLOWERS.getValues().forEach(item -> customBeeData.getBreedData().addFeedItem(item));
                    break;
                case BeeConstants.FLOWER_TAG_TALL:
                    ItemTags.TALL_FLOWERS.getValues().forEach(item -> customBeeData.getBreedData().addFeedItem(item));
                    break;
                default:
                    Item item = BeeInfoUtils.getItem(feedItem);
                    if (BeeInfoUtils.isValidItem(item)){
                        customBeeData.getBreedData().addFeedItem(item);
                    }
            }
        }
        if (!customBeeData.getBreedData().hasFeedItems()){
            customBeeData.getBreedData().setBreedable(false);
            LOGGER.warn("Could not find any valid feed items for {}", customBeeData.getName());
        }
    }
}
