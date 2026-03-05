import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const ProtectedRoute = ({ allowedRoles }) => {
    const { user, loading } = useAuth();

    if (loading) {
        return <div className="text-center py-10">Loading...</div>; // or a spinner
    }

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
        // Redirect to appropriate default page based on role
        if (user.role === 'STUDENT') return <Navigate to="/tickets" replace />;
        if (user.role === 'DEPT_ADMIN') return <Navigate to="/admin" replace />;
        if (user.role === 'SUPER_ADMIN') return <Navigate to="/super" replace />;
        return <Navigate to="/" replace />;
    }

    return <Outlet />;
};


export default ProtectedRoute;