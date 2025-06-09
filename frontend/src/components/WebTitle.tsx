import React from 'react'
import Typewriter from 'typewriter-effect';

const WebTitle = () => {
  return (
    <div className="text-center min-w-screen mt-28">
      <h1 className="text-4xl md:text-6xl font-bold mb-5 font-mono tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-pink-200 to-pink-900">
        Learning Bot
      </h1>
      <div className="text-lg md:text-xl font-semibold font-mono">      
        <Typewriter options={{
          strings: ['Your AI-Powered Learning Companion'],
          autoStart: true,
          loop: true,
          cursor: '_',
          delay: 100,
          deleteSpeed: 20,
        }}/>
      </div>
    </div>
  )
}

export default WebTitle
