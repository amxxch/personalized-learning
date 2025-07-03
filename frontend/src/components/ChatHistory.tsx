import React, { useRef, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'

import { MdLogout } from "react-icons/md";
import { MdQuiz } from "react-icons/md";

import { Message } from '../dto/response';
import { useAuth } from '../context/AuthContext';


interface ChatHistoryProps {
    initialMessages: Message[];
    courseId: number | null;
    skillId: number | null;
}


const ChatHistory = ({ initialMessages, courseId, skillId } : ChatHistoryProps) => {

  const [messages, setMessages] = useState<Message[]>(initialMessages);
  const [loading, setLoading] = useState(false);

  const [input, setInput] = useState('');
  const [datetime, setDatetime] = useState(new Date().toLocaleString());

  const { userId, userToken } = useAuth();

  const messagesEndRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);


  const handleQuit = () => {
    navigate(`/course/overview/${courseId}`, { replace: true });
  }

  const handleReviewLesson = () => {
    navigate(`/course/${courseId}/review/${skillId}`, { replace: true });
  }

  const handleInputSend = async () => {
      const question: Message = {
        sender: 'USER',
        type: 'TEXT',
        content: input,
      }
      setMessages(prevMessages => [...prevMessages, question]);

      if (!input.trim()) {
        const warningMessage: Message = {
          sender: 'ASSISTANT',
          type: 'TEXT',
          content: 'Please enter a question before sending.'
        };
        setMessages(prevMessages => [...prevMessages, warningMessage]);
        return;
      }
      
      // send api
      setLoading(true);

      try {
        const response = await axios.get('http://localhost:8080/api/v1/learning/ask-questions', {
          headers: {
            Authorization: `Bearer ${userToken}`
          },
          params: {
            userId: userId,
            skillId: skillId,
            question: input,
            review: false
          }
        });
        
        const data = response.data;

        const answer: Message = {
          sender: 'ASSISTANT',
          type: 'GPT',
          content: data.message
        };
        setMessages(prevMessages => [...prevMessages, answer]);

      } catch (error) {
        console.error('Error:', error);
        const errorMessage: Message = {
          sender: 'ASSISTANT',
          type: 'TEXT',
          content: 'An error occurred while processing your question. Please try again.'
        };
        setMessages(prevMessages => [...prevMessages, errorMessage]);
      } finally {
        setLoading(false);
        setInput('');
      }
  }
  
    return (
      <div style={{ height: 'calc(100vh - 64px)' }} className="flex flex-col p-4 overflow-y-auto">

        {/* Title */}
        <div className="flex-1 overflow-y-auto space-y-4">
        <div className="text-center mb-10">
          <p className="text-xl text-gray-600 font-semibold mb-1">C++ Courses</p>
          <p className='text-gray-500'>{datetime}</p>
        </div>

        {/* Messages */}
        {messages.map((msg, idx) => (
          <div key={idx} className={`message ${msg.sender === 'USER' ? 'chat-end' : 'chat-start'}`}>
            <div className={`chat-bubble ${msg.type === 'QUIZ' ? 'bg-yellow-200 text-black text-lg ml-5' :
                msg.sender === 'USER' ? 'bg-orange-400 text-white text-lg mr-5' : 'bg-gray-300 text-black text-lg ml-5'}`}>

              {msg.topic && <p className="font-bold mb-2">{msg.topic}</p>}
              {(msg.type === 'TEXT' || msg.type === 'GPT' || msg.type === 'QUIZ')  && <ReactMarkdown>{msg.content}</ReactMarkdown>}
              {msg.type === 'IMAGE' && <img src={msg.content} alt="IMAGE" className="w-32 h-32 object-cover" />}
              {msg.type === 'VIDEO' && <video src={msg.content} controls className="w-32 h-32 object-cover"></video>}
              {msg.type === 'CODE' && 
              <SyntaxHighlighter language="cpp" showLineNumbers>
                {msg.content}
              </SyntaxHighlighter>}
            </div>
          </div>
        ))}

        {loading && (
          <div className="chat-start">
            <div className="chat-bubble bg-gray-300 text-black text-lg ml-5">
              <span className="loading loading-dots loading-md"></span>
            </div>
          </div>
        )}

        {/* Scroll to bottom */}
        <div ref={messagesEndRef} />
        </div>


        {/* Decision-making Buttons */}
        <div className="flex justify-center mt-4">
          <div className="flex flex-wrap gap-4">
              <div className="options mt-2 ml-5 mr-2">
                  <button 
                    onClick={handleQuit} 
                    className="btn btn-danger btn-md text-lg mr-2 px-8"
                  >
                    Quit
                    <MdLogout className='text-2xl'/>
                  </button>
                  <button 
                    onClick={handleReviewLesson} 
                    className="btn btn-secondary btn-md text-lg mr-2 px-6"
                  >
                    Review Lesson
                    <MdQuiz className='text-2xl mb-1'/>
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
              onKeyDown={(e) => e.key === 'Enter' && handleInputSend()}
              placeholder="Ask a question"
            />
            <button 
              className="btn btn-accent" 
              onClick={handleInputSend}
            >
                Send
            </button>
        </div>
      </div>

    );
  }
  
export default ChatHistory;