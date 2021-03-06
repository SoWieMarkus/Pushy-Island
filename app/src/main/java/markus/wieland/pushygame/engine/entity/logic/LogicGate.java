package markus.wieland.pushygame.engine.entity.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import markus.wieland.pushygame.engine.Game;
import markus.wieland.pushygame.engine.entity.Entity;
import markus.wieland.pushygame.engine.events.LogicEvent;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.helper.Direction;
import markus.wieland.pushygame.engine.helper.Field;
import markus.wieland.pushygame.engine.level.EntityType;

public abstract class LogicGate extends Entity implements LogicOutput, LogicInput {

    public static final int TICK = 300;

    private final Ports ports;

    private boolean currentOutput;

    public LogicGate(Coordinate coordinate, EntityType entityType) {
        super(coordinate, entityType);
        this.ports = new Ports();
        for (Direction direction : Objects.requireNonNull(Direction.class.getEnumConstants())) {
            ports.configure(direction, configurePortType(direction));
        }
        this.ports.updateLists();
    }

    @Override
    public int[] getDrawableList() {
        return getIOOverlay(this, getDrawable());
    }

    public static int[] getIOOverlay(Field field, int baseDrawable) {
        List<Integer> drawables = new ArrayList<>();
        drawables.add(baseDrawable);
        for (Direction direction : Objects.requireNonNull(Direction.class.getEnumConstants())) {
            if (field instanceof LogicInput && ((LogicInput) field).isInput(direction))
                drawables.add(LogicInput.drawableByDirection(direction));
            if (field instanceof LogicOutput && ((LogicOutput) field).isOutput(direction))
                drawables.add(LogicOutput.drawableByDirection(direction));
        }

        int[] drawablesArray = new int[drawables.size()];
        for (int i = 0; i < drawables.size(); i++) {
            drawablesArray[i] = drawables.get(i);
        }
        return drawablesArray;
    }

    public Ports getPorts() {
        return ports;
    }

    public PortType configurePortNorth() {
        return PortType.VOID;
    }

    public PortType configurePortSouth() {
        return PortType.VOID;
    }

    public PortType configurePortEast() {
        return PortType.VOID;
    }

    public PortType configurePortWest() {
        return PortType.VOID;
    }

    public PortType configurePortType(Direction direction) {
        switch (direction) {
            case NORTH:
                return configurePortNorth();
            case WEST:
                return configurePortWest();
            case EAST:
                return configurePortEast();
            default:
                return configurePortSouth();
        }
    }

    @Override
    public boolean isInput(Direction direction) {
        return getPorts().getPortType(direction.getOppositeDirection()) == PortType.INPUT;
    }

    @Override
    public boolean isOutput(Direction direction) {
        return getPorts().getPortType(direction.getOppositeDirection()) == PortType.OUTPUT;
    }


    @Override
    public boolean isInputActive(Game game, Direction direction) {
        return LogicInput.isInputActive(game, direction, getCoordinate());
    }

    @Override
    public void update(Game game) {
        boolean isOutputActive = isOutputActive(game);
        if (currentOutput == isOutputActive) return;
        currentOutput = isOutputActive;
        game.getEntityManager().invalidate(this);
        LogicGateThread logicGateThread = new LogicGateThread(game);
        logicGateThread.start();
    }

    private class LogicGateThread extends Thread {

        private final Game game;

        public LogicGateThread(Game game) {
            this.game = game;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(TICK);
                game.getActivity().runOnUiThread(() -> {
                    for (Direction direction : getPorts().getOutputs()) {
                        game.execute(new LogicEvent(getCoordinate().getNextCoordinate(direction), direction));
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

}
