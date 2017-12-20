package com.ivy;

/**
 * Created by Singsys-0105 on 12/22/2015.
 */

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.globalclasses.CallBackListenar;
import com.globalclasses.GlobalMethod;
import com.globalclasses.Urls;
import com.globalclasses.AsyncTaskApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Setting alll country
 *
 * **/
public class AddCountryList extends Activity implements View.OnClickListener,CallBackListenar {
    public static final int WAITING_DIALOG = 22;
    ProgressDialog dialog;
    String id = "";
    SharedPreferences pref;
    ArrayList<JSONObject> extrafeature;
    private TextView no_record;
    ListView listView_extra_features;
    CustomList adapter;
    String selectedExtraFeature[];
    ArrayList<String> dataCountryList = new ArrayList<String>();
    ArrayList<String> datcountrycode = new ArrayList<>();
    ArrayList<String> dataID = new ArrayList<String>();
    Bundle params;

    SparseBooleanArray checked;
    // Button button_Add_Features;
    ImageView  edit_btn;
    ImageButton backBtn;
    String totalId = "";
    TextView titletextview;
    String country = "", country_id = "";
    LinearLayout linear_search;
    EditText searchEditText;
    ObjectAnimator animation;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_country_list);
        no_record = (TextView) findViewById(R.id.no_record);
        listView_extra_features = (ListView) findViewById(R.id.listView_extra_features);
        listView_extra_features.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        backBtn = (ImageButton) findViewById(R.id.backBtn);
        titletextview = (TextView) findViewById(R.id.headerText);
        GlobalMethod.AcaslonProSemiBoldTextView(AddCountryList.this, titletextview);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalMethod.hideSoftKeyboard(AddCountryList.this);
                finish();
            }
        });
        linear_search = (LinearLayout) findViewById(R.id.linear_search);
        linear_search.setVisibility(View.VISIBLE);
        params = new Bundle();
        searchEditText = (EditText) findViewById(R.id.searchEditText);

            titletextview.setText("Select Country Code");
            searchEditText.setHint("Search Country");


        if (SimpleHttpConnection.isNetworkAvailable(AddCountryList.this))
        {
            try {
                params.putString("mode","");
                new AsyncTaskApp(this, AddCountryList.this, Urls.country_list, "Add_Country").execute(params);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        else {
            GlobalMethod.showToast(AddCountryList.this, "Check Your Internet Connection.");
        }


        toSearchCountry();
        view = findViewById(android.R.id.content);
    }

    void toSearchCountry() {
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if ((adapter != null) && (arg0 != null)) {
                    adapter.getFilter().filter(arg0);
                } else {
                    if (adapter != null)
                        adapter.notifyDataSetChanged(dataCountryList, 1);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
    void Set_Result(String result) {

        GlobalMethod.write("============result+2" + result);
        try {
            JSONObject jobj = new JSONObject(result);
            String res = jobj.getString("success");
            JSONArray data = null;
            if (res.equalsIgnoreCase("1")) {
                data = jobj.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    if (data.getJSONObject(i).has("country"))
                        dataCountryList.add(data.getJSONObject(i).getString("country"));
                    datcountrycode.add(data.getJSONObject(i)
                            .getString("country_code"));
                }

            } else if (res.equalsIgnoreCase("0")) {

            }
            adapter = new CustomList(datcountrycode,dataCountryList);
            listView_extra_features.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void callBackFunction(String result, String action) throws JSONException {
        Set_Result(result);
    }

class CustomList extends BaseAdapter {
    ArrayList<String> arrInteernal = new ArrayList<String>();
    ArrayList<String> country_internal = new ArrayList<String>();
    ArrayList<String> country_code = new ArrayList<String>();
    ArrayList<String> arrID = new ArrayList<String>();
    ItemFilter itemFilter = new ItemFilter();
    int id=0;

    public CustomList(ArrayList<String> dataCountryList, ArrayList<String> arr) {
        this.arrInteernal = arr;
        this.country_internal = arr;
        this.country_code=dataCountryList;
    }

    public void notifyDataSetChanged(ArrayList<String> UsersArray, int userId) {
        this.arrInteernal = UsersArray;
        this.id=userId;
        super.notifyDataSetChanged();
    }

    public Filter getFilter() {
        // TODO Auto-generated method stub
        return itemFilter;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.arrInteernal.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(AddCountryList.this,R.layout.inflate_country_list, null);
            holder = new ViewHolder();
            holder.countryname = (TextView) convertView
                    .findViewById(R.id.countryname);
            holder.countryline = (LinearLayout) convertView
                    .findViewById(R.id.countryline);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            if (id==1)
            {
                holder.countryname.setText(arrInteernal.get(position));
            }
            else
            {
                holder.countryname.setText(arrInteernal.get(position)+" ("+(country_code.get(position)+")"));
            }

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    try {
                        GlobalMethod.hideSoftKeyboard(AddCountryList.this);
                        String stringData=holder.countryname.getText().toString();
                        String result = stringData.substring(stringData.lastIndexOf("(") + 1);
                        result= result.replace(")","");

                        GlobalMethod.write("----stringData"+stringData);
                        Intent intent = new Intent();
                        intent.putExtra("country_code",result);
                        setResult(RESULT_OK, intent);
                        finish();


//                        notifyDataSetChanged();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return convertView;
    }

    private class ItemFilter extends Filter {

        ArrayList<String> state_internal = new ArrayList<String>();

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub
            String filterString = constraint.toString().toLowerCase();
            FilterResults filterResult = new FilterResults();
            ArrayList<String> orignalCountry = dataCountryList;
            ArrayList<String> originalcountrycode = datcountrycode;
            ArrayList<String> country_internal = new ArrayList<String>(orignalCountry.size());
            ArrayList<String> code_internal = new ArrayList<String>(originalcountrycode.size());
            state_internal.clear();
            for (int i = 0; i < orignalCountry.size(); i++) {
                try {
                    if (orignalCountry.get(i).toLowerCase().contains(filterString)) {
                        country_internal.add(orignalCountry.get(i)+" ("+(originalcountrycode.get(i)+")"));
                    } else {

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            filterResult.values = country_internal;
            filterResult.count = country_internal.size();
            return filterResult;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub
            arrInteernal = (ArrayList<String>) results.values;
            GlobalMethod.write("=====arrInteernal"+arrInteernal+"---"+country_code);

            if (arrInteernal.size()>0)
            {
                no_record.setVisibility(View.GONE);
                listView_extra_features.setVisibility(View.VISIBLE);
            }
            else
            {
                no_record.setVisibility(View.VISIBLE);
                listView_extra_features.setVisibility(View.GONE);
            }
            notifyDataSetChanged(arrInteernal, 1);
        }
    }
}

class ViewHolder {
    TextView countryname;
    LinearLayout countryline;
}

}
