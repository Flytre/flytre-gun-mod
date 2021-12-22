package net.flytre.fguns.compat.rei.displays;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.flytre.fguns.workbench.WorkbenchRecipe;
import net.flytre.flytre_lib.api.compat.rei.AbstractRecipeDisplay;

import java.util.Collections;
import java.util.List;

public class WorkbenchRecipeDisplay extends AbstractRecipeDisplay<WorkbenchRecipe> {

    public WorkbenchRecipeDisplay(WorkbenchRecipe recipe) {
        super(recipe);
    }


    @Override
    public List<EntryIngredient> createOutputs() {
        return Collections.singletonList(EntryIngredients.of(recipe.getOutput()));
    }

    @Override
    public List<EntryIngredient> createInputs() {
        return CollectionUtils.map(recipe.getIngredients(), EntryIngredients::ofIngredient);
    }
}
