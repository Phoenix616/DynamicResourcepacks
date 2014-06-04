package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;
import de.rene_zeidler.dynamicresourcepacks.resourcepacks.Resourcepack;
import de.rene_zeidler.dynamicresourcepacks.resourcepacks.ResourcepackManager;
import de.rene_zeidler.dynamicresourcepacks.resourcepacks.Resourcepack.Permission;

public abstract class DynamicResourcepacksCommand {
	protected String label;
	protected String dynamicResourcepacksAlias;
	protected String setresourcepackAlias;
	protected String[] args;
	
	protected DynamicResourcepacks plugin;
	protected ResourcepackManager packManager;
	
	public DynamicResourcepacksCommand(DynamicResourcepacks plugin,
			String label, String dynamicResourcepacksAlias,
			String setresourcepackAlias, String[] args) {
		this.label = label;
		this.dynamicResourcepacksAlias = (dynamicResourcepacksAlias == null ? "dynamicresourcepacks" : dynamicResourcepacksAlias);
		this.setresourcepackAlias = (setresourcepackAlias == null ? "setresourcepack" : setresourcepackAlias);
		this.args = args;
		this.plugin = plugin;
		this.packManager = this.plugin.getResourcepackManager();
	}

	public abstract boolean run(CommandSender sender);
	
	public abstract List<String> tabComplete(CommandSender sender);
	
	
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
				sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + "/" + this.dynamicResourcepacksAlias + " rename " + pack + " <newName>" + ChatColor.GOLD + " to rename it.");
			
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
	
	public void printCurrentPackInfo(CommandSender sender) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can't have a selected resourcepack!");
			sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + "/" + this.dynamicResourcepacksAlias + " view <pack>");
			return;
		}
		
		Player p = (Player)sender;
		if(sender.hasPermission("dynamicresourcepacks.view.selected"))
			if(this.packManager.hasResourcepack(p)) {
				sender.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Currently selected resourcepack:");
				this.printPackInfo(sender, this.packManager.getResourcepack(p));
			} else {
				sender.sendMessage(ChatColor.RED + "You currently don't have a resourcepack selected!");
				if(sender.hasPermission("dynamicresourcepacks.view.selectable"))
					sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + "/" + this.dynamicResourcepacksAlias + " view <pack>" + ChatColor.GOLD + " to show the infomarion of another pack");
				if(sender.hasPermission("dynamicresourcepacks.list.selectable"))
					sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + "/" + this.dynamicResourcepacksAlias + " list" + ChatColor.GOLD + " to list all resourcepacks");
			}
		else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		}
	}
	
	public void printPackInfo(CommandSender sender, Resourcepack pack) {
		sender.sendMessage(ChatColor.BLUE      + "Resourcepack " + 
		                   ChatColor.DARK_AQUA + pack.getDisplayName() +
		                   ChatColor.BLUE      + " (id: " +
		                   ChatColor.DARK_AQUA + pack.getName() +
		                   ChatColor.BLUE      + ")");
		if(sender.hasPermission("dynamicresourcepacks.view.full")) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Added By: " + 
	                           ChatColor.AQUA      + pack.getAddedBy());
			sender.sendMessage(ChatColor.DARK_AQUA + "URL: " + 
			                   ChatColor.AQUA      + pack.getURL());
			sender.sendMessage(ChatColor.DARK_AQUA + "General Permission: " + 
			                   ChatColor.AQUA      + pack.getGeneralPermission().toString());
			sender.sendMessage(ChatColor.DARK_AQUA + "Use Self Permission: " + 
			                   ChatColor.AQUA      + pack.getUseSelfPermission().toString());
			
			if(pack.getName().equals("empty")) return;
			//It wouldn't work for the empty pack because it isn't included in the HashMap of current packs
			
			String users = "";
			HashMap<Player, String> packs = this.packManager.getCurrentResourcepacks();
			for(Entry<Player, String> e : packs.entrySet())
				if(pack.getName().equals(e.getValue()))
					users += e.getKey().getName() + ", ";
			if(users.isEmpty())
				users = ChatColor.ITALIC + "none";
			else
				users = users.substring(0, users.length() - 2);
			
			sender.sendMessage(ChatColor.DARK_AQUA + "Users: " + 
							   ChatColor.AQUA      + users);
		}
	}
	
	public Permission getResourcepackPermission(String s) {
		try {
			return Permission.valueOf(s.toUpperCase());
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
	
	public List<String> completeValues(String arg, String... strings) {
		List<String> completions = new ArrayList<String>();
		this.addCompletions(completions, arg, strings);
		return completions;
	}
	
	public List<String> completeResourcepack(CommandSender sender, String arg) {
		List<Resourcepack> visiblePacks;

		if(sender.hasPermission("dynamicresourcepacks.list.all"))
			visiblePacks = this.packManager.getResourcepacks();
		else if(sender.hasPermission("dynamicresourcepacks.list.usable"))
			visiblePacks = this.packManager.getUsableResourcepacks(sender);
		else if(sender.hasPermission("dynamicresourcepacks.list.selectable"))
			visiblePacks = this.packManager.getSelectableResourcepacks(sender);
		else
			return null;
		
		List<String> completions = new ArrayList<String>();
		for(Resourcepack pack : visiblePacks)
			if(pack.getName().startsWith(arg))
				completions.add(pack.getName());
		
		Collections.sort(completions);
		return completions;
	}
	
	public List<String> completePermission(String arg) {
		List<String> completions = new ArrayList<String>();
		for(Permission perm : Permission.values())
			if(StringUtil.startsWithIgnoreCase(perm.toString(), arg))
				completions.add(perm.toString());
		return completions;
	}
	
	public List<String> completeURL(CommandSender sender, String arg) {
		if(sender.hasPermission("dynamicresourcepacks.view.full")) {
			List<Resourcepack> packs;
			if(sender.hasPermission("dynamicresourcepacks.view.all"))
				packs = this.packManager.getResourcepacks();
			else if(sender.hasPermission("dynamicresourcepacks.view.usable"))
				packs = this.packManager.getUsableResourcepacks(sender);
			else if(sender.hasPermission("dynamicresourcepacks.view.selectable"))
				packs = this.packManager.getSelectableResourcepacks(sender);
			else
				return this.completeValues(arg, "http://", "https://");
			
			List<String> completions = new ArrayList<String>();
			for(Resourcepack pack : packs) {
				String domain = pack.getURL();
				int i = domain.indexOf('/', 8);
				if(i < 0)
					continue;
				domain = domain.substring(0, i + 1);
				if(completions.contains(domain))
					continue;
				if(StringUtil.startsWithIgnoreCase(domain, arg))
					completions.add(domain);
			}
			return (completions.size() == 0) ? this.completeValues(arg, "http://", "https://") : completions;
		} else
			return this.completeValues(arg, "http://", "https://");
	}
	
	public void addCompletions(List<String> completions, String arg, String... strings) {
		for(String s : strings)
			if(StringUtil.startsWithIgnoreCase(s, arg))
				completions.add(s);
	}
	
	public void addCommandCompletions(List<String> completions, String arg, String command, String... aliases) {
		if(command != null && StringUtil.startsWithIgnoreCase(command, arg))
			completions.add(command);
		if(arg.length() > 0)
			this.addCompletions(completions, arg, aliases);
	}
}