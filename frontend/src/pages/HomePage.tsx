import CourseCard from "../components/CourseCard";
import { useAuth } from "../context/AuthContext";


export default function HomePage() {
  const { logout, isProfileSetup } = useAuth();
  const handleLogout = () => {
    logout();
    window.location.href = '/login';
  }

  return (
    <div className="flex flex-col items-center min-h-screen p-8 sm:p-20 gap-8 font-[family-name:var(--font-geist-sans)]">
      <h1 className="min-w-screen text-5xl font-bold text-center font-mono tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-pink-300 to-pink-900">
        Welcome to LearningBot!
      </h1>

      { isProfileSetup && (
        <div className="flex flex-col items-center gap-8 font-[family-name:var(--font-geist-sans)]">
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
        )}
    </div>
  );
}
