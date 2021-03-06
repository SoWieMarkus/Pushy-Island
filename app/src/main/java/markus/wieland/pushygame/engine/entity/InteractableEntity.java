package markus.wieland.pushygame.engine.entity;

import markus.wieland.pushygame.engine.Game;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.helper.Direction;
import markus.wieland.pushygame.engine.level.EntityType;

public abstract class InteractableEntity extends Entity{

    public InteractableEntity(Coordinate coordinate, EntityType entityType) {
        super(coordinate, entityType);
    }

    public void interact(Direction direction, Game game) {
        if (canInteractFromThisDirection(direction.getOppositeDirection()))
            executeInteraction(direction, game);
    }

    protected abstract void executeInteraction(Direction direction, Game game);

    public abstract boolean canInteractFromThisDirection(Direction direction);
}
