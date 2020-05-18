package com.dungeonderps.resourcefulbees.compat.hwyla;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.block.IronBeehiveBlock;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import mcp.mobius.waila.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.IronBeehiveBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class IronBeehiveCompat implements IComponentProvider, IServerDataProvider<TileEntity> {

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        CompoundNBT nbt = accessor.getServerData();
        if (accessor.getPlayer().isSneaking()) {
            if (nbt.contains("HoneyLevel")) {
                int honeyLevel = nbt.getInt("HoneyLevel");
                tooltip.add(new TranslationTextComponent("gui." + ResourcefulBees.MOD_ID + ".beehive.honeylevel", honeyLevel));
            }
            if (nbt.contains(BeeConst.NBT_SMOKED_TE)) {
                tooltip.add(new TranslationTextComponent("gui." + ResourcefulBees.MOD_ID + ".beehive.smoked"));

                CompoundNBT progress = new CompoundNBT();
                progress.putInt("progress", nbt.getInt("TicksSmoked"));
                progress.putInt("total", BeeConst.SMOKE_TIME);

                RenderableTextComponent renderableTextComponent = new RenderableTextComponent(HwylaCompat.BEEHIVE_SMOKER_PROGRESS, progress);
                tooltip.add(renderableTextComponent);
            }
        }
    }

    @Override
    public void appendServerData(CompoundNBT compoundNBT, ServerPlayerEntity serverPlayerEntity, World world, TileEntity tileEntity) {
        if (tileEntity instanceof IronBeehiveBlockEntity){
            IronBeehiveBlockEntity blockEntity = (IronBeehiveBlockEntity) tileEntity;

            if (blockEntity.isSmoked) {
                compoundNBT.putBoolean(BeeConst.NBT_SMOKED_TE, true);
                compoundNBT.putInt("TicksSmoked", blockEntity.ticksSmoked);
            }
            BlockState state = blockEntity.getBlockState();
            if (state.has(IronBeehiveBlock.HONEY_LEVEL)) {
                compoundNBT.putInt("HoneyLevel", state.get(IronBeehiveBlock.HONEY_LEVEL));
            }
        }
    }
}