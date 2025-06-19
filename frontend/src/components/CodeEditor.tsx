import React, { useEffect, useRef, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import type * as monaco from 'monaco-editor';
import Editor from "@monaco-editor/react";
import { useAuth } from '../context/AuthContext';
import { FaPlay } from "react-icons/fa";
import { FaLightbulb } from "react-icons/fa";
import { FaRocket } from "react-icons/fa";
import { FaMapPin } from "react-icons/fa6";
import { FaSave } from "react-icons/fa";
import { CodeExercise, CodeOutput } from '../dto/response';
import LoadingSpinner from './LoadingSpinner';

const CodeEditor = () => {
    const { courseId, skillId } = useParams();
    const parsedSkillId = skillId ? parseInt(skillId) : null;
    const parsedCourseId = courseId ? parseInt(courseId) : null;
    const { userId } = useAuth();
    const editorRef = useRef<monaco.editor.IStandaloneCodeEditor | null>(null);
    const outputEndRef = useRef<HTMLDivElement>(null);
    const submitEndRef = useRef<HTMLDivElement>(null);
    const Navigate = useNavigate();
    
    const [isExerciseLoading, setIsExerciseLoading] = useState(false);
    const [isResultLoading, setIsResultLoading] = useState(false);
    const [runSample, setRunSample] = useState<CodeOutput | null>(null);
    const [runTestCases, setRunTestCases] = useState<CodeOutput[]>([]);
    const [exerciseId, setExerciseId] = useState(0);
    const [status, setStatus] = useState<'success' | 'failed' | 'in_progress'>('in_progress');
    const [hint, setHint] = useState<string | null>(null);
    const [codeExercise, setCodeExercise] = useState<CodeExercise | null>(null);
  
    useEffect(() => {
      console.log("User ID: ", userId);
      console.log("Skill ID: ", parsedSkillId);
      if (userId !== 0) {
        setIsExerciseLoading(true);
        axios.get('http://localhost:8080/api/v1/coding-exercise', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            params: {
                userId: userId,
                skillId: parsedSkillId
            }
        })
        .then(response => {
            const data: CodeExercise = response.data;
            console.log(data);
            console.log("Exercise ID: ", data.exerciseId);
            setExerciseId(data.exerciseId);
            setCodeExercise(data);
            setIsExerciseLoading(false);
        })
        .catch(error => {
              console.error('Error fetching profile setup data:', error);
              setIsExerciseLoading(false);
        });
      }}, [userId]);
  
    const handleEditorMount = (editor: any) => {
      editorRef.current = editor;
    };
  
    const handleRunCode = () => {
      if (!editorRef.current) {
          return;
      }

      setIsResultLoading(true);
      const currentCode = editorRef.current.getValue();
      console.log("Run code:", currentCode);
      resetExercise(false);

      axios.post('http://localhost:8080/api/v1/coding-exercise/run', {
          userId: userId,
          exerciseId: exerciseId,
          code: currentCode,
        }, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      .then(response => {
          const data: CodeOutput = response.data;
          console.log(data);
          setRunSample(data);
          setIsExerciseLoading(false);
          setIsResultLoading(false);
          outputEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
      })
      .catch(error => {
            console.error('Error fetching profile setup data:', error);
            setIsExerciseLoading(false);
            setIsResultLoading(false);
      });
    
    };

    const handleHint = () => {
      axios.get('http://localhost:8080/api/v1/coding-exercise/hint', {
        headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
        },
        params: {
            userId: userId,
            exerciseId: exerciseId
        }
    })
    .then(response => {
        const data = response.data;
        console.log(data);
        setHint(data);
        setIsExerciseLoading(false);
    })
    .catch(error => {
          console.error('Error fetching profile setup data:', error);
          setIsExerciseLoading(false);
    });
    }

    const handleSubmitCode = () => {
      if (!editorRef.current) {
        return;
    }
    const currentCode = editorRef.current.getValue();
      console.log("Submitting code:", currentCode);
      resetExercise(false);
      setIsResultLoading(true);
      // TODO: send to backend for compilation & execution
      axios.post('http://localhost:8080/api/v1/coding-exercise/submit', {
          userId: userId,
          exerciseId: exerciseId,
          code: currentCode,
        }, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      .then(response => {
          const data: CodeOutput[] = response.data;
          console.log(data);
          setRunTestCases(data);
          setStatus(data.every(d => d.success) ? "success" : "failed");
          setIsExerciseLoading(false);
          setIsResultLoading(false);
          submitEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
      })
      .catch(error => {
            console.error('Error fetching profile setup data:', error);
            setIsExerciseLoading(false);
            setIsResultLoading(false);
      });
    };


    const StatusBadge = ({ status }: { status: 'success' | 'failed' | 'in_progress' }) => {
      const badgeMap = {
        success: 'bg-green-500 text-white',
        failed: 'bg-red-500 text-white',
        in_progress: 'bg-yellow-400 text-black',
      };
      const labelMap = {
        success: '✔ All Test Cases Passed',
        failed: '✘ Some Test Cases Failed',
        in_progress: '⏳ Waiting for Submission...',
      };
    
      return (
        <div className={`px-4 py-2 rounded-full text-sm font-medium inline-block ${badgeMap[status]}`}>
          {labelMap[status]}
        </div>
      );
    };

    const handleSaveExercise = () => {
      // To be implemented
    }

    const resetExercise = (isNewExercise: boolean) => {
      setRunSample(null);
      setRunTestCases([]);
      setStatus('in_progress');
      if (isNewExercise) {
        setHint(null);
      }
    }

    const handleNextExercise = () => {
      setIsExerciseLoading(true);
      resetExercise(true);
      axios.get('http://localhost:8080/api/v1/coding-exercise', {
          headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`
          },
          params: {
              userId: userId,
              skillId: parsedSkillId
          }
      })
      .then(response => {
          const data: CodeExercise = response.data;
          console.log(data);
          console.log("Exercise ID: ", data.exerciseId);
          setExerciseId(data.exerciseId);
          setCodeExercise(data);
          setIsExerciseLoading(false);
      })
      .catch(error => {
            console.error('Error fetching profile setup data:', error);
            setIsExerciseLoading(false);
      });
    }
    
  
    return (
      <div className="min-h-screen font-mono p-2 space-y-6 flex flex-col items-center">
        {isExerciseLoading && <LoadingSpinner message="Loading Coding Exercise..." />}

        {!isExerciseLoading && codeExercise && (
          <div className="w-full max-w-screen-xl space-y-6">
            {/* Title */}
            <div className="text-center bg-white shadow-md rounded-2xl border border-gray-200 px-6 py-5 space-y-2">
            <div className="flex items-center justify-center flex-wrap gap-2">
              <h1 className="text-xl sm:text-2xl font-semibold text-gray-800">
                {codeExercise.title}
              </h1>

              <p className="text-sm font-medium">
                {codeExercise.difficulty === 'EASY' && (
                  <span className="px-2 py-1 ml-2 rounded-full font-sans font-semibold bg-green-100 text-green-600">Easy</span>
                )}
                {codeExercise.difficulty === 'MEDIUM' && (
                  <span className="px-2 py-1 ml-2 rounded-full font-sans font-semibold bg-yellow-100 text-yellow-600">Medium</span>
                )}
                {codeExercise.difficulty === 'HARD' && (
                  <span className="px-2 py-1 ml-2 rounded-full font-sans font-semibold bg-red-100 text-red-600">Hard</span>
                )}
              </p>
            </div>

              <p className="text-base text-gray-700 leading-relaxed max-w-3xl mx-auto">
                {codeExercise.task}
              </p>
            </div>

            {/* Code + Sample */}
            <div className="grid grid-cols-3 gap-6">
              {/* Code Editor */}
              <div className="col-span-2 bg-white rounded-2xl shadow-md p-4 border border-gray-200">
                <Editor
                  height="450px"
                  defaultLanguage="cpp"
                  defaultValue={`\n${codeExercise.starterCode}`}
                  theme="vs-dark"
                  onMount={handleEditorMount}
                  options={{
                    fontSize: 14,
                    minimap: { enabled: false },
                    scrollBeyondLastLine: false,
                    automaticLayout: true,
                    wordWrap: "on",
                    tabSize: 4,
                    fontFamily: "Fira Code, monospace",
                    lineNumbers: "on",
                  }}
                />
              </div>

              {/* Sample Test Case */}
              <div className="bg-white rounded-2xl shadow-md p-6 text-sm text-gray-800 border border-gray-200">
                <h3 className="font-semibold text-center text-xl mb-4"><FaMapPin className='inline pb-1' /> Sample Test Case</h3>
                <div className="font-semibold mb-4"><strong className='text-base'>Input:</strong><br /><code className="block bg-gray-100 p-2 rounded">{codeExercise.testCases[0].input}</code></div>
                <div className="font-semibold"><strong className='text-base'>Expected Output:</strong><br /><code className="block bg-gray-100 p-2 rounded">{codeExercise.testCases[0].output}</code></div>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex justify-center space-x-4">
              <button
                onClick={handleHint}
                className="bg-gray-400 hover:bg-gray-600 px-6 py-2 rounded-full text-white font-semibold shadow transition"
              >
                <FaLightbulb className='inline mr-1' /> Hint
              </button>

              <button
                onClick={handleSaveExercise}
                className="bg-blue-500 hover:bg-blue-600 px-6 py-2 rounded-full text-white font-semibold shadow transition"
              >
                <FaSave className='inline mr-1' /> Save
              </button>

              <button
                onClick={handleRunCode}
                className="bg-yellow-500 hover:bg-yellow-600 px-6 py-2 rounded-full text-white font-semibold shadow transition"
              >
                <FaPlay className="inline mr-1" /> Run Code
              </button>

              <button
                onClick={handleSubmitCode}
                className="bg-pink-500 hover:bg-pink-600 px-6 py-2 rounded-full text-white font-semibold shadow transition"
              >
                <FaRocket className="inline mr-1" /> Submit Code
              </button>

              { status === 'success' &&

                <button
                  onClick={handleNextExercise}
                  className="bg-green-500 hover:bg-green-600 px-6 py-2 rounded-full text-white font-semibold shadow transition"
                >
                  <FaRocket className="inline mr-1" /> Next Exercise
                </button>
              }
            </div>

            {/* Run Code Status */}
            {runSample && (
              <div className="flex justify-center mt-2">
                <div className="px-4 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800 border border-blue-300">
                  ▶️ Code executed. Output shown below.
                </div>
                <div ref={outputEndRef} />
              </div>
            )}
            {/* Scroll to bottom */}

            {/* Submit Status */}
            {runTestCases.length > 0 && (
              <div className="flex justify-center mt-2">
                <div className={`px-4 py-1 rounded-full text-sm font-medium border ${
                  status === 'success'
                    ? 'bg-green-100 text-green-800 border-green-300'
                    : status === 'failed'
                    ? 'bg-red-100 text-red-800 border-red-300'
                    : 'bg-yellow-100 text-yellow-800 border-yellow-300'
                }`}>
                  {status === 'success' && '✔ All Test Cases Passed'}
                  {status === 'failed' && '✘ Some Test Cases Failed'}
                  {status === 'in_progress' && '⏳ Running...'}
                </div>
              </div>
            )}

            {/* Hint */}
            {hint && (
              <div className="mt-6 bg-white rounded-2xl shadow-md p-4 border border-gray-200">
                <h3 className="font-semibold mb-2"><FaLightbulb className='inline pb-1' /> Hint</h3>
                <p className="text-sm text-gray-700">{hint}</p>
              </div>
            )}

            {/* Loading Spinner */}

            { isResultLoading && <LoadingSpinner message="Running Code..." /> }

            {/* Output (Run Code) */}
            {(runSample?.output) && (
              <div className="mt-6 bg-white rounded-2xl shadow-md p-4 border border-gray-200">
                <h3 className="font-semibold mb-2">🖨️ Output</h3>
                <Editor
                  height="250px"
                  defaultLanguage="text"
                  value={runSample.output}
                  theme="vs-dark"
                  options={{
                    readOnly: true,
                    fontSize: 14,
                    minimap: { enabled: false },
                    scrollBeyondLastLine: false,
                    automaticLayout: true,
                    wordWrap: "on",
                    fontFamily: "Fira Code, monospace",
                    lineNumbers: "off",
                  }}
                />
              </div>
            )}

            {/* Test Case Results */}
            {runTestCases.length > 0 && (
              <div className="mt-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {runTestCases.map((tc, idx) => (
                  <div key={idx} className={`rounded-2xl p-4 shadow border-l-4 ${
                    tc.success ? 'border-green-500' : 'border-red-500'
                  } bg-white border border-gray-200 text-base`}>
                    <div className="font-semibold text-2xl mb-4">Test Case #{tc.testcaseId - runTestCases[0].testcaseId + 1}</div>
                    <div className="mb-4"><strong>Status:</strong> {tc.success ? 'Correct' : 'Incorrect'}</div>
                    <div className="mb-4"><strong>Input:</strong><br /><code className="block bg-gray-100 p-2 rounded">{tc.input}</code></div>
                    <div className="mb-4"><strong>Output:</strong><br /><code className="block bg-gray-100 p-2 rounded">{tc.output}</code></div>
                    <div className="mb-4"><strong>Expected Output:</strong><br /><code className="block bg-gray-100 p-2 rounded">{tc.expectedOutput}</code></div>
                  </div>
                ))}
              <div ref={submitEndRef} />
              </div>
            )}
          </div>
        )}
      </div>
    );
}

export default CodeEditor
