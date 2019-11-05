package ir.adaktech.yaghoob;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UsersAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<User> Users;

    public UsersAdapter(Context context, ArrayList<User> Users) {
        this.mContext = context;
        this.Users = Users;
    }

    @Override
    public int getCount() {
        return Users.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    // 5
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final User user = Users.get(position);

        // view holder pattern
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.gridview_item, null);

            final ImageView IV_avatar = (ImageView)convertView.findViewById(R.id.item_avatar);
            final ImageView IV_more = (ImageView)convertView.findViewById(R.id.item_moremenu);
            final TextView TV_id = (TextView)convertView.findViewById(R.id.item_id);
            final TextView TV_email = (TextView)convertView.findViewById(R.id.item_email);
            final TextView TV_firstName = (TextView)convertView.findViewById(R.id.item_firstName);
            final TextView TV_lastName = (TextView)convertView.findViewById(R.id.item_lastName);
            final TextView TV_job = (TextView)convertView.findViewById(R.id.item_job);

            final ViewHolder viewHolder = new ViewHolder(IV_avatar,IV_more,TV_id, TV_email, TV_firstName, TV_lastName,TV_job);
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        Picasso.with(mContext).load(user.getAvatar()).placeholder(R.mipmap.placeholder).into(viewHolder.VH_avatar);
        viewHolder.VH_id.setText("User " + user.getId());
        viewHolder.VH_email.setText(user.getEmail());
        viewHolder.VH_firstName.setText(user.getFirst_Name());
        viewHolder.VH_lastName.setText(user.getLast_Name());
        viewHolder.VH_job.setText(user.getJob());
        viewHolder.VH_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, view);
                //inflating menu from xml resource
                popup.getMenuInflater().inflate(R.menu.user_item_menu, popup.getMenu());
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_view:
                                //handle menu_edit click
                                ShowUser(user,position);
                                break;
                            case R.id.menu_edit:
                                //handle menu_edit click
                                ShowEditDialog(user,position);
                                break;
                            case R.id.menu_delete:
                                //handle menu_delete click
                                ShowDeleteConfirm(user,position);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ShowUser(user,position);
                return false;
            }
        });

        return convertView;

    }
    // Your "view holder" that holds references to each subview
    private class ViewHolder {
        private final TextView VH_id;
        private final TextView VH_email;
        private final TextView VH_firstName;
        private final TextView VH_lastName;
        private final TextView VH_job;
        private final ImageView VH_avatar;
        private final ImageView VH_more;

        public ViewHolder(ImageView avatar,ImageView more, TextView id,TextView email, TextView firstName, TextView lastName, TextView job) {
            this.VH_id = id;
            this.VH_email = email;
            this.VH_firstName = firstName;
            this.VH_lastName = lastName;
            this.VH_job = job;
            this.VH_avatar = avatar;
            this.VH_more = more;
        }
    }



    public void ShowUser(final User user,final int position)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("View User "+ user.getId());
        alertDialog.setMessage("User Information:\n"+user.getFirst_Name() + " " + user.getLast_Name() +"\nEmail: "+user.getEmail()+"\nUser ID: "+user.getId());
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }



    public void ShowEditDialog(final User user,final int position)
    {
        LayoutInflater inflater = (LayoutInflater)  mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_edit, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Edit User "+ user.getId());
        alertDialog.setCancelable(false);
        final EditText name = (EditText) view.findViewById(R.id.dialog_name);
        final EditText job = (EditText) view.findViewById(R.id.dialog_job);
        name.setText(user.getFirst_Name());
        job.setText(user.getJob());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Not a good method but is Greedy! for developing One-Day Project! for Mr.Seifzadeh! :D
                if(name.getText().toString().length()>0 && job.getText().toString().length()>0)
                    new API_Update_User().execute("https://reqres.in/api/users/"+user.getId(),name.getText().toString(),job.getText().toString(),String.valueOf(position));
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

    public void ShowDeleteConfirm(final User user,final int position)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Delete Confirmation");
        alertDialog.setMessage("Are you sure for delete the user " + user.getFirst_Name()+ " " + user.getLast_Name()+ "?");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new API_Delete_User().execute("https://reqres.in/api/users/"+user.getId(),String.valueOf(position));
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();                                    }
        });
        alertDialog.show();
    }


    public class API_Update_User extends AsyncTask<String, Void, Void> {
        public final MediaType JSONtype = MediaType.parse("application/json; charset=utf-8");
        String data="";
        String fname="";
        String job="";
        int position=-1;

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
            position= Integer.parseInt(params[3]);
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

                Request req = new Request.Builder().url(params[0]).put(body).build();
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
                //actually i must use updatedAt property for best validating
                Users.get(position).setFirst_Name(fname);
                Users.get(position).setJob(job);
                notifyDataSetChanged();
                Toast.makeText(mContext,"Update Successfully!",Toast.LENGTH_SHORT).show();
            }
            else if(data.contains("ERROR"))
            {
                Toast.makeText(mContext,"Error :( " + data,Toast.LENGTH_SHORT).show();
            }

        }

    }




    public class API_Delete_User extends AsyncTask<String, Void, Void> {
        public final MediaType JSONtype = MediaType.parse("application/json; charset=utf-8");
        int StatusCode=-1;
        int position=-1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.i("4Yaghoob>>>>","Async Task >> Request Delete User step1!!");
            position= Integer.parseInt(params[1]);
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

                Request req = new Request.Builder().url(params[0]).delete().build();
                StatusCode=httpClient.newCall(req).execute().code();

            }
            catch (IOException e) {
                Log.i("4Yaghoob IOException",e.toString());
                StatusCode=500;
            }
            catch (NetworkOnMainThreadException e)
            {
                Log.i("4Yaghoob NetException",e.toString());
                StatusCode=500;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.i("4Yaghoob>","Delete User Response : "+StatusCode);
            if(StatusCode==204) {
                Users.remove(position);
                notifyDataSetChanged();
                Toast.makeText(mContext,"Delete Successfully!",Toast.LENGTH_SHORT).show();

            }else if(StatusCode==500)
            {
                Toast.makeText(mContext,"Error :( " ,Toast.LENGTH_SHORT).show();
            }
        }

    }



    }












