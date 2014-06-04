package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.plugin.java.JavaPlugin;

import de.rene_zeidler.dynamicresourcepacks.resourcepacks.PlayerManager;
import de.rene_zeidler.dynamicresourcepacks.resourcepacks.ResourcepackManager;

/**
 * Plugin to set the server resourcepack for players dynamically.
 * 
 * @author René Zeidler
 * @version 0.1.1
 */
public class DynamicResourcepacks extends JavaPlugin {
	private ResourcepackManager packManager;
	private PlayerListener playerListener;
	private PlayerManager playerManager;
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		
		this.packManager = new ResourcepackManager(this);
		this.playerManager = new PlayerManager(this);
		this.playerListener = new PlayerListener(this);
		
		this.getServer().getPluginManager().registerEvents(this.playerListener, this);
		this.getCommand("dynamicresourcepacks").setExecutor(this.playerListener);
		this.getCommand("setresourcepack")     .setExecutor(this.playerListener);
		
		this.packManager.loadFromConfig();
		this.playerManager.loadFromConfig();
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
	
	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}
}
