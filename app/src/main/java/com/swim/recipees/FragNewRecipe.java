package com.swim.recipees;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.swim.recipees.FragBrowse.recipes;





/**
 * A simple {@link Fragment} subclass.
 */
public class FragNewRecipe extends Fragment {
    private static final int REQUEST_TAKE_PHOTO = 1000;
    private static final int IMAGE_CAPTURED_CODE = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    class Ingredient{
        String name;
        int quantity;
        int measurement;

        Ingredient(String name, int quantity, int measurement){
            this.name = name;
            this.quantity = quantity;
            this.measurement = measurement;
        }

        @Override
        public String toString(){
            return name + ", " + quantity + ", " + measurement;
        }
    }

    static class Holder{
        EditText name;
        EditText quantity;
        Spinner measurements;
    }

    class IngredientAdapter extends ArrayAdapter<Ingredient>{

        private Context context;
        private ArrayList<Ingredient> list;

        public IngredientAdapter(Context context, int resource, ArrayList<Ingredient> objects) {
            super(context, resource, objects);
            this.list = objects;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            Holder holder;
            final Ingredient ingredient = getItem(position);


            if(convertView == null){
                holder = new Holder();
                convertView = IngredientView.inflate(parent);
                holder.name = convertView.findViewById(R.id.ingredient_element_name);
                holder.quantity = convertView.findViewById(R.id.ingredient_element_quantity);
                holder.measurements = convertView.findViewById(R.id.ingredient_element_spinner);

                convertView.setTag(holder);
            }else{
                holder = (Holder) convertView.getTag();
            }

            holder.name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ingredient.name = s.toString();
                }
            });

            holder.quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!s.toString().equals("")) ingredient.quantity = Integer.valueOf(s.toString());
                    else ingredient.quantity = 0;
                }
            });

            holder.measurements.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    ingredient.measurement = pos;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            Log.i("Ingredient list", list.toString());
            Log.i("Position ", String.valueOf(position));
            return convertView;
            /*
            IngredientView list_element = (IngredientView)convertView;
            if (list_element == null) {
                Log.i("convertView", "null");
                list_element = IngredientView.inflate(parent);
                list_element.setIngredient(getItem(position));
            }

            return list_element;*/

        }

        @Override
        public Ingredient getItem(int position){
            return list.get(position);
        }


        public Ingredient getLast(){
            return list.get(list.size()-1);
        }



    }


    private static ArrayList<Ingredient> ingredient_list = new ArrayList<>();
    IngredientAdapter adapter;

    public FragNewRecipe() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i("Instance state", "Saved");
    }

    private void dispatchTakePictureIntentBitmap() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    String currentPhotoPath;
    Uri imageUri;
    ImageView recipe_img;
    Boolean ready_to_change = false;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void openCamera(){
        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permission, REQUEST_TAKE_PHOTO);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Taken by the camera");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(camera, IMAGE_CAPTURED_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURED_CODE && resultCode == RESULT_OK) {
            ready_to_change = true;
            Picasso.with(getContext()).load(imageUri).into(recipe_img, new Callback() {
                @Override
                public void onSuccess() {
                    Log.i("Loaded", "image");
                    recipe_img.setRotation(90f);
                }

                @Override
                public void onError() {
                    Log.i("Failed to load", "image");
                }
            });
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_frag_new_recipe, container, false);
        Button add_new_ingredient = view.findViewById(R.id.add_ingredient_button);
        Button save_recipe = view.findViewById(R.id.save_recipe_button);
        final ListView list = view.findViewById(R.id.ingredients_listview);


        adapter = new IngredientAdapter(getContext(), R.layout.new_ingredients_list_element, ingredient_list);
        adapter.setNotifyOnChange(true);
        list.setAdapter(adapter);


        add_new_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add(new Ingredient(getString(R.string.default_name), getResources().getInteger(R.integer.default_quantity), getResources().getInteger(R.integer.default_measurement)));
                Log.i("Ingredient changed", "Added new ingredient");

            }
        });

        save_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupWindow popup = new PopupWindow(inflater.inflate(R.layout.save_popup, list, false), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popup.showAtLocation(getView(), Gravity.CENTER, 0, 0);
                dimBehind(popup);

                final Button save_recipe = popup.getContentView().findViewById(R.id.save_popup_button);
                final ImageView recipe_image = popup.getContentView().findViewById(R.id.save_popup_image);
                recipe_img = recipe_image;

                recipe_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCamera();

                    }
                });

                save_recipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText name = popup.getContentView().findViewById(R.id.save_popup_name);
                        String recipe_name = name.getText().toString();

                        EditText description = popup.getContentView().findViewById(R.id.save_popup_description);
                        String recipe_desc = description.getText().toString();

                        recipes.add(new Recipe(recipe_name, recipe_image, ingredient_list, recipe_desc));
                        //SAVE RECIPE HERE

                        Toast.makeText(getContext(), "Recipe saved", Toast.LENGTH_LONG).show();
                        popup.dismiss();
                        ingredient_list.clear();
                        adapter.notifyDataSetChanged();

                        Log.i("Saved recipe: ", recipes.get(recipes.size()-1).toString());

                        //TEMPORARY SOLUTION
                        FragBrowse browser = (FragBrowse)getFragmentManager().getFragments().get(0);
                        browser.getAdapter().notifyDataSetChanged();
                    }

                });
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.5f;
        wm.updateViewLayout(container, p);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.new_recipe_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_clear_all:
                if(ingredient_list.size() > 0){
                    ingredient_list.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), getResources().getText(R.string.cleared_all), Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return true;
    }

}
