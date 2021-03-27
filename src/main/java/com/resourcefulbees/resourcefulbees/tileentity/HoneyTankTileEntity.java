package com.resourcefulbees.resourcefulbees.tileentity;

import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoneyTankTileEntity extends AbstractHoneyTank {

    public TankTier getTier() {
        return tier;
    }

    public void setTier(TankTier tier) {
        this.tier = tier;
    }

    public enum TankTier {
        PURPUR(3, 64000, ModBlocks.PURPUR_HONEY_TANK, ModItems.PURPUR_HONEY_TANK_ITEM),
        NETHER(2, 16000, ModBlocks.NETHER_HONEY_TANK, ModItems.NETHER_HONEY_TANK_ITEM),
        WOODEN(1, 4000, ModBlocks.WOODEN_HONEY_TANK, ModItems.WOODEN_HONEY_TANK_ITEM);

        private final RegistryObject<Block> tankBlock;
        private final RegistryObject<Item> tankItem;
        int maxFillAmount;
        int tier;

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
        super(ModTileEntityTypes.HONEY_TANK_TILE_ENTITY.get(), tier.maxFillAmount);
        this.setTier(tier);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.getBlockPos().below().south().west(), this.getBlockPos().above().north().east());
    }

    @Override
    public CompoundNBT writeNBT(CompoundNBT tag) {
        tag.putInt("tier", getTier().tier);
        if (getFluidTank().isEmpty()) return tag;
        return super.writeNBT(tag);
    }

    @Override
    public void readNBT(CompoundNBT tag) {
        super.readNBT(tag);
        setTier(TankTier.getTier(tag.getInt("tier")));
        if (getFluidTank().getTankCapacity(0) != getTier().maxFillAmount) getFluidTank().setCapacity(getTier().maxFillAmount);
        if (getFluidTank().getFluidAmount() > getFluidTank().getTankCapacity(0))
            getFluidTank().getFluid().setAmount(getFluidTank().getTankCapacity(0));
    }
}
