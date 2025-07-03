import { CalendarComponent } from "@syncfusion/ej2-react-calendars";
import axios from "axios";
import "@syncfusion/ej2-base/styles/tailwind.css";
import "@syncfusion/ej2-react-calendars/styles/tailwind.css";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { FaFire } from "react-icons/fa";
import { GoDotFill } from "react-icons/go";
import LoadingSpinner from "./LoadingSpinner";


const dummyLoginDates = [
  "2025-06-02",
  "2025-06-04",
  "2025-06-12",
  "2025-06-18",
  "2025-06-19",
];

function convertDateToISO(date: Date): string {
    return date.toLocaleDateString("sv-SE");
}

function calculateStreak(dates: string[]): number {
    let date = new Date(); // today
    let streak = dates.includes(convertDateToISO(date)) ? 1 : 0;
    date.setDate(date.getDate() - 1); // start checking from yesterday
    
    while (dates.includes(convertDateToISO(date))) {
        streak++;
        date.setDate(date.getDate() - 1);
    }

    return streak;
}

export default function LoginCalendar() {
    const [isLoading, setIsLoading] = useState(true);
    const [loginDates, setLoginDates] = useState<string[]>([]);
    const [selectedDate, setSelectedDate] = useState<Date>(new Date());
    const [streak, setStreak] = useState<number>(0);
    const { userId } = useAuth();

    useEffect(() => {
        axios.get('http://localhost:8080/api/v1/engagement/monthly', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            params: {
                userId: userId,
                selectedDate: convertDateToISO(selectedDate),
            }
        })
        .then(response => {
            const data = response.data;
            console.log("log in calendar fetch: ", data);
            calculateStreak(data)
            setLoginDates(data);
            setIsLoading(false);
        })
        .catch(error => {
            console.error('Error fetching login dates:', error);
        });
    }, [userId, selectedDate]);

    useEffect(() => {
        const currentStreak = calculateStreak(loginDates);
        setStreak(currentStreak);
        console.log(loginDates, "current streak: ", currentStreak);
    }, [loginDates]);

    const handleNavigate = (args: any) => {
        console.log("navigated: ", args);
        if (args && args.currentValue) {
            setSelectedDate(new Date(args.currentValue));
          }
    }

    const highlightLoginDays = useCallback((args: any) => {
        const date = args.date;
        const dateISO = date.toLocaleDateString("sv-SE");
        if (loginDates.includes(dateISO)) {
          args.element.classList.add("bg-emerald-200", "text-green-800", "font-bold", "relative");
          const mark = document.createElement("div");
          args.element.appendChild(mark);
        }
      }, [loginDates]);

  return (
    <div className = "flex flex-col items-center">
        <h2 className="text-2xl font-bold text-center mb-4">Training Calendar</h2>
        { isLoading ? 
            <LoadingSpinner message="Loading your training calendar..." />
            :
            <>
            <CalendarComponent
                value={new Date()}
                renderDayCell={highlightLoginDays}
                cssClass="e-custom-calendar"
                showTodayButton={false}
                navigated={handleNavigate}
                className="w-full max-w-md mx-auto"
            />
            {/* <span className="mt-2 text-sm">
                <span className="font-semibold text-gray-500">Active Day = </span>
                <GoDotFill className="mx-2 text-3xl inline font-bold text-center text-emerald-200" />
            </span> */}
            <span className="mt-4">
                <span className="font-semibold">Current streak: </span>
                <FaFire className="mx-2 text-3xl mb-1 inline font-bold text-center text-orange-400" />
                <span className="font-bold text-xl text-orange-500">{streak} {streak > 1 ? "days" : "day"}</span>
            </span>
            <p className="mt-2 text-sm text-center text-gray-500">Practice each day so your streak won't be reset!</p>
            </>
            
        }
    </div>
  );
}
