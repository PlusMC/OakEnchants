package com.oakleyplugins.oakenchants.events;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static com.oakleyplugins.oakenchants.enchants.CustomEnchant.ENCHANTS;

public class PlayerInteractEvents implements org.bukkit.event.Listener {
    @EventHandler
    public void onKill(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getDamager() instanceof LivingEntity)) return;
        LivingEntity p = (LivingEntity) e.getDamager();
        if (p.getEquipment() == null) return;
        ItemStack item = p.getEquipment().getItemInMainHand();
        if (p.getEquipment().getItemInMainHand().getItemMeta() == null) return;
        ENCHANTS.forEach(enchant -> {
            if (item.containsEnchantment(enchant))
                enchant.onDamageEntity(e, item, item.getEnchantmentLevel(enchant));
        });
    }

    @EventHandler
    public void onBow(EntityShootBowEvent e) {
        if (e.isCancelled()) return;
        if (e.getBow() == null) return;
        ENCHANTS.forEach(enchant -> {
            if (e.getBow().containsEnchantment(enchant))
                enchant.onShootBow(e, e.getBow(), e.getBow().getEnchantmentLevel(enchant));
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getPersistentDataContainer().has(NamespacedKey.minecraft("armorstandanim"), PersistentDataType.INTEGER)) {
            e.setCancelled(true);
        }
    }

    public static boolean isEventFatal(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof LivingEntity)
            return ((LivingEntity) e.getEntity()).getHealth() - e.getFinalDamage() <= 0;
        return false;
    }

}
