package mycontroller;

import utilities.Coordinate;

public class GetNextToExitStrategy implements Strategy {

	@Override
	public Coordinate move(Coordinate current) {
		for (Coordinate coordinate : VisitRecordUtil.exitMap.keySet()) {
            Coordinate potentialGoal = VisitRecordUtil.searchNextToTarget(current, coordinate);
            if (potentialGoal != null) {
                return potentialGoal;
            }
        }
        return null;
    }
	
}
