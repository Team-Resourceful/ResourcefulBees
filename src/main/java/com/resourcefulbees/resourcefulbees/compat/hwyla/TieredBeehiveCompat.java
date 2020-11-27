package com.resourcefulbees.resourcefulbees.compat.hwyla;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.block.TieredBeehiveBlock;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.tileentity.TieredBeehiveTileEntity;
import mcp.mobius.waila.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class TieredBeehiveCompat implements IComponentProvider, IServerDataProvider<TileEntity> {

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        CompoundNBT nbt = accessor.getServerData();
        if (accessor.getPlayer().isSneaking()) {
            if (nbt.contains("HoneyLevel")) {
                int honeyLevel = nbt.getInt("HoneyLevel");
                tooltip.add(new StringTextComponent(I18n.format("gui." + ResourcefulBees.MOD_ID + ".beehive.honeylevel") + honeyLevel));
            }
            if (nbt.contains("MaxCombs") && nbt.contains("NumCombs")) {
                int maxCombs = nbt.getInt("MaxCombs");
                int numCombs = nbt.getInt("NumCombs");
                tooltip.add(new StringTextComponent(numCombs + " / " + maxCombs + " " + I18n.format("gui." + ResourcefulBees.MOD_ID + ".beehive.num_combs")));
            }
            if (nbt.contains(NBTConstants.NBT_SMOKED_TE)) {
                tooltip.add(new TranslationTextComponent("gui." + ResourcefulBees.MOD_ID + ".beehive.smoked"));

                CompoundNBT progress = new CompoundNBT();
                progress.putInt("progress", nbt.getInt("TicksSmoked"));
                progress.putInt("total", BeeConstants.SMOKE_TIME);

                RenderableTextComponent renderableTextComponent = new RenderableTextComponent(HwylaCompat.BEEHIVE_SMOKER_PROGRESS, progress);
                tooltip.add(renderableTextComponent);
            }
        }
    }

    @Override
    public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity serverPlayerEntity, World world, TileEntity tileEntity) {
        if (tileEntity instanceof TieredBeehiveTileEntity){
            TieredBeehiveTileEntity blockEntity = (TieredBeehiveTileEntity) tileEntity;

            if (blockEntity.isSmoked) {
                compoundNBT.putBoolean(NBTConstants.NBT_SMOKED_TE, true);
                compoundNBT.putInt("TicksSmoked", blockEntity.ticksSmoked);
            }
            BlockState state = blockEntity.getBlockState();
            if (state.has(TieredBeehiveBlock.HONEY_LEVEL)) {
                compoundNBT.putInt("HoneyLevel", state.get(TieredBeehiveBlock.HONEY_LEVEL));
            }

            if (blockEntity.hasCombs()) {
                compoundNBT.putInt("MaxCombs", blockEntity.getMaxCombs());
                compoundNBT.putInt("NumCombs", blockEntity.numberOfCombs());
            }
        }
    }
}