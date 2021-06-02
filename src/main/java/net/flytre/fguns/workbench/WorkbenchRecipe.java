package net.flytre.fguns.workbench;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.FlytreGuns;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.flytre_lib.common.util.InventoryUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkbenchRecipe implements Recipe<PlayerInventory> {

    private final Item output;
    private final ItemStack stackOutput;
    private final List<QuantifiedIngredient> ingredients;
    private final Identifier id;

    public WorkbenchRecipe(Identifier id, Item output, List<QuantifiedIngredient> ingredients) {
        this.output = output;
        this.ingredients = ingredients;
        this.id = id;
        this.stackOutput = new ItemStack(output, 1);
        stackOutput.getOrCreateTag().putInt("clip", 0);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FlytreGuns.WORKBENCH_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return FlytreGuns.WORKBENCH_RECIPE;
    }

    @Override
    public ItemStack getOutput() {
        return stackOutput.copy();
    }

    public Item getOutputItem() {
        return output;
    }

    public List<QuantifiedIngredient> getQuantifiedIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(PlayerInventory inv, World world) {

        Map<Item, Integer> contents = InventoryUtils.countInventoryContents(inv);

        for (QuantifiedIngredient ingredient : ingredients) {
            boolean bl = false;
            for (Map.Entry<Item, Integer> item : contents.entrySet()) {
                ItemStack stack = new ItemStack(item.getKey(), item.getValue());
                if (ingredient.test(stack)) {
                    contents.put(item.getKey(), item.getValue() - ingredient.getQuantity());
                    bl = true;
                }
            }
            if (!bl)
                return false;
        }

        return true;
    }

    @Environment(EnvType.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(FlytreGuns.WORKBENCH);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> def = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);
        List<Ingredient> ing = ingredients.stream().map(QuantifiedIngredient::getIngredient).collect(Collectors.toList());
        for (int i = 0; i < ing.size(); i++)
            def.set(i, ing.get(i));
        return def;
    }

    @Override
    public ItemStack craft(PlayerInventory inv) {

        Map<QuantifiedIngredient, Integer> counts = new HashMap<>();
        ingredients.forEach(i -> counts.put(i, i.getQuantity()));


        for (QuantifiedIngredient ingredient : ingredients) {
            for (int i = 0; i < inv.size(); i++) {
                ItemStack stack = inv.getStack(i);
                if (ingredient.getIngredient().test(stack) && counts.get(ingredient) != null) {
                    if (stack.getCount() >= counts.get(ingredient)) {
                        stack.decrement(counts.get(ingredient));
                        counts.remove(ingredient);
                    } else {
                        counts.put(ingredient, counts.get(ingredient) - stack.getCount());
                        stack.decrement(64);
                    }
                }
            }
        }
        return this.getOutput().copy();
    }

}
