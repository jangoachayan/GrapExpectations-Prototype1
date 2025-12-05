import React, { useState, useEffect } from 'react';

interface Batch {
    id: string;
    name: string;
    status: string;
    startAt: string;
}

export const BatchList: React.FC = () => {
    // Mock data for MVP - in real app would fetch from Firestore
    const [batches] = useState<Batch[]>([
        { id: '1', name: 'Summer Pinot', status: 'active', startAt: '2023-10-01' },
        { id: '2', name: 'Winter Stout', status: 'conditioning', startAt: '2023-11-15' },
    ]);

    return (
        <div className="batch-list" style={{ marginTop: '2rem' }}>
            <h2>Active Batches</h2>
            <div className="batch-grid" style={{ display: 'grid', gap: '1rem' }}>
                {batches.map(batch => (
                    <div key={batch.id} className="batch-card" style={{
                        padding: '1rem',
                        backgroundColor: '#1a1a1a',
                        borderRadius: '8px',
                        border: '1px solid #333'
                    }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <h3 style={{ margin: 0 }}>{batch.name}</h3>
                            <span style={{
                                padding: '4px 8px',
                                borderRadius: '4px',
                                backgroundColor: batch.status === 'active' ? '#00E676' : '#FFC107',
                                color: '#000',
                                fontSize: '0.8rem',
                                fontWeight: 'bold'
                            }}>
                                {batch.status.toUpperCase()}
                            </span>
                        </div>
                        <p style={{ color: '#888', marginTop: '0.5rem' }}>Started: {batch.startAt}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};
