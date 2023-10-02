package io.greitan.uniworlds.listeners;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import de.tr7zw.nbtapi.*;
import de.tr7zw.nbtapi.iface.*;

import java.io.File;
import java.io.IOException;

public class MainListener implements Listener {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String dataFolderPath = "./plugins/SavePlayers/";

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        String playerName = player.getName();
        File playerDataFile = new File(dataFolderPath + "player-"+playerName+".json");

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
                    //Teleport section.
                    double x = playerData.get("data").get("pos").get("x").asDouble();
                    double y = playerData.get("data").get("pos").get("y").asDouble();
                    double z = playerData.get("data").get("pos").get("z").asDouble();
    
                    Location location = new Location(player.getWorld(), x, y, z);
                    player.teleport(location);
                    player.sendMessage("U teleported.");

                    //NBT give seсtion.
                    JsonNode data = playerData.get("data");

                    Bukkit.getLogger().info("Check player inventory.");
                    if (data.has("inv")) {
                        for (int slot = 1; slot <= 36; slot++) {
                            String slotKey = String.valueOf(slot);
                            
                            if (data.get("inv").has(slotKey)){
                                String nbtData = data.get("inv").get(slotKey).asText();

                                ReadWriteNBT nbt = NBT.parseNBT(nbtData);
                                String str = nbt.toString();
                                ItemStack itemStack = NBT.itemStackFromNBT(nbt);

                                player.getInventory().setItem(slot - 1, itemStack);
                            }
                        }

                        player.updateInventory();
                        Bukkit.getLogger().info("Inventory give to: " + playerName + "success!");
                    }

                    if (data.has("echest")) {
                        for (int slot = 1; slot <= 27; slot++) {
                            String slotKey = String.valueOf(slot);

                            if (data.get("echest").has(slotKey)) {
                                String nbtData = data.get("echest").get(slotKey).asText();

                                ReadWriteNBT nbt = NBT.parseNBT(nbtData);
                                ItemStack itemStack = NBT.itemStackFromNBT(nbt);

                                player.getEnderChest().setItem(slot -1, itemStack);
                            }
                        }
                        player.updateInventory();
                        Bukkit.getLogger().info("Echest give to: " + playerName + "success!");
                    }

                    if (data.has("armor")) {
                        for (int slot = 36; slot <= 39; slot++) {
                            String slotKey = String.valueOf(slot);

                            if (data.get("armor").has(slotKey)){
                                String nbtData = data.get("armor").get(slotKey).asText();

                                ReadWriteNBT nbt = NBT.parseNBT(nbtData);
                                ItemStack itemStack = NBT.itemStackFromNBT(nbt);

                                player.getInventory().setItem(slot, itemStack);
                            }
                        }
                        player.updateInventory();
                        Bukkit.getLogger().info("Armor give to: " + playerName + " success!");
                    }
                    playerData.put("itemsGiven", true);
                    ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
                    writer.writeValue(playerDataFile, playerData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            player.sendMessage("U dnt have NBT.");
        }
    }
}