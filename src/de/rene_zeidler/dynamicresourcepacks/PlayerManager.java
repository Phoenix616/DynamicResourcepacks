package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.configuration.Configuration;

/**
 * Handles all actions that alter the resourcepack of players, including checks such as permissions or locked packs.
 * 
 * @author René Zeidler
 * @version 0.0.1
 */
public class PlayerManager {
	private DynamicResourcepacks plugin;
	private Configuration config;
	private ResourcepackManager packManager;
	
	private static PlayerManager instance;
	
	public PlayerManager(DynamicResourcepacks plugin, ResourcepackManager packManager) {
		PlayerManager.instance = this;
		
		this.plugin = plugin;
		this.config = this.plugin.getConfig();
		this.packManager = packManager;
	}
	
	public static PlayerManager getInstance() {
		return PlayerManager.instance;
	}
	
	protected ResourcepackManager getResourcepackManager() {
		return this.packManager;
	}
	
}
