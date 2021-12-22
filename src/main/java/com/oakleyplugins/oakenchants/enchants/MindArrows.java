package com.oakleyplugins.oakenchants.enchants;

import com.oakleyplugins.oakenchants.OakEnchants;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MindArrows extends CustomEnchant {
    public static List<Projectile> HOMING;
    private static BukkitTask TASK;
    private static long TICKS;

    public MindArrows() {
        super("mind_arrows");
    }

    @Override
    public String getName() {
        return "Mind Arrows";
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
        return new EnchantmentTarget[] { EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW };
    }

    @Override
    public boolean isEnchantItem(ItemStack stack) {
        return stack.getType().equals(Material.PLAYER_HEAD);
    }

    void load() {
        HOMING = new ArrayList<>();
        TASK = Bukkit.getScheduler().runTaskTimer(OakEnchants.getInstance(), this::tick, 0, 1);
        TICKS = 0;
    }

    void unload() {
        HOMING.clear();
        if(TASK != null) TASK.cancel();
    }

    private void tick() {
        HOMING.forEach(this::homingTick);
        TICKS++;
    }

    private void homingTick(Projectile homing) {
        Bukkit.getScheduler().runTask(OakEnchants.getInstance(), () -> {
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


    @Override
    public void onShootBow(EntityShootBowEvent e, ItemStack item, int level) {
        if (!(e.getProjectile() instanceof Projectile)) return;
        Projectile proj = (Projectile) e.getProjectile();
        LivingEntity shooter = e.getEntity();
        double chance = ((double) level / 5D);
        if (Math.random() > chance) return;
        HOMING.add(proj);
        shooter.getWorld().playSound(shooter.getEyeLocation().add(0, 1, 0), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 2f);
        shooter.getWorld().spawnParticle(Particle.HEART, shooter.getEyeLocation().add(0, 0.5, 0), 1);
    }
}
