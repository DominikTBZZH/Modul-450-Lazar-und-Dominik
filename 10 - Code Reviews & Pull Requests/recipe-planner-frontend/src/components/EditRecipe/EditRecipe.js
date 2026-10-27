import React, {useEffect, useState} from 'react';
import {Alert} from "react-bootstrap";
import {useParams} from "react-router-dom";

import RecipeForm, {EMPTY_RECIPE} from "../RecipeForm/RecipeForm";
import {getRecipe, updateRecipe} from "../../apis/recipeApi";

function EditRecipe() {
    const {recipeId} = useParams();
    const [recipe, setRecipe] = useState(null);
    const [loadError, setLoadError] = useState(null);

    useEffect(() => {
        getRecipe(recipeId)
            // The backend may return null for optional fields, the form needs strings
            .then(loaded => setRecipe({...EMPTY_RECIPE, ...removeNullValues(loaded)}))
            .catch(() => setLoadError('The recipe could not be loaded.'));
    }, [recipeId]);

    if (loadError) return <Alert variant="danger" className="m-3">{loadError}</Alert>;
    if (!recipe) return null;

    return <RecipeForm
        title="Edit Recipe"
        initialRecipe={recipe}
        onSave={changedRecipe => updateRecipe(recipeId, changedRecipe)}
    />
}

const removeNullValues = (object) =>
    Object.fromEntries(Object.entries(object).filter(([, value]) => value !== null));

export default EditRecipe;
