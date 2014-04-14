package de.rene_zeidler.dynamicresourcepacks.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.rene_zeidler.dynamicresourcepacks.DynamicResourcepacks;

public class CommandVersion extends DynamicResourcepacksCommand {

	public CommandVersion(DynamicResourcepacks plugin, String label,
			String dynamicResourcepacksAlias, String setresourcepackAlias,
			String[] args) {
		super(plugin, label, dynamicResourcepacksAlias, setresourcepackAlias, args);
	}

	@Override
	public boolean run(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_AQUA + this.plugin.getDescription().getFullName() + ChatColor.BLUE + " by René Zeidler");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender) {
		return new ArrayList<String>();
	}

}
