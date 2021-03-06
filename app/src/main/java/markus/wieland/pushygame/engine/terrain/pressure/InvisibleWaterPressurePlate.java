package markus.wieland.pushygame.engine.terrain.pressure;

import markus.wieland.pushygame.engine.Game;
import markus.wieland.pushygame.engine.entity.Entity;
import markus.wieland.pushygame.engine.events.ShowInvisibleWaterBlocksEvent;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.level.TerrainType;

public class InvisibleWaterPressurePlate extends PressurePlateTerrain {

    public InvisibleWaterPressurePlate(Coordinate coordinate, TerrainType terrainType) {
        super(coordinate, terrainType);
    }

    @Override
    public void interact(Entity entity, Game game) {
        game.execute(new ShowInvisibleWaterBlocksEvent());
    }

}
