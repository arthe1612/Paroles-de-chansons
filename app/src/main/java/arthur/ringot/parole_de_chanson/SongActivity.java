package arthur.ringot.parole_de_chanson;
import android.app.ActionBar;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

public class SongActivity extends AppCompatActivity {
    String lyrics;
    String artistName;
    String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras(); //récupération de toutes les valeurs nécéssaires à l'activité

        if(extras != null){
            lyrics = extras.getString("lyrics");
            artistName = extras.getString("artistName");
            songName = extras.getString("songName");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView lyricsView = findViewById(R.id.lyricsView);
        TextView songName_view = findViewById(R.id.songName);
        TextView artistName_view = findViewById(R.id.artistName);

        lyricsView.setText(lyrics);
        songName_view.setText(songName);
        artistName_view.setText(artistName);
    }
}
