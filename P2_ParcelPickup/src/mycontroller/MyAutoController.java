package mycontroller;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

import java.util.HashMap;

public class MyAutoController extends CarController {
	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 1;
	// Car Speed to move at
	private final int CAR_MAX_SPEED = 1;
	
	public StrategyFactory strategyFactory = StrategyFactory.getInstance();
	Strategy s1 = StrategyFactory.getStrategy("GetNextForParcelStrategy");
	Strategy s2 = StrategyFactory.getStrategy("GetNextToExitStrategy");
	Strategy s3 = StrategyFactory.getStrategy("GetNextToExplorerStrategy");

	public MyAutoController(Car car) {
		super(car);
		init();
	}

	private void init() {
		Coordinate startPosition = new Coordinate(getPosition());
		VisitRecordUtil.visit(startPosition);
		
	}

	@Override
	public void update() {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();

		// current position
		Coordinate current = new Coordinate(getPosition());
		VisitRecordUtil.visit(current);
		VisitRecordUtil.recognizeAroundView(currentView);
		
		// next coordinate to go
		Coordinate nextCoordinate = null;
		// find parcel, find the way to go
		if (VisitRecordUtil.getParcelMap().size() > 0) {
			nextCoordinate = s1.move(current);
		}

		// no parcel
		if (nextCoordinate == null ) {
			// we have package enough parcels, exit!
			if (numParcelsFound() >= numParcels()) {
				nextCoordinate = s2.move(current);
			}
			// to explore other place
			if (nextCoordinate == null) {
				nextCoordinate = s3.move(current);
			}
		}

		
		towardNext(currentView, current, nextCoordinate);
		
	}

	private void towardNext(HashMap<Coordinate, MapTile> currentView, Coordinate current, Coordinate nextCoordinate) {
		if (getSpeed() == 0) {
			if (!checkWallAhead(getOrientation(), currentView)) {
				applyForwardAcceleration();// if there is no wall in the fron,move forward
			} else {
				applyReverseAcceleration(); // if there is wall in the fron,move backward
			}
		}
		
		moveToTarget(current, getOrientation(),nextCoordinate);
	}

	private void moveToTarget(Coordinate currentCoordinate, WorldSpatial.Direction orientation,Coordinate neighborCoordinate) {
        boolean isHorizontal = false;
        boolean isVertical = false;
        boolean isLeft = false;
        boolean isBelow = false;
        if (currentCoordinate.x == neighborCoordinate.x) {
            isHorizontal = true;
        } else if (currentCoordinate.x > neighborCoordinate.x) {
            isLeft = true;
        }

        if (currentCoordinate.y == neighborCoordinate.y) {
            isVertical = true;
        } else if (currentCoordinate.y > neighborCoordinate.y) {
            isBelow = true;
        }
        
        switch (orientation) {
            case EAST:
                if (isVertical) {
                    if (isLeft) {
                        applyReverseAcceleration();
                    } else {
                        applyForwardAcceleration();
                    }
                } else if (isHorizontal) {
                    if (isBelow) {
                        turnRight();
                    } else {
                        turnLeft();
                    }
                }
                break;
            case WEST:
                if (isVertical) {
                    if (isLeft) {
                        applyForwardAcceleration();
                    } else {
                        applyReverseAcceleration();
                    }
                } else if (isHorizontal) {
                    if (isBelow) {
                        turnLeft();
                    } else {
                        turnRight();
                    }
                }
                break;
            case SOUTH:
                if (isVertical) {
                    if (isLeft) {
                        turnRight();
                    } else {
                        turnLeft();
                    }
                } else if (isHorizontal) {
                    if (isBelow) {
                        applyForwardAcceleration();
                    } else {
                        applyReverseAcceleration();
                    }
                }
                break;
            case NORTH:
                if (isVertical) {
                    if (isLeft) {
                        turnLeft();
                    } else {
                        turnRight();
                    }
                } else if (isHorizontal) {
                    if (isBelow) {
                        applyReverseAcceleration();
                    } else {
                        applyForwardAcceleration();
                    }
                }
                break;
                
        }
    }

	/**
	 * Check if you have a wall in front of you!
	 *
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		switch (orientation) {
			case EAST:
				return checkEast(currentView);
			case NORTH:
				return checkNorth(currentView);
			case SOUTH:
				return checkSouth(currentView);
			case WEST:
				return checkWest(currentView);
			default:
				return false;
		}
	}

	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x + i, currentPosition.y));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkWest(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x - i, currentPosition.y));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkNorth(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y + i));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkSouth(HashMap<Coordinate, MapTile> currentView) {
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y - i));
			if (tile.isType(MapTile.Type.WALL)) {
				return true;
			}
		}
		return false;
	}
}
