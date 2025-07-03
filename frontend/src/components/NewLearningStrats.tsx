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
import StatsBox from "./StatsBox";
import { Tabs, Tab } from "@mui/material";

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
  const [selectedTab, setSelectedTab] = useState(0);
  const [selectedChapter, setSelectedChapter] = useState<MasteryStats | null>(null);

  useEffect(() => {
    const fetchOverviewData = async () => {
      try {
        const overallRes = await axios.get('/api/v1/learning-stats/overall', {
          headers: { Authorization: `Bearer ${userToken}` },
          params: { userId }
        });

        setOverallStats(overallRes.data);

        const coursesRes = await axios.get('/api/v1/course/courses-taken', {
          headers: { Authorization: `Bearer ${userToken}` },
          params: { userId }
        });

        setCoursesTaken(coursesRes.data);
        setSelectedCourse(coursesRes.data[0].title);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchOverviewData();
  }, [userToken, userId]);

  useEffect(() => {
    const course = coursesTaken.find(c => c.title === selectedCourse);
    if (!course) return;

    axios.get('/api/v1/learning-stats/mastery', {
      headers: { Authorization: `Bearer ${userToken}` },
      params: { userId, courseId: course.courseId }
    })
      .then(res => {
        const sorted = res.data.sort((a: MasteryStats, b: MasteryStats) => a.chapterNumber - b.chapterNumber);
        setChapterPerformance(sorted);
      })
      .catch(err => console.error(err));
  }, [selectedCourse]);

  return (
    <div className="min-h-screen p-6">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-4xl font-bold text-center mb-8 text-gray-800">
          <GrScorecard className="inline mr-4" /> Your Learning Dashboard
        </h1>

        <Tabs value={selectedTab} onChange={(e, v: any) => setSelectedTab(v)} centered className="mb-6">
          <Tab label="Overview" />
          <Tab label="Course Insights" />
          <Tab label="Chapter Details" />
        </Tabs>

        {selectedTab === 0 && (
          <div>
            <h2 className="text-2xl font-semibold mb-4">Weekly Performance Summary</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              <StatsBox label="Exercises Completed" value={overallStats?.thisWeekExerciseStats?.totalExercises || 0} />
              <StatsBox label="Lessons Finished" value={overallStats?.thisWeekChapterStats || 0} />
              <StatsBox label="Quizzes Taken" value={overallStats?.thisWeekQuizStats?.totalQuestions || 0} />
            </div>

            <h2 className="text-2xl font-semibold mt-10 mb-4">Lifetime Summary</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              <StatsBox label="Total Exercises" value={overallStats?.allTimeExerciseStats?.totalExercises || 0} />
              <StatsBox label="Total Lessons" value={overallStats?.allTimeChapterStats || 0} />
              <StatsBox label="Total Quizzes" value={overallStats?.allTimeQuizStats?.totalQuestions || 0} />
            </div>
          </div>
        )}

        {selectedTab === 1 && (
          <div>
            <div className="flex items-center gap-4 mb-6">
              <label htmlFor="course-select" className="text-lg font-medium text-gray-700">Select Course:</label>
              <select
                id="course-select"
                value={selectedCourse}
                onChange={(e) => setSelectedCourse(e.target.value)}
                className="border rounded px-3 py-2 shadow-sm"
              >
                {coursesTaken.map(course => (
                  <option key={course.courseId} value={course.title}>{course.title}</option>
                ))}
              </select>
            </div>

            <div className="bg-white p-6 rounded-xl shadow mb-8">
              <h2 className="text-xl font-semibold text-gray-800 mb-4">Mastery Level per Chapter</h2>
              <ResponsiveContainer width="100%" height={250}>
                <LineChart data={chapterPerformance}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="chapterNumber" />
                  <YAxis domain={[0, 1]} />
                  <Tooltip />
                  <Line type="monotone" dataKey="masteryLevel" stroke="#10b981" strokeWidth={3} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {selectedTab === 2 && (
          <div>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {chapterPerformance.map(chapter => (
                <div
                  key={chapter.chapterNumber}
                  onClick={() => setSelectedChapter(chapter)}
                  className="bg-white hover:bg-orange-50 p-4 rounded-xl shadow cursor-pointer"
                >
                  <h3 className="text-lg font-semibold text-orange-700">Chapter {chapter.chapterNumber}</h3>
                  <p className="text-sm text-gray-600">{chapter.chapterName}</p>
                  <p className="mt-1 text-sm font-medium">Mastery: {(chapter.masteryLevel * 100).toFixed(0)}% ({classifyMasteryLevel(chapter.masteryLevel)})</p>
                </div>
              ))}
            </div>

            {/* {selectedChapter && (
              <ChapterDetailsModal chapter={selectedChapter} onClose={() => setSelectedChapter(null)} />
            )} */}
          </div>
        )}
      </div>
    </div>
  );
};

export default LearningStats;
