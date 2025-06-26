import axios from 'axios';
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Message } from '../dto/response';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import ChatHistory from '../components/ChatHistory';

const ChatHistoryPage = () => {
  const { userId, userToken } = useAuth();
  const [initialMessages, setInitialMessages] = useState<Message[]>([]);

  const [loading, setLoading] = useState(true);
  const { courseId, skillId } = useParams();
  const parsedCourseId = courseId ? parseInt(courseId) : null;
  const parsedSkillId = skillId ? parseInt(skillId) : null;
  const Navigate = useNavigate();

  useEffect(() => {
    setLoading(true);

    axios.get('http://localhost:8080/api/v1/chat-history/by-skill', {
        headers: {
          Authorization: `Bearer ${userToken}`
        },
        params: {
          userId: userId,
          skillId: parsedSkillId,
        }
      })
      .then((response) => {
        console.log('Chat history by skill:', response.data);
        const data: Message[] = response.data
          .sort((a: any, b: any) => a.chatId - b.chatId)
          .map((msg: any) => {
            msg.contentType = msg.contentType === 'UNSURE' ? 'TEXT' : msg.contentType;
            return {
              sender: msg.sender,
              type: msg.contentType,
              content: msg.content,
              skillId: msg.skillId,
              skillName: msg.skillName,
              bubbleOrder: msg.bubbleOrder,
              bubbleId: msg.bubbleId
            };
          });

        if (data.length > 0) {
          const latestMessage = data[data.length - 1];
          console.log('Latest one message:', latestMessage);
        } 
        setInitialMessages(data);
        setLoading(false);
      })
    }, []);

  return (
    <div style={{ height: 'calc(100vh - 64px)' }} className="flex flex-col p-4 overflow-y-auto">
        {loading ? (
          <LoadingSpinner message="Loading learning status..." />
        ) : (
          <ChatHistory
            initialMessages={initialMessages}
            courseId={parsedCourseId}
            skillId={parsedSkillId}
            />
        )}
    </div>
  )
}

export default ChatHistoryPage;
