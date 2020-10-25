package com.resourcefulbees.resourcefulbees.compat.top;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.block.TieredBeehiveBlock;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TopCompat implements Function<ITheOneProbe, Void>
{
    IFormattableTextComponent formattedName = new StringTextComponent(TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString() + BeeConstants.MOD_NAME);

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe)
    {
        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            if(world.getTileEntity(data.getPos()) instanceof TieredBeehiveTileEntity)
            {
                TieredBeehiveTileEntity beehiveBlockEntity = (TieredBeehiveTileEntity) world.getTileEntity(data.getPos());
                if(beehiveBlockEntity != null && mode.equals(ProbeMode.EXTENDED)){
                    TieredBeehiveTileEntity tieredBeehiveTileEntity = (TieredBeehiveTileEntity) world.getTileEntity(data.getPos());
                    if (tieredBeehiveTileEntity != null && tieredBeehiveTileEntity.hasCombs()) {
                        int honeyLevel = 0;
                        if (tieredBeehiveTileEntity.getBlockState().contains(TieredBeehiveBlock.HONEY_LEVEL))
                            honeyLevel = tieredBeehiveTileEntity.getBlockState().get(TieredBeehiveBlock.HONEY_LEVEL);
                        IProbeInfo vertical;
                        IProbeInfo horizontal;

                        probeInfo.horizontal()
                                .item(new ItemStack(blockState.getBlock().asItem()))
                                .vertical()
                                .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                                .text(formattedName)
                                .text(new StringTextComponent(new TranslationTextComponent("gui.resourcefulbees.beehive.smoked").getString() + beehiveBlockEntity.isSmoked))
                                .text(new StringTextComponent(new TranslationTextComponent("gui.resourcefulbees.beehive.honeylevel").getString() + honeyLevel))
                                .progress((int) Math.floor(beehiveBlockEntity.ticksSmoked / 20.0), 30);
                        vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xff006699).spacing(0));
                        int hiveCombSize = tieredBeehiveTileEntity.numberOfCombs();
                        if (hiveCombSize > 0) {
                            if (hiveCombSize <= 4) {
                                for (int i = 0; i < hiveCombSize; i++) {
                                    horizontal = vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER));
                                    horizontal.item(tieredBeehiveTileEntity.honeycombs.get(i))
                                            .text(tieredBeehiveTileEntity.honeycombs.get(i).getDisplayName());
                                }
                            } else {
                                HashMap<ItemStack, Integer> combs = new HashMap<>();
                                int columnCount = 0;
                                tieredBeehiveTileEntity.honeycombs.forEach(comb -> combs.merge(comb, 1, Integer::sum));
                                horizontal = vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER));
                                for (Map.Entry<ItemStack, Integer> entry : combs.entrySet()) {
                                    if (columnCount == 7) {
                                        horizontal = vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER));
                                        columnCount = 0;
                                    }
                                    ItemStack stack = entry.getKey().copy();
                                    stack.setCount(entry.getValue());
                                    horizontal.item(stack);
                                    columnCount++;
                                }
                            }
                        }
                        return true;
                    }
                }
            }
            if(world.getTileEntity(data.getPos()) instanceof CentrifugeTileEntity)
            {
                CentrifugeTileEntity beeHiveBlock = (CentrifugeTileEntity) world.getTileEntity(data.getPos());

                if(beeHiveBlock != null && beeHiveBlock.time > 0) {
                    probeInfo.horizontal()
                            .item(new ItemStack(blockState.getBlock().asItem()))
                            .vertical()
                            .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                            .progress((int) Math.floor(beeHiveBlock.time / 20.0), beeHiveBlock.totalTime / 20)
                            .text(formattedName);
                    return true;
                }
            }
            ResourceLocation registryName = blockState.getBlock().getRegistryName();
            if (registryName != null ) {
                if (registryName.getNamespace().equals(ResourcefulBees.MOD_ID)){
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