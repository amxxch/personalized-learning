import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAuth } from '../context/AuthContext';
import { FaPlus } from "react-icons/fa";
import { set } from 'date-fns';

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
  status: 'COMPLETED' | 'CONTINUE' | 'LOCKED';
}

interface CourseRoadmapProps {
  technicalFocus?: string;
}

const CourseRoadmapView = ({ technicalFocus } : CourseRoadmapProps ) => {
    const Navigate = useNavigate();
    const { userId } = useAuth();
    const [isLoading, setIsLoading] = useState(true);

    const [techFocusRoadmapList, setTechFocusRoadmapList] = useState<TechnicalFocusRoadmap[]>([]);
    const [activeTechFocus, setActiveTechFocus] = useState(technicalFocus || '');
    const [currentCourseIndex, setCurrentCourseIndex] = useState(0);

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
        data.forEach((focus) => {
          focus.roadmap.forEach(course => {
            course.courseLevel = course.courseLevel.charAt(0).toUpperCase() + course.courseLevel.slice(1).toLowerCase();
          })
        });
        setTechFocusRoadmapList(data);
        console.log(data)
        setActiveTechFocus(data[0]?.technicalFocus || '');
        if (data.length > 0) {
          let index = data[0].roadmap.findIndex(course => course.status === 'CONTINUE');
          if (index === -1) {
            index = data[0].roadmap.findIndex(course => course.status === 'LOCKED');
          }
          setCurrentCourseIndex(index === -1 ? 0 : index);
        }
    })
    .catch(error => {
        console.error('Error fetching profile setup data:', error);
    });
    }, [userId])

    const handleAddTechFocus = (e: React.MouseEvent<HTMLSpanElement>) => {
      e.preventDefault();
      Navigate('/profile-setup', { replace: true });
    }

    useEffect(() => {

      const currentRoadmap = techFocusRoadmapList.find(list => list.technicalFocus === activeTechFocus);
      if (!currentRoadmap) return;
      let index = currentRoadmap.roadmap.findIndex(course => course.status === 'CONTINUE');
      if (index === -1) {
        index = currentRoadmap.roadmap.findIndex(course => course.status === 'LOCKED');
      }
      setCurrentCourseIndex(index === -1 ? 0 : index);
    }, [activeTechFocus]);

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
        <button
          onClick={handleAddTechFocus}
          className="px-4 py-2 rounded-full bg-white border border-dashed border-pink-400 text-pink-500 hover:bg-pink-50 text-sm font-medium transition"
        >
          <FaPlus className='inline mr-2' />Add Tech Focus
        </button>
      </div>

      {/* Card Slider */}
      <div className="w-full overflow-x-auto">
        <div className="relative flex space-x-6 px-2 pb-4 snap-x snap-mandatory scrollbar-hide">
          {techFocusRoadmapList
            .filter((techFocus) => techFocus.technicalFocus === activeTechFocus)
            .flatMap((techFocus) =>
              techFocus.roadmap.map((course, idx) => (
                <motion.div
                  key={course.courseId}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: idx * 0.1 }}
                  onClick={() => idx === 0 && Navigate(`/course/overview/${course.courseId}`)}
                  className={`min-w-[320px] max-w-sm bg-white rounded-2xl shadow-xl p-6 snap-start flex-shrink-0 transform transition-transform duration-300 hover:scale-105 border ${
                    course.status === 'LOCKED' && idx !== currentCourseIndex ? 'opacity-40 cursor-not-allowed' : 'cursor-pointer'
                  }`}
                >
                  <div className="flex flex-col h-full justify-between">
                    <div>
                      <h2 className="text-sm font-medium text-gray-500 mb-1">Step {idx + 1}</h2>
                      <p className="text-xl font-semibold mb-2">{course.courseTitle}</p>
                      <div className="flex flex-wrap items-center gap-2 mb-3">
                        <span className="bg-yellow-100 text-yellow-700 text-xs px-2 py-0.5 rounded-full font-medium mr-1">{course.courseLevel}</span>
                        {course.languages.length > 0 && course.languages.map((lang, langIdx) => (
                          <span
                            key={langIdx}
                            className="bg-green-100 text-green-700 text-xs px-2 py-0.5 rounded-full font-medium mr-1"
                          >
                            {lang}
                          </span>
                        ))}
                      </div>
                    <p className="text-sm text-gray-600 mb-3 mt-2">{course.rationale}</p>
                    <p className="text-sm mb-2">
                      <strong>Estimated:</strong> {course.estimatedDurationWeeks} weeks
                    </p>
                    </div>

                  {course.status === 'COMPLETED' && (
                    <div className="mt-3 w-full text-center bg-yellow-500 text-white py-2 rounded-full text-sm font-medium">
                      Reviewing Lesson →
                    </div>
                  )}

                  {course.status === 'CONTINUE' && (
                    <div className="mt-3 w-full text-center bg-pink-400 text-white py-2 rounded-full text-sm font-medium">
                      Resume Learning →
                    </div>
                  )}

                  {course.status === 'LOCKED' && idx !== currentCourseIndex && (
                    <p className="text-xs italic text-gray-400 mt-4">
                      Unlocks after completing previous step
                    </p>
                  )}

                  {course.status === 'LOCKED' && idx === currentCourseIndex && (
                    <div className="mt-3 w-full text-center bg-pink-400 text-white py-2 rounded-full text-sm font-medium">
                      Start Learning →
                    </div>
                  )}
                </div>
              </motion.div>
              ))
            )}
        </div>
      </div>
    </div>

  );
};

export default CourseRoadmapView;
