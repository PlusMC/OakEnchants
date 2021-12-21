package com.oakleyplugins.oakenchants;

import com.oakleyplugins.oakenchants.enchants.Enchants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.awt.*;

import static com.oakleyplugins.oakenchants.enchants.Enchants.HOMING;

public final class OakEnchants extends JavaPlugin {

    public static Plugin PLUGIN;
    static long TICKS = 0;
    static BukkitTask TICKING_TASK;

    @Override
    public void onEnable() {
        PLUGIN = this;
        Enchants.register();
        getServer().getPluginManager().registerEvents(new Events(), PLUGIN);
        TICKS = 0;
        TICKING_TASK = Bukkit.getScheduler().runTaskTimer(PLUGIN, this::tick, 0, 1);
    }

    @Override
    public void onDisable() {
        TICKING_TASK.cancel();
    }


    public void tick() {
        HOMING.forEach(this::homingTick);
        TICKS++;
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
        for (Entity e : homing.getNearbyEntities(15, 15, 15)) {
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
        if(TICKS % 5 == 0) {
            Player p = (Player) cEntity;
            p.playSound(cEntity.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0.5f);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "Arrow is homing on you!"));
        }
        Vector tVector = ((LivingEntity) cEntity).getEyeLocation().toVector();
        Vector aVector = homing.getLocation().toVector();
        tVector.subtract(aVector);
        tVector.normalize();
        homing.setVelocity(tVector.multiply(0.75));

        //particle spiral
        double x = homing.getLocation().getX();
        double y = homing.getLocation().getY();
        double z = homing.getLocation().getZ();
        double x2 = x + (Math.cos(TICKS) * 0.5);
        double y2 = y + (Math.sin(TICKS) * 0.5);
        double z2 = z + (Math.cos(TICKS) * 0.5);

        homing.getWorld().spawnParticle(Particle.REDSTONE, new Location(homing.getWorld(), x2, y2, z2), 0, new Particle.DustOptions(Color.RED,1));
        homing.getWorld().playSound(homing.getLocation(), Sound.BLOCK_GRASS_BREAK, 1f, 2f);
    }

}
