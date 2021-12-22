package com.oakleyplugins.oakenchants;

import com.oakleyplugins.oakenchants.enchants.CustomEnchant;
import com.oakleyplugins.oakenchants.events.EnchantEvents;
import com.oakleyplugins.oakenchants.events.OtherEvents;
import com.oakleyplugins.oakenchants.events.PlayerInteractEvents;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class OakEnchants extends JavaPlugin {
    static final List<Listener> LISTENERS = Arrays.asList(
            new EnchantEvents(),
            new PlayerInteractEvents(),
            new OtherEvents()
    );

    @Override
    public void onEnable() {
        CustomEnchant.loadAll();

        LISTENERS.forEach(
                listener -> getServer().getPluginManager().registerEvents(listener, this)
        );
    }

    public static OakEnchants getInstance() {
        return JavaPlugin.getPlugin(OakEnchants.class);
    }

    @Override
    public void onDisable() {
        CustomEnchant.unloadAll();
    }


}
