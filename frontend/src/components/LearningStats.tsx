import { GiProgression } from "react-icons/gi";

const LearningStats = () => {
return (
    <div className="space-y-6">
      <span className="flex items-center text-3xl font-bold">
        <GiProgression className="mr-2" />
        Learning Progress
      </span>
      <p className="text-gray-600 text-sm">TODO: track weekly activity, progress by skill, etc.</p>
    </div>
    );
}

export default LearningStats
  
  