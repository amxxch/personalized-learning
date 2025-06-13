import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAuth } from '../context/AuthContext';

interface TechnicalFocusRoadmap {
  technicalFocus: string;
  totalEstimatedDurationWeeks: number;
  roadmap: Roadmap[];
}

interface Roadmap {
  sequence: number;
  courseId: number;
  courseTitle: string;
  languages: string[];
  courseLevel: string;
  estimatedDurationWeeks: number;
  rationale: string;
}


const CourseRoadmapView = () => {
  const navigate = useNavigate();
  const { userId } = useAuth();
  const [isLoading, setIsLoading] = useState(true);

  const [techFocusRoadmapList, setTechFocusRoadmapList] = useState<TechnicalFocusRoadmap[]>([]);
  const [activeTechFocus, setActiveTechFocus] = useState('');

  useEffect(() => {
    axios.get('http://localhost:8080/api/v1/user/roadmap', {
      headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      params: {
          userId: userId,
      }
  }).then(response => {
      const data: TechnicalFocusRoadmap[] = response.data;
      console.log(data)
      setTechFocusRoadmapList(data);
      setActiveTechFocus(data[0]?.technicalFocus || '');
  })
  .catch(error => {
      console.error('Error fetching profile setup data:', error);
  });
  }, [])

  return (
    <div className="p-6 space-y-6 max-w-6xl mx-auto">
    {/* Header */}
    <div className="text-center">
      <h1 className="text-4xl font-bold tracking-tight">Your Personalized Learning Roadmap</h1>
      <p className="text-gray-600 text-lg mt-2">Follow the steps to reach your learning goal</p>
    </div>

    {/* Tech Focus Tabs */}
    <div className="flex justify-center gap-4 flex-wrap">
      {techFocusRoadmapList.map((list) => (
        <button
          key={list.technicalFocus}
          onClick={() => setActiveTechFocus(list.technicalFocus)}
          className={`px-4 py-2 rounded-full border text-sm font-medium transition ${
            activeTechFocus === list.technicalFocus
              ? 'bg-pink-400 text-white'
              : 'bg-gray-100 text-gray-700 hover:bg-pink-100'
          }`}
        >
          {list.technicalFocus}
        </button>
      ))}
    </div>

    {/* Card Slider */}
    <div className="w-full overflow-x-auto">
      <div className="relative flex space-x-6 px-2 pb-4 snap-x snap-mandatory scrollbar-hide">
        {techFocusRoadmapList
          .filter((techFocus) => techFocus.technicalFocus === activeTechFocus)
          .flatMap((techFocus) =>
            techFocus.roadmap.map((course, idx) => (
              <motion.div
                key={course.sequence}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: idx * 0.1 }}
                onClick={() => idx === 0 && navigate(`/courses/${course.courseId}/chat`)}
                className={`min-w-[320px] max-w-sm bg-white rounded-2xl shadow-xl p-6 snap-start flex-shrink-0 transform transition-transform duration-300 hover:scale-105 border ${
                  idx > 0 ? 'opacity-40 cursor-not-allowed' : 'cursor-pointer'
                }`}
              >
                <h2 className="text-sm font-medium text-gray-500 mb-1">Step {idx + 1}</h2>
                <p className="text-xl font-semibold mb-1">{course.courseTitle}</p>
                {course.languages.length > 0 && course.languages.map((lang, langIdx) => (
                  <span
                    key={langIdx}
                    className="bg-green-100 text-green-700 text-xs px-2 py-0.5 rounded-full font-medium mr-1"
                  >
                    {lang}
                  </span>
                ))}
                <p className="text-sm text-gray-600 mb-3 mt-2">{course.rationale}</p>
                <p className="text-sm mb-2">
                  <strong>Estimated:</strong> {course.estimatedDurationWeeks} weeks
                </p>

                {idx === 0 ? (
                  <div className="mt-3 w-full text-center bg-pink-400 text-white py-2 rounded-full text-sm font-medium">
                    Start Learning →
                  </div>
                ) : (
                  <p className="text-xs italic text-gray-400 mt-4">
                    Unlocks after completing previous step
                  </p>
                )}
              </motion.div>
            ))
          )}
      </div>
    </div>
  </div>

  );
};

export default CourseRoadmapView;
