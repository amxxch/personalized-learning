import React from 'react'
import { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from './LoadingSpinner';

interface ProfileSetupProps {
    setIsStart: (value: boolean) => void;
}

const ProfileSetupForm = ({ setIsStart } : ProfileSetupProps) => {
    const [languages, setLanguages] = useState<string[]>([]);
    const [techFocus, setTechFocus] = useState<string[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    const [selectedLanguages, setSelectedLanguages] = useState<string[]>([]);
    const [selectedTechFocus, setSelectedTechFocus] = useState<string[]>([]);
    const [learningHours, setLearningHours] = useState<number>(12);
    const [experienceLevel, setExperienceLevel] = useState<string>("");
    const [careerGoal, setCareerGoal] = useState<string>("");

    const Navigate = useNavigate();
    const { userId, isProfileSetup, setIsProfileSetup } = useAuth();

    useEffect(() => {
        // Fetching languages and tech focus from the backend
        axios.get('http://localhost:8080/api/v1/profile-setup', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        })
        .then(response => {
            const data = response.data;
            const languagesList = data.languages;
            const techFocusList = data.techFocuses;
            setLanguages(languagesList);
            setTechFocus(techFocusList);
        })
        .catch(error => {
            console.error('Error fetching profile setup data:', error);
        });

        axios.get('http://localhost:8080/api/v1/user', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            params: {
                userId: userId,
            }
        }).then(response => {
            const userData = response.data;
            if (isProfileSetup) {
                console.log("profile initial setup data: ", userData);
                console.log(userData.knownLanguages, userData.technicalFocuses, userData.experienceLevel, userData.careerGoal, userData.weeklyLearningHours);
                setSelectedLanguages(userData.knownLanguages || []);
                setSelectedTechFocus(userData.technicalFocuses || []);
                setExperienceLevel(userData.experienceLevel || "");
                setCareerGoal(userData.careerGoal || "");
                setLearningHours(userData.weeklyLearningHours || 12);
            }
            setIsLoading(false);
        })
        .catch(error => {
            console.error('Error fetching profile setup data:', error);
        });
    }, [userId]);

    const handleSubmit = (e: React.FormEvent) => {
        setIsLoading(true);
        e.preventDefault();

        if (!experienceLevel || selectedLanguages.length === 0 || selectedTechFocus.length === 0 || !careerGoal) {
            alert("Please fill in all fields before submitting.");
            setIsLoading(false);
            return;
        }

        const profileData = {
            userId,
            experienceLevel,
            knownLanguages: selectedLanguages,
            technicalFocuses: selectedTechFocus,
            careerGoal,
            weeklyLearningHours: learningHours
        };

        axios.post('http://localhost:8080/api/v1/profile-setup', profileData, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        })
        .then(response => {
            console.log('Profile setup successful:', response.data);

            // Generate personalized learning path
            axios.post('http://localhost:8080/api/v1/profile-setup/roadmap', 
            { userId }, 
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }
            })
            .then(response => {
                console.log("Course Prediction Successful", response.data);
                if (!response.data || response.data.length === 0) {
                    alert("Roadmap is empty — generation failed or not ready.");
                    return;
                }
                setIsProfileSetup(true);
                setIsStart(false);
                setIsLoading(false);
                if (isProfileSetup) {
                    Navigate("/profile", { replace: true });
                } else {
                    Navigate("/profile/planner", { replace: true });
                }
            })
            .catch(error => {
                console.error('Error setting up profile:', error);
                alert("Failed to set up profile. Please try again.");
            })
        })
        .catch(error => {
            console.error('Error setting up profile:', error);
            alert("Failed to set up profile. Please try again.");
            setIsLoading(false);
        })
    }

    const handleBack = () => {
        if (isProfileSetup) {
            Navigate("/profile", { replace: true });
        } else {
            Navigate("/profile-setup", { replace: true });
        }
    }

  return (
    <div className="max-w-5xl mx-auto p-6 bg-white rounded-2xl shadow-md mt-2">
        <div className="space-y-6">
            { isLoading && <LoadingSpinner message="Setting up your profile..." /> }
            { !isLoading &&
            <div>
                <div className="flex items-center justify-between px-4 mb-1">
                <button
                    onClick={handleBack}
                    className="bg-gray-200 hover:bg-gray-300 w-[90px] text-gray-800 px-4 py-2 rounded-lg font-medium transition"
                >
                    ← Back
                </button>
                <h2 className="text-2xl font-bold text-gray-800 text-center flex-1">
                    Let's Set Up Your Profile
                </h2>
                <div className="w-[90px]">{/* spacer to center title */}</div>
                </div>
                <p className="text-gray-600 text-center">This will help us personalize your learning path.</p>
                
                <form className="space-y-8">

                    {/* Experience Level */}
                    <div>
                        <h2 className="text-xl font-semibold text-gray-800 my-3">Years of Experience in Programming</h2>
                        <div className="flex gap-6">
                        {["Less than 1 year", "1-2 years", "3-5 years","6-9 years", "10+ years"].map(level => (
                            <label key={level} className="flex items-center gap-2 text-gray-700">
                            <input 
                                type="radio" 
                                name="experience" 
                                value={level} 
                                checked={experienceLevel === level}
                                className="radio radio-sm border-rose-300 checked:bg-rose-300" 
                                onChange={(e) => setExperienceLevel(e.target.value)}
                            />
                            {level}
                            </label>
                        ))}
                        </div>
                    </div>

                    {/* Known Languages */}
                    <div>
                        <h2 className="text-xl font-semibold text-gray-800 mb-3">Known Tools or Framework</h2>
                        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                        {languages.map(lang => (
                            <label key={lang} className="flex items-center gap-2 text-gray-700">
                            <input 
                                type="checkbox" 
                                name="knownLanguages" 
                                value={lang} 
                                className="checkbox checkbox-xs" 
                                checked={selectedLanguages.includes(lang)}
                                onChange={(e) => {
                                    const value = e.target.value;
                                    setSelectedLanguages(prev => 
                                        prev.includes(value) ? prev.filter(l => l !== value) : [...prev, value]
                                    );
                                }}
                            />
                            {lang}
                            </label>
                        ))}
                        </div>
                    </div>

                    {/* Technical Focus */}
                    <div>
                        <h2 className="text-xl font-semibold text-gray-800 mb-3">Technical Focus</h2>
                        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                        {techFocus.map(tech => (
                            <label key={tech} className="flex items-center gap-2 text-gray-700">
                            <input 
                                type="checkbox" 
                                name="technicalFocus" 
                                value={tech} 
                                className="checkbox checkbox-neutral checkbox-xs" 
                                checked={selectedTechFocus.includes(tech)}
                                onChange={(e) => {
                                    const value = e.target.value;
                                    setSelectedTechFocus(prev => 
                                        prev.includes(value) ? prev.filter(t => t !== value) : [...prev, value]
                                    );
                                }}
                            />
                            {tech}
                            </label>
                        ))}
                        </div>
                    </div>

                    {/* Career Goal */}
                    <div>
                        <h2 className="text-xl font-semibold text-gray-800 mb-3">Career Goal</h2>
                        <input
                            type="text"
                            className="mt-1 w-full px-4 py-2 border rounded-lg shadow-sm focus:ring-rose-500 focus:border-rose-500"
                            placeholder="e.g. Frontend Developer, ML Engineer"
                            name="careerGoal"
                            value={careerGoal}
                            onChange={(e) => setCareerGoal(e.target.value)}
                        />
                    </div>

                    {/* Learning Hours per Week */}
                    <div>
                        <h2 className="text-xl font-semibold text-gray-800 mb-3">How many hours you plan to spend per week?</h2>
                        <div className="flex items-center gap-4">
                        <input 
                            type="range" 
                            name="learningHourPerWeek"
                            min="0"
                            max="24" 
                            className="range range-xs"
                            onChange={(e) => setLearningHours(parseInt(e.target.value))}
                        />
                        <span className="text-rose-400 font-bold text-lg">{learningHours} hrs</span>
                        </div>
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-rose-300 text-white font-bold py-2 px-4 rounded-lg hover:bg-rose-400 transition"
                        onClick={handleSubmit}
                    >
                        Submit Profile
                    </button>
                </form>
            </div>
            }
        </div>
    </div>
  )
}

export default ProfileSetupForm
