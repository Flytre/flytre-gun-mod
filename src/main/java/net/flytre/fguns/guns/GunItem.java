package net.flytre.fguns.guns;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.Sounds;
import net.flytre.fguns.entity.Bullet;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GunItem extends Item {

    public static final Set<GunItem> GUNS = new HashSet<>();
    private final double damage; //Base damage
    private final double armorPen; //Armor Penetration, eg 0.75 ignores 75% of armor
    private final double rps; //Rounds per second, eg 2 would fire a bullet every 10 ticks
    private final double dropoff; //Damage dropoff every block, %, eg 0.01 would be 1% less damage/block
    private final int spray; //Accuracy of the weapon, high spray = inaccurate
    private final int range; //range of the weapon, abstract value
    private final int clipSize;
    private final double reloadTime;
    private final GunType gunType;
    private String name;

    public GunItem(double damage, double armorPen, double rps, double dropoff, int spray, int range, int clipSize, double reloadTime, GunType gunType) {
        super(new Item.Settings().maxCount(1).group(FlytreGuns.TAB));
        this.damage = damage;
        this.armorPen = armorPen;
        this.rps = rps;
        this.dropoff = dropoff;
        this.spray = spray;
        this.range = range;
        this.clipSize = clipSize;
        this.reloadTime = reloadTime;
        this.gunType = gunType;
        this.name = null;
        GUNS.add(this);
    }

    public static GunItem randomGun() {
        int size = GUNS.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for (GunItem obj : GUNS) {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }

    //Has ammo
    public static boolean hasAmmo(Item ammo, PlayerEntity playerEntity) {
        if (!playerEntity.isCreative()) {
            for (ItemStack item : playerEntity.inventory.main) {
                if (!item.isEmpty() && item.getItem() == ammo) {
                    return true;
                }
            }
        }
        return playerEntity.isCreative();
    }

    public static void attemptEarlyReload(PlayerEntity player) {

        //get the gun
        ItemStack stack = player.getOffHandStack();
        if (!(stack.getItem() instanceof GunItem))
            stack = player.getMainHandStack();

        if (!(stack.getItem() instanceof GunItem))
            return;
        GunItem gun = (GunItem) stack.getItem();

        CompoundTag tag = stack.getOrCreateTag();
        //reload!
        if (hasAmmo(gun.getAmmoItem(), player)) {
            tag.putInt("reload", (int) (gun.getReloadTime() * 20));
            int clip = tag.contains("clip") ? tag.getInt("clip") : gun.getClipSize();
            tag.putInt("partialClip", clip);
            tag.putInt("clip", 0);
        }

    }

    public GunType getType() {
        return gunType;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (!(entity instanceof PlayerEntity))
            return;

        PlayerEntity player = (PlayerEntity) entity;

        CompoundTag tag = stack.getOrCreateTag();
        int clip = tag.contains("clip") ? tag.getInt("clip") : getClipSize();
        int reload = tag.contains("reload") ? tag.getInt("reload") : -1;
        if (reload != -1 || clip == 0) {
            int partialClip = tag.contains("partialClip") ? tag.getInt("partialClip") : 0;

            if (reload >= 0) {
                double ammoCalc = (double) getClipSize() / ((int) (getReloadTime() * 20));
                ammoCalc *= (int) (getReloadTime() * 20) - reload;
                while (partialClip < Math.floor(ammoCalc)) {
                    if (hasAmmo(getAmmoItem(), player)) {
                        removeAmmo(getAmmoItem(), player);
                        partialClip++;
                    } else {
                        tag.putDouble("clip", partialClip);
                        tag.remove("reload");
                        tag.remove("partialClip");
                        reload = -1;
                        partialClip = 0;
                        break;
                    }
                }
                tag.putInt("partialClip", partialClip);
            }

            if (reload == 0) {
                tag.putDouble("clip", partialClip);
                tag.remove("reload");
                tag.remove("partialClip");
            } else if (reload != -1) {
                tag.putInt("reload", reload - 1);

            } else if (hasAmmo(getAmmoItem(), player)) {
                tag.putInt("reload", (int) (getReloadTime() * 20));
                tag.putInt("partialClip", clip);
                tag.putInt("clip", 0);
            }

        }
        int cooldown = tag.contains("cooldown") ? tag.getInt("cooldown") : 0;

        if (cooldown > 0)
            tag.putDouble("cooldown", cooldown - 1);

    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {


        if (world != null && !world.isClient) {

            ItemStack stack = user.getStackInHand(hand);
            CompoundTag tag = stack.getOrCreateTag();
            int clip = tag.contains("clip") ? tag.getInt("clip") : getClipSize();
            int reload = tag.contains("reload") ? tag.getInt("reload") : -1;
            int maxCd = (int) (20 / rps - 1);
            int cooldown = tag.contains("cooldown") ? tag.getInt("cooldown") : 0;

            if (clip > 0 && reload == -1 && cooldown <= 0) {
                fireBullet(world, user, hand);
                playSound(world, user);
                tag.putInt("cooldown", maxCd);
                tag.putDouble("clip", clip - 1);
            } else if (clip == 0 && cooldown == 0) {
                world.playSound(
                        null,
                        user.getBlockPos(),
                        Sounds.DRY_FIRE_EVENT,
                        SoundCategory.PLAYERS,
                        1f,
                        1f
                );
                tag.putInt("cooldown", maxCd);
            }
            return TypedActionResult.pass(stack);
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    private void playSound(World world, PlayerEntity user) {

        SoundEvent event;
        switch (getType()) {
            case SNIPER:
                event = Sounds.SNIPER_FIRE_EVENT;
                break;
            case RIFLE:
            case SMG:
            case MINIGUN:
                event = Sounds.RIFLE_FIRE_EVENT;
                break;
            case SHOTGUN:
                event = Sounds.SHOTGUN_FIRE_EVENT;
                break;
            case SLIME:
                event = Sounds.SLIME_FIRE_EVENT;
                break;
            case ROCKET:
                event = Sounds.ROCKET_FIRE_EVENT;
                break;
            case SHOCKER:
                event = Sounds.SHOCKER_FIRE_EVENT;
                break;
            default:
                event = Sounds.PISTOL_FIRE_EVENT;
        }

        world.playSound(
                null,
                user.getBlockPos(),
                event,
                SoundCategory.PLAYERS,
                1f,
                1f
        );
    }

    public Item getAmmoItem() {
        Item ammo;
        switch (getType()) {
            case SNIPER:
                ammo = FlytreGuns.SNIPER_AMMO;
                break;
            case SHOTGUN:
                ammo = FlytreGuns.SHOTGUN_SHELL;
                break;
            case ROCKET:
                ammo = FlytreGuns.ROCKET_AMMO;
                break;
            default:
                ammo = FlytreGuns.BASIC_AMMO;
        }
        return ammo;
    }

    protected void removeAmmo(Item ammo, PlayerEntity playerEntity) {
        if (!playerEntity.isCreative()) {
            for (ItemStack item : playerEntity.inventory.main) {
                if (item.getItem() == ammo) {
                    item.decrement(1);
                    break;
                }
            }
        }
    }


    //Override for custom behavior
    protected void fireBullet(World world, PlayerEntity user, Hand hand) {
        Bullet bulletEntity = new Bullet(user, world);
        bulletEntity.setPos(user.getX(), user.getEyeY(), user.getZ());
        bulletEntity.setVelocity(getRotationVectorSpray(user).multiply(5));
        bulletEntity.setInitialPos(new Vec3d(user.getX(), user.getEyeY(), user.getZ()));
        bulletEntity.setProperties(damage, armorPen, dropoff, range);
        bulletSetup(world, user, hand, bulletEntity);
        world.spawnEntity(bulletEntity);
    }

    public void bulletSetup(World world, PlayerEntity user, Hand hand, Bullet bullet) {

    }

    public double getDamage() {
        return damage;
    }

    public double getArmorPen() {
        return armorPen;
    }

    public double getRps() {
        return rps;
    }

    public double getDropoff() {
        return dropoff;
    }

    public int getSpray() {
        return spray;
    }

    public int getRange() {
        return range;
    }

    public int getClipSize() {
        return clipSize;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    protected Vec3d getRotationVectorSpray(PlayerEntity user) {
        int tSpray = spray;
        if (user.isSneaking())
            tSpray = Math.max(0, Math.min((spray * 2) / 5, spray - 5));
        return getRotationVector((float) (user.pitch + (Math.random() * (tSpray + 1)) - 1 - tSpray / 2.0), (float) (user.yaw + (Math.random() * (tSpray + 1)) - 1 - tSpray / 2.0));
    }

    protected Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    @Environment(EnvType.CLIENT)
    public Text getName() {
        return new TranslatableText(name == null ? this.getTranslationKey() : name);
    }

    public void setName(String name) {
        this.name = name;
    }

    protected String getOrCreateTranslationKey() {
        if (name != null)
            return name;
        return super.getOrCreateTranslationKey();
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        if (!(stack.getItem() instanceof GunItem))
            return;

        GunItem g = (GunItem) stack.getItem();

        tooltip.add(g.gunType == GunType.ROCKET ?
                new TranslatableText("text.fguns.tooltip.damage.varies") :
                new TranslatableText(g.gunType == GunType.SHOTGUN ? "text.fguns.tooltip.damage.shotgun" : "text.fguns.tooltip.damage", String.format("%.1f", g.getDamage()))
        );

        int fireSpeed = (20 / (int) (20 / g.getRps()));
        tooltip.add(new TranslatableText("text.fguns.tooltip.rps", fireSpeed == 0 ? String.format("%.1f", g.getRps()) : fireSpeed));

        tooltip.add(new TranslatableText("text.fguns.tooltip.range", g.getRange()));

        if (g.getArmorPen() > 0)
            tooltip.add(new TranslatableText("text.fguns.tooltip.armor_pierce", (int) (g.getArmorPen() * 100)));

        if (g.getDropoff() > 0)
            if (g.getDropoff() > 0)
                tooltip.add(new TranslatableText("text.fguns.tooltip.dropoff", (int) (100 * (g.getDropoff() * 100)) / 100));
            else if (g.getDropoff() < 0)
                tooltip.add(new TranslatableText("text.fguns.tooltip.dropoff.increase", ((int) (100 * (Math.abs(g.getDropoff()) * 100))) / 100));

        tooltip.add(new LiteralText("§7Ammo: §c").append(new TranslatableText(getAmmoItem().getTranslationKey())));

        super.appendTooltip(stack, world, tooltip, context);
    }
}
