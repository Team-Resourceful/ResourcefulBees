package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.block.HoneycombBlock;
import com.dungeonderps.resourcefulbees.item.HoneycombBlockItem;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class BlockItemFunction extends LootFunction {

    protected BlockItemFunction(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        BlockState state = context.get(LootParameters.BLOCK_STATE);
        Block block = state.getBlock();
        if (block instanceof HoneycombBlock){
            HoneycombBlock combBlock = (HoneycombBlock)block;
            ItemStack blockItem = new ItemStack(new HoneycombBlockItem());
            blockItem.getOrCreateChildTag("ResourcefulBees").putString("BeeType", combBlock.beeType);
            //blockItem.getOrCreateChildTag("ResourcefulBees").putString("Color", combBlock.blockColor);
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<BlockItemFunction> {

        public Serializer() {
            super(new ResourceLocation("resourcefulbees:blockitem"), BlockItemFunction.class);
        }

        @Override
        public BlockItemFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new BlockItemFunction(conditionsIn);
        }
    }
}
