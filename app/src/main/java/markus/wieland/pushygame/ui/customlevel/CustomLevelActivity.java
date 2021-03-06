package markus.wieland.pushygame.ui.customlevel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import markus.wieland.defaultappelements.uielements.activities.DefaultActivity;
import markus.wieland.pushygame.R;
import markus.wieland.pushygame.engine.level.LevelConverter;
import markus.wieland.pushygame.engine.level.LevelDisplayItem;
import markus.wieland.pushygame.engine.level.RawLevel;
import markus.wieland.pushygame.levelbuilder.LevelBuilder;
import markus.wieland.pushygame.persistence.LevelViewModel;
import markus.wieland.pushygame.ui.customlevel.levelbuilder.LevelBuilderActivity;
import markus.wieland.pushygame.ui.dialog.Dialog;
import markus.wieland.pushygame.ui.dialog.TextInputDialog;
import markus.wieland.pushygame.ui.game.GameActivity;

public class CustomLevelActivity extends DefaultActivity implements View.OnClickListener, Observer<List<LevelDisplayItem>>, CustomLevelInteractionListener {

    private ExpandableFloatingActionButton add;
    private RecyclerView recyclerView;
    private LevelViewModel levelViewModel;
    private CustomLevelAdapter customLevelAdapter;

    private FloatingActionButton levelBuilderButton;
    private FloatingActionButton levelImportButton;

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

        levelBuilderButton = findViewById(R.id.activity_custom_level_builder_level);
        levelImportButton = findViewById(R.id.activity_custom_level_import_level);
    }

    @Override
    public void initializeViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        add.setOnClickListener(this);

        levelBuilderButton.setOnClickListener(view -> startActivity(new Intent(this, LevelBuilderActivity.class)));
        levelImportButton.setOnClickListener(this);

        add.initialize(levelBuilderButton, levelImportButton);
    }

    public void importLevel(TextInputDialog dialog) {
        String code = dialog.getText();
        LevelBuilder levelBuilder = new LevelBuilder(this);
        try {
            levelBuilder.importLevel(code);
            RawLevel rawLevel = new RawLevel(code);
            LevelDisplayItem levelDisplayItem = new LevelDisplayItem(rawLevel, code, LevelConverter.createThumbnail(CustomLevelActivity.this, levelBuilder.getTerrainManager(), levelBuilder.getEntityManager()));
            levelViewModel.insert(levelDisplayItem);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.level_builder_invalid_code), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void execute() {
        levelViewModel.getAllLevel(false).observe(this, this);
        customLevelAdapter = new CustomLevelAdapter(this);
        recyclerView.setAdapter(customLevelAdapter);
    }

    @Override
    public void onClick(View view) {
        TextInputDialog dialog = new TextInputDialog(this, "");
        dialog.setDeclineMessage(getString(R.string.dialog_cancel))
                .setMessage(getString(R.string.dialog_enter_code))
                .setOkEvent(dialog1 -> importLevel((TextInputDialog) dialog1))
                .getDialog()
                .show();
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
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, levelDisplayItem.getFile());
        startActivity(intent);
    }

    @Override
    public void onPlay(LevelDisplayItem levelDisplayItem) {
        startActivity(new Intent(this, GameActivity.class)
                .putExtra(GameActivity.LEVEL_ID, levelDisplayItem.getNumber())
                .putExtra(GameActivity.LEVEL_TEST, true));
    }

    @Override
    public void onDelete(LevelDisplayItem levelDisplayItem) {
        Dialog dialog = new Dialog(this);
        dialog.setMessage(getString(R.string.dialog_sure_delete))
                .setOkEvent(alertDialog -> levelViewModel.delete(levelDisplayItem))
                .getDialog()
                .show();
    }
}