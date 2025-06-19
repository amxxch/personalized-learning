import React from 'react'

interface LoadingSpinnerProps {
    message: string;
}

const LoadingSpinner = ({ message }: LoadingSpinnerProps) => {
  return (
    <div className="flex justify-center items-center p-4">
        <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-gray-800"></div>
        <div className="ml-2 text-gray-800">{message}</div>
    </div>
  )
}

export default LoadingSpinner
