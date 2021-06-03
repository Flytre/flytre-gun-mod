package net.flytre.fguns.compat.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.compat.rei.categories.WorkbenchRecipeCategory;
import net.flytre.fguns.compat.rei.displays.WorkbenchRecipeDisplay;
import net.flytre.fguns.workbench.WorkbenchRecipe;
import net.flytre.fguns.workbench.WorkbenchScreen;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

import java.util.*;

@Environment(EnvType.CLIENT)
public class FgunsPlugin implements REIClientPlugin {

    public static final Map<RecipeType<?>, ItemConvertible> ICON_MAP = new HashMap<>();
    public static final List<RecipeType<?>> TYPES = new ArrayList<>();

    public FgunsPlugin() {
        ICON_MAP.put(FlytreGuns.WORKBENCH_RECIPE, FlytreGuns.WORKBENCH);
        TYPES.addAll(Collections.singletonList(FlytreGuns.WORKBENCH_RECIPE));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        ExclusionZones exclusionZones = registry.exclusionZones();
        exclusionZones.register(WorkbenchScreen.class, screen ->
        {
            int x = screen.getX();
            int y = screen.getY();
            return Collections.singletonList(new Rectangle(x - 205, y, 500, 167));
        });
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new WorkbenchRecipeCategory(FlytreGuns.WORKBENCH_RECIPE));
        for (RecipeType<?> type : TYPES)
            registry.addWorkstations(CategoryIdentifier.of(Registry.RECIPE_TYPE.getId(type)), EntryStacks.of(ICON_MAP.get(type)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(WorkbenchRecipe.class, recipe -> recipe.getType() == FlytreGuns.WORKBENCH_RECIPE, WorkbenchRecipeDisplay::new);
    }

}