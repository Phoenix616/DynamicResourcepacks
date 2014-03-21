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
		if(input == null) return null;
		
		input = input.toLowerCase();
		
		if(this.packManager.resourcepackExists(input)) return this.packManager.getResourcepackForName(input);
		
		if(ResourcepackManager.isValidURL(input) && sender.hasPermission("dynamicresourcepacks.create")) {
			Resourcepack pack = this.packManager.getResoucepackForURL(input);
			if(pack != null) return pack;
			
			int i = 1;
			String name = "unnamed1";
			while(this.packManager.resourcepackExists(name)) {
				i++;
				name = "unnamed" + i;
			}
			
			sender.sendMessage(ChatColor.GOLD + "A new resourcepack \"" + ChatColor.YELLOW + pack + ChatColor.GOLD + "\" for the URL \"" + ChatColor.YELLOW + input + ChatColor.GOLD + "\" has been automatically created.");
			if(sender.hasPermission("dynamicresourcepacks.rename"))
				sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + "/dynamicresourepacks rename " + pack + " <newName>" + ChatColor.GOLD + " to rename it.");
			
			pack = new Resourcepack(name, input);
			this.packManager.addResourcepack(pack);
			return pack;
		} else {
			for(String name : this.packManager.getResourcepacks().keySet())
				if(name.startsWith(input)) return this.packManager.getResourcepackForName(name);
			
			sender.sendMessage(ChatColor.RED + "The resourcepack you entered (" + ChatColor.GOLD + input + ChatColor.RED + ") does not exist");
			return null;
		}
	}
	
	public boolean handleDynamicResourcepacksCommand(CommandSender sender, String label, String[] args) {
		//TODO
		
		return true;
	}
	
	public boolean handleSetResourcepackCommand(CommandSender sender, String label, String[] args) {
		if(args.length == 0)
			this.handleCurrentPackInfo(sender);
		else if(sender.hasPermission("dynamicresourcepacks.setpack")) {
			Player  player = null;
			String  pack   = null;
			boolean locked = false;
			
			if(args.length == 1) {
				pack = args[0];
			} else if(args.length == 2) {
				if("lock".equals(args[1]) || "true".equals(args[1])) {
					locked = true;
					pack = args[0];
				} else if("false".equals(args[1])) {
					pack = args[0];
				} else {
					player = sender.getServer().getPlayer(args[0]);
					if(player == null) {
						this.printSetpackUsage(sender, label);
						return true;
					}
					pack = args[1];
				}
			} else if(args.length == 3) {
				player = sender.getServer().getPlayer(args[0]);
				if(player == null) {
					this.printSetpackUsage(sender, label);
					return true;
				}
				pack = args[1];
				if("lock".equals(args[2]) || "true".equals(args[2]))
					locked = true;
			} else {
				this.printSetpackUsage(sender, label);
				return true;
			}
			
			if(player != null && !sender.hasPermission("dynamicresourcepacks.setpack.others")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to set the resourcepack of other players!");
				return true;
			} else {
				if(!(sender instanceof Player)) {
					this.printSetpackUsage(sender, label);
					return true;
				} else
					player = (Player)sender;
			}
			
			if(locked && !sender.hasPermission("dynamicresourcepacks.setpack.lock")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to lock the resourcepack of other players!");
				return true;
			}
			
			if(pack == null)
				this.printSetpackUsage(sender, label);
			else {
				Resourcepack resourcepack = this.getResourcepackForInputString(sender, pack);
				if(resourcepack != null)
					this.setResourcepack(sender, player, resourcepack);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
		}
		
		return true;
	}
	
	public void setResourcepack(Player player, Resourcepack pack) {
		this.setResourcepack(player, pack, false);
	}
	
	public void setResourcepack(Player player, Resourcepack pack, boolean lock) {
		this.setResourcepack(null, player, pack, false);
	}
	
	public void setResourcepack(CommandSender sender, Player player, Resourcepack pack) {
		this.setResourcepack(sender, player, pack, false);
	}
	
	public void setResourcepack(CommandSender sender, Player player, Resourcepack pack, boolean lock) {
		if(player == null || pack == null) return;
		
		boolean useSelf      = (sender == player);
		boolean senderExists = (sender != null);
		
		if(useSelf && !pack.checkUseSelfPermission(player)) {
			if(senderExists) sender.sendMessage(ChatColor.RED + "You don't have permission to use this resourcepack!");
			return;
		} else if(!pack.checkGeneralPermission(player)) {
			if(senderExists)
				sender.sendMessage(ChatColor.RED  + "The player " +
				                   ChatColor.GOLD + player.getName() +
				                   ChatColor.RED  + " doesn't have permission to use the resourcepack " +
				                   ChatColor.GOLD + pack.getDisplayName() +
				                   ChatColor.RED  + "!");
			return;
		}
		
		if(this.packManager.getLocked(player)) {
			if(senderExists) {
				if(sender.hasPermission("dynamicresourcepacks.unlock")) {
					if(!lock) {
						this.packManager.setLocked(player, false);
						if(useSelf) sender.sendMessage(ChatColor.GREEN + "Your resourcepack has been unlocked");
						else        sender.sendMessage(ChatColor.GREEN + "The resourcepack of " +
						                               ChatColor.DARK_GREEN + player.getName() +
						                               ChatColor.GREEN + " has been unlocked");
					}
				} else {
					if(useSelf) sender.sendMessage(ChatColor.RED + "Your resourcepack is locked!");
					else        sender.sendMessage(ChatColor.RED + "The resourcepack of " +
					                               ChatColor.GOLD + player.getName() +
					                               ChatColor.RED + " is locked!");
					return;
				}
			} else
				return;
		} else if(lock) {
			this.packManager.setLocked(player, false);
			if(useSelf) sender.sendMessage(ChatColor.GREEN + "Your resourcepack has been unlocked");
			else        sender.sendMessage(ChatColor.GREEN + "The resourcepack of " +
			                               ChatColor.DARK_GREEN + player.getName() +
			                               ChatColor.GREEN + " has been unlocked");
		}
		
		this.packManager.setResourcepack(player, pack);
		
		if(senderExists) {
			if(useSelf) sender.sendMessage(ChatColor.GREEN + "You now use the resourcepack " +
			                               ChatColor.DARK_GREEN + pack.getDisplayName());
			else        sender.sendMessage(ChatColor.GREEN + "The resourcepack of " +
			                               ChatColor.DARK_GREEN + player.getName() +
			                               ChatColor.GREEN + " has been set to " +
			                               ChatColor.DARK_GREEN + pack.getDisplayName());
		}
	}
	
	public void handleCurrentPackInfo(CommandSender sender) {
		this.handleCurrentPackInfo(sender, "dynamicresourcepacks");
	}
	
	public void handleCurrentPackInfo(CommandSender sender, String label) {
		if(label == null) label = "/dynamicresourcepacks";
		else if(!label.startsWith("/")) label = "/" + label;
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can't have a selected resourcepack!");
			sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + label + " view <pack>");
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
					sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + label + " view <pack>" + ChatColor.GOLD + " to show the infomarion of another pack");
				if(sender.hasPermission("dynamicresourcepacks.list.selectable"))
					sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + label + " list" + ChatColor.GOLD + " to list all resourcepacks");
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
	
	public void printSetpackUsage(CommandSender sender) {
		this.printSetpackUsage(sender, "setresourcepack");
	}
	
	public void printSetpackUsage(CommandSender sender, String label) {
		if(label == null) label = "/setresourcepack";
		else if(!label.startsWith("/")) label = "/" + label;
		
		boolean canSetOthers = sender.hasPermission("dynamicresourcepacks.setpack.others");
		boolean canLock      = sender.hasPermission("dynamicresourcepacks.setpack.lock");
		StringBuilder msg = new StringBuilder();
		msg.append(ChatColor.RED);
		
		if(!(sender instanceof Player)) {
			if(!canSetOthers)
				msg.append("You don't have permission!");
			else {
				msg.append("Usage: ");
				msg.append(label);
				msg.append(" <player> <pack>");
				if(canLock) msg.append(" [lock]");
			}
		} else {
			msg.append("Usage: ");
			msg.append(label);
			if(canSetOthers) msg.append(" [player]");
			msg.append(" <pack>");
			if(canLock) msg.append(" [lock]");
		}
		
		sender.sendMessage(msg.toString());
	}
}
