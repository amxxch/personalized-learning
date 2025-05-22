import { Routes, Route, BrowserRouter } from 'react-router-dom';
import CourseChatPage from './pages/CourseChatPage';
import Layout from './Layout';
import HomePage from './pages/HomePage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            <Layout>
              <HomePage />
            </Layout>
          }
        />
        <Route
          path="/courses/:courseTitle/chat"
          element={
            <Layout>
              <CourseChatPage />
            </Layout>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
