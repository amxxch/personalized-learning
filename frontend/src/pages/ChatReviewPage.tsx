import axios from 'axios';
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Message } from '../dto/response';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import ChatHistory from '../components/ChatHistory';
import ChatReview from '../components/ChatReview';

const ChatReviewPage = () => {
  const { userId, userToken } = useAuth();
  const [initialMessages, setInitialMessages] = useState<Message[]>([]);

  const [loading, setLoading] = useState(false);
  const { courseId, skillId } = useParams();
  const parsedCourseId = courseId ? parseInt(courseId) : null;
  const parsedSkillId = skillId ? parseInt(skillId) : null;
  const Navigate = useNavigate();

  useEffect(() => {
    const reviewTimeMessage: Message = {
      sender: 'ASSISTANT',
      type: 'REVIEW',
      content: "",
      topic: `Review Time! Let's answer the following question`,
    };
    setInitialMessages([reviewTimeMessage]);
    }, []);

  return (
    <div style={{ height: 'calc(100vh - 64px)' }} className="flex flex-col p-4 overflow-y-auto">
        {loading ? (
          <LoadingSpinner message="Loading learning status..." />
        ) : (
          <ChatReview
            initialMessages={initialMessages}
            courseId={parsedCourseId}
            skillId={parsedSkillId}
            />
        )}
    </div>
  )
}

export default ChatReviewPage;
