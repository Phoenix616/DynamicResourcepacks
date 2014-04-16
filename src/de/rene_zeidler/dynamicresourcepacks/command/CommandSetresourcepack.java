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
		/* TODO: selectors?
		 * pack:xyz
		 * radius:10
		 * 
		 * TODO: clear?
		 */
		
		if(this.args.length == 0)
			this.printCurrentPackInfo(sender);
		else if(this.args.length > 0 && ("help".equalsIgnoreCase(this.args[0]) || "?".equalsIgnoreCase(this.args[0]))) {
			String[] newArgs = new String[this.args.length - 1];
			for(int i = 0; i < newArgs.length; i++)
				newArgs[i] = this.args[i + 1];
			return new CommandHelp(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, newArgs).run(sender);
		} else if(sender.hasPermission("dynamicresourcepacks.setpack")) {
			this.parseArgs(sender);
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
		}
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<String> tabComplete(CommandSender sender) {
		if(this.args.length == 1) {
			List<String> completions = this.completeResourcepack(sender, this.args[0]);
			if(completions.size() == 0)
				return null; //complete players
			else
				return completions;
		} else if(this.args.length == 2 && sender.getServer().getPlayer(this.args[0]) != null) {
			return this.completeResourcepack(sender, this.args[1]);
		} else if(this.args.length > 1) {
			String argBefore = this.args[this.args.length - 2];
			String arg = this.args[this.args.length - 1];
			
			List<String> completions = new ArrayList<String>();
			
			if("-respectlock".equalsIgnoreCase(argBefore)) this.addCompletions(completions, arg, "true", "false");
			
			if(sender.hasPermission("dynamicresourcepacks.setpack.lock")) this.addCompletions(completions, arg, "-lock");
			if(sender.hasPermission("dynamicresourcepacks.unlock")) this.addCompletions(completions, arg, "-respectlock");
			
			return completions;
		}
		
		return new ArrayList<String>();
	}

	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.setpack");
	}
	
	/**
	 * Parses the arguments of the setresourcepack command
	 * 
	 * @param sender The CommandSender
	 * @param args The arguments
	 * @return True if the command successfully set a resourcepack
	 */
	@SuppressWarnings("deprecation")
	public boolean parseArgs(CommandSender sender) {
		if(this.args.length < 1) {
			this.printUsage(sender);
			return true;
		}
		
		Player player;
		Resourcepack pack;
		int packArg;
		
		if(this.args.length == 1 || this.args[1].startsWith("-")) {
			//no player
			if(!(sender instanceof Player)) {
				this.printUsage(sender);
				return true;
			}
			player = (Player)sender;
			packArg = 0;
		} else {
			//other player
			player = sender.getServer().getPlayer(this.args[0]);
			if(player == null) {
				if(this.args.length == 2 || this.args[2].startsWith("-")) {
					sender.sendMessage(ChatColor.RED + "There is no online player named " + this.args[0]);
					return true;
				} else {
					//just assume the player wanted to use the main dynamicresourcepacks command
					return new CommandDynamicResourcepacks(this.plugin,
							this.dynamicResourcepacksAlias,
							this.dynamicResourcepacksAlias,
							this.setresourcepackAlias,
							this.args).run(sender);
				}
			}
			packArg = 1;
		}
		pack = this.getResourcepackForInputString(sender, this.args[packArg]);
		if(pack == null) return true;
		
		boolean lock = false;
		boolean respectLock = !(sender instanceof Player);
		
		for(int i = packArg + 1; i < this.args.length; i++) {
			String flag = this.args[i];
			String value = (this.args.length > i + 1) ? this.args[i + 1] : null;
			if(value != null && value.startsWith("-"))
				value = null;
			else
				i++;
			
			if("-lock".equalsIgnoreCase(flag)) {
				lock = true;
			} else if("-respectlock".equalsIgnoreCase(flag)) {
				if(value == null && respectLock) {
					sender.sendMessage(ChatColor.RED + "The flag -respectlock needs to be either true or false");
					return true;
				} else if("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value)) {
					respectLock = false;
				} else if("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || value == null) {
					respectLock = true;
				} else {
					sender.sendMessage(ChatColor.RED + "The flag -respectlock needs to be either true or false");
					return true;
				}
			}
		}
		
		this.setResourcepack(sender, player, pack, lock, respectLock);
		
		return true;
	}
	
	public void printUsage(CommandSender sender) {
		boolean canSetOthers = sender.hasPermission("dynamicresourcepacks.setpack.others");
		StringBuilder msg = new StringBuilder();
		msg.append(ChatColor.RED);
		
		if(!(sender instanceof Player)) {
			if(!canSetOthers)
				msg.append("You don't have permission!");
			else {
				msg.append("Usage: /");
				msg.append(this.label);
				msg.append(" <player> <pack> [flags]");
			}
		} else {
			msg.append("Usage: /");
			msg.append(this.label);
			if(canSetOthers) msg.append(" [player]");
			msg.append(" <pack> [flags]");
		}
		msg.append("\n");
		msg.append(ChatColor.BLUE);
		msg.append("Available Flags:\n");
		
		int before = msg.length();
		if(sender.hasPermission("dynamicresourcepacks.setpack.lock")) {
			msg.append(ChatColor.DARK_AQUA);
			msg.append("-lock");
			msg.append(ChatColor.AQUA);
			msg.append("Lock the resourcepack\n");
		}
		if(sender.hasPermission("dynamicresourcepacks.unlock")) {
			msg.append(ChatColor.DARK_AQUA);
			msg.append("-respectlock [true|false]");
			msg.append(ChatColor.AQUA);
			msg.append("Don't run the command when the resourcepack is locked (value if the flag is omitted: false for players, true for console and commandblocks)\n");
		}
		
		if(msg.length() == before) {
			msg.append(ChatColor.DARK_AQUA);
			msg.append(ChatColor.ITALIC);
			msg.append("none\n");
		}
		
		sender.sendMessage(msg.toString());
	}
	
	public void setResourcepack(CommandSender sender, Player player, Resourcepack pack, boolean lock, boolean respectLock) {
		if(player == null || pack == null) return;

		boolean useSelf = (sender == player);

		if(!useSelf && !sender.hasPermission("dynamicresourcepacks.setpack.others")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to set the resourcepack of other players!");
			return;
		}
		
		if(lock && !sender.hasPermission("dynamicresourcepacks.setpack.lock")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to lock a resourcepack!");
			return;
		}
		
		if(useSelf && !pack.checkUseSelfPermission(player)) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use this resourcepack!");
			return;
		} else if(!pack.checkGeneralPermission(player)) {
			sender.sendMessage(ChatColor.RED  + "The player " +
							   ChatColor.GOLD + player.getName() +
							   ChatColor.RED  + " doesn't have permission to use the resourcepack " +
							   ChatColor.GOLD + pack.getDisplayName() +
							   ChatColor.RED  + "!");
			return;
		}
		
		if(this.packManager.getLocked(player)) {
			if(!respectLock && sender.hasPermission("dynamicresourcepacks.unlock")) {
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
		} else if(lock) {
			this.packManager.setLocked(player, true);
			if(useSelf) sender.sendMessage(ChatColor.GREEN      + "Your resourcepack has been locked");
			else        sender.sendMessage(ChatColor.GREEN      + "The resourcepack of " +
										   ChatColor.DARK_GREEN + player.getName() +
										   ChatColor.GREEN      + " has been locked");
		}

		this.packManager.setResourcepack(player, pack);

		if(useSelf) sender.sendMessage(ChatColor.GREEN      + "You now use the resourcepack " +
									   ChatColor.DARK_GREEN + pack.getDisplayName());
		else        sender.sendMessage(ChatColor.GREEN      + "The resourcepack of " +
									   ChatColor.DARK_GREEN + player.getName() +
									   ChatColor.GREEN      + " has been set to " +
									   ChatColor.DARK_GREEN + pack.getDisplayName());

		this.packManager.saveConfigForPlayer(player);
		this.plugin.saveConfig();
	}
}
