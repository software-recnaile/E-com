// import React from 'react'

// const App = () => {
//   return (
//     <div>App</div>
//   )
// }

// export default App

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
// import Navbar from './components/Navbar';
// import PrivateRoute from './components/PrivateRoute';
// import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import Signup from './pages/Signup';
// import VerifyEmail from './pages/VerifyEmail';
// import ForgotPassword from './pages/ForgotPassword';
// import ResetPassword from './pages/ResetPassword';
// import './styles/App.css';

function App() {
  return (
    <Router>
      <div className="App">
        {/* <Navbar /> */}
        <ToastContainer />
        <Routes>
          {/* <Route path="/" element={<PrivateRoute><Dashboard /></PrivateRoute>} /> */}
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          {/* <Route path="/verify-email" element={<VerifyEmail />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} /> */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;