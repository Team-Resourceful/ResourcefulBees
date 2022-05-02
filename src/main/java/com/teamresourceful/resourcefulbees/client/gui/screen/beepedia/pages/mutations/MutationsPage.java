package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.mutations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.BeepediaScreen;
import com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.pages.BeePage;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.enums.MutationType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public abstract class MutationsPage {
    final int mutationCount;
    protected final BeepediaScreen beepedia;
    final Entity entityParent;
    final MutationType type;
    final BeePage parent;
    protected int inputCounter;
    protected int outputCounter;
    protected final ResourceLocation infoIcon = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/jei/icons.png");
    protected final ResourceLocation mutationImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/mutate.png");
    protected final ResourceLocation mutationChanceImage = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/mutation_sparkles.png");


    protected MutationsPage(Entity entityParent, BeePage parent, MutationType type, int mutationCount, BeepediaScreen beepedia) {
        this.entityParent = entityParent;
        this.parent = parent;
        this.type = type;
        this.mutationCount = mutationCount;
        this.beepedia = beepedia;
    }

    public abstract void tick(int ticksActive);

    public void draw(PoseStack matrix, int xPos, int yPos) {
//        FontRenderer font = Minecraft.getInstance().font;
//        StringTextComponent mutationCountString = new StringTextComponent("x " + mutationCount);
//        font.draw(matrix, mutationCountString.withStyle(TextFormatting.GRAY), (float) xPos + 20, (float) yPos - 5, -1);
//        Minecraft.getInstance().getTextureManager().bind(mutationImage);
//        AbstractGui.blit(matrix, xPos, yPos, 0, 0, 169, 84, 169, 84);
//        Minecraft.getInstance().getTextureManager().bind(mutationChanceImage);
//        AbstractGui.blit(matrix, xPos, yPos - 9, 0, 0, 16, 16, 16, 16);
//        RenderUtils.renderEntity(matrix, entityParent, beepedia.getMinecraft().level, (float) xPos + ((float) SUB_PAGE_WIDTH / 2) - 15, (float) yPos + 6, 45, 1.25f);
    }

    public boolean mouseClick(int xPos, int yPos, int mouseX, int mouseY) {
//        if (entityParent instanceof CustomBeeEntity) {
//            CustomBeeEntity beeEntity = (CustomBeeEntity) entityParent;
//            if (BeepediaScreen.mouseHovering((float) xPos + ((float) SUB_PAGE_WIDTH / 2) - 20, (float) yPos + 6, 30, 30, mouseX, mouseY)) {
//                if (BeepediaScreen.currScreenState.getPageID().equals((beeEntity.getCoreData().getName())))
//                    return false;
//                BeepediaScreen.saveScreenState();
//                beepedia.setActive(BeepediaScreen.PageType.BEE, beeEntity.getCoreData().getName());
//                return true;
//            }
//        }
        return false;
    }

    public void drawTooltips(PoseStack matrix, int xPos, int yPos, int mouseX, int mouseY) {
//        if (BeepediaScreen.mouseHovering((float) xPos + ((float) SUB_PAGE_WIDTH / 2) - 20, (float) yPos + 6, 30, 30, mouseX, mouseY)) {
//            List<ITextComponent> tooltip = new ArrayList<>();
//            IFormattableTextComponent name = entityParent.getName().plainCopy();
//            IFormattableTextComponent id = new StringTextComponent(entityParent.getEncodeId()).withStyle(TextFormatting.DARK_GRAY);
//            tooltip.add(name);
//            tooltip.add(id);
//            beepedia.renderComponentTooltip(matrix, tooltip, mouseX, mouseY);
//        }
//        if (BeepediaScreen.mouseHovering(xPos, yPos - 9, 16, 16, mouseX, mouseY)) {
//            TranslationTextComponent text = new TranslationTextComponent("gui.resourcefulbees.beepedia.bee_subtab.mutations.mutation_count.tooltip");
//            beepedia.renderTooltip(matrix, text, mouseX, mouseY);
//        }
    }

    protected void drawWeight(PoseStack matrix, Double right, int xPos, int yPos) {
        Font font = Minecraft.getInstance().font;
        TextComponent text = new TextComponent(ModConstants.PERCENT_FORMAT.format(right));
        int padding = font.width(text) / 2;
        font.draw(matrix, text.withStyle(ChatFormatting.GRAY), (float) xPos - padding, yPos, -1);
    }

    protected void drawChance(PoseStack matrix, Double right, int xPos, int yPos) {
        if (right >= 1) return;
        drawWeight(matrix, right, xPos, yPos);
    }


    public abstract void addSearch();
}
