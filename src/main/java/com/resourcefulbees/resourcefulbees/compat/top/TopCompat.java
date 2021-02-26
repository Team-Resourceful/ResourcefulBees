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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class TopCompat implements Function<ITheOneProbe, Void>
{
    IFormattableTextComponent formattedName = new StringTextComponent(TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString() + BeeConstants.MOD_NAME);

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            TileEntity tileEntity = world.getTileEntity(data.getPos());
            if (tileEntity instanceof TieredBeehiveTileEntity) {
                TieredBeehiveTileEntity beehiveTileEntity = (TieredBeehiveTileEntity) tileEntity;
                int honeyLevel = beehiveTileEntity.getBlockState().get(BeehiveBlock.HONEY_LEVEL);

                probeInfo.horizontal()
                        .item(new ItemStack(blockState.getBlock().asItem()))
                        .vertical()
                        .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                        .text(formattedName)
                        .text(new StringTextComponent(new TranslationTextComponent("gui.resourcefulbees.beehive.honeylevel").getString() + honeyLevel))
                        .text(new StringTextComponent(new TranslationTextComponent("gui.resourcefulbees.beehive.smoked").getString() + beehiveTileEntity.isSmoked()));

                if (beehiveTileEntity.getTicksSmoked() != -1) {
                    probeInfo.horizontal().vertical()
                            .text(new StringTextComponent(new TranslationTextComponent("gui.resourcefulbees.beehive.smoke_time").getString()))
                            .progress((int) Math.floor(beehiveTileEntity.getTicksSmoked() / 20.0), 30);
                }

                if (mode.equals(ProbeMode.EXTENDED)) {
                    if (beehiveTileEntity.hasCombs()) {
                        List<ItemStack> combs = new ArrayList<>();

                        beehiveTileEntity.getHoneycombs().iterator().forEachRemaining(honeycomb -> {
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

                        IProbeInfo vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xff006699).spacing(0));
                        AtomicReference<IProbeInfo> horizontal = new AtomicReference<>(vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER)));

                        if (combs.size() <= 4) {
                            combs.forEach(honeycomb -> {
                                horizontal.set(vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER)));
                                horizontal.get().item(honeycomb).text(honeycomb.getDisplayName());
                            });
                        } else {
                            AtomicInteger columnCount = new AtomicInteger();
                            combs.forEach(honeycomb -> {
                                if (columnCount.get() == 7) {
                                    horizontal.set(vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER)));
                                    columnCount.set(0);
                                }
                                horizontal.get().item(honeycomb);
                                columnCount.incrementAndGet();
                            });
                        }
                    }
                }
                return true;
            } else if (tileEntity instanceof CentrifugeTileEntity) {
                CentrifugeTileEntity centrifugeTileEntity = (CentrifugeTileEntity) tileEntity;

                if(centrifugeTileEntity.getProcessTime(0) > 0) {
                    probeInfo.horizontal()
                            .item(new ItemStack(blockState.getBlock().asItem()))
                            .vertical()
                            .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                            .progress((int) Math.floor(centrifugeTileEntity.getProcessTime(0) / 20.0), centrifugeTileEntity.getRecipeTime(0) / 20)
                            .text(formattedName);
                    return true;
                }
            }
            ResourceLocation registryName = blockState.getBlock().getRegistryName();
            if (registryName != null ) {
                if (registryName.getNamespace().equals(ResourcefulBees.MOD_ID)) {
                    probeInfo.horizontal()
                            .item(new ItemStack(blockState.getBlock().asItem()))
                            .vertical()
                            .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                            .text(formattedName);
                    return true;
                }
            }
            return false;
        });

        return null;
    }


}