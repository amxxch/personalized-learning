import { useEffect, useState } from "react";
import CourseCard from "../components/CourseCard";
import { useAuth } from "../context/AuthContext";
import axios from "axios";
import { Course } from "../dto/response";
import { MdClear } from "react-icons/md";


export default function HomePage() {
    const { isProfileSetup } = useAuth();

    const [isLoading, setIsLoading] = useState(true);
    const [courseList, setCourseList] = useState<Course[]>([]);

    const [query, setQuery] = useState("");
    const [allLanguages, setAllLanguages] = useState<string[]>([]);
    const [allTechFocuses, setAllTechFocuses] = useState<string[]>([]);
    const allLevels = ["Beginner", "Intermediate", "Advanced"];
    

    const [selectedLanguages, setSelectedLanguages] = useState<string[]>([]);
    const [selectedTechFocus, setSelectedTechFocus] = useState<string[]>([]);
    const [selectedLevel, setSelectedLevel] = useState<string[]>([]);
  
    useEffect(() => {
      // Fetching languages and tech focus from the backend
      axios.get('http://localhost:8080/api/v1/course', {
          headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`
          }
      })
      .then(response => {
          const data: Course[] = response.data;
          console.log(data)
          setCourseList(data);
          setIsLoading(false);
      })
      .catch(error => {
          console.error('Error fetching profile setup data:', error);
      });

      // Fetching languages and tech focus from the backend
      axios.get('http://localhost:8080/api/v1/profile-setup', {
        headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
        }
    })
    .then(response => {
        const data = response.data;
        console.log(data)
        const languagesList = data.languages;
        const techFocusList = data.techFocuses;
        setAllLanguages(languagesList);
        setAllTechFocuses(techFocusList);
    })
    .catch(error => {
        console.error('Error fetching profile setup data:', error);
    });
  }, []);

  const toggleTag = (tag: string, list: string[], setList: (v: string[]) => void) => {
    if (list.includes(tag)) {
      setList(list.filter((t) => t !== tag));
    } else {
      setList([...list, tag]);
    }
  };

  return (
    <div className="flex flex-col items-center min-h-screen p-8 sm:p-20 gap-8 font-[family-name:var(--font-geist-sans)]">
      <h1 className="min-w-screen text-5xl font-bold text-center font-mono tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-pink-300 to-pink-900">
        Welcome to LearningBot!
      </h1>

      { isProfileSetup && (
        <div className="flex flex-col items-center gap-8 font-[family-name:var(--font-geist-sans)]">
          {/* Search Bar */}
          {/* <label className="input flex items-center gap-2 w-full max-w-md border border-base-300">
            <svg className="h-[1em] opacity-50" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
              <g strokeLinejoin="round" strokeLinecap="round" strokeWidth="2.5" fill="none" stroke="currentColor">
                <circle cx="11" cy="11" r="8"></circle>
                <path d="m21 21-4.3-4.3"></path>
              </g>
            </svg>
            <input
              type="search"
              required
              placeholder="Search Course Name"
              className="w-full"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </label> */}

          {/* Tag Filters */}
          <div className="flex flex-col lg:flex-row gap-4">

            {/* Level Filter */}
            <div className="dropdown dropdown-bottom">
              <div tabIndex={0} role="button" className="btn btn-sm btn-outline rounded-full">
                🧠 Levels
              </div>
              <ul tabIndex={0} className="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52">
                {allLevels.map((level) => (
                  <li key={level}>
                    <label className="cursor-pointer label justify-start gap-2">
                      <input
                        type="checkbox"
                        checked={selectedLevel.includes(level)}
                        onChange={() => toggleTag(level, selectedLevel, setSelectedLevel)}
                        className="checkbox checkbox-sm"
                      />
                      <span>{level}</span>
                    </label>
                  </li>
                ))}
              </ul>
            </div>

            {/* Language Filter */}
            <div className="dropdown dropdown-bottom">
              <div tabIndex={0} role="button" className="btn btn-sm btn-outline rounded-full">
                🌐 Languages
              </div>
              <ul tabIndex={0} className="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52">
                {allLanguages.map((lang) => (
                  <li key={lang}>
                    <label className="cursor-pointer label justify-start gap-2">
                      <input
                        type="checkbox"
                        checked={selectedLanguages.includes(lang)}
                        onChange={() => toggleTag(lang, selectedLanguages, setSelectedLanguages)}
                        className="checkbox checkbox-sm"
                      />
                      <span>{lang}</span>
                    </label>
                  </li>
                ))}
              </ul>
            </div>

            {/* Tech Focus Filter */}
            <div className="dropdown dropdown-bottom">
              <div tabIndex={0} role="button" className="btn btn-sm btn-outline rounded-full">
                🚀 Tech Area
              </div>
              <ul tabIndex={0} className="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52">
                {allTechFocuses.map((focus) => (
                  <li key={focus}>
                    <label className="cursor-pointer label justify-start gap-2">
                      <input
                        type="checkbox"
                        checked={selectedTechFocus.includes(focus)}
                        onChange={() => toggleTag(focus, selectedTechFocus, setSelectedTechFocus)}
                        className="checkbox checkbox-sm"
                      />
                      <span>{focus}</span>
                    </label>
                  </li>
                ))}
              </ul>
            </div>

            {/* Clear Filters Button */}
            <button
              className="btn btn-outline btn-sm text-xl font-semibold text-gray-600 transition-colors duration-300 border"
              onClick={() => {
                setSelectedLanguages([]);
                setSelectedTechFocus([]);
                setSelectedLevel([]);
              }}
            >
              <MdClear />
            </button>
          </div>


          {/* Display Selected Tags */}
          {(selectedLanguages.length || selectedTechFocus.length || selectedLevel.length) > 0 && (
            <div className="flex flex-wrap gap-2">
              <p className="text-md font-semibold">Search tags: </p>
              {selectedLevel.map((tag, i) => (
                <div key={i} className="bg-yellow-100 text-yellow-700 text-sm px-3 py-1 rounded-full font-medium">
                  {tag}
                </div>
              ))}
              {selectedLanguages.map((tag, i) => (
                <div key={i} className="bg-green-100 text-green-700 text-sm px-3 py-1 rounded-full font-medium">
                  {tag}
                </div>
              ))}
              {selectedTechFocus.map((tag, i) => (
                <div key={i} className="bg-purple-100 text-purple-700 text-sm px-3 py-1 rounded-full font-medium">
                  {tag}
                </div>
              ))}
            </div>
          )}
    
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-10 w-5/6">
            {courseList
            .filter((course) => {
              const matchesLanguage = selectedLanguages.length === 0 || course.language.some(lang => selectedLanguages.includes(lang));
              const matchesTechFocus = selectedTechFocus.length === 0 || course.techFocus.some(focus => selectedTechFocus.includes(focus));
              const matchesLevel = selectedLevel.length === 0 || selectedLevel.includes(course.level.charAt(0).toUpperCase() + course.level.slice(1).toLowerCase());

              return matchesLanguage && matchesTechFocus && matchesLevel;
            })
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
              />
            ))}
          </div>
        </div>
        )}
    </div>
  );
}
