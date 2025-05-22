export interface Course {
    courseId: number;
    courseTitle: string;
    description: string;
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
    contentType: 'TEXT' | 'IMAGE' | 'VIDEO';
    content: string;
    skill: Skill;
}

export interface Progress {
    progressId: number;
    completed: boolean;
    user: User;
    course: Course;
    skill: Skill;
    bubble: Bubble;
}