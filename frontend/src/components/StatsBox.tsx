import React from 'react'
import { ArrowUpRight, ArrowDownRight } from "lucide-react";
import { FaCheck } from "react-icons/fa";

interface Stat {
    label: string;
    value: number | string;
    breakdown?: {
        easy?: number;
        medium?: number;
        hard?: number;
    };
    increase?: boolean;
    change?: number;
    correct?: number;
    correctRate?: number;
}

const StatsBox = ({ label, value, breakdown, increase, change, correct, correctRate } : Stat) => {
  return (
    <div className="bg-white rounded-xl p-6 shadow text-center">
        <div className="text-sm text-gray-600 mb-1">{label}</div>
        <div className="text-4xl font-bold text-gray-800 mb-2">{value}</div>
        { change !== undefined && increase !== undefined && (
            <div
            className={`text-sm font-medium flex justify-center items-center gap-1 ${increase ? "text-green-600" : "text-red-500"}`}
            >
                {increase ? <ArrowUpRight className="w-4 h-4" /> : <ArrowDownRight className="w-4 h-4" />}
                {Math.abs(change)}% from last week
            </div>
        )}

        {correct !== undefined && (
        <div className="mt-2 text-sm text-gray-700">
          <FaCheck className='inline mr-1 mb-1 text-green-600 text-md' /><span className="font-semibold">{correct}</span> correct {" "}
          {correctRate !== undefined && (
            <span className="text-xs text-gray-500">({correctRate}%)</span>
          )}
        </div>
      )}

        {breakdown && (
            <div className="mt-5 text-xs font-medium">
                <span className="px-3 py-1 mr-2 rounded-full bg-green-100 text-green-800">
                    Easy: {breakdown.easy}
                </span>
                <span className="px-3 py-1 mr-2 rounded-full bg-yellow-100 text-yellow-800">
                    Medium: {breakdown.medium}
                </span>
                <span className="px-3 py-1 rounded-full bg-red-100 text-red-800">
                    Hard: {breakdown.hard}
                </span>
            </div>
            
        )}
    </div>
  )
}

export default StatsBox
