package markus.wieland.pushygame.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import markus.wieland.databases.BaseDataAccessObject;
import markus.wieland.pushygame.engine.level.LevelDisplayItem;

@Dao
public interface LevelDataAccessObject extends BaseDataAccessObject<LevelDisplayItem> {

    @Query("SELECT * FROM level")
    LiveData<List<LevelDisplayItem>> getAllLevel();

    @Query("SELECT * FROM level WHERE isCampaign = :isCampaign")
    LiveData<List<LevelDisplayItem>> getAllLevel(boolean isCampaign);

    @Query("SELECT * FROM level WHERE number = :id")
    LiveData<LevelDisplayItem> getLevel(long id);


}
