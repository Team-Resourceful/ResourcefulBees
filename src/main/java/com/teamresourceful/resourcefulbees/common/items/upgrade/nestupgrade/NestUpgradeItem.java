package com.teamresourceful.resourcefulbees.common.items.upgrade.nestupgrade;

import com.teamresourceful.resourcefulbees.common.items.upgrade.UpgradeType;
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

public class NestUpgradeItem extends Item implements NestUpgrade {

    private final BeehiveUpgrade upgrade;

    public NestUpgradeItem(BeehiveUpgrade upgrade, Properties pProperties) {
        super(pProperties);
        this.upgrade = upgrade;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(ItemTranslations.HIVE_UPGRADE.withStyle(ChatFormatting.GOLD));
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
