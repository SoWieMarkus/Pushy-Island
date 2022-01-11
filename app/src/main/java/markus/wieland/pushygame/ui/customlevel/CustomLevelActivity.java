package markus.wieland.pushygame.ui.customlevel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import markus.wieland.defaultappelements.uielements.activities.DefaultActivity;
import markus.wieland.pushygame.R;
import markus.wieland.pushygame.engine.level.LevelDisplayItem;
import markus.wieland.pushygame.persistence.LevelViewModel;
import markus.wieland.pushygame.ui.customlevel.levelbuilder.LevelBuilderActivity;
import markus.wieland.pushygame.ui.game.GameActivity;

public class CustomLevelActivity extends DefaultActivity implements View.OnClickListener, Observer<List<LevelDisplayItem>>, CustomLevelInteractionListener {

    private FloatingActionButton add;
    private RecyclerView recyclerView;
    private LevelViewModel levelViewModel;
    private CustomLevelAdapter customLevelAdapter;

    public CustomLevelActivity() {
        super(R.layout.activity_custom_level);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindViews() {
        add = findViewById(R.id.activity_custom_level_add);
        recyclerView = findViewById(R.id.activity_custom_level_recycler_view);
        levelViewModel = ViewModelProviders.of(this).get(LevelViewModel.class);
    }

    @Override
    public void initializeViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        add.setOnClickListener(this);
    }

    @Override
    public void execute() {
        levelViewModel.getAllLevel(false).observe(this, this);
        customLevelAdapter = new CustomLevelAdapter(this);
        recyclerView.setAdapter(customLevelAdapter);
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, LevelBuilderActivity.class));
    }

    @Override
    public void onChanged(List<LevelDisplayItem> levelDisplayItems) {
        customLevelAdapter.submitList(levelDisplayItems);
    }

    @Override
    public void onEdit(LevelDisplayItem levelDisplayItem) {
        startActivity(new Intent(this, LevelBuilderActivity.class).putExtra(LevelBuilderActivity.KEY_LEVEL_ID, levelDisplayItem.getNumber()));
    }

    @Override
    public void onShare(LevelDisplayItem levelDisplayItem) {
        // TODO
    }

    @Override
    public void onPlay(LevelDisplayItem levelDisplayItem) {
        startActivity(new Intent(this, GameActivity.class).putExtra(GameActivity.LEVEL_CODE, levelDisplayItem.getFile()));
    }

    @Override
    public void onDelete(LevelDisplayItem levelDisplayItem) {
        levelViewModel.delete(levelDisplayItem);
    }
}