// src/utils/helpers.js

/**
 * Format a date string into a human-readable format.
 * @param {string} dateString - ISO date string
 * @returns {string} formatted date (e.g., "Mar 5, 2026, 10:30 AM")
 */
export const formatDate = (dateString) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    }).format(date);
};

/**
 * Truncate text to a specified length and add ellipsis.
 * @param {string} text - original text
 * @param {number} length - max length
 * @returns {string} truncated text
 */
export const truncateText = (text, length = 100) => {
    if (!text) return '';
    return text.length > length ? text.substring(0, length) + '…' : text;
};

/**
 * Get Tailwind CSS classes for a ticket status badge.
 * @param {string} status - ticket status (OPEN, IN_PROGRESS, etc.)
 * @returns {string} Tailwind classes
 */
export const getStatusBadgeClass = (status) => {
    const classes = {
        OPEN: 'bg-yellow-100 text-yellow-800',
        AWAITING_INFO: 'bg-purple-100 text-purple-800',
        IN_PROGRESS: 'bg-blue-100 text-blue-800',
        RESOLVED_PENDING_APPROVAL: 'bg-orange-100 text-orange-800',
        CLOSED: 'bg-green-100 text-green-800',
        REJECTED: 'bg-red-100 text-red-800',
    };
    return classes[status] || 'bg-gray-100 text-gray-800';
};