package net.flytre.fguns.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.flytre.fguns.gun.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class CustomGunConfigHandler {


    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Item.class, new ItemSerializer()).create();
    public static Map<String, AbstractGun> LOADED_GUNS = new HashMap<>();
    public static Set<AbstractGun> CONFIG_ADDED_GUNS = new HashSet<>();

    public static void handleConfig() {
        Path location = FabricLoader.getInstance().getConfigDir();
        File config = location.toFile();

        File fguns = null;
        for (File file : config.listFiles()) {
            if (file.getName().equals("fguns")) {
                fguns = file;
                break;
            }
        }

        if (fguns != null) {

            for (File file : fguns.listFiles()) {
                parseConfigFile(file);
            }

        }

    }

    private static void parseConfigFile(File file) {
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(readFile(file.toPath()));
        String type = obj.get("type").getAsString();
        String id = obj.get("id").getAsString();
        String name = obj.get("name").getAsString();
        AbstractGun.Builder<?> builder = switch (type) {
            case "flare_gun" -> GSON.fromJson(obj, FlareGun.Builder.class);
            case "minigun" -> GSON.fromJson(obj, Minigun.Builder.class);
            case "pistol" -> GSON.fromJson(obj, Pistol.Builder.class);
            case "rifle" -> GSON.fromJson(obj, Rifle.Builder.class);
            case "rocket_launcher" -> GSON.fromJson(obj, RocketLauncher.Builder.class);
            case "shocker" -> GSON.fromJson(obj, Shocker.Builder.class);
            case "shotgun" -> GSON.fromJson(obj, Shotgun.Builder.class);
            case "slime_gun" -> GSON.fromJson(obj, SlimeGun.Builder.class);
            case "smg" -> GSON.fromJson(obj, Smg.Builder.class);
            case "sniper" -> GSON.fromJson(obj, Sniper.Builder.class);
            default -> throw new RuntimeException("Invalid type of gun: " + type + " in file " + file.getName());
        };
        createGun(builder, name, id);
    }

    private static void createGun(AbstractGun.Builder<?> builder, String name, String id) {
        AbstractGun gun = builder.build();
        gun.setName(name);
        LOADED_GUNS.put(id, gun);
        CONFIG_ADDED_GUNS.add(gun);
        Registry.register(Registry.ITEM, new Identifier("fguns", id), gun);
    }

    private static String readFile(Path path) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    public static class ItemSerializer implements JsonSerializer<Item>, JsonDeserializer<Item> {

        @Override
        public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Registry.ITEM.get(new Identifier(json.getAsString()));
        }

        @Override
        public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Registry.ITEM.getId(src).toString());
        }
    }
}
