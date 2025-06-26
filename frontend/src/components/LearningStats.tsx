import { GiProgression } from "react-icons/gi";
import { GrScorecard } from "react-icons/gr";
import axios from "axios";
import React, { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { OverallStats, QuizStats, ExerciseStats, Course, MasteryStats } from "../dto/response";
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
import { ArrowUpRight, ArrowDownRight } from "lucide-react";
import StatsBox from "./StatsBox";

const courses = ["C++ Fundamentals", "Python Basics", "Web Development"];

const chapterPerformanceData = [
  { chapter: "Ch 1", score: 78, mastery: 0.6, timeSpent: 45 },
  { chapter: "Ch 2", score: 84, mastery: 0.7, timeSpent: 50 },
  { chapter: "Ch 3", score: 70, mastery: 0.65, timeSpent: 60 },
  { chapter: "Ch 4", score: 90, mastery: 0.85, timeSpent: 35 },
  { chapter: "Ch 5", score: 88, mastery: 0.9, timeSpent: 40 },
];

const loginCalendar = [
  "2024-06-01",
  "2024-06-03",
  "2024-06-04",
  "2024-06-06",
  "2024-06-09",
  "2024-06-10",
];

const longestStreak = 4;

function classifyMasteryLevel(mastery: number) {
  if (mastery >= 0.8) return "Advanced";
  if (mastery >= 0.6) return "Intermediate";
  return "Beginner";
}


const LearningStats = () => {
  const { userToken, userId } = useAuth();

  const [loading, setLoading] = useState(true);
  const [overallStats, setOverallStats] = useState<OverallStats | null>(null);

  const [selectedCourse, setSelectedCourse] = useState('');
  const [coursesTaken, setCoursesTaken] = useState<Course[]>([]);
  const [chapterPerformance, setChapterPerformance] = useState<MasteryStats[]>([]);
  const weakestTopics = chapterPerformanceData
    .filter((c) => c.mastery < 0.7)
    .sort((a, b) => a.mastery - b.mastery)
    .slice(0, 2);

    useEffect(() => {
      const fetchOverviewData = async () => {
        try {
          const overallRes = await axios.get('http://localhost:8080/api/v1/learning-stats/overall', {
            headers: { Authorization: `Bearer ${userToken}` },
            params: { userId }
          });

          console.log("Overall Stats:", overallRes.data);
    
          setOverallStats(overallRes.data);
    
          const coursesRes = await axios.get('http://localhost:8080/api/v1/course/courses-taken', {
            headers: { Authorization: `Bearer ${userToken}` },
            params: { userId }
          });
    
          const coursesData: Course[] = coursesRes.data;
          setCoursesTaken(coursesData);
          setSelectedCourse(coursesData[0].title); // Set first course as default
        } catch (error) {
          console.error('Error fetching course overview:', error);
        } finally {
          setLoading(false);
        }
      };
    
      fetchOverviewData();
    }, [userId, userToken]);   
    
    useEffect(() => {
      const course = coursesTaken.find(c => c.title === selectedCourse)
      if (!course) return;
      console.log("Selected Course:", selectedCourse);
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
      })
      .catch(error => {
        console.error('Error fetching chapter performance data:', error);
      })

    }, [selectedCourse]);

    const CustomTooltip: React.FC<{ active?: boolean; payload?: any[]; label?: string }> = ({ active, payload, label }) => {
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
    

  return (
    <div className="min-h-screen px-6 py-10">
      <div className="max-w-6xl mx-auto space-y-12">
        <h1 className="text-3xl font-bold text-gray-800"><GrScorecard className="inline mr-4 text-4xl"/>Your Learning Dashboard</h1>

        {/* Weekly and Lifetime Performance */}
        <div className="space-y-6">
          <h2 className="text-xl font-semibold text-gray-800">Weekly Performance</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
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

            {/* Lesson Box */}
            <StatsBox
              label="Lessons Finished"
              value={overallStats?.thisWeekChapterStats || 0}
              change={Math.abs(overallStats?.chapterPercentGrowth || 0)}
              increase={overallStats?.chapterPercentGrowth == null
                ? undefined
                : overallStats.chapterPercentGrowth > 0}
            />

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

          <h2 className="text-xl font-semibold text-gray-800">All-time Performance</h2>
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
        </div>

        {/* Course Selection */}
        <div className="flex items-center gap-4 mt-6">
          <label htmlFor="course-select" className="text-lg font-medium text-gray-700 mb-4">
            Select Course:
          </label>
          <select
            id="course-select"
            value={selectedCourse}
            onChange={(e) => setSelectedCourse(e.target.value)}
            className="border rounded px-3 py-2 shadow-sm focus:outline-none mb-4"
          >
            {coursesTaken.map((course) => (
              <option key={course.courseId} value={course.title}>
                {course.title}
              </option>
            ))}
          </select>
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
                  <Tooltip content={<CustomTooltip />} />
                  <Line type="monotone" dataKey="masteryLevel" stroke="#10b981" strokeWidth={3} />
                </LineChart>
              </ResponsiveContainer>
            </div>
            <div className="bg-white p-6 rounded-xl shadow">

            {/* Mastery Level Classification */}
            <h2 className="text-xl font-semibold text-gray-800 mb-4">Chapter Mastery Classification</h2>
            <div className="grid grid-cols-1 gap-4 text-sm text-gray-700">
              <div className="bg-green-50 p-4 rounded">
                <h3 className="font-bold text-green-700 mb-2 text-lg">Advanced</h3>
                {chapterPerformance.filter(c => classifyMasteryLevel(c.masteryLevel) === "Advanced").map(item => (
                  <div key={item.chapterNumber} className="ml-2 text-md">
                    <strong>Chapter {item.chapterNumber}:</strong> {item.chapterName} ({(item.masteryLevel * 100).toFixed(0)}%)
                  </div>
                ))}
              </div>
              <div className="bg-yellow-50 p-4 rounded">
                <h3 className="font-bold text-yellow-700 mb-2 text-lg">Intermediate</h3>
                {chapterPerformance.filter(c => classifyMasteryLevel(c.masteryLevel) === "Intermediate").map(item => (
                  <div key={item.chapterNumber} className="ml-2 text-md">
                    <strong>Chapter {item.chapterNumber}:</strong> {item.chapterName} ({(item.masteryLevel * 100).toFixed(0)}%)
                  </div>
                ))}
              </div>
              <div className="bg-red-50 p-4 rounded">
                <h3 className="font-bold text-red-700 mb-2 text-lg">Beginner</h3>
                {chapterPerformance.filter(c => classifyMasteryLevel(c.masteryLevel) === "Beginner").map(item => (
                  <div key={item.chapterNumber} className="ml-2 text-md">
                    <strong>Chapter {item.chapterNumber}:</strong> {item.chapterName} ({(item.masteryLevel * 100).toFixed(0)}%)
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LearningStats
  
  