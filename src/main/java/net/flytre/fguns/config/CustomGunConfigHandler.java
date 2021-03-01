package net.flytre.fguns.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.flytre.fguns.gun.AbstractGun;
import net.flytre.fguns.gun.BulletProperties;
import net.minecraft.util.JsonHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class CustomGunConfigHandler {


    public static HashMap<String, AbstractGun> LOADED_GUNS = new HashMap<>();
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
        JsonElement element = parser.parse(readFile(file.toPath()));
        JsonObject object = (JsonObject) element;
        double damage = JsonHelper.getFloat(object, "damage");
        double armorPen = JsonHelper.getFloat(object, "armor_pen");
        double rps = JsonHelper.getFloat(object, "rps");
        double dropoff = JsonHelper.getFloat(object, "dropoff");
        int spray = JsonHelper.getInt(object, "spray");
        int range = JsonHelper.getInt(object, "range");
        int clip = JsonHelper.getInt(object, "clip");
        double reload = JsonHelper.getFloat(object, "reload");
        BulletProperties type = BulletProperties.valueOf(JsonHelper.getString(object, "type"));
        String name = JsonHelper.getString(object, "name");
        String id = JsonHelper.getString(object, "id");
        createGun(damage, armorPen, rps, dropoff, spray, range, clip, reload, type, name, id);
    }

    private static void createGun(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clip, double reload, BulletProperties type, String name, String id) {
//        AbstractGun gun = null;
//        gun.setName(name);
//        LOADED_GUNS.put(id, gun);
//        CONFIG_ADDED_GUNS.add(gun);
//        Registry.register(Registry.ITEM, new Identifier("fguns", id), gun);

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
}
