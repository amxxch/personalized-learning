import { GiProgression } from "react-icons/gi";
import { GrScorecard } from "react-icons/gr";
import { FaArrowUpRightFromSquare } from "react-icons/fa6";
import axios from "axios";
import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { OverallStats, QuizStats, ExerciseStats, Course, MasteryStats, Skill, TechFocus, techFocusReport } from "../dto/response";
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
import { buildStyles, CircularProgressbar } from 'react-circular-progressbar';
import 'react-circular-progressbar/dist/styles.css';
import StatsBox from "./StatsBox";
import { Tabs, Tab } from "@mui/material";
import { Link, Navigate, useNavigate } from "react-router-dom";
import TechSkillChart from "./TechSkillChart";

function classifyMasteryLevel(mastery: number) {
  if (mastery >= 0.8) return "Advanced";
  if (mastery >= 0.6) return "Intermediate";
  return "Beginner";
}

const levels = [
  { name: "Novice", start: 0.0, end: 0.2 },
  { name: "Emerging", start: 0.2, end: 0.4 },
  { name: "Average", start: 0.4, end: 0.6 },
  { name: "Above Average", start: 0.6, end: 0.8 },
  { name: "Expert", start: 0.8, end: 1.0 },
];

const LearningStats = () => {
  const { userToken, userId } = useAuth();

  const [selectedTab, setSelectedTab] = useState(0);
  
  const [loading, setLoading] = useState(true);
  const [overallStats, setOverallStats] = useState<OverallStats | null>(null);
  const [maxExerciseStats, setMaxExerciseStats] = useState<number>(40);
  const [maxChapterStats, setMaxChapterStats] = useState<number>(40);
  const [maxQuizStats, setMaxQuizStats] = useState<number>(40);
  
  const [selectedTechFocusStr, setSelectedTechFocusStr] = useState('');
  const [techFocusList, setTechFocusList] = useState<TechFocus[]>([]);
  const [techFocusScore, setTechFocusScore] = useState<number>(0);
  const [techFocusProgress, setTechFocusProgress] = useState<number>(0);

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
    
          const techRes = await axios.get('http://localhost:8080/api/v1/user/tech-focus', {
            headers: { Authorization: `Bearer ${userToken}` },
            params: { userId }
          });
    
          const techData: TechFocus[] = techRes.data;
          setTechFocusList(techData);
          setSelectedTechFocusStr(techData[0].techFocusName); // Set first course as default
        } catch (error) {
          console.error('Error fetching course overview:', error);
        } finally {
          setLoading(false);
        }
      };
    
      fetchOverviewData();
    }, [userId, userToken]);   

    useEffect(() => {
      if (!selectedTechFocusStr || techFocusList.length === 0) return;
      const techFocusId = techFocusList.find(tech => tech.techFocusName === selectedTechFocusStr)?.techFocusId;
      axios.get('http://localhost:8080/api/v1/tech-focus/report', {
        headers: { Authorization: `Bearer ${userToken}` },
        params: { userId, technicalFocusId: techFocusId }
      })
      .then((response) => {
        const report: techFocusReport = response.data;
        console.log("Mastery Score:", report);
        setTechFocusScore(report.score);
        setTechFocusProgress(parseFloat(report.progress.toFixed(2)));
      })
    }, [selectedTechFocusStr])
    

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
            <label htmlFor="course-select" className="text-lg font-medium text-gray-700">
              Select Course:
            </label>
            <select
              id="course-select"
              value={selectedTechFocusStr}
              onChange={(e) => setSelectedTechFocusStr(e.target.value)}
              className="border rounded px-3 py-2 shadow-sm focus:outline-none"
            >
              {techFocusList.map((tech) => (
                <option key={tech.techFocusId} value={tech.techFocusName}>
                  {tech.techFocusName}
                </option>
              ))}
            </select>
          </div>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 items-start">
              <div className="col-span-2">
              <TechSkillChart userScore={techFocusScore} />
              </div>
              <div className="col-span-1 flex flex-col items-center bg-white p-8 rounded-2xl shadow-lg space-y-6">
                <h2 className="text-2xl font-semibold text-gray-800 text-center leading-tight">
                  🚀 You're <span className="text-pink-500 font-bold">{techFocusProgress}%</span> through the
                  <span className="text-indigo-600"> {selectedTechFocusStr} Roadmap</span>!
                </h2>

                <div className="w-36 h-36">
                  <CircularProgressbar
                    value={techFocusProgress}
                    text={`${techFocusProgress}%`}
                    styles={buildStyles({
                      textColor: "#374151",
                      pathColor: techFocusProgress >= 70 
                        ? "#10B981" // green
                        : techFocusProgress >= 30
                          ? "#FBBF24" // yellow
                          : "#EF4444", // red,
                      trailColor: "#E5E7EB",
                      textSize: "16px",
                      pathTransitionDuration: 0.5,
                    })}
                  />
                </div>
                <button 
                  className="px-6 py-3 bg-pink-500 text-white rounded-full shadow-md hover:bg-pink-600 transition-all font-semibold text-sm"
                  onClick={() => navigate(`/profile/planner/${selectedTechFocusStr}`, { replace: true })}
                  >
                  View Course Roadmap
                </button>
            </div>
            </div>
        </>)}
      </div>
    </div>
  );
}

export default LearningStats;
  
  