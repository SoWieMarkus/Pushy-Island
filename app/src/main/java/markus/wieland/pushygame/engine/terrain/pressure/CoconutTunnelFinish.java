package markus.wieland.pushygame.engine.terrain.pressure;

import markus.wieland.pushygame.engine.Game;
import markus.wieland.pushygame.engine.entity.Entity;
import markus.wieland.pushygame.engine.entity.movable.Coconut;
import markus.wieland.pushygame.engine.events.CoconutTunnelFinishEvent;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.level.TerrainType;

public class CoconutTunnelFinish extends PressurePlateTerrain {

    private Entity lastEntity;

    public CoconutTunnelFinish(Coordinate coordinate, TerrainType terrainType) {
        super(coordinate, terrainType);
    }

    @Override
    public void interact(Entity entity, Game game) {
        if (lastEntity == entity) return;
        lastEntity = entity;
        if (entity instanceof Coconut) {
            game.execute(new CoconutTunnelFinishEvent());
        }
    }

    @Override
    public boolean isCoconutTunnel() {
        return true;
    }

    @Override
    public boolean coconutCanEnterFromTunnel() {
        return true;
    }
}
