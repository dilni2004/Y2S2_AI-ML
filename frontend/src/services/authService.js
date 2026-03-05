import axios from 'axios';

const API_BASE = '/api/auth';

export const login = async (username, password) => {
    const response = await axios.post(`${API_BASE}/login`, { username, password });
    return response.data;
};
