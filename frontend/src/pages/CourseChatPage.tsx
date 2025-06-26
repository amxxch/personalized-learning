import axios from 'axios';
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ChatBubble from '../components/ChatBubble';
import { Message } from '../dto/response';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';

const CourseChatPage = () => {
  const { userId, userToken } = useAuth();
  // const [skillId, setSkillId] = useState(1);
  const [bubbleId, setBubbleId] = useState(1);
  const [initialMessages, setInitialMessages] = useState<Message[]>([]);

  const [loading, setLoading] = useState(true);
  const { courseId, skillId } = useParams();
  const parsedCourseId = courseId ? parseInt(courseId) : null;
  const parsedSkillId = skillId ? parseInt(skillId) : null;
  const Navigate = useNavigate();

  useEffect(() => {
    setLoading(true);

    // Check if initial assessment is done
    axios.get('http://localhost:8080/api/v1/learning/isAssessmentDone', {
      headers: {
        Authorization: `Bearer ${userToken}`
      },
      params: {
        userId: userId,
        courseId: parsedCourseId,
      }
    })
    .then((response) => {
      console.log('Assessment status:', response.data);
      if (!response.data) {
        // if assessment is done, redirect to assessment page
        setLoading(false);
        Navigate(`/assessment/${parsedCourseId}`);
      } else {
        fetchChatHistory();
      }
    })
    .catch((error) => {
      console.error('Error checking assessment status:', error);
      setLoading(false);
    })

    const fetchChatHistory = async () => {
      try {

        // Fetch chat history
        // const chatResponse = await axios.get('http://localhost:8080/api/v1/chat-history/by-course', {
        //   headers: {
        //     Authorization: `Bearer ${userToken}`
        //   },
        //   params: {
        //     userId: userId,
        //     courseId: parsedCourseId
        //   }
        // });


        const chatResponse = await axios.get('http://localhost:8080/api/v1/chat-history/by-skill', {
          headers: {
            Authorization: `Bearer ${userToken}`
          },
          params: {
            userId: userId,
            skillId: parsedSkillId,
          }
        });
        const data = chatResponse.data;

        console.log("Fetched chat history:", data);

        if (!data || data.length === 0) {
          setInitialMessages([]);
          return;
        }

        const messages: Message[] = data
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

        // Set current skillId and BubbleId
        const latestMessages = messages
          .filter((msg: any) => msg.sender === 'ASSISTANT' && msg.type !== 'GPT')
          .sort((a: any, b: any) => a.chatId - b.chatId);

        if (latestMessages.length > 0) {
          const latestMessage = latestMessages[latestMessages.length - 1];
          setBubbleId(latestMessage.bubbleId || 1);
        } 
        setInitialMessages(messages);

        console.log("skillId in page:", parsedSkillId);
        console.log("Initial messages set:", messages);

      } catch (error) {
        console.error('Error fetching chat history:', error);
        setInitialMessages([]);
      } finally {
        setLoading(false);
      }
    }
  }, [userId])

  return (
    <div style={{ height: 'calc(100vh - 64px)' }} className="flex flex-col p-4 overflow-y-auto">
        {loading ? (
          <LoadingSpinner message="Loading learning status..." />
        ) : (
          <ChatBubble 
          initialMessages={initialMessages} 
          initialSkillId={parsedSkillId || 1} 
          initialBubbleId={bubbleId} 
          courseId={parsedCourseId}
          />
        )}
    </div>
  )
}

export default CourseChatPage
