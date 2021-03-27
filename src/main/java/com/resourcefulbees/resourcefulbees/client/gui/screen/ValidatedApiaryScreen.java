package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.container.ValidatedApiaryContainer;
import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.ApiaryTabMessage;
import com.resourcefulbees.resourcefulbees.network.packets.ExportBeeMessage;
import com.resourcefulbees.resourcefulbees.network.packets.ImportBeeMessage;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
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
import net.minecraft.util.text.StringTextComponent;

import org.jetbrains.annotations.NotNull;
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
    private TabImageButton breedTabButton;
    private TabImageButton storageTabButton;

    public ValidatedApiaryScreen(ValidatedApiaryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageWidth = 250;
        this.imageHeight = 152;
    }

    @Override
    protected void init() {
        super.init();

        apiaryTileEntity = this.menu.getApiaryTileEntity();

        importButton = this.addButton(new Button(this.leftPos + 73, this.topPos + 10, 40, 20, new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.button.import")), onPress -> this.importBee()));
        exportButton = this.addButton(new Button(this.leftPos + 159, this.topPos + 10, 40, 20, new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.button.export")), onPress -> this.exportSelectedBee()));

        addTabButtons();


    }

    private void addTabButtons() {
        int i = this.leftPos;
        int j = this.topPos;
        int t = i + this.imageWidth - 25;

        this.addButton(new TabImageButton(t+1, j+17, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(ModItems.BEE_JAR.get()), 1, 1,
                onPress -> this.changeScreen(ApiaryTabs.MAIN), 128, 128) {

            @Override
            public void renderToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
                StringTextComponent s = new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.button.main_screen"));
                ValidatedApiaryScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        }).active = false;

        storageTabButton = this.addButton(new TabImageButton(t + 1, j + 37, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(net.minecraft.item.Items.HONEYCOMB),2, 1,
                onPress -> this.changeScreen(ApiaryTabs.STORAGE), 128, 128) {

            @Override
            public void renderToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
                StringTextComponent s = new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.button.storage_screen"));
                ValidatedApiaryScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });

        breedTabButton = this.addButton(new TabImageButton(t + 1, j + 57, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(ModItems.GOLD_FLOWER_ITEM.get()), 1, 1,
                onPress -> this.changeScreen(ApiaryTabs.BREED), 128, 128) {

            @Override
            public void renderToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
                StringTextComponent s = new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.button.breed_screen"));
                ValidatedApiaryScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });
    }

    private void changeScreen(ApiaryTabs tab) {
        switch (tab) {
            case BREED:
                if (breedTabButton.active)
                    NetPacketHandler.sendToServer(new ApiaryTabMessage(apiaryTileEntity.getBlockPos(), ApiaryTabs.BREED));
                break;
            case STORAGE:
                if (storageTabButton.active)
                    NetPacketHandler.sendToServer(new ApiaryTabMessage(apiaryTileEntity.getBlockPos(), ApiaryTabs.STORAGE));
                break;
            case MAIN:
        }
    }

    private void exportSelectedBee() {
        if (apiaryTileEntity.getBeeCount() != 0) {
            NetPacketHandler.sendToServer(new ExportBeeMessage(this.menu.getPos(), this.menu.getBeeList()[this.menu.getSelectedBee()]));
            //beeIndexOffset--;
            // TODO this causes Array Out of Bounds exception.
            //  Not having it causes GUI to not update correctly when last bee in list is exported.
            //  Figure out how to make list update correctly when last bee is exported without this error.
        }
    }

    private void importBee() {
        NetPacketHandler.sendToServer(new ImportBeeMessage(this.menu.getPos()));
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrix, mouseX, mouseY);

        int l = this.leftPos + 5;
        int i1 = this.topPos + 18;
        int j1 = this.beeIndexOffset + 7;

        renderBeeToolTip(matrix, mouseX, mouseY, l, i1, j1);
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null) {
            if (this.menu.getSelectedBee() > apiaryTileEntity.getBeeCount() - 1) {
                this.menu.selectBee(apiaryTileEntity.getBeeCount() - 1);
            }
            if (this.menu.getSelectedBee() == -1 && apiaryTileEntity.getBeeCount() > 0) {
                this.menu.selectBee(0);
            }
            exportButton.active = this.menu.getSelectedBee() != -1;
            importButton.active = apiaryTileEntity.getBeeCount() < Config.APIARY_MAX_BEES.get();

            breedTabButton.active = apiaryTileEntity.getApiaryBreeder() != null;
            storageTabButton.active = apiaryTileEntity.getApiaryStorage() != null;

            this.menu.setBeeList(Arrays.copyOf(apiaryTileEntity.bees.keySet().toArray(), apiaryTileEntity.getBeeCount(), String[].class));
            this.minecraft.getTextureManager().bind(VALIDATED_TEXTURE);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            if (!this.canScroll()) {
                this.sliderProgress = 0;
            }
            int k = (int) (99.0F * this.sliderProgress);
            this.blit(matrix, i + 44, j + 18 + k, 54 + (this.canScroll() ? 0 : 6), 152, 6, 27);
            int l = this.leftPos + 5;
            int i1 = this.topPos + 18;
            int j1 = this.beeIndexOffset + 7;
            this.drawRecipesBackground(matrix, mouseX, mouseY, l, i1, j1);
            this.drawBees(l, i1, j1);

            int t = i + this.imageWidth - 25;
            this.minecraft.getTextureManager().bind(TABS_BG);
            blit(matrix, t-1, j + 12, 0,0, 25, 68, 128, 128);
        }
    }

    @Override
    protected void renderLabels(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
        String s = String.format("(%1$s/%2$s) Bees", apiaryTileEntity.getBeeCount(), Config.APIARY_MAX_BEES.get());
        this.font.draw(matrix, s, 4, 7, 0x404040);

        for (Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(matrix, mouseX - this.leftPos, mouseY - this.topPos);
                break;
            }
        }
    }

    private void renderBeeToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18;

            if (mouseX >= left && mouseY >= i1 && mouseX < left + 16 && mouseY < i1 + 18) {
                List<ITextComponent> beeInfo = new ArrayList<>();
                ApiaryTileEntity.ApiaryBee apiaryBee = this.menu.getApiaryBee(i);

                int ticksInHive = apiaryBee.getTicksInHive();
                int minTicks = apiaryBee.minOccupationTicks;
                int ticksLeft = Math.max(minTicks - ticksInHive, 0);
                beeInfo.add(apiaryBee.displayName);
                beeInfo.add(new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.bee.ticks_in_hive") + ": " + ticksInHive));
                beeInfo.add(new StringTextComponent(I18n.get("gui.resourcefulbees.apiary.bee.ticks_left") + ": " + ticksLeft));
                this.renderComponentTooltip(matrix, beeInfo, mouseX, mouseY);
            }
        }
    }

    private void drawRecipesBackground(@NotNull MatrixStack matrix, int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {

        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int k = left;
            int i1 = top + j * 18;
            int j1 = this.imageHeight;
            if (i == this.menu.getSelectedBee()) {
                j1 += 18;
            } else if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
                j1 += 36;
            }
            this.blit(matrix, k, i1, 0, j1, 18, 18);
            int l1 = 18;
            k = k + 18;
            j1 = this.imageHeight;
            if (this.menu.getApiaryBee(i).isLocked()) {
                l1 += 18;
            }
            if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
                j1 += 18;
            }

            this.blit(matrix, k, i1, l1, j1, 18, 18);
        }

    }

    private void drawBees(int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18 + 2;
            ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
            CompoundNBT data = new CompoundNBT();

            CompoundNBT tag = this.menu.getApiaryBee(i).entityData;
            String entityID = tag.getString("id");

            data.putString(NBTConstants.NBT_ENTITY, entityID);
            data.putString(NBTConstants.NBT_COLOR, this.menu.getApiaryBee(i).beeColor);

            beeJar.setTag(data);
            if (this.minecraft != null)
                this.minecraft.getItemRenderer().renderAndDecorateItem(beeJar, left, i1);
        }
    }

    private boolean canScroll() {
        return apiaryTileEntity.getBeeCount() > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (this.canScroll()) {
            int i = this.getHiddenRows();
            this.sliderProgress = (float) (this.sliderProgress - scrollAmount / i);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ((this.sliderProgress * i) + 0.5D);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int pMouseDragged5, double pMouseDragged6, double pMouseDragged8) {
        if (this.clickedOnScroll && this.canScroll()) {
            int i = this.topPos + 14;
            int j = i + 101;
            this.sliderProgress = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ((double) (this.sliderProgress * (float) this.getHiddenRows()) + 0.5D);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, pMouseDragged5, pMouseDragged6, pMouseDragged8);
        }
    }

    private int getHiddenRows() { return apiaryTileEntity.getBeeCount() - 7; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pMouseClicked5) {
        this.clickedOnScroll = false;
        if (apiaryTileEntity.getBeeCount() > 0) {
            int i = this.leftPos + 5;
            int j = this.topPos + 18;
            int k = this.beeIndexOffset + 7;

            for (int l = this.beeIndexOffset; l < k; ++l) {
                int i1 = l - this.beeIndexOffset;
                double d0 = mouseX - (double) (i);
                double d1 = mouseY - (double) (j + i1 * 18);
                if (d0 >= 0.0D && d1 >= 0.0D && d0 < 17.0D && d1 < 17.0D && this.menu.selectBee(l)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }

                if (d0 >= 18.0D && d1 >= 0.0D && d0 < 35.0D && d1 < 17.0D && this.menu.lockOrUnlockBee(l)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }

            i = this.leftPos + 44;
            j = this.topPos + 18;
            if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 101)) {
                this.clickedOnScroll = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, pMouseClicked5);
    }
}
