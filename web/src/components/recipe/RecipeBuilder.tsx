import React, { useState } from 'react';

interface Ingredient {
    id: string;
    name: string;
    amount: number;
    unit: string;
}

interface Recipe {
    name: string;
    style: string;
    ingredients: Ingredient[];
}

export const RecipeBuilder: React.FC = () => {
    const [recipe, setRecipe] = useState<Recipe>({
        name: '',
        style: '',
        ingredients: []
    });

    const addIngredient = () => {
        const newIngredient: Ingredient = {
            id: Date.now().toString(),
            name: '',
            amount: 0,
            unit: 'kg'
        };
        setRecipe({
            ...recipe,
            ingredients: [...recipe.ingredients, newIngredient]
        });
    };

    const updateIngredient = (id: string, field: keyof Ingredient, value: string | number) => {
        setRecipe({
            ...recipe,
            ingredients: recipe.ingredients.map(ing =>
                ing.id === id ? { ...ing, [field]: value } : ing
            )
        });
    };

    return (
        <div className="recipe-builder">
            <h2>Create New Recipe</h2>

            <div className="form-group">
                <label>Recipe Name</label>
                <input
                    type="text"
                    value={recipe.name}
                    onChange={(e) => setRecipe({ ...recipe, name: e.target.value })}
                    placeholder="e.g., Summer Pinot Noir"
                />
            </div>

            <div className="form-group">
                <label>Style</label>
                <input
                    type="text"
                    value={recipe.style}
                    onChange={(e) => setRecipe({ ...recipe, style: e.target.value })}
                    placeholder="e.g., Red Wine"
                />
            </div>

            <h3>Ingredients</h3>
            {recipe.ingredients.map(ing => (
                <div key={ing.id} className="ingredient-row">
                    <input
                        type="text"
                        value={ing.name}
                        onChange={(e) => updateIngredient(ing.id, 'name', e.target.value)}
                        placeholder="Ingredient Name"
                    />
                    <input
                        type="number"
                        value={ing.amount}
                        onChange={(e) => updateIngredient(ing.id, 'amount', parseFloat(e.target.value))}
                        placeholder="Amount"
                    />
                    <select
                        value={ing.unit}
                        onChange={(e) => updateIngredient(ing.id, 'unit', e.target.value)}
                    >
                        <option value="kg">kg</option>
                        <option value="g">g</option>
                        <option value="L">L</option>
                        <option value="ml">ml</option>
                    </select>
                </div>
            ))}

            <button onClick={addIngredient}>+ Add Ingredient</button>

            <div className="actions">
                <button className="primary" onClick={() => console.log('Save', recipe)}>Save Recipe</button>
            </div>
        </div>
    );
};
