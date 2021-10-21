package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyTankContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.utils.BeeInfoUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoneyTankTileEntity extends AbstractHoneyTankContainer {

    public static final int BOTTLE_INPUT_FILL = 2;
    public static final int BOTTLE_OUTPUT_FILL = 3;

    private int processingFill = 0;

    public enum TankTier {
        PURPUR(3, 128000, ModBlocks.PURPUR_HONEY_TANK, ModItems.PURPUR_HONEY_TANK_ITEM),
        NETHER(2, 32000, ModBlocks.NETHER_HONEY_TANK, ModItems.NETHER_HONEY_TANK_ITEM),
        WOODEN(1, 8000, ModBlocks.WOODEN_HONEY_TANK, ModItems.WOODEN_HONEY_TANK_ITEM);

        private final RegistryObject<Block> tankBlock;
        private final RegistryObject<Item> tankItem;
        final int maxFillAmount;
        final int tier;

        TankTier(int tier, int maxFillAmount, RegistryObject<Block> tankBlock, RegistryObject<Item> tankItem) {
            this.tier = tier;
            this.maxFillAmount = maxFillAmount;
            this.tankBlock = tankBlock;
            this.tankItem = tankItem;
        }

        public static TankTier getTier(Item item) {
            for (TankTier t : TankTier.values()) {
                if (t.tankItem.get() == item) return t;
            }
            return WOODEN;
        }

        public RegistryObject<Block> getTankBlock() {
            return tankBlock;
        }

        public RegistryObject<Item> getTankItem() {
            return tankItem;
        }

        public int getTier() {
            return tier;
        }

        public int getMaxFillAmount() {
            return maxFillAmount;
        }

        public static TankTier getTier(int tier) {
            for (TankTier t : TankTier.values()) {
                if (t.tier == tier) return t;
            }
            return WOODEN;
        }
    }

    public static final Logger LOGGER = LogManager.getLogger();

    private TankTier tier;

    public HoneyTankTileEntity(TankTier tier) {
        super(ModBlockEntityTypes.HONEY_TANK_TILE_ENTITY.get(), tier.maxFillAmount);
        this.setTier(tier);
        setTileStackHandler(new TileStackHandler(4, getAcceptor(), getRemover()));
    }

    public TankTier getTier() {
        return tier;
    }

    public void setTier(TankTier tier) {
        this.tier = tier;
    }

    public float getProcessFillPercent() {
        if (!canProcessFill()) return 0;
        if (processingFill == CommonConfig.HONEY_PROCESS_TIME.get()) return 1;
        return processingFill / (float) CommonConfig.HONEY_PROCESS_TIME.get();
    }

    @Override
    @NotNull
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.resourcefulbees.honey_tank");
    }


    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        if (level == null) return null;
        return new HoneyTankContainer(id, level, worldPosition, playerInventory);
    }

    public boolean canProcessFill() {
        ItemStack stack = getTileStackHandler().getStackInSlot(BOTTLE_INPUT_FILL);
        FluidStack fluidStack = getFluidTank().getFluid();
        ItemStack outputStack = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT_FILL);

        boolean isBottle = stack.getItem() == Items.GLASS_BOTTLE;

        Item outputItem = isBottle ? BeeInfoUtils.getHoneyBottleFromFluid(fluidStack.getFluid()) : fluidStack.getRawFluid().getBucket();

        boolean isTankReady = !fluidStack.isEmpty() && getFluidTank().getFluidAmount() >= (isBottle ? ModConstants.HONEY_PER_BOTTLE : 1000);
        boolean hasInput = !stack.isEmpty();
        boolean canOutput = outputStack.isEmpty() || outputStack.getItem() == outputItem && outputStack.getCount() < outputStack.getMaxStackSize();

        return isTankReady && hasInput && canOutput;
    }

    public void processFill() {
        ItemStack inputStack = getTileStackHandler().getStackInSlot(BOTTLE_INPUT_FILL);
        boolean isBottle = inputStack.getItem() != Items.BUCKET;
        inputStack.shrink(1);
        FluidStack fluidStack = new FluidStack(getFluidTank().getFluid(), isBottle ? ModConstants.HONEY_PER_BOTTLE : 1000);
        getTileStackHandler().setStackInSlot(BOTTLE_INPUT_FILL, inputStack);
        ItemStack outputStack = getTileStackHandler().getStackInSlot(BOTTLE_OUTPUT_FILL);
        if (outputStack.isEmpty()) {
            outputStack = isBottle ? BeeInfoUtils.getHoneyBottleFromFluid(fluidStack.getFluid()).getDefaultInstance() :
                    fluidStack.getRawFluid().getBucket().getDefaultInstance();
        } else outputStack.grow(1);
        getTileStackHandler().setStackInSlot(BOTTLE_OUTPUT_FILL, outputStack);
        getFluidTank().drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
        return (slot, stack, automation) -> !automation || (slot == 0 || slot == 2);
    }

    @Override
    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
        return (slot, automation) -> !automation || (slot == 1 || slot == 3);
    }

    @Override
    public void tick() {
        if (canProcessFill()) {
            if (processingFill >= CommonConfig.HONEY_PROCESS_TIME.get()) {
                processFill();
                processingFill = 0;
            }
            processingFill++;
        }
        super.tick();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getBlockPos().below().south().west(), this.getBlockPos().above().north().east());
    }

    @Override
    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(getFluidTank().getFluid());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    @Override
    public void handleGUINetworkPacket(PacketBuffer buffer) {
        getFluidTank().setFluid(buffer.readFluidStack());
    }

    @Override
    public CompoundNBT writeNBT(CompoundNBT tag) {
        CompoundNBT inv = this.getTileStackHandler().serializeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inv);
        tag.putInt("tier", getTier().tier);
        if (getFluidTank().isEmpty()) return tag;
        return super.writeNBT(tag);
    }

    @Override
    public void readNBT(CompoundNBT tag) {
        super.readNBT(tag);
        CompoundNBT invTag = tag.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        setTier(TankTier.getTier(tag.getInt("tier")));
        if (getFluidTank().getTankCapacity(0) != getTier().maxFillAmount)
            getFluidTank().setCapacity(getTier().maxFillAmount);
        if (getFluidTank().getFluidAmount() > getFluidTank().getTankCapacity(0))
            getFluidTank().getFluid().setAmount(getFluidTank().getTankCapacity(0));
    }

    protected class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
            super(slots, acceptor, remover);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            boolean slot0Valid = slot == 0 && AbstractHoneyTankContainer.isItemValid(stack);
            boolean slot2Valid = slot == 2 && (stack.getItem() == Items.GLASS_BOTTLE || stack.getItem() == Items.BUCKET);
            return slot0Valid || slot2Valid;
        }
    }
}
