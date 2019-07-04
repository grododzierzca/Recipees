package com.swim.recipees;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import static com.swim.recipees.FragNewRecipe.dimBehind;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragBrowse extends Fragment {

    public static ArrayList<Recipe> recipes = new ArrayList<>();
    private GridAdapter adapter;

    public FragBrowse() {
        // Required empty public constructor
    }


    public GridAdapter getAdapter(){
        return adapter;
    }

    static class Holder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView name;
        ImageView image;
        public Holder(View v){
            super(v);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
            menu.add(0, 1, 0, R.string.delete);
            menu.add(0, 2, 3, R.string.add_to_favourites);
        }

    }

    class GridAdapter extends ArrayAdapter<Recipe> {
        private Context context;
        private ArrayList<Recipe> recipes;
        private ArrayList<Recipe> filtered_recipes;
        private RecipeFilter filter;
        private int position;
        private int layout_resource_id;


        GridAdapter(Context context, int layout_resource_id, ArrayList<Recipe> recipes){
            super(context, layout_resource_id, recipes);
            this.context = context;
            this.recipes = recipes;
            this.filtered_recipes = recipes;
            this.layout_resource_id = layout_resource_id;
            this.position = 0;
            this.filter = new RecipeFilter(this.recipes);
        }

        private void setPosition(int position){
            this.position = position;
        }

        public int getPosition(){
            return position;
        }

        @Override
        public int getCount() {
            return filtered_recipes.size();
        }

        @Override
        public Recipe getItem(int position) {
            return filtered_recipes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Filter getFilter(){
            return filter;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final Holder holder;

            if(convertView == null){
                convertView = GridView.inflate(context, R.layout.browse_recipe_element, null);
                holder = new Holder(convertView);
                holder.name = convertView.findViewById(R.id.recipe_element_name);
                holder.image = convertView.findViewById(R.id.recipe_element_image);
                convertView.setTag(holder);
            }else{
                holder = (Holder)convertView.getTag();
            }

            holder.name.setText(filtered_recipes.get(position).getName());
            holder.image.setImageDrawable(filtered_recipes.get(position).getImage().getDrawable());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Opening: " + holder.name.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), ViewRecipe.class);
                    intent.putExtra("recipe_position", position);
                    Log.i("Opening recipe: ", String.valueOf(position));
                    Log.i("All recipes: ", recipes.toString());
                    startActivity(intent);
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setPosition(position);
                    return false;
                }
            });


            return convertView;
        }

    }
    class RecipeFilter extends Filter{

        private ArrayList<Recipe> list;
        private String last_filter;

        RecipeFilter(ArrayList<Recipe> list){
            this.list = list;
            this.last_filter = "";
        }

        public String getFilter(){
            return last_filter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            last_filter = constraint.toString();
            String filter_string = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            ArrayList<Recipe> new_list = new ArrayList<>(list.size());

            for(int i = 0; i < list.size(); i++){
                if(list.get(i).getName().toLowerCase().contains(filter_string)){
                    new_list.add(list.get(i));
                }
            }
            Log.i("Filtered recipes", new_list.toString());
            results.values = new_list;
            results.count = new_list.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            getAdapter().filtered_recipes = (ArrayList<Recipe>) results.values;
            getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //saveRecipes();
    }



    @Override
    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void saveRecipes(){
        Log.i("Recipes", recipes.toString());
        SharedPreferences sp = getActivity().getSharedPreferences("shared_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recipes);
        spe.putString("Recipes", json);
        spe.apply();
        Log.i("Saved recipes: ", recipes.toString());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //recipes = new ArrayList<>(); //load recipes here;
        setHasOptionsMenu(true);

        ImageView placeholder = new ImageView(getContext());
        placeholder.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));


        int orientation = getActivity().getResources().getConfiguration().orientation;
        View view;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            view = inflater.inflate(R.layout.fragment_frag_browse, container, false);
        }else{
            view = inflater.inflate(R.layout.fragment_frag_browse, container, false);
        }

        GridView grid_view = view.findViewById(R.id.browse_recipes_grid);

        adapter = new GridAdapter(getContext(), android.R.layout.simple_list_item_1, recipes);

        grid_view.setAdapter(adapter);


        return view;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case 1:
                final PopupWindow popup = new PopupWindow(LayoutInflater.from(getContext()).inflate(R.layout.delete_popup, null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popup.showAtLocation(getView(), Gravity.CENTER, 0, 0);
                dimBehind(popup);
                Button delete_yes = popup.getContentView().findViewById(R.id.delete_button_yes);
                Button delete_no = popup.getContentView().findViewById(R.id.delete_button_no);

                delete_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                    }
                });

                delete_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Recipe current = adapter.filtered_recipes.get(adapter.getPosition());
                        recipes.remove(current);
                        if(FragFavourites.favourites.contains(current)) FragFavourites.favourites.remove(current);
                        popup.dismiss();
                        for (Fragment fragment: getFragmentManager().getFragments()){
                            if(fragment instanceof FragFavourites){
                                ((FragFavourites) fragment).getAdapter().notifyDataSetChanged();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            case 2:
                //add to favourites
                Recipe current = adapter.filtered_recipes.get(adapter.getPosition());
                if(!FragFavourites.favourites.contains(current)) {
                    FragFavourites.favourites.add(current);
                    for (Fragment fragment: getFragmentManager().getFragments()){
                        if(fragment instanceof FragFavourites){
                            ((FragFavourites) fragment).getAdapter().notifyDataSetChanged();
                        }
                    }
                }
                else Toast.makeText(getContext(), "Recipe is already in favourites!", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.browse_menu, menu);

        android.widget.SearchView search = (android.widget.SearchView) menu.findItem(R.id.menu_browse_search).getActionView();
        RecipeFilter filter = (RecipeFilter) getAdapter().getFilter();
        search.setQuery(filter.getFilter(), false);
        search.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_browse_search:
                Log.i("Clicked menu", "search");
                //search
                return true;
            case R.id.menu_browse_select_all:
                Log.i("Clicked menu", "select all");
                //select all
                return true;
            case R.id.menu_browse_clear_selection:
                Log.i("Clicked menu", "clear selection");
                //clear selection
                return true;
            case R.id.menu_browse_delete:
                Log.i("Clicked menu", "delete selected");
                //delete selected
                return true;

        }
        return true;
    }

}
