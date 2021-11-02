package com.oakleyplugins.oakenchants.enchants;

import com.oakleyplugins.oakenchants.Utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.oakleyplugins.oakenchants.OakEnchants.PLUGIN;
import static com.oakleyplugins.oakenchants.Utils.Anim.playDeathEffect;
import static com.oakleyplugins.oakenchants.Utils.Utils.handleLore;
import static com.oakleyplugins.oakenchants.Utils.Utils.spawnParticles;

public class Enchants {
    public static final CustomEnchant DECAPITATOR = new CustomEnchant("decapitator", "Decapitator", 5, Material.END_CRYSTAL, EnchantmentTarget.WEAPON, EnchantmentTarget.TOOL);
    public static final CustomEnchant MIND_OF_ITS_OWN = new CustomEnchant("mindofitsown", "Mind of Its Own", 5, Material.PLAYER_HEAD, EnchantmentTarget.BOW, EnchantmentTarget.CROSSBOW);
    public static final CustomEnchant HONESTLY_THIS_IS_QUITE_INCREDIBLE = new CustomEnchant("honestlythisisquiteincredible", "Honestly This Is Quite Incredible", 1, Material.JUKEBOX, EnchantmentTarget.WEAPON, EnchantmentTarget.TOOL);
    public static final List<CustomEnchant> ENCHANTS = Arrays.asList(DECAPITATOR, MIND_OF_ITS_OWN, HONESTLY_THIS_IS_QUITE_INCREDIBLE);
    public static List<Projectile> HOMING;
    private static List<LivingEntity> NOTES;

    public static void register() {
        NOTES = new ArrayList<>();
        HOMING = new ArrayList<>();
        boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).containsAll(ENCHANTS);

        if (!registered)
            ENCHANTS.forEach(Enchants::registerEnchant);


        registerEvents();
    }

    public static void registerEnchant(Enchantment enchant) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchant);
            PLUGIN.getLogger().info("Registered Enchantment " + enchant.getKey());
        } catch (Exception e) {
            PLUGIN.getLogger().warning("Could not register Enchantment: " + enchant.getKey());
        }
    }

    static void registerEvents() {
        DECAPITATOR.setOnDamage(e -> {
            if (e.getEntity() instanceof Player)
                if (Utils.isEventFatal(e)) {
                    handleLore((LivingEntity) e.getDamager());
                    playDeathEffect((Player) e.getEntity());
                }
        });

        HONESTLY_THIS_IS_QUITE_INCREDIBLE.setOnDamage(e -> {
            if (Utils.isEventFatal(e)) {
                LivingEntity entity = (LivingEntity) e.getDamager();
                if (!(e.getEntity() instanceof Player)) return;

                if (NOTES.contains(entity))
                    return;

                NOTES.add(entity);
                World world = entity.getWorld();
                Random r = new Random();
                float rf = r.nextFloat() * 2;
                Sound[] discs = Arrays.stream(Sound.values()).filter(s -> s.name().startsWith("MUSIC_DISC_")).toArray(Sound[]::new);
                Sound s = discs[r.nextInt(discs.length)];
                world.playSound(entity.getLocation(), s, 2f, rf);
                Bukkit.getScheduler().runTaskLater(PLUGIN, () -> {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        pl.stopSound(s);
                    }
                    NOTES.remove(entity);
                }, 140);
                spawnParticles(entity, 140);
            }
        });

        MIND_OF_ITS_OWN.setOnBow(e -> {
            if (!(e.getProjectile() instanceof Projectile)) return;
            Projectile proj = (Projectile) e.getProjectile();
            LivingEntity shooter = e.getEntity();
            int lvl = e.getBow().getEnchantmentLevel(MIND_OF_ITS_OWN);
            double chance = ((double) lvl / 5D);
            if (Math.random() > chance) return;
            HOMING.add(proj);
            shooter.getWorld().playSound(shooter.getEyeLocation().add(0, 1, 0), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 2f);
            shooter.getWorld().spawnParticle(Particle.HEART, shooter.getEyeLocation().add(0, 0.5, 0), 1);
        });
    }


}
