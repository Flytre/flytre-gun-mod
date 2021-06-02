package net.flytre.fguns.compat.rei.displays;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.utils.CollectionUtils;
import net.flytre.fguns.workbench.WorkbenchRecipe;
import net.flytre.flytre_lib.compat.rei.AbstractRecipeDisplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WorkbenchRecipeDisplay extends AbstractRecipeDisplay<WorkbenchRecipe> {

    public WorkbenchRecipeDisplay(WorkbenchRecipe recipe) {
        super(recipe);
    }


    //SHOULD BE List<EntryIngredient>
    @Override
    public List<List<EntryStack>> createOutputs() {
        return Collections.singletonList(Collections.singletonList(EntryStack.create(recipe.getOutput())));
    }

    @Override
    public List<List<EntryStack>> createInputs() {
        return new ArrayList<>(CollectionUtils.map(recipe.getIngredients(), ing -> EntryStack.ofItemStacks(Arrays.asList(ing.getMatchingStacksClient()))));
    }
}
