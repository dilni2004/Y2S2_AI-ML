import axios from 'axios';
import api from './axiosConfig';

const API_BASE = '/api/tickets';

export const getMyTickets = () => api.get('/tickets/my').then(res => res.data);
export const getDepartments = () => api.get('/departments').then(res => res.data);
export const getTicketById = (id) => api.get(`/tickets/${id}`).then(res => res.data);
export const createTicket = (data) => api.post('/tickets', data).then(res => res.data);
export const addAttachment = (ticketId, files) => {
    const formData = new FormData();
    files.forEach(file => formData.append('files', file));
    return api.post(`/tickets/${ticketId}/attachments`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    }).then(res => res.data);
};
export const deleteTicket = (id) => api.delete(`/tickets/${id}`);
export const getDepartmentTickets = () => api.get('/tickets/department').then(res => res.data);
export const getAllTickets = () => api.get('/tickets/all').then(res => res.data);
export const getPendingApprovalTickets = () => api.get('/tickets/pending-approval').then(res => res.data);
export const performAdminAction = (action) => api.post('/tickets/action', action).then(res => res.data);