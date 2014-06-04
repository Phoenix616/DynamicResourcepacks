package de.rene_zeidler.dynamicresourcepacks.resourcepacks;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;

public class PlayerManager {
	private DynamicResourcepacks plugin;
	private Configuration config;
	
	private HashMap<UUID, LocalPlayer> localPlayers;
	
	public PlayerManager(DynamicResourcepacks plugin) {
		this.plugin = plugin;
		this.config = this.plugin.getConfig();
		
		this.localPlayers = new HashMap<UUID, LocalPlayer>();
	}
	
	public LocalPlayer getLocalPlayer(OfflinePlayer player) {
		UUID playerUUID = player.getUniqueId();
		if(!this.localPlayers.containsKey(playerUUID))
			this.localPlayers.put(playerUUID, new LocalPlayer(this.plugin, player));
		
		return this.localPlayers.get(playerUUID);
	}
	
	public void loadFromConfig() {
		ConfigurationSection section = this.config.getConfigurationSection("players");
		if(section != null) {
			for(String uuid : section.getKeys(false)) {
				ConfigurationSection playerSection = section.getConfigurationSection(uuid);
				try {
					OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
					if(player != null) {
						if (this.localPlayers.containsKey(player.getUniqueId()))
							this.plugin.getLogger().warning("Config contained duplicate player " + player.getName() + ". Ignoring duplicate entry");
						else
							this.localPlayers.put(player.getUniqueId(), new LocalPlayer(this.plugin, player, playerSection));
					}
				} catch(IllegalArgumentException ex) {
					//TODO: Convert previously stored playername
//					@SuppressWarnings("deprecation")
//					OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
//					if(player != null) {
//						section.set(uuid, null);
//						this.setResourcepack(player, this.getResourcepackForName(playerSection.getString("pack")));
//						this.setLocked(player, playerSection.getBoolean("locked"));
//						this.saveConfigForPlayer(player);
//					}
				}
			}
		}
	}
}
