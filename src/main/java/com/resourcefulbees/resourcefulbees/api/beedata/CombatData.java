package com.resourcefulbees.resourcefulbees.api.beedata;

public class CombatData extends AbstractBeeData {
    /**
     * The damage the bee makes.
     */
    private final Float attackDamage;

    /**
     * If the bee gets tortured when it stings you.
     */
    private final boolean removeStingerOnAttack;

    /**
     * If the bee is passive and doensn't attacks at all.
     */
    private final boolean isPassive;
    private final boolean inflictsPoison;

    private CombatData(boolean isPassive, Float attackDamage, boolean removeStingerOnAttack, boolean inflictsPoison) {
        this.isPassive = isPassive;
        this.attackDamage = attackDamage;
        this.removeStingerOnAttack = removeStingerOnAttack;
        this.inflictsPoison = inflictsPoison;
    }

    public Float getAttackDamage() { return attackDamage == null ? 1.0f : attackDamage; }

    public boolean removeStingerOnAttack() { return removeStingerOnAttack; }

    public boolean isPassive() { return isPassive; }

    public boolean inflictsPoison() { return inflictsPoison; }

    public static class Builder {

        private final boolean isPassive;
        private Float attackDamage;
        private boolean removeStingerOnAttack;
        private boolean inflictsPoison;

        public Builder(boolean isPassive) {
            this.isPassive = isPassive;
        }

        public Builder setAttackDamage(Float attackDamage) {
            this.attackDamage = attackDamage;
            return this;
        }

        public Builder setRemoveStingerOnAttack(boolean removeStingerOnAttack) {
            this.removeStingerOnAttack = removeStingerOnAttack;
            return this;
        }

        public Builder setInflictsPoison(boolean inflictsPoison) {
            this.inflictsPoison = inflictsPoison;
            return this;
        }

        public CombatData create() { return new CombatData(isPassive, attackDamage, removeStingerOnAttack, inflictsPoison); }
    }

    public static CombatData createDefault() {
        return new Builder(false).create();
    }
}
