import React from 'react';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {MemoryRouter, Route, Routes} from 'react-router-dom';

import EditRecipe from './EditRecipe';
import {getRecipe, updateRecipe} from '../../apis/recipeApi';

jest.mock('../../apis/recipeApi', () => ({
    getRecipe: jest.fn(),
    updateRecipe: jest.fn(),
}));

const RECIPE_ID = '3f2b8c1e-0000-0000-0000-000000000001';

const storedRecipe = {
    id: RECIPE_ID,
    name: 'Pancakes',
    description: null,
    imageUrl: 'https://example.com/pancakes.jpg',
    ingredients: [{id: 'i-1', name: 'Flour', comment: 'The fine one', unit: 'GRAMM', amount: 200}],
};

const renderEditRecipe = () => render(
    <MemoryRouter initialEntries={[`/recipes/${RECIPE_ID}/edit`]}>
        <Routes>
            <Route path="/" element={<div>Browse page</div>}/>
            <Route path="/recipes/:recipeId/edit" element={<EditRecipe/>}/>
        </Routes>
    </MemoryRouter>
);

describe('EditRecipe', () => {
    beforeEach(() => {
        getRecipe.mockReset();
        updateRecipe.mockReset();
    });

    it('loads the recipe and prefills the form', async () => {
        getRecipe.mockResolvedValue(storedRecipe);
        renderEditRecipe();

        expect(await screen.findByLabelText('Recipe Name:')).toHaveValue('Pancakes');
        expect(screen.getByLabelText('Description:')).toHaveValue('');
        expect(screen.getByLabelText('Ingredient 1 name')).toHaveValue('Flour');
        expect(screen.getByLabelText('Ingredient 1 unit')).toHaveValue('GRAMM');
        expect(screen.getByLabelText('Ingredient 1 quantity')).toHaveValue(200);
        expect(getRecipe).toHaveBeenCalledWith(RECIPE_ID);
    });

    it('sends the changed recipe to the backend and navigates back to browse', async () => {
        getRecipe.mockResolvedValue(storedRecipe);
        updateRecipe.mockResolvedValue({});
        renderEditRecipe();

        const nameInput = await screen.findByLabelText('Recipe Name:');
        userEvent.clear(nameInput);
        userEvent.type(nameInput, 'Blueberry Pancakes');
        const quantityInput = screen.getByLabelText('Ingredient 1 quantity');
        userEvent.clear(quantityInput);
        userEvent.type(quantityInput, '250');
        userEvent.click(screen.getByRole('button', {name: 'Submit'}));

        expect(await screen.findByText('Browse page')).toBeInTheDocument();
        expect(updateRecipe).toHaveBeenCalledWith(RECIPE_ID, expect.objectContaining({
            name: 'Blueberry Pancakes',
            description: '',
            imageUrl: 'https://example.com/pancakes.jpg',
            ingredients: [expect.objectContaining({name: 'Flour', comment: 'The fine one', unit: 'GRAMM', amount: 250})],
        }));
    });

    it('shows an error when the recipe cannot be loaded', async () => {
        getRecipe.mockRejectedValue(new Error('Request failed with status code 404'));
        renderEditRecipe();

        expect(await screen.findByText('The recipe could not be loaded.')).toBeInTheDocument();
    });

    it('stays on the page and shows an error when saving fails', async () => {
        getRecipe.mockResolvedValue(storedRecipe);
        updateRecipe.mockRejectedValue(new Error('Network Error'));
        renderEditRecipe();

        userEvent.click(await screen.findByRole('button', {name: 'Submit'}));

        expect(await screen.findByText('The recipe could not be saved. Please try again.')).toBeInTheDocument();
        expect(screen.queryByText('Browse page')).not.toBeInTheDocument();
    });
});
