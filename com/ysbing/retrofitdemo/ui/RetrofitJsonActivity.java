package com.ysbing.retrofitdemo.ui;

import java.util.List;

import com.ysbing.retrofit.demo.adapter.MyAdapter;
import com.ysbing.retrofitdemo.api.APIService;
import com.ysbing.retrofitdemo.bean.Cook;
import com.ysbing.retrofitdemo.bean.Tngou;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class RetrofitJsonActivity extends Activity implements Callback<Tngou> {
 
     private ListView lv;
 
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_retrofit_json);
 
         Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.tngou.net")
                 .addConverterFactory(GsonConverterFactory.create()).build();
         APIService service = retrofit.create(APIService.class);
         Call<Tngou> call = service.getList("cook",0, 1, 20);
         call.enqueue(this);
         lv = (ListView) findViewById(R.id.json_lv);
 
     }
 
//     @Override
//     public void onResponse(Call<Tngou> call, Response<Tngou> response) {
//         List<Cook> list = response.body().getList();
//         lv.setAdapter(new MyAdapter(this,list));
//     }
// 
//     @Override
//     public void onFailure(Call<Tngou> call, Throwable t) {
// 
//     }

	@Override
	public void onFailure(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResponse(Response<Tngou> response, Retrofit arg1) {
		// TODO Auto-generated method stub
		   List<Cook> list = response.body().getList();
         lv.setAdapter(new MyAdapter(this,list));
		
	}
 }
