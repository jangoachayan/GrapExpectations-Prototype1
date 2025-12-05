import { RecipeBuilder } from './components/recipe/RecipeBuilder'
import { BatchList } from './components/batch/BatchList'
import './App.css'

function App() {
  return (
    <div className="app-container">
      <header style={{ textAlign: 'center', marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '2.5rem', margin: '1rem 0' }}>GrapExpectations Studio</h1>
        <p style={{ color: '#888' }}>Premium Fermentation Management</p>
      </header>

      <main style={{ maxWidth: '800px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
        <RecipeBuilder />
        <BatchList />
      </main>
    </div>
  )
}

export default App
