package io.greitan.uniworlds;

import lombok.Getter; 
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;
import io.greitan.uniworlds.listeners.MainListener;
import org.bukkit.Bukkit;

public class SavePlayers extends JavaPlugin {
    @Getter
    private static SavePlayers instance;
    @Getter
    private static Logger jlogger;
    @Override
    public void onEnable() {
        instance = this; 
        jlogger = super.getLogger();

      	jlogger.info("Plugin loaded!");

        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
    }
}