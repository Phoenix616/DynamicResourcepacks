package de.rene_zeidler.dynamicresourcepacks;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener, CommandExecutor, TabCompleter {
	private DynamicResourcepacks plugin;
	private PlayerManager playerManager;
	
	public PlayerListener(DynamicResourcepacks plugin) {
		this.plugin = plugin;
		this.playerManager = this.plugin.getPlayerManager();
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		this.playerManager.getResourcepackManager().loadPlayerFromConfig(event.getPlayer());
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		this.playerManager.getResourcepackManager().saveConfigForPlayer(event.getPlayer());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("dynamicresourcepacks"))
			return this.playerManager.handleDynamicResourcepacksCommand(sender, label, args);
		else if(command.getName().equalsIgnoreCase("setresourcepack"))
			return this.playerManager.handleSetResourcepackCommand(sender, label, args);
		
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Debugging!
		sender.sendMessage("Tab complete for: " + command.getName());
		sender.sendMessage("Alias: \"" + alias + "\"");
		int i = 0;
		for(String arg : args) {
			sender.sendMessage("Arg " + i + ": \"" + arg + "\"");
			i++;
		}
		return null;
	}
}
