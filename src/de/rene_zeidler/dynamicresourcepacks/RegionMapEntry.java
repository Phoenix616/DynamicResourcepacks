package de.rene_zeidler.dynamicresourcepacks;

public class RegionMapEntry {
	private String regionName;
	private String packName;
	private boolean setLocked;
	private boolean overrideLock;
	
	public RegionMapEntry(String regionName, String packName) {
		this(regionName, packName, false, false);
	}
	
	public RegionMapEntry(String regionName, String packName,
			boolean setLocked, boolean overrideLock) {
		this.regionName = regionName;
		this.packName = packName;
		this.setLocked = setLocked;
		this.overrideLock = overrideLock;
	}

	public String getRegionName() {
		return regionName;
	}

	public String getPackName() {
		return packName;
	}

	public boolean isSetLocked() {
		return setLocked;
	}

	public boolean isOverrideLock() {
		return overrideLock;
	}
}
