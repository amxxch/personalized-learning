export interface Course {
    courseId: number;
    title: string;
    description: string;
    language: string[];
    level: string;
    techFocus: string[];
}

export interface User {
    userId: number;
    name: string;
}

export interface Skill {
    skillId: number;
    skillName: string;
    skillOrder: number;
    course: Course;
}

export interface Bubble {
    bubbleId: number;
    topic: string;
    bubbleOrder: number;
    contentType: 'TEXT' | 'IMAGE' | 'VIDEO' | 'CODE';
    content: string;
    skillId: number;
}

export interface Progress {
    progressId: number;
    completed: boolean;
    user: User;
    course: Course;
    skill: Skill;
    bubble: Bubble;
}

export interface ChatHistory {
    chatId: number;
    skillId: number;
    skillName: string;
    sender: 'CHATBOT' | 'USER';
    contentType: 'TEXT' | 'IMAGE' | 'VIDEO' | 'CODE' | 'GPT' | 'UNSURE' | 'QUIZ';
    content: string;
    topic?: string;
    timestamp: Date;
    bubbleOrder: number;
    bubbleId?: number;
}

export interface Message {
    sender: 'ASSISTANT' | 'USER';
    type: 'TEXT' | 'IMAGE' | 'VIDEO' | 'CODE' | 'GPT' | 'UNSURE' | 'QUIZ';
    content: string;
    topic?: string;
    skillId?: number;
    skillName?: string;
    bubbleOrder?: number;
    bubbleId?: number;
  }

export interface QuizChoice {
    content: string;
    choiceLetter: 'A' | 'B' | 'C' | 'D';
}

export const techFocusOptions = [
    "Web Development",
    "Machine Learning",
    "Data Science",
    "Mobile Development",
    "Game Development",
    "Cybersecurity",
    "Cloud Computing",
    "Embedded Systems",
    "DevOps",
    "Computer Vision"
  ] as const;
  
export type TechTopic = typeof techFocusOptions[number];