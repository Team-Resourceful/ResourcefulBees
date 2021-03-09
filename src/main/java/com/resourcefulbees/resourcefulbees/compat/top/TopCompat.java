package com.resourcefulbees.resourcefulbees.compat.top;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class TopCompat implements Function<ITheOneProbe, Void>
{
    IFormattableTextComponent formattedName = new StringTextComponent(BeeConstants.MOD_NAME).formatted(TextFormatting.BLUE, TextFormatting.ITALIC);

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            TileEntity tileEntity = world.getTileEntity(data.getPos());
            if (tileEntity instanceof TieredBeehiveTileEntity) {
                return createHiveProbeData(mode, probeInfo, blockState, (TieredBeehiveTileEntity) tileEntity);
            } else if (tileEntity instanceof CentrifugeTileEntity) {
                return createCentrifugeProbeData(probeInfo, blockState, (CentrifugeTileEntity) tileEntity);
            }
            if (isBlockOurs(blockState.getBlock().getRegistryName())) {
                return createModProbeData(probeInfo, blockState);
            }
            return false;
        });

        return null;
    }

    private boolean createModProbeData(IProbeInfo probeInfo, net.minecraft.block.BlockState blockState) {
        probeInfo.horizontal()
                .item(new ItemStack(blockState.getBlock().asItem()))
                .vertical()
                .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                .text(formattedName);
        return true;
    }

    private boolean isBlockOurs(ResourceLocation registryName) {
        return registryName != null && registryName.getNamespace().equals(ResourcefulBees.MOD_ID);
    }

    private boolean createCentrifugeProbeData(IProbeInfo probeInfo, net.minecraft.block.BlockState blockState, CentrifugeTileEntity tileEntity) {
        if(tileEntity.getProcessTime(0) > 0) {
            probeInfo.horizontal()
                    .item(new ItemStack(blockState.getBlock().asItem()))
                    .vertical()
                    .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                    .progress((int) Math.floor(tileEntity.getProcessTime(0) / 20.0), tileEntity.getRecipeTime(0) / 20)
                    .text(formattedName);
            return true;
        }
        return false;
    }

    private boolean createHiveProbeData(ProbeMode mode, IProbeInfo probeInfo, net.minecraft.block.BlockState blockState, TieredBeehiveTileEntity tileEntity) {
        probeInfo.horizontal()
                .item(new ItemStack(blockState.getBlock().asItem()))
                .vertical()
                .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                .text(formattedName)
                .text(new TranslationTextComponent("gui.resourcefulbees.beehive.tier").append(getHiveTier(tileEntity)))
                .text(new StringTextComponent(getHiveBeeCount(tileEntity))
                        .append(" / ")
                        .append(getHiveMaxBees(tileEntity))
                        .append(new TranslationTextComponent("gui.resourcefulbees.beehive.bees")))
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
        return String.valueOf(tileEntity.getBeeCount());
    }

    @NotNull
    private String getSmokedStatus(TieredBeehiveTileEntity tileEntity) {
        return String.valueOf(tileEntity.isSmoked());
    }

    @NotNull
    private String getHiveTier(TieredBeehiveTileEntity tileEntity) {
        return String.valueOf(tileEntity.getTier());
    }

    @NotNull
    private String getHoneyLevel(TieredBeehiveTileEntity tileEntity) {
        return String.valueOf(tileEntity.getBlockState().get(BeehiveBlock.HONEY_LEVEL));
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
        horizontal.get().item(honeycomb).text(honeycomb.getDisplayName());
    }

    private void buildHoneycombList(TieredBeehiveTileEntity tileEntity, List<ItemStack> combs) {
        tileEntity.getHoneycombs().iterator().forEachRemaining(honeycomb -> {
            Iterator<ItemStack> iterator = combs.iterator();
            while (iterator.hasNext() && !honeycomb.isEmpty()) {
                ItemStack stackInList = iterator.next();
                if (Container.areItemsAndTagsEqual(honeycomb, stackInList)) {
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