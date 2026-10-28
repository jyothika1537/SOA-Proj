import React, { useState, useEffect } from 'react';
import { fetchIncidents, createIncident, deleteIncident, updateIncidentStatus } from '../services/api';
import './TableStyles.css';

export const Incidents = () => {
    const [incidents, setIncidents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [notice, setNotice] = useState('');

    useEffect(() => {
        const load = async () => {
            try {
                const data = await fetchIncidents();
                setIncidents(data);
            } catch (err) { setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        load();
    }, []);

    if (loading) return <div className="loading-state">Loading incidents...</div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <h1>Incidents</h1>
                <p>Manage and track all reported emergencies.</p>
            <button className="action-btn" onClick={async () => { const raw = window.prompt('Incident JSON'); if (!raw) return; try { await createIncident(JSON.parse(raw)); setNotice('Incident created'); window.location.reload(); } catch (e) { setError(e.message); } }}>Create incident</button>
            {error && <div className="error-state">{error}</div>}{notice && <div className="success-state">{notice}</div>}
            </div>
            
            <div className="table-container glass-panel">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Type</th>
                            <th>Severity</th>
                            <th>Status</th>
                            <th>Location</th><th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {incidents.length === 0 ? (
                            <tr><td colSpan="6" className="empty-state">No incidents reported.</td></tr>
                        ) : (
                            incidents.map(inc => (
                                <tr key={inc.incidentId}>
                                    <td><strong>{inc.title}</strong></td>
                                    <td>{inc.incidentType}</td>
                                    <td>
                                        <span className={`badge badge-${inc.severity.toLowerCase()}`}>
                                            {inc.severity}
                                        </span>
                                    </td>
                                    <td>{inc.status}</td>
                                    <td>{inc.location}</td>
                                    <td><button onClick={async () => { const s = window.prompt('Status', inc.incidentStatus); if (s) { await updateIncidentStatus(inc.incidentId, s); window.location.reload(); } }}>Update</button> <button onClick={async () => { if (window.confirm('Delete incident?')) { await deleteIncident(inc.incidentId); window.location.reload(); } }}>Delete</button></td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
