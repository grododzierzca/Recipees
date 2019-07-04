package com.swim.recipees;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.swim.recipees.FragBrowse.recipes;
import static com.swim.recipees.FragNewRecipe.dimBehind;
import static com.swim.recipees.MainActivity.first_color;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragFavourites extends Fragment {


    public static ArrayList<Recipe> favourites = new ArrayList<>();
    private FavouritesAdapter adapter;

    public FragFavourites() {
        // Required empty public constructor
    }

    public FavouritesAdapter getAdapter() {
        return adapter;
    }

    public static void addRecipe(Recipe recipe) {
        favourites.add(recipe);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_favourites, container, false);
        ListView list_view = view.findViewById(R.id.favourites_listview);
        adapter = new FavouritesAdapter(getContext(), R.layout.favourites_list_element, favourites);
        list_view.setAdapter(adapter);

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favourites_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favourites_help:
                Intent intent = new Intent(getContext(), HelpActivity.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    class FavouritesAdapter extends ArrayAdapter<Recipe> {
        private Context context;
        private ArrayList<Recipe> favourites;
        private int position;
        private int layout_resource_id;

        FavouritesAdapter(Context context, int layout_resource_id, ArrayList<Recipe> favourites) {
            super(context, layout_resource_id, favourites);
            this.context = context;
            this.favourites = favourites;
            this.layout_resource_id = layout_resource_id;
            this.position = 0;
        }

        private void setPosition(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public int getCount() {
            return favourites.size();
        }

        @Override
        public Recipe getItem(int position) {
            return favourites.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final Holder holder;

            if (convertView == null) {
                convertView = GridView.inflate(context, R.layout.favourites_list_element, null);
                holder = new Holder(convertView);
                holder.name = convertView.findViewById(R.id.fav_list_elem_name);
                holder.image = convertView.findViewById(R.id.fav_list_elem_img);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.name.setText(favourites.get(position).getName());
            holder.image.setImageDrawable(favourites.get(position).getImage().getDrawable());
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 3:
                Log.i("Clicked", "3");
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
                        Recipe current = adapter.favourites.remove(adapter.getPosition());
                        favourites.remove(current);
                        recipes.remove(current);
                        for (Fragment fragment : getFragmentManager().getFragments()) {
                            if (fragment instanceof FragBrowse) {
                                ((FragBrowse) fragment).getAdapter().notifyDataSetChanged();
                            }
                        }
                        popup.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            case 4:
                //remove from favourites
                Recipe current = adapter.favourites.remove(adapter.getPosition());
                Log.i("Removed from favourites", current.toString());
                favourites.remove(current);
                adapter.notifyDataSetChanged();

        }

        return super.onContextItemSelected(item);
    }

    static class Holder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView name;
        ImageView image;

        public Holder(View v) {
            super(v);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(1, 3, 0, R.string.delete);
            menu.add(1, 4, 3, R.string.remove_from_favourites);
        }

    }

}
