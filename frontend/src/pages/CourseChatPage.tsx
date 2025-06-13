'use client'

import axios from 'axios';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import ChatBubble from '../components/ChatBubble';
import { Message } from '../dto/response';
import { useAuth } from '../context/AuthContext';

const CourseChatPage = () => {
  const { userId, userToken } = useAuth();
  const [skillId, setSkillId] = useState(1);
  const [bubbleId, setBubbleId] = useState(1);
  const [initialMessages, setInitialMessages] = useState<Message[]>([]);

  const [loading, setLoading] = useState(false);
  const { courseId } = useParams();

  useEffect(() => {
    // fetch chat history
    const fetchChatHistory = async () => {
      try {
        setLoading(true);
        const response = await axios.get('http://localhost:8080/api/v1/chat-history/by-course', {
          headers: {
            Authorization: `Bearer ${userToken}`
          },
          params: {
            userId: userId,
            courseId: courseId
          }
        });
        const data = response.data;

        console.log("Fetched chat history:", data);

        if (!data || data.length === 0) {
          setInitialMessages([]);
          return;
        }

        const messages: Message[] = data
        .sort((a: any, b: any) => a.chatId - b.chatId)
        .map((msg: any) => {
          msg.contentType = msg.contentType === 'UNSURE' ? 'TEXT' : msg.contentType;
          return msg.topic ?
          {
            sender: msg.sender,
            type: msg.contentType,
            content: msg.content,
            topic: msg.topic,
            skillId: msg.skillId,
            skillName: msg.skillName,
            bubbleOrder: msg.bubbleOrder,
            bubbleId: msg.bubbleId
          } : {
            sender: msg.sender,
            type: msg.contentType,
            content: msg.content,
            skillId: msg.skillId,
            skillName: msg.skillName,
            bubbleOrder: msg.bubbleOrder
          };
        });

        const latestMessages = messages
          .filter((msg: any) => msg.sender === 'ASSISTANT')
          .filter((msg: any) => msg.type !== 'GPT')
          .sort((a: any, b: any) => a.bubbleId - b.bubbleId);

        console.log('Latest messages:', latestMessages);

        if (latestMessages.length > 0) {
          const latestMessage = latestMessages[latestMessages.length - 1];
          console.log('Latest one message:', latestMessage);
          setSkillId(latestMessage.skillId || 1);
          setBubbleId(latestMessage.bubbleId || 1);
        } 
        setInitialMessages(messages);

      } catch (error) {
        console.error('Error fetching chat history:', error);
        setInitialMessages([]);
      } finally {
        setLoading(false);
      }
    }

    fetchChatHistory();
  }, [])

  return (
    <div>
        {loading ? (
          <div>
            <span className="loading loading-spinner text-primary"></span>
            <span className="loading loading-spinner text-secondary"></span>
            <span className="loading loading-spinner text-accent"></span>
            <span className="loading loading-spinner text-neutral"></span>
            <span className="loading loading-spinner text-info"></span>
            <span className="loading loading-spinner text-success"></span>
            <span className="loading loading-spinner text-warning"></span>
            <span className="loading loading-spinner text-error"></span>
          </div>
        ) : (
          <ChatBubble 
          initialMessages={initialMessages} 
          initialSkillId={skillId} 
          initialBubbleId={bubbleId} 
          courseId={parseInt(courseId!)}
          />
        )}
    </div>
  )
}

export default CourseChatPage
