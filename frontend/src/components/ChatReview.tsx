import React, { useRef, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'

import { GrFormNextLink } from "react-icons/gr";
import { MdLogout } from "react-icons/md";
import { MdQuestionMark } from "react-icons/md";

import { Message } from '../dto/response';
import { useAuth } from '../context/AuthContext';


interface ChatReviewProps {
    initialMessages: Message[];
    initialSkillId?: number;
    initialBubbleId?: number;
    courseId: number | null;
    skillId: number | null;
}


const ChatReview = ({ initialMessages, courseId, skillId } : ChatReviewProps) => {

  const maxQuizQuestion = 5;

  const [messages, setMessages] = useState<Message[]>(initialMessages);
  const [loading, setLoading] = useState(false);

  const [quizTime, setQuizTime] = useState(false);
  const [quizEvalTime, setQuizEvalTime] = useState(false);
  const [numOfQuiz, setNumOfQuiz] = useState(1);
  const [quizChoices, setQuizChoices] = useState([]);

  const [enabledInput, setEnabledInput] = useState(!quizTime);
  const [input, setInput] = useState('');
  const [datetime, setDatetime] = useState(new Date().toLocaleString());

  const { userId, userToken } = useAuth();
  const [quizId, setQuizId] = useState(1);

  const messagesEndRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  const tutorial = `🧠 **Ready to Review?**\n

We'll go through **5 short questions** to help you review what you've learned in this lesson.\n

You can ask **any question** if something is unclear — I’m here to help!
`;
  
  useEffect(() => {
    if (!initialMessages || initialMessages.length === 0) {
        console.log('No initial messages provided, setting tutorial message.');
      setMessages([
        {
          sender: 'ASSISTANT',
          type: 'TEXT',
          content: tutorial,
        }
      ]);
    }
    }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleQuit = () => {
    navigate(`/course/overview/${courseId}`, { replace: true });
  }

  const handleRephrase = async () => {
    const stillUnsureMessage: Message = {
      sender: 'USER',
      type: 'TEXT',
      content: "I'm still unsure. Can you elaborate more?"
    }
    setMessages(prevMessages => [...prevMessages, stillUnsureMessage]);

    // send API request to rephrase the lesson
    const response = await axios.get('http://localhost:8080/api/v1/learning/rephrase', {
      headers: {
        Authorization: `Bearer ${userToken}`
      },
      params: {
        userId: userId,
        courseId: courseId,
        review: true
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
  };

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
      const url = (quizTime || numOfQuiz !== 0) ? 
      'http://localhost:8080/api/v1/quiz/ask-questions' :
      'http://localhost:8080/api/v1/learning/ask-questions';

      try {
        const response = await axios.get(url, {
          headers: {
            Authorization: `Bearer ${userToken}`
          },
          params: {
            userId: userId,
            skillId: skillId,
            question: input,
            review: true
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
    setLoading(true);
    setEnabledInput(false);

    try {
      const response = await axios.get('http://localhost:8080/api/v1/quiz/next-review-question', {
        headers: {
          Authorization: `Bearer ${userToken}`
        },
        params: {
          userId: userId,
          skillId: skillId,
          questionNum: numOfQuiz,
          review: true
        }
      });
      
      const data = response.data;
      console.log("Next quiz question data:", data);

      if (!data || data === null) {
        if (quizTime) {
          askForQuizEvaluation();
          return;
        } else {
          handleNoQuestion();
          setEnabledInput(true);
          setLoading(false);
          return;
        }
      }

      setQuizTime(true);
      
      let choices = '';
      for (let choice of data.quizChoices) {
        choices += `**${choice.choiceLetter}.** ${choice.content}\n\n`;
        choice.color = choice.choiceLetter === 'A' ? 'btn-secondary' :
                      choice.choiceLetter === 'B' ? 'btn-primary' :
                      choice.choiceLetter === 'C' ? 'btn-accent' :
                      choice.choiceLetter === 'D' ? 'btn-success' : '';
      }

      setQuizChoices(data.quizChoices);

      const question: Message = {
        sender: 'ASSISTANT',
        type: 'QUIZ',
        topic: `Quiz Question # ${numOfQuiz}`,
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

  const handleNoQuestion = () => {
    const noQuestionMessage: Message = {
      sender: 'ASSISTANT',
      type: 'TEXT',
      content: "No more quiz questions available. Please come back tomorrow for more quizzes!",
    }
    setMessages(prevMessages => [...prevMessages, noQuestionMessage]);
    setQuizTime(false);
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
        headers: {
          Authorization: `Bearer ${userToken}`
        },
        params: {
          userId: userId,
          skillId: skillId,
          review: true
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
      setNumOfQuiz(1);

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
      setQuizEvalTime(false); // to be changed after adding more quiz
      setQuizTime(false);
      setEnabledInput(true);
      setLoading(false);
    }
  }

  const handleSubmitQuiz = async (answer: string) => {
    const selectedAnswer: Message = {
      sender: 'USER',
      type: 'QUIZ',
      content: `Selected answer: **${answer}**`,
    }
    setMessages(prevMessages => [...prevMessages, selectedAnswer]);
    setQuizChoices([]);

    try {
      setLoading(true);
      const response = await axios.get('http://localhost:8080/api/v1/quiz/submit-answer', {
        headers: {
          Authorization: `Bearer ${userToken}`
        },
        params: {
          userId: userId,
          questionId: quizId,
          choiceLetterStr: answer,
          questionNum: numOfQuiz,
          review: true
        }
      });
      
      const data = response.data;
      setNumOfQuiz(prev => prev + 1);

      console.log(data);

      const resultMessage: Message = {
        sender: 'ASSISTANT',
        type: 'QUIZ',
        content: data,
      };
      setMessages(prevMessages => [...prevMessages, resultMessage]);
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
              {(msg.type === 'TEXT' || msg.type === 'GPT' || msg.type === 'QUIZ' || msg.type === 'REVIEW')  && <ReactMarkdown>{msg.content}</ReactMarkdown>}
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
                    onClick={handleQuit} 
                    className="btn btn-danger btn-md text-lg mr-2 px-8"
                    disabled={quizTime || loading}
                  >
                    Quit
                    <MdLogout className='text-2xl'/>
                  </button>
                  <button 
                    onClick={handleRephrase} 
                    className="btn btn-secondary btn-md text-lg mr-2 px-6"
                    disabled={messages.length === 0 || loading}
                  >
                    Still Unsure
                    <MdQuestionMark className='text-2xl mb-1'/>
                  </button>
                  <button 
                    onClick={() => {
                      if (quizEvalTime) {
                        askForQuizEvaluation();
                      } else {
                        handleNextQuiz();
                      }
                    }}
                    className="btn btn-primary btn-md text-lg px-8"
                    disabled={loading}
                  >
                    Next
                    <GrFormNextLink className='text-2xl'/>
                  </button>
              </div>
            }

            { quizTime && quizChoices.length > 0 &&
            <div className="options mt-2 ml-5 mr-2">
                { quizChoices.map((choice: any) => (
                  <button 
                  onClick={() => handleSubmitQuiz(choice.choiceLetter)} 
                  className={`btn ${choice.color} btn-lg text-2xl mr-5 px-10`}
                  disabled={loading}
                >
                  {choice.choiceLetter}
                </button>
                ))}
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
              disabled={!enabledInput || messages.length === 0}
              placeholder="Ask a question"
            />
            <button 
              className="btn btn-accent" 
              onClick={handleInputSend}
              disabled={!enabledInput || messages.length === 0}
            >
                Send
            </button>
        </div>
      </div>

    );
  }
  
export default ChatReview;