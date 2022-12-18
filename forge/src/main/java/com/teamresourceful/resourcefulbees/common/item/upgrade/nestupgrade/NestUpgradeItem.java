package com.teamresourceful.resourcefulbees.common.item.upgrade.nestupgrade;

import com.teamresourceful.resourcefulbees.common.items.ExpandableTooltip;
import com.teamresourceful.resourcefulbees.common.item.upgrade.UpgradeType;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NestUpgradeItem extends Item implements NestUpgrade, ExpandableTooltip {

    private final BeehiveUpgrade upgrade;

    public NestUpgradeItem(BeehiveUpgrade upgrade, Properties pProperties) {
        super(pProperties);
        this.upgrade = upgrade;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        setupTooltip(stack, level, components, flag);
    }

    @Override
    public Component getShiftingDisplay() {
        return TranslationConstants.Items.FOR_MORE_INFO;
    }

    @Override
    public void appendShiftTooltip(@NotNull ItemStack stack, @Nullable BlockGetter pLevel, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(TranslationConstants.Items.HIVE_UPGRADE.withStyle(ChatFormatting.GOLD));
    }

    @Override
    public UpgradeType getUpgradeType() {
        return UpgradeType.NEST;
    }

    @Override
    public BeehiveUpgrade getTier() {
        return upgrade;
    }
}
