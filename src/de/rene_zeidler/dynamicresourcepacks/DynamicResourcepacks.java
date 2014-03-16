package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicResourcepacks extends JavaPlugin {
	ResourcepackManager packManager;
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		this.packManager = new ResourcepackManager(this);
	}
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
	}
 
	@Override
	public void onDisable(){
		this.saveConfig();
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		return true;
    }
}
