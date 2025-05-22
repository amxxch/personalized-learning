'use client';

import React, { useRef, useState, useEffect } from 'react';
import axios from 'axios';
import { Progress } from '../dto/response';
import { GrFormNextLink } from "react-icons/gr";
import { IoReloadOutline } from "react-icons/io5";
import { MdLogout } from "react-icons/md";

interface Message {
  sender: 'user' | 'teacher';
  type: 'TEXT' | 'IMAGE' | 'VIDEO';
  content: string;
}

interface ChatBubbleProps {
    initialMessages: Message[];
}


const ChatBubble = ({ initialMessages } : ChatBubbleProps) => {
  const [messages, setMessages] = useState<Message[]>(initialMessages);
  const [loading, setLoading] = useState(false);
  const [input, setInput] = useState('');
  const [datetime, setDatetime] = useState(new Date().toLocaleString());

  const [courseId, setCourseId] = useState(1);
  const [skillId, setSkillId] = useState(1);

  const messagesEndRef = useRef<HTMLDivElement>(null);
  
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleOptionClick = async (option: 'next' | 'repeat' | 'quit') => {
    setLoading(true);

    try {
      if (option === 'next') {
        // send API request
        const response = await axios.post('http://localhost:8080/api/v1/learning/next-bubble', {
          "userId": 1,
          "courseId": 1,
          "skillId": 1,
        });
        const data = response.data;

        if (typeof data === 'string') {
          // Course completed
          setMessages([...messages, {
            sender: 'teacher',
            type: 'TEXT',
            content: data
          }]);
          // TODO: add quiz page
        } else if (data && data.content) {
          const newBubble: Message = {
            sender: 'teacher',
            type: data.contentType,
            content: data.content,
          };
          setMessages([...messages, newBubble]);
        }

      } else if (option === 'repeat') {
        // send API request
        // setMessages([...messages, 'Repeat message']);
        const response = await axios.post('http://localhost:8080/api/v1/progress/reset', {
          "userId": 1,
          "courseId": 1,
          "skillId": 1,
        });
        const data = response.data;
        console.log(data);

        const resetConfirmation: Message = {
          sender: 'teacher',
          type: 'TEXT',
          content: "Reset progress successfully. You can start over.",
        };

        const newBubble: Message = {
          sender: 'teacher',
          type: data.bubble.contentType,
          content: data.bubble.content,
        };

        setMessages([...messages, resetConfirmation, newBubble]);

      } else if (option === 'quit') {
        // send API request
        // setMessages([...messages, 'Quit message']);
        window.location.href = '/';
      }
    } catch (error) {
      console.error('Error:', error);
      setMessages([...messages, {
        sender: 'teacher',
        type: 'TEXT',
        content: 'Failed to load next bubble. Please try again.'
    }]);
    } finally {
      setLoading(false);
    }
  }

  const handleSend = async () => {
      // gpt api to ask questions
  }
  
    return (
      <div style={{ height: 'calc(100vh - 64px)' }} className="flex flex-col h-screen p-4">

        {/* Title */}
        <div className="flex-1 overflow-y-auto space-y-4">
        <div className="text-center mb-10">
          <p className="text-xl text-gray-600 font-semibold mb-1">Python Courses</p>
          <p className='text-gray-500'>{datetime}</p>
        </div>

        {/* Messages */}
        {messages.map((msg, idx) => (
          <div key={idx} className={`message ${msg.sender === 'user' ? 'chat-end' : 'chat-start'}`}>
            <div className={`chat-bubble ${msg.sender === 'user' ? 'bg-orange-400 text-white text-lg mr-5' : 'bg-gray-300 text-black text-lg ml-5'}`}>
              {msg.type === 'TEXT' && msg.content}
              {msg.type === 'IMAGE' && <img src={msg.content} alt="IMAGE" className="w-32 h-32 object-cover" />}
              {msg.type === 'VIDEO' && <video src={msg.content} controls className="w-32 h-32 object-cover"></video>}
            </div>
          </div>
        ))}
        </div>

        <div ref={messagesEndRef} />

        {/* Decision-making Buttons */}
        <div className="flex justify-center mt-4">
          <div className="flex flex-wrap gap-4">
            <div className="options mt-2 ml-5 mr-2">
                <button 
                  onClick={() => handleOptionClick('quit')} 
                  className="btn btn-danger btn-md text-lg mr-2 px-8"
                >
                  Quit
                  <MdLogout className='text-2xl'/>
                </button>
                <button 
                  onClick={() => handleOptionClick('repeat')} 
                  className="btn btn-secondary btn-md text-lg mr-2 px-8"
                >
                  Still Unsure
                  <IoReloadOutline className='text-2xl'/>
                </button>
                <button 
                  onClick={() => handleOptionClick('next')} 
                  className="btn btn-primary btn-md text-lg px-8"
                >
                  Next
                  <GrFormNextLink className='text-2xl'/>
                </button>
            </div>
            </div>
        </div>

        {/* Search bar */}
        <div className="flex items-center gap-2 mt-4 mx-5">
            <input
              className="input input-bordered flex-1"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSend()}
              placeholder="Ask a question"
            />
            <button className="btn btn-accent" onClick={handleSend}>Send</button>
        </div>
      </div>
    );
  }
  
export default ChatBubble;