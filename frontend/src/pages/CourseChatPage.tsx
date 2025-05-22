'use client'

import ChatBubble from '../components/ChatBubble';
import React from 'react'

const CourseChatPage = () => {
  // fetch by courseId, incompleted
  // add another rest api to get from only incompleted

  return (
    <div>
      <ChatBubble initialMessages={[
        { sender: 'teacher', type: 'TEXT', content: 'Hello, how can I help you?'},
        { sender: 'user', type: 'TEXT', content: 'Hello, I have a question about the course.'},
      ]} />
    </div>
  )
}

export default CourseChatPage
