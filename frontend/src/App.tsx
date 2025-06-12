import { Routes, Route, BrowserRouter } from 'react-router-dom';
import axios from 'axios';
import CourseChatPage from './pages/CourseChatPage';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignUpPage';
import { useAuth } from './context/AuthContext';
import RouteWithLayout from './components/RouteWithLayout';
import ProfileSetupPage from './pages/ProfileSetupPage';
import WelcomePage from './pages/WelcomePage';
import UserProfilePage from './pages/UserProfilePage';

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
      <Route
        path="/"
        element={
          <RouteWithLayout isPrivate={true}>
            <HomePage />
          </RouteWithLayout>
        }
      />
      <Route
        path="/courses/:courseId/chat"
        element={
          <RouteWithLayout isPrivate={true}>
            <CourseChatPage />
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
        path="/welcome"
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

    </Routes>
  );
}

export default App;
