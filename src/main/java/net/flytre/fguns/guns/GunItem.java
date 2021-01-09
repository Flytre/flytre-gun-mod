package net.flytre.fguns.guns;

import net.flytre.fguns.FlytreGuns;
import net.flytre.fguns.Sounds;
import net.flytre.fguns.entity.Bullet;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GunItem extends Item {

    private final double damage; //Base damage
    private final double armorPen; //Armor Penetration, eg 0.75 ignores 75% of armor
    private final double rps; //Rounds per second, eg 2 would fire a bullet every 10 ticks
    private final double dropoff; //Damage dropoff every block, %, eg 0.01 would be 1% less damage/block
    private final int spray; //Accuracy of the weapon, high spray = inaccurate
    private final int range; //range of the weapon, abstract value
    private final int clipSize;
    private final double reloadTime;
    private final GunType gunType;

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
    }

    public GunType getType() {
        return gunType;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        CompoundTag tag = stack.getOrCreateTag();
        int clip = tag.contains("clip") ? tag.getInt("clip") : getClipSize();
        if (clip == 0) {
            int reload = tag.contains("reload") ? tag.getInt("reload") : -1;
            if (reload == 0) {
                tag.putDouble("clip", getClipSize());
                tag.remove("reload");
            } else if (reload != -1) {
                tag.putInt("reload", reload - 1);
            } else {
                tag.putInt("reload", (int) (getReloadTime() * 20));
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
            } else if(clip == 0 && cooldown == 0) {
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
        switch(getType()) {
            case SNIPER:
                event = Sounds.SNIPER_FIRE_EVENT;
                break;
            case RIFLE:
            case SMG:
                event = Sounds.RIFLE_FIRE_EVENT;
                break;
            case SHOTGUN:
                event = Sounds.SHOTGUN_FIRE_EVENT;
                break;
            case SLIME:
                event = Sounds.SLIME_FIRE_EVENT;
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
        return getRotationVector((float) (user.pitch + (Math.random() * (spray + 1)) - 1 - spray / 2.0), (float) (user.yaw + (Math.random() * (spray + 1)) - 1 - spray / 2.0));
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

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

        if (!(stack.getItem() instanceof GunItem))
            return;

        GunItem g = (GunItem) stack.getItem();

        tooltip.add(Text.of(g.getType().equals(GunType.PISTOL) ? "§2Secondary" : "§2Primary"));
        tooltip.add(Text.of(
                g.getType() == GunType.ROCKET ?
                        "§7Damage: §cVaries" :
                        "§7Damage: §c" + String.format("%.1f",g.getDamage()) +
                                (g.getType() == GunType.SHOTGUN ? " / pellet" : "")
        ));

        int fireSpeed = (20 / (int) (20 / g.getRps()));
        tooltip.add(Text.of(fireSpeed == 0 ? "§7RPS: §c" + String.format("%.1f",g.getRps()) : "§7RPS: §c" + fireSpeed));

        tooltip.add(Text.of("§7Range: §c" + g.getRange() + " blocks"));

        if (g.getArmorPen() > 0)
            tooltip.add(Text.of("§7Armor Pierce: §c" + (int) (g.getArmorPen()*100) + "%"));

        if (g.getDropoff() > 0)
            tooltip.add(Text.of("§7Dropoff: §c" + ((int) (100 * (g.getDropoff() * 100))) / 100 + " % per block"));
        else if (g.getDropoff() < 0)
            tooltip.add(Text.of("§7Damage Increase: §c" + ((int) (100 * (Math.abs(g.getDropoff()) * 100))) / 100 + " % per block"));


        super.appendTooltip(stack, world, tooltip, context);
    }
}
