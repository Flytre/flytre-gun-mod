package net.flytre.fguns.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.compat.rei.categories.WorkbenchRecipeCategory;
import net.flytre.fguns.compat.rei.displays.WorkbenchRecipeDisplay;
import net.flytre.fguns.workbench.WorkbenchScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class FgunsPlugin implements REIPluginV0 {

    public static final Identifier PLUGIN = new Identifier("fguns", "plugin");
    public static final Map<RecipeType<?>, ItemConvertible> iconMap = new HashMap<>();
    public static final List<RecipeType<?>> types = new ArrayList<>();

    public FgunsPlugin() {
        iconMap.put(FlytreGuns.WORKBENCH_RECIPE, FlytreGuns.WORKBENCH);
        types.addAll(Collections.singletonList(FlytreGuns.WORKBENCH_RECIPE));
    }

    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }

    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        BaseBoundsHandler baseBoundsHandler = BaseBoundsHandler.getInstance();
        baseBoundsHandler.registerExclusionZones(WorkbenchScreen.class, () ->
        {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            List<Rectangle> result = new ArrayList<>();
            if (currentScreen instanceof WorkbenchScreen) {
                WorkbenchScreen actualScreen = (WorkbenchScreen) currentScreen;
                int x = actualScreen.getX();
                int y = actualScreen.getY();
                result.add(new Rectangle(x - 205, y, 500, 167));
            }
            return result;
        });
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new WorkbenchRecipeCategory(FlytreGuns.WORKBENCH_RECIPE));
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(Registry.RECIPE_TYPE.getId(FlytreGuns.WORKBENCH_RECIPE), (Function<Recipe, Boolean>) recipe -> recipe.getType() == FlytreGuns.WORKBENCH_RECIPE, WorkbenchRecipeDisplay::new);
    }


    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        for (RecipeType<?> type : types)
            recipeHelper.registerWorkingStations(Registry.RECIPE_TYPE.getId(type), EntryStack.create(iconMap.get(type)));
    }


}
