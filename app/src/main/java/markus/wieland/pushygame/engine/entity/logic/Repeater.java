package markus.wieland.pushygame.engine.entity.logic;

import java.util.Objects;

import markus.wieland.pushygame.engine.Game;
import markus.wieland.pushygame.engine.entity.movable.MovableEntity;
import markus.wieland.pushygame.engine.events.LogicEvent;
import markus.wieland.pushygame.engine.events.UpdateCableEvent;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.helper.Direction;
import markus.wieland.pushygame.engine.level.EntityType;

public class Repeater extends MovableEntity implements LogicInput, LogicOutput {

    public Repeater(Coordinate coordinate, EntityType entityType) {
        super(coordinate, entityType);
        currentOutput = false;
    }

    private boolean currentOutput;

    @Override
    public boolean isInputActive(Game game, Direction direction) {
        return LogicInput.isInputActive(game, direction, getCoordinate());
    }

    public Direction getDirectionFromType() {
        switch (getType()) {
            case REPEATER_EAST:
                return Direction.EAST;
            case REPEATER_NORTH:
                return Direction.NORTH;
            case REPEATER_SOUTH:
                return Direction.SOUTH;
            default:
                return Direction.WEST;
        }
    }

    public EntityType getEntityTypeFromDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return EntityType.REPEATER_NORTH;
            case SOUTH:
                return EntityType.REPEATER_SOUTH;
            case EAST:
                return EntityType.REPEATER_EAST;
            default:
                return EntityType.REPEATER_WEST;
        }
    }

    @Override
    public int[] getDrawableList() {
        return LogicGate.getIOOverlay(this, getDrawable());
    }

    @Override
    public boolean isInput(Direction direction) {
        return getDirectionFromType().getOppositeDirection() == direction.getOppositeDirection();
    }

    @Override
    public boolean isOutputActive(Game game) {
        for (Direction direction : Objects.requireNonNull(Direction.class.getEnumConstants())) {
            if (isInput(direction.getOppositeDirection()) && isInputActive(game, direction))
                return true;
        }
        return false;
    }

    @Override
    public boolean isOutput(Direction direction) {
        return getDirectionFromType() == direction.getOppositeDirection();
    }

    @Override
    protected void executeMove(Coordinate nextCoordinate, Game game) {
        Direction direction = getCoordinate().getDirection(nextCoordinate);
        super.executeMove(nextCoordinate, game);
        setType(getEntityTypeFromDirection(direction));
        game.execute(new UpdateCableEvent());
        update(game);
    }

    @Override
    public void update(Game game) {
        boolean isOutputActive = isOutputActive(game);
        if (isOutputActive == currentOutput) return;
        currentOutput = isOutputActive;
        game.getEntityManager().invalidate(this);
        for (Direction directionFromList : Objects.requireNonNull(Direction.class.getEnumConstants())) {
            if (isOutput(directionFromList.getOppositeDirection()))
                game.execute(new LogicEvent(getCoordinate().getNextCoordinate(directionFromList), directionFromList));
        }
    }
}
