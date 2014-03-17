package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;

/**
 * Handles all actions that alter the resourcepack of players, including checks such as permissions or locked packs.
 * 
 * @author René Zeidler
 * @version 0.0.2
 */
public class PlayerManager implements CommandExecutor {
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
	
	public Resourcepack getResourcepackForInputString(CommandSender sender, String input) {
		input = input.toLowerCase();
		
		if(this.packManager.resourcepackExists(input)) return this.packManager.getResourcepackForName(input);
		
		if(ResourcepackManager.isValidURL(input)) {
			Resourcepack pack = this.packManager.getResoucepackForURL(input);
			if(pack != null) return pack;
			
			int i = 1;
			String name = "unnamed1";
			while(this.packManager.resourcepackExists(name)) {
				i++;
				name = "unnamed" + i;
			}
			
			sender.sendMessage(ChatColor.GOLD + "A new resourcepack \"" + ChatColor.YELLOW + pack + ChatColor.GOLD + "\" for the URL \"" + ChatColor.YELLOW + input + ChatColor.GOLD + "\" has been automatically created.");
			sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + "/dynamicresourepacks rename " + pack + " <newName>" + ChatColor.GOLD + " to rename it.");
			
			pack = new Resourcepack(name, input);
			this.packManager.addResourcepack(pack);
			return pack;
		} else {
			for(String name : this.packManager.getResourcepacks().keySet())
				if(name.startsWith(input)) return this.packManager.getResourcepackForName(name);
			
			sender.sendMessage(ChatColor.RED + "The resourcepack you entered (" + ChatColor.GOLD + input + ChatColor.RED + ") does not exist and is no valid URL");
			return null;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
}
