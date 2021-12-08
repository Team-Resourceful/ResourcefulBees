package com.teamresourceful.resourcefulbees.api.honeycombdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Unmodifiable
public class OutputVariation {

    public static final List<Integer> DEFAULT_APIARY_AMOUNTS = Arrays.asList(CommonConfig.T1_APIARY_QUANTITY.get(), CommonConfig.T2_APIARY_QUANTITY.get(), CommonConfig.T3_APIARY_QUANTITY.get(), CommonConfig.T4_APIARY_QUANTITY.get());
    public static final List<ApiaryOutputType> DEFAULT_APIARY_OUTPUT_TYPES = Arrays.asList(CommonConfig.T1_APIARY_OUTPUT.get(), CommonConfig.T2_APIARY_OUTPUT.get(), CommonConfig.T3_APIARY_OUTPUT.get(), CommonConfig.T4_APIARY_OUTPUT.get());
    private static final String MISSING_ID = "Identifier is REQUIRED!";
    private static final String MISSING_APIARY_COMB = "Default comb must be present when list is empty and config contains combs!!!";
    private static final String MISSING_APIARY_BLOCK = "Default block must be present when list is empty and config contains blocks!!!";
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_COMB = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.COMB);
    private static final boolean DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK = DEFAULT_APIARY_OUTPUT_TYPES.contains(ApiaryOutputType.BLOCK);


    public static final Codec<OutputVariation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("identifier").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error(MISSING_ID), null).forGetter(OutputVariation::getIdentifier),
            CodecUtils.ITEM_STACK_CODEC.listOf().fieldOf("hiveCombs").orElseGet(ArrayList::new).forGetter(OutputVariation::getHiveCombs),
            CodecUtils.ITEM_STACK_CODEC.listOf().fieldOf("apiaryCombs").orElseGet(ArrayList::new).forGetter(OutputVariation::getApiaryCombs),
            CodecUtils.ITEM_STACK_CODEC.optionalFieldOf("defaultComb").forGetter(OutputVariation::getDefaultComb),
            CodecUtils.ITEM_STACK_CODEC.optionalFieldOf("defaultCombBlock").forGetter(OutputVariation::getDefaultCombBlock)
    ).apply(instance, OutputVariation::new));

    private final String identifier;
    private final List<ItemStack> hiveCombs;
    private final List<ItemStack> apiaryCombs;
    private final Optional<ItemStack> defaultComb;
    private final Optional<ItemStack> defaultCombBlock;

    private OutputVariation(String identifier, List<ItemStack> hiveCombs, List<ItemStack> apiaryCombs, Optional<ItemStack> defaultComb, Optional<ItemStack> defaultCombBlock) {
        this.identifier = identifier;
        this.hiveCombs = new ArrayList<>(hiveCombs);
        this.apiaryCombs = new ArrayList<>(apiaryCombs);
        this.defaultComb = defaultComb;
        this.defaultComb.ifPresent(comb -> comb.setCount(1));
        this.defaultCombBlock = defaultCombBlock;
        this.defaultCombBlock.ifPresent(block -> block.setCount(1));
        fixHiveCombs();
        fixApiaryCombs();
    }

    public String getIdentifier() {
        return identifier;
    }

    private List<ItemStack> getHiveCombs() {
        return hiveCombs;
    }

    private List<ItemStack> getApiaryCombs() {
        return apiaryCombs;
    }

    private Optional<ItemStack> getDefaultComb() {
        return defaultComb;
    }

    private Optional<ItemStack> getDefaultCombBlock() {
        return defaultCombBlock;
    }

    public ItemStack getHiveOutput(int tier) {
        return getHiveCombs().get(tier).copy();
    }

    public ItemStack getApiaryOutput(int tier) {
        return getApiaryCombs().get(tier).copy();
    }

    /**
     * When the {@link #hiveCombs} list is less than 5 the list will be expanded to a size of 5 using the last entry in the list.
     * When the {@link #hiveCombs} list is empty, then a list will be created using the default comb. The {@link #defaultComb} <b>MUST</b>
     * be provided for the list to be created!
     * */
    private void fixHiveCombs() {
        do {
            if (hiveCombs.isEmpty() && defaultComb.isPresent()) {
                hiveCombs.add(defaultComb.get().copy());
            } else if (hiveCombs.isEmpty()) {
                throw new IllegalArgumentException("HiveCombs list can't be empty without a default comb supplied!!");
            } else {
                hiveCombs.add(hiveCombs.get(hiveCombs.size() -1));
            }
        } while (hiveCombs.size() < 6);
    }

    /**
     * When the {@link #apiaryCombs} list is less than 4 the list will be expanded to a size of 4 using the last entry in the list.
     * When the {@link #apiaryCombs} list is empty then a new list will be created based on the values specified in the config.
     * <b>BOTH</b> a {@link #defaultComb} and a {@link #defaultCombBlock} <b>MUST</b> be supplied for the list when they are specified in the config!
     * */
    private void fixApiaryCombs() {
        if (apiaryCombs.isEmpty()) {
            checkDefaultsAreOK();
            for (int i = 0; i < 4; i++) {
                ItemStack stack = DEFAULT_APIARY_OUTPUT_TYPES.get(i).equals(ApiaryOutputType.COMB) ? defaultComb.get() : defaultCombBlock.get();
                stack = stack.copy();
                stack.setCount(DEFAULT_APIARY_AMOUNTS.get(i));
                apiaryCombs.add(stack);
            }
        } else {
            while (apiaryCombs.size() < 4) {
                apiaryCombs.add(apiaryCombs.get(apiaryCombs.size() -1));
            }
        }
    }

    private void checkDefaultsAreOK() {
        if (DEFAULT_OUTPUT_TYPE_INCLUDES_COMB && !defaultComb.isPresent()) {
            throw new IllegalArgumentException(MISSING_APIARY_COMB);
        } else if (DEFAULT_OUTPUT_TYPE_INCLUDES_BLOCK && !defaultCombBlock.isPresent()) {
            throw new IllegalArgumentException(MISSING_APIARY_BLOCK);
        }
    }
}
