'use client';

import React, { useRef, useState, useEffect } from 'react';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { Message } from '../dto/response';
import { GrFormNextLink } from "react-icons/gr";
import { IoReloadOutline } from "react-icons/io5";
import { MdLogout } from "react-icons/md";

interface ChatBubbleProps {
    initialMessages: Message[];
    initialSkillId?: number;
    initialBubbleId?: number;
}


const ChatBubble = ({ initialMessages, initialSkillId = 1, initialBubbleId = 1 } : ChatBubbleProps) => {
  const [messages, setMessages] = useState<Message[]>(initialMessages);
  const [loading, setLoading] = useState(false);
  const [input, setInput] = useState('');
  const [datetime, setDatetime] = useState(new Date().toLocaleString());

  const [userId] = useState(1);
  const [courseId] = useState(1);
  const [skillId, setSkillId] = useState(initialSkillId);
  const [bubbleId, setBubbleId] = useState(initialBubbleId);

  const messagesEndRef = useRef<HTMLDivElement>(null);
  
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    console.log('Skill ID:', skillId);
    console.log('Bubble ID:', bubbleId);
  }, [skillId, bubbleId]);

  const handleOptionClick = async (option: 'next' | 'rephrase' | 'quit') => {
    setLoading(true);

    try {
      if (option === 'next') {
        // send API request
        const response = await axios.post('http://localhost:8080/api/v1/learning/next-bubble', {
          "userId": userId,
          "courseId": courseId,
          "skillId": skillId,
        });
        const data = response.data;

        console.log(data)

        if (data.status === 'COMPLETED') {
          const completeMessage: Message = {
            sender: 'CHATBOT',
            type: 'TEXT',
            content: data.message
          };
          setMessages(prevMessages => [...prevMessages, completeMessage]);

          // TODO: add quiz page

        } else if (data.status === 'CONTINUE') {
          const newBubble: Message = {
            sender: 'CHATBOT',
            type: data.nextBubble.contentType,
            content: data.nextBubble.content,
            topic: data.nextBubble.topic,
            skillId: data.nextBubble.skillId,
            skillName: data.nextBubble.skillName,
            bubbleOrder: data.nextBubble.bubbleOrder
          };

          // Update state with new bubble and progress
          setSkillId(data.nextBubble.skillId);
          setBubbleId(data.nextBubble.bubbleId);
          setMessages(prevMessages => [...prevMessages, newBubble]);

          // TODO: find a way to detect if this is the last bubble and call for quiz page
          // Actually, might be able to handle it in backend learning endpoint

          console.log("Bubble ID:", bubbleId);
          console.log("Skill ID:", skillId);
        }

      } else if (option === 'rephrase') {
        // Create still unsure message from user's side
        const stillUnsureMessage: Message = {
          sender: 'USER',
          type: 'TEXT',
          content: "I'm still unsure. Can you elaborate more?"
        }
        setMessages(prevMessages => [...prevMessages, stillUnsureMessage]);

        // send API request to rephrase the lesson
        const response = await axios.post('http://localhost:8080/api/v1/learning/rephrase', {
          "userId": userId,
          "bubbleId": bubbleId,
        });
        const data = response.data;
        console.log(data);

        if (data.status === 'COMPLETED') {
          const answer: Message = {
            sender: 'CHATBOT',
            type: 'GPT',
            content: data.message
          };
          setMessages(prevMessages => [...prevMessages, answer]);
        }

      } else if (option === 'quit') {
        // send API request
        // setMessages([...messages, 'Quit message']);
        window.location.href = '/';
      }
    } catch (error) {
      console.error('Error:', error);
      const errorMessage: Message = {
        sender: 'CHATBOT',
        type: 'TEXT',
        content: 'An error occurred while processing your request. Please try again.'
      };
      setMessages(prevMessages => [...prevMessages, errorMessage]);
    } finally {
      setLoading(false);
    }
  }

  const handleSend = async () => {
      // post message
      const question: Message = {
        sender: 'USER',
        type: 'TEXT',
        content: input,
      }
      setMessages(prevMessages => [...prevMessages, question]);

      if (!input.trim()) {
        const warningMessage: Message = {
          sender: 'CHATBOT',
          type: 'TEXT',
          content: 'Please enter a question before sending.'
        };
        setMessages(prevMessages => [...prevMessages, warningMessage]);
      }
      
      // send api
      setLoading(true);
      try {
        const response = await axios.post('http://localhost:8080/api/v1/learning/ask-questions', {
          "userId": userId,
          "skillId": skillId,
          "question": input,
        });
        const data = response.data;

        const answer: Message = {
          sender: 'CHATBOT',
          type: 'GPT',
          content: data.message
        };
        setMessages(prevMessages => [...prevMessages, answer]);

      } catch (error) {
        console.error('Error:', error);
        const errorMessage: Message = {
          sender: 'CHATBOT',
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
          <p className="text-xl text-gray-600 font-semibold mb-1">Python Courses</p>
          <p className='text-gray-500'>{datetime}</p>
        </div>

        {/* Messages */}
        {messages.map((msg, idx) => (
          <div key={idx} className={`message ${msg.sender === 'USER' ? 'chat-end' : 'chat-start'}`}>
            <div className={`chat-bubble ${msg.sender === 'USER' ? 'bg-orange-400 text-white text-lg mr-5' : 'bg-gray-300 text-black text-lg ml-5'}`}>
              {msg.skillName && msg.skillId && (msg.bubbleOrder && msg.bubbleOrder === 1) &&
               <p className="font-bold mb-2">Chapter {msg.skillId}: {msg.skillName}</p>
               }
              {msg.topic && <p className="font-bold mb-2">{msg.topic}</p>}
              {msg.type === 'TEXT' && msg.content}
              {msg.type === 'IMAGE' && <img src={msg.content} alt="IMAGE" className="w-32 h-32 object-cover" />}
              {msg.type === 'VIDEO' && <video src={msg.content} controls className="w-32 h-32 object-cover"></video>}
              {msg.type === 'CODE' && 
              <SyntaxHighlighter language="cpp" showLineNumbers>
                {msg.content}
              </SyntaxHighlighter>}
              {msg.type === 'GPT' && <ReactMarkdown>{msg.content}</ReactMarkdown>}
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
                  onClick={() => handleOptionClick('quit')} 
                  className="btn btn-danger btn-md text-lg mr-2 px-8"
                >
                  Quit
                  <MdLogout className='text-2xl'/>
                </button>
                <button 
                  onClick={() => handleOptionClick('rephrase')} 
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