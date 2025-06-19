import { useEffect, useState } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import ProfileSetupForm from "./ProfileSetupForm";
import { useNavigate } from "react-router-dom";

import { FaUserGraduate } from "react-icons/fa6";
import { MdEdit } from "react-icons/md";

type User = {
  userId: number;
  name: string;
  email: string;
  careerGoal: string;
  weeklyLearningHours: number;
  experienceLevel: string;
  knownLanguages: string[];
  technicalFocuses: string[];
}

const ProfileOverview = () => {
  const Navigate = useNavigate();
  const [user, setUser] = useState<User>();
  const { userId } = useAuth();
  const [isEditProfile, setIsEditProfile] = useState(false);

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
        const data: User = response.data;
        console.log(data)
        setUser(data);
    })
    .catch(error => {
        console.error('Error fetching profile overview data:', error);
    });
}, [userId]);

  const handleEditProfile = (e: React.MouseEvent<HTMLSpanElement>) => {
    e.preventDefault();
    setIsEditProfile(true);
    Navigate('/profile-setup', { replace: true });
  }

  return (
    <div className="space-y-6">
      
      <span className="flex justify-between items-center text-3xl font-bold">
        <div className="flex items-center">
          👋🏻 Hi {user?.name.toLocaleUpperCase() || 'User'}!
          {/* <FaUserGraduate className="ml-4" /> */}
        </div>
        <span 
          className="ml-4 p-2 text-2xl rounded-full hover:bg-gray-200 transition-all duration-300 ease-in-out"
          onClick={(e) => {handleEditProfile(e)}}
        >
          <MdEdit />
        </span>
      </span>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

        <div>
          <h3 className="font-semibold mb-1">Email</h3>
          <p>{user?.email}</p>
        </div>

        <div>
          <h3 className="font-semibold mb-1">Career Goal</h3>
          <p>{user?.careerGoal}</p>
        </div>

        <div>
          <h3 className="font-semibold mb-1">Years of Experience</h3>
          <p>{user?.experienceLevel}</p>
        </div>

        <div>
          <h3 className="font-semibold mb-1">Weekly Learning Hours</h3>
          <p>{user?.weeklyLearningHours} hours</p>
        </div>
      </div>

      <div>
        <h3 className="font-semibold mb-2">Known Languages</h3>
        <div className="flex flex-wrap gap-2">
          {user?.knownLanguages && user.knownLanguages.map((lang) => (
            <span key={lang} className="px-3 py-1 rounded-full text-sm bg-yellow-100 text-yellow-700">
              {lang}
            </span>
          ))}
        </div>
      </div>

      <div>
        <h3 className="font-semibold mb-2">Technical Focus</h3>
        <div className="flex flex-wrap gap-2">
          {user?.technicalFocuses && user.technicalFocuses.map((focus) => (
            <span key={focus} className="px-3 py-1 rounded-full text-sm bg-green-100 text-green-700">
              {focus}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
};

export default ProfileOverview;
