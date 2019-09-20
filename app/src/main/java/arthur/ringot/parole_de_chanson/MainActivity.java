package arthur.ringot.parole_de_chanson;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String PREFERENCES = "preferences";

    EditText artistName;
    EditText songName;
    RequestQueue requestQueue;
    String lyrics ="null";
    String baseUrl = "https://api.lyrics.ovh/v1/"; //url de base de l'api externe
    String url;
    Boolean songNameisValid=false;
    Boolean artistNameisValid=false;

    //Méthode permettant de fermer le clavier automatiquement si nécessaire
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null)
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        artistName = (TextInputEditText) findViewById(R.id.artistName);
        songName = (TextInputEditText) findViewById(R.id.songName);

        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        artistName.setText(prefs.getString("artistName", "")); //restauration d'une éventuelle valeur "artistName" sauvegardée
        songName.setText(prefs.getString("songName", ""));//restauration d'une éventuelle valeur "songName" sauvegardée
    }

    //méthode appellée en appuyant sur "rechercher"
    public void getSong(View v){
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit();
        editor.putString("artistName", artistName.getText().toString()); //sauvegarde de la valeur artistName
        editor.putString("songName", songName.getText().toString()); //sauvegarde de la valeur songName
        editor.apply();

        artistNameisValid = !artistName.getText().toString().matches(""); //check de la valeur "nom de l'artiste"
        songNameisValid = !songName.getText().toString().matches(""); //check de la valeur "nom de la chanson"

        if(artistNameisValid && songNameisValid){ //vérification des données
            url = baseUrl + artistName.getText() + "/" + songName.getText(); //création de l'url pour utiliser l'api
            url = url.replace(" ", "%20");
            JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url, //exécution d'un http.get en fournissant l'url
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) { //en cas de réponse
                        try{
                            if (response.length() > 0){ //si la réponse est correcte
                                lyrics = response.getString("lyrics"); //on récupère les paroles de la chanson
                                goToLyricsView(); //passage au visionnage des paroles
                            }
                            else //la musique n'a pas été trouvée
                                setError();
                        }catch (Exception e){
                            Log.e("Exception", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { //la musique n'a pas été trouvée
                        setError();
                        Log.e("Volley", error.toString());
                    }
                }
            );
            requestQueue.add(arrReq);
        }
        else{
            if(!artistNameisValid)
                artistName.setError("Champ obligatoire");
            if(!songNameisValid)
                songName.setError("Champ obligatoire");
        }
    }

    private void goToLyricsView(){ //passage vers la seconde activité en lui passant les infos nécessaires
        final Intent i = new Intent(this, SongActivity.class);
        i.putExtra("lyrics", lyrics);
        i.putExtra("artistName", artistName.getText().toString());
        i.putExtra("songName", songName.getText().toString());
        startActivity(i);
    }

    private void setError(){
        View contextView = findViewById(R.id.snackBarLayout);
        Snackbar.make(contextView, "aucune chanson trouvée", Snackbar.LENGTH_SHORT).show(); //petit message sous forme de Snackbar Android
    }
}
