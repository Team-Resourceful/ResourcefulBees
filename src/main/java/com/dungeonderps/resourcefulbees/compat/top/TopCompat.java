package com.dungeonderps.resourcefulbees.compat.top;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.beehive.Tier1BeehiveBlock;
import com.dungeonderps.resourcefulbees.entity.passive.CustomBeeEntity;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.CentrifugeBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.HoneycombBlockEntity;
import com.dungeonderps.resourcefulbees.tileentity.beehive.Tier1BeehiveBlockEntity;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.function.Function;

import static com.dungeonderps.resourcefulbees.config.BeeInfo.BEE_INFO;

public class TopCompat implements Function<ITheOneProbe, Void>
{
    private final String formatting = TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString();

    public static ItemStack honeyComb(String num, TileEntity te){
        final ItemStack honeyComb = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        if (!te.serializeNBT().getCompound(BeeConst.NBT_HONEYCOMBS_TE).getString(num).equals("")) {
                honeyComb.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_BEE_TYPE, te.serializeNBT().getCompound(BeeConst.NBT_HONEYCOMBS_TE).getString(num));
                honeyComb.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_COLOR, String.valueOf(BEE_INFO.get(te.serializeNBT().getCompound(BeeConst.NBT_HONEYCOMBS_TE).getString(num)).getHoneycombColor()));
            return honeyComb;
        }
        else return new ItemStack(Items.AIR);
    }

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe)
    {
        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            if(world.getTileEntity(data.getPos()) instanceof HoneycombBlockEntity)
            {
                TileEntity honeyBlock = world.getTileEntity(data.getPos());
                final ItemStack honeyCombBlock = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());

                if (honeyBlock != null){
                    honeyCombBlock.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_BEE_TYPE, honeyBlock.serializeNBT().getString(BeeConst.NBT_BEE_TYPE));
                    honeyCombBlock.getOrCreateChildTag(BeeConst.NBT_ROOT).putString(BeeConst.NBT_COLOR, honeyBlock.serializeNBT().getString(BeeConst.NBT_COLOR));
                }

                probeInfo.horizontal()
                        .item(honeyCombBlock)
                        .vertical()
                        .itemLabel(honeyCombBlock)
                        .text(formatting + BeeConst.MOD_NAME);
                return true;
            }
            if(world.getTileEntity(data.getPos()) instanceof Tier1BeehiveBlockEntity)
            {
                Tier1BeehiveBlockEntity beehiveBlockEntity = (Tier1BeehiveBlockEntity) world.getTileEntity(data.getPos());
                if(beehiveBlockEntity != null && mode.equals(ProbeMode.EXTENDED)){
                    Tier1BeehiveBlockEntity ironBeeHive = (Tier1BeehiveBlockEntity) world.getTileEntity(data.getPos());
                    if (ironBeeHive != null && ironBeeHive.hasCombs()) {
                        int honeyLevel = 0;
                        if (ironBeeHive.getBlockState().has(Tier1BeehiveBlock.HONEY_LEVEL))
                            honeyLevel = ironBeeHive.getBlockState().get(Tier1BeehiveBlock.HONEY_LEVEL);
                        IProbeInfo vertical;
                        IProbeInfo horizontal;
                        probeInfo.horizontal()
                                .item(new ItemStack(blockState.getBlock().asItem()))
                                .vertical()
                                .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                                .text(new TranslationTextComponent("gui.resourcefulbees.beehive.smoked").getFormattedText() + beehiveBlockEntity.isSmoked)
                                .text(new TranslationTextComponent("gui.resourcefulbees.beehive.honeylevel").getFormattedText() + honeyLevel)
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

                if(beeHiveBlock != null && beeHiveBlock.time > 0) {
                    probeInfo.horizontal()
                            .item(new ItemStack(blockState.getBlock().asItem()))
                            .vertical()
                            .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                            .progress((int) Math.floor(beeHiveBlock.time / 20.0), beeHiveBlock.totalTime / 20)
                            .text(formatting + BeeConst.MOD_NAME);
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
                            .text(formatting + BeeConst.MOD_NAME);
                    return true;
                }
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


}