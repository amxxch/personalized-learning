import { FaRegCircleUser } from "react-icons/fa6";
import { FaLock } from "react-icons/fa";
import { IoIosMail } from "react-icons/io";
import InputField from '../components/InputField';
import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import WebTitle from "../components/WebTitle";


const SignupPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmedPassword, setConfirmedPassword] = useState('');
  const [name, setName] = useState('');
  const [error, setError] = useState('');
  const { signup } = useAuth();

  const validateInput = () => {
    if (!email || !password || !name) {
        setError('Name, email, and password are required.');
        return false;
    }
    if (!/\S+@\S+\.\S+/.test(email)) {
        setError('Please enter a valid email address.');
        return false;
    }
    if (password.length < 6) {
        setError('Password must be at least 6 characters long.');
        return false;
    }
    if (password !== confirmedPassword) {
        setError('Passwords do not match.');
        return false;
    }
    setError('');
    return true;
}

const handleSignUp = (event: React.FormEvent<HTMLButtonElement>) => {
    event.preventDefault();
    if (!validateInput()) {
        return;
    }

    signup(email, password, name)
      .then(() => {
        // Redirect to home page or dashboard after successful login
        window.location.href = '/';
      })
      .catch((error) => {
        console.error('Sign up failed:', error);
        setError('Sign up failed.' + error.message);
      });

};

  return (
  <div className="min-h-screen bg-cover bg-center px-4 flex flex-col">

    <WebTitle />

    <div className="flex flex-1 items-start mt-8 justify-center">

        {/* Book Icon on the left side */}
        <div className="hidden md:flex flex-[1.5] justify-end pr-4 mt-16">
          <img src="images/book-icon.png" alt="Book Icon" className="w-64 h-auto object-contain" />
        </div>

        <div className="md:block h-[70%] w-3 mx-4"></div>

        {/* Signup Form on the right side */}
        <div className="flex flex-[1.8] justify-start pl-4">
          <div className="relative w-full max-w-md bg-transparent rounded-[20px] 
                          flex justify-center items-center px-12 py-8">
            <form>
                <h1 className="text-3xl pb-2 font-bold text-center">Sign Up</h1>
                <InputField label="Name" type="text" Icon={FaRegCircleUser} onChange={setName} />
                <InputField label="Email" type="text" Icon={IoIosMail} onChange={setEmail} />
                <InputField label="Password" type="password" Icon={FaLock} onChange={setPassword} />
                <InputField label="Confirm Password" type="password" Icon={FaLock} onChange={setConfirmedPassword} />

                {error && <p className="text-red-500 text-md break-words">*{error}</p>}

                <button 
                    type="submit" 
                    className="w-full h-10 mt-5 rounded-full bg-orange-300 cursor-pointer text-white 
                            font-semibold transition-all duration-400 ease-in-out hover:bg-orange-200"
                    onClick={handleSignUp}
                >
                    Sign Up
                </button>

                <div className='text-md text-center my-5 mb-1'>
                    <p>Already have an account? 
                        <a href="/login" className='text-orange-400 font-semibold ml-1 transition-all duration-500 ease-in-out hover:underline'>Log In</a>
                    </p>
                </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}

export default SignupPage
