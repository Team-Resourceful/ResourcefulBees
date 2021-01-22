package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.ITag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MutationData extends AbstractBeeData {

    public static final Logger LOGGER = LogManager.getLogger();
    /**
     * The Input that is getting mutated
     */
    @Deprecated
    private final String mutationInput;

    /**
     * The new muted output
     */
    @Deprecated
    private final String mutationOutput;

    /**
     * What type of mutation it is
     */
    @Deprecated
    private final MutationTypes mutationType;

    /**
     * If the bee has a mutation
     */
    private boolean hasMutation;

    /**
     * how many inputs in a radius get mutated
     */
    private final int mutationCount;

    /**
     * List of block mutations
     */
    private List<Mutation> mutations = new LinkedList<>();

    public transient Map<Block, IBlockMutation> iBlockMutations = new HashMap<>();
    public transient Map<String, IBlockMutation> iBlockTagMutations = new HashMap<>();
    public transient Map<EntityType, IEntityMutation> iEntityMutations = new HashMap<>();

    private MutationData(String mutationInput, String mutationOutput, int mutationCount, boolean hasMutation, MutationTypes mutationType) {
        this.mutationInput = mutationInput;
        this.mutationOutput = mutationOutput;
        this.mutationCount = mutationCount;
        this.hasMutation = hasMutation;
        this.mutationType = mutationType;
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

    public void initMutations() {
        iBlockTagMutations = new HashMap<String, IBlockMutation>();
        iBlockMutations = new HashMap<>();
        iEntityMutations = new HashMap<>();
        initMutationList();
        initBaseMutationData();
    }

    private void initBaseMutationData() {
        if (hasMutation) {
            Mutation mutation = new Mutation(mutationType, mutationInput, mutationOutput, 1);
            switch (mutationType) {
                case FLUID_TO_FLUID:
                case BLOCK_TO_FLUID:
                case FLUID_TO_BLOCK:
                case BLOCK_TO_BLOCK:
                    if (mutationInput.startsWith(BeeConstants.TAG_PREFIX)) {
                        addBlockTagMutation(mutation);
                    } else {
                        addBlockMutation(mutation);
                    }
                    break;
                case ENTITY_TO_ENTITY:
                    if (mutationInput.startsWith(BeeConstants.ENTITY_PREFIX) && mutationOutput.startsWith(BeeConstants.ENTITY_PREFIX)) {
                        addEntityMutation(mutation);
                    }
                    break;
            }
        }
    }

    private void addBlockMutation(Mutation mutation) {
        Block input = BeeInfoUtils.getBlock(mutation.inputID);
        Block output = BeeInfoUtils.getBlock(mutation.outputID);
        if (input != null && input != Blocks.AIR && output != null && output != Blocks.AIR && mutation.type != null) {
            iBlockMutations.put(input, new IBlockMutation(mutation.type, output, mutation.getChance(), mutation));
        } else {
            printWarning(mutation);
        }
    }

    private void addBlockTagMutation(Mutation mutation) {
        String tag = mutation.inputID.toLowerCase().replace(BeeConstants.TAG_PREFIX, "");
        ITag input = BeeInfoUtils.getBlockTag(tag);
        if (input == null) {
            input = BeeInfoUtils.getFluidTag(tag);
        }
        Block output = BeeInfoUtils.getBlock(mutation.outputID.toLowerCase());
        if (input != null && output != null && mutation.type != null) {
            iBlockTagMutations.put(tag, new IBlockMutation(mutation.type, output, mutation.getChance(), mutation));
        } else {
            printWarning(mutation);
        }
    }

    private void addEntityMutation(Mutation mutation) {
        EntityType input = BeeInfoUtils.getEntityType(mutation.inputID.toLowerCase().replace(BeeConstants.ENTITY_PREFIX, ""));
        EntityType output = BeeInfoUtils.getEntityType(mutation.outputID.toLowerCase().replace(BeeConstants.ENTITY_PREFIX, ""));
        if (input != null && output != null && mutation.type != null) {
            iEntityMutations.put(input, new IEntityMutation(mutation.type, output, mutation.getChance(), mutation));
        } else {
            printWarning(mutation);
        }
    }

    private void initMutationList() {
        if (mutations == null || mutations.isEmpty()) return;
        mutations.stream().forEach(b -> {
            if (b.inputID.toLowerCase().startsWith(BeeConstants.TAG_PREFIX)) {
                addBlockTagMutation(b);
            } else if (isEntityMutation(b.inputID, b.outputID) && isValidEntityMutation(b.inputID, b.outputID, b.type)) {
                addEntityMutation(b);
            } else if (isEntityMutation(b.inputID, b.outputID) && !isValidEntityMutation(b.inputID, b.outputID, b.type)) {
                printWarning(b);
            } else {
                addBlockMutation(b);
            }
        });
    }

    private boolean isValidEntityMutation(String inputID, String outputID, MutationTypes type) {
        return inputID.toLowerCase().startsWith(BeeConstants.ENTITY_PREFIX) && outputID.toLowerCase().startsWith(BeeConstants.ENTITY_PREFIX) && type == MutationTypes.ENTITY_TO_ENTITY;
    }

    private boolean isEntityMutation(String inputID, String outputID) {
        return inputID.toLowerCase().startsWith(BeeConstants.ENTITY_PREFIX) || outputID.toLowerCase().startsWith(BeeConstants.ENTITY_PREFIX);
    }

    private void printWarning(Mutation mutation) {
        LOGGER.warn(String.format("Could not validate mutation: [\"type\": \"%s\", \"inputID\": \"%s\", \"outputID\": \"%s\", \"chance\": %f]", mutation.type, mutation.inputID, mutation.outputID, mutation.getChance()));
    }

    /**
     * Used to determine how the recipe will look
     */

    public class Mutation {
        public MutationTypes type;
        public String inputID;
        public String outputID;
        private float chance = 1;

        public Mutation(MutationTypes type, String inputID, String outputID, int chance) {
            this.type = type;
            this.inputID = inputID;
            this.outputID = outputID;
            this.chance = chance;
        }

        public float getChance() {
            return chance < 0 ? 1 : chance;
        }
    }

    public class IBlockMutation {
        public MutationTypes type;
        public Block output;
        public float chance;
        public Mutation mutationData;

        public IBlockMutation(MutationTypes type, Block output, float chance, Mutation mutationData) {
            this.type = type;
            this.output = output;
            this.chance = chance;
            this.mutationData = mutationData;
        }
    }

    public class IEntityMutation {
        public MutationTypes type;
        public EntityType output;
        public float chance;
        public Mutation mutationData;

        public IEntityMutation(MutationTypes type, EntityType output, float chance, Mutation mutationData) {
            this.type = type;
            this.output = output;
            this.chance = chance;
            this.mutationData = mutationData;
        }
    }


}

