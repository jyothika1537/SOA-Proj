const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

class ApiError extends Error {
    constructor(status, message) {
        super(message);
        this.status = status;
    }
}

function getAuthToken() {
    return localStorage.getItem('token');
}

function getHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    const token = getAuthToken();
    if (token) headers.Authorization = `Bearer ${token}`;
    return headers;
}

async function request(path, options = {}) {
    const response = await fetch(`${BASE_URL}${path}`, { ...options, headers: { ...getHeaders(), ...(options.headers || {}) } });
    if (response.status === 401) { localStorage.removeItem('token'); localStorage.removeItem('role'); localStorage.removeItem('email'); window.dispatchEvent(new Event('auth:unauthorized')); }
    if (!response.ok) { let message = 'Request failed'; try { message = (await response.json()).message || message; } catch {} throw new ApiError(response.status, message); }
    return response.status === 204 ? null : response.json();
}

export async function login(email, password) {
    return request('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) });
}

export async function register(user) {
    return request('/auth/register', { method: 'POST', body: JSON.stringify(user) });
}

export async function fetchIncidents() { return request('/incidents'); }
export async function createIncident(incident) { return request('/incidents', { method: 'POST', body: JSON.stringify(incident) }); }
export async function updateIncident(id, incident) { return request(`/incidents/${id}`, { method: 'PUT', body: JSON.stringify(incident) }); }
export async function updateIncidentStatus(id, incidentStatus) { return request(`/incidents/${id}/status`, { method: 'PATCH', body: JSON.stringify({ incidentStatus }) }); }
export async function deleteIncident(id) { return request(`/incidents/${id}`, { method: 'DELETE' }); }

export async function fetchResources() { return request('/resources'); }
export async function createResource(resource) { return request('/resources', { method: 'POST', body: JSON.stringify(resource) }); }
export async function updateResource(id, resource) { return request(`/resources/${id}`, { method: 'PUT', body: JSON.stringify(resource) }); }
export async function deleteResource(id) { return request(`/resources/${id}`, { method: 'DELETE' }); }

export async function fetchResponses() { return request('/responses'); }
export async function createResponse(response) { return request('/responses', { method: 'POST', body: JSON.stringify(response) }); }
export async function updateResponseStatus(id, responseStatus) { return request(`/responses/${id}/status`, { method: 'PATCH', body: JSON.stringify({ responseStatus }) }); }
export async function deleteResponse(id) { return request(`/responses/${id}`, { method: 'DELETE' }); }
