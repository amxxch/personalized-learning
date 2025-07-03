import React, { useEffect } from 'react'

interface ProgressBarProps {
    progressPercent: number;
    }

const ProgressBar = ({ progressPercent } : ProgressBarProps) => {
    useEffect(() => {
        console.log("enter ProgressBar", progressPercent)
    })
  return (
    <div className="mt-4">
        <div className="text-sm font-medium text-gray-700 mb-1">
        Progress: {progressPercent}%
        </div>
        <div className="w-full bg-gray-200 rounded-full h-4">
            <div
                className="bg-pink-600 h-4 rounded-full transition-all"
                style={{ width: `${progressPercent}%` }}
            ></div>
        </div>
    </div>
  )
}

export default ProgressBar
