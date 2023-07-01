package impel.imhealthy.adminapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.facebook.share.widget.ShareDialog;
import com.opencsv.CSVReader;

import org.apache.poi.ss.formula.functions.T;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import impel.imhealthy.adminapp.Adapter.DayAdapter;
import impel.imhealthy.adminapp.Adapter.StudentListAdapter;
import impel.imhealthy.adminapp.Model.DaySortingModel;
import impel.imhealthy.adminapp.Model.ExpenseModel;
import impel.imhealthy.adminapp.Model.ToyotaModel;
import impel.imhealthy.adminapp.Model.ToyotaNameAmountModel;
import impel.imhealthy.adminapp.Utilities.SessionManager;

import static android.graphics.Color.parseColor;

public class StudentListActivity extends AppCompatActivity// implements StudentListAdapter.OnItemClickListener
{

    private RecyclerView mRecyclerView;
    StudentListAdapter studentListAdapter;
    DatabaseHelperNew mydb;
    ImageView back;
    private List<ToyotaModel> list = new ArrayList<>();
    private List<ToyotaNameAmountModel> sortedlist = new ArrayList<>();
    String id, name, amount, date;
    int positions;
    Button btnaddmore, save, btnback;

    SessionManager session;
    android.widget.TextView dno, tvset_as_default, dialogtitle, dname, tvheading;
    String sendername = "data", senderid;
    EditText names, amounts, dates, add_comment;
    Button post, btnaddmultiple;
    private static final int PERMISSION_REQUEST = 101;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    final int CONTACT_PICK_REQUEST = 1000;
    String filepath = "";
    Button btnaddcsv;
    TextClock clock;
    private SearchView searchView;
    String gid, gname = "null", coid = "null", ciid = "null", Date;
    Spinner spnmodal, spnsuffix, city;
    ArrayList<String> gendername = new ArrayList<String>();
    ArrayList<String> suffixlist = new ArrayList<String>();

    public static TextView individualtotal;
    public static TextView companytotal;
    String modelname;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_list);

        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> users = session.getUserDetails();

        tvheading = findViewById(R.id.tvheading);
        individualtotal = findViewById(R.id.individualtotal);
        companytotal = findViewById(R.id.companytotal);

        searchView = findViewById(R.id.searchview);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (studentListAdapter == null) {
                    return false;
                } else {
                    studentListAdapter.getFilter().filter(newText);
                    return true;
                }
            }
        });

        back = findViewById(R.id.backarrow);
        back.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                onBackPressed();
            }
        });

        mydb = new DatabaseHelperNew(this);
        list.addAll(mydb.homedeletedata());

        mRecyclerView = findViewById(R.id.expenserecycler);
        mRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

