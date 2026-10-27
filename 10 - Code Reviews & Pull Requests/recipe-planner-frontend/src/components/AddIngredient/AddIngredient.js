import React from 'react';
import './AddIngredient.css';
import {Button, Col, Form, Row} from "react-bootstrap";

// Must match the enum ch.tbz.recipe.planner.domain.Unit in the backend
export const UNITS = ['PIECE', 'GRAMM', 'KILOGRAMM', 'LITRE', 'DECILITRE'];

const AddIngredient = ({index, register, errors, onRemove}) => {
    const fieldName = (field) => `ingredients.${index}.${field}`;

    return (
        <Row>
            <Col>
                <Form.Group className="mb-1">
                    <Form.Control
                        placeholder="Name"
                        aria-label={`Ingredient ${index + 1} name`}
                        isInvalid={!!errors?.name}
                        {...register(fieldName('name'), {required: 'Name is required'})}
                    />
                    <Form.Control.Feedback type="invalid">{errors?.name?.message}</Form.Control.Feedback>
                </Form.Group>
            </Col>
            <Col>
                <Form.Group className="mb-1">
                    <Form.Select aria-label={`Ingredient ${index + 1} unit`} {...register(fieldName('unit'))}>
                        {UNITS.map(unit => <option key={unit} value={unit}>{unit}</option>)}
                    </Form.Select>
                </Form.Group>
            </Col>
            <Col>
                <Form.Group className="mb-1">
                    <Form.Control
                        type="number"
                        placeholder="Quantity"
                        aria-label={`Ingredient ${index + 1} quantity`}
                        isInvalid={!!errors?.amount}
                        {...register(fieldName('amount'), {
                            valueAsNumber: true,
                            required: 'Quantity is required',
                            min: {value: 1, message: 'Quantity must be at least 1'}
                        })}
                    />
                    <Form.Control.Feedback type="invalid">{errors?.amount?.message}</Form.Control.Feedback>
                </Form.Group>
            </Col>
            <Col xs={1}>
                <Button
                    onClick={() => onRemove(index)}
                    variant='outline-dark'
                    className="mb-1"
                    aria-label={`Remove ingredient ${index + 1}`}
                >x</Button>
            </Col>
        </Row>
    )
}

export default AddIngredient;
