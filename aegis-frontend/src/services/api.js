const BASE_URL = '/api';

function getAuthToken() {
    return localStorage.getItem('token');
}

function getHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    const token = getAuthToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

export async function login(email, password) {
    const res = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    });
    if (!res.ok) throw new Error('Login failed');
    return res.json();
}

export async function register(user) {
    const res = await fetch(`${BASE_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    });
    if (!res.ok) throw new Error('Registration failed');
    return res.json();
}

export async function fetchIncidents() {
    const res = await fetch(`${BASE_URL}/incidents`, { headers: getHeaders() });
    if (!res.ok) throw new Error('Failed to fetch incidents');
    return res.json();
}

export async function createIncident(incident) {
    const res = await fetch(`${BASE_URL}/incidents`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify(incident)
    });
    if (!res.ok) throw new Error('Failed to create incident');
    return res.json();
}

export async function fetchResources() {
    const res = await fetch(`${BASE_URL}/resources`, { headers: getHeaders() });
    if (!res.ok) throw new Error('Failed to fetch resources');
    return res.json();
}

export async function createResource(resource) {
    const res = await fetch(`${BASE_URL}/resources`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify(resource)
    });
    if (!res.ok) throw new Error('Failed to create resource');
    return res.json();
}

export async function fetchResponses() {
    const res = await fetch(`${BASE_URL}/responses`, { headers: getHeaders() });
    if (!res.ok) throw new Error('Failed to fetch responses');
    return res.json();
}

export async function createResponse(response) {
    const res = await fetch(`${BASE_URL}/responses`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify(response)
    });
    if (!res.ok) throw new Error('Failed to create response');
    return res.json();
}
