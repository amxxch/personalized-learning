import React from 'react'

const DueTask = () => {
  return (
    <div>
        <span className="flex items-center text-2xl font-bold mb-3">
        📌 Due Tasks
        </span>
        {/* You can replace with a real component later */}
        <ul className="space-y-3">
        <li className="p-4 rounded-lg shadow flex justify-between items-center">
            <span>1. Complete C++ Chapter 12</span>
            <span className="text-sm">0%</span>
        </li>
        <li className="p-4 rounded-lg shadow flex justify-between items-center">
            <span>2. Do coding exercise</span>
            <span className="text-sm">50%</span>
        </li>
        </ul>
    </div>
  )
}

export default DueTask
