import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="bg-indigo-600 text-white p-4">
            <div className="container mx-auto flex justify-between items-center">
                <Link to="/" className="text-xl font-bold">SFS EDUConnect</Link>
                <div className="flex items-center space-x-4">
                    <span>Welcome, {user?.fullName}</span>
                    <button
                        onClick={handleLogout}
                        className="bg-indigo-800 hover:bg-indigo-900 px-3 py-1 rounded"
                    >
                        Logout
                    </button>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;