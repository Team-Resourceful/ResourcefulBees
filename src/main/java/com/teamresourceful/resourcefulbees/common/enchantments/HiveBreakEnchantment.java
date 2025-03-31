package com.teamresourceful.resourcefulbees.common.enchantments;

import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEnchantments;
import com.teamresourceful.resourcefulbees.platform.common.events.PlayerBrokeBlockEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.RegisterHiveBreakBlocksEvent;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HiveBreakEnchantment extends Enchantment {
    
    private static final Supplier<Map<Block, Block>> CONVERSIONS = Suppliers.memoize(HiveBreakEnchantment::createConversions);

    public HiveBreakEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public static void onBlockBreak(PlayerBrokeBlockEvent event) {
        Level level = event.level();
        if (!level.isClientSide() && level.getRandom().nextInt(10) == 0) {
            int value = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HIVE_BREAK.get(), event.player());
            if (value > 0 && level.getRandom().nextInt(10 / value) == 0) {
                Block nest = getConversion(event.state().getBlock());
                if (nest == null) return;
                BlockState nestState = nest.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom()));
                level.setBlockAndUpdate(event.pos(), nestState);
            }
        }
    }
    
    public static Block getConversion(Block block) {
        return CONVERSIONS.get().get(block);
    }

    private static Map<Block, Block> createConversions() {
        Map<Block, Block> conversions = new HashMap<>();
        var event = new RegisterHiveBreakBlocksEvent((input, output) -> conversions.put(input.get(), output.get()));
        RegisterHiveBreakBlocksEvent.EVENT.fire(event);
        return Collections.unmodifiableMap(conversions);
    }
}