import React, { useContext } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { LayoutDashboard, AlertTriangle, Package, Activity, LogOut, ShieldAlert } from 'lucide-react';
import './Layout.css';

export const Layout = () => {
    const { user, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="app-container">
            <aside className="sidebar glass-panel">
                <div className="sidebar-header">
                    <ShieldAlert size={32} color="#3b82f6" />
                    <h2>Aegis</h2>
                </div>
                
                <nav className="sidebar-nav">
                    <NavLink to="/" className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                        <LayoutDashboard size={20} />
                        <span>Dashboard</span>
                    </NavLink>
                    <NavLink to="/incidents" className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                        <AlertTriangle size={20} />
                        <span>Incidents</span>
                    </NavLink>
                    <NavLink to="/resources" className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                        <Package size={20} />
                        <span>Resources</span>
                    </NavLink>
                    <NavLink to="/responses" className={({ isActive }) => isActive ? 'nav-item active' : 'nav-item'}>
                        <Activity size={20} />
                        <span>Responses</span>
                    </NavLink>
                </nav>

                <div className="sidebar-footer">
                    <div className="user-info">
                        <div className="user-avatar">
                            {user?.email?.charAt(0).toUpperCase() || 'U'}
                        </div>
                        <div className="user-details">
                            <span className="user-email">{user?.email}</span>
                            <span className="user-role">{user?.role || 'Official'}</span>
                        </div>
                    </div>
                    <button onClick={handleLogout} className="btn-logout">
                        <LogOut size={20} />
                        <span>Logout</span>
                    </button>
                </div>
            </aside>

            <main className="main-content">
                <Outlet />
            </main>
        </div>
    );
};
