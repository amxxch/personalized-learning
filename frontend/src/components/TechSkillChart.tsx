import { useEffect, useState } from "react";
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer,
    ReferenceDot,
    ReferenceLine,
    CartesianGrid,
    ReferenceArea,
    Customized,
  } from "recharts";
  
  type Props = {
    userScore: number;
  };
  
  const TechSkillChart = ({ userScore }: Props) => {
    const [userPercentile, setUserPercentile] = useState(0);
    // S-curve simulated data
    const data = [
    { percentile: 0.0, score: 0 },
    { percentile: 0.1, score: 5 },
    { percentile: 0.2, score: 10 },
    { percentile: 0.3, score: 20 },
    { percentile: 0.4, score: 33 },
    { percentile: 0.5, score: 50 },
    { percentile: 0.6, score: 70 },
    { percentile: 0.7, score: 84 },
    { percentile: 0.8, score: 92 },
    { percentile: 0.9, score: 97 },
    { percentile: 1.0, score: 100 },
    ];

    function estimatePercentileFromScore(score: number): number {
        for (let i = 0; i < data.length - 1; i++) {
          const a = data[i];
          const b = data[i + 1];
      
          if (score >= a.score && score <= b.score) {
            // Linear interpolation
            const t = (score - a.score) / (b.score - a.score);
            return a.percentile + t * (b.percentile - a.percentile);
          }
        }
      
        // If below first or above last point, clamp to 0 or 1
        if (score < data[0].score) return 0;
        if (score > data[data.length - 1].score) return 1;
      
        return 0; // fallback
      }
      

    useEffect(() => {
        // Calculate user percentile based on score
        setUserPercentile(estimatePercentileFromScore(userScore));
        console.log("User Score:", userScore);
    }, [userScore]);

  
    // Percentile bands
    const levels = [
      { name: "Novice", start: 0.0, end: 0.2 },
      { name: "Emerging", start: 0.2, end: 0.4 },
      { name: "Average", start: 0.4, end: 0.6 },
      { name: "Above Avg", start: 0.6, end: 0.8 },
      { name: "Expert", start: 0.8, end: 1.0 },
    ];
  
    const userLevel = levels.find(
      (l) => userPercentile >= l.start && userPercentile <= l.end
    );
  
    return (
      <div className="w-full h-96 bg-white rounded-xl p-4 text-black shadow-lg">
        <h2 className="text-2xl font-semibold mb-6">
          Technical Focus Benchmark
        </h2>
        <ResponsiveContainer width="100%" height="90%">
          <LineChart data={data} margin={{ left: 10, right: 20, top: 20, bottom: 20 }}>
            {/* Gradient for the curve */}
            <defs>
              <linearGradient id="iqGradient" x1="0" y1="0" x2="1" y2="0">
                <stop offset="0%" stopColor="#FFC107" />
                <stop offset="50%" stopColor="#00C49F" />
                <stop offset="100%" stopColor="#0088FE" />
              </linearGradient>
            </defs>
  
            {/* Shaded background bands */}
            {levels.map((level, index) => (
            <>
                <ReferenceArea
                key={index}
                x1={level.start}
                x2={level.end}
                y1={100} // Start at the top of the graph
                y2={0}   // End at the bottom of the graph
                stroke="none"
                fill={userLevel?.name === level.name ? "#E3F2FD" : "#F5F8FB"}
                fillOpacity={1}
                />

                <ReferenceLine
                key={`label-line-${index}`}
                x={(level.start + level.end) / 2}
                stroke="none"
                label={{
                    value: level.name,
                    position: "top", // Move labels to the top
                    fill: level.name === userLevel?.name ? "#0D47A1" : "black",
                    fontSize: level.name === userLevel?.name ? 20 : 14,
                    fontWeight: level.name === userLevel?.name ? "bold" : "normal",
                }}
                />
            </>
            ))}
  
            <CartesianGrid stroke="#ddd" strokeDasharray="3 3" />

            <XAxis
            dataKey="percentile"
            type="number"
            domain={[0, 1]}
            tick={false}
            />

            <YAxis
            domain={[0, 100]}
            tick={{ fill: "#333", fontSize: 12 }}
            label={{
                value: "Score",
                angle: -90,
                position: "insideLeft",
                fill: "#333",
                fontSize: 12,
            }}
            />

  
            {/* Tooltip
            <Tooltip
              formatter={(v: any) => `${Math.round(v)}`}
              labelFormatter={() => ""}
              contentStyle={{ backgroundColor: "#1e1e2f", border: "none" }}
            /> */}
  
            {/* Skill curve */}
            <Line
              type="monotone"
              dataKey="score"
              stroke="url(#iqGradient)"
              strokeWidth={3}
              dot={false}
              isAnimationActive={true}
            />
  
            {/* Horizontal reference line */}
            <ReferenceLine
            y={userScore}
            stroke="#aaa"
            strokeDasharray="5 5"
            label={{
                position: "left",
                value: userScore,
                fill: "black",
                fontSize: 16,
                fontWeight: "bold",
            }}
            />

            <ReferenceDot
            x={userPercentile}
            y={userScore}
            r={10}
            stroke="#333"
            strokeWidth={2}
            fill="white"
            />

          </LineChart>
        </ResponsiveContainer>
      </div>
    );
  };
  
  export default TechSkillChart;
  