import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import StudentTicketList from './components/tickets/StudentTicketList';
import CreateTicket from './components/tickets/CreateTicket';
import TicketDetail from './components/tickets/TicketDetail';
import AdminTicketDashboard from './components/tickets/AdminTicketDashboard';
import SuperAdminDashboard from './components/tickets/SuperAdminDashboard';

function App() {
    return (
        <AuthProvider>
            <Routes>
                <Route path="/login" element={<LoginPage />} />

                {/* Student routes */}
                <Route element={<ProtectedRoute allowedRoles={['STUDENT']} />}>
                    <Route path="/tickets" element={<StudentTicketList />} />
                    <Route path="/tickets/new" element={<CreateTicket />} />
                    <Route path="/tickets/:id" element={<TicketDetail />} />
                </Route>

                {/* Department admin routes */}
                <Route element={<ProtectedRoute allowedRoles={['DEPT_ADMIN']} />}>
                    <Route path="/admin" element={<AdminTicketDashboard />} />
                    <Route path="/admin/tickets/:id" element={<TicketDetail />} />
                </Route>

                {/* Super admin routes */}
                <Route element={<ProtectedRoute allowedRoles={['SUPER_ADMIN']} />}>
                    <Route path="/super" element={<SuperAdminDashboard />} />
                    <Route path="/super/tickets/:id" element={<TicketDetail />} />
                </Route>

                <Route path="/" element={<Navigate to="/tickets" />} />
                <Route path="*" element={<Navigate to="/" />} />
            </Routes>
        </AuthProvider>
    );
}

export default App;