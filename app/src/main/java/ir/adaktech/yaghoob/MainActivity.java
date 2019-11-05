package ir.adaktech.yaghoob;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    public ArrayList<User> Users = new ArrayList<User>();
    UsersAdapter Adapter;
    public GridView gridView;
    public int CurrentPage=-1;
    public int TotalPages=-1;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        gridView = (GridView)findViewById(R.id.main_gridview);
        Button BtnMore= findViewById(R.id.main_btn_more);
        BtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentPage<TotalPages)
                    new API_GetUserList().execute("https://reqres.in/api/users?page="+(++CurrentPage));
                else
                {
                    Toast.makeText(getBaseContext(), "No More Data...", Toast.LENGTH_SHORT).show();
                    view.setVisibility(View.GONE);
                }
            }
        });

        FloatingActionButton FAB= findViewById(R.id.main_FAB);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)  mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.dialog_edit, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Add New User ");
                alertDialog.setCancelable(false);
                final EditText name = (EditText) view.findViewById(R.id.dialog_name);
                final EditText job = (EditText) view.findViewById(R.id.dialog_job);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Insert", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Not a good method but is Greedy! for developing One-Day Project! for Mr.Seifzadeh! :D
                        if(name.getText().toString().length()>0 && job.getText().toString().length()>0)
                        new API_Add_User().execute("https://reqres.in/api/users",name.getText().toString(),job.getText().toString());
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();                                    }
                });
                alertDialog.setView(view);
                alertDialog.show();
            }
        });

        //Initial Fetch after load en Start Activity
        new API_GetUserList().execute("https://reqres.in/api/users");
    }






public class API_GetUserList extends AsyncTask<String, Void, Void> {
    String data="";
    Button BtnMore= findViewById(R.id.main_btn_more);
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        BtnMore.setText("Loading...");
        Log.i("4Yaghoob>>>>","Async Task >>  GET User List onPreExecute");
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.i("4Yaghoob>>>>","Async Task >>  GET User List doInBackground");
        data="";
        if(!params[0].startsWith("http://") && !params[0].startsWith("https://")) {
            params[0] = "http://" + params[0];
        }
        Log.i("4Yaghoob>>>>","Async Task >>  URL: "+ params[0]);

        try {
            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            OkHttpClient httpClient = client.newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
            Request req = new Request.Builder().url(params[0]).get().build();
            data = httpClient.newCall(req).execute().body().string();
        }
        catch (IOException e) {
            Log.i("4Yaghoob>>>>> Failed:",e.toString());
            // Toast.makeText(getBaseContext(),"IO ERROR" ,Toast.LENGTH_SHORT).show();
            data="ERROR:CATCH IO ex...";
        }
        catch (NetworkOnMainThreadException e)
        {
            Log.i("4Yaghoob> Failed:",e.toString());
            // Toast.makeText(getBaseContext(),"NETWORK ERROR" ,Toast.LENGTH_SHORT).show();
            data="ERROR:CATCH Network ex...";
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("4Yaghoob>>>>","Async Task >>  GET User List onPostExecute " +data);
        BtnMore.setText("MORE");

        if(data.contains("ERROR"))
        {
            Toast.makeText(mContext,"Error :( " + data,Toast.LENGTH_SHORT).show();
        }
        else
        //Read en Parse REQRES Json!
        try {
            JSONObject job= new JSONObject(data);
            CurrentPage=job.getInt("page");
            TotalPages=job.getInt("total_pages");
            Type listType = new TypeToken<List<User>>(){}.getType();
            ArrayList<User> NewFetchedUsers = new Gson().fromJson(job.getString("data"), listType);
            Users.addAll(NewFetchedUsers);
            Adapter = new UsersAdapter(mContext, Users);
            gridView.setAdapter(Adapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(mContext,"Error :( ",Toast.LENGTH_SHORT).show();
        }

    }
}



    public class API_Add_User extends AsyncTask<String, Void, Void> {
        public final MediaType JSONtype = MediaType.parse("application/json; charset=utf-8");
        String data="";
        String fname="";
        String job="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.i("4Yaghoob>>>>","Async Task >> Request Update User step1!!");
            data="";
            fname=params[1];
            job=params[2];
            if(!params[0].startsWith("http://") && !params[0].startsWith("https://")) {
                params[0] = "http://" + params[0];
            }
            try {
                final OkHttpClient client = new OkHttpClient.Builder()
                        .build();
                OkHttpClient httpClient = client.newBuilder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                RequestBody body;
                JSONObject jsonobj = new JSONObject();
                try {
                    jsonobj.put("name", params[1]);
                    jsonobj.put("job",params[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("4Yaghoob"," JSONtype create ERROR!");
                }

                Log.i("4Yaghoob",jsonobj.toString());
                body = RequestBody.create(JSONtype,jsonobj.toString());

                Request req = new Request.Builder().url(params[0]).post(body).build();
                data = httpClient.newCall(req).execute().body().string();
            }
            catch (IOException e) {
                Log.i("4Yaghoob IOException",e.toString());
                data="ERROR:CATCH IO ex...";
            }
            catch (NetworkOnMainThreadException e)
            {
                Log.i("4Yaghoob NetException",e.toString());
                data="ERROR:CATCH Network ex...";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.i("4Yaghoob>","Update User Response : "+data);
            if(Singelton.isJSONValid(data))
            {
                User addedUser=new User();
                addedUser.setFirst_Name(fname);
                addedUser.setJob(job);
                addedUser.setLast_Name("<blank>");
                addedUser.setEmail("<blank>");
                Users.add(addedUser);
                Adapter.notifyDataSetChanged();
                //actually i must use createdAt property for best validating
                Toast.makeText(mContext,"Add new User Successfully!",Toast.LENGTH_SHORT).show();
            }
            else if(data.contains("ERROR"))
            {
                Toast.makeText(mContext,"Error :( " + data,Toast.LENGTH_SHORT).show();
            }
        }

    }







    private static final long BACK_PRESS_DELAY = 1500;
    private boolean mBackPressCancelled = false;
    private long mBackPressTimestamp;
    private Toast mBackPressToast;
    @Override
    public void onBackPressed() {
        // Do nothing if the back button is disabled.
        if (!mBackPressCancelled) {
            // Pop fragment if the back stack is not empty.
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                super.onBackPressed();
            } else {
                if (mBackPressToast != null) {
                    mBackPressToast.cancel();
                }
                long currentTimestamp = System.currentTimeMillis();
                if (currentTimestamp < mBackPressTimestamp + BACK_PRESS_DELAY) {
                    // super.onBackPressed();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("Are you sure for exit?");
                    builder1.setCancelable(true);
                    builder1.setIcon(R.mipmap.ic_launcher);
                    builder1.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // dialog.cancel();
                                    System.exit(0);
                                }
                            }
                    );
                    builder1.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // mBackPressTimestamp = System.currentTimeMillis();
                                    // mBackPressCancelled=true;
                                    dialog.cancel();
                                }
                            }
                    );

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    // mBackPressCancelled=false;
                    mBackPressTimestamp = currentTimestamp;
                    mBackPressToast = Toast.makeText(this,"For exiting press back again...", Toast.LENGTH_SHORT);
                    mBackPressToast.show();
                }
            }
        }
    }

}





