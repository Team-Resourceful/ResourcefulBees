package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.compat.jei.BiomeParser;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class SpawningPage extends BeeDataPage {

    private int subScrollPos;
    private int scrollHeight;

    List<ResourceLocation> biomeList;

    private Button biomes;
    private Button back;

    public SpawningPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        this.beeData = beeData;
        biomes = new Button(xPos + subPageWidth - 50, yPos, 46, 20, new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.biomes_button"), onPress -> {
            beepedia.setBiomesOpen(true);
            back.visible = beepedia.isBiomesOpen();
            biomes.visible = !beepedia.isBiomesOpen();
        });
        back = new Button(xPos + subPageWidth - 50, yPos, 46, 20, new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.back_button"), onPress -> {
            beepedia.setBiomesOpen(false);
            back.visible = beepedia.isBiomesOpen();
            biomes.visible = !beepedia.isBiomesOpen();
        });
        beepedia.addButton(back);
        beepedia.addButton(biomes);
        back.visible = false;
        biomes.visible = false;
        biomeList = BiomeParser.getBiomes(beeData);
        scrollHeight = biomeList.size() * 12;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        if (beepedia.isBiomesOpen()) {
            TranslationTextComponent biomesTitle = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.biomes");

            FontRenderer font = Minecraft.getInstance().fontRenderer;
            font.draw(matrix, biomesTitle, xPos, yPos + 8, Color.parse("white").getRgb());
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            double scale = beepedia.getMinecraft().getWindow().getGuiScaleFactor();
            int scissorY = (int) (beepedia.getMinecraft().getWindow().getFramebufferHeight() - (yPos + subPageHeight) * scale);
            GL11.glScissor((int) (xPos * scale), scissorY, (int) (subPageWidth * scale), (int) ((subPageHeight - 22) * scale));
            for (int i = 0; i < biomeList.size(); i++) {
                TranslationTextComponent text = new TranslationTextComponent(String.format("biome.%s.%s", biomeList.get(i).getNamespace(), biomeList.get(i).getPath()));
                font.draw(matrix, text, xPos, yPos + 22 + subScrollPos + i * 12, Color.parse("gray").getRgb());
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            FontRenderer font = beepedia.getMinecraft().fontRenderer;
            TranslationTextComponent groupName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.group");
            TranslationTextComponent heightName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.height");
            TranslationTextComponent weightName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.weight");
            TranslationTextComponent lightName = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.spawning.light");
            groupName.append(new StringTextComponent(String.format("%d - %d", beeData.getSpawnData().getMinGroupSize(), beeData.getSpawnData().getMaxGroupSize())));
            heightName.append(new StringTextComponent(String.format("%d - %d", beeData.getSpawnData().getMinYLevel(), beeData.getSpawnData().getMaxYLevel())));
            weightName.append(new StringTextComponent(String.format("%d", beeData.getSpawnData().getSpawnWeight())));
            lightName.append(BeeInfoUtils.getLightName(beeData.getSpawnData().getLightLevel()));
            font.draw(matrix, groupName, xPos, yPos + 6, Color.parse("white").getRgb());
            font.draw(matrix, heightName, xPos, yPos + 18, Color.parse("white").getRgb());
            font.draw(matrix, weightName, xPos, yPos + 30, Color.parse("white").getRgb());
            font.draw(matrix, lightName, xPos, yPos + 42, Color.parse("white").getRgb());
        }
    }

    @Override
    public void renderForeground(MatrixStack matrix, int mouseX, int mouseY) {

    }

    @Override
    public void tick(int ticksActive) {

    }

    @Override
    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (!beepedia.isBiomesOpen()) return false;
        if (mouseX >= xPos && mouseY >= yPos + 22 && mouseX <= xPos + subPageWidth && mouseY <= yPos + subPageHeight) {
            int boxHeight = subPageHeight - 22;
            if (boxHeight > scrollHeight) {
                return true;
            }
            subScrollPos += scrollAmount * 8;
            if (subScrollPos > 0) subScrollPos = 0;
            else if (subScrollPos < -(scrollHeight - boxHeight)) {
                subScrollPos = -(scrollHeight - boxHeight);
            }
            beepedia.setSpawningScroll(subScrollPos);
            return true;
        }
        return false;
    }

    @Override
    public void openPage() {
        super.openPage();
        back.visible = beepedia.isBiomesOpen();
        biomes.visible = !beepedia.isBiomesOpen();
        subScrollPos = beepedia.getSpawningScroll();
        int boxHeight = subPageHeight - 22;
        if (boxHeight > scrollHeight) {
            beepedia.setSpawningScroll(0);
            subScrollPos = 0;
        }
    }

    @Override
    public void closePage() {
        back.visible = false;
        biomes.visible = false;
        super.closePage();
    }
}
