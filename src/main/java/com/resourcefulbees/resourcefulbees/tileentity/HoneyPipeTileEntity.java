package com.resourcefulbees.resourcefulbees.tileentity;

//extends TileEntity implements ITickableTileEntity
public class HoneyPipeTileEntity  {
//
//    private Direction inputSide = null;
//
//    FluidTank tank = new FluidTank(Config.MAX_PIPE_FLOW.get(), BeeInfoUtils.getHoneyPredicate());
//    private LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(this::getFluidTank);
//
//    public HoneyPipeTileEntity() {
//        super(ModTileEntityTypes.HONEY_PIPE_TILE_ENTITY.get());
//    }
//
//    public Direction getInputSide() {
//        return inputSide;
//    }
//
//    public void setInputSide(Direction inputSide) {
//        this.inputSide = inputSide;
//    }
//
//    @Override
//    public void tick() {
//        if (inputSide == null) return;
//    }
//
//    public @NotNull FluidTank getFluidTank() {
//        return tank;
//    }
//
//    // read from tag
//    @Override
//    public void load(@NotNull BlockState state, @NotNull CompoundNBT tag) {
//        super.load(state, tag);
//        readNBT(tag);
//    }
//
//    // write to tag
//    @NotNull
//    @Override
//    public CompoundNBT save(@NotNull CompoundNBT tag) {
//        super.save(tag);
//        writeNBT(tag);
//        return tag;
//    }
//
//    @Override
//    protected void invalidateCaps() {
//        this.fluidOptional.invalidate();
//        super.invalidateCaps();
//    }
//
//    public CompoundNBT writeNBT(CompoundNBT tag) {
//        if (getFluidTank().isEmpty()) return tag;
//        tag.put("fluid", getFluidTank().writeToNBT(new CompoundNBT()));
//        return tag;
//    }
//
//    public void readNBT(CompoundNBT tag) {
//        getFluidTank().readFromNBT(tag.getCompound("fluid"));
//    }
//
//
//    @Nullable
//    @Override
//    public SUpdateTileEntityPacket getUpdatePacket() {
//        return new SUpdateTileEntityPacket(worldPosition, 0, writeNBT(new CompoundNBT()));
//    }
//
//    @Override
//    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
//        CompoundNBT nbt = pkt.getTag();
//        readNBT(nbt);
//    }
//
//    @NotNull
//    @Override
//    public CompoundNBT getUpdateTag() {
//        CompoundNBT nbt = super.getUpdateTag();
//        return writeNBT(nbt);
//    }
//
//    @Override
//    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
//        super.handleUpdateTag(state, tag);
//        readNBT(tag);
//    }
//
//    @NotNull
//    @Override
//    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (side == null || side == inputSide)) {
//            return fluidOptional.cast();
//        } else {
//            return super.getCapability(cap, side);
//        }
//    }
}
