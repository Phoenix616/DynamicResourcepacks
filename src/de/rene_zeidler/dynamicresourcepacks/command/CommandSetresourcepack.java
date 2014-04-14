package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.Resourcepack;

public class CommandSetresourcepack extends DynamicResourcepacksCommand {

	public CommandSetresourcepack(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		if(this.args.length == 0)
			this.printCurrentPackInfo(sender);
		else if(this.args.length > 0 && ("help".equalsIgnoreCase(this.args[0]) || "?".equalsIgnoreCase(this.args[0]))) {
			String[] newArgs = new String[this.args.length - 1];
			for(int i = 0; i < newArgs.length; i++)
				newArgs[i] = this.args[i + 1];
			return new CommandHelp(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, newArgs).run(sender);
		} else if(sender.hasPermission("dynamicresourcepacks.setpack")) {
			this.parseArgs(sender, this.args);
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
		}
		
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender) {
		//TODO: New flag system
		
		if(args.length == 2 && this.packManager.resourcepackExists(args[0])
		|| args.length == 3 && this.packManager.resourcepackExists(args[1])) {
			return completeValues(args[args.length - 1], "true", "false");
		} else if(args.length == 1 || args.length == 2)
			return completeResourcepack(sender, args[args.length - 1]);
		else
			return new ArrayList<String>();
	}

	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.setpack");
	}
	
	/**
	 * Parses the arguments of the setresourcepack command
	 * TODO: New flag system
	 * 
	 * @param sender The CommandSender
	 * @param args The arguments
	 * @return True if the command successfully set a resourcepack
	 */
	public boolean parseArgs(CommandSender sender, String[] args) {
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
					this.printUsage(sender);
					return false;
				}
				pack = args[1];
			}
		} else if(args.length == 3) {
			player = sender.getServer().getPlayer(args[0]);
			if(player == null) {
				this.printUsage(sender);
				return false;
			}
			pack = args[1];
			if("lock".equals(args[2]) || "true".equals(args[2]))
				locked = true;
		} else {
			this.printUsage(sender);
			return false;
		}
		
		if(player != null && !sender.hasPermission("dynamicresourcepacks.setpack.others")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to set the resourcepack of other players!");
			return false;
		} else {
			if(!(sender instanceof Player)) {
				this.printUsage(sender);
				return false;
			} else
				player = (Player)sender;
		}
		
		if(locked && !sender.hasPermission("dynamicresourcepacks.setpack.lock")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to lock the resourcepack of other players!");
			return false;
		}
		
		if(pack == null)
			this.printUsage(sender);
		else {
			Resourcepack resourcepack = this.getResourcepackForInputString(sender, pack);
			if(resourcepack != null)
				this.setResourcepack(sender, player, resourcepack, locked);
		}
		
		return true;
	}
	
	public void printUsage(CommandSender sender) {
		boolean canSetOthers = sender.hasPermission("dynamicresourcepacks.setpack.others");
		boolean canLock      = sender.hasPermission("dynamicresourcepacks.setpack.lock");
		StringBuilder msg = new StringBuilder();
		msg.append(ChatColor.RED);
		
		if(!(sender instanceof Player)) {
			if(!canSetOthers)
				msg.append("You don't have permission!");
			else {
				msg.append("Usage: /");
				msg.append(this.label);
				msg.append(" <player> <pack>");
				if(canLock) msg.append(" [lock]");
			}
		} else {
			msg.append("Usage: /");
			msg.append(this.label);
			if(canSetOthers) msg.append(" [player]");
			msg.append(" <pack>");
			if(canLock) msg.append(" [lock]");
		}
		
		sender.sendMessage(msg.toString());
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
			this.packManager.setLocked(player, true);
			if(useSelf) sender.sendMessage(ChatColor.GREEN      + "Your resourcepack has been locked");
			else        sender.sendMessage(ChatColor.GREEN      + "The resourcepack of " +
			                               ChatColor.DARK_GREEN + player.getName() +
			                               ChatColor.GREEN      + " has been locked");
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
}
