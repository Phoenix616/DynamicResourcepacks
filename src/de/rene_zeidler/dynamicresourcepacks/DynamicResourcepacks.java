package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicResourcepacks extends JavaPlugin {

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
