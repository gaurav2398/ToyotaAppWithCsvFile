package impel.imhealthy.adminapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import impel.imhealthy.adminapp.Adapter.DoctorListAdapter;
import impel.imhealthy.adminapp.Adapter.FirstListAdapter;
import impel.imhealthy.adminapp.Model.FirstModel;
import impel.imhealthy.adminapp.Model.ToyotaModel;

public class FirstActivity extends AppCompatActivity {

    RecyclerView chipsrecycler;
    FirstListAdapter chipsAdpater;
    private List<FirstModel> list = new ArrayList<>();

    DatabaseHelperNew mydb;
    Button btnaddcsv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mydb = new DatabaseHelperNew(this);

        chipsrecycler = findViewById(R.id.expenserecycler);
        chipsrecycler.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),2);

        chipsrecycler.setLayoutManager(mLayoutManager);
        chipsrecycler.setItemAnimator(new DefaultItemAnimator());
        addMenuItemsFromJson();

        chipsAdpater=new FirstListAdapter(getApplicationContext(),list);
        chipsrecycler.setAdapter(chipsAdpater);

        btnaddcsv = findViewById(R.id.btnaddcsv);
        btnaddcsv.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                try {
                    String csvfileString = "/storage/emulated/0/Download/exceltoyota.csv";
                    File csvfile = new File(csvfileString);

                    CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
                    Log.d("myEntries", reader.readNext().toString());
                    Log.d("myEntries", reader.getLinesRead() + "");
                    Log.d("myEntries", reader.getRecordsRead() + "");

                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        // nextLine[] is an array of values from the line
                        Log.d("data", nextLine[0].toString());

                        boolean isInserted = mydb.inertData(nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5],
                                nextLine[6], nextLine[7], nextLine[8], nextLine[9], nextLine[10], nextLine[11],
                                nextLine[12], nextLine[13], nextLine[14], nextLine[15]);

                        if (isInserted) {
                            Log.d("inserted data",  nextLine[0] + " " + nextLine[1]);
                        } else {
                            Toast.makeText(FirstActivity.this, "Data not inserted", Toast.LENGTH_SHORT).show();
                        }
                        System.out.println(nextLine[0] + nextLine[1] + "etc...");
                    }
                } catch (IOException e) {
                    Log.d("IOException", e.toString());
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/*");
                startActivityForResult(Intent.createChooser(intent, "Open CSV"), 1);
            }
        });

    }
    private void addMenuItemsFromJson() {
        try {
            String jsonDataString = readJsonDataFromFile();
            JSONArray menuItemsJsonArray = new JSONArray(jsonDataString);

            for (int i = 0; i < menuItemsJsonArray.length(); ++i) {
                FirstModel model = new FirstModel();

                JSONObject jsonObject = menuItemsJsonArray.getJSONObject(i);

                model.setName(jsonObject.getString("name"));
                model.setImage(jsonObject.getString("image"));

                list.add(model);
                chipsAdpater = new FirstListAdapter(getApplicationContext(), list);      //ds=model       d=Model
                chipsrecycler.setAdapter(chipsAdpater);
            }
        } catch (IOException | JSONException exception) {
            Log.e(FirstActivity.class.getName(), "Unable to parse JSON file.", exception);
        }
    }

    private String readJsonDataFromFile() throws IOException {

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString = null;
            inputStream = getResources().openRawResource(R.raw.menu_item);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return new String(builder);
    }

}
