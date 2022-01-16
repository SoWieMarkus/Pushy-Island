package markus.wieland.pushygame.engine.entity.logic;

import android.app.Activity;

import markus.wieland.pushygame.engine.Game;
import markus.wieland.pushygame.engine.entity.movable.MovableEntity;
import markus.wieland.pushygame.engine.events.LogicEvent;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.helper.Direction;
import markus.wieland.pushygame.engine.level.EntityType;

public class Repeater extends MovableEntity implements LogicInput, LogicOutput {

    public Repeater(Coordinate coordinate, EntityType entityType) {
        super(coordinate, entityType);
    }

    @Override
    public boolean isInputActive(Game game, Direction direction) {
        return LogicInput.isInputActive(game, direction, getCoordinate());
    }

    public Direction getDirectionFromType(){
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

    public EntityType getEntityTypeFromDirection(Direction direction){
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
        return getDirectionFromType() == direction;
    }

    @Override
    public boolean isOutputActive(Game game) {
        for (Direction direction : Direction.class.getEnumConstants()) {
            if (isInput(direction) && isInputActive(game, direction))
                return true;
        }
        return false;
    }

    @Override
    public boolean isOutput(Direction direction) {
        return getDirectionFromType() == direction.getOppositeDirection();
    }

    @Override
    public PortType getPortType(Direction direction) {
        return null;
    }

    @Override
    protected void executeMove(Coordinate nextCoordinate, Game game) {
        Direction direction = getCoordinate().getDirection(nextCoordinate);
        super.executeMove(nextCoordinate, game);
        setType(getEntityTypeFromDirection(direction));

        update(game);
    }

    @Override
    public void update(Game game) {

        game.getEntityManager().invalidate(this);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(300);
                ((Activity) game.getEntityManager().getView(getCoordinate()).getContext()).runOnUiThread(() -> {
                    for (Direction direction1 : Direction.class.getEnumConstants()) {
                        if (isOutput(direction1) && isOutputActive(game))
                            game.execute(new LogicEvent(getCoordinate().getNextCoordinate(direction1), direction1));
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
