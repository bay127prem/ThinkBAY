package sample.kernel.util;

public enum TaskPeriod {
	ONCE(ThinkStr.once),
	DAILY(ThinkStr.daily),
	WEEKLY(ThinkStr.weekly),
	MONTHLY(ThinkStr.monthly),
	YEARLY(ThinkStr.yearly);
	
	private ThinkStr str;
	
	private TaskPeriod(ThinkStr str) {
		this.str = str;
	}
	
	public ThinkStr getThinkStr() {
		return str;
	}
}
