import React from 'react'

const loginCalendar = [
    "2024-06-01",
    "2024-06-03",
    "2024-06-04",
    "2024-06-06",
    "2024-06-09",
    "2024-06-10",
  ];

const LoginCalendar = () => {
  return (
      <div className="bg-white p-4 rounded-xl shadow w-full md:w-1/2">
        <h2 className="text-md font-semibold text-gray-800 mb-2">📆 Login Calendar (June 2024)</h2>
        <div className="grid grid-cols-7 gap-1 text-center text-xs">
            {Array.from({ length: 30 }, (_, i) => {
            const date = `2024-06-${String(i + 1).padStart(2, "0")}`;
            const isLogged = loginCalendar.includes(date);
            return (
                <div
                key={date}
                className={`rounded-full py-1 ${isLogged ? "bg-green-200 text-green-800 font-bold" : "text-gray-400"}`}
                >
                {i + 1}
                </div>
            );
            })}
        </div>
        </div>
  )
}

export default LoginCalendar
