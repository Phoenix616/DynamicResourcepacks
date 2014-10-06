package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin to set the server resourcepack for players dynamically.
 * 
 * @author René Zeidler
 * @version 0.1.3
 */
public class DynamicResourcepacks extends JavaPlugin {
	private ResourcepackManager packManager;
	private PlayerListener playerListener;
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		
		this.packManager = new ResourcepackManager(this);
		this.playerListener = new PlayerListener(this);
		
		this.getServer().getPluginManager().registerEvents(this.playerListener, this);
		this.getCommand("dynamicresourcepacks").setExecutor(this.playerListener);
		this.getCommand("setresourcepack")     .setExecutor(this.playerListener);
		
		this.packManager.loadFromConfig();
	}
 
	@Override
	public void onDisable(){
		this.packManager.saveConfig();
		this.saveConfig();
		
		this.packManager = null;
	}
	
	public ResourcepackManager getResourcepackManager() {
		return this.packManager;
	}
}
