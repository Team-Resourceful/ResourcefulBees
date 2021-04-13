package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

import static com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils.TAG_RESOURCE_PATTERN;

public class BreedingSetup {

    private BreedingSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setupFeedItems() {
        BeeRegistry.getRegistry().getBees().values().forEach(BreedingSetup::setupFeedItems);
    }

    private static void setupFeedItems(CustomBeeData customBeeData) {
        String feedItem = customBeeData.getBreedData().getFeedItem();
        if (TAG_RESOURCE_PATTERN.matcher(feedItem).matches()){
            ITag<Item> itemTag = BeeInfoUtils.getItemTag(feedItem.replace(BeeConstants.TAG_PREFIX, ""));
            if (itemTag != null){ itemTag.getValues().forEach(item -> customBeeData.getBreedData().addFeedItem(item)); }
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
    }
}
