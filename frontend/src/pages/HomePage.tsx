import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import CourseCard from "../components/CourseCard";


export default function HomePage() {
  return (
    <div className="flex flex-col items-center min-h-screen p-8 sm:p-20 gap-8 font-[family-name:var(--font-geist-sans)]">
      <h1 className="text-5xl font-bold text-center">
        Welcome to LearningBot!
      </h1>

      <label className="input flex items-center gap-2 w-full max-w-md border border-base-300">
        <svg className="h-[1em] opacity-50" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
          <g
            strokeLinejoin="round"
            strokeLinecap="round"
            strokeWidth="2.5"
            fill="none"
            stroke="currentColor"
          >
            <circle cx="11" cy="11" r="8"></circle>
            <path d="m21 21-4.3-4.3"></path>
          </g>
        </svg>
        <input type="search" required placeholder="Search Course Name" className="w-full" />
      </label>

      <div className="grid grid-cols-1 sm:grid-cols-4 gap-6 w-full">
        <CourseCard 
          title="Introduction to C++ Programming" 
          image="images/cpp.png" 
          description="This course introduces students to the C++ programming language."
        />
      </div>
    </div>

  );
}
