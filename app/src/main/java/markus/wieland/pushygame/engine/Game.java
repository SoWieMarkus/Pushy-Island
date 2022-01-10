package markus.wieland.pushygame.engine;

import markus.wieland.pushygame.engine.entity.Entity;
import markus.wieland.pushygame.engine.entity.interactable.CrabMama;
import markus.wieland.pushygame.engine.entity.interactable.Flower;
import markus.wieland.pushygame.engine.entity.interactable.rewards.CrabMamaReward;
import markus.wieland.pushygame.engine.entity.interactable.rewards.PirateReward;
import markus.wieland.pushygame.engine.entity.movable.Barrel;
import markus.wieland.pushygame.engine.entity.movable.Box;
import markus.wieland.pushygame.engine.entity.movable.Count;
import markus.wieland.pushygame.engine.entity.movable.Octopus;
import markus.wieland.pushygame.engine.entity.movable.PushShell;
import markus.wieland.pushygame.engine.entity.movable.Pushy;
import markus.wieland.pushygame.engine.entity.movable.SeaStar;
import markus.wieland.pushygame.engine.entity.movable.Statue;
import markus.wieland.pushygame.engine.events.Event;
import markus.wieland.pushygame.engine.events.GameEventListener;
import markus.wieland.pushygame.engine.events.InventoryEventListener;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.helper.Direction;
import markus.wieland.pushygame.engine.helper.Inventory;
import markus.wieland.pushygame.engine.helper.Matrix;
import markus.wieland.pushygame.engine.level.TerrainType;
import markus.wieland.pushygame.engine.level.TileMapBuilder;
import markus.wieland.pushygame.engine.terrain.BarrelFinish;
import markus.wieland.pushygame.engine.terrain.ChangeableFlower;
import markus.wieland.pushygame.engine.terrain.FlowerFinish;
import markus.wieland.pushygame.engine.terrain.StatueFinish;
import markus.wieland.pushygame.engine.terrain.Terrain;
import markus.wieland.pushygame.engine.terrain.Water;
import markus.wieland.pushygame.engine.terrain.pressure.PressurePlateTerrain;
import markus.wieland.pushygame.engine.terrain.pressure.Teleporter;
import markus.wieland.pushygame.ui.PushyFieldView;

public class Game {

    private final EntityManager entityManager;
    private final TerrainManager terrainManager;
    private final Inventory inventory;
    private GameEventListener gameEventListener;

    public Game(Matrix<PushyFieldView<Entity>> entityViews, Matrix<PushyFieldView<Terrain>> terrainViews) {
        this.entityManager = new EntityManager(entityViews);
        this.terrainManager = new TerrainManager(terrainViews);
        this.inventory = new Inventory();

        for (Box box : entityManager.getOfType(Box.class)) {
            if (terrainManager.getObject(box) instanceof Water) {
                entityManager.remove(box);
                terrainManager.setObject(box.getCoordinate(), TileMapBuilder.build(TerrainType.BOX_WATER, box.getCoordinate()));
            }
        }
        for (SeaStar seaStar : entityManager.getOfType(SeaStar.class)) {
            if (terrainManager.getObject(seaStar) instanceof Water) {
                entityManager.remove(seaStar);
            }
        }
    }

    public void setInventoryEventListener(InventoryEventListener inventoryEventListener) {
        this.inventory.setInventoryEventListener(inventoryEventListener);
    }

    public void setGameEventListener(GameEventListener gameEventListener) {
        this.gameEventListener = gameEventListener;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public TerrainManager getTerrainManager() {
        return terrainManager;
    }


    public Inventory getInventory() {
        return inventory;
    }

    public void move(Direction direction) {

        Pushy pushy = entityManager.getPushy();

        Coordinate currentCoordinate = pushy.getCoordinate();
        Coordinate nextCoordinate = currentCoordinate.getNextCoordinate(direction);

        pushy.setFacing(direction);
        entityManager.invalidate(pushy);

        pushy.move(nextCoordinate, this);

        for (PressurePlateTerrain pressurePlateTerrain : terrainManager.getPressurePlateTerrains()) {
            pressurePlateTerrain.interact(entityManager.getObject(pressurePlateTerrain), this);
        }

        for (Entity entity : entityManager.getAll()) {
            entity.setTeleported(terrainManager.getObject(entity) instanceof Teleporter);
        }


    }

    public void execute(Event event) {
        event.setGame(this);
        event.execute();
    }

    public void finishGame() {
        if (checkGameState()) gameEventListener.onFinish();
    }

    public boolean checkGameState() {
        if (entityManager.getPirate() != null && inventory.getAmount(PirateReward.class) == 0)
            return false;


        for (Entity entity : entityManager.getAll()) {
            if (entity instanceof SeaStar) return false;
            if (entity instanceof Statue) {
                Terrain terrain = terrainManager.getObject(entity);
                if (!(terrain instanceof StatueFinish && ((StatueFinish) terrain).equalsType(((Statue) entity).getType())))
                    return false;
            }
            if (entity instanceof CrabMama && inventory.getAmount(CrabMamaReward.class) == 0)
                return false;
            if (entity instanceof CrabMama && inventory.getAmount(CrabMamaReward.class) == 0)
                return false;
            if (entity instanceof Count && !((Count) entity).isUncovered()) return false;
            if (entity instanceof PushShell && ((PushShell) entity).getCount() != PushShell.HAPPY) return false;
            if (entity instanceof Barrel && !(terrainManager.getObject(entity) instanceof BarrelFinish)) return false;
            if (entity instanceof Octopus) return false;
        }
        for (FlowerFinish flowerFinish : terrainManager.getOfType(FlowerFinish.class)) {
            Entity entity = entityManager.getObject(flowerFinish);
            if (entity == null) return false;
            if (!(entity instanceof Flower)) return false;
            if (((Flower) entity).getType() != flowerFinish.getFlowerType()) return false;
        }

        TerrainType changeableFlowerTerrainType = null;
        for (ChangeableFlower changeableFlower : terrainManager.getOfType(ChangeableFlower.class)) {
            if (changeableFlowerTerrainType == null) changeableFlowerTerrainType = changeableFlower.getType();
            if (changeableFlowerTerrainType != changeableFlower.getType()) return false;
        }

        return true;
    }

}
