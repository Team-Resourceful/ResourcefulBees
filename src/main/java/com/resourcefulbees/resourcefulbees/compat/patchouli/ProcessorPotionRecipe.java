package com.resourcefulbees.resourcefulbees.compat.patchouli;

import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

public class ProcessorPotionRecipe implements IComponentProcessor {

    private BrewingRecipe mix = null;
    private BrewingRecipe mix2 = null;

    String text;
    private IVariable hidden;

    @Override
    public void setup(IVariableProvider lookup) {
        if (lookup.has("potion") && lookup.has("stack")) {
            IVariable potionID = lookup.get("potion");
            IVariable stackID = lookup.get("stack");
            Potion potion = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(potionID.asString()));
            ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(stackID.asString())));
            mix = BeeInfoUtils.getMix(potion, stack);
        }
        if (lookup.has("potion2") && lookup.has("stack2")) {
            IVariable potionID2 = lookup.get("potion2");
            IVariable stackID2 = lookup.get("stack2");
            Potion potion2 = ForgeRegistries.POTION_TYPES.getValue(new ResourceLocation(potionID2.asString()));
            ItemStack stack2 = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(stackID2.asString())));
            mix2 = BeeInfoUtils.getMix(potion2, stack2);
        }
        if (lookup.has("text")) {
            this.text = lookup.get("text").asString();
        }
        if (lookup.has("hidden")) {
            this.hidden = lookup.get("hidden");
        }
    }

    @Override
    public IVariable process(String key) {
        switch (key) {
            case "mix":
                return IVariable.wrap(mix != null);
            case "mix2":
                return IVariable.wrap(mix2 != null);
            case "mix2null":
                return IVariable.wrap(mix2 == null);
            case "mixnull":
                return IVariable.wrap(mix == null);
            case "title":
                if (mix != null) return IVariable.from(mix.getOutput().getHoverName());
                return IVariable.empty();
            case "input":
                if (mix != null) return IVariable.from(mix.getInput().getItems());
                return IVariable.empty();
            case "output":
                if (mix != null) return IVariable.from(mix.getOutput());
                return IVariable.empty();
            case "ingredient":
                if (mix != null) return IVariable.from(mix.getIngredient().getItems());
                return IVariable.empty();
            case "title2":
                if (mix2 != null) return IVariable.from(mix2.getOutput().getHoverName());
                return IVariable.empty();
            case "input2":
                if (mix2 != null) return IVariable.from(mix2.getInput().getItems());
                return IVariable.empty();
            case "output2":
                if (mix2 != null) return IVariable.from(mix2.getOutput());
                return IVariable.empty();
            case "ingredient2":
                if (mix2 != null) return IVariable.from(mix2.getIngredient().getItems());
                return IVariable.empty();
            case "stand":
            case "stand2":
                return IVariable.from(new ItemStack(Items.BREWING_STAND));
            case "background2":
            case "background":
                return IVariable.wrap("textures/gui/patchouli/brewing_stand.png");
            case "hidden":
                return hidden;
            case "text":
                return IVariable.wrap(text);
            default:
                return IVariable.empty();
        }
    }

    @Override
    public boolean allowRender(String group) {
        return !"hidden".equals(group);
    }
}
