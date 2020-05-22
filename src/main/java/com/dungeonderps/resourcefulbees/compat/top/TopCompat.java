package com.dungeonderps.resourcefulbees.compat.top;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.IronBeehiveBlock;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.CentrifugeBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombBlockEntity;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.function.Function;

import static com.dungeonderps.resourcefulbees.config.BeeInfo.BEE_INFO;

public class TopCompat implements Function<ITheOneProbe, Void>
{
    private final String formatting = TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString();

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe)
    {
        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            if(world.getTileEntity(data.getPos()) instanceof HoneycombBlockEntity)
            {
                TileEntity honeyBlock = world.getTileEntity(data.getPos());
                final ItemStack honeyCombBlock = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
                honeyCombBlock.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_BEE_TYPE, honeyBlock.serializeNBT().getString(BeeConst.NBT_BEE_TYPE));
                honeyCombBlock.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_COLOR, honeyBlock.serializeNBT().getString(BeeConst.NBT_COLOR));

                probeInfo.horizontal()
                        .item(honeyCombBlock)
                        .vertical()
                        .itemLabel(honeyCombBlock)
                        .text(formatting + BeeConst.MOD_NAME);
                return true;
            }
            if(world.getTileEntity(data.getPos()) instanceof IronBeehiveBlockEntity)
            {
                IronBeehiveBlockEntity beehiveBlockEntity = (IronBeehiveBlockEntity) world.getTileEntity(data.getPos());
                if(beehiveBlockEntity.isSmoked) {
                    probeInfo.horizontal()
                            .item(blockState.getBlock().asItem().getDefaultInstance())
                            .vertical()
                            .itemLabel(blockState.getBlock().asItem().getDefaultInstance())
                            .text(I18n.format("gui." + ResourcefulBees.MOD_ID + ".beehive.smoked"))
                            .progress((int) Math.floor(beehiveBlockEntity.ticksSmoked / 20.0), 30)
                            .text(formatting + BeeConst.MOD_NAME);
                    return true;
                }
                if(mode.equals(ProbeMode.EXTENDED)){
                    IronBeehiveBlockEntity ironBeeHive = (IronBeehiveBlockEntity) world.getTileEntity(data.getPos());
                    if (ironBeeHive.hasCombs()) {
                        int honeyLevel = 0;
                        if (ironBeeHive.getBlockState().has(IronBeehiveBlock.HONEY_LEVEL))
                            honeyLevel = ironBeeHive.getBlockState().get(IronBeehiveBlock.HONEY_LEVEL);
                        IProbeInfo vertical = null;
                        IProbeInfo horizontal = null;
                        probeInfo.horizontal()
                                .item(blockState.getBlock().asItem().getDefaultInstance())
                                .vertical()
                                .itemLabel(blockState.getBlock().asItem().getDefaultInstance())
                                .text(I18n.format("gui." + ResourcefulBees.MOD_ID + ".beehive.smoked"))
                                .text(I18n.format("gui." + ResourcefulBees.MOD_ID + ".beehive.honeylevel") + honeyLevel)
                                .progress((int) Math.floor(beehiveBlockEntity.ticksSmoked / 20.0), 30)
                                .text(formatting + BeeConst.MOD_NAME);
                        vertical = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xff006699).spacing(0));
                        int hiveCombSize = ironBeeHive.numberOfCombs();
                        hiveCombSize = Math.min(hiveCombSize, 6);
                        for (int i =0; i < hiveCombSize; i++){
                            horizontal = vertical.horizontal(probeInfo.defaultLayoutStyle().spacing(10).alignment(ElementAlignment.ALIGN_CENTER));
                            horizontal.item(honeyComb(String.valueOf(i), ironBeeHive))
                                    .text(honeyComb(String.valueOf(i), ironBeeHive).getDisplayName().getFormattedText());
                        }
                        return true;
                    }
                }
            }
            if(world.getTileEntity(data.getPos()) instanceof CentrifugeBlockEntity)
            {
                CentrifugeBlockEntity beeHiveBlock = (CentrifugeBlockEntity) world.getTileEntity(data.getPos());

                if(beeHiveBlock.time > 0) {
                    probeInfo.horizontal()
                            .item(blockState.getBlock().asItem().getDefaultInstance())
                            .vertical()
                            .itemLabel(blockState.getBlock().asItem().getDefaultInstance())
                            .progress((int) Math.floor(beeHiveBlock.time / 20.0), beeHiveBlock.totalTime / 20)
                            .text(formatting + BeeConst.MOD_NAME);
                    return true;
                }
            }
            if (blockState.getBlock().getRegistryName().getNamespace().equals(ResourcefulBees.MOD_ID)){
                probeInfo.horizontal()
                        .item(blockState.getBlock().asItem().getDefaultInstance())
                        .vertical()
                        .itemLabel(blockState.getBlock().asItem().getDefaultInstance())
                        .text(formatting + BeeConst.MOD_NAME);
                return true;
            }
            return false;
        });

        theOneProbe.registerEntityDisplayOverride((mode, probeInfo, player, world, entity, data) -> {
            if (entity instanceof CustomBeeEntity){
                probeInfo.horizontal()
                        .vertical()
                        .text(entity.getDisplayName().getString())
                        .text(formatting + BeeConst.MOD_NAME);
                return true;
            }
            return false;
        });

        return null;
    }

    public static ItemStack honeyComb(String num, TileEntity te){
        final ItemStack honeyComb = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        if (!te.serializeNBT().getCompound(BeeConst.NBT_HONEYCOMBS_TE).getString(num).equals("")) {
                honeyComb.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_BEE_TYPE, te.serializeNBT().getCompound(BeeConst.NBT_HONEYCOMBS_TE).getString(num));
                honeyComb.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_COLOR, String.valueOf(BEE_INFO.get(te.serializeNBT().getCompound(BeeConst.NBT_HONEYCOMBS_TE).getString(num)).getColor()));
            return honeyComb;
        }
        else return Items.AIR.getDefaultInstance();
    }


}