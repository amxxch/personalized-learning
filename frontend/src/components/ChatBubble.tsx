'use client';

import React, { useRef, useState, useEffect } from 'react';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'

import { GrFormNextLink } from "react-icons/gr";
import { IoReloadOutline } from "react-icons/io5";
import { MdLogout } from "react-icons/md";

import { Message, QuizChoice } from '../dto/response';
import QuizTimeAnim from './QuizTimeAnim';


interface ChatBubbleProps {
    initialMessages: Message[];
    initialSkillId?: number;
    initialBubbleId?: number;
}


const ChatBubble = ({ initialMessages, initialSkillId = 1, initialBubbleId = 1 } : ChatBubbleProps) => {

  const maxQuizQuestion = 5;

  const [messages, setMessages] = useState<Message[]>(initialMessages);
  const [loading, setLoading] = useState(false);

  const [quizTime, setQuizTime] = useState(false);
  const [quizEvalTime, setQuizEvalTime] = useState(false);
  const [quizTimeAnim, setQuizTimeAnim] = useState(quizTime);
  const [numOfQuiz, setNumOfQuiz] = useState(0);

  const [enabledInput, setEnabledInput] = useState(!quizTime);
  const [input, setInput] = useState('');
  const [datetime, setDatetime] = useState(new Date().toLocaleString());

  const [userId] = useState(1);
  const [courseId] = useState(1);
  const [skillId, setSkillId] = useState(initialSkillId);
  const [bubbleId, setBubbleId] = useState(initialBubbleId);
  const [quizId, setQuizId] = useState(1);

  const messagesEndRef = useRef<HTMLDivElement>(null);
  
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    const timer = setTimeout(() => {
      setQuizTimeAnim(false);
    }, 1500)

    return () => clearTimeout(timer);
  }, [quizTimeAnim]);

  useEffect(() => {
    console.log('Skill ID:', skillId);
    console.log('Bubble ID:', bubbleId);
  }, [skillId, bubbleId]);

  useEffect(() => {
    console.log('num of quiz: ', numOfQuiz);
  }, [numOfQuiz]);

  const handleLessonOptionClick = async (option: 'next' | 'rephrase' | 'quit') => {
    setLoading(true);

    try {
      if (option === 'next') {
        // send API request

        const response = await axios.get('http://localhost:8080/api/v1/learning/next-bubble', {
          params: {
            userId: userId,
            courseId: courseId,
            skillId: skillId,
          }
        });
        const data = response.data;

        console.log(data)

        if (data.status === 'COMPLETED') {
          const completeMessage: Message = {
            sender: 'ASSISTANT',
            type: 'TEXT',
            content: data.message
          };
          setMessages(prevMessages => [...prevMessages, completeMessage]);

        } else if (data.status === 'CONTINUE') {
          const newBubble: Message = {
            sender: 'ASSISTANT',
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
        } else if (data.status === 'QUIZ') {
          if (numOfQuiz === 0) {
            setQuizTimeAnim(true);
            setTimeout(() => {
              const quizTimeMessage: Message = {
                sender: 'ASSISTANT',
                type: 'QUIZ',
                content: "",
                topic: "Quiz Time! Let's answer the following question"
              };
              setMessages(prevMessages => [...prevMessages, quizTimeMessage]);

              handleNextQuiz();
            }, 1500);
          } else {
            handleNextQuiz();
          }
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
        const response = await axios.get('http://localhost:8080/api/v1/learning/rephrase', {
          params: {
            userId: userId,
            courseId: courseId
          }
        });
        
        const data = response.data;
        console.log(data);

        if (data.status === 'COMPLETED') {
          const answer: Message = {
            sender: 'ASSISTANT',
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
        sender: 'ASSISTANT',
        type: 'TEXT',
        content: 'An error occurred while processing your request. Please try again.'
      };
      setMessages(prevMessages => [...prevMessages, errorMessage]);
    } finally {
      setLoading(false);
    }
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
      }
      
      // send api
      setLoading(true);
      const url = (quizTime || numOfQuiz !== 0) ? 
      'http://localhost:8080/api/v1/quiz/ask-questions' :
      'http://localhost:8080/api/v1/learning/ask-questions';

      try {
        const response = await axios.get(url, {
          params: {
            userId: userId,
            skillId: skillId,
            question: input,
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

  const handleNextQuiz = async () => {
    setQuizTime(true);
    setLoading(true);
    setEnabledInput(false);

    try {
      const response = await axios.get('http://localhost:8080/api/v1/quiz/next-quiz-question', {
        params: {
          userId: userId,
          skillId: skillId,
        }
      });
      
      const data = response.data;

      if (!data || data === null) {
        askForQuizEvaluation();
        return;
      }

      console.log(data);
      
      let choices = '';
      for (let choice of data.quizChoices) {
        choices += `**${choice.choiceLetter}.** ${choice.content}\n\n`;
      }
      
      const question: Message = {
        sender: 'ASSISTANT',
        type: 'QUIZ',
        topic: `Quiz Question # ${numOfQuiz + 1}`,
        content: `${data.question}\n\n${choices}`,
      }

      setMessages(prevMessages => [...prevMessages, question]);
      console.log(data.questionId)
      setQuizId(data.questionId);

    } catch (error) {
      console.error('Error fetching quiz question:', error);
      const errorMessage: Message = {
        sender: 'ASSISTANT',
        type: 'TEXT',
        content: 'An error occurred while fetching the quiz question. Please try again.',
      };
      setMessages(prevMessages => [...prevMessages, errorMessage]);
      setEnabledInput(true);
    } finally {
      setLoading(false);
    }
  }

  const askForQuizEvaluation = async () => {
    setLoading(true);
    const selectedAnswer: Message = {
      sender: 'ASSISTANT',
      type: 'QUIZ',
      content: 'Generating quiz evaluation...',
    }
    setMessages(prevMessages => [...prevMessages, selectedAnswer]);

    try {
      const response = await axios.get('http://localhost:8080/api/v1/quiz/evaluate', {
        params: {
          userId: userId,
          skillId: skillId,
        }
      });
      
      const data = response.data;
      console.log(data);

      const evaluationMessage: Message = {
        sender: 'ASSISTANT',
        type: 'QUIZ',
        content: data,
      }
      setMessages(prevMessages => [...prevMessages, evaluationMessage]);

      setQuizTime(false);
      setQuizEvalTime(false);
      setEnabledInput(true);
      setNumOfQuiz(0);

    } catch (error) {
      console.error('Error evaluating quiz answer:', error);
      const errorMessage: Message = {
        sender: 'ASSISTANT',
        type: 'TEXT',
        content: 'An error occurred while evaluating your answer. Please try again.'
      };
      setMessages(prevMessages => [...prevMessages, errorMessage]);
      setQuizEvalTime(true);
    } finally {
      setLoading(false);
    }
  }

  const handleSubmitQuiz = async (answer: string) => {
    setQuizTime(false);
    const selectedAnswer: Message = {
      sender: 'USER',
      type: 'QUIZ',
      content: `Selected answer: **${answer}**`,
    }
    setMessages(prevMessages => [...prevMessages, selectedAnswer]);

    try {
      setLoading(true);
      const response = await axios.get('http://localhost:8080/api/v1/quiz/submit-answer', {
        params: {
          userId: userId,
          questionId: quizId,
          choiceLetterStr: answer,
        }
      });
      
      const data = response.data;

      console.log(data);

      const resultMessage: Message = {
        sender: 'ASSISTANT',
        type: 'QUIZ',
        content: data,
      }
      setMessages(prevMessages => [...prevMessages, resultMessage]);
      setNumOfQuiz(prev => prev + 1);
      setEnabledInput(true);

      if (numOfQuiz >= maxQuizQuestion) {
        setQuizEvalTime(true);
      }

    } catch (error) {
      console.error('Error submitting quiz answer:', error);
      const errorMessage: Message = {
        sender: 'ASSISTANT',
        type: 'TEXT',
        content: 'An error occurred while submitting your answer. Please try again.'
      };
      setMessages(prevMessages => [...prevMessages, errorMessage]);
    } finally {
      setLoading(false);
    }
  }
  
    return (
      <div style={{ height: 'calc(100vh - 64px)' }} className="flex flex-col p-4 overflow-y-auto">

        {/* Quiz Time Animation */}
        { quizTimeAnim && <QuizTimeAnim /> }

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

              {msg.skillName && msg.skillId && (msg.bubbleOrder && msg.bubbleOrder === 1) &&
               <p className="font-bold mb-2">Chapter {msg.skillId}: {msg.skillName}</p>
               }

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
            { enabledInput &&
              <div className="options mt-2 ml-5 mr-2">
                  <button 
                    onClick={() => handleLessonOptionClick('quit')} 
                    className="btn btn-danger btn-md text-lg mr-2 px-8"
                    disabled={quizTime || numOfQuiz !== 0}
                  >
                    Quit
                    <MdLogout className='text-2xl'/>
                  </button>
                  <button 
                    onClick={() => handleLessonOptionClick('rephrase')} 
                    className="btn btn-secondary btn-md text-lg mr-2 px-8"
                  >
                    Still Unsure
                    <IoReloadOutline className='text-2xl'/>
                  </button>
                  <button 
                    onClick={() => {
                      if (quizEvalTime) {
                        askForQuizEvaluation();
                      } else if (quizTime) {
                        handleNextQuiz();
                      } else {
                        handleLessonOptionClick('next');
                      }
                    }}
                    className="btn btn-primary btn-md text-lg px-8"
                  >
                    Next
                    <GrFormNextLink className='text-2xl'/>
                  </button>
              </div>
            }

            { quizTime &&
            <div className="options mt-2 ml-5 mr-2">
                <button 
                  onClick={() => handleSubmitQuiz('A')} 
                  className="btn btn-secondary btn-lg text-2xl mr-5 px-10"
                >
                  A
                </button>
                <button 
                  onClick={() => handleSubmitQuiz('B')} 
                  className="btn btn-primary btn-lg text-2xl mr-5 px-10"
                >
                  B
                </button>
                <button 
                  onClick={() => handleSubmitQuiz('C')} 
                  className="btn btn-accent btn-lg text-2xl mr-5 px-10"
                >
                  C
                </button>
                <button 
                  onClick={() => handleSubmitQuiz('D')} 
                  className="btn btn-success btn-lg text-2xl px-10"
                >
                  D
                </button>
            </div>
            }
            
          </div>
        </div>

        {/* Search bar */}
        <div className="flex items-center gap-2 mt-4 mx-5">
            <input
              className="input input-bordered flex-1"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleInputSend()}
              disabled={!enabledInput}
              placeholder="Ask a question"
            />
            <button 
              className="btn btn-accent" 
              onClick={handleInputSend}
              disabled={!enabledInput}
            >
                Send
            </button>
        </div>
      </div>

    );
  }
  
export default ChatBubble;