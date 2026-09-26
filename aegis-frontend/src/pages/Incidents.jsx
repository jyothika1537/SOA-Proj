import React, { useState, useEffect } from 'react';
import { fetchIncidents } from '../services/api';
import './TableStyles.css';

export const Incidents = () => {
    const [incidents, setIncidents] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const load = async () => {
            try {
                const data = await fetchIncidents();
                setIncidents(data);
            } catch (err) {
                console.error(err);
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
            </div>
            
            <div className="table-container glass-panel">
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Type</th>
                            <th>Severity</th>
                            <th>Status</th>
                            <th>Location</th>
                        </tr>
                    </thead>
                    <tbody>
                        {incidents.length === 0 ? (
                            <tr><td colSpan="5" className="empty-state">No incidents reported.</td></tr>
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
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
