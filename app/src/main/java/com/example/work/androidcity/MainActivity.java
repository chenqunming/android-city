package com.example.work.androidcity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView showTV = null;//显示选择结果
    private Spinner provinceSpinner = null;//省
    private Spinner citySpinner = null;//市
    private Spinner countySpinner = null;//县
    ArrayAdapter<String> provinceAdapter = null;//省适配器
    ArrayAdapter<String> cityAdapter = null;//市适配器
    ArrayAdapter<String> countyAdapter = null;//县适配器
    private ArrayList<String> arrProvinces = new ArrayList<String>();
    private ArrayList<String> arrCitys = new ArrayList<String>();
    private ArrayList<String> arrAreas = new ArrayList<String>();
     // 所有省
    private String[] mProvinceDatas;
    //省-市
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    //市-区
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();

    private JSONObject mJsonObj;
    private static int provincePosition = 4;
    private static int cityPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //加载数据
        initJsonData();
        initDatas();
        //加载视图
        setSpinner();
    }

    private void setSpinner(){

        showTV = (TextView)findViewById(R.id.text01);

        provinceSpinner = (Spinner)findViewById(R.id.spin_province);
        citySpinner = (Spinner)findViewById(R.id.spin_city);
        countySpinner = (Spinner)findViewById(R.id.spin_county);

        provinceAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,mProvinceDatas);
        provinceSpinner.setAdapter(provinceAdapter);
        provinceSpinner.setSelection(4,true);//设置默认选中项

        cityAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,mCitisDatasMap.get(mProvinceDatas[4]));
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setSelection(0,true);  //默认选中第0个

        countyAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, mAreaDatasMap.get((mCitisDatasMap.get(mProvinceDatas[4])[0])));
        countySpinner.setAdapter(countyAdapter);
        countySpinner.setSelection(0, true);

        //省下拉监听
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,mCitisDatasMap.get(mProvinceDatas[position]));
                citySpinner.setAdapter(cityAdapter);
                provincePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //地级下拉监听
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3)
            {
                if(mAreaDatasMap.get((mCitisDatasMap.get(mProvinceDatas[provincePosition])[position])) != null)
                {
                    countySpinner.setVisibility(View.VISIBLE);
                    countyAdapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_item, mAreaDatasMap.get((mCitisDatasMap.get(mProvinceDatas[provincePosition])[position])));
                    countySpinner.setAdapter(countyAdapter);
                    cityPosition = position;
                }else {
                    countySpinner.setVisibility(View.INVISIBLE);
                    showTV.setText(mProvinceDatas[provincePosition]+"-"+mCitisDatasMap.get(mProvinceDatas[provincePosition])[position]);

                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

        //县级下拉监听
        countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showTV.setText(mProvinceDatas[provincePosition]+"-"+mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition]+"-"+mAreaDatasMap.get((mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition]))[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //从文件中读取数据
    private void initJsonData(){
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = MainActivity.this.getClass().getClassLoader().getResourceAsStream("assets/" + "city.json");
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, "utf8"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   // 解析JSon
    private void initDatas(){
        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject jsonP = jsonArray.getJSONObject(i);
                String province = jsonP.getString("p");
                mProvinceDatas[i] = province;
                JSONArray jsonCs = null;
                try{
                    jsonCs = jsonP.getJSONArray("c");
                }catch (Exception e1){
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for(int j = 0;j<jsonCs.length();j++){
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n");
                    mCitiesDatas[j] = city;
                    JSONArray jsonAreas = null;
                    try{
                        jsonAreas = jsonCity.getJSONArray("a");
                    }catch (Exception e){
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];
                    for(int k = 0; k<jsonAreas.length();k++){
                        String area = jsonAreas.getJSONObject(k).getString("s");
                        mAreasDatas[k] = area;
                    }
                    mAreaDatasMap.put(city,mAreasDatas);
                }
                mCitisDatasMap.put(province,mCitiesDatas);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }

}
