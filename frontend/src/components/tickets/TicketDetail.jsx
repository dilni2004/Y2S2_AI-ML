import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getTicketById, addAttachment, deleteTicket, performAdminAction } from '../../services/ticketService';
import { useAuth } from '../../context/AuthContext';

const TicketDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth(); // contains role, departmentId, etc.
    const [ticket, setTicket] = useState(null);
    const [loading, setLoading] = useState(true);
    const [commentText, setCommentText] = useState('');
    const [internalComment, setInternalComment] = useState(false);
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [actionLoading, setActionLoading] = useState(false);

    useEffect(() => {
        fetchTicket();
    }, [id]);

    const fetchTicket = async () => {
        try {
            const data = await getTicketById(id);
            setTicket(data);
        } catch (error) {
            console.error('Failed to load ticket', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFileChange = (e) => {
        setSelectedFiles([...e.target.files]);
    };

    const handleAddAttachment = async () => {
        if (selectedFiles.length === 0) return;
        setActionLoading(true);
        try {
            await addAttachment(id, selectedFiles);
            await fetchTicket(); // refresh
            setSelectedFiles([]);
        } catch (error) {
            alert('Failed to upload files');
        } finally {
            setActionLoading(false);
        }
    };

    const handleDelete = async () => {
        if (!window.confirm('Are you sure you want to delete this ticket?')) return;
        try {
            await deleteTicket(id);
            navigate('/tickets');
        } catch (error) {
            alert('Failed to delete ticket');
        }
    };

    const handleAdminAction = async (action, additionalData = {}) => {
        setActionLoading(true);
        try {
            await performAdminAction({
                ticketId: ticket.id,
                action,
                comment: commentText,
                internal: internalComment,
                ...additionalData
            });
            await fetchTicket();
            setCommentText('');
            setInternalComment(false);
        } catch (error) {
            alert('Action failed');
        } finally {
            setActionLoading(false);
        }
    };

    if (loading) return <div className="text-center py-10">Loading...</div>;
    if (!ticket) return <div className="text-center py-10">Ticket not found</div>;

    const isStudent = user.role === 'STUDENT';
    const isDeptAdmin = user.role === 'DEPT_ADMIN';
    const isSuperAdmin = user.role === 'SUPER_ADMIN';

    return (
        <div className="max-w-4xl mx-auto px-4 py-8">
            <div className="bg-white shadow overflow-hidden sm:rounded-lg">
                {/* Header */}
                <div className="px-4 py-5 sm:px-6 flex justify-between items-center">
                    <div>
                        <h3 className="text-lg leading-6 font-medium text-gray-900">Ticket #{ticket.id}</h3>
                        <p className="mt-1 max-w-2xl text-sm text-gray-500">Created on {new Date(ticket.createdAt).toLocaleString()}</p>
                    </div>
                    <span className={`px-3 py-1 inline-flex text-sm leading-5 font-semibold rounded-full 
            ${ticket.status === 'CLOSED' ? 'bg-green-100 text-green-800' :
                            ticket.status === 'OPEN' ? 'bg-yellow-100 text-yellow-800' :
                                ticket.status === 'AWAITING_INFO' ? 'bg-purple-100 text-purple-800' :
                                    ticket.status === 'RESOLVED_PENDING_APPROVAL' ? 'bg-orange-100 text-orange-800' :
                                        'bg-blue-100 text-blue-800'}`}>
                        {ticket.status}
                    </span>
                </div>

                {/* Student Info */}
                <div className="border-t border-gray-200 px-4 py-5 sm:px-6">
                    <dl className="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
                        <div>
                            <dt className="text-sm font-medium text-gray-500">Student ID</dt>
                            <dd className="mt-1 text-sm text-gray-900">{ticket.studentIdNumber}</dd>
                        </div>
                        <div>
                            <dt className="text-sm font-medium text-gray-500">Name</dt>
                            <dd className="mt-1 text-sm text-gray-900">{ticket.studentName}</dd>
                        </div>
                        <div>
                            <dt className="text-sm font-medium text-gray-500">Phone</dt>
                            <dd className="mt-1 text-sm text-gray-900">{ticket.studentPhone}</dd>
                        </div>
                        <div>
                            <dt className="text-sm font-medium text-gray-500">Email</dt>
                            <dd className="mt-1 text-sm text-gray-900">{ticket.studentEmail}</dd>
                        </div>
                        <div>
                            <dt className="text-sm font-medium text-gray-500">Inquiry Type</dt>
                            <dd className="mt-1 text-sm text-gray-900">{ticket.inquiryType}</dd>
                        </div>
                        <div>
                            <dt className="text-sm font-medium text-gray-500">Department</dt>
                            <dd className="mt-1 text-sm text-gray-900">{ticket.departmentName}</dd>
                        </div>
                    </dl>
                </div>

                {/* Description */}
                <div className="border-t border-gray-200 px-4 py-5 sm:px-6">
                    <dt className="text-sm font-medium text-gray-500">Description</dt>
                    <dd className="mt-1 text-sm text-gray-900 whitespace-pre-wrap">{ticket.description}</dd>
                </div>

                {/* Attachments */}
                {ticket.attachments?.length > 0 && (
                    <div className="border-t border-gray-200 px-4 py-5 sm:px-6">
                        <dt className="text-sm font-medium text-gray-500 mb-2">Attachments</dt>
                        <dd className="mt-1 text-sm text-gray-900">
                            <ul className="border border-gray-200 rounded-md divide-y divide-gray-200">
                                {ticket.attachments.map(file => (
                                    <li key={file.id} className="pl-3 pr-4 py-3 flex items-center justify-between text-sm">
                                        <div className="w-0 flex-1 flex items-center">
                                            <span className="ml-2 flex-1 w-0 truncate">{file.fileName}</span>
                                        </div>
                                        <div className="ml-4 flex-shrink-0">
                                            <a href={`/uploads/${file.fileName}`} target="_blank" rel="noopener noreferrer" className="font-medium text-indigo-600 hover:text-indigo-500">
                                                Download
                                            </a>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        </dd>
                    </div>
                )}

                {/* Comments */}
                <div className="border-t border-gray-200 px-4 py-5 sm:px-6">
                    <dt className="text-sm font-medium text-gray-500 mb-4">Comments</dt>
                    <dd className="mt-1 text-sm text-gray-900 space-y-4">
                        {ticket.comments?.length === 0 ? (
                            <p className="text-gray-500">No comments yet.</p>
                        ) : (
                            ticket.comments.map(comment => (
                                <div key={comment.id} className={`bg-gray-50 p-3 rounded ${comment.internal ? 'border-l-4 border-yellow-400' : ''}`}>
                                    <div className="flex justify-between">
                                        <span className="font-medium">{comment.authorName}</span>
                                        <span className="text-xs text-gray-500">{new Date(comment.createdAt).toLocaleString()}</span>
                                    </div>
                                    <p className="mt-1">{comment.content}</p>
                                    {comment.internal && <span className="text-xs text-yellow-600 mt-1 inline-block">Internal note</span>}
                                </div>
                            ))
                        )}
                    </dd>
                </div>

                {/* Action area */}
                <div className="border-t border-gray-200 px-4 py-5 sm:px-6">
                    {/* Student actions */}
                    {isStudent && (
                        <div className="space-y-4">
                            {ticket.status === 'AWAITING_INFO' && (
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">Upload additional files</label>
                                    <input type="file" multiple onChange={handleFileChange} className="mb-2" />
                                    <button
                                        onClick={handleAddAttachment}
                                        disabled={actionLoading || selectedFiles.length === 0}
                                        className="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 disabled:opacity-50"
                                    >
                                        Upload
                                    </button>
                                </div>
                            )}
                            {(ticket.status === 'OPEN' || ticket.status === 'AWAITING_INFO') && (
                                <button
                                    onClick={handleDelete}
                                    className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
                                >
                                    Delete Ticket
                                </button>
                            )}
                        </div>
                    )}

                    {/* Admin actions (Dept Admin) */}
                    {isDeptAdmin && !ticket.pendingApproval && (
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700">Add Comment</label>
                                <textarea
                                    rows={2}
                                    value={commentText}
                                    onChange={(e) => setCommentText(e.target.value)}
                                    className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3"
                                />
                                <div className="mt-2 flex items-center">
                                    <input
                                        type="checkbox"
                                        checked={internalComment}
                                        onChange={(e) => setInternalComment(e.target.checked)}
                                        className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                                    />
                                    <label className="ml-2 block text-sm text-gray-900">Internal note (not visible to student)</label>
                                </div>
                                <button
                                    onClick={() => handleAdminAction('ADD_COMMENT')}
                                    disabled={actionLoading || !commentText.trim()}
                                    className="mt-2 px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 disabled:opacity-50"
                                >
                                    Post Comment
                                </button>
                            </div>

                            <div className="flex flex-wrap gap-2">
                                <select
                                    onChange={(e) => handleAdminAction('UPDATE_STATUS', { status: e.target.value })}
                                    className="border border-gray-300 rounded px-3 py-2"
                                    defaultValue=""
                                >
                                    <option value="" disabled>Change Status</option>
                                    <option value="OPEN">Open</option>
                                    <option value="IN_PROGRESS">In Progress</option>
                                    <option value="AWAITING_INFO">Awaiting Info</option>
                                </select>

                                <button
                                    onClick={() => handleAdminAction('SUBMIT_APPROVAL')}
                                    className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
                                >
                                    Submit for Approval
                                </button>

                                {/* Reassign department – could be a modal, simplified here */}
                                <button
                                    onClick={() => {
                                        const newDeptId = prompt('Enter new department ID:');
                                        if (newDeptId) handleAdminAction('REASSIGN', { newDepartmentId: parseInt(newDeptId) });
                                    }}
                                    className="px-4 py-2 bg-yellow-600 text-white rounded hover:bg-yellow-700"
                                >
                                    Reassign Department
                                </button>
                            </div>
                        </div>
                    )}

                    {/* Super Admin actions */}
                    {isSuperAdmin && ticket.pendingApproval && (
                        <div className="space-y-4">
                            <p className="text-sm text-gray-700">This ticket is pending final approval.</p>
                            <div className="flex gap-2">
                                <button
                                    onClick={() => handleAdminAction('APPROVE')}
                                    className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
                                >
                                    Approve & Close
                                </button>
                                <button
                                    onClick={() => handleAdminAction('SEND_BACK')}
                                    className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
                                >
                                    Send Back to Department
                                </button>
                            </div>
                        </div>
                    )}

                    {isSuperAdmin && !ticket.pendingApproval && (
                        <div>
                            <button
                                onClick={() => handleAdminAction('ADD_COMMENT')}
                            // similar to dept admin comment box
                            >
                                Add Note
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TicketDetail;