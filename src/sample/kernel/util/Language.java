package sample.kernel.util;

public enum Language {
	AR(true, "العربيّة"),
	EN(false, "English"),
	FR(false, "Français");
	
	private Boolean RTL;
	private String name;
	
	private Language(Boolean RTL, String name) {
		this.RTL = RTL;
		this.name = name;
	}
	
	public Boolean isRTL() {
		return RTL;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
