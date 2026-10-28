import React, { useState, useEffect } from 'react';
import { fetchResources, createResource, deleteResource } from '../services/api';
import './TableStyles.css';

export const Resources = () => {
    const [resources, setResources] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const load = async () => {
            try {
                const data = await fetchResources();
                setResources(data);
            } catch (err) { setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, []);

    if (loading) return <div className="loading-state">Loading resources...</div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <h1>Resources</h1>
                <p>Manage equipment and personnel availability.</p>
            <button onClick={async () => { const raw = window.prompt('Resource JSON'); if (!raw) return; try { await createResource(JSON.parse(raw)); window.location.reload(); } catch (e) { setError(e.message); } }}>Register resource</button>
            {error && <div className="error-state">{error}</div>}
            </div>
            
            <div className="table-container glass-panel">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Resource Name</th>
                            <th>Type</th>
                            <th>Zone</th>
                            <th>Total Qty</th>
                            <th>Available Qty</th>
                            <th>Status</th><th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {resources.length === 0 ? (
                            <tr><td colSpan="7" className="empty-state">No resources available.</td></tr>
                        ) : (
                            resources.map(res => (
                                <tr key={res.resourceId}>
                                    <td><strong>{res.resourceName}</strong></td>
                                    <td>{res.resourceType}</td>
                                    <td>{res.deploymentZone}</td>
                                    <td>{res.quantity}</td>
                                    <td>{res.availableQuantity}</td>
                                    <td>
                                        <span className={`badge ${res.status === 'AVAILABLE' ? 'badge-low' : 'badge-high'}`}>
                                            {res.status}
                                        </span>
                                    </td>
                                    <td><button onClick={async () => { if (window.confirm('Delete resource?')) { await deleteResource(res.resourceId); window.location.reload(); } }}>Delete</button></td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
