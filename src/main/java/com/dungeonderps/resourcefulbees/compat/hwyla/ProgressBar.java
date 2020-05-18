package com.dungeonderps.resourcefulbees.compat.hwyla;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import mcp.mobius.waila.api.ICommonAccessor;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.tooltiprenderers.TooltipRendererProgressBar;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ProgressBar extends TooltipRendererProgressBar {

    private static final ResourceLocation SPRITE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/hwyla/progress_bar.png");

    public ProgressBar() {
        super();
    }

    @Override
    public Dimension getSize(CompoundNBT tag, ICommonAccessor accessor) {
        return new Dimension(59, 11);
    }

    @Override
    public void draw(CompoundNBT tag, ICommonAccessor accessor, int x, int y) {
        int currentValue = tag.getInt("progress");
        Minecraft.getInstance().getTextureManager().bindTexture(SPRITE);
        DisplayUtil.drawTexturedModalRect(x + 2, y, 2, 11, 55, 7, 55, 7);
        int maxValue = tag.getInt("total");
        if (maxValue > 0) {
            int progress = currentValue * 55 / maxValue;
            DisplayUtil.drawTexturedModalRect(x + 2, y, 2, 2, progress +1, 7, progress +1, 7);
        }

    }
}
