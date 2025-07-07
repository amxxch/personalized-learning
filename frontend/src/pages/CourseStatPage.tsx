import React, { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from "react-router-dom";
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

import { useAuth } from '../context/AuthContext';
import axios from 'axios';
import { Course, MasteryStats, QuizStats, Skill } from '../dto/response';

function classifyMasteryLevel(mastery: number) {
    if (mastery >= 0.8) return "Advanced";
    if (mastery >= 0.6) return "Intermediate";
    return "Beginner";
  }  

const CourseStatPage = () => {

  const { userToken, userId } = useAuth();

  const { courseId } = useParams();
  
  const [loading, setLoading] = useState(true);
  
  const [selectedChapter, setSelectedChapter] = useState<Skill | null>(null);
  const [chaptersTaken, setChaptersTaken] = useState<Skill[]>([]);
  const [chapterPerformance, setChapterPerformance] = useState<MasteryStats[]>([]);

  const [chapterQuizStats, setChapterQuizStats] = useState<QuizStats[]>([]);

  const navigate = useNavigate();  
    
    useEffect(() => {
      axios.get('http://localhost:8080/api/v1/learning-stats/mastery', {
        headers: { Authorization: `Bearer ${userToken}` },
        params: { userId, courseId: courseId }  
      })
      .then((response) => {
        const data = response.data;
        console.log("Chapter Performance Data:", data)
        data.sort((a: MasteryStats, b: MasteryStats) => a.chapterNumber - b.chapterNumber);
        setChapterPerformance(data);

        axios.get('http://localhost:8080/api/v1/course/skills-taken', {
          headers: { Authorization: `Bearer ${userToken}` },
          params: { userId, courseId: courseId }  
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

    }, []);

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
    <div className="min-h-screen flex">
        <main className="flex-1 px-20 py-12 flex flex-col gap-6">
        <div className="flex items-center justify-between px-4 mb-4">
            <button
                onClick={() => navigate(`/course/overview/${courseId}`, { replace: true })}
                className="bg-gray-200 hover:bg-gray-300 w-[90px] text-gray-800 px-4 py-2 rounded-lg font-medium transition"
            >
                ← Back
            </button>
            <h2 className="text-3xl font-bold text-gray-800 text-center flex-1">
                Course Performance Analysis
            </h2>
            <div className="w-[90px]">{/* spacer to center title */}</div>
        </div>

          <div className="space-y-10">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-start">
              {/* Mastery Performance Graphs */}
              <div className="bg-white p-6 rounded-xl shadow">
                <h2 className="text-xl font-semibold text-gray-800 mb-4">Mastery Level per Chapter</h2>
                <ResponsiveContainer width="100%" height={250}>
                <BarChart data={chapterPerformance}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="chapterNumber" />
                    <YAxis domain={[0, 1]} />
                    <Tooltip content={<MasteryCustomTooltip />} />
                    <Bar dataKey="masteryLevel" fill="#10b981" radius={[6, 6, 0, 0]} />
                </BarChart>
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
                        <Link to={`/course/${courseId}/review/${item.skillId}`} className="hover:underline">
                          <strong>Chapter {item.chapterNumber}:</strong> {item.chapterName} ({(item.masteryLevel * 100).toFixed(0)}%)
                        </Link>
                      </div>
                    ))}
                  </div>
                  <div className="bg-yellow-50 p-4 rounded">
                    <h3 className="font-bold text-yellow-700 mb-2 text-lg">Intermediate</h3>
                    {chapterPerformance.filter(c => classifyMasteryLevel(c.masteryLevel) === "Intermediate").map(item => (
                      <div key={item.chapterNumber} className="ml-2 text-md">
                        <Link to={`/course/${courseId}/review/${item.skillId}`} className="hover:underline">
                          <strong>Chapter {item.chapterNumber}:</strong> {item.chapterName} ({(item.masteryLevel * 100).toFixed(0)}%)
                        </Link>
                      </div>
                    ))}
                  </div>
                  <div className="bg-red-50 p-4 rounded">
                    <h3 className="font-bold text-red-700 mb-2 text-lg">Beginner</h3>
                    {chapterPerformance.filter(c => classifyMasteryLevel(c.masteryLevel) === "Beginner").map(item => (
                      <div key={item.chapterNumber} className="ml-2 text-md">
                        <Link to={`/course/${courseId}/review/${item.skillId}`} className="hover:underline">
                          <strong>Chapter {item.chapterNumber}:</strong> {item.chapterName} ({(item.masteryLevel * 100).toFixed(0)}%)
                        </Link>
                      </div>
                    ))}
                  </div>
                </div>
                </div>
            </div>
          </div>

            <div className="mt-4 ml-4">
            <label htmlFor="course-select" className="text-lg font-medium text-gray-700 mb-4 mr-4">
            Select Chapter:
            </label>
            <select
            id="course-select"
            value={selectedChapter ? selectedChapter.skillName : ''}
            onChange={(e) => setSelectedChapter(chaptersTaken.find(s => s.skillName === e.target.value) || null)}
            className="border w-1/3 rounded px-3 py-2 shadow-sm focus:outline-none mb-4"
            >
            {chaptersTaken.map((chapter) => (
                <option key={chapter.skillId} value={chapter.skillName}>
                Chapter {chapter.skillOrder}: {chapter.skillName}
                </option>
            ))}
            </select>
            </div>
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
          </main>
        </div>
  )
}

export default CourseStatPage
