package com.swim.recipees;

import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class Recipe {
    private String name;
    private ImageView image;
    private ArrayList<FragNewRecipe.Ingredient> ingredients;
    private String description;



    public Recipe(String name, ImageView image, ArrayList<FragNewRecipe.Ingredient> ingredients, String description){
        this.name = name;
        this.image = image;
        this.ingredients = ingredients;
        this.description = description;
    }


    public ImageView getImage(){
        return image;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }


    public String toString(){
        return getName()+", "+getDescription()+", "+ingredients.toString();
    }
}
