package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.plugin.java.JavaPlugin;

public class DynamicResourcepacks extends JavaPlugin {
	private ResourcepackManager packManager;
	private PlayerManager playerManager;
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		this.packManager = new ResourcepackManager(this);
		this.playerManager = new PlayerManager(this, this.packManager);
	}
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
	}
 
	@Override
	public void onDisable(){
		this.saveConfig();
	}
	
	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}
}
