package de.rene_zeidler.dynamicresourcepacks;

import org.bukkit.permissions.Permissible;

public class Resourcepack {
	private String name;
	private String displayName;
	private String url;
	/**
	 * The permission needed to get this resourcepack.
	 * If the player doesn't have this permission, there is no way for them to get it.
	 */
	private Permission generalPermission;
	/**
	 * The permission needed for the player to select the resourcepack himself (with a command).
	 * If the player doesn't have this permission, he can still get this pack when it is set by another player or event.
	 * The player also needs the permission for the specific command to be able to select a resourcepack.
	 */
	private Permission useSelfPermission;
	
	public enum Permission {
		/**
		 * No permission, everyone is allowed to use it
		 */
		NONE,
		/**
		 * Only the general use/set permission is needed (dynamicresourcepacks.usepack)
		 */
		GENERAL,
		/**
		 * The specific permission with the name of this pack is needed (dynamicresourcepacks.usepack.name)
		 */
		SPECIFIC
	}
	
	public Resourcepack(String name, String url) {
		this(name, name, url, Permission.GENERAL, Permission.SPECIFIC);
	}
	
	public Resourcepack(String name, String displayName, String url) {
		this(name, displayName, url, Permission.GENERAL, Permission.SPECIFIC);
	}
	
	public Resourcepack(String name, String displayName, String url, Permission generalPermission, Permission useSelfPermission) {
		this.name = name;
		this.displayName = displayName;
		this.url = url;
		this.generalPermission = generalPermission;
		this.useSelfPermission = useSelfPermission;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public Permission getGeneralPermission() {
		return generalPermission;
	}

	public void setGeneralPermission(Permission generalPermission) {
		this.generalPermission = generalPermission;
	}

	public Permission getUseSelfPermission() {
		return useSelfPermission;
	}

	public void setUseSelfPermission(Permission useSelfPermission) {
		this.useSelfPermission = useSelfPermission;
	}
	
	public String getGeneralPermissionNode() {
		if(this.generalPermission == Permission.NONE) return null;
		return "dynamicresourcepacks.usepack" + (this.generalPermission == Permission.SPECIFIC ? this.name : "");
	}
	
	public String getUseSelfPermissionNode() {
		if(this.useSelfPermission == Permission.NONE) return null;
		return "dynamicresourcepacks.usepack" + (this.useSelfPermission == Permission.SPECIFIC ? this.name : "");
	}
	
	public boolean checkGeneralPermission(Permissible sender) {
		if(this.generalPermission == Permission.NONE) return true;
		return sender.hasPermission("dynamicresourcepacks.usepack" + (this.generalPermission == Permission.SPECIFIC ? this.name : ""));
	}
	
	public boolean checkUseSelfPermission(Permissible sender) {
		if(this.useSelfPermission == Permission.NONE) return true;
		return sender.hasPermission("dynamicresourcepacks.usepack" + (this.generalPermission == Permission.SPECIFIC ? this.name : ""));
	}
}
