package markus.wieland.pushygame.engine.entity.logic;

import markus.wieland.pushygame.R;
import markus.wieland.pushygame.engine.Game;
import markus.wieland.pushygame.engine.entity.GameFinishEntity;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.helper.Direction;
import markus.wieland.pushygame.engine.level.EntityType;

public class Lamp extends LogicGate implements GameFinishEntity, LogicInput {

    private boolean active;

    public Lamp(Coordinate coordinate, EntityType entityType) {
        super(coordinate, entityType);
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int[] getDrawableList() {
        return new int[]{getDrawable()};
    }

    @Override
    public PortType configurePortSouth() {
        return PortType.INPUT;
    }

    @Override
    public PortType configurePortNorth() {
        return PortType.INPUT;
    }

    @Override
    public PortType configurePortEast() {
        return PortType.INPUT;
    }

    @Override
    public PortType configurePortWest() {
        return PortType.INPUT;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int getDrawable() {
        return active ? R.drawable.lamp_active : super.getDrawable();
    }

    @Override
    public boolean isInput(Direction direction) {
        return true;
    }

    @Override
    public boolean isOutputActive(Game game) {
        boolean willBeActive = false;
        for (Direction direction : getPorts().getInputs()) {
            willBeActive = isInputActive(game, direction) || willBeActive;
        }

        setActive(willBeActive);
        return isActive();
    }

    @Override
    public boolean check(Game game) {
        return isActive();
    }
}
