package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {
	private DynamicResourcepacks plugin;
	private PlayerManager playerManager;
	
	public PlayerListener(DynamicResourcepacks plugin) {
		this.plugin = plugin;
		this.playerManager = this.plugin.getPlayerManager();
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		this.playerManager.getResourcepackManager().loadPlayerFromConfig(event.getPlayer());
	}
}
