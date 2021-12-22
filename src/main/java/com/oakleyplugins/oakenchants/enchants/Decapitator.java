package com.oakleyplugins.oakenchants.enchants;

import com.oakleyplugins.oakenchants.OakEnchants;
import com.oakleyplugins.oakenchants.Utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

import static com.oakleyplugins.oakenchants.Utils.Utils.enchantCustom;
import static com.oakleyplugins.oakenchants.Utils.Utils.getSkull;

public class Decapitator extends CustomEnchant {

    public Decapitator() {
        super("decapitator");
    }

    @Override
    public String getName() {
        return "Decapitator";
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public EnchantmentTarget[] getEnchantTargets() {
        return new EnchantmentTarget[] { EnchantmentTarget.WEAPON, EnchantmentTarget.TOOL };
    }

    @Override
    public boolean isEnchantItem(ItemStack stack) {
        return stack.getType().equals(Material.END_CRYSTAL);
    }

    @Override
    public void onDamageEntity(EntityDamageByEntityEvent e, ItemStack item, int level) {
        if (e.getEntity() instanceof Player)
            if (Utils.isEventFatal(e)) {
                enchantCustom(item, level - 1, this);
                playDeathEffect((Player) e.getEntity());
            }
    }


    //anim
    //TODO: make this not suck
    private void playDeathEffect(Player player) {
        Location loc = player.getEyeLocation();
        World world = loc.getWorld();
        world.playSound(loc, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 1f);
        createArmorStand(loc.subtract(0, 0.5, 0), getSkull(player));
    }

    private void createArmorStand(Location loc, ItemStack head) {
        World world = loc.getWorld();
        ArmorStand stand = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setSmall(true);
        stand.setInvulnerable(true);
        stand.getEquipment().setHelmet(head);
        helixEffect(stand);
    }

    private void helixEffect(ArmorStand stand) {//double animation tornado
        Location loc = stand.getLocation();
        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            stand.getWorld().spawnParticle(Particle.SOUL, loc.clone().add(r.nextGaussian(), r.nextGaussian(), r.nextGaussian()), 1);
        }
        double expandedRadius = 2.25;
        double y = 0;
        int delay = 0;
        boolean expand = true;
        double rand = -0.5 + (0.1 - -0.5) * r.nextDouble();
        for (double radius = 0.1; radius >= rand; y += 0.1) {//queueing animating loop (stops when radius hits 0)
            if (expand) { //do if expand is true
                radius += 0.1 + (0.15 * r.nextDouble()); //radius math (expanding)
                expand = (expandedRadius >= radius);//start shrinking once it hits its expanded radius (set expand to false)
            } else radius -= 0.05 + (0.15 * r.nextDouble()); //radius math (shrinking)
            double x = radius * Math.cos(y * 2.1);
            double z = radius * Math.sin(y * 2.1);//helix math
            Location fLoc = loc.clone().add(x, y, z); //location of where armor stand needs to be
            double finalRadius = radius;
            Bukkit.getScheduler().runTaskLater(OakEnchants.getInstance(), () -> {//movement done every tick
                Location rotate = stand.getLocation().clone();
                rotate.setYaw((float) (rotate.getYaw() + (10 * (finalRadius + 2))));
                stand.teleport(rotate);
                nudge(stand, fLoc, stand.getLocation().distance(fLoc));//moves armor stand to the location
                stand.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, stand.getLocation(), 1); //spawn particle effect
            }, delay);
            delay++;
            //loc.getWorld().spawnParticle(Particle.BARRIER,fLoc,20); //debug
        }
        Bukkit.getScheduler().runTaskLater(OakEnchants.getInstance(), () -> { //drop item after animating
            Location cur = stand.getEyeLocation();
            ItemStack skull = stand.getEquipment().getHelmet();
            stand.remove();
            cur.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, cur, 1);
            cur.getWorld().playSound(cur, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
            cur.getWorld().dropItem(cur, skull);
        }, delay);
    }

    private void nudge(LivingEntity entity, Location loc, double strength) {
        Vector goTo = loc.toVector().subtract(entity.getLocation().toVector());
        goTo.normalize();
        entity.setVelocity(goTo.multiply(strength));
    }
}
