package de.rene_zeidler.dynamicresourcepacks;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardIntegration {
	private DynamicResourcepacks plugin;
	private Configuration config;
	private ResourcepackManager packManager;
	
	private boolean enabled = false;
	private HashMap<String, RegionMapEntry> regionPacks;
	private HashMap<Player, String> lastTrackedRegion;
	
	public WorldGuardIntegration(DynamicResourcepacks plugin) {
		this.plugin = plugin;
		this.config = this.plugin.getConfig();
		this.packManager = this.plugin.getResourcepackManager();
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public HashMap<String, RegionMapEntry> getRegionMapping() {
		return this.regionPacks;
	}
	
	public void loadFromConfig() {
		Plugin wgplugin = this.plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		if(this.config.getBoolean("worldGuardSupport") && wgplugin != null && (wgplugin instanceof WorldGuardPlugin))
			this.enabled = true;
		else {
			this.enabled = false;
			return;
		}
		
		this.regionPacks = new HashMap<String, RegionMapEntry>();
		
		ConfigurationSection section = this.config.getConfigurationSection("regions");
		if(section != null) {
			for(String region : section.getKeys(false)) {
				RegionMapEntry entry;
				if(section.isString(region))
					entry = new RegionMapEntry(region, section.getString(region));
				else if(section.isConfigurationSection(region))
					entry = new RegionMapEntry(region, section.getString("pack"),
													   section.getBoolean("setLocked"),
													   section.getBoolean("overrideLock"));
				else
					continue;
				
				if(entry.getPackName() != null && !entry.getPackName().isEmpty()
						&& !this.regionPacks.containsKey(entry.getRegionName()))
					this.regionPacks.put(entry.getRegionName(), entry);
			}
		}
	}
	
	public boolean isTrackedRegion(String region) {
		return this.regionPacks.containsKey(region);
	}
	
	public String getLastTrackedRegion(Player player) {
		return this.lastTrackedRegion.get(player);
	}
	
	public void setLastTrackedRegion(Player player, String region) {
		if(region != null)
			this.lastTrackedRegion.put(player, region);
		else
			this.lastTrackedRegion.remove(player);
	}
	
	public void updatePlayer(Player player) {
		RegionManager regionManager = WGBukkit.getRegionManager(player.getWorld());
		if(regionManager == null) return;
		ApplicableRegionSet regions = regionManager.getApplicableRegions(player.getLocation());
		
		boolean isInTrackedRegion = false;
		Iterator<ProtectedRegion> i = regions.iterator();
		while(i.hasNext()) {
			String region = i.next().getId();
			if(this.isTrackedRegion(region) && !this.getLastTrackedRegion(player).equals(region)) {
				this.enterRegion(player, region);
				isInTrackedRegion = true;
				break;
			}
		}
		if(!isInTrackedRegion)
			this.enterRegion(player, null);
	}
	
	public void enterRegion(Player player, String region) {
		if(region == null) {
			RegionMapEntry oldEntry = this.regionPacks.get(this.getLastTrackedRegion(player));
			if(oldEntry != null && this.packManager.hasResourcepack(player)
					&& this.packManager.getResourcepack(player).getName().equals(oldEntry.getPackName()))
				this.packManager.clearResourcepack(player);
		}
		this.setLastTrackedRegion(player, region);
		RegionMapEntry entry = this.regionPacks.get(region);
		if(entry == null) return;
		
		if(!entry.isOverrideLock() && this.packManager.getLocked(player)) return;
		Resourcepack pack = this.packManager.getResourcepackForName(entry.getPackName());
		if(pack == null) return;
		this.packManager.setResourcepack(player, pack);
		if(entry.isSetLocked())
			this.packManager.setLocked(player, true);
	}
}
