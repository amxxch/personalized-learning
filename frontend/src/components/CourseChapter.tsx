import React, { useEffect, useState } from 'react'
import { Skill } from '../dto/response'
import { useParams, useNavigate } from 'react-router-dom'
import { FaPlayCircle } from "react-icons/fa";
import { FaCheckCircle } from "react-icons/fa";
import { FaLock } from "react-icons/fa";
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
        Navigate(`/exercise/${courseId}/${chapter.skillId}`);
    }

    const handleSpecificLesson = () => {
        Navigate(`/course/${courseId}/lesson/${chapter.skillId}`);
    }

    return (
        <div className="flex flex-col">
            <div className="flex justify-between items-center">
                {/* Status */}
                <div className="min-w-[70px] flex">
                    {thisChapter.unlocked ? (
                        thisChapter.completed ? (
                            <FaCheckCircle className="text-4xl text-green-600" />
                        ) : (
                            <FaPlayCircle className="text-4xl text-orange-500" />
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
                        <button
                        className="bg-yellow-500 text-white rounded-xl px-4 py-2 font-medium shadow-md hover:bg-yellow-600 disabled:opacity-50"
                        disabled={!thisChapter.unlocked}
                        onClick={handleSpecificLesson}
                        >
                        <RiLoopLeftFill className='inline mb-1 text-lg mr-2' />Review Lesson
                        </button>
                        <button
                        className="bg-emerald-600 text-white rounded-xl px-4 py-2 font-medium shadow-md hover:bg-emerald-700 disabled:opacity-50"
                        disabled={!thisChapter.unlocked}
                        onClick={handleExercise}
                        >
                        <FaLaptopCode className='inline mb-1 text-lg mr-2' />Coding Exercise
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default CourseChapter
