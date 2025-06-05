import React from 'react'
import { AnimatePresence, motion } from 'framer-motion'

const QuizTimeAnim = () => {
  return (
      <AnimatePresence>
            <>
                {/* Transparent Background Overlay */}
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 0.7 }}
                    exit={{ opacity: 0 }}
                    className="fixed inset-0 bg-white z-40"
                />

                {/* Notification Bubble */}
                <motion.div
                    initial={{ scale: 1.0, opacity: 0 }}
                    animate={{ scale: 1.5, opacity: 1, transition: { duration: 0.6 } }}
                    exit={{ scale: 1.0, opacity: 0, transition: { duration: 1.0 } }} // exit takes longer
                    className="fixed top-[20vh] left-[37vw] py-6 z-50 max-w-md"
                >
                    <img alt='Quiz Time' src='/images/quiz-time-2.png'/>
                </motion.div>
            </>
        </AnimatePresence>
  )
}

export default QuizTimeAnim