//        studentListAdapter.setOnItemClickListener(StudentListActivity.this);

        ActivityCompat.requestPermissions(StudentListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS},
                MY_PERMISSIONS_REQUEST_SEND_SMS);

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
                        Log.d("data", sendername + " " + nextLine[0] + " " + nextLine[1] + " " + sendername);

                        if (isInserted) {
                            //               Toast.makeText(StudentListActivity.this, "Data inserted", Toast.LENGTH_SHORT).show();
                            Log.d("inserted data", sendername + " " + nextLine[0] + " " + nextLine[1] + " " + sendername);
                        } else {
                            Toast.makeText(StudentListActivity.this, "Data not inserted", Toast.LENGTH_SHORT).show();
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

        spnmodal = findViewById(R.id.spnmodal);
        ArrayList<String> ArrList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            ArrList.add(list.get(i).getMODEL());
        }
        HashSet<String> hset = new HashSet<String>(ArrList);
        gendername.clear();
        gendername.add("Select Model");
        gendername.addAll(hset);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StudentListActivity.this, android.R.layout.simple_list_item_1, gendername);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnmodal.setAdapter(adapter);
        spnmodal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!gendername.get(position).equals("Select Model")) {
                    Log.d("gendername", gendername.get(position));
                    modelname = gendername.get(position);

                    ArrayList<String> ArrList = new ArrayList<String>();
                    for (int i = 0; i < list.size(); i++) {
                        if (gendername.get(position).equals(list.get(i).getMODEL()))
                            ArrList.add(list.get(i).getSUFFIX());
                    }
                    suffixlist.clear();
                    HashSet<String> hset = new HashSet<String>(ArrList);
                    suffixlist.add("Select Suffix");
                    suffixlist.addAll(hset);
                    spnsuffix.setSelection(0);

                    sortedlist.clear();
                    studentListAdapter = new StudentListAdapter(getApplicationContext(), sortedlist);      //ds=model       d=Model
                    mRecyclerView.setAdapter(studentListAdapter);
                    individualtotal.setText("0");
                    companytotal.setText("0");

                } else {
                    Toast.makeText(StudentListActivity.this, "Select Car Model", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnsuffix = findViewById(R.id.spnsuffix);

        modelname = getIntent().getStringExtra("model");
        tvheading.setText(modelname);
        Log.d("modelname1",modelname+"");
        ArrayList<String> ArrList1 = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            if (modelname.equals(list.get(i).getMODEL()))
                ArrList1.add(list.get(i).getSUFFIX());
        }
        suffixlist.clear();
        HashSet<String> hset1 = new HashSet<String>(ArrList1);
        suffixlist.add("Select Suffix");
        suffixlist.addAll(hset1);
        spnsuffix.setSelection(0);

        sortedlist.clear();
        studentListAdapter = new StudentListAdapter(getApplicationContext(), sortedlist);      //ds=model       d=Model
        mRecyclerView.setAdapter(studentListAdapter);
        individualtotal.setText("0");
        companytotal.setText("0");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(StudentListActivity.this, android.R.layout.simple_list_item_1, suffixlist);
        adapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnsuffix.setAdapter(adapter1);
        spnsuffix.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!suffixlist.get(position).equals("Select Suffix")) {
                    Log.d("suffix", suffixlist.get(position));
                    sortedlist.clear();
                    for (int i = 0; i < list.size(); i++) {
                        if (modelname.equals(list.get(i).getMODEL()) && suffixlist.get(position).equals(list.get(i).getSUFFIX())) {
                            sortedlist.clear();
                            Log.d("ListData1", list.get(i).getMODEL());
                            Log.d("ListData2", list.get(i).getSUFFIX());
                            Log.d("ListData3", list.get(i).toString());
                            ToyotaNameAmountModel toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Ex-Showroom Price");
                            toyotaNameAmountModel.setAmount(list.get(i).getEXSHOWROOM());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Registration Individual");
                            toyotaNameAmountModel.setAmount(list.get(i).getIndividual());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Registration Taxi/Company");
                            toyotaNameAmountModel.setAmount(list.get(i).getTaxicompany());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Insurance");
                            toyotaNameAmountModel.setAmount(list.get(i).getInsurance());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("TCS 1.66%");
                            toyotaNameAmountModel.setAmount(list.get(i).getTcs());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Depo Charges");
                            toyotaNameAmountModel.setAmount(list.get(i).getDepoCharges());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Number Plate");
                            toyotaNameAmountModel.setAmount(list.get(i).getNumberPlate());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Fastag");
                            toyotaNameAmountModel.setAmount(list.get(i).getFastag());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Accessories");
                            toyotaNameAmountModel.setAmount(list.get(i).getAccessories());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Car Kit");
                            toyotaNameAmountModel.setAmount(list.get(i).getCarkit());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Gears LKM");
                            toyotaNameAmountModel.setAmount(list.get(i).getGearslkm());
                            sortedlist.add(toyotaNameAmountModel);
                            toyotaNameAmountModel = new ToyotaNameAmountModel();
                            toyotaNameAmountModel.setName("Toyota Smiles");
                            toyotaNameAmountModel.setAmount(list.get(i).getTyotasmiles());
                            sortedlist.add(toyotaNameAmountModel);
                            studentListAdapter = new StudentListAdapter(getApplicationContext(), sortedlist);      //ds=model       d=Model
                            mRecyclerView.setAdapter(studentListAdapter);

//                            individualtotal.setText(list.get(i).getOnroadindividual());
//                            companytotal.setText(list.get(i).getOnroadcompany());
                        }
                    }

                    Log.d("SIZE OF ARRAY", String.valueOf(list.size()));
                    Log.d("SIZE OF ARRAY", mydb.homedeletedata() + "");


                } else {
                    Toast.makeText(StudentListActivity.this, "Select Suffix", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (requestCode == 1) {
                    }
                }
                break;

        }
    }
}