import React, {useState} from 'react';
import './AddRecipe.css';
import {useFieldArray, useForm} from "react-hook-form";
import {Alert, Button, Col, Form, Row} from 'react-bootstrap';
import {useNavigate} from "react-router-dom";

import AddIngredient from "../AddIngredient/AddIngredient";
import {addRecipe} from "../../apis/recipeApi";

const EMPTY_RECIPE = {name: '', description: '', imageUrl: '', ingredients: []};
const EMPTY_INGREDIENT = {name: '', comment: '', unit: 'PIECE', amount: ''};

function AddRecipe() {
    const navigate = useNavigate();
    const [submitError, setSubmitError] = useState(null);

    const {
        register,
        control,
        handleSubmit,
        formState: {errors, isSubmitting},
    } = useForm({defaultValues: EMPTY_RECIPE})

    const {fields, append, remove} = useFieldArray({control, name: 'ingredients'});

    const onSubmit = async (recipe) => {
        setSubmitError(null);
        try {
            await addRecipe(recipe);
            navigate('/');
        } catch {
            setSubmitError('The recipe could not be saved. Please try again.');
        }
    }

    return (
        <div className="m-3">
            <h1 className="h3 bg-dark text-bg-primary mt-2">Add Recipe</h1>
            <Form noValidate onSubmit={handleSubmit(onSubmit)}>
                {submitError && <Alert variant="danger">{submitError}</Alert>}
                <Form.Group className="mb-1" controlId="recipeName">
                    <Form.Label>Recipe Name:</Form.Label>
                    <Form.Control
                        placeholder="Name"
                        isInvalid={!!errors.name}
                        {...register('name', {required: 'Name is required'})}
                    />
                    <Form.Control.Feedback type="invalid">{errors.name?.message}</Form.Control.Feedback>
                </Form.Group>
                <Form.Group className="mb-1" controlId="recipeDescription">
                    <Form.Label>Description:</Form.Label>
                    <Form.Control placeholder="Description" {...register('description')}/>
                </Form.Group>
                <Form.Group className="mb-5" controlId="recipeImageUrl">
                    <Form.Label>Image URL:</Form.Label>
                    <Form.Control type="url" placeholder="URL" {...register('imageUrl')}/>
                </Form.Group>
                <Row>
                    <Col>Ingredient</Col>
                    <Col>Unit</Col>
                    <Col>Quantity</Col>
                    <Col xs={1}></Col>
                </Row>
                <hr/>
                {fields.map((field, index) => <AddIngredient
                    key={field.id}
                    index={index}
                    register={register}
                    errors={errors.ingredients?.[index]}
                    onRemove={remove}
                />)}
                <Button
                    variant='warning'
                    onClick={() => append(EMPTY_INGREDIENT)}
                    className="mt-1"
                >Add Ingredient</Button>
                <Button variant="primary" type="submit" className="mb-5 ms-2" disabled={isSubmitting}>
                    Submit
                </Button>
            </Form>
        </div>
    )
}

export default AddRecipe;
