package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryOutputType;
import com.teamresourceful.resourcefulbees.common.lib.enums.HoneycombType;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;

@Unmodifiable
public class HoneycombData {
    //TODO REMOVE THIS CLASS AFTER BEEPEDIA IS FIXED!!

    public static final List<Integer> DEFAULT_APIARY_AMOUNTS = Arrays.asList(CommonConfig.T1_APIARY_QUANTITY.get(), CommonConfig.T2_APIARY_QUANTITY.get(), CommonConfig.T3_APIARY_QUANTITY.get(), CommonConfig.T4_APIARY_QUANTITY.get());
    public static final List<ApiaryOutputType> DEFAULT_APIARY_OUTPUTS = Arrays.asList(CommonConfig.T1_APIARY_OUTPUT.get(), CommonConfig.T2_APIARY_OUTPUT.get(), CommonConfig.T3_APIARY_OUTPUT.get(), CommonConfig.T4_APIARY_OUTPUT.get());
    public static final HoneycombData DEFAULT = new HoneycombData(HoneycombType.NONE, Items.AIR, Items.AIR, Color.DEFAULT, true, DEFAULT_APIARY_AMOUNTS, DEFAULT_APIARY_OUTPUTS);

    /**
     * A {@link Codec<HoneycombData>} that can be parsed to create a
     * {@link HoneycombData} object.
     */
    public static final Codec<HoneycombData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HoneycombType.CODEC.fieldOf("type").orElse(HoneycombType.DEFAULT).forGetter(HoneycombData::getHoneycombType),
            Registry.ITEM.fieldOf("item").orElse(Items.BARRIER).forGetter(HoneycombData::getHoneycomb),
            Registry.ITEM.fieldOf("block").orElse(Items.BARRIER).forGetter(HoneycombData::getHoneycombBlock),
            Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(HoneycombData::getColor),
            Codec.BOOL.fieldOf("edible").orElse(true).forGetter(HoneycombData::isEdible),
            Codec.intRange(-1, Integer.MAX_VALUE).listOf().fieldOf("apiaryOutputAmounts").orElse(DEFAULT_APIARY_AMOUNTS).forGetter(HoneycombData::getApiaryOutputAmounts),
            ApiaryOutputType.CODEC.listOf().fieldOf("apiaryOutputTypes").orElse(DEFAULT_APIARY_OUTPUTS).forGetter(HoneycombData::getApiaryOutputTypes)
    ).apply(instance, HoneycombData::new));

    /*
    *
    * "apairyOutput": [
  {"id":"resourcefulbees:diamond_comb"}
  {"id":"resourcefulbees:diamond_comb", "count": 4}
  {"id":"resourcefulbees:diamond_comb", "count": 8}
  {"id":"resourcefulbees:diamond_comb_block", "count": 4}
  {"id":"resourcefulbees:diamond_comb_block", "count": 8}
]
    *
    *
    *
    * */




    protected final HoneycombType honeycombType;
    protected Item honeycomb;
    protected Item honeycombBlock;
    protected Color color;
    protected List<Integer> apiaryOutputAmounts;
    protected List<ApiaryOutputType> apiaryOutputTypeTypes;
    protected boolean edible;

    private HoneycombData(HoneycombType honeycombType, Item honeycomb, Item honeycombBlock, Color color, boolean edible, List<Integer> apiaryOutputAmounts, List<ApiaryOutputType> apiaryOutputTypeTypes) {
        this.honeycombType = honeycombType;
        this.honeycomb = honeycomb;
        this.honeycombBlock = honeycombBlock;
        this.color = color;
        this.edible = edible;
        this.apiaryOutputAmounts = apiaryOutputAmounts;
        this.apiaryOutputTypeTypes = apiaryOutputTypeTypes;
        fixApiaryOutputs();
    }

    private void fixApiaryOutputs() {
        for (int i = 0; i < 4; i++) {
            if (apiaryOutputAmounts.get(i) == null || apiaryOutputAmounts.get(i) <= 0) {
                apiaryOutputAmounts.set(i, DEFAULT_APIARY_AMOUNTS.get(i));
            }

            if (apiaryOutputTypeTypes.get(i) == null) {
                apiaryOutputTypeTypes.set(i, DEFAULT_APIARY_OUTPUTS.get(i));
            }
        }
    }

    /**
     * Gets the {@link HoneycombType} for the associated bee. There are
     * three possible values for {@link HoneycombType}:
     * <br>
     * <br>
     * <b>NONE</b> - The associated bee doesn't output a honeycomb
     * <br>
     * <b>DEFAULT</b> - The associated bee outputs a honeycomb item that
     * was registered by Resourceful Bees
     * <br>
     * <b>CUSTOM</b> - The associated bee outputs a custom item as its
     * honeycomb. This can be any in-game item.
     *
     * @return Returns the {@link HoneycombType} for the associated bee.
     */
    public HoneycombType getHoneycombType() {
        return honeycombType;
    }

    /**
     * Gets the honeycomb for the associated bee as an {@link Item}. Use the method
     * {@link Item#getDefaultInstance()} to create an {@link ItemStack} from this
     * {@link Item}.
     *
     * @return Returns the honeycomb for the associated bee as an {@link Item}.
     */
    public Item getHoneycomb() {
        return honeycomb;
    }

    /**
     * Gets the honeycomb block for the associated bee as an {@link Item}. Use the method
     * {@link Item#getDefaultInstance()} to create an {@link ItemStack} from this
     * {@link Item}.
     *
     * @return Returns the honeycomb block for the associated bee as an {@link Item}.
     */
    public Item getHoneycombBlock() {
        return honeycombBlock;
    }

    /**
     * Gets the {@link Color} for the associated bee's honeycomb.
     *
     * @return Returns the {@link Color} for the associated bee's honeycomb.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets a {@link Boolean} that indicates whether the associated bee's honeycomb
     * can be eaten by the player.
     *
     * @return Returns <b>true</b> if the honeycomb is edible.
     */
    public boolean isEdible() {
        return edible;
    }

    /**
     * Gets a {@link List} of {@link Integer}s indicating the output amounts
     * for each Apiary tier. If the amounts are not specified or have a value that is
     * less than or equal to zero, then the global default value is used.
     *
     * @return Returns a {@link List} of {@link Integer}s indicating the output amounts
     * for each Apiary tier.
     */
    public List<Integer> getApiaryOutputAmounts() {
        return apiaryOutputAmounts;
    }

    /**
     * Gets a {@link List} of {@link ApiaryOutputType}s indicating the output type
     * for each Apiary tier. Any missing or unspecified entries in the list are automatically
     * setup as the global default value.
     *
     * @return Returns a {@link List} of {@link ApiaryOutputType}s indicating the output types
     * for each Apiary tier.
     */
    public List<ApiaryOutputType> getApiaryOutputTypes() {
        return apiaryOutputTypeTypes;
    }

    /**
     * This method takes into consideration the {@link HoneycombData#apiaryOutputAmounts} and
     * the {@link HoneycombData#apiaryOutputTypeTypes} to create an appropriate {@link ItemStack}
     * output based on the apiary tier.
     *
     * @return Returns an appropriate {@link ItemStack} output based on the apiary tier.
     */
    public ItemStack createApiaryOutput(int tier) {
        return new ItemStack(getApiaryOutputTypes().get(tier) == ApiaryOutputType.COMB ? honeycomb : honeycombBlock, apiaryOutputAmounts.get(tier));
    }

    public HoneycombData toImmutable() {
        return this;
    }

    public static class Mutable extends HoneycombData {

        public Mutable(HoneycombType honeycombType, Item honeycomb, Item honeycombBlock, Color color, boolean edible, List<Integer> apiaryOutputAmounts, List<ApiaryOutputType> apiaryOutputTypeTypes) {
            super(honeycombType, honeycomb, honeycombBlock, color, edible, apiaryOutputAmounts, apiaryOutputTypeTypes);
        }

        public Mutable(boolean hasComb) {
            super(hasComb ? HoneycombType.CUSTOM : HoneycombType.NONE, null, null, Color.DEFAULT, false, DEFAULT_APIARY_AMOUNTS, DEFAULT_APIARY_OUTPUTS);
        }

        public void setHoneycomb(Item honeycomb) {
            this.honeycomb = honeycomb;
        }

        public void setHoneycombBlock(Item honeycombBlock) {
            this.honeycombBlock = honeycombBlock;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setApiaryOutputAmounts(List<Integer> apiaryOutputAmounts) {
            this.apiaryOutputAmounts = apiaryOutputAmounts;
        }

        public void setApiaryOutputTypeTypes(List<ApiaryOutputType> apiaryOutputTypeTypes) {
            this.apiaryOutputTypeTypes = apiaryOutputTypeTypes;
        }

        public void setEdible(boolean edible) {
            this.edible = edible;
        }

        @Override
        public HoneycombData toImmutable() {
            return new HoneycombData(this.honeycombType, this.honeycomb,this.honeycombBlock, this.color, this.edible, this.apiaryOutputAmounts, this.apiaryOutputTypeTypes);
        }
    }
}
