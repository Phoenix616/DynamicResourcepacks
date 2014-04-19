package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin to set the server resourcepack for players dynamically.
 * 
 * @author René Zeidler
 * @version 0.1.1
 */
public class DynamicResourcepacks extends JavaPlugin {
	private ResourcepackManager packManager;
	private PlayerListener playerListener;
	private WorldGuardIntegration worldGuardIntegration;
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		
		this.packManager = new ResourcepackManager(this);
		this.playerListener = new PlayerListener(this);
		this.worldGuardIntegration = new WorldGuardIntegration(this);
		
		this.getServer().getPluginManager().registerEvents(this.playerListener, this);
		this.getCommand("dynamicresourcepacks").setExecutor(this.playerListener);
		this.getCommand("setresourcepack")     .setExecutor(this.playerListener);
		
		this.worldGuardIntegration.loadFromConfig();
		this.packManager.loadFromConfig();
	}
	
	@Override
	public void onDisable(){
		this.packManager.saveConfig();
		this.saveConfig();
		
		this.packManager = null;
	}
	
	public WorldGuardIntegration getWorldGuardIntegration() {
		return this.worldGuardIntegration;
	}
	
	public ResourcepackManager getResourcepackManager() {
		return this.packManager;
	}
}
