import React, { useState, useEffect } from 'react';
import { fetchResponses } from '../services/api';
import './TableStyles.css';

export const Responses = () => {
    const [responses, setResponses] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const load = async () => {
            try {
                const data = await fetchResponses();
                setResponses(data);
            } catch (err) {
                console.error(err);
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
            </div>
            
            <div className="table-container glass-panel">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Response ID</th>
                            <th>Incident ID</th>
                            <th>Resource ID</th>
                            <th>Priority</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {responses.length === 0 ? (
                            <tr><td colSpan="5" className="empty-state">No active responses.</td></tr>
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
                                    <td>{resp.status}</td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
