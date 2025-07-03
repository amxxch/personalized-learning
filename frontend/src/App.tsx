import { Routes, Route } from 'react-router-dom';
import axios from 'axios';
import { registerLicense } from '@syncfusion/ej2-base';
import CourseChatPage from './pages/CourseChatPage';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignUpPage';
import { useAuth } from './context/AuthContext';
import RouteWithLayout from './components/RouteWithLayout';
import ProfileSetupPage from './pages/ProfileSetupPage';
import WelcomePage from './pages/WelcomePage';
import UserProfilePage from './pages/UserProfilePage';
import AssessmentPage from './pages/AssessmentPage';
import CodeEditor from './pages/CodeExercisePage';
import CourseOverviewPage from './pages/CourseOverviewPage';
import ChatHistoryPage from './pages/ChatHistoryPage';
import ChatReviewPage from './pages/ChatReviewPage';

registerLicense('Ngo9BigBOggjHTQxAR8/V1JEaF5cXmRCeUx0THxbf1x1ZFdMYltbQXFPMyBoS35Rc0VlW3ZecnZSQ2lbVkd3VEFd');

function App() {

  const { setUserId, setUserToken } = useAuth();

  axios.interceptors.response.use(
    response => response,
    error => {
      if (error.response?.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        setUserToken('');
        setUserId(0);
        if (window.location.pathname !== '/login' && window.location.pathname !== '/signup') {
          window.location.href = "/login";
        }
      }
      return Promise.reject(error);
    }
  );

  return (
    <Routes>
      {/* <Route
        path="/"
        element={
          <RouteWithLayout isPrivate={true}>
            <HomePage />
          </RouteWithLayout>
        }
      /> */}
      <Route
        path="/course/:courseId/full-lesson/:skillId"
        element={
          <RouteWithLayout isPrivate={true}>
            <CourseChatPage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/course/overview/:courseId"
        element={
          <RouteWithLayout isPrivate={true}>
            <CourseOverviewPage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/login"
        element={
          <RouteWithLayout>
            <LoginPage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/signup"
        element={
          <RouteWithLayout>
            <SignupPage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/profile-setup"
        element={
          <RouteWithLayout isPrivate={true}>
            <ProfileSetupPage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/"
        element={
          <RouteWithLayout>
            <WelcomePage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/profile"
        element={
          <RouteWithLayout isPrivate={true}>
            <UserProfilePage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/profile/:tab"
        element={
          <RouteWithLayout isPrivate={true}>
            <UserProfilePage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/assessment/:courseId"
        element={
          <RouteWithLayout>
            <AssessmentPage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/course/:courseId/exercise/:skillId"
        element={
          <RouteWithLayout isPrivate={true}>
            <CodeEditor />
          </RouteWithLayout>
        }
      />
      <Route
        path="/course/:courseId/lesson/:skillId"
        element={
          <RouteWithLayout isPrivate={true}>
            <ChatHistoryPage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/course/:courseId/review/:skillId"
        element={
          <RouteWithLayout isPrivate={true}>
            <ChatReviewPage />
          </RouteWithLayout>
        }
      />
    </Routes>
  );
}

export default App;
