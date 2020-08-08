package com.dungeonderps.resourcefulbees.client.gui.screen;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.client.gui.widget.TabImageButton;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.container.ValidatedApiaryContainer;
import com.dungeonderps.resourcefulbees.lib.ApiaryTabs;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import com.dungeonderps.resourcefulbees.network.NetPacketHandler;
import com.dungeonderps.resourcefulbees.network.packets.ApiaryTabMessage;
import com.dungeonderps.resourcefulbees.network.packets.ExportBeeMessage;
import com.dungeonderps.resourcefulbees.network.packets.ImportBeeMessage;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.tileentity.ApiaryTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidatedApiaryScreen extends ContainerScreen<ValidatedApiaryContainer> {

    private static final ResourceLocation VALIDATED_TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/validated.png");
    private static final ResourceLocation TABS_BG = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_gui_tabs.png");

    private int beeIndexOffset;

    private float sliderProgress;
    private boolean clickedOnScroll;

    private ApiaryTileEntity apiaryTileEntity;

    private Button importButton;
    private Button exportButton;

    public ValidatedApiaryScreen(ValidatedApiaryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 250;
        this.ySize = 152;
    }

    @Override
    protected void init() {
        super.init();

        apiaryTileEntity = this.container.apiaryTileEntity;

        importButton = this.addButton(new Button(this.guiLeft + 73, this.guiTop + 10, 40, 20, I18n.format("gui.resourcefulbees.apiary.button.import"), (onPress) -> this.importBee()));
        exportButton = this.addButton(new Button(this.guiLeft + 159, this.guiTop + 10, 40, 20, I18n.format("gui.resourcefulbees.apiary.button.export"), (onPress) -> this.exportSelectedBee()));

        addTabButtons();
    }

    private void addTabButtons() {
        int i = this.guiLeft;
        int j = this.guiTop;
        int t = i + this.xSize - 25;

        this.addButton(new TabImageButton(t+2, j+17, 18, 18, 74, 0, 18, TABS_BG,
                (onPress) -> this.changeScreen(ApiaryTabs.MAIN)) {
            public void renderToolTip(int mouseX, int mouseY) {
                String s = I18n.format("gui.resourcefulbees.apiary.button.main_screen");
                ValidatedApiaryScreen.this.renderTooltip(s, mouseX, mouseY);
            }
        }).active = false;

        this.addButton(new TabImageButton(t + 2, j + 37, 18, 18, 110, 0, 18, TABS_BG,
                (onPress) -> this.changeScreen(ApiaryTabs.STORAGE)) {
            public void renderToolTip(int mouseX, int mouseY) {
                String s = I18n.format("gui.resourcefulbees.apiary.button.storage_screen");
                ValidatedApiaryScreen.this.renderTooltip(s, mouseX, mouseY);
            }
        });

        this.addButton(new TabImageButton(t + 2, j + 57, 18, 18, 92, 0, 18, TABS_BG,
                (onPress) -> this.changeScreen(ApiaryTabs.BREED)) {
            public void renderToolTip(int mouseX, int mouseY) {
                String s = I18n.format("gui.resourcefulbees.apiary.button.breed_screen");
                ValidatedApiaryScreen.this.renderTooltip(s, mouseX, mouseY);
            }
        }).active = false;
    }

    private void changeScreen(ApiaryTabs tab) {
        switch (tab) {
            case BREED:
                break;
            case STORAGE:
                NetPacketHandler.sendToServer(new ApiaryTabMessage(apiaryTileEntity.getPos(), ApiaryTabs.STORAGE));
                break;
            case MAIN:
        }
    }

    private void exportSelectedBee() {
        if (apiaryTileEntity.getBeeCount() != 0)
            NetPacketHandler.sendToServer(new ExportBeeMessage(this.container.pos, this.container.beeList[this.container.getSelectedBee()]));
    }

    private void importBee() {
        NetPacketHandler.sendToServer(new ImportBeeMessage(this.container.pos));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int l = this.guiLeft + 5;
        int i1 = this.guiTop + 18;
        int j1 = this.beeIndexOffset + 7;

        renderBeeToolTip(mouseX, mouseY, l, i1, j1);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            if (this.container.getSelectedBee() > apiaryTileEntity.getBeeCount() - 1) {
                this.container.selectBee(apiaryTileEntity.getBeeCount() - 1);
            }
            if (this.container.getSelectedBee() == -1 && apiaryTileEntity.getBeeCount() > 0) {
                this.container.selectBee(0);
            }
            exportButton.active = this.container.getSelectedBee() != -1;
            importButton.active = apiaryTileEntity.getBeeCount() < Config.APIARY_MAX_BEES.get();

            this.container.beeList = Arrays.copyOf(apiaryTileEntity.BEES.keySet().toArray(), apiaryTileEntity.getBeeCount(), String[].class);
            this.minecraft.getTextureManager().bindTexture(VALIDATED_TEXTURE);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
            if (!this.canScroll()) {
                this.sliderProgress = 0;
            }
            int k = (int) (99.0F * this.sliderProgress);
            this.blit(i + 44, j + 18 + k, 54 + (this.canScroll() ? 0 : 6), 152, 6, 27);
            int l = this.guiLeft + 5;
            int i1 = this.guiTop + 18;
            int j1 = this.beeIndexOffset + 7;
            this.drawRecipesBackground(mouseX, mouseY, l, i1, j1);
            this.drawRecipesItems(l, i1, j1);

            int t = i + this.xSize - 25;
            this.minecraft.getTextureManager().bindTexture(TABS_BG);
            blit(t, j + 12, 0,0, 25, 68, 128, 128);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = String.format("(%1$s/%2$s) Bees", apiaryTileEntity.getBeeCount(), Config.APIARY_MAX_BEES.get());
        this.font.drawString(s, 4, 7, 0x404040);

        for (Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(mouseX - this.guiLeft, mouseY - this.guiTop);
                break;
            }
        }
    }

    private void renderBeeToolTip(int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18;

            if (mouseX >= left && mouseY >= i1 && mouseX < left + 16 && mouseY < i1 + 18) {
                List<String> beeInfo = new ArrayList<>();
                String beeType = apiaryTileEntity.BEES.get(this.container.beeList[i]).beeType;
                int ticksInHive = apiaryTileEntity.BEES.get(beeType).ticksInHive;
                int minTicks = apiaryTileEntity.BEES.get(beeType).minOccupationTicks;
                int ticksLeft = Math.max(minTicks - ticksInHive, 0);
                String s = String.format("entity.resourcefulbees.%1$s_bee", beeType);
                beeInfo.add(I18n.format(s));
                beeInfo.add(I18n.format("gui.resourcefulbees.apiary.bee.ticks_in_hive") + ": " + ticksInHive);
                beeInfo.add(I18n.format("gui.resourcefulbees.apiary.bee.ticks_left") + ": " + ticksLeft);
                this.renderTooltip(beeInfo, mouseX, mouseY);
            }
        }
    }

    private void drawRecipesBackground(int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {

        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int k = left;
            int i1 = top + j * 18;
            int j1 = this.ySize;
            if (i == this.container.getSelectedBee()) {
                j1 += 18;
            } else if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
                j1 += 36;
            }
            this.blit(k, i1, 0, j1, 18, 18);
            int l1 = 18;
            k = k + 18;
            j1 = this.ySize;
            if (apiaryTileEntity.BEES.get(this.container.beeList[i]).isLocked) {
                l1 += 18;
            }
            if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
                j1 += 18;
            }

            this.blit(k, i1, l1, j1, 18, 18);
        }

    }

    private void drawRecipesItems(int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18 + 2;
            ItemStack beeJar = new ItemStack(RegistryHandler.BEE_JAR.get());
            CompoundNBT data = new CompoundNBT();
            data.putString(BeeConstants.NBT_ENTITY, "resourcefulbees:bee");
            data.putString(BeeConstants.NBT_BEE_TYPE, this.container.beeList[i]);
            data.putString(BeeConstants.NBT_COLOR, BeeInfo.getInfo(this.container.beeList[i]).getPrimaryColor());
            beeJar.setTag(data);
            if (this.minecraft != null)
                this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(beeJar, left, i1);
        }
    }

    private boolean canScroll() {
        return apiaryTileEntity.getBeeCount() > 7;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (this.canScroll()) {
            int i = this.getHiddenRows();
            this.sliderProgress = (float) (this.sliderProgress - scrollAmount / i);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ((this.sliderProgress * i) + 0.5D);
        }

        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (this.clickedOnScroll && this.canScroll()) {
            int i = this.guiTop + 14;
            int j = i + 101;
            this.sliderProgress = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ((double) (this.sliderProgress * (float) this.getHiddenRows()) + 0.5D);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
        }
    }

    private int getHiddenRows() {
        return apiaryTileEntity.getBeeCount() - 7;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int p_mouseClicked_5_) {
        this.clickedOnScroll = false;
        if (apiaryTileEntity.getBeeCount() > 0) {
            int i = this.guiLeft + 5;
            int j = this.guiTop + 18;
            int k = this.beeIndexOffset + 7;

            for (int l = this.beeIndexOffset; l < k; ++l) {
                int i1 = l - this.beeIndexOffset;
                double d0 = mouseX - (double) (i);
                double d1 = mouseY - (double) (j + i1 * 18);
                if (d0 >= 0.0D && d1 >= 0.0D && d0 < 17.0D && d1 < 17.0D && this.container.selectBee(l)) {
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }

                if (d0 >= 18.0D && d1 >= 0.0D && d0 < 35.0D && d1 < 17.0D && this.container.lockOrUnlockBee(l)) {
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }

            i = this.guiLeft + 44;
            j = this.guiTop + 18;
            if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 101)) {
                this.clickedOnScroll = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, p_mouseClicked_5_);
    }
}
