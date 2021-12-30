package com.resourcefulbees.resourcefulbees.compat.patchouli;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class HiddenComponentProcessor implements IComponentProcessor {

    IVariableProvider lookup;

    @Override
    public void setup(IVariableProvider lookup) {
        this.lookup = lookup;
    }

    @Override
    public IVariable process(String key) {
        if (lookup.has(key)) {
            return lookup.get(key);
        }
        return IVariable.empty();
    }

    @Override
    public boolean allowRender(String group) {
        return !"hidden".equals(group);
    }
}
