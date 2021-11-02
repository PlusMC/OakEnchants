package com.oakleyplugins.oakenchants;

import com.oakleyplugins.oakenchants.enchants.Enchants;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import static com.oakleyplugins.oakenchants.enchants.Enchants.HOMING;

public final class OakEnchants extends JavaPlugin {

    public static Plugin PLUGIN;
    static BukkitTask TICKING_TASK;

    @Override
    public void onEnable() {
        PLUGIN = this;
        Enchants.register();
        getServer().getPluginManager().registerEvents(new Events(), PLUGIN);
        TICKING_TASK = Bukkit.getScheduler().runTaskTimer(PLUGIN, this::tick, 0, 1);
    }

    @Override
    public void onDisable() {
        TICKING_TASK.cancel();
    }


    public void tick() {
        HOMING.forEach(this::homingTick);
    }

    public void homingTick(Projectile homing) {
        Bukkit.getScheduler().runTask(PLUGIN, () -> {
            if (homing.getTicksLived() > 200 || !homing.isValid()) {
                HOMING.remove(homing);
            }
        });
        if (!(homing.getShooter() instanceof LivingEntity)) return;
        LivingEntity shooter = (LivingEntity) homing.getShooter();
        Entity cEntity = null;
        double cDist = Double.MAX_VALUE;
        for (Entity e : homing.getNearbyEntities(10, 10, 10)) {
            if (e == shooter) continue;
            if (!shooter.hasLineOfSight(e)) continue;
            if (!(e instanceof Player)) continue;
            Vector c = shooter.getEyeLocation().toVector().subtract(((LivingEntity) e).getEyeLocation().toVector());// Get vector between you and other
            Vector d = shooter.getEyeLocation().getDirection();
            double delta = c.dot(d);
            if (delta > 0) continue;
            double dist = e.getLocation().distance(homing.getLocation());
            if (dist < cDist) {
                cDist = dist;
                cEntity = e;
            }
        }
        if (cEntity == null) return;
        Vector tVector = ((LivingEntity) cEntity).getEyeLocation().toVector();
        Vector aVector = homing.getLocation().toVector();
        tVector.subtract(aVector);
        tVector.normalize();
        homing.setVelocity(tVector);
        homing.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, homing.getLocation(), 1);
        homing.getWorld().playSound(homing.getLocation(), Sound.BLOCK_ROOTED_DIRT_BREAK, 1f, 2f);
    }

}
