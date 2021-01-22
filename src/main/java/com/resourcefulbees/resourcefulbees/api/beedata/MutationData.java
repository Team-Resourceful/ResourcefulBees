package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.ITag;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MutationData extends AbstractBeeData {
    /**
     * The Input that is getting mutated
     */
    private final String mutationInput;

    /**
     * The new muted output
     */
    private final String mutationOutput;

    private List<BlockMutation> blockMutations = new LinkedList<>();

    private List<EntityMutation> entityMutations = new LinkedList<>();

    /**
     * how many inputs in a radius get mutated
     */
    private final int mutationCount;

    /**
     * If the bee has a mutation
     */
    private boolean hasMutation;

    /**
     * What type of mutation it is
     */
    private final MutationTypes mutationType;

    public transient Map<Block, IBlockMutation> iBlockMutations = new HashMap<>();
    public transient Map<ITag<Block>, IBlockMutation> iBlockTagMutations = new HashMap<>();
    public transient Map<EntityType, IEntityMutation> iEntityMutations = new HashMap<>();

    private MutationData(String mutationInput, String mutationOutput, int mutationCount, boolean hasMutation, MutationTypes mutationType) {
        this.mutationInput = mutationInput;
        this.mutationOutput = mutationOutput;
        this.mutationCount = mutationCount;
        this.hasMutation = hasMutation;
        this.mutationType = mutationType;
    }

    public String getMutationInput() {
        return mutationInput;
    }

    public String getMutationOutput() {
        return mutationOutput;
    }

    public int getMutationCount() {
        return mutationCount <= 0 ? 10 : mutationCount;
    }

    public boolean hasMutation() {
        return hasMutation;
    }

    public void setHasMutation(boolean hasMutation) {
        this.hasMutation = hasMutation;
    }

    public MutationTypes getMutationType() {
        return mutationType;
    }

    public static class Builder {
        private String mutationInput;
        private String mutationOutput;
        private int mutationCount;
        private final boolean hasMutation;
        private final MutationTypes mutationType;

        public Builder(boolean hasMutation, MutationTypes mutationType) {
            this.hasMutation = hasMutation;
            this.mutationType = mutationType;
        }

        public Builder setMutationInput(String mutationInput) {
            this.mutationInput = mutationInput;
            return this;
        }

        public Builder setMutationOutput(String mutationOutput) {
            this.mutationOutput = mutationOutput;
            return this;
        }

        public Builder setMutationCount(int mutationCount) {
            this.mutationCount = mutationCount;
            return this;
        }

        public MutationData createMutationData() {
            return new MutationData(mutationInput, mutationOutput, mutationCount, hasMutation, mutationType);
        }
    }

    public static MutationData createDefault() {
        return new Builder(false, MutationTypes.NONE).createMutationData();
    }

    public void initBlockMutationList() {
        blockMutations.stream().filter(b -> !b.inputID.toLowerCase().startsWith("tag:")).forEach(b -> {
            Block input = BeeInfoUtils.getBlock(b.inputID);
            Block output = BeeInfoUtils.getBlock(b.outputID);
            if (input != null && output != null) {
                iBlockMutations.put(input, new IBlockMutation(output, b.getChance()));
            }
        });
    }

    public void initBlockTagMutationList() {
        blockMutations.stream().filter(b -> b.inputID.toLowerCase().startsWith("tag:")).forEach(b -> {
            ITag<Block> input = BeeInfoUtils.getBlockTag(b.inputID.replace("tag:", ""));
            Block output = BeeInfoUtils.getBlock(b.outputID);
            if (input != null && output != null) {
                iBlockTagMutations.put(input, new IBlockMutation(output, b.getChance()));
            }
        });
    }

    public void initEntityMutationList() {
        entityMutations.stream().forEach(e -> {
            EntityType input = BeeInfoUtils.getEntityType(e.inputID);
            EntityType output = BeeInfoUtils.getEntityType(e.outputID);
            if (input != null && output != null) {
                iEntityMutations.put(input, new IEntityMutation(output, e.getChance()));
            }
        });
    }

    /**
     * Used to determine how the recipe will look
     */
    public enum BlockMutationType {
        BLOCK_TO_BLOCK,
        BLOCK_TO_FLUID,
        FLUID_TO_BLOCK,
        FLUID_TO_FLUID
    }

    public class BlockMutation {
        BlockMutationType type;
        String inputID;
        String outputID;
        float chance = 1;

        public float getChance() {
            return chance < 0 ? 1 : chance;
        }
    }

    public class EntityMutation {
        String inputID;
        String outputID;
        float chance = 1;

        public float getChance() {
            return chance < 0 ? 1 : chance;
        }
    }

    private class IBlockMutation {
        Block output;
        float chance;

        public IBlockMutation(Block output, float chance) {
            this.output = output;
            this.chance = chance;
        }
    }

    private class IEntityMutation {
        EntityType output;
        float chance;

        public IEntityMutation(EntityType output, float chance) {
            this.output = output;
            this.chance = chance;
        }
    }
}

