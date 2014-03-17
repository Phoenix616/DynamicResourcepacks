package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

/**
 * Handles all actions that alter the resourcepack of players, including checks such as permissions or locked packs.
 * 
 * @author René Zeidler
 * @version 0.0.3
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
	
	public boolean handleDynamicResourcepacksCommand(CommandSender sender, String label, String[] args) {
		
		return true;
	}
	
	public boolean handleSetResourcepackCommand(CommandSender sender, String label, String[] args) {
		if(args.length == 0)
			this.handleCurrentPackInfo(sender);
		else {
			
		}
		
		return true;
	}
	
	public void handleCurrentPackInfo(CommandSender sender) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can't have a selected resourcepack!");
			sender.sendMessage(ChatColor.GOLD + "Use /dynamicresourcepacks view <pack>");
			return;
		}
		
		Player p = (Player)sender;
		if(sender.hasPermission("dynamicresourcepacks.view"))
			if(this.packManager.hasResourcepack(p)) {
				sender.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "Currently selected resourcepack:");
				this.printPackInfo(sender, this.packManager.getResourcepack(p));
			} else {
				sender.sendMessage(ChatColor.RED + "You currently don't have a resourcepack selected!");
				if(sender.hasPermission("dynamicresourcepacks.view.selectable"))
					sender.sendMessage(ChatColor.GOLD + "Use /dynamicresourcepacks view <pack> to show the infomarion of another pack");
				if(sender.hasPermission("dynamicresourcepacks.list.selectable"))
					sender.sendMessage(ChatColor.GOLD + "Use /dynamicresourcepacks list to list all resourcepacks");
			}
		else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		}
	}
	
	public void printPackInfo(CommandSender sender, Resourcepack pack) {
		sender.sendMessage(ChatColor.DARK_AQUA + "Resourcepack " + 
		                   ChatColor.AQUA      + pack.getDisplayName() +
		                   ChatColor.DARK_AQUA + " (id: " +
		                   ChatColor.AQUA      + pack.getName() +
		                   ChatColor.DARK_AQUA + ")");
		if(sender.hasPermission("dynamicresourcepacks.view.full")) {
			sender.sendMessage(ChatColor.DARK_AQUA + "URL: " + 
			                   ChatColor.AQUA      + pack.getURL());
			sender.sendMessage(ChatColor.DARK_AQUA + "General Permission: " + 
			                   ChatColor.AQUA      + pack.getGeneralPermission().toString());
			sender.sendMessage(ChatColor.DARK_AQUA + "Use Self Permission: " + 
			                   ChatColor.AQUA      + pack.getUseSelfPermission().toString());
		}
	}
}
