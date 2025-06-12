import React from 'react'

const WelcomePage = () => {
  return (
    <div className="min-h-screen bg-gradient-to-r from-blue-500 to-indigo-600 flex flex-col items-center justify-center text-white px-6"
    style={{
        backgroundImage:
          'url("images/background.png")',
      }}>
    <h1 className="text-5xl font-extrabold mb-4 text-center drop-shadow-lg">
      Welcome to LearnHub
    </h1>
    <p className="text-lg max-w-xl text-center mb-8 drop-shadow-md">
        Please set up your profile before getting started for a personalized learning path!
    </p>
    <div className="flex flex-col sm:flex-row gap-4">
      <button className="bg-white text-indigo-600 font-semibold px-6 py-3 rounded-lg shadow-lg hover:bg-indigo-100 transition">
        Get Started
      </button>
      <button className="bg-indigo-700 bg-opacity-30 border border-white font-semibold px-6 py-3 rounded-lg shadow-lg hover:bg-indigo-800 transition">
        Learn More
      </button>
    </div>
    <footer className="mt-16 text-sm opacity-80">
      © 2024 LearnHub. All rights reserved.
    </footer>
  </div>
  )
}

export default WelcomePage
