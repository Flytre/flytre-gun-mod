package net.flytre.fguns;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.flytre.fguns.entity.Bullet;
import net.flytre.fguns.guns.*;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FlytreGuns implements ModInitializer {



    public static final Identifier RELOAD_PACKET_ID = new Identifier("fguns", "reload");


    public static final GunItem LETHAL_MARK = new GunItem(5, .40, 2, 0.02, 3, 30, 10, 1.4, GunType.PISTOL);
    public static final ItemGroup TAB = FabricItemGroupBuilder.build(
            new Identifier("fguns", "all"),
            () -> new ItemStack(FlytreGuns.LETHAL_MARK));
    public static final GunItem BEAMER = new GunItem(12, .50, 1, 0.02, 3, 30, 6, 3.0, GunType.PISTOL);
    public static final GunItem LASER_SPEED = new GunItem(4, .20, 4, 0.03, 6, 25, 20, 1.4, GunType.PISTOL);

    public static final GunItem HUNTER = new GunItem(5, .40, 6, 0.04, 9, 25, 25, 3.2, GunType.RIFLE);
    public static final GunItem BLASTER = new GunItem(5, .50, 4, 0.03, 7, 25, 30, 3.0, GunType.RIFLE);

    public static final GunItem RAPIDSTRIKE = new GunItem(4, .30, 10, 0.06, 13, 20, 40, 2.5, GunType.SMG);

    public static final Sniper SEEKER = new Sniper(13, .75, 0.2, 0.0, 17, 100, 5, 12.0, GunType.SNIPER);
    public static final Sniper NIGHTMARE = new Sniper(22, .90, 0.11, 0.0, 17, 100, 1, 9.0, GunType.SNIPER);

    public static final Shotgun SHOTGUN = new Shotgun();

    public static final GunItem TRIFORCE = new GunItem(6, .40, 20, 0.01, 3, 35, 3, 1.25, GunType.RIFLE);

    public static final SlimeGun SLIMER = new SlimeGun(4, .20, 4, 0.01, 0, 40, 10, 2.0);


    //AMMO
    public static final Item BASIC_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item SHOTGUN_SHELL = new Item(new Item.Settings().group(FlytreGuns.TAB));
    public static final Item SNIPER_AMMO = new Item(new Item.Settings().group(FlytreGuns.TAB));

    public static final Item MYSTERY_GUN = new MysteryGun();

    public static final EntityType<Bullet> BULLET = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier("fguns", "bullet"),
            FabricEntityTypeBuilder.<Bullet>create(SpawnGroup.MISC, Bullet::new).dimensions(new EntityDimensions(0.5F, 0.2F, true)).trackRangeChunks(4).trackedUpdateRate(20).build());

    @Override
    public void onInitialize() {
        System.out.println("Flytre's Gun Mod is being loaded!");
        Registry.register(Registry.ITEM, new Identifier("fguns", "lethal_mark"), LETHAL_MARK);
        Registry.register(Registry.ITEM, new Identifier("fguns", "beamer"), BEAMER);
        Registry.register(Registry.ITEM, new Identifier("fguns", "laser_speed"), LASER_SPEED);
        Registry.register(Registry.ITEM, new Identifier("fguns", "hunter"), HUNTER);
        Registry.register(Registry.ITEM, new Identifier("fguns", "blaster"), BLASTER);
        Registry.register(Registry.ITEM, new Identifier("fguns", "shotgun"), SHOTGUN);
        Registry.register(Registry.ITEM, new Identifier("fguns", "rapidstrike"), RAPIDSTRIKE);
        Registry.register(Registry.ITEM, new Identifier("fguns", "seeker"), SEEKER);
        Registry.register(Registry.ITEM, new Identifier("fguns", "nightmare"), NIGHTMARE);
        Registry.register(Registry.ITEM, new Identifier("fguns", "triforce"), TRIFORCE);
        Registry.register(Registry.ITEM, new Identifier("fguns", "slimer"), SLIMER);

        Registry.register(Registry.ITEM, new Identifier("fguns", "basic_ammo"), BASIC_AMMO);
        Registry.register(Registry.ITEM, new Identifier("fguns", "shotgun_shell"), SHOTGUN_SHELL);
        Registry.register(Registry.ITEM, new Identifier("fguns", "sniper_ammo"), SNIPER_AMMO);

        Registry.register(Registry.ITEM, new Identifier("fguns", "mystery_gun"), MYSTERY_GUN);

        Sounds.init();


        ServerPlayNetworking.registerGlobalReceiver(FlytreGuns.RELOAD_PACKET_ID,(server, player, handler, buf, responseSender) -> {
            server.execute(() -> GunItem.attemptEarlyReload(player));
        });
    }
}
