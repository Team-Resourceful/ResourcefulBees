package com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;


public class HoneycombPage extends BeeDataPage {

    Button centrifuge;
    Button honeycombs;
    private int counter;
    private int max;

    private ItemStack hiveOutput;
    private ItemStack apiary1Output;
    private ItemStack apiary2Output;
    private ItemStack apiary3Output;
    private ItemStack apiary4Output;

    List<Item> hives = BeeInfoUtils.getItemTag("minecraft:beehives").values();
    ItemStack apiary1 = new ItemStack(ModItems.T1_APIARY_ITEM.get());
    ItemStack apiary2 = new ItemStack(ModItems.T2_APIARY_ITEM.get());
    ItemStack apiary3 = new ItemStack(ModItems.T3_APIARY_ITEM.get());
    ItemStack apiary4 = new ItemStack(ModItems.T4_APIARY_ITEM.get());

    ResourceLocation honeycombsImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/honeycombs.png");

    public HoneycombPage(BeepediaScreen beepedia, CustomBeeData beeData, int xPos, int yPos, BeePage parent) {
        super(beepedia, beeData, xPos, yPos, parent);
        centrifuge = new Button(xPos + subPageWidth - 74, yPos, 70, 20, new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.honeycombs.centrifuge_button"), onPress -> {
            BeepediaScreen.currScreenState.setCentrifugeOpen(true);
            honeycombs.visible = BeepediaScreen.currScreenState.isCentrifugeOpen();
            centrifuge.visible = !BeepediaScreen.currScreenState.isCentrifugeOpen();
        });
        honeycombs = new Button(xPos + subPageWidth - 74, yPos, 70, 20, new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.honeycombs.honeycombs_button"), onPress -> {
            BeepediaScreen.currScreenState.setCentrifugeOpen(false);
            honeycombs.visible = BeepediaScreen.currScreenState.isCentrifugeOpen();
            centrifuge.visible = !BeepediaScreen.currScreenState.isCentrifugeOpen();
        });
        beepedia.addButton(honeycombs);
        beepedia.addButton(centrifuge);
        honeycombs.visible = false;
        centrifuge.visible = false;
        counter = 0;
        max = hives.size();

        int[] apiaryAmounts = beeData.getApiaryOutputAmounts();
        if (apiaryAmounts == null) apiaryAmounts = new int[]{Config.T1_APIARY_QUANTITY.get(), Config.T2_APIARY_QUANTITY.get(), Config.T3_APIARY_QUANTITY.get(), Config.T4_APIARY_QUANTITY.get()};
        hiveOutput = new ItemStack(beeData.getCombRegistryObject().get(), 1);
        apiary1Output = new ItemStack(beeData.getCombRegistryObject().get(), apiaryAmounts[0]);
        apiary2Output = new ItemStack(beeData.getCombRegistryObject().get(), apiaryAmounts[1]);
        apiary3Output = new ItemStack(beeData.getCombBlockItemRegistryObject().get(), apiaryAmounts[2]);
        apiary4Output = new ItemStack(beeData.getCombBlockItemRegistryObject().get(), apiaryAmounts[3]);
    }

    @Override
    public void openPage() {
        super.openPage();
        honeycombs.visible = BeepediaScreen.currScreenState.isCentrifugeOpen();
        centrifuge.visible = !BeepediaScreen.currScreenState.isCentrifugeOpen();
    }

    @Override
    public void closePage() {
        super.closePage();
        honeycombs.visible = false;
        centrifuge.visible = false;
    }

    @Override
    public void renderBackground(MatrixStack matrix, float partialTick, int mouseX, int mouseY) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        String title = BeepediaScreen.currScreenState.isCentrifugeOpen() ? "gui.resourcefulbees.beepedia.bee_subtab.centrifuge" : "gui.resourcefulbees.beepedia.bee_subtab.honeycombs";
        font.draw(matrix, new TranslationTextComponent(title), xPos, (float) yPos + 8, TextFormatting.WHITE.getColor());
        if (BeepediaScreen.currScreenState.isCentrifugeOpen()) {

        } else {
            manager.bindTexture(honeycombsImage);
            beepedia.drawTexture(matrix, xPos, yPos + 22, 0, 0, 169, 84, 169, 84);
            beepedia.drawSlot(matrix, hives.get(counter), xPos + 14, yPos + 23);
            beepedia.drawSlot(matrix, apiary1, xPos + 43, yPos + 23);
            beepedia.drawSlot(matrix, apiary2, xPos + 72, yPos + 23);
            beepedia.drawSlot(matrix, apiary3, xPos + 101, yPos + 23);
            beepedia.drawSlot(matrix, apiary4, xPos + 130, yPos + 23);

            beepedia.drawSlot(matrix, hiveOutput, xPos + 14, yPos + 82);
            beepedia.drawSlot(matrix, apiary1Output, xPos + 43, yPos + 82);
            beepedia.drawSlot(matrix, apiary2Output, xPos + 72, yPos + 82);
            beepedia.drawSlot(matrix, apiary3Output, xPos + 101, yPos + 82);
            beepedia.drawSlot(matrix, apiary4Output, xPos + 130, yPos + 82);
        }
    }

    @Override
    public void tick(int ticksActive) {
        if (ticksActive % 20 == 0 && !BeeInfoUtils.isShiftPressed()) {
            counter++;
            if (counter >= max) counter = 0;
        }
    }
}
