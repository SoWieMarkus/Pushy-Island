package markus.wieland.pushygame.engine.entity.interactable;

import markus.wieland.pushygame.R;
import markus.wieland.pushygame.engine.entity.CollectibleEntity;
import markus.wieland.pushygame.engine.entity.RewardEntity;
import markus.wieland.pushygame.engine.entity.collectible.Coin;
import markus.wieland.pushygame.engine.entity.collectible.Key;
import markus.wieland.pushygame.engine.helper.Coordinate;
import markus.wieland.pushygame.engine.helper.Direction;
import markus.wieland.pushygame.engine.level.EntityType;

public class Chest extends RewardEntity {

    public Chest(Coordinate coordinate, EntityType entityType) {
        super(coordinate, entityType, Key.class);
        setNeedItemsAmount(1);
    }

    @Override
    public int getDrawable() {
        return gotReward ? R.drawable.chest_open : super.getDrawable();
    }

    @Override
    protected CollectibleEntity createRewardItem() {
        return new Coin(getCoordinate(), EntityType.COIN);
    }

    @Override
    public boolean canInteractFromThisDirection(Direction direction) {
        return direction == Direction.SOUTH;
    }

}
