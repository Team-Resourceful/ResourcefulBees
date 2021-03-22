package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.api.beedata.mutation.Mutation;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.BlockOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.EntityOutput;
import com.resourcefulbees.resourcefulbees.api.beedata.mutation.outputs.ItemOutput;
import com.resourcefulbees.resourcefulbees.lib.MutationTypes;
import com.resourcefulbees.resourcefulbees.utils.RandomCollection;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MutationData extends AbstractBeeData {

    public static final Logger LOGGER = LogManager.getLogger();
    /**
     * The Input that is getting mutated
     *
     * @deprecated To be removed in 1.17
     */
    @Deprecated
    private final String mutationInput;

    /**
     * The new muted output
     *
     * @deprecated To be removed in 1.17
     */
    @Deprecated
    private final String mutationOutput;

    /**
     * What type of mutation it is
     *
     * @deprecated To be removed in 1.17
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
    private final List<Mutation> mutations;

    private transient Map<Block, Pair<Double, RandomCollection<BlockOutput>>> blockMutations; //IN WORLD
    private transient Map<Tag<?>, Pair<Double, RandomCollection<BlockOutput>>> jeiBlockTagMutations; // JEI ONLY
    private transient Map<Block, Pair<Double, RandomCollection<BlockOutput>>> jeiBlockMutations; //JEI ONLY

    private transient Map<EntityType<?>, Pair<Double, RandomCollection<EntityOutput>>> entityMutations;

    private transient Map<Block, Pair<Double, RandomCollection<ItemOutput>>> itemMutations; //IN WORLD
    private transient Map<Tag<?>, Pair<Double, RandomCollection<ItemOutput>>> jeiBlockTagItemMutations; //JEI ONLY
    private transient Map<Block, Pair<Double, RandomCollection<ItemOutput>>> jeiItemMutations; //JEI ONLY


    private MutationData(String mutationInput, String mutationOutput, int mutationCount, boolean hasMutation, MutationTypes mutationType, List<Mutation> mutations) {
        super("MutationData");
        this.mutationInput = mutationInput;
        this.mutationOutput = mutationOutput;
        this.mutationCount = mutationCount;
        this.hasMutation = hasMutation;
        this.mutationType = mutationType;
        this.mutations = mutations;
    }

    public int getMutationCount() {
        return mutationCount <= 0 ? 10 : mutationCount;
    }

    public boolean hasMutation() {
        return hasMutation;
    }

    public boolean testMutations() {
        return hasMutation && ((blockMutations != null && !blockMutations.isEmpty())
                || (entityMutations != null && !entityMutations.isEmpty())
                || (itemMutations != null && !itemMutations.isEmpty()));
    }

    public void setHasMutation(boolean hasMutation) {
        this.hasMutation = hasMutation;
    }

    public String getMutationInput() {
        return mutationInput == null ? "" : mutationInput;
    }

    public String getMutationOutput() {
        return mutationOutput == null ? "" : mutationOutput;
    }

    public MutationTypes getMutationType() {
        return mutationType == null ? MutationTypes.NONE : mutationType;
    }

    public List<Mutation> getMutations() {
        return mutations == null ? new LinkedList<>() : mutations;
    }

    public void initializeMutations() {
        this.blockMutations = new HashMap<>();
        this.jeiBlockMutations = new HashMap<>();
        this.jeiBlockTagMutations = new HashMap<>();
        this.entityMutations = new HashMap<>();
        this.itemMutations = new HashMap<>();
        this.jeiBlockTagItemMutations = new HashMap<>();
        this.jeiItemMutations = new HashMap<>();
    }

    public void addBlockMutation(Block input, RandomCollection<BlockOutput> outputs, double chance) {
        this.blockMutations.put(input, Pair.of(chance, outputs));
    }

    public void addEntityMutation(EntityType<?> input, RandomCollection<EntityOutput> outputs, double chance) {
        this.entityMutations.put(input, Pair.of(chance, outputs));
    }

    public void addItemMutation(Block input, RandomCollection<ItemOutput> outputs, double chance) {
        this.itemMutations.put(input, Pair.of(chance, outputs));
    }

    public void addJeiBlockMutation(Block input, RandomCollection<BlockOutput> outputs, double chance) {
        this.jeiBlockMutations.put(input, Pair.of(chance, outputs));
    }

    public void addJeiBlockTagItemMutation(Tag<?> input, RandomCollection<ItemOutput> outputs, double chance) {
        this.jeiBlockTagItemMutations.put(input, Pair.of(chance, outputs));
    }

    public void addJeiItemMutation(Block input, RandomCollection<ItemOutput> outputs, double chance) {
        this.jeiItemMutations.put(input, Pair.of(chance, outputs));
    }

    public void addJeiBlockTagMutation(Tag<?> input, RandomCollection<BlockOutput> outputs, double chance) {
        this.jeiBlockTagMutations.put(input, Pair.of(chance, outputs));
    }


    public boolean hasBlockMutations() {
        return this.blockMutations != null && !this.blockMutations.isEmpty();
    }

    public boolean hasBlockTagMutations() {
        return this.jeiBlockTagMutations != null && !this.jeiBlockTagMutations.isEmpty();
    }

    public boolean hasEntityMutations() {
        return this.entityMutations != null && !this.entityMutations.isEmpty();
    }

    public boolean hasItemMutations() {
        return this.itemMutations != null && !this.itemMutations.isEmpty();
    }

    public boolean hasJeiBlockTagItemMutations() {
        return this.jeiBlockTagItemMutations != null && !this.jeiBlockTagItemMutations.isEmpty();
    }

    public boolean hasJeiItemMutations() {
        return this.jeiItemMutations != null && !this.jeiItemMutations.isEmpty();
    }

    public boolean hasJeiBlockMutations() {
        return this.jeiBlockMutations != null && !this.jeiBlockMutations.isEmpty();
    }

    public Map<Tag<?>, Pair<Double, RandomCollection<BlockOutput>>> getJeiBlockTagMutations() {
        return Collections.unmodifiableMap(this.jeiBlockTagMutations);
    }

    public Map<Block, Pair<Double, RandomCollection<BlockOutput>>> getBlockMutations() {
        return Collections.unmodifiableMap(this.blockMutations);
    }

    public Map<EntityType<?>, Pair<Double, RandomCollection<EntityOutput>>> getEntityMutations() {
        return Collections.unmodifiableMap(this.entityMutations);
    }

    public Map<Block, Pair<Double, RandomCollection<ItemOutput>>> getItemMutations() {
        return Collections.unmodifiableMap(this.itemMutations);
    }

    public Map<Tag<?>, Pair<Double, RandomCollection<ItemOutput>>> getJeiBlockTagItemMutations() {
        return Collections.unmodifiableMap(this.jeiBlockTagItemMutations);
    }

    public Map<Block, Pair<Double, RandomCollection<ItemOutput>>> getJeiItemMutations() {
        return Collections.unmodifiableMap(this.jeiItemMutations);
    }

    public Map<Block, Pair<Double, RandomCollection<BlockOutput>>> getJeiBlockMutations() {
        return Collections.unmodifiableMap(this.jeiBlockMutations);
    }

    @SuppressWarnings("unused")
    public static class Builder {
        private String mutationInput;
        private String mutationOutput;
        private int mutationCount;
        private final boolean hasMutation;
        private final MutationTypes mutationType;
        private final List<Mutation> mutations = new LinkedList<>();

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

        /**
         * @return Builds the MutationData object
         * @deprecated This method will change in 1.17 to match the removal of legacy mutation syntax
         */
        @Deprecated
        public MutationData createMutationData() {
            return new MutationData(mutationInput, mutationOutput, mutationCount, hasMutation, mutationType, mutations);
        }
    }

    public static MutationData createDefault() {
        return new Builder(false, MutationTypes.NONE).createMutationData();
    }
}

