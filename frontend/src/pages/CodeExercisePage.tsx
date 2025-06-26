// CppEditorPage.jsx
import { useNavigate, useParams } from "react-router-dom";
import CodeEditor from "../components/CodeEditor";


const CodeExercisePage = () => {
  const { courseId } = useParams();
  const parsedCourseId = courseId ? parseInt(courseId) : null;
  const Navigate = useNavigate();

  const handleBackToCourse = () => {  
    Navigate(`/course/overview/${parsedCourseId}`);
  }
  return (
    <div className="p-6 space-y-4">
      <div className="flex items-center justify-between w-full mb-4 mt-4">
        <button
          onClick={handleBackToCourse}
          className="bg-gray-200 hover:bg-gray-300 w-[210px] text-gray-800 px-4 py-2 ml-[40px] rounded-lg font-medium transition"
        >
          ← Back to Course Page
        </button>

        <h1 className="min-w-screen text-5xl font-bold text-center font-mono tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-pink-300 to-pink-900">
          Coding Exercise
        </h1>

        <div className="w-[250px]" />
      </div>

      {/* Bottom row: Code Editor */}
      <div className="w-full">
        <CodeEditor />
      </div>
    </div>
  )
}

export default CodeExercisePage
