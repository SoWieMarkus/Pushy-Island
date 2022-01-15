package markus.wieland.pushygame.engine.entity.collectible;

import markus.wieland.pushygame.engine.entity.CollectibleEntity;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.level.EntityType;

public class Energy extends CollectibleEntity {
    public Energy(Coordinate coordinate, EntityType entityType) {
        super(coordinate, entityType);
    }
}
