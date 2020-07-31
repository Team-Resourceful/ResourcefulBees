package com.dungeonderps.resourcefulbees.compat.top;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.TieredBeehiveBlock;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.CentrifugeTileEntity;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombTileEntity;
import com.dungeonderps.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.function.Function;

import static com.dungeonderps.resourcefulbees.config.BeeInfo.BEE_INFO;

public class TopCompat implements Function<ITheOneProbe, Void>
{
    IFormattableTextComponent formattedName = new StringTextComponent(BeeConstants.MOD_NAME).mergeStyle(TextFormatting.BLUE, TextFormatting.ITALIC);

    public static ItemStack honeyComb(String num, TileEntity te){
        final ItemStack honeyComb = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        if (!te.serializeNBT().getCompound(BeeConstants.NBT_HONEYCOMBS_TE).getString(num).equals("")) {
                honeyComb.getOrCreateChildTag(BeeConstants.NBT_ROOT).putString(BeeConstants.NBT_BEE_TYPE, te.serializeNBT().getCompound(BeeConstants.NBT_HONEYCOMBS_TE).getString(num));
                honeyComb.getOrCreateChildTag(BeeConstants.NBT_ROOT).putString(BeeConstants.NBT_COLOR, String.valueOf(BEE_INFO.get(te.serializeNBT().getCompound(BeeConstants.NBT_HONEYCOMBS_TE).getString(num)).getHoneycombColor()));
            return honeyComb;
        }
        else return new ItemStack(Items.AIR);
    }

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe)
    {
        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            if(world.getTileEntity(data.getPos()) instanceof HoneycombTileEntity)
            {
                TileEntity honeyBlock = world.getTileEntity(data.getPos());
                final ItemStack honeyCombBlock = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());

                if (honeyBlock != null){
                    honeyCombBlock.getOrCreateChildTag(BeeConstants.NBT_ROOT).putString(BeeConstants.NBT_BEE_TYPE, honeyBlock.serializeNBT().getString(BeeConstants.NBT_BEE_TYPE));
                    honeyCombBlock.getOrCreateChildTag(BeeConstants.NBT_ROOT).putString(BeeConstants.NBT_COLOR, honeyBlock.serializeNBT().getString(BeeConstants.NBT_COLOR));
                }

                probeInfo.horizontal()
                        .item(honeyCombBlock)
                        .vertical()
                        .itemLabel(honeyCombBlock)
                        .text(formattedName);
                return true;
            }
            if(world.getTileEntity(data.getPos()) instanceof TieredBeehiveTileEntity)
            {
                TieredBeehiveTileEntity beehiveBlockEntity = (TieredBeehiveTileEntity) world.getTileEntity(data.getPos());
                if(beehiveBlockEntity != null && mode.equals(ProbeMode.EXTENDED)){
                    TieredBeehiveTileEntity ironBeeHive = (TieredBeehiveTileEntity) world.getTileEntity(data.getPos());
                    if (ironBeeHive != null && ironBeeHive.hasCombs()) {
                        int honeyLevel = 0;
                        if (ironBeeHive.getBlockState().hasProperty(TieredBeehiveBlock.HONEY_LEVEL))
                            honeyLevel = ironBeeHive.getBlockState().get(TieredBeehiveBlock.HONEY_LEVEL);
                        IProbeInfo vertical;
                        IProbeInfo horizontal;
                        probeInfo.horizontal()
                                .item(new ItemStack(blockState.getBlock().asItem()))
                                .vertical()
                                .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                                .text(new StringTextComponent(new TranslationTextComponent("gui.resourcefulbees.beehive.smoked").getString() + beehiveBlockEntity.isSmoked))
                                .text(new StringTextComponent(new TranslationTextComponent("gui.resourcefulbees.beehive.honeylevel").getString() + honeyLevel))
                                .progress((int) Math.floor(beehiveBlockEntity.ticksSmoked / 20.0), 30)
                                .text(formattedName);
                        vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xff006699).spacing(0));
                        int hiveCombSize = ironBeeHive.numberOfCombs();
                        hiveCombSize = Math.min(hiveCombSize, 6);
                        for (int i =0; i < hiveCombSize; i++){
                            horizontal = vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER));
                            horizontal.item(honeyComb(String.valueOf(i), ironBeeHive))
                                    .text(new StringTextComponent(honeyComb(String.valueOf(i), ironBeeHive).getDisplayName().getString()));
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

        theOneProbe.registerEntityDisplayOverride((mode, probeInfo, player, world, entity, data) -> {
            if (entity instanceof CustomBeeEntity){
                probeInfo.horizontal()
                        .vertical()
                        .text(new StringTextComponent(entity.getDisplayName().getString()))
                        .text(formattedName);
                return true;
            }
            return false;
        });

        return null;
    }


}