import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getDepartmentTickets } from '../../services/ticketService';

const AdminTicketDashboard = () => {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchTickets();
    }, []);

    const fetchTickets = async () => {
        try {
            const data = await getDepartmentTickets();
            setTickets(data);
        } catch (error) {
            console.error('Failed to load tickets', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <h1 className="text-2xl font-bold text-gray-900 mb-6">Department Tickets</h1>
            {loading ? (
                <div>Loading...</div>
            ) : (
                <div className="bg-white shadow overflow-hidden sm:rounded-md">
                    <ul className="divide-y divide-gray-200">
                        {tickets.map(ticket => (
                            <li key={ticket.id}>
                                <Link to={`/admin/tickets/${ticket.id}`} className="block hover:bg-gray-50">
                                    <div className="px-4 py-4 sm:px-6">
                                        <div className="flex items-center justify-between">
                                            <p className="text-sm font-medium text-indigo-600 truncate">
                                                {ticket.inquiryType} - {ticket.studentName}
                                            </p>
                                            <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                        ${ticket.status === 'CLOSED' ? 'bg-green-100 text-green-800' :
                                                    ticket.status === 'OPEN' ? 'bg-yellow-100 text-yellow-800' :
                                                        'bg-blue-100 text-blue-800'}`}>
                                                {ticket.status}
                                            </span>
                                        </div>
                                        <div className="mt-2 sm:flex sm:justify-between">
                                            <div className="sm:flex">
                                                <p className="flex items-center text-sm text-gray-500">
                                                    {ticket.description.substring(0, 80)}...
                                                </p>
                                            </div>
                                            <div className="mt-2 flex items-center text-sm text-gray-500 sm:mt-0">
                                                <p>{new Date(ticket.createdAt).toLocaleDateString()}</p>
                                            </div>
                                        </div>
                                    </div>
                                </Link>
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default AdminTicketDashboard;