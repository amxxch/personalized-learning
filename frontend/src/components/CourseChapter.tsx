import React, { useEffect, useState } from 'react'
import { Skill } from '../dto/response'
import { useParams, useNavigate, replace } from 'react-router-dom'
import { FaCheckCircle } from "react-icons/fa";
import { FaBookReader } from "react-icons/fa";
import { FaLock } from "react-icons/fa";
import { BsThreeDots } from "react-icons/bs";
import { FaLaptopCode } from "react-icons/fa";
import { RiLoopLeftFill } from "react-icons/ri";

interface CourseChapterProps {
    chapter: Skill;
    assessmentDone: boolean;
}


const CourseChapter = ({ chapter, assessmentDone } : CourseChapterProps) => {
    const { courseId } = useParams();
    const [thisChapter, setThisChapter] = useState<Skill>(chapter);
    const Navigate = useNavigate();

    useEffect(() => {
        if (!assessmentDone) {
            setThisChapter(prev => ({
                ...prev,
                unlocked: false,
            }))

        }
    }, []);

    const handleExercise = () => {
        Navigate(`/course/${courseId}/exercise/${chapter.skillId}`);
    }

    const handleSpecificLesson = () => {
        Navigate(`/course/${courseId}/lesson/${chapter.skillId}`);
    }

    const handleReviewLesson = () => {
        Navigate(`/course/${courseId}/review/${chapter.skillId}`);
    }

    return (
        <div className={`bg-white p-6 rounded-2xl shadow flex flex-col lg:flex-row items-start 
                        lg:items-center justify-between gap-4 ${thisChapter.unlocked ? "" : "opacity-60"}`}>
            <div className="flex justify-between items-center">
                {/* Status */}
                <div className="min-w-[70px] flex">
                    {thisChapter.unlocked ? (
                        thisChapter.completed ? (
                            <FaCheckCircle className="text-4xl text-green-600" />
                        ) : (
                            <BsThreeDots className="text-4xl text-gray-600" />
                        )
                    ) : (
                        <FaLock className="text-3xl text-gray-400" />
                    )}
                </div>

                <div className="flex-1">
                    {/* Chapter Name */}
                    <h2 className="text-xl font-semibold text-gray-800">
                        Chapter {thisChapter.skillOrder} : {thisChapter.skillName}
                    </h2>
                    {/* Action Buttons */}
                    <div className="flex gap-4 flex-wrap mt-4">

                        {/* Lesson History */}
                        <button
                        className="bg-indigo-400 text-white rounded-xl px-4 py-2 font-medium shadow-md hover:bg-indigo-300 disabled:opacity-50"
                        disabled={!thisChapter.unlocked || !thisChapter.completed}
                        onClick={handleSpecificLesson}
                        >
                        <FaBookReader className='inline mb-1 text-lg mr-2' />Lesson History
                        </button>

                        {/* Coding Exercise */}
                        <button
                        className="bg-purple-400 text-white rounded-xl px-4 py-2 font-medium shadow-md hover:bg-purple-300 disabled:opacity-50"
                        disabled={!thisChapter.unlocked || !thisChapter.completed}
                        onClick={handleExercise}
                        >
                        <FaLaptopCode className='inline mb-1 text-lg mr-2' />Coding Exercise
                        </button>

                        {/* Review Lesson */}
                        <button
                        className="bg-blue-400 text-white rounded-xl px-4 py-2 font-medium shadow-md hover:bg-blue-300 disabled:opacity-50"
                        disabled={!thisChapter.unlocked || !thisChapter.completed}
                        onClick={handleReviewLesson}
                        >
                        <RiLoopLeftFill className='inline mb-1 text-lg mr-2' />Review Lesson
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default CourseChapter
