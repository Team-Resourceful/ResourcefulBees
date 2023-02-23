
package com.teamresourceful.resourcefulbees.common.compat.top;

import com.teamresourceful.resourcefulbees.common.blockentities.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.TopTranslations;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TieredBeehiveDisplayOverride implements IBlockDisplayOverride {

    @Override
    public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity tileEntity = level.getBlockEntity(iProbeHitData.getPos());
        if (tileEntity instanceof TieredBeehiveBlockEntity tieredHive) {
            createHiveProbeData(probeMode, iProbeInfo, blockState, tieredHive);
            return true;
        }
        return false;
    }

    private void createHiveProbeData(ProbeMode mode, IProbeInfo probeInfo, BlockState blockState, TieredBeehiveBlockEntity tileEntity) {
        probeInfo.horizontal()
                .item(new ItemStack(blockState.getBlock().asItem()))
                .vertical()
                .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                .text(CompoundText.create().style(TextStyleClass.MODNAME).text(BeeConstants.MOD_NAME));

        probeInfo.horizontal()
                .vertical()
                .text(Component.translatable(TopTranslations.BEES, getHiveBeeCount(tileEntity), getHiveMaxBees(blockState)))
                .text(Component.translatable(TopTranslations.HONEY_LEVEL, getHoneyLevel(tileEntity)))
                .text(Component.translatable(TopTranslations.SMOKED, getSmokedStatus(tileEntity)));

        createSmokedProbeData(probeInfo, tileEntity);

        if (mode.equals(ProbeMode.EXTENDED)) {
            createHoneycombData(probeInfo, tileEntity);
        }
    }

    private String getHiveMaxBees(BlockState state) {
        if (state.getBlock() instanceof TieredBeehiveBlock tieredBeehiveBlock) {
            return String.valueOf(tieredBeehiveBlock.getTier().maxBees());
        }
        return "Error";
    }

    @NotNull
    private String getHiveBeeCount(TieredBeehiveBlockEntity tileEntity) {
        return String.valueOf(tileEntity.getOccupantCount());
    }

    @NotNull
    private String getSmokedStatus(TieredBeehiveBlockEntity tileEntity) {
        return String.valueOf(tileEntity.isSedated());
    }

    private String getHoneyLevel(TieredBeehiveBlockEntity tileEntity) {
        return String.valueOf(tileEntity.getBlockState().getValue(BeehiveBlock.HONEY_LEVEL));
    }

    private void createHoneycombData(IProbeInfo probeInfo, TieredBeehiveBlockEntity tileEntity) {
        if (tileEntity.hasCombs()) {
            List<ItemStack> combs = new ArrayList<>();
            buildHoneycombList(tileEntity, combs);
            IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xff006699).spacing(0));
            AtomicReference<IProbeInfo> horizontal = new AtomicReference<>(vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER)));

            if (combs.size() <= 4) {
                combs.forEach(honeycomb -> formatHoneycombData(probeInfo, vertical, horizontal, honeycomb));
            } else {
                AtomicInteger columnCount = new AtomicInteger();
                combs.forEach(honeycomb -> formatHoneycombData(probeInfo, vertical, horizontal, columnCount, honeycomb));
            }
        }
    }

    private void formatHoneycombData(IProbeInfo probeInfo, IProbeInfo vertical, AtomicReference<IProbeInfo> horizontal, AtomicInteger columnCount, ItemStack honeycomb) {
        if (columnCount.get() == 7) {
            horizontal.set(vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER)));
            columnCount.set(0);
        }
        horizontal.get().item(honeycomb);
        columnCount.incrementAndGet();
    }

    private void formatHoneycombData(IProbeInfo probeInfo, IProbeInfo vertical, AtomicReference<IProbeInfo> horizontal, ItemStack honeycomb) {
        horizontal.set(vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER)));
        horizontal.get().item(honeycomb).text(honeycomb.getHoverName());
    }

    private void buildHoneycombList(TieredBeehiveBlockEntity tileEntity, List<ItemStack> combs) {
        tileEntity.getHoneycombs().iterator().forEachRemaining(honeycomb -> {
            ItemStack comb = honeycomb.copy();
            Iterator<ItemStack> iterator = combs.iterator();
            while (iterator.hasNext() && !comb.isEmpty()) {
                ItemStack stackInList = iterator.next();
                if (ItemStack.isSameItemSameTags(honeycomb, stackInList)) {
                    combs.get(combs.indexOf(stackInList)).grow(1);
                    comb.setCount(0);
                }
            }

            if (!comb.isEmpty()) combs.add(comb);
        });
    }

    private void createSmokedProbeData(IProbeInfo probeInfo, TieredBeehiveBlockEntity tileEntity) {
        if (tileEntity.getTicksSmoked() != -1) {
            probeInfo.horizontal().vertical()
                    .text(TopTranslations.SMOKE_TIME)
                    .progress((int) Math.floor(tileEntity.getTicksSmoked() / 20.0), 30);
        }
    }
}

