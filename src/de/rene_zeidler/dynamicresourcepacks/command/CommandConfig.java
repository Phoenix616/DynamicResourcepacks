package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permissible;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;

public class CommandConfig extends DynamicResourcepacksCommand {

	public CommandConfig(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		if(!sender.hasPermission("dynamicresourcepacks.config")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		
		Configuration config = this.plugin.getConfig();
		
		if(this.args.length <= 1) {
			ConfigurationSection section = config;
			String sectionName;
			if(this.args.length == 1) {
				if(section.isConfigurationSection(this.args[0])) {
					section = section.getConfigurationSection(this.args[0]);
					sectionName = section.getCurrentPath();
				} else {
					Object value = section.get(this.args[0]);
					if(value == null)
						sender.sendMessage(ChatColor.RED + this.args[0] + " does not exist!");
					else
						sender.sendMessage(ChatColor.GREEN + "The value for " + this.args[0] + " is: " +
										   ChatColor.DARK_GREEN + value.toString());
					return true;
				}
			} else
				sectionName = ChatColor.ITALIC + "root";
			
			StringBuilder msg = new StringBuilder();
			msg.append(ChatColor.BLUE)
			.append("Config keys for ")
			.append(ChatColor.DARK_AQUA)
			.append(sectionName)
			.append(ChatColor.BLUE)
			.append(": ");
			
			boolean first = true;
			for(String key : section.getValues(false).keySet()) {
				if(!first) {
					msg.append(ChatColor.AQUA)
					.append(", ");
				} else first = false;
				
				msg.append(ChatColor.DARK_AQUA);
				if(section.isConfigurationSection(key))
					msg.append(ChatColor.ITALIC);
				msg.append(key);
			}
			
			sender.sendMessage(msg.toString());
		} else if(this.args.length >= 2) {
			String valueArg = this.args[1];
			for(int i = 2; i < this.args.length; i++)
				valueArg += " " + this.args[i];
			
			Object oldValue = config.get(this.args[0]);
			Object newValue = valueArg;
			if(valueArg.equalsIgnoreCase("null")  || valueArg.equalsIgnoreCase("remove")
			|| valueArg.equalsIgnoreCase("clear") || valueArg.equalsIgnoreCase("delete"))
				newValue = null;
			else if(valueArg.equalsIgnoreCase("true") || valueArg.equalsIgnoreCase("yes"))
				newValue = true;
			else if(valueArg.equalsIgnoreCase("false") || valueArg.equalsIgnoreCase("no"))
				newValue = false;
			
			config.set(this.args[0], newValue);
			if(oldValue != null) {
				sender.sendMessage(ChatColor.GREEN + "Overwriting old value: " +
						   		   ChatColor.DARK_GREEN + oldValue.toString());
			}
			sender.sendMessage(ChatColor.GREEN + "The value for " + this.args[0] + " has been set to: " +
							   ChatColor.DARK_GREEN + newValue.toString());
			
			return true;
		}
		
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		if(!sender.hasPermission("dynamicresourcepacks.config"))
			return new ArrayList<String>();
		
		if(this.args.length == 1) {
			sender.sendMessage(ChatColor.GOLD + "Tab completion for configuration paths is not supported, omit the value argument to get a list of keys in the given configuration section.");
		} else if (this.args.length == 2) {
			ArrayList<String> completions = new ArrayList<String>();
			this.addCommandCompletions(completions, this.args[1], "null", "remove", "clear", "delete");
			this.addCommandCompletions(completions, this.args[1], "true", "yes");
			this.addCommandCompletions(completions, this.args[1], "false", "no");
			return completions;
		}
		
		return new ArrayList<String>();
	}

	public static boolean canSee(Permissible permissible) {
		return permissible.hasPermission("dynamicresourcepacks.config");
	}
	
}
