import { GiProgression } from "react-icons/gi";
import { GrScorecard } from "react-icons/gr";
import { FaArrowUpRightFromSquare } from "react-icons/fa6";
import axios from "axios";
import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { OverallStats, QuizStats, ExerciseStats, Course, MasteryStats, Skill } from "../dto/response";
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  Legend,
  LineChart,
  Line,
  CartesianGrid,
} from "recharts";
import StatsBox from "./StatsBox";
import { Tabs, Tab } from "@mui/material";
import { Link, Navigate, useNavigate } from "react-router-dom";

function classifyMasteryLevel(mastery: number) {
  if (mastery >= 0.8) return "Advanced";
  if (mastery >= 0.6) return "Intermediate";
  return "Beginner";
}


const LearningStats = () => {
  const { userToken, userId } = useAuth();

  const [selectedTab, setSelectedTab] = useState(0);
  
  const [loading, setLoading] = useState(true);
  const [overallStats, setOverallStats] = useState<OverallStats | null>(null);
  const [maxExerciseStats, setMaxExerciseStats] = useState<number>(40);
  const [maxChapterStats, setMaxChapterStats] = useState<number>(40);
  const [maxQuizStats, setMaxQuizStats] = useState<number>(40);
  
  const [selectedCourseStr, setSelectedCourseStr] = useState('');
  const [selectedCourseObject, setSelectedCourseObject] = useState<Course | null>(null);
  const [selectedChapter, setSelectedChapter] = useState<Skill | null>(null);
  const [chaptersTaken, setChaptersTaken] = useState<Skill[]>([]);
  const [coursesTaken, setCoursesTaken] = useState<Course[]>([]);
  const [chapterPerformance, setChapterPerformance] = useState<MasteryStats[]>([]);

  const [chapterQuizStats, setChapterQuizStats] = useState<QuizStats[]>([]);

  const navigate = useNavigate();

    useEffect(() => {
      const fetchOverviewData = async () => {
        try {
          const overallRes = await axios.get('http://localhost:8080/api/v1/learning-stats/overall', {
            headers: { Authorization: `Bearer ${userToken}` },
            params: { userId }
          });

          const response: OverallStats = overallRes.data;

          let maxExercise = response.exerciseStatsList.reduce((max, stat) => Math.max(max, stat.totalExercises), 0);
          let maxChapter = response.chapterStatsList.reduce((max, stat) => Math.max(max, stat.lessonCount), 0);
          let maxQuiz = response.quizStatsList.reduce((max, stat) => Math.max(max, stat.totalQuestions), 0);
          setMaxExerciseStats(maxExercise === 0 ? 4 : maxExercise + 2);
          setMaxChapterStats(maxChapter === 0 ? 4 : maxChapter + 2);
          setMaxQuizStats(maxQuiz === 0 ? 4 : maxQuiz + 2);

          console.log("Overall Stats:", response);

    
          setOverallStats(response);
    
          const coursesRes = await axios.get('http://localhost:8080/api/v1/course/courses-taken', {
            headers: { Authorization: `Bearer ${userToken}` },
            params: { userId }
          });
    
          const coursesData: Course[] = coursesRes.data;
          setCoursesTaken(coursesData);
          setSelectedCourseStr(coursesData[0].title); // Set first course as default
        } catch (error) {
          console.error('Error fetching course overview:', error);
        } finally {
          setLoading(false);
        }
      };
    
      fetchOverviewData();
    }, [userId, userToken]);   
    
    useEffect(() => {
      const course = coursesTaken.find(c => c.title === selectedCourseStr)
      if (!course) return;
      setSelectedCourseObject(course);
      console.log("Selected Course:", selectedCourseStr);
      console.log(course.courseId);
      axios.get('http://localhost:8080/api/v1/learning-stats/mastery', {
        headers: { Authorization: `Bearer ${userToken}` },
        params: { userId, courseId: course.courseId }  
      })
      .then((response) => {
        const data = response.data;
        console.log("Chapter Performance Data:", data)
        data.sort((a: MasteryStats, b: MasteryStats) => a.chapterNumber - b.chapterNumber);
        setChapterPerformance(data);

        axios.get('http://localhost:8080/api/v1/course/skills-taken', {
          headers: { Authorization: `Bearer ${userToken}` },
          params: { userId, courseId: course.courseId }  
        })
        .then((response) => {
          const data: Skill[] = response.data;
          console.log("Chapter Taken:", data)
          data.sort((a: Skill, b: Skill) => a.skillOrder - b.skillOrder);
          setSelectedChapter(data[0]);
          setChaptersTaken(data);
        })
        .catch(error => {
          console.error('Error fetching chapter performance data:', error);
        })
      })
      .catch(error => {
        console.error('Error fetching chapter performance data:', error);
      })

    }, [selectedCourseStr]);

    useEffect(() => {
      console.log("Selected Chapter:", selectedChapter);
      if (!selectedChapter) return;
      axios.get('http://localhost:8080/api/v1/learning-stats/chapter-quiz-stats', {
        headers: { Authorization: `Bearer ${userToken}` },
        params: { userId, skillId: selectedChapter.skillId }  
      })
      .then((response) => {
        const data: QuizStats[] = response.data;
        console.log("Quiz Stats:", data)
        if (data.length === 0) {
          setChapterQuizStats([]);
          return;
        }
        data.sort((a: QuizStats, b: QuizStats) => a.date.localeCompare(b.date));
        setChapterQuizStats(data);
      })
      .catch(error => {
        console.error('Error fetching chapter performance data:', error);
      })

    }, [selectedChapter]);

    const MasteryCustomTooltip: React.FC<{ active?: boolean; payload?: any[]; label?: string }> = ({ active, payload, label }) => {
      if (active && payload && payload.length) {
        const data = payload[0].payload;
        return (
          <div className="bg-white border border-gray-300 p-3 rounded shadow text-sm">
            <p><strong>Chapter {data.chapterNumber}:</strong> {data.chapterName}</p>
            <p><strong>Mastery:</strong> {(data.masteryLevel * 100).toFixed(0)}%</p>
          </div>
        );
      }
      return null;
    };

    const ExerciseCustomTooltip: React.FC<{ active?: boolean; payload?: any[]; label?: string }> = ({ active, payload, label }) => {
      if (active && payload && payload.length) {
        const data = payload[0].payload;
        return (
          <div className="bg-white border border-gray-300 p-3 rounded shadow text-sm">
            <p><strong>Total Exercises: </strong> {data.totalExercises}</p>
            <p className="text-green-600"><strong>Easy: </strong> {data.easyExercises}</p>
            <p className="text-orange-500"><strong>Medium: </strong> {data.mediumExercises}</p>
            <p className="text-red-700"><strong>Hard: </strong> {data.hardExercises}</p>
          </div>
        );
      }
      return null;
    };

    const ChapterCustomTooltip: React.FC<{ active?: boolean; payload?: any[]; label?: string }> = ({ active, payload, label }) => {
      if (active && payload && payload.length) {
        const data = payload[0].payload;
        return (
          <div className="bg-white border border-gray-300 p-3 rounded shadow text-sm">
            <p><strong>Lesson count: </strong> {data.lessonCount}</p>
          </div>
        );
      }
      return null;
    };

    const QuizCustomTooltip: React.FC<{ active?: boolean; payload?: any[]; label?: string }> = ({ active, payload, label }) => {
      if (active && payload && payload.length) {
        const data = payload[0].payload;
        return (
          <div className="bg-white border border-gray-300 p-3 rounded shadow text-sm">
            <p><strong>Correct Questions: </strong>{data.totalCorrectQuestions} / {data.totalQuestions}</p>
            <p className="text-green-600"><strong>Easy: </strong>{data.correctEasyQuestions} / {data.easyQuestions}</p>
            <p className="text-orange-500"><strong>Medium: </strong>{data.correctMediumQuestions} / {data.mediumQuestions}</p>
            <p className="text-red-700"><strong>Hard: </strong>{data.correctHardQuestions} / {data.hardQuestions}</p>
          </div>
        );
      }
      return null;
    };

  return (
    <div className="min-h-screen p-6">
      <div className="max-w-6xl mx-auto space-y-12">
        <h1 className="text-4xl font-bold text-gray-800 text-center">
          <GrScorecard className="inline mr-4"/>
          Your Learning Dashboard
        </h1>

        <Tabs value={selectedTab} onChange={(e, v: any) => setSelectedTab(v)} centered className="mb-6">
          <Tab label="Overview" />
          <Tab label="Weekly Insights" />
          <Tab label="Course Stats" />
        </Tabs>

        {/* Overview */}
        {selectedTab === 0 && (
        <div className="space-y-8">
          {/* Weekly Performance */}
          <h2 className="text-3xl text-center mt-2 font-semibold text-gray-800">Weekly Performance</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">

            <StatsBox
              label="Exercises Completed"
              value={overallStats?.thisWeekExerciseStats?.totalExercises || 0}
              change={Math.abs(overallStats?.exercisePercentGrowth || 0)}
              increase={overallStats?.exercisePercentGrowth == null
                ? undefined
                : overallStats.exercisePercentGrowth > 0}
              breakdown={{
                easy: overallStats?.thisWeekExerciseStats?.easyExercises || 0,
                medium: overallStats?.thisWeekExerciseStats?.mediumExercises || 0,
                hard: overallStats?.thisWeekExerciseStats?.hardExercises || 0,
              }}
            />

            <StatsBox
              label="Lessons Finished"
              value={overallStats?.thisWeekChapterStats || 0}
              change={Math.abs(overallStats?.chapterPercentGrowth || 0)}
              increase={overallStats?.chapterPercentGrowth == null
                ? undefined
                : overallStats.chapterPercentGrowth > 0}
            />

            <StatsBox
              label="Quizzes Taken"
              value={overallStats?.thisWeekQuizStats?.totalQuestions || 0}
              change={Math.abs(overallStats?.quizPercentGrowth || 0)}
              increase={overallStats?.quizPercentGrowth == null
                ? undefined 
                : overallStats.quizPercentGrowth > 0}
              breakdown={{
                easy: overallStats?.thisWeekQuizStats?.easyQuestions || 0,
                medium: overallStats?.thisWeekQuizStats?.mediumQuestions || 0,
                hard: overallStats?.thisWeekQuizStats?.hardQuestions || 0,
              }}
              correct={overallStats?.thisWeekQuizStats?.totalCorrectQuestions}
              correctRate={overallStats?.thisWeekQuizStats?.totalQuestions ? 
                parseInt((overallStats.thisWeekQuizStats.totalCorrectQuestions / overallStats.allTimeQuizStats.totalQuestions * 100).toFixed(0)) : 0}
              />
          </div>

          {/* All Time Performance */}
          <h2 className="text-3xl text-center mt-2 font-semibold text-gray-800">All-time Performance</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* Exercise Box */}
            <StatsBox
              label="Exercises Completed"
              value={overallStats?.allTimeExerciseStats?.totalExercises || 0}
              breakdown={{
                easy: overallStats?.allTimeExerciseStats?.easyExercises || 0,
                medium: overallStats?.allTimeExerciseStats?.mediumExercises || 0,
                hard: overallStats?.allTimeExerciseStats?.hardExercises || 0,
              }}
            />

            {/* Lesson Box */}
            <StatsBox
              label="Lessons Finished"
              value={overallStats?.allTimeChapterStats || 0}
            />

            {/* Quiz Box */}
            <StatsBox
              label="Quizzes Taken"
              value={overallStats?.allTimeQuizStats?.totalQuestions || 0}
              breakdown={{
                easy: overallStats?.allTimeQuizStats?.easyQuestions || 0,
                medium: overallStats?.allTimeQuizStats?.mediumQuestions || 0,
                hard: overallStats?.allTimeQuizStats?.hardQuestions || 0,
              }}
              correct={overallStats?.allTimeQuizStats?.totalCorrectQuestions}
              correctRate={overallStats?.allTimeQuizStats?.totalQuestions ? 
                parseInt((overallStats.allTimeQuizStats.totalCorrectQuestions / overallStats.allTimeQuizStats.totalQuestions * 100).toFixed(0)) : 0}
              />
            </div>
            
        </div>
        )}

        {/* Weekly Stats */}
        {selectedTab === 1 && (
          <div className="space-y-6">
            {/* Weekly Trend Graph */}
            <h2 className="text-3xl text-center font-semibold text-gray-800">Weekly Performance</h2>
            <div className="grid grid-cols-1 items-start sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {/* Exercise Graph */}
              <div className="col-span-2 bg-white p-6 rounded-xl shadow">
                <h2 className="text-xl font-semibold text-gray-800 mb-4">Exercises Completed per Week</h2>
                <ResponsiveContainer width="100%" height={250}>
                  <LineChart data={overallStats?.exerciseStatsList}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis domain={[0, maxExerciseStats]} />
                    <Tooltip content={<ExerciseCustomTooltip />} />
                    <Line type="monotone" dataKey="totalExercises" stroke="#10b981" strokeWidth={3} />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              {/* Exercise Box */}
              <StatsBox
                label="Exercises Completed"
                value={overallStats?.thisWeekExerciseStats?.totalExercises || 0}
                change={Math.abs(overallStats?.exercisePercentGrowth || 0)}
                increase={overallStats?.exercisePercentGrowth == null
                  ? undefined
                  : overallStats.exercisePercentGrowth > 0}
                breakdown={{
                  easy: overallStats?.thisWeekExerciseStats?.easyExercises || 0,
                  medium: overallStats?.thisWeekExerciseStats?.mediumExercises || 0,
                  hard: overallStats?.thisWeekExerciseStats?.hardExercises || 0,
                }}
              />

              {/* Lesson Count Graph */}
              <div className="col-span-2 bg-white p-6 rounded-xl shadow">
                <h2 className="text-xl font-semibold text-gray-800 mb-4">Lessons Completed per Week</h2>
                <ResponsiveContainer width="100%" height={250}>
                  <LineChart data={overallStats?.chapterStatsList}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis domain={[0, maxChapterStats]} />
                    <Tooltip content={<ChapterCustomTooltip />} />
                    <Line type="monotone" dataKey="lessonCount" stroke="#10b981" strokeWidth={3} />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              {/* Lesson Box */}
              <StatsBox
                label="Lessons Finished"
                value={overallStats?.thisWeekChapterStats || 0}
                change={Math.abs(overallStats?.chapterPercentGrowth || 0)}
                increase={overallStats?.chapterPercentGrowth == null
                  ? undefined
                  : overallStats.chapterPercentGrowth > 0}
              />

              {/* Quiz Graph */}
              <div className="col-span-2 bg-white p-6 rounded-xl shadow">
                <h2 className="text-xl font-semibold text-gray-800 mb-4">Quiz / Review Questions Completed per Week</h2>
                <ResponsiveContainer width="100%" height={250}>
                  <LineChart data={overallStats?.quizStatsList}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis domain={[0, maxQuizStats]} />
                    <Tooltip content={<QuizCustomTooltip />} />
                    <Line type="monotone" dataKey="totalQuestions" stroke="#10b981" strokeWidth={3} />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              {/* Quiz Box */}
              <StatsBox
                label="Quizzes Taken"
                value={overallStats?.thisWeekQuizStats?.totalQuestions || 0}
                change={Math.abs(overallStats?.quizPercentGrowth || 0)}
                increase={overallStats?.quizPercentGrowth == null
                  ? undefined 
                  : overallStats.quizPercentGrowth > 0}
                breakdown={{
                  easy: overallStats?.thisWeekQuizStats?.easyQuestions || 0,
                  medium: overallStats?.thisWeekQuizStats?.mediumQuestions || 0,
                  hard: overallStats?.thisWeekQuizStats?.hardQuestions || 0,
                }}
                correct={overallStats?.thisWeekQuizStats?.totalCorrectQuestions}
                correctRate={overallStats?.thisWeekQuizStats?.totalQuestions ? 
                  parseInt((overallStats.thisWeekQuizStats.totalCorrectQuestions / overallStats.allTimeQuizStats.totalQuestions * 100).toFixed(0)) : 0}
                />
            </div>
          </div>
        )}

        {/* Courses Stats */}
        {selectedTab === 2 && (
        <>
          {/* Course Selection */}
          <div className="flex items-center gap-4 mt-12">
            <label htmlFor="course-select" className="text-lg font-medium text-gray-700 mb-4">
              Select Course:
            </label>
            <select
              id="course-select"
              value={selectedCourseStr}
              onChange={(e) => setSelectedCourseStr(e.target.value)}
              className="border rounded px-3 py-2 shadow-sm focus:outline-none mb-4"
            >
              {coursesTaken.map((course) => (
                <option key={course.courseId} value={course.title}>
                  {course.title}
                </option>
              ))}
            </select>
            <button 
            onClick={() => navigate(`/course/overview/${selectedCourseObject?.courseId}`)}
            className="btn btn-primary px-3 py-1 mb-4 ml-6"
            >
              <FaArrowUpRightFromSquare className="inline ml-2" />
              Go to course
            </button>
          </div>

          <div className="space-y-10">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Mastery Performance Graphs */}
              <div className="bg-white p-6 rounded-xl shadow">
                <h2 className="text-xl font-semibold text-gray-800 mb-4">Mastery Level per Chapter</h2>
                <ResponsiveContainer width="100%" height={250}>
                  <LineChart data={chapterPerformance}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="chapterNumber" />
                    <YAxis domain={[0, 1]} />
                    <Tooltip content={<MasteryCustomTooltip />} />
                    <Line type="monotone" dataKey="masteryLevel" stroke="#10b981" strokeWidth={3} />
                  </LineChart>
                </ResponsiveContainer>

                <div className="text-center mt-4">
                  <p className="text-gray-700 text-md font-bold">
                    Average Mastery Level:{" "}
                    <span className="font-semibold text-green-600">
                      {(
                        chapterPerformance.reduce((sum, chapter) => sum + chapter.masteryLevel, 0) /
                        chapterPerformance.length
                      ).toFixed(2)}
                    </span>
                  </p>
                </div>
              </div>

              <div className="bg-white p-6 rounded-xl shadow">
                {/* Mastery Level Classification */}
                <h2 className="text-xl font-semibold text-gray-800 mb-1">Chapter Mastery Classification</h2>
                <p className="text-gray-500 italic text-sm mb-4 ml-2">**Click to review each chapter</p>
                <div className="grid grid-cols-1 gap-4 text-sm text-gray-700">
                  <div className="bg-green-50 p-4 rounded">
                    <h3 className="font-bold text-green-700 mb-2 text-lg">Advanced</h3>
                    {chapterPerformance.filter(c => classifyMasteryLevel(c.masteryLevel) === "Advanced").map(item => (
                      <div key={item.chapterNumber} className="ml-2 text-md">
                        <Link to={`/course/${selectedCourseObject?.courseId}/review/${item.skillId}`} className="hover:underline">
                          <strong>Chapter {item.chapterNumber}:</strong> {item.chapterName} ({(item.masteryLevel * 100).toFixed(0)}%)
                        </Link>
                      </div>
                    ))}
                  </div>
                  <div className="bg-yellow-50 p-4 rounded">
                    <h3 className="font-bold text-yellow-700 mb-2 text-lg">Intermediate</h3>
                    {chapterPerformance.filter(c => classifyMasteryLevel(c.masteryLevel) === "Intermediate").map(item => (
                      <div key={item.chapterNumber} className="ml-2 text-md">
                        <Link to={`/course/${selectedCourseObject?.courseId}/review/${item.skillId}`} className="hover:underline">
                          <strong>Chapter {item.chapterNumber}:</strong> {item.chapterName} ({(item.masteryLevel * 100).toFixed(0)}%)
                        </Link>
                      </div>
                    ))}
                  </div>
                  <div className="bg-red-50 p-4 rounded">
                    <h3 className="font-bold text-red-700 mb-2 text-lg">Beginner</h3>
                    {chapterPerformance.filter(c => classifyMasteryLevel(c.masteryLevel) === "Beginner").map(item => (
                      <div key={item.chapterNumber} className="ml-2 text-md">
                        <Link to={`/course/${selectedCourseObject?.courseId}/review/${item.skillId}`} className="hover:underline">
                          <strong>Chapter {item.chapterNumber}:</strong> {item.chapterName} ({(item.masteryLevel * 100).toFixed(0)}%)
                        </Link>
                      </div>
                    ))}
                  </div>
                </div>
            </div>
          </div>

          {selectedCourseStr !== '' && (
              <>
              <label htmlFor="course-select" className="text-lg font-medium text-gray-700 mb-4 mr-4">
              Select Chapter:
              </label>
              <select
                id="course-select"
                value={selectedChapter ? selectedChapter.skillName : ''}
                onChange={(e) => setSelectedChapter(chaptersTaken.find(s => s.skillName === e.target.value) || null)}
                className="border rounded px-3 py-2 shadow-sm focus:outline-none mb-4"
              >
                {chaptersTaken.map((chapter) => (
                  <option key={chapter.skillId} value={chapter.skillName}>
                    Chapter {chapter.skillOrder}: {chapter.skillName}
                  </option>
                ))}
              </select>
            </>
            )}
          {selectedChapter && (
            <div className="bg-white p-6 rounded-xl shadow">
            {/* Mastery Performance Graphs */}
              <h2 className="text-xl font-semibold text-gray-800 mb-4">Quiz / Review Performance History</h2>
              <ResponsiveContainer width="100%" height={250}>
                <LineChart data={chapterQuizStats}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis domain={[0, 5]} />
                  <Tooltip content={<QuizCustomTooltip />} />
                  <Line type="monotone" dataKey="totalCorrectQuestions" stroke="#10b981" strokeWidth={3} />
                </LineChart>
              </ResponsiveContainer>
              <div className="text-center mt-4">
                  <p className="text-gray-700 text-md font-bold">
                    Average Quiz Score:{" "}
                    <span className="font-semibold text-green-600">
                      {(
                        chapterQuizStats.reduce((sum, quiz) => sum + quiz.totalCorrectQuestions, 0) /
                        (chapterQuizStats.length)
                      ).toFixed(2) + " "}
                      / 5
                    </span>
                  </p>
                </div>
            </div>
          )}
        </div>
        </>)}
      </div>
    </div>
  );
}

export default LearningStats;
  
  