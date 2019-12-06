package mycontroller;

public class StrategyFactory {
	
	private StrategyFactory() {}
	public static StrategyFactory obj;
	
	public static StrategyFactory getInstance() 
	{
		if (obj==null)
		{
			obj = new StrategyFactory();
		}
		return obj;
	}
	
	public static Strategy getStrategy(String strategyType) {
		if (strategyType == null) {
			return null;
		}
		
		if (strategyType.equalsIgnoreCase("GetNextForParcelStrategy")) {
			return new GetNextForParcelStrategy();
		}
		else if (strategyType.equalsIgnoreCase("GetNextToExitStrategy")) {
			return new GetNextToExitStrategy();
			
		}
		else if (strategyType.equalsIgnoreCase("GetNextToExplorerStrategy")) {
			return new GetNextToExplorerStrategy();
		}
		
		return null;
	}

}
