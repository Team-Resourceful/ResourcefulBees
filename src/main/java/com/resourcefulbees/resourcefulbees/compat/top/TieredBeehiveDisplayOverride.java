package com.resourcefulbees.resourcefulbees.compat.top;

import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TieredBeehiveDisplayOverride implements IBlockDisplayOverride {

    @Override
    public boolean overrideStandardInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
        TileEntity tileEntity = world.getBlockEntity(iProbeHitData.getPos());
        return tileEntity instanceof TieredBeehiveTileEntity && createHiveProbeData(probeMode, iProbeInfo, blockState, (TieredBeehiveTileEntity) tileEntity);
    }

    private boolean createHiveProbeData(ProbeMode mode, IProbeInfo probeInfo, net.minecraft.block.BlockState blockState, TieredBeehiveTileEntity tileEntity) {
        probeInfo.horizontal()
                .item(new ItemStack(blockState.getBlock().asItem()))
                .vertical()
                .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                .text(CompoundText.create().style(TextStyleClass.MODNAME).text(BeeConstants.MOD_NAME));

        probeInfo.horizontal()
                .vertical()
                .text(new TranslationTextComponent("gui.resourcefulbees.beehive.tier").append(getHiveTier(tileEntity)))
                .text(new TranslationTextComponent("gui.resourcefulbees.beehive.bees")
                        .append(" / ")
                        .append(getHiveMaxBees(tileEntity))
                        .append(new StringTextComponent(getHiveBeeCount(tileEntity))))
                .text(new TranslationTextComponent("gui.resourcefulbees.beehive.honeylevel").append(getHoneyLevel(tileEntity)))
                .text(new TranslationTextComponent("gui.resourcefulbees.beehive.smoked").append(getSmokedStatus(tileEntity)));

        createSmokedProbeData(probeInfo, tileEntity);

        if (mode.equals(ProbeMode.EXTENDED)) {
            createHoneycombData(probeInfo, tileEntity);
        }
        return true;
    }

    private String getHiveMaxBees(TieredBeehiveTileEntity tileEntity) {
        return String.valueOf(tileEntity.getMaxBees());
    }

    @NotNull
    private String getHiveBeeCount(TieredBeehiveTileEntity tileEntity) {
        return String.valueOf(tileEntity.getOccupantCount());
    }

    @NotNull
    private String getSmokedStatus(TieredBeehiveTileEntity tileEntity) {
        return String.valueOf(tileEntity.isSedated());
    }

    @NotNull
    private String getHiveTier(TieredBeehiveTileEntity tileEntity) {
        return String.valueOf(tileEntity.getTier());
    }

    private String getHoneyLevel(TieredBeehiveTileEntity tileEntity) {
        return String.valueOf(tileEntity.getBlockState().getValue(BeehiveBlock.HONEY_LEVEL));
    }

    private void createHoneycombData(IProbeInfo probeInfo, TieredBeehiveTileEntity tileEntity) {
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

    private void buildHoneycombList(TieredBeehiveTileEntity tileEntity, List<ItemStack> combs) {
        tileEntity.getHoneycombs().iterator().forEachRemaining(honeycomb -> {
            Iterator<ItemStack> iterator = combs.iterator();
            while (iterator.hasNext() && !honeycomb.isEmpty()) {
                ItemStack stackInList = iterator.next();
                if (Container.consideredTheSameItem(honeycomb, stackInList)) {
                    combs.get(combs.indexOf(stackInList)).grow(1);
                    honeycomb.setCount(0);
                }
            }

            if (!honeycomb.isEmpty()) combs.add(honeycomb);
        });
    }

    private void createSmokedProbeData(IProbeInfo probeInfo, TieredBeehiveTileEntity tileEntity) {
        if (tileEntity.getTicksSmoked() != -1) {
            probeInfo.horizontal().vertical()
                    .text(new StringTextComponent(new TranslationTextComponent("gui.resourcefulbees.beehive.smoke_time").getString()))
                    .progress((int) Math.floor(tileEntity.getTicksSmoked() / 20.0), 30);
        }
    }
}
