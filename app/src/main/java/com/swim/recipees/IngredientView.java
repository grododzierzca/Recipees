package com.swim.recipees;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class IngredientView extends ConstraintLayout {

    private EditText name;
    private EditText quantity;
    private Spinner measurements;

    public IngredientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //LayoutInflater.from(context).inflate(R.layout.new_ingredients_list_element, this, false);
        //setupChildren();
    }

    public IngredientView(Context context, AttributeSet attrs){
        super(context, attrs);
        //LayoutInflater.from(context).inflate(R.layout.new_ingredients_list_element, this, false);
        //setupChildren();
    }

    public IngredientView(Context context){
        super(context);
        //LayoutInflater.from(context).inflate(R.layout.new_ingredients_list_element, this, false);
        //setupChildren();
    }


    public static IngredientView inflate(ViewGroup parent){
        IngredientView ingredient_view =  (IngredientView) LayoutInflater.from(parent.getContext()).inflate(R.layout.new_ingredients_list_element, parent, false);
        ingredient_view.setupChildren();
        return ingredient_view;
    }

    private void setupChildren(){
        name = findViewById(R.id.ingredient_element_name);
        quantity = findViewById(R.id.ingredient_element_quantity);
        measurements = findViewById(R.id.ingredient_element_spinner);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.measurements));
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measurements.setAdapter(spinner_adapter);

    }

    public void setIngredient(FragNewRecipe.Ingredient ingredient){
        name.setText(ingredient.name);
        quantity.setText(String.valueOf(ingredient.quantity));
        measurements.setSelection(ingredient.measurement);
    }

    public void updateIngredient(FragNewRecipe.Ingredient ingredient){
        ingredient.name = this.name.getText().toString();
        if(this.quantity.getText().toString().equals("")){
            ingredient.quantity = 0;
        }else   ingredient.quantity = Integer.parseInt(this.quantity.getText().toString());
        ingredient.measurement = this.measurements.getSelectedItemPosition();
    }

}
