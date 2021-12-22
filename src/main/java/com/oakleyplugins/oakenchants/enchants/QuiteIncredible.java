package com.oakleyplugins.oakenchants.enchants;

import com.oakleyplugins.oakenchants.OakEnchants;
import com.oakleyplugins.oakenchants.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.oakleyplugins.oakenchants.Utils.Utils.spawnParticles;

public class QuiteIncredible extends CustomEnchant {
    private static List<LivingEntity> NOTES;

    public QuiteIncredible() {
        super("quite_incredible");
    }

    @Override
    public EnchantmentTarget[] getEnchantTargets() {
        return new EnchantmentTarget[] { EnchantmentTarget.WEAPON, EnchantmentTarget.TOOL };
    }

    @Override
    public boolean isEnchantItem(ItemStack stack) {
        return stack.getType().equals(Material.JUKEBOX);
    }

    @Override
    public String getName() {
        return "Quite Incredible";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    void load() {
        NOTES = new ArrayList<>();
    }

    void unload() {
        NOTES.clear();
    }

    @Override
    public void onDamageEntity(EntityDamageByEntityEvent e, ItemStack item, int level) {
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
            Bukkit.getScheduler().runTaskLater(OakEnchants.getInstance(), () -> {
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    pl.stopSound(s);
                }
                NOTES.remove(entity);
            }, 140);
            spawnParticles(entity, 140);
        }
    }
}
