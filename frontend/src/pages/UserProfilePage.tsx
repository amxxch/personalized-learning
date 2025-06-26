import { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import ProfileOverview from "../components/ProfileOverview";
import LearningStats from "../components/LearningStats";
import CoursePlanner from "../components/CoursePlanner";
import DueTasks from "../components/DueTasks";
import { TechTopic } from "../dto/response";

const ProfilePage = () => {
    const [activeTab, setActiveTab] = useState<"dashboard" | "stats" | "planner" | "achievement">("dashboard");
    const { userId } = useAuth();
    const [ techFocus, setTechFocus ] = useState<TechTopic[]>([]);

    const tabs = [
        { label: "Dashboard", key: "dashboard" },
        { label: "Course Planner", key: "planner" },
        { label: "Learning Stats", key: "stats" },
        { label: "Achievement", key: "achievement" },
    ];

    useEffect(() => {
        // Fetching languages and tech focus from the backend
        axios.get('http://localhost:8080/api/v1/user', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            params: {
                userId: userId,
            }
        })
        .then(response => {
            const techData: TechTopic[] = response.data.technicalFocuses;
            setTechFocus(techData);
        })
        .catch(error => {
            console.error('Error fetching profile overview data:', error);
        });
    }, [userId]);

  return (
    <div className="min-h-screen flex">
      {/* Sidebar */}
      <aside className="w-56 p-6 shadow-md">
        <nav className="flex flex-col gap-2 text-sm font-medium text-gray-700">
            {tabs.map(({ label, key }) => (
            <button
                key={key}
                onClick={() => setActiveTab(key as typeof activeTab)}
                className={`px-4 py-2 rounded-lg text-left transition-all duration-200 ${
                activeTab === key
                    ? "bg-white font-bold shadow-sm"
                    : "hover:bg-stone-200"
                }`}
            >
                {label}
            </button>
            ))}
        </nav>
        </aside>

      {/* Main Dashboard */}
      <main className="flex-1 p-6 flex flex-col gap-6">
        {activeTab === "dashboard" && (
          <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
            {/* Profile Info Card */}
            <div className="col-span-3 lg:col-span-3 rounded-2xl shadow p-6 flex flex-col gap-4 bg-stone-50">
              <ProfileOverview />
            </div>

            {/* Performance Summary */}
            <div className="col-span-2 rounded-2xl shadow p-6 flex flex-col gap-4 bg-stone-50">
              
            </div>

            {/* Due Tasks Section */}
            <div className="col-span-3 lg:col-span-5 rounded-2xl shadow p-6 flex flex-col gap-4 bg-stone-50">
              <DueTasks />
            </div>
          </div>
        )}

        {activeTab === "stats" && <LearningStats />}
        {activeTab === "planner" && <CoursePlanner />}
      </main>
    </div>
  );
};

export default ProfilePage;
