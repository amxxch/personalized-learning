import React, { useEffect } from 'react'
import { Course, Skill } from '../dto/response'
import { FaPlayCircle } from "react-icons/fa";
import { FaFilePen } from "react-icons/fa6";
import { FaHourglassStart } from "react-icons/fa6";
import { useNavigate } from 'react-router-dom';
import ProgressBar from './ProgressBar';

interface CourseHeaderProps {
    course: Course | null;
    currentChapter: Skill | null
    progressPercent: number;
    assessmentDone: boolean;
}

const CourseHeader = ({ course, currentChapter, progressPercent, assessmentDone } : CourseHeaderProps) => {
    const Navigate = useNavigate();

    const handleWholeLesson = () => {
        Navigate(`/course/${course?.courseId}/full-lesson/${currentChapter?.skillId}`, { replace: true });
    }

    return (
        <div>
            { course && 
            <div className="bg-white shadow-lg rounded-2xl p-8">
                <div className="flex md:flex-row md:items-center md:justify-between gap-6">
                    <div className="flex-1">
                    <h1 className="text-3xl font-bold text-gray-800 mb-2">
                        {course.title}
                    </h1>
                    <p className="text-gray-600 text-lg mb-4">{course.description}</p>
                    <div className="flex flex-wrap items-center gap-2 mb-4">
                        {/* Level tag */}
                        <span className="px-3 py-1 rounded-full bg-yellow-100 text-yellow-800 text-sm font-medium">
                            {course.level.charAt(0).toUpperCase() + course.level.slice(1).toLowerCase()}
                        </span>

                        {/* Language tags */}
                        {course.language && course.language.length > 0 &&
                            course.language.map((lang) => (
                            <span
                                key={lang}
                                className="px-3 py-1 rounded-full bg-green-100 text-green-800 text-sm font-medium"
                            >
                                {lang}
                            </span>
                            ))
                        }

                        {/* Tech focus tags */}
                        {course.techFocus && course.techFocus.length > 0 &&
                            course.techFocus.map((focus) => (
                            <span
                                key={focus}
                                className="px-3 py-1 rounded-full bg-purple-100 text-purple-800 text-sm font-medium"
                            >
                                {focus}
                            </span>
                            ))
                        }
                        </div>
                        {/* Progress Bar */}
                        <ProgressBar progressPercent={progressPercent} />
                    </div>
                    <div className="mt-4 md:mt-0 md:ml-4 w-full md:w-64">
                    { assessmentDone ? 
                    currentChapter && currentChapter.skillOrder !== 1 ? 
                        <button 
                            className="bg-pink-400 text-white w-full h-20 rounded-2xl font-semibold text-base 
                            shadow hover:bg-pink-700 flex justify-center items-center gap-2 text-center"
                            onClick={handleWholeLesson}
                        >
                            <FaPlayCircle className='inline text-2xl' />
                            <span className="text-lg font-semibold leading-snug text-white">
                            Continue Chapter {currentChapter.skillOrder}
                            </span>
                        </button> 
                        :
                        progressPercent === 0 ?
                            <button 
                                className="bg-pink-400 text-white w-full h-20 p-2 rounded-2xl font-semibold text-base 
                                shadow hover:bg-pink-700 flex justify-center items-center gap-2 text-center"
                                onClick={handleWholeLesson}
                            >
                                <FaHourglassStart className='inline text-2xl' />
                                <span className="text-lg font-semibold leading-snug text-white">
                                    Start the lesson
                                </span>
                            </button>
                            :
                            <button 
                                className="bg-green-300 text-green-700 w-full h-24 rounded-2xl font-semibold text-base shadow flex flex-col justify-center items-center gap-2 text-center"
                                disabled={true}
                            >
                                <span className="text-sm leading-snug font-normal">
                                    You have completed the course!
                                </span>
                            </button>
                        :
                        <button 
                            className="bg-pink-400 text-white w-full h-20 p-2 rounded-2xl font-semibold text-base 
                            shadow hover:bg-pink-00 flex justify-center items-center gap-2 text-center"
                            onClick={handleWholeLesson}
                        >
                            <FaFilePen className='inline text-5xl ml-8' />
                            <span className="text-lg font-semibold leading-snug text-white">
                                Complete Initial Assessment
                            </span>
                        </button>
                        }
                    </div>
                </div>
            </div>
            }
        </div>
    )
}

export default CourseHeader
