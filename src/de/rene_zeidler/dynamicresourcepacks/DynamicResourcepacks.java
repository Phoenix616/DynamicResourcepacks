package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin to set the server resourcepack for players dynamically.
 * 
 * @author René Zeidler
 * @version 0.1.0
 */
public class DynamicResourcepacks extends JavaPlugin {
	private ResourcepackManager packManager;
	private PlayerManager playerManager;
	private PlayerListener playerListener;
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		
		this.packManager = new ResourcepackManager(this);
		this.playerManager = new PlayerManager(this, this.packManager);
		this.playerListener = new PlayerListener(this);
		
		this.getServer().getPluginManager().registerEvents(this.playerListener, this);
		this.getCommand("dynamicresourcepacks").setExecutor    (this.playerListener);
		this.getCommand("dynamicresourcepacks").setTabCompleter(this.playerListener);
		this.getCommand("setresourcepack")     .setExecutor    (this.playerListener);
		this.getCommand("setresourcepack")     .setTabCompleter(this.playerListener);
		
		this.packManager.loadFromConfig();
	}
 
	@Override
	public void onDisable(){
		this.packManager.saveConfig();
		this.saveConfig();
		
		this.playerManager = null;
		this.packManager = null;
	}
	
	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}
}
