package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import com.resourcefulbees.resourcefulbees.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class EntityMutationPage extends MutationsPage {

    Entity input;
    List<Pair<Double, EntityOutput>> outputs = new ArrayList<>();
    private Double outputChance;

    public EntityMutationPage(EntityType<?> entity, Pair<Double, RandomCollection<EntityOutput>> outputs, MutationTypes type, CustomBeeData beeData, BeepediaScreen beepedia) {
        super(type, beeData, beepedia);
        input = entity.create(beepedia.getMinecraft().world);
        initOutputs(outputs);
    }

    private void initOutputs(Pair<Double, RandomCollection<EntityOutput>> outputs) {
        outputChance = outputs.getKey();
        outputs.getRight().getMap().forEach((b, m) -> this.outputs.add(Pair.of(b, m)));
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !BeeInfoUtils.isShiftPressed()) {
            outputCounter++;
            if (outputCounter >= outputs.size()) outputCounter = 0;
        }
    }

    @Override
    public void draw(MatrixStack matrix, int xPos, int yPos) {
        RenderUtils.renderEntity(matrix, input, beepedia.getMinecraft().world, xPos, yPos, 45, 1);
        RenderUtils.renderEntity(matrix, outputs.get(outputCounter).getRight().getGuiEntity(beepedia.getMinecraft().world), beepedia.getMinecraft().world, (float) xPos + 40, (float) yPos, -45, 1);
    }

    @Override
    public boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY) {
        return false;
    }

    @Override
    public void drawTooltips(MatrixStack matrix, int mouseX, int mouseY) {

    }
}
