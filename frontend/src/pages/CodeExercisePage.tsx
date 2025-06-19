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
      <div className="flex items-center justify-between w-full mb-4">
        <button
          onClick={handleBackToCourse}
          className="bg-gray-200 hover:bg-gray-300 w-[210px] text-gray-800 px-4 py-2 ml-[40px] rounded-lg font-medium transition"
        >
          ← Back to Course Page
        </button>

        <h1 className="text-2xl sm:text-3xl font-bold font-mono text-center flex-1">
          C++ Code Editor
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
