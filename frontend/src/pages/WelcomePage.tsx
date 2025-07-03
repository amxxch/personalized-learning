import CourseCard from "../components/CourseCard";
import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Course } from "../dto/response";
import LoadingSpinner from "../components/LoadingSpinner";
import { MdPlayLesson } from "react-icons/md";
import Typewriter from "typewriter-effect";


const currentCourses = [
  {
    courseId: 1,
    title: "C++ Fundamentals",
    description: "Master the basics of C++ programming from syntax to advanced concepts.",
    level: "Beginner",
    language: ["C++"],
    techFocus: ["Programming", "Algorithms"],
  },
  {
    courseId: 2,
    title: "Web Development with React",
    description: "Learn how to build modern web apps using React.js and Tailwind CSS.",
    level: "Intermediate",
    language : ["JavaScript", "TypeScript"],
    techFocus: ["Frontend", "React", "Tailwind CSS"],
  },
];

export default function HomePage() {
  const navigate = useNavigate();
  const { userId, userToken } = useAuth();

  const [loading, setLoading] = useState(true);
  const [courses, setCourses] = useState<Course[]>([]);

  useEffect(() => {
    if (!userId || !userToken) return;

    axios.get('http://localhost:8080/api/v1/course/courses-taken/current', {
        headers: {
            Authorization: `Bearer ${userToken}`
        },
        params: {
            userId: userId,
        }
    })
    .then((response) => {
        const data: Course[] = response.data;
        console.log('Course Taken:', data);
        setCourses(data);
        // set data.techFocus
        setLoading(false);
    })
    .catch((error) => {
        console.error('Error fetching course taken:', error);
        setLoading(false);
    });
    console.log("User ID:", userId);
    console.log("User Token:", userToken);
}, [userId, userToken]);


  return (
    <div className="min-h-screen bg-gradient-to-b from-indigo-100 to-white text-gray-800 font-sans">
      <section className="pt-20 pb-12 px-6 bg-white h-[350px] ">
        <div className="max-w-6xl mx-auto flex flex-col md:flex-row items-center gap-10">
          <div className="md:w-11/12 text-left">
            <div className="relative mb-5">
              {/* Transparent placeholder to reserve space */}
              <div className="invisible text-4xl md:text-6xl font-bold font-mono tracking-wide">
                Welcome to LearningBot
              </div>

              <div className="absolute top-0 left-0 text-4xl md:text-6xl font-bold font-mono tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-pink-300 to-pink-900">
                <Typewriter
                  options={{
                    strings: ['Welcome to LearningBot'],
                    autoStart: true,
                    loop: true,
                    cursor: '_',
                    delay: 100,
                    deleteSpeed: 20,
                  }}
                />
              </div>
            </div>

            <p className="text-lg text-gray-600 mb-8">
              Your personalized platform to master programming with guided lessons, hands-on exercises, and progress tracking.
            </p>
            <button
              onClick={() => navigate("/profile", { replace: true })}
              className="px-8 py-3 text-white bg-pink-600 hover:bg-pink-700 rounded-full font-semibold shadow-md transition"
            >
              Go to Dashboard
            </button>
          </div>
          <div className="md:w-11/12 flex justify-center">
            <img src="/images/book-icon.png" alt="Learning Illustration" className="max-w-64 w-full" />
          </div>
        </div>
      </section>

      <section className="py-20 px-6 bg-gradient-to-b from-white to-pink-100">
      { loading ? (
        <LoadingSpinner message="Loading your courses..." />
      ) : (
        <div className="py-2 px-6 max-w-5xl mx-auto">
          <h2 className="text-3xl text-center font-semibold mb-12">
            <MdPlayLesson className="inline text-4xl mr-2 mb-1" />
              Your Current Courses
          </h2>
          { courses.length === 0
          ? <div className="bg-white shadow-md rounded-xl p-6 text-center space-y-4 border border-gray-200">
              <p className="text-lg text-gray-700 font-medium">
                🚧 You’re not enrolled in any courses yet.
              </p>
              <p className="text-gray-500">
                Start your learning journey by exploring your personalized course roadmap!
              </p>
              <a
                href="/profile/planner"
                className="inline-block px-5 py-2 bg-pink-600 text-white font-semibold rounded-lg shadow hover:bg-pink-700 transition"
              >
                View Course Roadmap
              </a>
            </div>
          : 
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-10">
            {courses
            .map((course) => (
              <CourseCard
                key={course.courseId}
                title={course.title}
                image={""}
                description={course.description}
                courseId={course.courseId}
                language={course.language}
                techFocus={course.techFocus}
                level={course.level}
                progressPercent={course.progressPercent || 0}
              />
            ))}
          </div>
          }
        </div>
      )}
  </section>
</div>

  );
}
