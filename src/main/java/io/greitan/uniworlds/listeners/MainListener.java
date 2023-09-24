package io.greitan.uniworlds.listeners;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.File;
import java.io.IOException;

public class MainListener implements Listener {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String dataFolderPath = "./plugins/SavePlayers/";

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        File playerDataFile = new File(dataFolderPath + "player-" + playerName + ".json");
    
        if (playerDataFile.exists()) {
            try {
                ObjectNode playerData = objectMapper.readValue(playerDataFile, ObjectNode.class);
    
                if (!playerData.has("itemsGiven")) {
                    playerData.put("itemsGiven", false);
    
                    ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
                    writer.writeValue(playerDataFile, playerData);
                }
    
                boolean itemsGiven = playerData.get("itemsGiven").asBoolean(false);
    
                if (!itemsGiven) {
                    double x = playerData.get("data").get("pos").get("x").asDouble();
                    double y = playerData.get("data").get("pos").get("y").asDouble();
                    double z = playerData.get("data").get("pos").get("z").asDouble();
    
                    Location location = new Location(player.getWorld(), x, y, z);
                    player.teleport(location);
    
                    // TODO: Выдача
    
                    playerData.put("itemsGiven", true);
    
                    ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
                    writer.writeValue(playerDataFile, playerData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            TextComponent message = Component.text("U dnt have NBT.").color(NamedTextColor.GREEN);
            player.sendMessage(message);
        }
    }
}
