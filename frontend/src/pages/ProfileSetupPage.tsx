
import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import Typewriter from "typewriter-effect";

import ProfileSetupForm from "../components/ProfileSetupForm";

export default function HomePage() {
    const [isStart, setIsStart] = useState(false);
    const { isProfileSetup } = useAuth();

    return (
        <div>
            {!isProfileSetup && 
                <div 
                className="flex flex-col items-center min-h-screen min-w-screen p-8 sm:p-20 gap-8 font-[family-name:var(--font-geist-sans)]"
                style={{
                    backgroundImage: isStart ? '' : 'url("")',
                    backgroundSize: 'cover',
                    backgroundRepeat: 'no-repeat',
                  }}
            >
                <h1 className="min-w-screen text-5xl font-bold text-center font-mono tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-pink-300 to-pink-900">
                    Welcome to LearningBot!
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
    
                {isStart ? 
                    <ProfileSetupForm setIsStart={setIsStart} />
                : (
                    <div className="max-w-5xl mx-auto p-6 bg-white rounded-2xl shadow-md mt-4">
                    <div className="text-center">
                        <p className="text-gray-600 text-lg mt-2">Please set up your profile before getting started for a personalized learning path!</p>
                        <button className="btn btn btn-outline btn-secondary mt-6 px-6 py-2 text-lg text-white rounded-lg" onClick={() => setIsStart(true)}>
                            Get Started
                        </button>
                    </div>
                    </div>
                )}
                </div>
            }

            {isProfileSetup && <ProfileSetupForm setIsStart={setIsStart} />}
            
        </div>
    );
}
