export interface Course {
    courseId: number;
    title: string;
    description: string;
    level: string;
    language: string[];
    techFocus: string[];
    skills: Skill[];
    assessmentDone: boolean;
}

export interface User {
    userId: number;
    name: string;
}

export interface Skill {
    skillId: number;
    skillName: string;
    skillOrder: number;
    difficulty: string;
    completed: boolean;
    unlocked: boolean;
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

export interface QuizQuestion {
    questionId: number;
    question: string;
    difficulty: 'EASY' | 'MEDIUM' | 'HARD';
    quizChoices: QuizChoice[];
}

export interface QuizChoice {
    content: string;
    choiceLetter: 'A' | 'B' | 'C' | 'D' | 'E';
}

export interface SelectedAnswer {
    questionId: number;
    choiceIndex: number;
    correctIndex?: number;
    isCorrect?: boolean;
}

export interface QuizSolution {
    questionId: number;
    selectedChoice: string;
    correctChoice: string;
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

export interface CodeExercise {
    exerciseId: number;
    title: string;
    task: string;
    starterCode: string;
    hint: string;
    difficulty: 'EASY' | 'MEDIUM' | 'HARD';
    testCases: TestCase[];
}

export interface TestCase {
    input: string;
    output: string;
}

export interface CodeOutput {
    success: boolean;
    input: string;
    output: string;
    expectedOutput: string;
    testcaseId: number;
}