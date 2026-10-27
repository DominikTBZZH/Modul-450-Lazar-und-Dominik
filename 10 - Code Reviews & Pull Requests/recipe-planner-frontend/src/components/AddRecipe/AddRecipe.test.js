import React from 'react';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {MemoryRouter, Route, Routes} from 'react-router-dom';

import AddRecipe from './AddRecipe';
import {addRecipe} from '../../apis/recipeApi';

jest.mock('../../apis/recipeApi', () => ({
    addRecipe: jest.fn(),
}));

const renderAddRecipe = () => render(
    <MemoryRouter initialEntries={['/new-menues']}>
        <Routes>
            <Route path="/" element={<div>Browse page</div>}/>
            <Route path="/new-menues" element={<AddRecipe/>}/>
        </Routes>
    </MemoryRouter>
);

describe('AddRecipe', () => {
    beforeEach(() => {
        addRecipe.mockReset();
    });

    it('sends the new recipe with its ingredients and navigates back to browse', async () => {
        addRecipe.mockResolvedValue({id: '1'});
        renderAddRecipe();

        userEvent.type(screen.getByLabelText('Recipe Name:'), 'Pancakes');
        userEvent.type(screen.getByLabelText('Description:'), 'Fluffy');
        userEvent.type(screen.getByLabelText('Image URL:'), 'https://example.com/pancakes.jpg');
        userEvent.click(screen.getByRole('button', {name: 'Add Ingredient'}));
        userEvent.type(await screen.findByLabelText('Ingredient 1 name'), 'Flour');
        userEvent.selectOptions(screen.getByLabelText('Ingredient 1 unit'), 'GRAMM');
        userEvent.type(screen.getByLabelText('Ingredient 1 quantity'), '250');
        userEvent.click(screen.getByRole('button', {name: 'Submit'}));

        expect(await screen.findByText('Browse page')).toBeInTheDocument();
        expect(addRecipe).toHaveBeenCalledWith({
            name: 'Pancakes',
            description: 'Fluffy',
            imageUrl: 'https://example.com/pancakes.jpg',
            ingredients: [{name: 'Flour', comment: '', unit: 'GRAMM', amount: 250}],
        });
    });

    it('does not submit without a recipe name', async () => {
        renderAddRecipe();

        userEvent.click(screen.getByRole('button', {name: 'Submit'}));

        expect(await screen.findByText('Name is required')).toBeInTheDocument();
        expect(addRecipe).not.toHaveBeenCalled();
    });

    it('does not submit an ingredient without a positive quantity', async () => {
        renderAddRecipe();

        userEvent.type(screen.getByLabelText('Recipe Name:'), 'Pancakes');
        userEvent.click(screen.getByRole('button', {name: 'Add Ingredient'}));
        userEvent.type(await screen.findByLabelText('Ingredient 1 name'), 'Flour');
        userEvent.type(screen.getByLabelText('Ingredient 1 quantity'), '0');
        userEvent.click(screen.getByRole('button', {name: 'Submit'}));

        expect(await screen.findByText('Quantity must be at least 1')).toBeInTheDocument();
        expect(addRecipe).not.toHaveBeenCalled();
    });

    it('removes an ingredient row', async () => {
        renderAddRecipe();

        userEvent.click(screen.getByRole('button', {name: 'Add Ingredient'}));
        userEvent.click(await screen.findByRole('button', {name: 'Remove ingredient 1'}));

        await waitFor(() => expect(screen.queryByLabelText('Ingredient 1 name')).not.toBeInTheDocument());
    });

    it('shows an error when the backend call fails', async () => {
        addRecipe.mockRejectedValue(new Error('Network Error'));
        renderAddRecipe();

        userEvent.type(screen.getByLabelText('Recipe Name:'), 'Pancakes');
        userEvent.click(screen.getByRole('button', {name: 'Submit'}));

        expect(await screen.findByText('The recipe could not be saved. Please try again.')).toBeInTheDocument();
        await waitFor(() => expect(screen.getByRole('button', {name: 'Submit'})).toBeEnabled());
    });
});
