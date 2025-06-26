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
import { GrPowerReset } from "react-icons/gr";
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
    const [isTestcaseResultLoading, setIsTestcaseResultLoading] = useState(false);
    const [isSaveExerciseLoading, setIsSaveExerciseLoading] = useState(false);
    const [isCustomCodeRunning, setIsCustomCodeRunning] = useState(false);

    const [saveSuccess, setSaveSuccess] = useState(false);
    const [saveError, setSaveError] = useState(false);

    const [runSample, setRunSample] = useState<CodeOutput | null>(null);
    const [runTestCases, setRunTestCases] = useState<CodeOutput[]>([]);
    const [exerciseList, setExerciseList] = useState<CodeExercise[]>([]);
    const [exerciseId, setExerciseId] = useState(0)
    const [exerciseIndex, setExerciseIndex] = useState(0);
    const [customInput, setCustomInput] = useState('');
    const [customOutput, setCustomOutput] = useState('');
    const [status, setStatus] = useState<'success' | 'failed' | 'in_progress'>('in_progress');
    const [hint, setHint] = useState<string | null>(null);
    const [codeExercise, setCodeExercise] = useState<CodeExercise | null>(null);

    useEffect(() => {
      console.log("Exercise List: ", exerciseList);
    }, [exerciseList])
  
    useEffect(() => {
      console.log("User ID: ", userId);
      console.log("Skill ID: ", parsedSkillId);
      if (userId !== 0) {
        setIsExerciseLoading(true);
        // Get Current Coding Exercise
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
            const exercise: CodeExercise = response.data;
            console.log(exercise);
            console.log("Exercise ID: ", exercise.exerciseId);
            setExerciseId(exercise.exerciseId);
            setCodeExercise(exercise);
            setExerciseList([exercise]);
            setIsExerciseLoading(false);

            // Get All Coding Exercises for the Skill (for selection dropdown)
            axios.get('http://localhost:8080/api/v1/coding-exercise/all', {
              headers: {
                  Authorization: `Bearer ${localStorage.getItem('token')}`
              },
              params: {
                  userId: userId,
                  skillId: parsedSkillId
              }
            })
            .then(response => {
                const exerciseList: CodeExercise[] = response.data;
                console.log(exerciseList);
                setExerciseList(exerciseList);
                const currentIndex = exerciseList.findIndex(ex => ex.exerciseId === exercise.exerciseId);
                setExerciseIndex(currentIndex);
            })
            .catch(error => {
                  console.error('Error fetching profile setup data:', error);
            });
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

      setIsCustomCodeRunning(true);
      const currentCode = editorRef.current.getValue();
      console.log("Run code:", currentCode);
      resetSetting(false);
      setCustomOutput('');

      axios.post('http://localhost:8080/api/v1/coding-exercise/run', {
          userId: userId,
          exerciseId: exerciseId,
          code: currentCode,
          input: customInput,
        }, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      .then(response => {
          const data: CodeOutput = response.data;
          console.log(data);
          setCustomOutput(data.output);
          setIsExerciseLoading(false);
          setIsCustomCodeRunning(false);
          outputEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
      })
      .catch(error => {
            console.error('Error fetching profile setup data:', error);
            setIsExerciseLoading(false);
            setIsCustomCodeRunning(false);
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
      resetSetting(false);
      setIsTestcaseResultLoading(true);
      const updatedExerciseList = exerciseList.map(ex => {
        if (ex.exerciseId === exerciseId) {
          return { ...ex, starterCode: currentCode };
        }
        return ex;
      });
      setExerciseList(updatedExerciseList);

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
          setIsTestcaseResultLoading(false);
          submitEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
      })
      .catch(error => {
            console.error('Error fetching profile setup data:', error);
            setIsExerciseLoading(false);
            setIsTestcaseResultLoading(false);
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
      setIsSaveExerciseLoading(true);
      console.log("Saving code: ", editorRef.current?.getValue());
      axios.post('http://localhost:8080/api/v1/coding-exercise/save', {
          userId: userId,
          exerciseId: exerciseId,
          code: editorRef.current?.getValue() || '',
          input: '',
        }, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      .then(response => {
          console.log("Exercise saved successfully");
          console.log(response.data);
          setIsSaveExerciseLoading(false);
          setExerciseList(prevList => {
            return prevList.map(ex => {
              if (ex.exerciseId === exerciseId) {
                return {
                  ...ex,
                  starterCode: editorRef.current?.getValue() || ''
                };
              }
              return ex;
            });
          });
          setSaveSuccess(true);
          setTimeout(() => {
            setSaveSuccess(false);
          }, 2000);
      })
      .catch(error => {
            console.error('Error saving exercise:', error);
            setIsSaveExerciseLoading(false);
            setSaveError(true);
            setTimeout(() => {
              setSaveError(false);
            }, 2000);
      });
    }

    const resetSetting = (isNewExercise: boolean) => {
      setRunSample(null);
      setRunTestCases([]);
      setStatus('in_progress');
      if (isNewExercise) {
        setHint(null);
        setCustomOutput('');
        setCustomInput('');
      }
    }

    const handleNextExercise = () => {
      setIsExerciseLoading(true);
      resetSetting(true);
      const nextIndex = exerciseList.findIndex(ex => ex.exerciseId === exerciseId) + 1;
      if (nextIndex >= exerciseList.length) {
        console.log("No more exercises available");
        setIsExerciseLoading(false);
        return;
      }
      setExerciseIndex(nextIndex);
      const nextExercise = exerciseList[nextIndex];
      setCodeExercise(nextExercise);
      setExerciseId(nextExercise.exerciseId);

      let updatedExerciseList = exerciseList.map(ex => {
        if (ex.exerciseId === exerciseId) {
          return { ...ex, completed: true };
        } else if (ex.exerciseId === nextExercise.exerciseId) {
          return { ...ex, unlocked: true };
        }
        return ex;
      })
      setExerciseList(updatedExerciseList);
      editorRef.current?.setValue(nextExercise.starterCode);
      setIsExerciseLoading(false);
    }

    const handleExerciseChange = (exercise: CodeExercise) => {
      console.log("exercise change", exercise.exerciseId);
      setCodeExercise(exercise);
      setExerciseId(exercise.exerciseId);
      resetSetting(true);
      editorRef.current?.setValue(exercise.starterCode);
    }

    const handleReset = () => {
      setIsExerciseLoading(true);
      axios.post('http://localhost:8080/api/v1/coding-exercise/reset', {
        userId: userId,
        exerciseId: exerciseId,
        code: '',
        input: '',
      }, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })      
      .then(response => {
          console.log("Exercise reset successfully");
          console.log(response.data);
          setIsExerciseLoading(false);
          resetSetting(true);
          editorRef.current?.setValue(response.data || '');
      })
      .catch(error => {
            console.error('Error resetting exercise:', error);
            setIsExerciseLoading(false);
      } );
    }

    const generateDifficultyTag = (difficulty: string) => {
      switch (difficulty) {
        case 'EASY':
          return <span className="px-2 py-1 ml-2 rounded-full font-sans font-semibold bg-green-100 text-green-600">Easy</span>;
        case 'MEDIUM':
          return <span className="px-2 py-1 ml-2 rounded-full font-sans font-semibold bg-yellow-100 text-yellow-600">Medium</span>;
        case 'HARD':
          return <span className="px-2 py-1 ml-2 rounded-full font-sans font-semibold bg-red-100 text-red-600">Hard</span>;
        default:
          return null;
      }
    }
  
    return (
      <div className="min-h-screen p-2 space-y-6 flex flex-col items-center">
        {isExerciseLoading && <LoadingSpinner message="Loading Coding Exercise..." />}

        {!isExerciseLoading && codeExercise && (
          <div className="w-full max-w-screen-xl space-y-6">
            {/* Title */}
            <div className="font-mono bg-white shadow-md rounded-2xl border border-gray-200 px-6 py-5 space-y-2">
              <div className="flex items-center justify-between flex-wrap gap-2">
                <div className="flex flex-wrap items-center gap-3">
                  <h1 className="text-xl sm:text-3xl font-semibold text-gray-800">
                    {codeExercise.title}
                  </h1>

                  <p className="text-sm font-medium">
                    {codeExercise.difficulty && generateDifficultyTag(codeExercise.difficulty)}
                  </p>
                </div>

                {/* Select Question Dropdown */}
                <div className="dropdown dropdown-bottom">
                  <div
                    tabIndex={0}
                    role="button"
                    className="btn btn-outline btn-sm rounded-full px-6 mr-6 shadow-md text-md"
                  >
                    Select Exercise
                  </div>
                  <ul
                    tabIndex={0}
                    className="dropdown-content z-[10] menu p-4 shadow-lg bg-white rounded-xl w-80 space-y-2"
                  >
                    {exerciseList.map((ex) => (
                      <li key={ex.exerciseId} className="w-full">
                        <button
                          onClick={() => handleExerciseChange(ex)}
                          disabled={!ex.unlocked}
                          className={`w-full text-left px-4 py-3 rounded-lg flex items-center justify-between gap-3 transition-all
                            ${ex.unlocked ? "hover:bg-gray-100" : "opacity-50 cursor-not-allowed"}`}
                        >
                          <div className="flex flex-col">
                            <span className="text-sm font-medium">{ex.title}</span>
                            <span className="text-sm text-gray-500">Exercise #{ex.exerciseId}</span>
                          </div>
                          <div className="shrink-0">
                            {ex.difficulty && generateDifficultyTag(ex.difficulty)}
                          </div>
                        </button>
                      </li>
                    ))}
                  </ul>
                </div>

              </div>

              <p className="text-base text-gray-700 leading-relaxed">
                {codeExercise.task}
              </p>
            </div>

            {/* Save Successfully */}
            {saveSuccess && (
              <div className="flex items-center gap-2 px-4 py-2 bg-green-100 text-green-800 rounded-xl shadow-md text-sm font-medium max-w-fit mx-auto mt-4 border border-green-300">
                <svg
                  className="w-5 h-5 text-green-600"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  viewBox="0 0 24 24"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                </svg>
                <span>Saved successfully</span>
              </div>
            )}

            {/* Save Error */}
            {saveError && (
              <div className="flex items-center gap-2 px-4 py-2 bg-green-100 text-red-800 rounded-xl shadow-md text-sm font-medium max-w-fit mx-auto mt-4 border border-red-300">
                <svg
                  className="w-5 h-5 text-red-600"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  viewBox="0 0 24 24"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                </svg>
                <span>Saving failed. Please try again.</span>
              </div>
            )}
            

            {/* Code + Sample */}
            <div className="grid grid-cols-3 gap-6">
              {/* Code Editor */}
              <div className="col-span-2 bg-white rounded-2xl shadow-md p-4 border border-gray-200">
                <Editor
                  height="450px"
                  defaultLanguage="cpp"
                  defaultValue={codeExercise.starterCode}
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

              <div className="col-span-1 flex flex-col gap-4">
                {/* Sample Test Case */}
                <div className="bg-white font-mono rounded-2xl shadow-md p-6 text-sm text-gray-800 border border-gray-200">
                  <h3 className="font-semibold text-center text-xl mb-4"><FaMapPin className='inline pb-1' /> Sample Test Case</h3>
                  <div className="font-semibold mb-4"><strong className='text-base'>Input:</strong><br /><code className="block bg-gray-100 p-2 rounded">{codeExercise.testCases[0].input}</code></div>
                  <div className="font-semibold"><strong className='text-base'>Expected Output:</strong><br /><code className="block bg-gray-100 p-2 rounded">{codeExercise.testCases[0].output}</code></div>
                </div>
                
                {/* Custom Test Case */}
                <div className="bg-white font-mono rounded-2xl shadow-md p-6 text-sm text-gray-800 border border-gray-200">
                  <h3 className="font-semibold text-center text-xl mb-4"><FaMapPin className='inline pb-1' />Test your own testcase</h3>
                  <div className="font-semibold mb-4">
                    <strong className='text-base'>Input:</strong>
                    <br />
                    <input 
                      className="block bg-gray-100 p-2 rounded" 
                      type="text"
                      onChange={(e) => setCustomInput(e.target.value)}
                      value={customInput}
                    />
                  </div>
                  { isCustomCodeRunning && <LoadingSpinner message="Running Code..." /> }
                  { customOutput && 
                  <div className="font-semibold"><strong className='text-base'>Expected Output:</strong><br />
                    <code className="block bg-gray-100 p-2 rounded">{customOutput}</code>
                  </div>
                  }
                  <div className="flex justify-center items-center">
                    <button
                        onClick={handleRunCode}
                        className="bg-yellow-500 hover:bg-yellow-600 px-6 py-2 mt-4 rounded-full text-white font-semibold shadow transition"
                      >
                      <FaPlay className="inline mr-1" /> Run Code
                    </button>
                  </div>
                </div>
              </div>

            </div>

            {/* Action Buttons */}
            <div className="flex justify-center space-x-4">

              {/* Hint Button */}
              <button
                onClick={handleHint}
                className="bg-amber-100 hover:bg-amber-200 px-6 py-2 rounded-full text-amber-700 font-semibold shadow transition"
              >
                <FaLightbulb className='inline mr-1' /> Hint
              </button>

              {/* Reset Button */}
              <button
                onClick={handleReset}
                className="bg-gray-100 hover:bg-gray-200 px-6 py-2 rounded-full text-gray-700 font-semibold shadow transition"
              >
                <GrPowerReset className='inline mr-1' /> Reset
              </button>

              {/* Save Button */}
              <button
                onClick={handleSaveExercise}
                className="bg-sky-100 hover:bg-sky-200 px-6 py-2 rounded-full text-sky-700 font-semibold shadow transition"
              >
                <FaSave className='inline mr-1' /> Save
              </button>

              {/* Submit Button */}
              <button
                onClick={handleSubmitCode}
                className="bg-blue-500 hover:bg-blue-600 px-6 py-2 rounded-full text-white font-semibold shadow transition"
              >
                <FaRocket className="inline mr-1" /> Submit Code
              </button>

              {/* Next Exercise Button */}
              {status === 'success' && (exerciseIndex < exerciseList.length - 1) &&
                <button
                  onClick={handleNextExercise}
                  className="bg-green-100 hover:bg-green-200 px-6 py-2 rounded-full text-green-700 font-semibold shadow transition"
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

            {/* Testcase Loading Spinner */}
            { isTestcaseResultLoading && <LoadingSpinner message="Running Code..." /> }

            {/* Save Loading Spinner */}
            { isSaveExerciseLoading && <LoadingSpinner message="Saving Code..." /> }


            {/* Output (Run Code) */}
            {(runSample?.output) && (
              <div className="mt-6 bg-white rounded-2xl shadow-md p-4 border border-gray-200 max-w-md mx-auto text-center">
                <h3 className="font-semibold text-xl text-center mb-2">Output</h3>
                <code className="block w-full bg-gray-100 p-2 rounded text-left">
                  {runSample.output}
                </code>
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
