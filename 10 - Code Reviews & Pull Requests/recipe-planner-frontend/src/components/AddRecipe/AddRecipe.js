import React from 'react';

import RecipeForm from "../RecipeForm/RecipeForm";
import {addRecipe} from "../../apis/recipeApi";

function AddRecipe() {
    return <RecipeForm title="Add Recipe" onSave={addRecipe}/>
}

export default AddRecipe;
