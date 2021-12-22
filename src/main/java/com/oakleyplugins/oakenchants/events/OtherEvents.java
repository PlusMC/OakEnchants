package com.oakleyplugins.oakenchants.events;

import com.oakleyplugins.oakenchants.enchants.MindArrows;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class OtherEvents implements Listener {
    @EventHandler
    public void onHitGround(ProjectileHitEvent e) {
        if (MindArrows.HOMING.remove(e.getEntity())) {
            if (e.getHitEntity() != null)
                e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 2f);
        }
    }
}
