import React, { useState, useEffect } from 'react';
import { fetchResponses, createResponse, updateResponseStatus, deleteResponse } from '../services/api';
import './TableStyles.css';

export const Responses = () => {
    const [responses, setResponses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const load = async () => {
            try {
                const data = await fetchResponses();
                setResponses(data);
            } catch (err) { setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, []);

    if (loading) return <div className="loading-state">Loading responses...</div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <h1>Active Responses</h1>
                <p>Track ongoing incident response efforts.</p>
            <button onClick={async () => { const raw = window.prompt('Response JSON'); if (!raw) return; try { await createResponse(JSON.parse(raw)); window.location.reload(); } catch (e) { setError(e.message); } }}>Create response</button>
            {error && <div className="error-state">{error}</div>}
            </div>
            
            <div className="table-container glass-panel">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Response ID</th>
                            <th>Incident ID</th>
                            <th>Resource ID</th>
                            <th>Priority</th>
                            <th>Status</th><th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {responses.length === 0 ? (
                            <tr><td colSpan="6" className="empty-state">No active responses.</td></tr>
                        ) : (
                            responses.map(resp => (
                                <tr key={resp.responseId}>
                                    <td><strong>{resp.responseId.substring(0, 8)}...</strong></td>
                                    <td>{resp.incidentId}</td>
                                    <td>{resp.resourceId}</td>
                                    <td>
                                        <span className={`badge badge-${resp.priority.toLowerCase()}`}>
                                            {resp.priority}
                                        </span>
                                    </td>
                                    <td>{resp.responseStatus}</td>
                                    <td><button onClick={async () => { const s = window.prompt('Status', resp.responseStatus); if (s) { await updateResponseStatus(resp.responseId, s); window.location.reload(); } }}>Update</button> <button onClick={async () => { if (window.confirm('Delete response?')) { await deleteResponse(resp.responseId); window.location.reload(); } }}>Delete</button></td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
