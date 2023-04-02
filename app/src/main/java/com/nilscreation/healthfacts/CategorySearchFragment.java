package com.nilscreation.healthfacts;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.rvadapter.AdmobNativeAdAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategorySearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private List<FactsModel> factslist;
    Context context;
    String getCategory = "all";

    public CategorySearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        requestQueue = VolleySingleton.getmInstance(getContext()).getRequestQueue();
        factslist = new ArrayList<>();
        fetchMovies();

        return view;
    }

    private void fetchMovies() {

        String url = "https://raw.githubusercontent.com/NilsCreation/DailyFacts/main/DailyFacts.json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        String poster = jsonObject.getString("poster");
                        String category = jsonObject.getString("category");
                        String title = jsonObject.getString("title");
                        String text = jsonObject.getString("text");

                        FactsModel facts = new FactsModel(title);
                        factslist.add(facts);

//                        if (getCategory.equals("all")) {
//                            FactsModel facts = new FactsModel(poster, category, title, text);
//                            factslist.add(facts);
//                        } else if (getCategory.equals("Animals")) {
//                            if (category.equals("Animals")) {
//                                FactsModel facts = new FactsModel(poster, category, title, text);
//                                factslist.add(facts);
//                            }
//                        } else if (getCategory.equals("Geography")) {
//                            if (category.equals("Geography")) {
//                                FactsModel facts = new FactsModel(poster, category, title, text);
//                                factslist.add(facts);
//                            }
//                        } else if (getCategory.equals("Health")) {
//                            if (category.equals("Health")) {
//                                FactsModel facts = new FactsModel(poster, category, title, text);
//                                factslist.add(facts);
//                            }
//                        } else if (getCategory.equals("History")) {
//                            if (category.equals("History")) {
//                                FactsModel facts = new FactsModel(poster, category, title, text);
//                                factslist.add(facts);
//                            }
//                        } else if (getCategory.equals("Mystery")) {
//                            if (category.equals("Mystery")) {
//                                FactsModel facts = new FactsModel(poster, category, title, text);
//                                factslist.add(facts);
//                            }
//                        } else if (getCategory.equals("Psychology")) {
//                            if (category.equals("Psychology")) {
//                                FactsModel facts = new FactsModel(poster, category, title, text);
//                                factslist.add(facts);
//                            }
//                        } else if (getCategory.equals("Random")) {
//                            if (category.equals("Random")) {
//                                FactsModel facts = new FactsModel(poster, category, title, text);
//                                factslist.add(facts);
//                            }
//                        } else if (getCategory.equals("Technology")) {
//                            if (category.equals("Technology")) {
//                                FactsModel facts = new FactsModel(poster, category, title, text);
//                                factslist.add(facts);
//                            }
//                        } else {
////                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    FactsAdapter adapter = new FactsAdapter(context, factslist, getActivity());
//                    recyclerView.setAdapter(adapter);
                    AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with("ca-app-pub-9137303962163689/4766109503", adapter,
                            "small").adItemInterval(3).build();
                    recyclerView.setAdapter(admobNativeAdAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public void category(String category, Context context) {
        this.getCategory = category;
    }

    public static class CategoryFragment extends Fragment {

        CardView card_mystery, card_animals, card_health, card_technology,
                card_psychology, card_geography, card_random, card_history;
        String categoryName;

        public CategoryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_category, container, false);

            card_mystery = view.findViewById(R.id.card_mystery);
            card_animals = view.findViewById(R.id.card_animals);
            card_health = view.findViewById(R.id.card_health);
            card_technology = view.findViewById(R.id.card_technology);
            card_psychology = view.findViewById(R.id.card_psychology);
            card_geography = view.findViewById(R.id.card_geography);
            card_random = view.findViewById(R.id.card_random);
            card_history = view.findViewById(R.id.card_history);

            card_animals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryName = "Animals";
                    changeFragment();
                }
            });
            card_geography.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryName = "Geography";
                    changeFragment();
                }
            });
            card_health.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryName = "Health";
                    changeFragment();
                }
            });
            card_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryName = "History";
                    changeFragment();
                }
            });
            card_mystery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryName = "Mystery";
                    changeFragment();
                }
            });
            card_psychology.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryName = "Psychology";
                    changeFragment();
                }
            });
            card_random.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryName = "Random";
                    changeFragment();
                }
            });
            card_technology.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoryName = "Technology";
                    changeFragment();
                }
            });

            return view;
        }

        private void changeFragment() {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            CategorySearchFragment fragment = new CategorySearchFragment();
            fragment.category(categoryName, getActivity());
            ft.replace(R.id.mainContainer, fragment);
            ft.commit();
        }
    }
}