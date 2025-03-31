package com.teamresourceful.resourcefulbees.common.items.upgrade;

import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ItemTranslations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HoneyGenUpgradeItem extends Item implements Upgrade {

    private final UpgradeType upgradeType;

    public HoneyGenUpgradeItem(Properties arg, UpgradeType upgradeType) {
        super(arg);
        this.upgradeType = upgradeType;
    }

    @Override
    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag isAdvanced) {
        switch (upgradeType) {
            case ENERGY_CAPACITY -> components.add(ItemTranslations.ENERGY_CAP_TOOLTIP);
            case ENERGY_FILL -> {
                components.add(ItemTranslations.ENERGY_FILL_TOOLTIP_1);
                components.add(ItemTranslations.ENERGY_FILL_TOOLTIP_2);
            }
            case ENERGY_XFER -> components.add(ItemTranslations.ENERGY_XFER_TOOLTIP);
            case HONEY_CAPACITY -> components.add(ItemTranslations.HONEY_CAP_TOOLTIP);
            default -> {/*do nothing*/}
        }
    }


}
