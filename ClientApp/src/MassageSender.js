// src/MessageSender.js
import React, { useState } from 'react';
import axios from 'axios';

const MessageSender = () => {
    const [message, setMessage] = useState('');
    const [response, setResponse] = useState('');

    const sendMessage = async () => {
            const res = await axios.post('http://localhost:5005/api/messages', message, {
                headers: {
                    'Content-Type': 'text/plain',
                },
            });
            setResponse(res.data || 'No response received');

    };

    return (
        <div>
            <h1>Kafka Message Sender</h1>
            <textarea
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="Enter your message here..."
                rows={4}
                cols={50}
            />
            <br />
            <button onClick={sendMessage}>Send Message</button>
            <h2>Response:</h2>
            <p>{response}</p>
        </div>
    );
};

export default MessageSender;
