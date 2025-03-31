package com.teamresourceful.resourcefulbees.common.items.upgrade;

import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ItemTranslations;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BreederTimeUpgradeItem extends Item implements IntegerUpgrade {

    private final int reduction;

    public BreederTimeUpgradeItem(Properties pProperties) {
        this(pProperties, 300);
    }

    public BreederTimeUpgradeItem(Properties pProperties, int reduction) {
        super(pProperties);
        this.reduction = reduction;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level pLevel, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(Component.translatable(ItemTranslations.BREED_TIME_UPGRADE, getUpgradeTier(stack)).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public final int getUpgradeTier(ItemStack stack) {
        return Math.min(getReduction(stack), 2300);
    }

    private int getReduction(ItemStack stack) {
        //noinspection ConstantConditions
        if (stack.hasTag() && stack.getTag().contains(NBTConstants.Breeder.TIME_REDUCTION)) {
            return stack.getTag().getInt(NBTConstants.Breeder.TIME_REDUCTION);
        }
        return reduction;
    }

    @Override
    public UpgradeType getUpgradeType() {
        return UpgradeType.BREEDER;
    }
}
