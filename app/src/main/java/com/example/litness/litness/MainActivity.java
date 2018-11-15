package com.example.litness.litness;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.litness.litness.Adapter.BarCardAdapter;
import com.example.litness.litness.Adapter.FilterAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private RecyclerView rvCards;
    private BarCardAdapter adapter;

    private SwipeRefreshLayout swipeContainer;

    List<String> activeFilters = new ArrayList<>();
    TextView tvNoBars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);


        rvCards = findViewById(R.id.main_card_rv);
        adapter = new BarCardAdapter(this);
        rvCards.setAdapter(adapter);

        activeFilters.add("All Bars");
        updateFilters();
        LinearLayout ll = findViewById(R.id.main_container_filters);
        for(String s : getResources().getStringArray(R.array.filter_options)){
            int layout, hiddenLayout;
            if(s.equals("All Bars")){
                layout = R.layout.adapter_filter_on;
                hiddenLayout = R.layout.adapter_filter_off;
            }
            else{
                layout = R.layout.adapter_filter_off;
                hiddenLayout = R.layout.adapter_filter_on;
            }
            View v = getLayoutInflater().inflate(layout, null, false);
            View w = getLayoutInflater().inflate(hiddenLayout, null, false);

            ((TextView) v.findViewById(R.id.adapter_alt_filter)).setText(s);
            ((TextView) w.findViewById(R.id.adapter_alt_filter)).setText(s);
            w.findViewById(R.id.adapter_container).setVisibility(View.GONE);
            v.findViewById(R.id.adapter_container).setOnClickListener(x->{ // OFF to ON
                v.findViewById(R.id.adapter_container).setVisibility(View.GONE);
                w.findViewById(R.id.adapter_container).setVisibility(View.VISIBLE);
                if(s.equals("All Bars")) activeFilters.remove(s);
                else activeFilters.add(s);
                updateFilters();

            });
            w.findViewById(R.id.adapter_container).setOnClickListener(x->{
                w.findViewById(R.id.adapter_container).setVisibility(View.GONE);
                v.findViewById(R.id.adapter_container).setVisibility(View.VISIBLE);
                if(s.equals("All Bars")) activeFilters.add(s);
                else activeFilters.remove(s);
                updateFilters();
            });
            ll.addView(v);
            ll.addView(w);
        }

        swipeContainer = findViewById(R.id.main_sr);
        swipeContainer.setOnRefreshListener(this::actionSwipeRefresh);

        tvNoBars = findViewById(R.id.main_alt_nobars);
    }

    public void updateFilters(){
        Set<String> bs = Client.barMap.keySet();
        List<Bar> filtered = new ArrayList<>();
        for(String s : bs){
            Bar b = Client.barMap.get(s);
            for(String f : activeFilters){
                if(b.tags.contains(f) && !filtered.contains(b))
                    filtered.add(b);
            }
        }
        adapter.updateBars(filtered);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.input_filter);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);

        //fixes all the colors for searching with the toolbar
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.PrimaryLight));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.PrimaryLight));

        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.ic_search_24dp);
        ImageView closeIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeIcon.setImageResource(R.drawable.ic_close_primary_24dp);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //updateFilters(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //updateFilters(newText);
                return false;
            }
        });
        searchView.setOnCloseListener( () -> {
            //updateFilters("");
            return false;
        });
        return true;
    }

    private void actionSwipeRefresh() {
        swipeContainer.setRefreshing(true); {
            updateFilters();
        }
        if (swipeContainer.isRefreshing())
            swipeContainer.setRefreshing(false);
    }

    private List<Bar> applyFilter(Collection<Bar> nonFilteredBarMap) {
        List<Bar> barList = new ArrayList<>(nonFilteredBarMap);
        return barList;
    }
/*    Fragment fragList;
    FragmentManager fragDaddy = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        fragList = new BarListFragment();
        fragDaddy.beginTransaction().add(R.id.main_fragframe, fragList, "List").show(fragList).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.input_filter);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);

        //fixes all the colors for searching with the toolbar
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.PrimaryLight));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.PrimaryLight));

        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.ic_search_24dp);
        ImageView closeIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeIcon.setImageResource(R.drawable.ic_close_primary_24dp);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //updateFilters(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //updateFilters(newText);
                return false;
            }
        });
        searchView.setOnCloseListener( () -> {
            //updateFilters("");
            return false;
        });
        return true;
    }*/
}
