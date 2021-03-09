package com.resourcefulbees.resourcefulbees.utils;

import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class BeepediaUtils {
    
    @OnlyIn(Dist.CLIENT)
    public static void loadBeepedia(){
        Minecraft.getInstance().displayGuiScreen(new BeepediaScreen( null));
    }

    @OnlyIn(Dist.CLIENT)
    public static void loadBeepedia(Entity entity){
        Minecraft.getInstance().displayGuiScreen(new BeepediaScreen( ((CustomBeeEntity) entity).getBeeType()));
    }
}
