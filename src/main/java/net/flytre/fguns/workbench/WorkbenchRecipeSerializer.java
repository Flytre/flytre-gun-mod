package net.flytre.fguns.workbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.flytre.flytre_lib.common.recipe.QuantifiedIngredient;
import net.flytre.flytre_lib.common.util.PacketUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class WorkbenchRecipeSerializer implements RecipeSerializer<WorkbenchRecipe> {

    private final RecipeFactory recipeFactory;

    public WorkbenchRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }


    @Override
    public WorkbenchRecipe read(Identifier id, JsonObject json) {
        List<QuantifiedIngredient> ingredients = new ArrayList<>();
        JsonArray array = JsonHelper.getArray(json, "ingredients");
        for (JsonElement element : array) {
            ingredients.add(QuantifiedIngredient.fromJson(element));
        }
        String result = JsonHelper.getString(json, "result");
        Identifier resultId = new Identifier(result);
        ItemStack itemStack = new ItemStack(Registry.ITEM.getOrEmpty(resultId).orElseThrow(() -> new IllegalStateException("Item: " + result + " does not exist")));
        return recipeFactory.create(id, itemStack.getItem(), ingredients);
    }

    @Override
    public WorkbenchRecipe read(Identifier id, PacketByteBuf buf) {
        Item item = buf.readItemStack().getItem();
        List<QuantifiedIngredient> ingredients = PacketUtils.listFromPacket(buf, QuantifiedIngredient::fromPacket);
        return recipeFactory.create(id, item, ingredients);
    }

    @Override
    public void write(PacketByteBuf buf, WorkbenchRecipe recipe) {
        buf.writeItemStack(recipe.getOutput());
        PacketUtils.toPacket(buf, recipe.getIngredients(), QuantifiedIngredient::toPacket);
    }

    public interface RecipeFactory {
        WorkbenchRecipe create(Identifier id, Item output, List<QuantifiedIngredient> ingredients);
    }
}
