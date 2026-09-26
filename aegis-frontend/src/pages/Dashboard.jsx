import React, { useState, useEffect } from 'react';
import { fetchIncidents, fetchResources, fetchResponses } from '../services/api';
import { AlertTriangle, Package, Activity, Users } from 'lucide-react';
import './Dashboard.css';

export const Dashboard = () => {
    const [stats, setStats] = useState({
        activeIncidents: 0,
        availableResources: 0,
        activeResponses: 0,
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadDashboardData = async () => {
            try {
                const [incidents, resources, responses] = await Promise.all([
                    fetchIncidents(),
                    fetchResources(),
                    fetchResponses()
                ]);

                setStats({
                    activeIncidents: incidents.filter(i => i.status !== 'RESOLVED').length,
                    availableResources: resources.reduce((acc, r) => acc + r.availableQuantity, 0),
                    activeResponses: responses.length,
                });
            } catch (error) {
                console.error("Failed to fetch dashboard data", error);
            } finally {
                setLoading(false);
            }
        };

        loadDashboardData();
    }, []);

    if (loading) {
        return <div className="loading-state">Loading dashboard...</div>;
    }

    return (
        <div className="dashboard">
            <header className="page-header">
                <h1>Dashboard Overview</h1>
                <p>Welcome back! Here is the current status of operations.</p>
            </header>

            <div className="dashboard-grid">
                <div className="glass-card stat-card">
                    <div className="stat-icon alert"><AlertTriangle size={24} /></div>
                    <div className="stat-details">
                        <h3>Active Incidents</h3>
                        <p className="stat-value">{stats.activeIncidents}</p>
                    </div>
                </div>

                <div className="glass-card stat-card">
                    <div className="stat-icon success"><Package size={24} /></div>
                    <div className="stat-details">
                        <h3>Available Resources</h3>
                        <p className="stat-value">{stats.availableResources}</p>
                    </div>
                </div>

                <div className="glass-card stat-card">
                    <div className="stat-icon primary"><Activity size={24} /></div>
                    <div className="stat-details">
                        <h3>Active Responses</h3>
                        <p className="stat-value">{stats.activeResponses}</p>
                    </div>
                </div>
            </div>

            <section className="dashboard-section">
                <h2>Quick Actions</h2>
                <div className="action-buttons">
                    <button className="btn btn-primary"><AlertTriangle size={18}/> Report Incident</button>
                    <button className="btn btn-secondary"><Package size={18}/> Add Resource</button>
                </div>
            </section>
        </div>
    );
};
