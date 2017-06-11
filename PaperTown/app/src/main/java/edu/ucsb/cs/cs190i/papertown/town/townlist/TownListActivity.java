/*
 *  Copyright (c) 2017 - present, Zhenyu Yang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 */

package edu.ucsb.cs.cs190i.papertown.town.townlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ucsb.cs.cs190i.papertown.ListTownAdapter;
import edu.ucsb.cs.cs190i.papertown.R;
import edu.ucsb.cs.cs190i.papertown.RecyclerItemClickListener;
import edu.ucsb.cs.cs190i.papertown.geo.GeoActivity;
import edu.ucsb.cs.cs190i.papertown.models.Town;
import edu.ucsb.cs.cs190i.papertown.models.TownBuilder;
import edu.ucsb.cs.cs190i.papertown.splash.SplashScreenActivity;
import edu.ucsb.cs.cs190i.papertown.town.newtown.NewTownActivity;
import edu.ucsb.cs.cs190i.papertown.town.towndetail.TownDetailActivity;

import static edu.ucsb.cs.cs190i.papertown.application.AppConstants.CRED;
import static edu.ucsb.cs.cs190i.papertown.application.AppConstants.EMAIL;
import static edu.ucsb.cs.cs190i.papertown.application.AppConstants.PREF_NAME;
import static edu.ucsb.cs.cs190i.papertown.application.AppConstants.TOKEN;
import static edu.ucsb.cs.cs190i.papertown.application.AppConstants.TOKEN_TIME;
import static edu.ucsb.cs.cs190i.papertown.application.AppConstants.USERID;

public class TownListActivity extends AppCompatActivity {
    public List<Town> allTowns = new ArrayList<>();
    private GridView imageGrid;
    private ArrayList<Bitmap> bitmapList;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_town_list);


        //============   get all towns   ==========


        DatabaseReference townsDatabase;
        townsDatabase = FirebaseDatabase.getInstance().getReference().child("towns");
        townsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allTowns.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Town t = ds.getValue(Town.class);
                    allTowns.add(t);
                    printTown(t);
                }

                if (allTowns != null && allTowns.size() > 0) {
                    Collections.reverse(allTowns);
                    ListTownAdapter mAdapter = new ListTownAdapter(allTowns);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //=================   end of get all towns    =============


        //towns = (List<Town>)getIntent().getSerializableExtra("townArrayList");

        mRecyclerView = (RecyclerView) findViewById(R.id.list_town);
        //mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //initData();

//    if(allTowns!=null&&allTowns.size()>0) {
//      ListTownAdapter mAdapter = new ListTownAdapter(allTowns);
//      mRecyclerView.setAdapter(mAdapter);
//    }

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.i("RecyclerItemClr", "onItemClick");

                        Intent intent = new Intent(getApplicationContext(), TownDetailActivity.class);
                        intent.putExtra("town", allTowns.get(position));
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.i("RecyclerItemClr", "onLongItemClick");
                    }
                })
        );


        //
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.add_town:
                        Intent newTownIntent = new Intent(TownListActivity.this, NewTownActivity.class);
                        startActivity(newTownIntent);
                        break;
                    case R.id.geo_view:
                        Intent geoTownIntent = new Intent(TownListActivity.this, GeoActivity.class);
                        startActivity(geoTownIntent);
                        break;
                    case R.id.action_settings:
                        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                        editor.remove(TOKEN);
                        editor.remove(TOKEN_TIME);
                        editor.remove(USERID);
                        editor.remove(EMAIL);
                        editor.remove(CRED);
                        editor.commit();
                        Intent splashIntent = new Intent(TownListActivity.this, SplashScreenActivity.class);
                        startActivity(splashIntent);
                        finish();
                        break;
                }
                return true;
            }
        });
    }


    void printTown(Town town) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String result = gson.toJson(town);
        Log.d("TAG", result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        final SearchView searchView = (SearchView) findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.i("onQueryTextSubmit", "query = " + query);
//        if( ! searchView.isIconified()) {
//          searchView.setIconified(true);
//        }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                List<Town> searchResults = new ArrayList<Town>();
                for (int i = 0; i < allTowns.size(); i++) {
                    if (allTowns.get(i).getTitle().toLowerCase().contains(s.toLowerCase())) {
                        //Log.i("onQueryTextChange", "found = "+allTowns.get(i).getTitle());
                        searchResults.add(allTowns.get(i));
                    }
                }
                ListTownAdapter mAdapter = new ListTownAdapter(searchResults);
                mRecyclerView.setAdapter(mAdapter);

                return false;
            }
        });


        return true;
    }

    private void initData() {


        //if not town list is passed in, create default list of towns
        if (allTowns.size() == 0) {
            allTowns = new ArrayList<>();

            List<String> imgs1 = new ArrayList<>();
            imgs1.add("https://s-media-cache-ak0.pinimg.com/564x/58/82/11/588211a82d4c688041ed5bf239c48715.jpg");

            List<String> imgs2 = new ArrayList<>();
            imgs2.add("https://s-media-cache-ak0.pinimg.com/564x/5f/d1/3b/5fd13bce0d12da1b7480b81555875c01.jpg");

            List<String> imgs3 = new ArrayList<>();
            imgs3.add("http://68.media.tumblr.com/132947bb8b5319f81f8a77d3c83b3fbf/tumblr_o1z5pav4xH1s49orpo1_1280.jpg");


            Town t1 = new TownBuilder()
                    .setTitle("Mother Susanna Monument")
                    .setCategory("Place")
                    //.setDescription("Discription here. ipsum dolor sit amet, consectetur adipisicing elit")
                    .setAddress("6510 El Colegio Rd Apt 1223")
                    .setLat(35.594559f)
                    .setLng(-117.899149f)
                    .setUserId("theUniqueEye")
                    .setImages(imgs1)
                    .setSketch("")
                    .build();

            Town t2 = new TownBuilder()
                    .setTitle("Father Crowley Monument")
                    .setCategory("Place")
                    //.setDescription("Discription here. ipsum dolor sit amet, consectetur adipisicing elit")
                    .setAddress("6510 El Colegio Rd Apt 1223")
                    .setLat(35.594559f)
                    .setLng(-117.899149f)
                    .setUserId("theUniqueEye")
                    .setImages(imgs2)
                    .setSketch("")
                    .build();

            Town t3 = new TownBuilder()
                    .setTitle("Wonder Land")
                    .setCategory("Creature")
                    //.setDescription("Discription here. ipsum dolor sit amet, consectetur adipisicing elit")
                    .setAddress("Rabbit Hole 1901C")
                    .setLat(35.594559f)
                    .setLng(-117.899149f)
                    .setUserId("Sams to Go")
                    .setImages(imgs3)
                    .setSketch("")
                    .build();

            allTowns.add(t1);
            allTowns.add(t2);
            allTowns.add(t3);
            allTowns.add(t1);
            allTowns.add(t2);
            allTowns.add(t3);
            allTowns.add(t1);
            allTowns.add(t2);
            allTowns.add(t3);
        }
    }
}
