package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin to set the server resourcepack for players dynamically.
 * 
 * @author René Zeidler
 * @version 0.0.2
 */
public class DynamicResourcepacks extends JavaPlugin {
	private ResourcepackManager packManager;
	private PlayerManager playerManager;
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		
		this.packManager = new ResourcepackManager(this);
		this.playerManager = new PlayerManager(this, this.packManager);
	}
 
	@Override
	public void onDisable(){
		this.saveConfig();
		
		this.playerManager = null;
		this.packManager = null;
		
		this.getCommand("dynamicresourcepacks").setExecutor(this.playerManager);
	}
	
	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}
}
