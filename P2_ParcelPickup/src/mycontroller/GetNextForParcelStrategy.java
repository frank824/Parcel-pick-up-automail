package mycontroller;

import utilities.Coordinate;

public class GetNextForParcelStrategy implements Strategy {

	@Override
	public Coordinate move(Coordinate current) {
		for (Coordinate coordinate : VisitRecordUtil.parcelMap.keySet()) {
            Coordinate potentialGoal = VisitRecordUtil.searchNextToTarget(current, coordinate);
            if (potentialGoal != null) {
                return potentialGoal;
            }
        }
        return null;
	}

}
