package de.rene_zeidler.dynamicresourcepacks;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.Resourcepack.Permission;

/**
 * Handles all actions that alter the resourcepack of players, including checks such as permissions or locked packs.
 * 
 * @author René Zeidler
 * @version 0.0.3
 */
public class PlayerManager {
	private DynamicResourcepacks plugin;
	private ResourcepackManager packManager;
	
	private static PlayerManager instance;
	
	public PlayerManager(DynamicResourcepacks plugin, ResourcepackManager packManager) {
		PlayerManager.instance = this;
		
		this.plugin = plugin;
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
			
			pack = new Resourcepack(name, input, sender.getName());
			this.packManager.addResourcepack(pack);
			this.packManager.saveConfigPacks();
			this.plugin.saveConfig();
			
			return pack;
		} else {
			for(Resourcepack pack : this.packManager.getResourcepacks())
				if(pack.getName().startsWith(input)) return pack;
			
			sender.sendMessage(ChatColor.RED  + "The resourcepack you entered (" +
			                   ChatColor.GOLD + input +
			                   ChatColor.RED  + ") does not exist");
			return null;
		}
	}
	
	public boolean handleDynamicResourcepacksCommand(CommandSender sender, String label, String[] args) {
		if(args.length == 0) {
			this.printHelp(sender, label, null, args);
		} else {
			String command = args[0];
			String[] newArgs = new String[args.length - 1];
			for(int i = 0; i < newArgs.length; i++)
				newArgs[i] = args[i + 1];
			
			if("help".equalsIgnoreCase(command) ||
			   "?"   .equalsIgnoreCase(command)) {
				this.printHelp(sender, label, null, newArgs);
				
			} else if("view".equalsIgnoreCase(command) ||
					  "show".equalsIgnoreCase(command)) {
				this.handleViewCommand(sender, label, newArgs);
				
			} else if("list".equalsIgnoreCase(command)) {
				this.handleListCommand(sender, label, newArgs);
				
			} else if("create".equalsIgnoreCase(command) ||
					  "add"   .equalsIgnoreCase(command)) {
				this.handleCreateCommand(sender, label, newArgs);
				
			} else if("edit".equalsIgnoreCase(command) ||
					  "set" .equalsIgnoreCase(command)) {
				this.handleEditCommand(sender, label, newArgs);
			
			} else if(command.startsWith("set" ) ||
					  command.startsWith("edit")) {
				String property = command.substring(command.startsWith("set") ? 3 : 4);
				if(args.length > 1) {
					args[0] = args[1];
					args[1] = property;
				}
				this.handleEditCommand(sender, label, args);
				
			} else if("rename".equalsIgnoreCase(command)) {
				this.handleRenameCommand(sender, label, newArgs);
				
			} else if("remove".equalsIgnoreCase(command) ||
					  "delete".equalsIgnoreCase(command)) {
				//TODO
				
			} else if("switch".equalsIgnoreCase(command) ||
					  "use"   .equalsIgnoreCase(command)) {
				//TODO
				
			} else if("lock".equalsIgnoreCase(command)) {
				//TODO
			
			} else if("unlock".equalsIgnoreCase(command)) {
				//TODO
				
			} else if("version".equalsIgnoreCase(command) ||
					  "ver"    .equalsIgnoreCase(command)) {
				//TODO
				
			} else {
				this.printHelp(sender, label, null, args);
				
			}
		}
		
		return true;
	}
	
	public void handleViewCommand(CommandSender sender, String label, String[] args) {
		if(args.length == 0) {
			this.handleCurrentPackInfo(sender, label);
			return;
		}
		
		Resourcepack pack = this.getResourcepackForInputString(sender, args[0]);
		if(pack != null)
			this.printPackInfo(sender, pack);
	}
	
	public void handleListCommand(CommandSender sender, String label, String[] args) {
		if(args.length == 0 || !sender.hasPermission("dynamicresourcepacks.list.others"))
			this.printResourcepackList(sender, sender);
		else {
			Player player = sender.getServer().getPlayer(args[0]);
			if(player != null)
				this.printResourcepackList(sender, player);
			else
				sender.sendMessage(ChatColor.RED + "There is no online player named " + args[0]);
		}
	}
	
	public void handleCreateCommand(CommandSender sender, String label, String[] args) {
		if(!sender.hasPermission("dynamicresourcepacks.create")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return;
		}
		
		if(args.length >= 2) {
			if(!ResourcepackManager.isValidURL(args[1])) {
				sender.sendMessage(ChatColor.RED + "The URL you entered is not a valid URL!");
				return;
			}
			
			Resourcepack pack = new Resourcepack(args[0], args[1], sender.getName());
			if(args.length >= 3)
				pack.setDisplayName(args[2]);
			if (args.length >= 4) {
				Permission perm = Permission.valueOf(args[3].toUpperCase());
				if(perm == null) {
					sender.sendMessage(ChatColor.RED + "The general permission must be either NONE, GENERAL or SPECIFIC!");
					return;
				}
				pack.setGeneralPermission(perm);
			}
			if (args.length >= 5) {
				Permission perm = Permission.valueOf(args[4].toUpperCase());
				if(perm == null) {
					sender.sendMessage(ChatColor.RED + "The use self permission must be either NONE, GENERAL or SPECIFIC!");
					return;
				}
				pack.setUseSelfPermission(perm);
			}
			
			this.packManager.addResourcepack(pack);
			
			sender.sendMessage(ChatColor.GREEN + "Successfully added resourcepack");
			this.printPackInfo(sender, pack);

		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " create <name> <url> [displayName] [generalPermission] [useSelfPermission]");
		}
	}
	
	public void handleEditCommand(CommandSender sender, String label, String[] args) {
		boolean canRename = sender.hasPermission("dynamicresourcepacks.rename");
		boolean canEdit   = sender.hasPermission("dynamicresourcepacks.edit");
		
		if(args.length != 3) {
			if(canEdit)
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + " edit <name> <property> <value>");
			else if(canRename)
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + " rename <oldName> <newName>");
			else
				sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return;
		}
		
		if("name".equalsIgnoreCase(args[1])) {
			this.handleRenameCommand(sender, label, new String[] {args[0], args[2]});
		} else if(canEdit) {
			Resourcepack pack = this.getResourcepackForInputString(sender, args[0]);
			if(pack == null)
				return;
			
			if("url".equalsIgnoreCase(args[1])) {
				if(ResourcepackManager.isValidURL(args[2])) {
					pack.setURL(args[2]);
					sender.sendMessage(ChatColor.GREEN      + "Successfully set the URL of " +
					                   ChatColor.DARK_GREEN + pack.getDisplayName() +
					                   ChatColor.GREEN      + " to " +
					                   ChatColor.DARK_GREEN + pack.getURL());
				} else {
					sender.sendMessage(ChatColor.RED + "The URL you entered is not a valid URL!");
					return;
				}
				
			} else if("displayName".equalsIgnoreCase(args[1])) {
				pack.setDisplayName(args[2]);
				sender.sendMessage(ChatColor.GREEN      + "Successfully set the display name of " +
				                   ChatColor.DARK_GREEN + pack.getName() +
				                   ChatColor.GREEN      + " to " +
				                   ChatColor.DARK_GREEN + pack.getDisplayName());
				
			} else if("generalPermission".equalsIgnoreCase(args[1]) ||
					  "permission"       .equalsIgnoreCase(args[1]) ||
					  "useSelfPermission".equalsIgnoreCase(args[1]) ||
					  "selfPermission"   .equalsIgnoreCase(args[1])) {
				Permission perm = this.getResourcepackPermission(args[2]);
				if(perm == null) {
					sender.sendMessage(ChatColor.RED + "The permission must be either NONE, GENERAL or SPECIFIC!");
					return;
				}
				
				if("generalPermission".equalsIgnoreCase(args[1]) ||
				   "permission"       .equalsIgnoreCase(args[1])) {
					pack.setGeneralPermission(perm);
					sender.sendMessage(ChatColor.GREEN      + "Successfully set the general permission of " +
			                           ChatColor.DARK_GREEN + pack.getDisplayName() +
			                           ChatColor.GREEN      + " to " +
			                           ChatColor.DARK_GREEN + pack.getGeneralPermission().toString());
				} else {
					pack.setUseSelfPermission(perm);
					sender.sendMessage(ChatColor.GREEN      + "Successfully set the use self permission of " +
	                                   ChatColor.DARK_GREEN + pack.getDisplayName() +
	                                   ChatColor.GREEN      + " to " +
	                                   ChatColor.DARK_GREEN + pack.getUseSelfPermission().toString());
				}
			
			} else {
				sender.sendMessage(ChatColor.RED + "Unknown property \"" + args[1] + "\"!");
				return;
			}
			
			this.packManager.saveConfigPacks();
			this.plugin.saveConfig();
			
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
		}
	}
	
	public void handleRenameCommand(CommandSender sender, String label, String[] args) {
		if(!sender.hasPermission("dynamicresourcepacks.edit")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return;
		}
		
		if(args.length == 2) {
			Resourcepack pack = this.getResourcepackForInputString(sender, args[0]);
			if(pack != null) {
				this.packManager.renameResourcepack(pack, args[1]);
				sender.sendMessage(ChatColor.GREEN      + "Successfully set the id (name) of " +
                                   ChatColor.DARK_GREEN + pack.getDisplayName() +
                                   ChatColor.GREEN      + " to " +
                                   ChatColor.DARK_GREEN + pack.getName());
				this.packManager.saveConfig();
				this.plugin.saveConfig();
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " rename <oldName> <newName>");
		}
	}
	
	public void printResourcepackList(CommandSender sender, Permissible player) {
		List<Resourcepack> visiblePacks;
		
		if(player.hasPermission("dynamicresourcepacks.list.all"))
			visiblePacks = this.packManager.getResourcepacks();
		else if(player.hasPermission("dynamicresourcepacks.list.usable"))
			visiblePacks = this.packManager.getUsableResourcepacks(player);
		else if(player.hasPermission("dynamicresourcepacks.list.selectable"))
			visiblePacks = this.packManager.getUsableResourcepacks(player);
		else {
			if(sender == player)
				sender.sendMessage(ChatColor.RED + "You don't have permission!");
			else
				sender.sendMessage(ChatColor.RED + "The player doesn't have permission to view the list of resourcepacks!");
			return;
		}
		
		if(visiblePacks.size() == 0) {
			sender.sendMessage(ChatColor.RED + "There are currently no available resourcepacks!");
		} else {
			sender.sendMessage(ChatColor.GOLD + "Available resourcepacks (" + visiblePacks.size() + "):");
			for(Resourcepack pack : visiblePacks)
				sender.sendMessage(ChatColor.BLUE + pack.getDisplayName()
						+ ChatColor.DARK_AQUA + " (" + pack.getName() + ")");
		}
	}
	
	public void printHelp(CommandSender sender, String mainLabel, String setpackLabel, String[] args) {
		if(mainLabel == null) mainLabel = "/dynamicresourcepacks";
		else if(!mainLabel.startsWith("/")) mainLabel = "/" + mainLabel;
		if(setpackLabel == null) setpackLabel = "/setresourcepack";
		else if(!setpackLabel.startsWith("/")) setpackLabel = "/" + setpackLabel;
		
		StringBuilder msg = new StringBuilder();
		
		boolean foundTopic = false;
		if(args.length > 0) {
			if("aliases".equalsIgnoreCase(args[0])) {
				foundTopic = true;
				msg.append(ChatColor.GOLD);
				msg.append(ChatColor.ITALIC);
				msg.append("Showing aliases for DynamicResourcepacks commands:\n");
				
				msg.append(ChatColor.BLUE);
				msg.append(mainLabel);
				for(String alias : this.plugin.getCommand("dynamicresourcepacks").getAliases())
					this.appendAlias(msg, alias);
				msg.append("\n");
				
				
				this.appendAliases(msg, mainLabel, "help", "?");
				if(sender.hasPermission("dynamicresourcepacks.view.selected"))
					this.appendAliases(msg, mainLabel, "view",   "show"  );
				if(sender.hasPermission("dynamicresourcepacks.created"))
					this.appendAliases(msg, mainLabel, "create", "add"   );
				if(sender.hasPermission("dynamicresourcepacks.remove"))
					this.appendAliases(msg, mainLabel, "remove", "delete");
				this.appendAliases(msg, mainLabel, "version", "ver");
				
				msg.append(ChatColor.BLUE);
				msg.append(setpackLabel);
				for(String alias : this.plugin.getCommand("setresourcepack").getAliases())
					this.appendAlias(msg, alias);
				msg.append("\n");
				
				this.appendAliases(msg, setpackLabel, "...", mainLabel + " use ...", mainLabel + " switch ...");
			}
		}
		
		if(!foundTopic) {
			msg.append(ChatColor.GOLD);
			msg.append(ChatColor.ITALIC);
			msg.append("Showing help for DynamicResourcepacks:\n");
			
			this.appendHelpEntry(msg, mainLabel, "help",           "Shows a list of available commands");
			this.appendHelpEntry(msg, mainLabel, "help aliases",   "Shows a list of available command aliases");
			
			this.appendHelpEntry(msg, mainLabel, "list",          "Lists all available resourcepacks",                      sender, "dynamicresourcepacks.list.selectable");
			this.appendHelpEntry(msg, mainLabel, "list <player>", "Lists all for the given player available resourcepacks", sender, "dynamicresourcepacks.list.others");
			this.appendHelpEntry(msg, mainLabel, "view",          "Shows which resourcepack you have currently selected",   sender, "dynamicresourcepacks.view.selected"  );
			this.appendHelpEntry(msg, mainLabel, "view <pack>",   "Shows information on the given resourcepack",            sender, "dynamicresourcepacks.view.selectable");
			
			this.appendHelpEntry(msg, mainLabel, "create <name> <url> [displayName] [generalPermission] [useSelfPermission]",
					                                                              null, sender, "dynamicresourcepacks.create"      );
			this.appendHelpEntry(msg, mainLabel, "rename <oldName> <newName>",    null, sender, "dynamicresourcepacks.rename"      );
			this.appendHelpEntry(msg, mainLabel, "edit <name> <setting> <value>", null, sender, "dynamicresourcepacks.edit"        );
			this.appendHelpEntry(msg, mainLabel, "remove <name>",                 null, sender, "dynamicresourcepacks.remove"      );
			this.appendHelpEntry(msg, mainLabel, "lock <player>",                 null, sender, "dynamicresourcepacks.setpack.lock");
			this.appendHelpEntry(msg, mainLabel, "unlock <player>",               null, sender, "dynamicresourcepacks.unlock"      );
			
			this.appendHelpEntry(msg, mainLabel, "version", "Show the version of this plugin");
			
			boolean canLock = sender.hasPermission("dynamicresourcepacks.setpack.lock");
			this.appendHelpEntry(msg, setpackLabel, "<pack>" + (canLock ? " [lock]" : ""), "Use the given resourcepack");
			this.appendHelpEntry(msg, setpackLabel, "<player> <pack>" + (canLock ? " [lock]" : ""), "Sets the resourcepack of a given player", sender, "dynamicresourcepacks.setpack.others");
		}
	
		sender.sendMessage(msg.toString());
	}
	
	public void appendAliases(StringBuilder stringBuilder, String mainCommand, String mainAlias, String... aliases) {
		stringBuilder.append(ChatColor.BLUE);
		stringBuilder.append(mainCommand);
		stringBuilder.append(' ');
		stringBuilder.append(mainAlias);
		for(String alias : aliases)
			this.appendAlias(stringBuilder, alias);
		stringBuilder.append("\n");
	}
	
	public void appendAlias(StringBuilder stringBuilder, String alias) {
		stringBuilder.append(ChatColor.BLUE);
		stringBuilder.append(" / ");
		stringBuilder.append(ChatColor.DARK_AQUA);
		stringBuilder.append(alias);
	}
	
	public void appendHelpEntry(StringBuilder stringBuilder, String mainCommand, String commandArgs, String description) {
		this.appendHelpEntry(stringBuilder, mainCommand, commandArgs, description, null, null);
	}
	
	public void appendHelpEntry(StringBuilder stringBuilder, String mainCommand, String commandArgs, String description, CommandSender sender, String permission) {
		if(permission == null || permission.isEmpty() || (sender != null && sender.hasPermission(permission))) {
			stringBuilder.append(ChatColor.BLUE);
			stringBuilder.append(mainCommand);
			if(commandArgs != null) {
				stringBuilder.append(' ');
				stringBuilder.append(commandArgs);
			}
			if(description != null) {
				stringBuilder.append(ChatColor.DARK_AQUA);
				stringBuilder.append(" - ");
				stringBuilder.append(description);
			}
			stringBuilder.append("\n");
		}
	}
	
	public boolean handleSetResourcepackCommand(CommandSender sender, String label, String[] args) {
		if(args.length == 0)
			this.handleCurrentPackInfo(sender);
		else if(args.length > 0 && ("help".equalsIgnoreCase(args[0]) || "?".equalsIgnoreCase(args[0]))) {
			String[] newArgs = new String[args.length - 1];
			for(int i = 0; i < newArgs.length; i++)
				newArgs[i] = args[i + 1];
			this.printHelp(sender, null, label, newArgs);
		} else if(sender.hasPermission("dynamicresourcepacks.setpack")) {
			this.parseSetResourcepack(sender, label, args);
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
		}
		
		return true;
	}
	
	/**
	 * Parses the arguments of the setresourcepack command
	 * 
	 * @param sender The CommandSender
	 * @param label The label used to execute this command
	 * @param args The arguments
	 * @return True if the command successfully set a resourcepack
	 */
	public boolean parseSetResourcepack(CommandSender sender, String label, String[] args) {
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
					return false;
				}
				pack = args[1];
			}
		} else if(args.length == 3) {
			player = sender.getServer().getPlayer(args[0]);
			if(player == null) {
				this.printSetpackUsage(sender, label);
				return false;
			}
			pack = args[1];
			if("lock".equals(args[2]) || "true".equals(args[2]))
				locked = true;
		} else {
			this.printSetpackUsage(sender, label);
			return false;
		}
		
		if(player != null && !sender.hasPermission("dynamicresourcepacks.setpack.others")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to set the resourcepack of other players!");
			return false;
		} else {
			if(!(sender instanceof Player)) {
				this.printSetpackUsage(sender, label);
				return false;
			} else
				player = (Player)sender;
		}
		
		if(locked && !sender.hasPermission("dynamicresourcepacks.setpack.lock")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to lock the resourcepack of other players!");
			return false;
		}
		
		if(pack == null)
			this.printSetpackUsage(sender, label);
		else {
			Resourcepack resourcepack = this.getResourcepackForInputString(sender, pack);
			if(resourcepack != null)
				this.setResourcepack(sender, player, resourcepack);
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
						if(useSelf) sender.sendMessage(ChatColor.GREEN      + "Your resourcepack has been unlocked");
						else        sender.sendMessage(ChatColor.GREEN      + "The resourcepack of " +
						                               ChatColor.DARK_GREEN + player.getName() +
						                               ChatColor.GREEN      + " has been unlocked");
					}
				} else {
					if(useSelf) sender.sendMessage(ChatColor.RED  + "Your resourcepack is locked!");
					else        sender.sendMessage(ChatColor.RED  + "The resourcepack of " +
					                               ChatColor.GOLD + player.getName() +
					                               ChatColor.RED  + " is locked!");
					return;
				}
			} else
				return;
		} else if(lock) {
			this.packManager.setLocked(player, false);
			if(useSelf) sender.sendMessage(ChatColor.GREEN      + "Your resourcepack has been unlocked");
			else        sender.sendMessage(ChatColor.GREEN      + "The resourcepack of " +
			                               ChatColor.DARK_GREEN + player.getName() +
			                               ChatColor.GREEN      + " has been unlocked");
		}
		
		this.packManager.setResourcepack(player, pack);
		
		if(senderExists) {
			if(useSelf) sender.sendMessage(ChatColor.GREEN      + "You now use the resourcepack " +
			                               ChatColor.DARK_GREEN + pack.getDisplayName());
			else        sender.sendMessage(ChatColor.GREEN      + "The resourcepack of " +
			                               ChatColor.DARK_GREEN + player.getName() +
			                               ChatColor.GREEN      + " has been set to " +
			                               ChatColor.DARK_GREEN + pack.getDisplayName());
		}
		
		this.packManager.saveConfigForPlayer(player);
		this.plugin.saveConfig();
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
			sender.sendMessage(ChatColor.DARK_AQUA + "Added By: " + 
	                           ChatColor.AQUA      + pack.getAddedBy());
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
	
	public Permission getResourcepackPermission(String s) {
		try {
			return Permission.valueOf(s.toUpperCase());
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
}
