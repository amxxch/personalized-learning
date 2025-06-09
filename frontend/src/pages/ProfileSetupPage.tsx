import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Typewriter from "typewriter-effect";

export default function HomePage() {
    const [isStart, setIsStart] = useState(false);
    const [languages, setLanguages] = useState<string[]>([]);
    const [techFocus, setTechFocus] = useState<string[]>([]);

    const [selectedLanguages, setSelectedLanguages] = useState<string[]>([]);
    const [selectedTechFocus, setSelectedTechFocus] = useState<string[]>([]);
    const [learningHours, setLearningHours] = useState<number>(6);
    const [experienceLevel, setExperienceLevel] = useState<string>("");
    const [careerGoal, setCareerGoal] = useState<string>("");

    const Navigate = useNavigate();
    const { userId, setIsProfileSetup, username } = useAuth();

    useEffect(() => {
        // Fetching languages and tech focus from the backend
        axios.get('http://localhost:8080/api/v1/profile-setup', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        })
        .then(response => {
            const data = response.data;
            console.log(data)
            const languagesList = data.languages.map((lang: any) => lang.languageName);
            const techFocusList = data.techFocuses.map((tech: any) => tech.techFocusName);
            setLanguages(languagesList);
            setTechFocus(techFocusList);
        })
        .catch(error => {
            console.error('Error fetching profile setup data:', error);
        });
    }, []);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!experienceLevel || selectedLanguages.length === 0 || selectedTechFocus.length === 0 || !careerGoal) {
            alert("Please fill in all fields before submitting.");
            return;
        }

        const profileData = {
            userId,
            experienceLevel,
            preferredLanguages: selectedLanguages,
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
            setIsStart(false);
            setIsProfileSetup(true);
            Navigate('/', { replace: true });
        })
        .catch(error => {
            console.error('Error setting up profile:', error);
            alert("Failed to set up profile. Please try again.");
        });
    }

    return (
        <div className="flex flex-col items-center min-h-screen min-w-screen p-8 sm:p-20 gap-8 font-[family-name:var(--font-geist-sans)]">
            <h1 className="min-w-screen text-5xl font-bold text-center font-mono tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-pink-300 to-pink-900">
                Hi {username}, Welcome to LearningBot!
            </h1>

            <div className="text-lg md:text-xl font-semibold font-mono">      
                <Typewriter options={{
                strings: ['Your AI-Powered Learning Companion'],
                autoStart: true,
                loop: true,
                cursor: '_',
                delay: 50,
                deleteSpeed: 20,
                }}/>
            </div>

            <div className="max-w-5xl mx-auto p-6 bg-white rounded-2xl shadow-md mt-4">
            {isStart ? (
                <div className="space-y-6">
                <h2 className="text-2xl font-bold text-gray-800 text-center">Let's Set Up Your Profile</h2>
                <p className="text-gray-600 text-center">This will help us personalize your learning path.</p>
                
                <form className="space-y-8">

                    {/* Experience Level */}
                    <div>
                        <h2 className="text-xl font-semibold text-gray-800 mb-3">Years of Experience in Programming</h2>
                        <div className="flex gap-6">
                        {["Less than 1 year", "1-2 years", "3-5 years","6-9 years", "10+ years"].map(level => (
                            <label key={level} className="flex items-center gap-2 text-gray-700">
                            <input 
                                type="radio" 
                                name="experience" 
                                value={level} 
                                className="radio radio-sm border-rose-300 checked:bg-rose-300" 
                                onChange={(e) => setExperienceLevel(e.target.value)}
                            />
                            {level}
                            </label>
                        ))}
                        </div>
                    </div>

                    {/* Preferred Languages */}
                    <div>
                        <h2 className="text-xl font-semibold text-gray-800 mb-3">Preferred Languages</h2>
                        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                        {languages.map(lang => (
                            <label key={lang} className="flex items-center gap-2 text-gray-700">
                            <input 
                                type="checkbox" 
                                name="preferredLanguages" 
                                value={lang} 
                                className="checkbox checkbox-xs" 
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
            ) : (
                <div className="text-center">
                    <p className="text-gray-600 text-lg mt-2">Please set up your profile before getting started for a personalized learning path!</p>
                    <button className="btn btn-warning mt-6 px-6 py-2 text-lg text-white rounded-lg" onClick={() => setIsStart(true)}>
                        Get Started
                    </button>
                </div>
            )}
            </div>

        </div>
    );
}
