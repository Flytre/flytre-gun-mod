package net.flytre.fguns.compat.rei.categories;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.flytre.fguns.compat.rei.FgunsPlugin;
import net.flytre.fguns.workbench.WorkbenchRecipe;
import net.flytre.flytre_lib.compat.rei.AbstractCustomCategory;
import net.flytre.flytre_lib.compat.rei.AbstractRecipeDisplay;
import net.flytre.flytre_lib.compat.rei.ArrowWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorkbenchRecipeCategory extends AbstractCustomCategory<WorkbenchRecipe> {

    public WorkbenchRecipeCategory(RecipeType<WorkbenchRecipe> recipeType) {
        super(recipeType);
    }

    @NotNull
    public String getCategoryName() {
        return I18n.translate("recipe.fguns.workbench");
    }


    @Override
    public @NotNull List<Widget> setupDisplay(AbstractRecipeDisplay<WorkbenchRecipe> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;

        int size = recipeDisplay.getInputEntries().size();

        if (size <= 2) {
            widgets.add(Widgets.createSlot(new Point(x + w / 9 - 9, y + h / 2 - 9)).entries(getInput(recipeDisplay, 0)).markInput());
            if (size > 1)
                widgets.add(Widgets.createSlot(new Point(x + w / 4 - 9, y + h / 2 - 9)).entries(getInput(recipeDisplay, 1)).markInput());
        } else {
            widgets.add(Widgets.createSlot(new Point(x + w / 9 - 9, y + h / 2 - 18)).entries(getInput(recipeDisplay, 0)).markInput());
            widgets.add(Widgets.createSlot(new Point(x + w / 4 - 9, y + h / 2 - 18)).entries(getInput(recipeDisplay, 1)).markInput());

        }
        if (size > 2)
            widgets.add(Widgets.createSlot(new Point(x + w / 9 - 9, y + 3 * h / 4 - 15)).entries(getInput(recipeDisplay, 2)).markInput());

        if (size > 3)
            widgets.add(Widgets.createSlot(new Point(x + w / 4 - 9, y + 3 * h / 4 - 15)).entries(getInput(recipeDisplay, 3)).markInput());

        widgets.add(Widgets.createSlot(new Point(x + 3 * w / 4 - 9, y + h / 2 - 9)).entries(getOutput(recipeDisplay, 0)).markOutput());
        widgets.add(new ArrowWidget(new Rectangle(x + w / 2 - 12, y + h / 2 - 9, 24, 7)));

        return widgets;
    }


    @Override
    public @NotNull Renderer getIcon() {
        return EntryStacks.of(FgunsPlugin.ICON_MAP.getOrDefault(getRecipeType(), () -> Items.BARRIER));
    }
}
