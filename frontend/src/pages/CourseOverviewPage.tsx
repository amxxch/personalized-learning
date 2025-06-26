import React, { useState, useEffect } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Course, Skill } from "../dto/response";
import LoadingSpinner from "../components/LoadingSpinner";
import CourseChapter from "../components/CourseChapter";
import CourseHeader from "../components/CourseHeader";

export default function CourseOverviewPage() {

    const { userId, userToken } = useAuth();
    const { courseId } = useParams();
    const parsedCourseId = courseId ? parseInt(courseId) : null;

    const [isLoading, setLoading] = useState(true);
    const [courseOverview, setCourseOverview] = useState<Course | null>(null);
    const [assessmentDone, setAssessmentDone] = useState(false);
    const [progressPercent, setProgressPercent] = useState(0);
    const [currentChapter, setCurrentChapter] = useState<Skill | null>(null);

    useEffect(() => {
        axios.get('http://localhost:8080/api/v1/course/overview', {
            headers: {
                Authorization: `Bearer ${userToken}`
            },
            params: {
                userId: userId,
                courseId: parsedCourseId,
            }
        })
        .then((response) => {
            const data: Course = response.data;
            console.log('Course Overview:', response.data);
            let totalSkills = data.skills.length;
            let completedSkills = data.skills.filter(skill => skill.completed).length;
            let percentage = (completedSkills / totalSkills) * 100;
            setProgressPercent(
                data.skills.length > 0 ? parseFloat(percentage.toFixed(2)) : 0
            );

            let candidate = data.skills.find(skill => skill.unlocked && !skill.completed) || null;
            if (candidate === null) {
                candidate = data.skills.find(skill => !skill.unlocked) || null;
            }
            if (candidate !== null) {
                data.skills.find(skill => skill.skillId === candidate?.skillId)!.unlocked = true;
            }
            console.log("candidate: ", candidate);
            setCourseOverview(data);
            setCurrentChapter(candidate);
            setAssessmentDone(data.assessmentDone);
            setLoading(false);
        })
        .catch((error) => {
            console.error('Error fetching course overview:', error);
            setLoading(false);
        });
    }, []);

    return (
        <div className="min-h-screen px-6 py-10 font-sans">
            { isLoading && <LoadingSpinner message="Loading course overview..." /> }
            
            <div className="max-w-5xl mx-auto space-y-8">
                {/* Course Header */}
                <CourseHeader course={courseOverview} currentChapter={currentChapter} progressPercent={progressPercent} assessmentDone={assessmentDone} />

                {/* Chapters */}
                <div className="space-y-6">
                { courseOverview && courseOverview.skills && courseOverview.skills.map((chapter, index) => (
                    <div
                    key={index}
                    >
                        <CourseChapter chapter={chapter} assessmentDone={assessmentDone} />
                    </div>
                ))}
                </div>
            </div>
        </div>
    );
}
