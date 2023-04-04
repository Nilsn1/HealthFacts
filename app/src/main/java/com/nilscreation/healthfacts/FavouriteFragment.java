package com.nilscreation.healthfacts;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.google.rvadapter.AdmobNativeAdAdapter;
import com.nilscreation.healthfacts.R;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<FactsModel> factslist;
    Context context;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        requestQueue = VolleySingleton.getmInstance(getContext()).getRequestQueue();
        factslist = new ArrayList<>();

        fetchData();

        return view;
    }

    private void fetchData() {
        MyDBHelper myDBHelper = new MyDBHelper(getContext());

        ArrayList<FactsModel> facts = myDBHelper.readData();

        for (int i = 0; i < facts.size(); i++) {

            String title = facts.get(i).title;

            FactsModel factmodel = new FactsModel(title);
            factslist.add(factmodel);

            FavouriteAdapter adapter = new FavouriteAdapter(context, factslist, getActivity());
//                    recyclerView.setAdapter(adapter);
            AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with("ca-app-pub-9137303962163689/3884272678", adapter,
                    "small").adItemInterval(3).build();
            recyclerView.setAdapter(admobNativeAdAdapter);

        }
    }
}