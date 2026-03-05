import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getPendingApprovalTickets } from '../../services/ticketService';

const SuperAdminDashboard = () => {
    const [pendingTickets, setPendingTickets] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchPending();
    }, []);

    const fetchPending = async () => {
        try {
            const data = await getPendingApprovalTickets();
            setPendingTickets(data);
        } catch (error) {
            console.error('Failed to load', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <h1 className="text-2xl font-bold text-gray-900 mb-6">Pending Approval Tickets</h1>
            {loading ? <div>Loading...</div> : (
                <div className="bg-white shadow overflow-hidden sm:rounded-md">
                    {pendingTickets.length === 0 ? (
                        <p className="p-4 text-gray-500">No tickets pending approval.</p>
                    ) : (
                        <ul className="divide-y divide-gray-200">
                            {pendingTickets.map(ticket => (
                                <li key={ticket.id}>
                                    <Link to={`/super/tickets/${ticket.id}`} className="block hover:bg-gray-50">
                                        <div className="px-4 py-4 sm:px-6">
                                            <div className="flex items-center justify-between">
                                                <p className="text-sm font-medium text-indigo-600 truncate">
                                                    {ticket.inquiryType} - {ticket.departmentName}
                                                </p>
                                                <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-orange-100 text-orange-800">
                                                    Pending Approval
                                                </span>
                                            </div>
                                            <div className="mt-2 sm:flex sm:justify-between">
                                                <p className="text-sm text-gray-500">{ticket.studentName}</p>
                                                <p className="text-sm text-gray-500">{new Date(ticket.createdAt).toLocaleDateString()}</p>
                                            </div>
                                        </div>
                                    </Link>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            )}
        </div>
    );
};

export default SuperAdminDashboard;